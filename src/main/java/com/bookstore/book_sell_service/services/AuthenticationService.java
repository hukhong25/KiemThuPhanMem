package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.AuthenticationRequest;
import com.bookstore.book_sell_service.dto.request.IntrospectRequest;
import com.bookstore.book_sell_service.dto.request.LogOut_Refresh.LogoutRequest;
import com.bookstore.book_sell_service.dto.request.LogOut_Refresh.RefreshRequest;
import com.bookstore.book_sell_service.dto.responses.AuthenticationResponse;
import com.bookstore.book_sell_service.dto.responses.IntrospectResponse;
import com.bookstore.book_sell_service.entity.InvalidateToken;
import com.bookstore.book_sell_service.entity.KhachHang;
import com.bookstore.book_sell_service.entity.NhanVien;
import com.bookstore.book_sell_service.exception.AppException;
import com.bookstore.book_sell_service.exception.ErrorCode;
import com.bookstore.book_sell_service.repositories.InvalidatedTokenRepository;
import com.bookstore.book_sell_service.repositories.KhachHangRepository;
import com.bookstore.book_sell_service.repositories.NhanVienRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true )
public class AuthenticationService {
    KhachHangRepository khachHangRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;
    NhanVienRepository nhanVienRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESH_DURATION;


    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token =request.getToken();
        JWSVerifier verifier ;

            verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);
        signedJWT.verify((verifier));
        Date expiryTime=signedJWT.getJWTClaimsSet().getExpirationTime();
    var verified =signedJWT.verify(verifier);

    return IntrospectResponse.builder()
            .valid(verified && expiryTime.after(new Date()))
            .build();

    }

   public AuthenticationResponse authenticate(AuthenticationRequest request){

        Object user;
        try {
            user = khachHangRepository.findByUserName(request.getUserName())
                    .orElseThrow();
        } catch (Exception e) {
            user = nhanVienRepository.findByTenDangNhap(request.getUserName())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }

        String matKhau = ( user instanceof KhachHang)
                ? ((KhachHang) user).getMatKhau() : ((NhanVien) user).getMatKhau() ;

        Set<String> roles = ( user instanceof KhachHang)
                ? ((KhachHang) user).getRoles()
                : ((NhanVien) user).getRoles() ;

        boolean authenticated= passwordEncoder.matches(request.getMatKhau(), matKhau);
        if(!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .roles(roles)
                .token(token)
                .authenticated(true)
                .build();

    }

    public void logout(LogoutRequest request)  throws ParseException, JOSEException{
        try {
            var signJWT = verifyToken(request.getToken(),true);

            String jit = signJWT.getJWTClaimsSet().getJWTID();
            var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

            InvalidateToken invalidateToken =
                    InvalidateToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidateToken);

        } catch (AppException exception){
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken (RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidateToken invalidatedTToken =
                InvalidateToken.builder().id(jit).expiryTime(expiryTime).build();

        var username = signJWT.getJWTClaimsSet().getSubject();

        var user = khachHangRepository.findByUserName(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }


    private SignedJWT verifyToken(String token , boolean isRefresh) throws JOSEException, ParseException {

        // tao verify xac thuc chu ky token
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY);

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expotyTime = (isRefresh) ?
                new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESH_DURATION,ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(jwsVerifier);

        if (!(verified && expotyTime.after(new Date()))) throw  new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;


    }

    private String generateToken(Object user){

        String userName;
        String scope;

        if (user instanceof  KhachHang kh){
            userName = kh.getUserName();
            scope = buildScope(kh);
        }
        else if (user instanceof  NhanVien nv){
            userName = nv.getTenDangNhap();
            scope = buildScope(nv);
        }else {
            throw new RuntimeException("Invalid user type");
        }


        JWSHeader jwsHeader= new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userName)
                .issuer("stewie.vn")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", scope)
                .build();

        Payload payload=new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject=new JWSObject(jwsHeader,payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        } catch (JOSEException e) {
            log.error("cannot create token");
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }
    private String buildScope(Object user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (user instanceof KhachHang kh) {
            if (!CollectionUtils.isEmpty(kh.getRoles())) {
                kh.getRoles().forEach(stringJoiner::add);
            }
        }
        else if (user instanceof NhanVien nv) {
            if (!CollectionUtils.isEmpty(nv.getRoles())) {
                nv.getRoles().forEach(stringJoiner::add);
            }
        }
        else {
            throw new RuntimeException("Invalid user type");
        }

        return stringJoiner.toString();
    }


    public KhachHang khachHang () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return khachHangRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
    public NhanVien nhanVien () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return nhanVienRepository.findByTenDangNhap(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}
