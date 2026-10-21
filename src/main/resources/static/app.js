/**
 * FBC BOOKSTORE - FRONTEND SINGLE PAGE APPLICATION
 * Connects to Spring Boot Backend API (Port 8081, Context-path /store)
 */

const API_BASE = (window.location.port === '8081') ? '/store' : 'http://localhost:8081/store';

// Application State
let token = localStorage.getItem('fbc_jwt_token') || null;
let currentUser = null;
let allBooks = [];
let allCategories = [];
let allAuthors = [];
let allVouchers = [];
let currentCart = null;
let selectedVoucher = null;
let selectedRating = 5;
let currentDetailBookId = null;
let revenueChartInstance = null;

// ================= INITIALIZATION =================
document.addEventListener('DOMContentLoaded', async () => {
    await checkAuth();
    await loadInitialData();
    showView('home');
});

async function loadInitialData() {
    await Promise.all([
        loadCategories(),
        loadAuthors(),
        loadVouchers(),
        loadBooks()
    ]);
    if (token) {
        await loadCartBadge();
    }
}

// ================= API REQUEST WRAPPER =================
async function apiRequest(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const options = { method, headers };
    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const errorMsg = data?.message || (data?.code ? `Lỗi hệ thống (Mã: ${data.code})` : 'Có lỗi xảy ra khi gửi yêu cầu');
            throw new Error(errorMsg);
        }
        return data;
    } catch (err) {
        console.error(`API Error [${method} ${endpoint}]:`, err);
        throw err;
    }
}

// ================= VIEW NAVIGATION =================
function showView(viewName) {
    const views = ['home', 'detail', 'cart', 'orders', 'vouchers', 'profile', 'admin'];
    views.forEach(v => {
        const el = document.getElementById(`view-${v}`);
        if (el) el.classList.add('d-none');
        const navEl = document.getElementById(`nav-${v}`);
        if (navEl) navEl.classList.remove('active');
    });

    const targetView = document.getElementById(`view-${viewName}`);
    if (targetView) targetView.classList.remove('d-none');
    const targetNav = document.getElementById(`nav-${viewName}`);
    if (targetNav) targetNav.classList.add('active');

    window.scrollTo({ top: 0, behavior: 'smooth' });

    // View specific actions
    if (viewName === 'cart') loadCartView();
    if (viewName === 'orders') loadUserOrders();
    if (viewName === 'vouchers') loadVouchersView();
    if (viewName === 'profile') loadProfileView();
    if (viewName === 'admin') loadAdminDashboard();
}

// ================= AUTHENTICATION =================
async function checkAuth() {
    if (!token) {
        renderAuthUI(null);
        return;
    }

    try {
        // Try getting customer info first
        try {
            const res = await apiRequest('/kh/myInfo');
            if (res?.result) {
                currentUser = { ...res.result, role: 'USER' };
                renderAuthUI(currentUser);
                return;
            }
        } catch (e) {
            // Not a regular customer, try staff info
            const staffRes = await apiRequest('/nv/myInfo');
            if (staffRes?.result) {
                const roles = staffRes.result.roles || [];
                const isAdmin = roles.includes('ADMIN') || staffRes.result.tenDangNhap === 'admin';
                currentUser = { 
                    ...staffRes.result, 
                    userName: staffRes.result.tenDangNhap,
                    role: isAdmin ? 'ADMIN' : 'STAFF' 
                };
                renderAuthUI(currentUser);
                return;
            }
        }
    } catch (err) {
        console.warn("Token expired or invalid, logging out:", err);
        logout(false);
    }
}

function renderAuthUI(user) {
    const authSection = document.getElementById('authSection');
    const navOrdersItem = document.getElementById('nav-orders-item');
    const navAdminItem = document.getElementById('nav-admin-item');

    if (!user) {
        authSection.innerHTML = `
            <button class="btn btn-outline-light btn-sm px-3 fw-bold" onclick="openLoginModal()">
                <i class="fa-solid fa-right-to-bracket me-1"></i> Đăng nhập
            </button>
        `;
        if (navOrdersItem) navOrdersItem.classList.add('d-none');
        if (navAdminItem) navAdminItem.classList.add('d-none');
        return;
    }

    if (navOrdersItem) navOrdersItem.classList.remove('d-none');
    
    // Show admin tab for ADMIN or STAFF
    const isStaffOrAdmin = user.role === 'ADMIN' || user.role === 'STAFF';
    if (navAdminItem) {
        if (isStaffOrAdmin) {
            navAdminItem.classList.remove('d-none');
        } else {
            navAdminItem.classList.add('d-none');
        }
    }

    const roleBadge = user.role === 'ADMIN' 
        ? '<span class="badge bg-danger ms-1">Admin</span>' 
        : (user.role === 'STAFF' ? '<span class="badge bg-info text-dark ms-1">Nhân viên</span>' : '');

    authSection.innerHTML = `
        <div class="dropdown">
            <button class="btn btn-warning btn-sm dropdown-toggle fw-bold d-flex align-items-center gap-1" data-bs-toggle="dropdown">
                <i class="fa-solid fa-circle-user fs-6"></i>
                <span>${user.hoTen || user.userName || 'Tài khoản'}</span>
                ${roleBadge}
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow">
                <li>
                    <a class="dropdown-item" href="#" onclick="showView('profile')">
                        <i class="fa-solid fa-id-card me-2 text-primary"></i> Hồ sơ tài khoản
                    </a>
                </li>
                <li>
                    <a class="dropdown-item" href="#" onclick="showView('orders')">
                        <i class="fa-solid fa-clipboard-list me-2 text-success"></i> Đơn hàng của tôi
                    </a>
                </li>
                ${isStaffOrAdmin ? `
                <li><hr class="dropdown-divider"></li>
                <li>
                    <a class="dropdown-item fw-bold text-danger" href="#" onclick="showView('admin')">
                        <i class="fa-solid fa-gear me-2"></i> Bảng điều khiển Quản trị
                    </a>
                </li>
                ` : ''}
                <li><hr class="dropdown-divider"></li>
                <li>
                    <a class="dropdown-item text-danger" href="#" onclick="logout()">
                        <i class="fa-solid fa-right-from-bracket me-2"></i> Đăng xuất
                    </a>
                </li>
            </ul>
        </div>
    `;
}

function openLoginModal() {
    new bootstrap.Modal(document.getElementById('loginModal')).show();
}

function switchToRegisterModal() {
    bootstrap.Modal.getInstance(document.getElementById('loginModal'))?.hide();
    new bootstrap.Modal(document.getElementById('registerModal')).show();
}

function switchToLoginModal() {
    bootstrap.Modal.getInstance(document.getElementById('registerModal'))?.hide();
    new bootstrap.Modal(document.getElementById('loginModal')).show();
}

function fillLoginForm(username, password) {
    document.getElementById('loginUsername').value = username;
    document.getElementById('loginPassword').value = password;
}

async function handleLogin(e) {
    e.preventDefault();
    const userName = document.getElementById('loginUsername').value.trim();
    const matKhau = document.getElementById('loginPassword').value;

    try {
        Swal.showLoading();
        const res = await apiRequest('/auth/token', 'POST', { userName, matKhau });
        
        if (res?.result?.token) {
            token = res.result.token;
            localStorage.setItem('fbc_jwt_token', token);
            
            bootstrap.Modal.getInstance(document.getElementById('loginModal'))?.hide();
            await checkAuth();
            await loadCartBadge();

            Swal.fire({
                icon: 'success',
                title: 'Đăng nhập thành công!',
                text: `Chào mừng bạn quay trở lại, ${userName}`,
                timer: 1500,
                showConfirmButton: false
            });

            if (currentUser?.role === 'ADMIN' || currentUser?.role === 'STAFF') {
                showView('admin');
            } else {
                showView('home');
            }
        }
    } catch (err) {
        Swal.fire({
            icon: 'error',
            title: 'Đăng nhập thất bại',
            text: err.message || 'Tên đăng nhập hoặc mật khẩu không chính xác'
        });
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const userName = document.getElementById('regUsername').value.trim();
    const matKhau = document.getElementById('regPassword').value;
    const hoTen = document.getElementById('regFullName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const soDT = document.getElementById('regPhone').value.trim();
    const diaChi = document.getElementById('regAddress').value.trim();

    try {
        Swal.showLoading();
        await apiRequest('/kh', 'POST', {
            userName, matKhau, hoTen, email, soDT, diaChi
        });

        bootstrap.Modal.getInstance(document.getElementById('registerModal'))?.hide();
        Swal.fire({
            icon: 'success',
            title: 'Đăng ký thành công!',
            text: 'Bạn có thể đăng nhập ngay bằng tài khoản vừa tạo.',
            confirmButtonText: 'Đăng nhập ngay'
        }).then(() => {
            fillLoginForm(userName, matKhau);
            openLoginModal();
        });
    } catch (err) {
        Swal.fire({
            icon: 'error',
            title: 'Đăng ký không thành công',
            text: err.message || 'Vui lòng kiểm tra lại thông tin đăng ký'
        });
    }
}

async function logout(showAlert = true) {
    if (token) {
        try {
            await apiRequest('/auth/logout', 'POST', { token });
        } catch (e) {
            // Ignore token expiration on logout
        }
    }

    token = null;
    currentUser = null;
    localStorage.removeItem('fbc_jwt_token');
    renderAuthUI(null);
    updateCartBadge(0);
    showView('home');

    if (showAlert) {
        Swal.fire({
            icon: 'info',
            title: 'Đã đăng xuất',
            text: 'Hẹn gặp lại bạn lần sau!',
            timer: 1500,
            showConfirmButton: false
        });
    }
}

// ================= BOOK CATALOG & SEARCH =================
async function loadBooks() {
    try {
        const res = await apiRequest('/sach');
        allBooks = res?.result || [];
        renderBooksGrid(allBooks);
        document.getElementById('bookCountBadge').textContent = `${allBooks.length} cuốn sách`;
    } catch (err) {
        console.error("Error loading books:", err);
        document.getElementById('booksGrid').innerHTML = `
            <div class="col-12 text-center text-muted py-5">
                <i class="fa-solid fa-triangle-exclamation fs-1 text-warning mb-2"></i>
                <p>Không thể tải danh sách sách. Vui lòng kiểm tra kết nối với Backend Service.</p>
            </div>
        `;
    }
}

function renderBooksGrid(books) {
    const grid = document.getElementById('booksGrid');
    if (!books || books.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center text-muted py-5">
                <i class="fa-solid fa-box-open fs-1 text-secondary mb-2"></i>
                <p class="fs-5">Không tìm thấy cuốn sách nào phù hợp với bộ lọc.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = books.map(book => {
        const priceFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(book.donGia || 0);
        const stockBadge = (book.soLuongCo > 0)
            ? `<span class="badge bg-success-subtle text-success border border-success badge-stock"><i class="fa-solid fa-check me-1"></i>Còn ${book.soLuongCo} cuốn</span>`
            : `<span class="badge bg-danger-subtle text-danger border border-danger badge-stock"><i class="fa-solid fa-xmark me-1"></i>Hết hàng</span>`;
        
        const categoryName = book.loaiSach?.tenLoai || 'Chung';
        const defaultCover = 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=400&q=80';
        const imgUrl = (book.hinhAnhList && book.hinhAnhList.length > 0) ? book.hinhAnhList[0].linkHinhAnh : defaultCover;

        return `
            <div class="col">
                <div class="book-card">
                    <div class="book-card-img-wrap cursor-pointer" onclick="openBookDetail(${book.maSach})">
                        <img src="${imgUrl}" alt="${book.tenSach}" class="book-card-img" onerror="this.src='${defaultCover}'">
                    </div>
                    <div class="book-card-body">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="badge-category">${categoryName}</span>
                            ${stockBadge}
                        </div>
                        <h5 class="book-title" title="${book.tenSach}" onclick="openBookDetail(${book.maSach})">
                            ${book.tenSach}
                        </h5>
                        <div class="book-author">
                            <i class="fa-solid fa-feather-pointed me-1"></i>
                            ${book.nhaXuatBan?.tenNXB || 'NXB Văn Học'}
                        </div>
                        <div class="book-price">${priceFmt}</div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-primary btn-sm flex-grow-1" onclick="openBookDetail(${book.maSach})">
                                <i class="fa-solid fa-circle-info me-1"></i> Chi tiết
                            </button>
                            <button class="btn btn-primary btn-sm px-3" onclick="handleAddToCart(${book.maSach})" ${book.soLuongCo <= 0 ? 'disabled' : ''} title="Thêm vào giỏ">
                                <i class="fa-solid fa-cart-plus"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

async function loadCategories() {
    try {
        const res = await apiRequest('/loai-sach');
        allCategories = res?.result || [];

        const filterSelect = document.getElementById('filterCategory');
        const formSelect = document.getElementById('bookFormCategory');

        const options = allCategories.map(c => `<option value="${c.maLoai}">${c.tenLoai}</option>`).join('');
        
        filterSelect.innerHTML = `<option value="">Tất cả thể loại</option>${options}`;
        if (formSelect) formSelect.innerHTML = options;
    } catch (err) {
        console.error("Error loading categories:", err);
    }
}

async function loadAuthors() {
    try {
        const res = await apiRequest('/tac-gia');
        allAuthors = res?.result || [];

        const filterSelect = document.getElementById('filterAuthor');
        const formSelect = document.getElementById('bookFormAuthor');

        const options = allAuthors.map(a => `<option value="${a.maTG}">${a.tenTG}</option>`).join('');
        
        filterSelect.innerHTML = `<option value="">Tất cả tác giả</option>${options}`;
        if (formSelect) formSelect.innerHTML = `<option value="">Chọn tác giả...</option>${options}`;
    } catch (err) {
        console.error("Error loading authors:", err);
    }
}

function applyFilters() {
    const catId = document.getElementById('filterCategory').value;
    const authorId = document.getElementById('filterAuthor').value;
    const priceRange = document.getElementById('filterPrice').value;

    let filtered = [...allBooks];

    if (catId) {
        filtered = filtered.filter(b => b.loaiSach?.maLoai == catId);
    }

    if (authorId) {
        filtered = filtered.filter(b => b.tacGiaSet?.some(a => a.maTG == authorId));
    }

    if (priceRange === 'under100') {
        filtered = filtered.filter(b => b.donGia < 100000);
    } else if (priceRange === '100to200') {
        filtered = filtered.filter(b => b.donGia >= 100000 && b.donGia <= 200000);
    } else if (priceRange === 'over200') {
        filtered = filtered.filter(b => b.donGia > 200000);
    }

    renderBooksGrid(filtered);
    document.getElementById('catalogTitle').textContent = `Kết Quả Lọc Sách (${filtered.length})`;
    document.getElementById('bookCountBadge').textContent = `${filtered.length} cuốn`;
}

function resetFilters() {
    document.getElementById('filterCategory').value = '';
    document.getElementById('filterAuthor').value = '';
    document.getElementById('filterPrice').value = 'all';
    document.getElementById('searchInput').value = '';
    document.getElementById('catalogTitle').textContent = 'Danh Sách Sách Mới Nhất';
    renderBooksGrid(allBooks);
    document.getElementById('bookCountBadge').textContent = `${allBooks.length} cuốn`;
}

function handleSearchKeyUp(event) {
    if (event.key === 'Enter') {
        performSearch();
    }
}

async function performSearch() {
    const term = document.getElementById('searchInput').value.trim();
    if (!term) {
        renderBooksGrid(allBooks);
        document.getElementById('catalogTitle').textContent = 'Danh Sách Sách Mới Nhất';
        return;
    }

    showView('home');
    document.getElementById('catalogTitle').textContent = `Tìm kiếm cho: "${term}"`;

    try {
        // Try Fuzzy search on elasticsearch if available
        const res = await apiRequest(`/sach/search?term=${encodeURIComponent(term)}`);
        if (res?.result && res.result.length > 0) {
            // Map elastic search results to books
            const matchedIds = res.result.map(d => d.maSach);
            const matchedBooks = allBooks.filter(b => matchedIds.includes(b.maSach));
            renderBooksGrid(matchedBooks.length > 0 ? matchedBooks : allBooks.filter(b => b.tenSach.toLowerCase().includes(term.toLowerCase())));
            return;
        }
    } catch (e) {
        // Fallback to client-side fuzzy search
    }

    const filtered = allBooks.filter(b => 
        b.tenSach.toLowerCase().includes(term.toLowerCase()) ||
        b.moTa?.toLowerCase().includes(term.toLowerCase()) ||
        b.loaiSach?.tenLoai?.toLowerCase().includes(term.toLowerCase())
    );
    renderBooksGrid(filtered);
    document.getElementById('bookCountBadge').textContent = `${filtered.length} kết quả`;
}

// ================= BOOK DETAIL & REVIEWS =================
async function openBookDetail(maSach) {
    currentDetailBookId = maSach;
    showView('detail');

    const detailContainer = document.getElementById('bookDetailContent');
    detailContainer.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary"></div></div>';

    try {
        const res = await apiRequest(`/sach/${maSach}`);
        const book = res?.result;
        if (!book) throw new Error("Không tìm thấy thông tin cuốn sách này");

        const priceFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(book.donGia || 0);
        const defaultCover = 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=400&q=80';
        const imgUrl = (book.hinhAnhList && book.hinhAnhList.length > 0) ? book.hinhAnhList[0].linkHinhAnh : defaultCover;

        detailContainer.innerHTML = `
            <div class="row g-4 align-items-center">
                <div class="col-md-4 text-center">
                    <img src="${imgUrl}" alt="${book.tenSach}" class="img-fluid rounded shadow-sm" style="max-height: 380px;" onerror="this.src='${defaultCover}'">
                </div>
                <div class="col-md-8">
                    <div class="d-flex gap-2 mb-2">
                        <span class="badge bg-primary px-3 py-2 fs-6 rounded-pill">${book.loaiSach?.tenLoai || 'Tiểu thuyết'}</span>
                        <span class="badge bg-secondary px-3 py-2 fs-6 rounded-pill">${book.nhaXuatBan?.tenNXB || 'NXB Văn Học'}</span>
                    </div>
                    <h2 class="fw-bold text-dark mb-2">${book.tenSach}</h2>
                    <div class="fs-2 fw-bold text-danger mb-3">${priceFmt}</div>
                    
                    <div class="row g-3 mb-4 text-muted small">
                        <div class="col-6 col-sm-4">
                            <strong><i class="fa-solid fa-boxes-stacked me-1"></i> Tồn kho:</strong> ${book.soLuongCo} ${book.donViTinh || 'cuốn'}
                        </div>
                        <div class="col-6 col-sm-4">
                            <strong><i class="fa-solid fa-star text-warning me-1"></i> Đánh giá:</strong> ${book.avgSao ? book.avgSao.toFixed(1) : '5.0'} / 5.0 (${book.soLuotDG || 0} lượt)
                        </div>
                        <div class="col-6 col-sm-4">
                            <strong><i class="fa-solid fa-truck me-1"></i> Vận chuyển:</strong> Toàn quốc (20.000 đ)
                        </div>
                    </div>

                    <div class="mb-4">
                        <h6 class="fw-bold text-dark">Mô tả nội dung sách:</h6>
                        <p class="text-secondary" style="line-height: 1.7;">
                            ${book.moTa || 'Chưa có mô tả chi tiết cho cuốn sách này.'}
                        </p>
                    </div>

                    <div class="d-flex align-items-center gap-3">
                        <div class="input-group" style="width: 140px;">
                            <button class="btn btn-outline-secondary" type="button" onclick="changeDetailQty(-1)">-</button>
                            <input type="number" id="detailQuantity" class="form-control text-center" value="1" min="1" max="${book.soLuongCo}">
                            <button class="btn btn-outline-secondary" type="button" onclick="changeDetailQty(1)">+</button>
                        </div>
                        <button class="btn btn-primary px-4 py-2 fw-bold" onclick="handleAddToCartFromDetail(${book.maSach})" ${book.soLuongCo <= 0 ? 'disabled' : ''}>
                            <i class="fa-solid fa-cart-plus me-1"></i> Thêm vào giỏ hàng
                        </button>
                    </div>
                </div>
            </div>
        `;

        await loadBookReviews(maSach);
    } catch (err) {
        detailContainer.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}

function changeDetailQty(delta) {
    const input = document.getElementById('detailQuantity');
    if (!input) return;
    let val = parseInt(input.value) || 1;
    val += delta;
    if (val < 1) val = 1;
    input.value = val;
}

async function handleAddToCartFromDetail(maSach) {
    const qty = parseInt(document.getElementById('detailQuantity')?.value) || 1;
    await handleAddToCart(maSach, qty);
}

// Reviews
async function loadBookReviews(maSach) {
    const list = document.getElementById('reviewsList');
    try {
        const res = await apiRequest(`/danh_gia/${maSach}`);
        const reviews = res?.result || [];

        if (reviews.length === 0) {
            list.innerHTML = `
                <div class="text-center text-muted py-3">
                    <i class="fa-regular fa-comments fs-3 mb-2"></i>
                    <p class="mb-0">Chưa có đánh giá nào cho cuốn sách này. Hãy là người đầu tiên chia sẻ cảm nhận!</p>
                </div>
            `;
            return;
        }

        list.innerHTML = reviews.map(r => `
            <div class="border-bottom py-3">
                <div class="d-flex justify-content-between align-items-center mb-1">
                    <div class="fw-bold text-dark">
                        <i class="fa-solid fa-user-circle text-primary me-1"></i>
                        ${r.khachHang?.hoTen || r.khachHang?.userName || 'Độc giả ẩn danh'}
                    </div>
                    <div class="text-warning small">
                        ${Array.from({ length: 5 }, (_, i) => `<i class="fa-${i < r.sao ? 'solid' : 'regular'} fa-star"></i>`).join('')}
                    </div>
                </div>
                <div class="text-secondary small mb-1">${r.nhanXet || ''}</div>
                <div class="text-muted small" style="font-size: 0.75rem;">
                    ${r.ngayDanhGia ? new Date(r.ngayDanhGia).toLocaleDateString('vi-VN') : 'Gần đây'}
                </div>
            </div>
        `).join('');
    } catch (err) {
        list.innerHTML = '<div class="text-muted text-center py-2">Không thể tải đánh giá.</div>';
    }
}

function setRating(star) {
    selectedRating = star;
    const stars = document.querySelectorAll('#ratingStars i');
    stars.forEach((s, idx) => {
        if (idx < star) {
            s.className = 'fa-solid fa-star text-warning';
        } else {
            s.className = 'fa-regular fa-star text-warning';
        }
    });
}

async function submitReview() {
    if (!token) {
        Swal.fire({
            icon: 'info',
            title: 'Yêu cầu đăng nhập',
            text: 'Bạn cần đăng nhập tài khoản để viết nhận xét cuốn sách này.',
            confirmButtonText: 'Đăng nhập ngay'
        }).then(() => openLoginModal());
        return;
    }

    const comment = document.getElementById('reviewComment').value.trim();
    if (!comment) {
        Swal.fire({ icon: 'warning', text: 'Vui lòng nhập nội dung nhận xét của bạn' });
        return;
    }

    try {
        Swal.showLoading();
        await apiRequest('/danh_gia', 'POST', {
            maSach: currentDetailBookId,
            sao: selectedRating,
            nhanXet: comment
        });

        document.getElementById('reviewComment').value = '';
        Swal.fire({
            icon: 'success',
            title: 'Thành công!',
            text: 'Cảm ơn bạn đã gửi đánh giá cho cuốn sách.',
            timer: 1500,
            showConfirmButton: false
        });

        await loadBookReviews(currentDetailBookId);
    } catch (err) {
        Swal.fire({ icon: 'error', title: 'Lỗi', text: err.message });
    }
}

// ================= CART & CHECKOUT =================
async function loadCartBadge() {
    if (!token) {
        updateCartBadge(0);
        return;
    }
    try {
        const res = await apiRequest('/gh/GH_of_user');
        const cart = res?.result;
        const totalItems = cart?.chiTietGioHangList?.reduce((acc, item) => acc + item.soLuongMua, 0) || 0;
        updateCartBadge(totalItems);
    } catch (e) {
        updateCartBadge(0);
    }
}

function updateCartBadge(count) {
    const badge = document.getElementById('cartBadge');
    if (!badge) return;
    if (count > 0) {
        badge.textContent = count;
        badge.classList.remove('d-none');
    } else {
        badge.classList.add('d-none');
    }
}

async function handleAddToCart(maSach, soLuongMua = 1) {
    if (!token) {
        Swal.fire({
            icon: 'info',
            title: 'Yêu cầu đăng nhập',
            text: 'Bạn vui lòng đăng nhập tài khoản để thêm sách vào giỏ hàng.',
            confirmButtonText: 'Đăng nhập ngay'
        }).then(() => openLoginModal());
        return;
    }

    try {
        Swal.showLoading();
        await apiRequest('/gh/addGH', 'POST', { maSach, soLuongMua });
        await loadCartBadge();

        Swal.fire({
            icon: 'success',
            title: 'Đã thêm vào giỏ hàng!',
            text: 'Cuốn sách đã được thêm thành công vào giỏ hàng của bạn.',
            timer: 1200,
            showConfirmButton: false
        });
    } catch (err) {
        Swal.fire({ icon: 'error', title: 'Lỗi thêm giỏ hàng', text: err.message });
    }
}

async function loadCartView() {
    if (!token) {
        Swal.fire({
            icon: 'info',
            title: 'Yêu cầu đăng nhập',
            text: 'Vui lòng đăng nhập để xem giỏ hàng của bạn.',
            confirmButtonText: 'Đăng nhập ngay'
        }).then(() => openLoginModal());
        showView('home');
        return;
    }

    const tbody = document.getElementById('cartTableBody');
    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4"><div class="spinner-border text-primary"></div></td></tr>';

    try {
        const res = await apiRequest('/gh/GH_of_user');
        currentCart = res?.result;
        renderCartTable(currentCart);
        await loadProvinces();
        populateVoucherDropdown();
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-danger">Không thể tải giỏ hàng: ${err.message}</td></tr>`;
    }
}

function renderCartTable(cart) {
    const tbody = document.getElementById('cartTableBody');
    const items = cart?.chiTietGioHangList || [];

    if (items.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-5">
                    <i class="fa-solid fa-cart-arrow-down fs-1 text-muted mb-2"></i>
                    <p class="text-muted fs-5 mb-3">Giỏ hàng của bạn hiện đang trống!</p>
                    <button class="btn btn-primary btn-sm px-4" onclick="showView('home')">Khám phá sách ngay</button>
                </td>
            </tr>
        `;
        updateCartCalculations(0);
        return;
    }

    let subtotal = 0;
    tbody.innerHTML = items.map(item => {
        const book = item.sach;
        const lineTotal = (item.soLuongMua || 1) * (book.donGia || 0);
        subtotal += lineTotal;
        const priceFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(book.donGia || 0);
        const lineTotalFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(lineTotal);
        const defaultCover = 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=400&q=80';
        const imgUrl = (book.hinhAnhList && book.hinhAnhList.length > 0) ? book.hinhAnhList[0].linkHinhAnh : defaultCover;

        return `
            <tr>
                <td>
                    <div class="d-flex align-items-center gap-3">
                        <img src="${imgUrl}" alt="${book.tenSach}" style="width: 50px; height: 65px; object-fit: cover;" class="rounded" onerror="this.src='${defaultCover}'">
                        <div>
                            <div class="fw-bold text-dark">${book.tenSach}</div>
                            <div class="small text-muted">${book.loaiSach?.tenLoai || ''}</div>
                        </div>
                    </div>
                </td>
                <td class="text-center fw-bold text-secondary">${priceFmt}</td>
                <td class="text-center">
                    <div class="input-group input-group-sm justify-content-center" style="width: 100px; margin: 0 auto;">
                        <button class="btn btn-outline-secondary" type="button" onclick="updateCartItemQty(${book.maSach}, -1)">-</button>
                        <input type="text" class="form-control text-center bg-white" value="${item.soLuongMua}" readonly>
                        <button class="btn btn-outline-secondary" type="button" onclick="updateCartItemQty(${book.maSach}, 1)">+</button>
                    </div>
                </td>
                <td class="text-end fw-bold text-danger">${lineTotalFmt}</td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm" onclick="removeCartItem(${book.maSach})" title="Xóa khỏi giỏ">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');

    updateCartCalculations(subtotal);
}

async function updateCartItemQty(maSach, delta) {
    const item = currentCart?.chiTietGioHangList?.find(i => i.sach.maSach === maSach);
    if (!item) return;
    const newQty = item.soLuongMua + delta;
    if (newQty <= 0) {
        removeCartItem(maSach);
        return;
    }

    try {
        await apiRequest('/gh/update-soluong', 'PUT', { maSach, soLuong: newQty });
        await loadCartView();
        await loadCartBadge();
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

async function removeCartItem(maSach) {
    const confirm = await Swal.fire({
        title: 'Xóa khỏi giỏ hàng?',
        text: 'Bạn có chắc chắn muốn bỏ cuốn sách này khỏi giỏ hàng?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Đồng ý xóa',
        cancelButtonText: 'Giữ lại'
    });

    if (!confirm.isConfirmed) return;

    try {
        await apiRequest(`/gh/delete-GH/${maSach}`, 'DELETE');
        await loadCartView();
        await loadCartBadge();
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

function updateCartCalculations(subtotal) {
    const shipping = 20000;
    let discount = 0;

    if (selectedVoucher && subtotal >= (selectedVoucher.donHangToiThieu || 0)) {
        discount = subtotal * (selectedVoucher.chietKhau || 0) / 100;
    }

    const total = Math.max(0, subtotal - discount + (subtotal > 0 ? shipping : 0));

    const fmt = (n) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n);

    document.getElementById('cartSubtotal').textContent = fmt(subtotal);
    document.getElementById('cartDiscount').textContent = `-${fmt(discount)}`;
    document.getElementById('cartShipping').textContent = subtotal > 0 ? fmt(shipping) : fmt(0);
    document.getElementById('cartTotal').textContent = fmt(total);

    const btn = document.getElementById('btnCheckout');
    if (btn) btn.disabled = (subtotal <= 0);
}

// Vouchers
async function loadVouchers() {
    try {
        const res = await apiRequest('/giam-gia');
        allVouchers = res?.result || [];
    } catch (e) {
        allVouchers = [];
    }
}

function populateVoucherDropdown() {
    const sel = document.getElementById('voucherSelect');
    if (!sel) return;
    sel.innerHTML = '<option value="">Chọn hoặc nhập mã giảm giá...</option>' + 
        allVouchers.map(v => `<option value="${v.maGiamGia}">Giảm ${v.chietKhau}% (Đơn từ ${new Intl.NumberFormat('vi-VN').format(v.donHangToiThieu || 0)}đ)</option>`).join('');
}

function applyVoucherSelect() {
    const vId = document.getElementById('voucherSelect').value;
    selectedVoucher = allVouchers.find(v => v.maGiamGia == vId) || null;
    
    // Recalculate
    const subtotalText = document.getElementById('cartSubtotal').textContent.replace(/[^\d]/g, '');
    const subtotal = parseInt(subtotalText) || 0;
    updateCartCalculations(subtotal);
}

function loadVouchersView() {
    const grid = document.getElementById('vouchersGrid');
    if (!allVouchers || allVouchers.length === 0) {
        grid.innerHTML = '<div class="col-12 text-center text-muted py-5">Hiện tại chưa có mã giảm giá nào đang áp dụng.</div>';
        return;
    }

    grid.innerHTML = allVouchers.map(v => `
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm p-3 h-100" style="border-left: 4px solid var(--accent-color) !important;">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="badge bg-warning text-dark fw-bold px-3 py-1 fs-6">GIẢM ${v.chietKhau}%</span>
                    <span class="small text-muted">Mã: #${v.maGiamGia}</span>
                </div>
                <h6 class="fw-bold text-dark mb-1">Voucher Ưu Đãi Độc Quyền FBC</h6>
                <p class="small text-muted mb-2">Áp dụng cho đơn hàng từ ${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v.donHangToiThieu || 0)}</p>
                <div class="small text-secondary mb-3">
                    <i class="fa-regular fa-calendar-days me-1"></i>
                    Hạn dùng: ${v.ngayBatDau || ''} đến ${v.ngayKetThuc || ''}
                </div>
                <button class="btn btn-outline-primary btn-sm mt-auto" onclick="showView('cart')">
                    Dùng voucher ngay
                </button>
            </div>
        </div>
    `).join('');
}

// Location
async function loadProvinces() {
    const sel = document.getElementById('checkoutProvince');
    if (!sel) return;
    try {
        const res = await apiRequest('/tinh');
        const provinces = res?.result || [];
        sel.innerHTML = '<option value="">Chọn Tỉnh / Thành phố...</option>' + 
            provinces.map(p => `<option value="${p.maTinh}">${p.tenTinh}</option>`).join('');
    } catch (e) {
        sel.innerHTML = '<option value="1">Hà Nội</option><option value="2">TP. Hồ Chí Minh</option><option value="3">Đà Nẵng</option>';
    }
}

async function loadDistricts() {
    const pId = document.getElementById('checkoutProvince').value;
    const distSel = document.getElementById('checkoutDistrict');
    if (!pId || !distSel) return;
    try {
        const res = await apiRequest('/quan-huyen');
        const districts = res?.result || [];
        const filtered = districts.filter(d => !pId || d.tinh?.maTinh == pId);
        distSel.innerHTML = '<option value="">Chọn Quận / Huyện...</option>' + 
            filtered.map(d => `<option value="${d.maQuanHuyen}">${d.tenQuanHuyen}</option>`).join('');
    } catch (e) {
        distSel.innerHTML = '<option value="1">Bắc Từ Liêm</option><option value="2">Cầu Giấy</option>';
    }
}

// Checkout Process
async function processCheckout() {
    if (!currentCart || !currentCart.chiTietGioHangList || currentCart.chiTietGioHangList.length === 0) {
        Swal.fire({ icon: 'warning', text: 'Giỏ hàng của bạn đang rỗng!' });
        return;
    }

    const diaChiGiao = document.getElementById('checkoutAddress').value.trim();
    if (!diaChiGiao) {
        Swal.fire({ icon: 'warning', text: 'Vui lòng nhập địa chỉ nhận hàng chi tiết' });
        return;
    }

    const maQuanHuyen = document.getElementById('checkoutDistrict').value || 1;
    const maPTTT = document.getElementById('checkoutPayment').value || 1;
    const maGiamGia = selectedVoucher ? selectedVoucher.maGiamGia : null;

    const requestBody = {
        maQuanHuyen: parseInt(maQuanHuyen),
        diaChiGiao: diaChiGiao,
        phiGiaoHang: 20000,
        maPTTT: parseInt(maPTTT),
        maGiamGia: maGiamGia
    };

    try {
        Swal.showLoading();
        const res = await apiRequest('/don-hang/createDH', 'POST', requestBody);
        
        await loadCartBadge();

        Swal.fire({
            icon: 'success',
            title: 'Đặt hàng thành công!',
            text: `Mã đơn hàng: #${res?.result?.maDonHang || ''}. Chúng tôi sẽ xử lý và liên hệ giao hàng sớm nhất.`,
            confirmButtonText: 'Xem đơn hàng'
        }).then(() => {
            showView('orders');
        });
    } catch (err) {
        Swal.fire({
            icon: 'error',
            title: 'Đặt hàng thất bại',
            text: err.message || 'Không thể tạo đơn hàng, vui lòng thử lại.'
        });
    }
}

// ================= CUSTOMER ORDERS =================
async function loadUserOrders() {
    if (!token) return;
    const tbody = document.getElementById('userOrdersTableBody');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4"><div class="spinner-border text-primary"></div></td></tr>';

    try {
        const res = await apiRequest('/don-hang/don_hang_of_KH');
        const orders = res?.result || [];

        if (orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">Bạn chưa có đơn hàng nào.</td></tr>';
            return;
        }

        tbody.innerHTML = orders.map(o => {
            const dateStr = o.ngayTao ? new Date(o.ngayTao).toLocaleString('vi-VN') : 'Mới';
            const totalFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(o.tongTien || 0);
            const statusBadge = getStatusBadge(o.trangThai);
            const canCancel = (o.trangThai === 'CHO_XAC_NHAN' || o.trangThai?.includes('Chờ'));

            return `
                <tr>
                    <td class="fw-bold">#${o.maDonHang}</td>
                    <td>${dateStr}</td>
                    <td class="fw-bold text-danger">${totalFmt}</td>
                    <td>${statusBadge}</td>
                    <td class="small text-muted">${o.diaChiGiao || o.ghiChu || 'Đơn hàng FBC'}</td>
                    <td class="text-center">
                        <button class="btn btn-outline-primary btn-sm me-1" onclick="openOrderDetailModal(${o.maDonHang})">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                        ${canCancel ? `
                        <button class="btn btn-outline-danger btn-sm" onclick="cancelCustomerOrder(${o.maDonHang})" title="Hủy đơn">
                            <i class="fa-solid fa-ban"></i>
                        </button>
                        ` : ''}
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">${err.message}</td></tr>`;
    }
}

function getStatusBadge(status) {
    if (!status) return '<span class="badge bg-secondary">Không rõ</span>';
    if (status.includes('CHO') || status.includes('Chờ')) return '<span class="badge bg-warning text-dark"><i class="fa-solid fa-clock me-1"></i>Chờ xác nhận</span>';
    if (status.includes('DA_XAC') || status.includes('Đã xác')) return '<span class="badge bg-primary"><i class="fa-solid fa-check me-1"></i>Đã xác nhận</span>';
    if (status.includes('DANG_GIAO') || status.includes('Đang')) return '<span class="badge bg-info text-dark"><i class="fa-solid fa-truck-fast me-1"></i>Đang giao</span>';
    if (status.includes('DA_GIAO') || status.includes('thành công')) return '<span class="badge bg-success"><i class="fa-solid fa-circle-check me-1"></i>Đã giao</span>';
    if (status.includes('HUY') || status.includes('Hủy')) return '<span class="badge bg-danger"><i class="fa-solid fa-xmark me-1"></i>Đã hủy</span>';
    return `<span class="badge bg-secondary">${status}</span>`;
}

async function cancelCustomerOrder(maDonHang) {
    const confirm = await Swal.fire({
        title: 'Xác nhận hủy đơn hàng?',
        text: `Bạn có chắc chắn muốn hủy đơn hàng #${maDonHang}?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Đồng ý hủy',
        cancelButtonText: 'Đóng'
    });

    if (!confirm.isConfirmed) return;

    try {
        await apiRequest(`/don-hang/${maDonHang}/updateTTofKH`, 'PUT', {
            maDonHang: maDonHang,
            trangThai: 'HUY'
        });

        Swal.fire({ icon: 'success', title: 'Đã hủy đơn hàng thành công', timer: 1500, showConfirmButton: false });
        await loadUserOrders();
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

async function openOrderDetailModal(maDonHang) {
    const body = document.getElementById('orderDetailModalBody');
    body.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';
    new bootstrap.Modal(document.getElementById('orderDetailModal')).show();

    try {
        const res = await apiRequest(`/don-hang/getDH/${maDonHang}`);
        const order = res?.result;
        if (!order) throw new Error("Không tìm thấy thông tin đơn hàng");

        const totalFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.tongTien || 0);

        body.innerHTML = `
            <div class="row g-3 mb-3">
                <div class="col-md-6">
                    <div class="small text-muted">Mã đơn hàng:</div>
                    <div class="fs-5 fw-bold text-primary">#${order.maDonHang}</div>
                </div>
                <div class="col-md-6 text-md-end">
                    <div class="small text-muted">Trạng thái:</div>
                    <div>${getStatusBadge(order.trangThai)}</div>
                </div>
                <div class="col-md-6">
                    <div class="small text-muted">Khách hàng:</div>
                    <div class="fw-bold">${order.khachHang?.hoTen || order.khachHang?.userName || 'Khách vãng lai'}</div>
                </div>
                <div class="col-md-6">
                    <div class="small text-muted">Địa chỉ nhận hàng:</div>
                    <div class="fw-bold">${order.diaChiGiao || 'Theo hồ sơ'}</div>
                </div>
            </div>
            <hr>
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fs-5 fw-bold">Tổng thanh toán:</span>
                <span class="fs-4 fw-bold text-danger">${totalFmt}</span>
            </div>
        `;
    } catch (err) {
        body.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}

// ================= PROFILE & PASSWORD =================
function loadProfileView() {
    if (!currentUser) return;
    document.getElementById('profUsername').value = currentUser.userName || '';
    document.getElementById('profRole').value = currentUser.role || 'USER';
    document.getElementById('profFullName').value = currentUser.hoTen || '';
    document.getElementById('profEmail').value = currentUser.email || '';
    document.getElementById('profPhone').value = currentUser.soDT || '';
    document.getElementById('profAddress').value = currentUser.diaChi || '';
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    if (!currentUser) return;

    const hoTen = document.getElementById('profFullName').value.trim();
    const email = document.getElementById('profEmail').value.trim();
    const soDT = document.getElementById('profPhone').value.trim();
    const diaChi = document.getElementById('profAddress').value.trim();

    try {
        Swal.showLoading();
        await apiRequest(`/kh/${currentUser.maKH || currentUser.userName}`, 'PUT', {
            hoTen, email, soDT, diaChi
        });
        currentUser.hoTen = hoTen;
        currentUser.email = email;
        currentUser.soDT = soDT;
        currentUser.diaChi = diaChi;
        renderAuthUI(currentUser);

        Swal.fire({ icon: 'success', title: 'Đã cập nhật hồ sơ thành công!', timer: 1500, showConfirmButton: false });
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

async function handleChangePassword(e) {
    e.preventDefault();
    if (!currentUser) return;

    const newPass = document.getElementById('newPassword').value;
    const confirmPass = document.getElementById('confirmNewPassword').value;

    if (newPass !== confirmPass) {
        Swal.fire({ icon: 'warning', text: 'Mật khẩu xác nhận không trùng khớp' });
        return;
    }

    try {
        Swal.showLoading();
        await apiRequest(`/kh/password/${currentUser.maKH || currentUser.userName}`, 'PUT', {
            matKhauMoi: newPass
        });
        document.getElementById('newPassword').value = '';
        document.getElementById('confirmNewPassword').value = '';

        Swal.fire({ icon: 'success', title: 'Đổi mật khẩu thành công!', timer: 1500, showConfirmButton: false });
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

// ================= ADMIN & STAFF DASHBOARD =================
function switchAdminTab(tabName) {
    const tabs = ['stats', 'orders', 'books', 'categories', 'vouchers', 'users'];
    tabs.forEach(t => {
        const el = document.getElementById(`admin-tab-${t}`);
        if (el) el.classList.add('d-none');
    });

    const activeEl = document.getElementById(`admin-tab-${tabName}`);
    if (activeEl) activeEl.classList.remove('d-none');

    const links = document.querySelectorAll('#adminTabs .nav-link');
    links.forEach(l => l.classList.remove('active'));
    event?.target?.closest('.nav-link')?.classList.add('active');

    if (tabName === 'stats') loadAdminStats();
    if (tabName === 'orders') loadAdminOrders();
    if (tabName === 'books') loadAdminBooks();
    if (tabName === 'categories') loadAdminCategoriesAndAuthors();
    if (tabName === 'vouchers') loadAdminVouchers();
    if (tabName === 'users') loadAdminUsers();
}

function loadAdminDashboard() {
    if (!currentUser || (currentUser.role !== 'ADMIN' && currentUser.role !== 'STAFF')) {
        Swal.fire({ icon: 'error', text: 'Bạn không có quyền truy cập trang quản trị' });
        showView('home');
        return;
    }

    // Role badge
    const badge = document.getElementById('adminRoleBadge');
    if (badge) {
        badge.innerHTML = (currentUser.role === 'ADMIN')
            ? '<span class="badge bg-danger px-3 py-2 fs-6">QUẢN TRỊ VIÊN (ADMIN)</span>'
            : '<span class="badge bg-info text-dark px-3 py-2 fs-6">NHÂN VIÊN (STAFF)</span>';
    }

    // Hide admin-only tabs for staff
    const adminOnlyTabs = document.querySelectorAll('.admin-only');
    adminOnlyTabs.forEach(t => {
        if (currentUser.role !== 'ADMIN') t.classList.add('d-none');
        else t.classList.remove('d-none');
    });

    loadAdminStats();
}

async function loadAdminStats() {
    try {
        const res = await apiRequest('/baocao_doanhthu');
        const stats = res?.result;

        const rev = stats?.doanhThu || 0;
        const totalOrders = stats?.tongSoDonHang || 0;
        const totalBooksSold = stats?.tongSoLuongSachDaBan || 0;

        document.getElementById('statRevenue').textContent = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(rev);
        document.getElementById('statTotalOrders').textContent = totalOrders;
        document.getElementById('statBooksSold').textContent = `${totalBooksSold} cuốn`;

        // Render Top Selling Table
        const topTable = document.getElementById('topSellingTableBody');
        const topBooks = stats?.thongKeSachList || [];
        if (topBooks.length === 0) {
            topTable.innerHTML = '<tr><td colspan="2" class="text-center text-muted">Chưa có dữ liệu</td></tr>';
        } else {
            topTable.innerHTML = topBooks.slice(0, 5).map(b => `
                <tr>
                    <td class="fw-bold text-dark">${b.tenSach || 'Sách'}</td>
                    <td class="text-end fw-bold text-primary">${b.soLuong || 0}</td>
                </tr>
            `).join('');
        }

        // Render Chart.js
        renderRevenueChart(rev, totalOrders, totalBooksSold);
    } catch (err) {
        console.error("Error loading admin stats:", err);
    }
}

function renderRevenueChart(revenue, orders, booksSold) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    if (revenueChartInstance) {
        revenueChartInstance.destroy();
    }

    revenueChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Doanh thu (triệu đ)', 'Đơn hàng (chiếc)', 'Sách đã bán (cuốn)'],
            datasets: [{
                label: 'Chỉ số kinh doanh FBC Bookstore',
                data: [revenue / 1000000, orders, booksSold],
                backgroundColor: ['#10b981', '#3b82f6', '#f59e0b'],
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            }
        }
    });
}

// Admin Orders
async function loadAdminOrders() {
    const tbody = document.getElementById('adminOrdersTableBody');
    tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4"><div class="spinner-border text-primary"></div></td></tr>';

    try {
        const res = await apiRequest('/don-hang/getALLDH');
        const orders = res?.result || [];

        if (orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">Chưa có đơn hàng nào trong hệ thống.</td></tr>';
            return;
        }

        tbody.innerHTML = orders.map(o => {
            const dateStr = o.ngayTao ? new Date(o.ngayTao).toLocaleDateString('vi-VN') : 'Mới';
            const totalFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(o.tongTien || 0);

            return `
                <tr>
                    <td class="fw-bold">#${o.maDonHang}</td>
                    <td>${o.khachHang?.hoTen || o.khachHang?.userName || 'Khách'}</td>
                    <td>${dateStr}</td>
                    <td class="fw-bold text-danger">${totalFmt}</td>
                    <td>${getStatusBadge(o.trangThai)}</td>
                    <td>
                        <select class="form-select form-select-sm" onchange="updateOrderStatusAdmin(${o.maDonHang}, this.value)">
                            <option value="">Cập nhật trạng thái...</option>
                            <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
                            <option value="DA_XAC_NHAN">Đã xác nhận</option>
                            <option value="DANG_GIAO">Đang giao hàng</option>
                            <option value="DA_GIAO">Đã giao thành công</option>
                            <option value="HUY">Hủy đơn hàng</option>
                        </select>
                    </td>
                    <td class="text-center">
                        <button class="btn btn-outline-primary btn-sm" onclick="openOrderDetailModal(${o.maDonHang})">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-danger">${err.message}</td></tr>`;
    }
}

async function updateOrderStatusAdmin(maDonHang, newStatus) {
    if (!newStatus) return;
    try {
        Swal.showLoading();
        await apiRequest(`/don-hang/${maDonHang}/updateTT`, 'PUT', {
            maDonHang: maDonHang,
            trangThai: newStatus
        });

        Swal.fire({ icon: 'success', title: 'Cập nhật trạng thái thành công', timer: 1200, showConfirmButton: false });
        await loadAdminOrders();
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

// Admin Books
async function loadAdminBooks() {
    const tbody = document.getElementById('adminBooksTableBody');
    tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4"><div class="spinner-border text-primary"></div></td></tr>';

    try {
        const res = await apiRequest('/sach');
        const books = res?.result || [];

        tbody.innerHTML = books.map(b => {
            const priceFmt = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(b.donGia || 0);
            return `
                <tr>
                    <td>${b.maSach}</td>
                    <td class="fw-bold text-dark">${b.tenSach}</td>
                    <td><span class="badge bg-light text-primary border">${b.loaiSach?.tenLoai || 'Chung'}</span></td>
                    <td class="fw-bold text-danger">${priceFmt}</td>
                    <td><span class="badge ${b.soLuongCo > 0 ? 'bg-success' : 'bg-danger'}">${b.soLuongCo}</span></td>
                    <td class="small text-muted">${b.nhaXuatBan?.tenNXB || 'NXB Văn Học'}</td>
                    <td class="text-center">
                        <button class="btn btn-outline-warning btn-sm me-1" onclick="openEditBookModal(${b.maSach})" title="Sửa">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button class="btn btn-outline-danger btn-sm" onclick="deleteBookAdmin(${b.maSach})" title="Xóa">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-danger">${err.message}</td></tr>`;
    }
}

function openAddBookModal() {
    document.getElementById('bookModalTitle').textContent = 'Thêm Sách Mới';
    document.getElementById('bookFormId').value = '';
    document.getElementById('bookFormName').value = '';
    document.getElementById('bookFormPrice').value = '100000';
    document.getElementById('bookFormStock').value = '50';
    document.getElementById('bookFormDesc').value = '';
    new bootstrap.Modal(document.getElementById('bookModal')).show();
}

function openEditBookModal(maSach) {
    const book = allBooks.find(b => b.maSach === maSach);
    if (!book) return;

    document.getElementById('bookModalTitle').textContent = 'Sửa Thông Tin Sách';
    document.getElementById('bookFormId').value = book.maSach;
    document.getElementById('bookFormName').value = book.tenSach || '';
    document.getElementById('bookFormPrice').value = book.donGia || 0;
    document.getElementById('bookFormStock').value = book.soLuongCo || 0;
    document.getElementById('bookFormDesc').value = book.moTa || '';
    if (book.loaiSach) document.getElementById('bookFormCategory').value = book.loaiSach.maLoai;

    new bootstrap.Modal(document.getElementById('bookModal')).show();
}

async function handleSaveBook(e) {
    e.preventDefault();
    const id = document.getElementById('bookFormId').value;
    const tenSach = document.getElementById('bookFormName').value.trim();
    const donGia = parseFloat(document.getElementById('bookFormPrice').value) || 0;
    const soLuongCo = parseInt(document.getElementById('bookFormStock').value) || 0;
    const donViTinh = document.getElementById('bookFormUnit').value.trim() || 'cuốn';
    const maLoai = parseInt(document.getElementById('bookFormCategory').value) || 1;
    const tenNXB = document.getElementById('bookFormPublisher').value.trim() || 'NXB Văn Học';
    const moTa = document.getElementById('bookFormDesc').value.trim();
    const maTG = document.getElementById('bookFormAuthor').value ? [parseInt(document.getElementById('bookFormAuthor').value)] : [];

    const payload = { tenSach, donGia, soLuongCo, donViTinh, maLoai, tenNXB, moTa, maTG };

    try {
        Swal.showLoading();
        if (id) {
            await apiRequest(`/sach/${id}`, 'PUT', payload);
        } else {
            await apiRequest('/sach', 'POST', payload);
        }

        bootstrap.Modal.getInstance(document.getElementById('bookModal'))?.hide();
        Swal.fire({ icon: 'success', title: 'Lưu thông tin sách thành công!', timer: 1500, showConfirmButton: false });
        await loadBooks();
        await loadAdminBooks();
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

async function deleteBookAdmin(maSach) {
    const confirm = await Swal.fire({
        title: 'Xác nhận xóa sách?',
        text: `Bạn có chắc muốn xóa cuốn sách #${maSach}?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Xóa ngay',
        cancelButtonText: 'Hủy'
    });
    if (!confirm.isConfirmed) return;

    try {
        await apiRequest(`/sach/${maSach}`, 'DELETE');
        Swal.fire({ icon: 'success', title: 'Đã xóa sách thành công!', timer: 1200, showConfirmButton: false });
        await loadBooks();
        await loadAdminBooks();
    } catch (err) {
        Swal.fire({ icon: 'error', text: err.message });
    }
}

// Categories & Authors
async function loadAdminCategoriesAndAuthors() {
    await Promise.all([loadCategories(), loadAuthors()]);

    const catBody = document.getElementById('adminCategoriesTableBody');
    catBody.innerHTML = allCategories.map(c => `
        <tr>
            <td>${c.maLoai}</td>
            <td class="fw-bold">${c.tenLoai}</td>
            <td class="small text-muted">${c.moTa || ''}</td>
            <td class="text-center">
                <button class="btn btn-outline-danger btn-sm" onclick="deleteCategoryAdmin(${c.maLoai})">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');

    const authorBody = document.getElementById('adminAuthorsTableBody');
    authorBody.innerHTML = allAuthors.map(a => `
        <tr>
            <td>${a.maTG}</td>
            <td class="fw-bold">${a.tenTG}</td>
            <td class="small text-muted">${a.moTa || ''}</td>
            <td class="text-center">
                <button class="btn btn-outline-danger btn-sm" onclick="deleteAuthorAdmin(${a.maTG})">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function openAddCategoryModal() {
    const { value: formValues } = await Swal.fire({
        title: 'Thêm Thể Loại Mới',
        html: `
            <input id="swal-cat-name" class="swal2-input" placeholder="Tên thể loại (VD: Lịch sử, Kinh tế...)">
            <input id="swal-cat-desc" class="swal2-input" placeholder="Mô tả tóm tắt">
        `,
        focusConfirm: false,
        showCancelButton: true,
        preConfirm: () => [
            document.getElementById('swal-cat-name').value,
            document.getElementById('swal-cat-desc').value
        ]
    });

    if (formValues && formValues[0]) {
        try {
            await apiRequest('/loai-sach', 'POST', { tenLoai: formValues[0], moTa: formValues[1] });
            Swal.fire({ icon: 'success', title: 'Thêm thể loại thành công', timer: 1200, showConfirmButton: false });
            await loadAdminCategoriesAndAuthors();
        } catch (e) {
            Swal.fire({ icon: 'error', text: e.message });
        }
    }
}

async function deleteCategoryAdmin(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa thể loại này?')) return;
    try {
        await apiRequest(`/loai-sach/${id}`, 'DELETE');
        await loadAdminCategoriesAndAuthors();
    } catch (e) {
        Swal.fire({ icon: 'error', text: e.message });
    }
}

async function openAddAuthorModal() {
    const { value: formValues } = await Swal.fire({
        title: 'Thêm Tác Giả Mới',
        html: `
            <input id="swal-author-name" class="swal2-input" placeholder="Tên tác giả">
            <input id="swal-author-desc" class="swal2-input" placeholder="Tiểu sử / Giới thiệu">
        `,
        focusConfirm: false,
        showCancelButton: true,
        preConfirm: () => [
            document.getElementById('swal-author-name').value,
            document.getElementById('swal-author-desc').value
        ]
    });

    if (formValues && formValues[0]) {
        try {
            await apiRequest('/tac-gia', 'POST', { tenTG: formValues[0], moTa: formValues[1] });
            Swal.fire({ icon: 'success', title: 'Thêm tác giả thành công', timer: 1200, showConfirmButton: false });
            await loadAdminCategoriesAndAuthors();
        } catch (e) {
            Swal.fire({ icon: 'error', text: e.message });
        }
    }
}

async function deleteAuthorAdmin(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa tác giả này?')) return;
    try {
        await apiRequest(`/tac-gia/${id}`, 'DELETE');
        await loadAdminCategoriesAndAuthors();
    } catch (e) {
        Swal.fire({ icon: 'error', text: e.message });
    }
}

// Admin Vouchers
async function loadAdminVouchers() {
    await loadVouchers();
    const tbody = document.getElementById('adminVouchersTableBody');
    tbody.innerHTML = allVouchers.map(v => `
        <tr>
            <td class="fw-bold">#${v.maGiamGia}</td>
            <td class="text-danger fw-bold">${v.chietKhau}%</td>
            <td>${new Intl.NumberFormat('vi-VN').format(v.donHangToiThieu || 0)} đ</td>
            <td class="small text-muted">${v.ngayBatDau || ''}</td>
            <td class="small text-muted">${v.ngayKetThuc || ''}</td>
            <td class="text-center">
                <button class="btn btn-outline-danger btn-sm" onclick="deleteVoucherAdmin(${v.maGiamGia})">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function openAddVoucherModal() {
    const { value: formValues } = await Swal.fire({
        title: 'Tạo Mã Giảm Giá Mới',
        html: `
            <div class="text-start small fw-bold mb-1">Mức giảm giá (%):</div>
            <input id="v-discount" type="number" class="swal2-input mt-0" placeholder="VD: 10, 20" min="1" max="100" value="10">
            <div class="text-start small fw-bold mb-1 mt-2">Đơn hàng tối thiểu (VNĐ):</div>
            <input id="v-min" type="number" class="swal2-input mt-0" placeholder="VD: 200000" value="150000">
            <div class="text-start small fw-bold mb-1 mt-2">Ngày bắt đầu:</div>
            <input id="v-start" type="date" class="swal2-input mt-0" value="${new Date().toISOString().split('T')[0]}">
            <div class="text-start small fw-bold mb-1 mt-2">Ngày kết thúc:</div>
            <input id="v-end" type="date" class="swal2-input mt-0" value="${new Date(Date.now() + 30*86400000).toISOString().split('T')[0]}">
        `,
        focusConfirm: false,
        showCancelButton: true,
        preConfirm: () => [
            document.getElementById('v-discount').value,
            document.getElementById('v-min').value,
            document.getElementById('v-start').value,
            document.getElementById('v-end').value
        ]
    });

    if (formValues && formValues[0]) {
        try {
            await apiRequest('/giam-gia', 'POST', {
                chietKhau: parseFloat(formValues[0]),
                donHangToiThieu: parseFloat(formValues[1]),
                ngayBatDau: formValues[2],
                ngayKetThuc: formValues[3]
            });
            Swal.fire({ icon: 'success', title: 'Tạo Voucher thành công', timer: 1200, showConfirmButton: false });
            await loadAdminVouchers();
        } catch (e) {
            Swal.fire({ icon: 'error', text: e.message });
        }
    }
}

async function deleteVoucherAdmin(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa voucher này?')) return;
    try {
        await apiRequest(`/giam-gia/${id}`, 'DELETE');
        await loadAdminVouchers();
    } catch (e) {
        Swal.fire({ icon: 'error', text: e.message });
    }
}

// Admin Users (Staff & Customers)
async function loadAdminUsers() {
    // Staff
    const staffBody = document.getElementById('adminStaffTableBody');
    try {
        const res = await apiRequest('/nv');
        const staffList = res?.result || [];
        staffBody.innerHTML = staffList.map(s => `
            <tr>
                <td>${s.maNV}</td>
                <td class="fw-bold">${s.tenDangNhap}</td>
                <td>${s.hoTen || ''}</td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm" onclick="deleteStaffAdmin(${s.maNV})" ${s.tenDangNhap === 'admin' ? 'disabled' : ''}>
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        staffBody.innerHTML = '<tr><td colspan="4" class="text-muted text-center">Không thể tải danh sách nhân viên</td></tr>';
    }

    // Customers
    const custBody = document.getElementById('adminCustomersTableBody');
    try {
        const res = await apiRequest('/kh');
        const custList = res?.result || [];
        custBody.innerHTML = custList.map(c => `
            <tr>
                <td>${c.maKH || c.userName}</td>
                <td class="fw-bold">${c.userName}</td>
                <td>${c.hoTen || ''}</td>
                <td>${c.soDT || ''}</td>
            </tr>
        `).join('');
    } catch (e) {
        custBody.innerHTML = '<tr><td colspan="4" class="text-muted text-center">Không thể tải danh sách khách hàng</td></tr>';
    }
}

async function openAddStaffModal() {
    const { value: formValues } = await Swal.fire({
        title: 'Thêm Nhân Viên Mới',
        html: `
            <input id="s-username" class="swal2-input" placeholder="Tên đăng nhập (username)">
            <input id="s-password" type="password" class="swal2-input" placeholder="Mật khẩu">
            <input id="s-fullname" class="swal2-input" placeholder="Họ và tên">
            <input id="s-cccd" class="swal2-input" placeholder="Số CCCD">
        `,
        focusConfirm: false,
        showCancelButton: true,
        preConfirm: () => [
            document.getElementById('s-username').value,
            document.getElementById('s-password').value,
            document.getElementById('s-fullname').value,
            document.getElementById('s-cccd').value
        ]
    });

    if (formValues && formValues[0]) {
        try {
            await apiRequest('/nv', 'POST', {
                tenDangNhap: formValues[0],
                matKhau: formValues[1],
                hoTen: formValues[2],
                soCCCD: formValues[3]
            });
            Swal.fire({ icon: 'success', title: 'Thêm nhân viên thành công', timer: 1200, showConfirmButton: false });
            await loadAdminUsers();
        } catch (e) {
            Swal.fire({ icon: 'error', text: e.message });
        }
    }
}

async function deleteStaffAdmin(maNV) {
    if (!confirm('Bạn có chắc chắn muốn xóa nhân viên này?')) return;
    try {
        await apiRequest(`/nv/${maNV}`, 'DELETE');
        await loadAdminUsers();
    } catch (e) {
        Swal.fire({ icon: 'error', text: e.message });
    }
}
