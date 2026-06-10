-- 1. Tạo Database mới
CREATE DATABASE HeThongQuanLyCaLamViec;
GO

USE HeThongQuanLyCaLamViec;
GO

-- 2. Tạo bảng Nhân Viên
CREATE TABLE NhanVien (
    maNV VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    vaiTro NVARCHAR(50) NOT NULL,    -- Admin, Staff...
    taiKhoan VARCHAR(50) UNIQUE NOT NULL,
    matKhau VARCHAR(255) NOT NULL,   -- Để độ dài lớn phục vụ hash BCrypt
    trangThai NVARCHAR(30) DEFAULT N'HoatDong' -- HoatDong, NgungHoatDong
);
GO

-- 3. Tạo bảng Ca Làm Việc
CREATE TABLE CaLamViec (
    maCa VARCHAR(20) PRIMARY KEY,
    tenCa NVARCHAR(50) NOT NULL,
    gioBatDau TIME NOT NULL,
    gioKetThuc TIME NOT NULL
);
GO

-- 4. Tạo bảng Lịch Phân Ca
CREATE TABLE LichPhanCa (
    maLich VARCHAR(20) PRIMARY KEY,
    maNV VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV),
    maCa VARCHAR(20) FOREIGN KEY REFERENCES CaLamViec(maCa),
    ngayLamViec DATE NOT NULL,
    trangThai NVARCHAR(30) DEFAULT N'DaPhan' -- DaPhan, DaHuy
);
GO

-- 5. Tạo bảng Chấm Công
CREATE TABLE ChamCong (
    maCong VARCHAR(20) PRIMARY KEY,
    maLich VARCHAR(20) FOREIGN KEY REFERENCES LichPhanCa(maLich),
    gioVao DATETIME NULL,            -- Sẽ lấy bằng GETDATE() từ Server theo quy tắc
    gioRa DATETIME NULL,             -- Sẽ lấy bằng GETDATE() từ Server theo quy tắc
    trangThai NVARCHAR(50) NULL,     -- DungGio, DiMuon, VeSom
    minhChung NVARCHAR(255) NULL     -- Đường dẫn ảnh hoặc ghi chú nếu có
);
GO

-- 6. Tạo bảng Yêu Cầu Đổi Ca
CREATE TABLE YeuCauDoiCa (
    maYeuCau VARCHAR(20) PRIMARY KEY,
    maLichGoc VARCHAR(20) FOREIGN KEY REFERENCES LichPhanCa(maLich),
    maNVTarget VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV), -- Người được yêu cầu đổi cùng
    lyDo NVARCHAR(255) NULL,
    trangThai NVARCHAR(30) DEFAULT N'ChoDuyet', -- ChoDuyet, DaChapNhan, TuChoi
    ngayTao DATETIME DEFAULT GETDATE()
);
GO

-- =====================================
-- LOGIN
-- =====================================
GO
CREATE PROCEDURE sp_Login
    @TaiKhoan VARCHAR(50),
    @MatKhau VARCHAR(255)
AS
BEGIN
    SELECT *
    FROM NhanVien
    WHERE taiKhoan = @TaiKhoan
      AND matKhau = @MatKhau
      AND trangThai = N'HoatDong';
END
GO

-- =====================================
-- TÌM NHÂN VIÊN THEO MÃ
-- =====================================
CREATE PROCEDURE sp_FindNhanVienById
    @MaNV VARCHAR(20)
AS
BEGIN
    SELECT *
    FROM NhanVien
    WHERE maNV = @MaNV;
END
GO

-- =====================================
-- LẤY TOÀN BỘ NHÂN VIÊN
-- =====================================
CREATE PROCEDURE sp_FindAllNhanVien
AS
BEGIN
    SELECT *
    FROM NhanVien;
END
GO

-- =====================================
-- KHÓA NHÂN VIÊN
-- =====================================
CREATE PROCEDURE sp_DeactivateNhanVien
    @MaNV VARCHAR(20)
AS
BEGIN
    UPDATE NhanVien
    SET trangThai = N'NgungHoatDong'
    WHERE maNV = @MaNV;
END
GO