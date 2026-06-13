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
-- =====================================
-- THÊM NHÂN VIÊN
-- =====================================
CREATE PROCEDURE sp_InsertNhanVien
    @MaNV     VARCHAR(20),
    @HoTen    NVARCHAR(100),
    @VaiTro   NVARCHAR(50),
    @TaiKhoan VARCHAR(50),
    @MatKhau  VARCHAR(255),
    @TrangThai NVARCHAR(30) = N'HoatDong'
AS
BEGIN
    INSERT INTO NhanVien (maNV, hoTen, vaiTro, taiKhoan, matKhau, trangThai)
    VALUES (@MaNV, @HoTen, @VaiTro, @TaiKhoan, @MatKhau, @TrangThai);
END
GO

-- =====================================
-- CẬP NHẬT NHÂN VIÊN
-- =====================================
CREATE PROCEDURE sp_UpdateNhanVien
    @MaNV     VARCHAR(20),
    @HoTen    NVARCHAR(100),
    @VaiTro   NVARCHAR(50),
    @TaiKhoan VARCHAR(50),
    @MatKhau  VARCHAR(255)
AS
BEGIN
    UPDATE NhanVien
    SET hoTen = @HoTen, vaiTro = @VaiTro, taiKhoan = @TaiKhoan, matKhau = @MatKhau
    WHERE maNV = @MaNV;
END
GO

-- =====================================
-- CA LÀM VIỆC
-- =====================================
CREATE PROCEDURE sp_FindAllCaLamViec
AS
BEGIN
    SELECT * FROM CaLamViec;
END
GO

CREATE PROCEDURE sp_FindCaLamViecById
    @MaCa VARCHAR(20)
AS
BEGIN
    SELECT * FROM CaLamViec WHERE maCa = @MaCa;
END
GO

CREATE PROCEDURE sp_InsertCaLamViec
    @MaCa       VARCHAR(20),
    @TenCa      NVARCHAR(50),
    @GioBatDau  TIME,
    @GioKetThuc TIME
AS
BEGIN
    INSERT INTO CaLamViec (maCa, tenCa, gioBatDau, gioKetThuc)
    VALUES (@MaCa, @TenCa, @GioBatDau, @GioKetThuc);
END
GO

CREATE PROCEDURE sp_UpdateCaLamViec
    @MaCa       VARCHAR(20),
    @TenCa      NVARCHAR(50),
    @GioBatDau  TIME,
    @GioKetThuc TIME
AS
BEGIN
    UPDATE CaLamViec
    SET tenCa = @TenCa, gioBatDau = @GioBatDau, gioKetThuc = @GioKetThuc
    WHERE maCa = @MaCa;
END
GO

CREATE PROCEDURE sp_DeleteCaLamViec
    @MaCa VARCHAR(20)
AS
BEGIN
    DELETE FROM CaLamViec WHERE maCa = @MaCa;
END
GO

-- =====================================
-- LỊCH PHÂN CA
-- =====================================
CREATE PROCEDURE sp_FindAllLichPhanCa
AS
BEGIN
    SELECT * FROM LichPhanCa;
END
GO

CREATE PROCEDURE sp_FindLichPhanCaById
    @MaLich VARCHAR(20)
AS
BEGIN
    SELECT * FROM LichPhanCa WHERE maLich = @MaLich;
END
GO

CREATE PROCEDURE sp_InsertLichPhanCa
    @MaLich      VARCHAR(20),
    @MaNV        VARCHAR(20),
    @MaCa        VARCHAR(20),
    @NgayLamViec DATE,
    @TrangThai   NVARCHAR(30) = N'DaPhan'
AS
BEGIN
    INSERT INTO LichPhanCa (maLich, maNV, maCa, ngayLamViec, trangThai)
    VALUES (@MaLich, @MaNV, @MaCa, @NgayLamViec, @TrangThai);
END
GO

CREATE PROCEDURE sp_UpdateLichPhanCa
    @MaLich    VARCHAR(20),
    @TrangThai NVARCHAR(30)
AS
BEGIN
    UPDATE LichPhanCa
    SET trangThai = @TrangThai
    WHERE maLich = @MaLich;
END
GO

-- =====================================
-- CHẤM CÔNG
-- =====================================
CREATE PROCEDURE sp_CheckIn
    @MaCong VARCHAR(20),
    @MaLich VARCHAR(20)
AS
BEGIN
    INSERT INTO ChamCong (maCong, maLich, gioVao, trangThai)
    VALUES (@MaCong, @MaLich, GETDATE(), N'DungGio');
END
GO

CREATE PROCEDURE sp_CheckOut
    @MaLich VARCHAR(20)
AS
BEGIN
    UPDATE ChamCong
    SET gioRa = GETDATE()
    WHERE maLich = @MaLich AND gioRa IS NULL;
END
GO

CREATE PROCEDURE sp_IsCheckedIn
    @MaLich VARCHAR(20)
AS
BEGIN
    SELECT COUNT(*) AS soLuong
    FROM ChamCong
    WHERE maLich = @MaLich AND gioVao IS NOT NULL;
END
GO

CREATE PROCEDURE sp_GetLichSuChamCong
    @MaNV VARCHAR(20)
AS
BEGIN
    SELECT cc.*
    FROM ChamCong cc
    INNER JOIN LichPhanCa lpc ON cc.maLich = lpc.maLich
    WHERE lpc.maNV = @MaNV
    ORDER BY cc.gioVao DESC;
END
GO

-- =====================================
-- YÊU CẦU ĐỔI CA
-- =====================================
CREATE PROCEDURE sp_FindAllYeuCauDoiCa
AS
BEGIN
    SELECT * FROM YeuCauDoiCa ORDER BY ngayTao DESC;
END
GO

CREATE PROCEDURE sp_FindYeuCauDoiCaById
    @MaYeuCau VARCHAR(20)
AS
BEGIN
    SELECT * FROM YeuCauDoiCa WHERE maYeuCau = @MaYeuCau;
END
GO

CREATE PROCEDURE sp_InsertYeuCauDoiCa
    @MaYeuCau  VARCHAR(20),
    @MaLichGoc VARCHAR(20),
    @MaNVTarget VARCHAR(20),
    @LyDo      NVARCHAR(255),
    @TrangThai NVARCHAR(30) = N'ChoDuyet'
AS
BEGIN
    INSERT INTO YeuCauDoiCa (maYeuCau, maLichGoc, maNVTarget, lyDo, trangThai, ngayTao)
    VALUES (@MaYeuCau, @MaLichGoc, @MaNVTarget, @LyDo, @TrangThai, GETDATE());
END
GO

CREATE PROCEDURE sp_UpdateTrangThaiYeuCau
    @MaYeuCau  VARCHAR(20),
    @TrangThai NVARCHAR(30)
AS
BEGIN
    UPDATE YeuCauDoiCa
    SET trangThai = @TrangThai
    WHERE maYeuCau = @MaYeuCau;
END
GO

-- =====================================
-- LỊCH PHÂN CA: Sửa & Xóa (Quản lý)
-- =====================================
CREATE PROCEDURE sp_UpdateLichPhanCaFull
    @MaLich      VARCHAR(20),
    @MaNV        VARCHAR(20),
    @MaCa        VARCHAR(20),
    @NgayLamViec DATE,
    @TrangThai   NVARCHAR(30)
AS
BEGIN
    UPDATE LichPhanCa
    SET maNV = @MaNV,
        maCa = @MaCa,
        ngayLamViec = @NgayLamViec,
        trangThai = @TrangThai
    WHERE maLich = @MaLich;
END
GO

CREATE PROCEDURE sp_DeleteLichPhanCa
    @MaLich VARCHAR(20)
AS
BEGIN
    DELETE FROM LichPhanCa WHERE maLich = @MaLich;
END
GO
/* =====================================================================
   FIX: Đồng bộ database với code Java (model YeuCauDoiCa có thêm maLichTarget)
   Chạy script này trên database hiện tại của bạn (KHÔNG cần xóa dữ liệu cũ)
   ===================================================================== */

USE HeThongQuanLyCaLamViec; 
GO

-- 1) Thêm cột maLichTarget vào bảng YeuCauDoiCa (nếu chưa có)
IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('YeuCauDoiCa') AND name = 'maLichTarget'
)
BEGIN
    ALTER TABLE YeuCauDoiCa
    ADD maLichTarget VARCHAR(20) NULL
        CONSTRAINT FK_YeuCauDoiCa_LichTarget FOREIGN KEY REFERENCES LichPhanCa(maLich);
END
GO

-- 2) Sửa lại sp_InsertYeuCauDoiCa để nhận thêm @MaLichTarget (6 tham số, đúng với DAO)
IF EXISTS (SELECT 1 FROM sys.procedures WHERE name = 'sp_InsertYeuCauDoiCa')
    DROP PROCEDURE sp_InsertYeuCauDoiCa;
GO

CREATE PROCEDURE sp_InsertYeuCauDoiCa
    @MaYeuCau     VARCHAR(20),
    @MaLichGoc    VARCHAR(20),
    @MaNVTarget   VARCHAR(20),
    @MaLichTarget VARCHAR(20) = NULL,
    @LyDo         NVARCHAR(255),
    @TrangThai    NVARCHAR(30) = N'ChoDuyet'
AS
BEGIN
    INSERT INTO YeuCauDoiCa (maYeuCau, maLichGoc, maNVTarget, maLichTarget, lyDo, trangThai, ngayTao)
    VALUES (@MaYeuCau, @MaLichGoc, @MaNVTarget, @MaLichTarget, @LyDo, @TrangThai, GETDATE());
END
GO

-- 3) sp_FindAllYeuCauDoiCa và sp_FindYeuCauDoiCaById dùng SELECT * nên không cần sửa,
--    cột mới sẽ tự động được trả về.

