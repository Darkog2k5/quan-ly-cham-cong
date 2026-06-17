--  0 - TẠO & CHỌN DATABASE

IF NOT EXISTS (
    SELECT 1 FROM sys.databases WHERE name = N'HeThongQuanLyCaLamViec'
)
    CREATE DATABASE HeThongQuanLyCaLamViec;
GO

USE HeThongQuanLyCaLamViec;
GO

--  1 - TẠO CÁC BẢNG
--  Thứ tự tạo bảng: NhanVien → CaLamViec → LichPhanCa → ChamCong → YeuCauDoiCa

-- 1.1  NHÂN VIÊN
--      Lưu thông tin tài khoản & vai trò của từng nhân viên.
--      vaiTro   : 'admin' | 'manager' | 'Staff'
--      trangThai: 'HoatDong' | 'NgungHoatDong'
IF OBJECT_ID(N'NhanVien', N'U') IS NULL
CREATE TABLE NhanVien (
    maNV      VARCHAR(20)   NOT NULL  CONSTRAINT PK_NhanVien PRIMARY KEY,
    hoTen     NVARCHAR(100) NOT NULL,
    vaiTro    NVARCHAR(50)  NOT NULL,
    taiKhoan  VARCHAR(50)   NOT NULL  CONSTRAINT UQ_NhanVien_TaiKhoan UNIQUE,
    matKhau   VARCHAR(255)  NOT NULL,
    trangThai NVARCHAR(30)  NOT NULL  CONSTRAINT DF_NhanVien_TrangThai DEFAULT N'HoatDong'
);
GO

-- 1.2  CA LÀM VIỆC
--      Định nghĩa các loại ca (sáng/chiều/tối) và khung giờ.
IF OBJECT_ID(N'CaLamViec', N'U') IS NULL
CREATE TABLE CaLamViec (
    maCa       VARCHAR(20)  NOT NULL  CONSTRAINT PK_CaLamViec PRIMARY KEY,
    tenCa      NVARCHAR(50) NOT NULL,
    gioBatDau  TIME         NOT NULL,
    gioKetThuc TIME         NOT NULL
);
GO

-- 1.3  LỊCH PHÂN CA
--      Ghi nhận NV nào được xếp vào ca nào, ngày nào.
--      trangThai: 'DaPhan' | 'DaHuy'
IF OBJECT_ID(N'LichPhanCa', N'U') IS NULL
CREATE TABLE LichPhanCa (
    maLich      VARCHAR(20)  NOT NULL  CONSTRAINT PK_LichPhanCa PRIMARY KEY,
    maNV        VARCHAR(20)  NOT NULL
        CONSTRAINT FK_LichPhanCa_NhanVien  FOREIGN KEY REFERENCES NhanVien(maNV),
    maCa        VARCHAR(20)  NOT NULL
        CONSTRAINT FK_LichPhanCa_CaLamViec FOREIGN KEY REFERENCES CaLamViec(maCa),
    ngayLamViec DATE         NOT NULL,
    trangThai   NVARCHAR(30) NOT NULL  CONSTRAINT DF_LichPhanCa_TrangThai DEFAULT N'DaPhan'
);
GO

-- 1.4  CHẤM CÔNG
--      Ghi nhận giờ vào / giờ ra thực tế của NV.
--      trangThai: 'DungGio' | 'DiMuon' | 'VeSom'
--                 Được tính tự động bởi sp_CheckIn / sp_CheckOut.
--      minhChung: đường dẫn ảnh hoặc ghi chú (để dành mở rộng)
IF OBJECT_ID(N'ChamCong', N'U') IS NULL
CREATE TABLE ChamCong (
    maCong    VARCHAR(20)   NOT NULL  CONSTRAINT PK_ChamCong PRIMARY KEY,
    maLich    VARCHAR(20)   NOT NULL
        CONSTRAINT FK_ChamCong_LichPhanCa FOREIGN KEY REFERENCES LichPhanCa(maLich),
    gioVao    DATETIME      NULL,     -- NULL trước khi check-in
    gioRa     DATETIME      NULL,     -- NULL trước khi check-out
    trangThai NVARCHAR(50)  NULL,
    minhChung NVARCHAR(255) NULL
);
GO

-- 1.5  YÊU CẦU ĐỔI CA
--      maLichGoc   : ca của người GỬI yêu cầu
--      maNVTarget  : NV được nhờ / đổi cùng (có thể NULL)
--      maLichTarget: ca của NV target muốn đổi lấy
--                    NULL  → "nhờ làm giúp" (chuyển hẳn ca cho NV target)
--                    khác  → "đổi ca cho nhau" (hoán đổi 2 lịch)
--      trangThai: 'ChoDuyet' | 'DaChapNhan' | 'TuChoi'
IF OBJECT_ID(N'YeuCauDoiCa', N'U') IS NULL
CREATE TABLE YeuCauDoiCa (
    maYeuCau     VARCHAR(20)   NOT NULL  CONSTRAINT PK_YeuCauDoiCa PRIMARY KEY,
    maLichGoc    VARCHAR(20)   NOT NULL
        CONSTRAINT FK_YeuCauDoiCa_LichGoc    FOREIGN KEY REFERENCES LichPhanCa(maLich),
    maNVTarget   VARCHAR(20)   NULL
        CONSTRAINT FK_YeuCauDoiCa_NVTarget   FOREIGN KEY REFERENCES NhanVien(maNV),
    maLichTarget VARCHAR(20)   NULL
        CONSTRAINT FK_YeuCauDoiCa_LichTarget FOREIGN KEY REFERENCES LichPhanCa(maLich),
    lyDo         NVARCHAR(255) NULL,
    trangThai    NVARCHAR(30)  NOT NULL  CONSTRAINT DF_YeuCauDoiCa_TrangThai DEFAULT N'ChoDuyet',
    ngayTao      DATETIME      NOT NULL  CONSTRAINT DF_YeuCauDoiCa_NgayTao   DEFAULT GETDATE()
);
GO


--  2 – STORED PROCEDURES: NHÂN VIÊN

-- 2.1  Đăng nhập
--      Trả về 1 hàng nếu tài khoản + mật khẩu đúng và đang HoatDong.
IF OBJECT_ID(N'sp_Login', N'P') IS NOT NULL DROP PROCEDURE sp_Login;
GO
CREATE PROCEDURE sp_Login
    @TaiKhoan VARCHAR(50),
    @MatKhau  VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM   NhanVien
    WHERE  taiKhoan  = @TaiKhoan
      AND  matKhau   = @MatKhau
      AND  trangThai = N'HoatDong';
END
GO

-- 2.2  Tìm nhân viên theo mã
IF OBJECT_ID(N'sp_FindNhanVienById', N'P') IS NOT NULL DROP PROCEDURE sp_FindNhanVienById;
GO
CREATE PROCEDURE sp_FindNhanVienById
    @MaNV VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM NhanVien WHERE maNV = @MaNV;
END
GO

-- 2.3  Lấy toàn bộ nhân viên
IF OBJECT_ID(N'sp_FindAllNhanVien', N'P') IS NOT NULL DROP PROCEDURE sp_FindAllNhanVien;
GO
CREATE PROCEDURE sp_FindAllNhanVien
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM NhanVien ORDER BY maNV;
END
GO

-- 2.4  Thêm nhân viên mới
IF OBJECT_ID(N'sp_InsertNhanVien', N'P') IS NOT NULL DROP PROCEDURE sp_InsertNhanVien;
GO
CREATE PROCEDURE sp_InsertNhanVien
    @MaNV      VARCHAR(20),
    @HoTen     NVARCHAR(100),
    @VaiTro    NVARCHAR(50),
    @TaiKhoan  VARCHAR(50),
    @MatKhau   VARCHAR(255),
    @TrangThai NVARCHAR(30) = N'HoatDong'
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO NhanVien (maNV, hoTen, vaiTro, taiKhoan, matKhau, trangThai)
    VALUES (@MaNV, @HoTen, @VaiTro, @TaiKhoan, @MatKhau, @TrangThai);
END
GO

-- 2.5  Cập nhật thông tin nhân viên (không đổi trangThai)
IF OBJECT_ID(N'sp_UpdateNhanVien', N'P') IS NOT NULL DROP PROCEDURE sp_UpdateNhanVien;
GO
CREATE PROCEDURE sp_UpdateNhanVien
    @MaNV     VARCHAR(20),
    @HoTen    NVARCHAR(100),
    @VaiTro   NVARCHAR(50),
    @TaiKhoan VARCHAR(50),
    @MatKhau  VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE NhanVien
    SET hoTen    = @HoTen,
        vaiTro   = @VaiTro,
        taiKhoan = @TaiKhoan,
        matKhau  = @MatKhau
    WHERE maNV = @MaNV;
END
GO

-- 2.6  Khóa tài khoản (xóa mềm – chỉ đổi trạng thái)
IF OBJECT_ID(N'sp_DeactivateNhanVien', N'P') IS NOT NULL DROP PROCEDURE sp_DeactivateNhanVien;
GO
CREATE PROCEDURE sp_DeactivateNhanVien
    @MaNV VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE NhanVien SET trangThai = N'NgungHoatDong' WHERE maNV = @MaNV;
END
GO


--  PHẦN 3 – STORED PROCEDURES: CA LÀM VIỆC

-- 3.1  Lấy toàn bộ ca làm việc
IF OBJECT_ID(N'sp_FindAllCaLamViec', N'P') IS NOT NULL DROP PROCEDURE sp_FindAllCaLamViec;
GO
CREATE PROCEDURE sp_FindAllCaLamViec
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM CaLamViec ORDER BY gioBatDau;
END
GO

-- 3.2  Tìm ca theo mã
IF OBJECT_ID(N'sp_FindCaLamViecById', N'P') IS NOT NULL DROP PROCEDURE sp_FindCaLamViecById;
GO
CREATE PROCEDURE sp_FindCaLamViecById
    @MaCa VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM CaLamViec WHERE maCa = @MaCa;
END
GO

-- 3.3  Thêm ca làm việc
IF OBJECT_ID(N'sp_InsertCaLamViec', N'P') IS NOT NULL DROP PROCEDURE sp_InsertCaLamViec;
GO
CREATE PROCEDURE sp_InsertCaLamViec
    @MaCa       VARCHAR(20),
    @TenCa      NVARCHAR(50),
    @GioBatDau  TIME,
    @GioKetThuc TIME
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO CaLamViec (maCa, tenCa, gioBatDau, gioKetThuc)
    VALUES (@MaCa, @TenCa, @GioBatDau, @GioKetThuc);
END
GO

-- 3.4  Cập nhật ca làm việc
IF OBJECT_ID(N'sp_UpdateCaLamViec', N'P') IS NOT NULL DROP PROCEDURE sp_UpdateCaLamViec;
GO
CREATE PROCEDURE sp_UpdateCaLamViec
    @MaCa       VARCHAR(20),
    @TenCa      NVARCHAR(50),
    @GioBatDau  TIME,
    @GioKetThuc TIME
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE CaLamViec
    SET tenCa      = @TenCa,
        gioBatDau  = @GioBatDau,
        gioKetThuc = @GioKetThuc
    WHERE maCa = @MaCa;
END
GO

-- 3.5  Xóa ca làm việc
--      Sẽ thất bại nếu ca đang được dùng trong LichPhanCa.
--      Java bắt SQLException và thông báo cho user.
IF OBJECT_ID(N'sp_DeleteCaLamViec', N'P') IS NOT NULL DROP PROCEDURE sp_DeleteCaLamViec;
GO
CREATE PROCEDURE sp_DeleteCaLamViec
    @MaCa VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM CaLamViec WHERE maCa = @MaCa;
END
GO


--  PHẦN 4 – STORED PROCEDURES: LỊCH PHÂN CA

-- 4.1  Lấy toàn bộ lịch phân ca
IF OBJECT_ID(N'sp_FindAllLichPhanCa', N'P') IS NOT NULL DROP PROCEDURE sp_FindAllLichPhanCa;
GO
CREATE PROCEDURE sp_FindAllLichPhanCa
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM LichPhanCa ORDER BY ngayLamViec, maNV;
END
GO

-- 4.2  Tìm lịch phân ca theo mã
IF OBJECT_ID(N'sp_FindLichPhanCaById', N'P') IS NOT NULL DROP PROCEDURE sp_FindLichPhanCaById;
GO
CREATE PROCEDURE sp_FindLichPhanCaById
    @MaLich VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM LichPhanCa WHERE maLich = @MaLich;
END
GO

-- 4.3  Thêm lịch phân ca mới
IF OBJECT_ID(N'sp_InsertLichPhanCa', N'P') IS NOT NULL DROP PROCEDURE sp_InsertLichPhanCa;
GO
CREATE PROCEDURE sp_InsertLichPhanCa
    @MaLich      VARCHAR(20),
    @MaNV        VARCHAR(20),
    @MaCa        VARCHAR(20),
    @NgayLamViec DATE,
    @TrangThai   NVARCHAR(30) = N'DaPhan'
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO LichPhanCa (maLich, maNV, maCa, ngayLamViec, trangThai)
    VALUES (@MaLich, @MaNV, @MaCa, @NgayLamViec, @TrangThai);
END
GO

-- 4.4  Cập nhật trạng thái lịch (dùng khi hủy ca)
IF OBJECT_ID(N'sp_UpdateLichPhanCa', N'P') IS NOT NULL DROP PROCEDURE sp_UpdateLichPhanCa;
GO
CREATE PROCEDURE sp_UpdateLichPhanCa
    @MaLich    VARCHAR(20),
    @TrangThai NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE LichPhanCa SET trangThai = @TrangThai WHERE maLich = @MaLich;
END
GO

-- 4.5  Cập nhật đầy đủ lịch phân ca (NV, ca, ngày, trạng thái)
--      Dùng khi Quản lý sửa lịch, hoặc khi duyệt đổi ca
--      (hoán đổi maNV giữa 2 lịch).
IF OBJECT_ID(N'sp_UpdateLichPhanCaFull', N'P') IS NOT NULL DROP PROCEDURE sp_UpdateLichPhanCaFull;
GO
CREATE PROCEDURE sp_UpdateLichPhanCaFull
    @MaLich      VARCHAR(20),
    @MaNV        VARCHAR(20),
    @MaCa        VARCHAR(20),
    @NgayLamViec DATE,
    @TrangThai   NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE LichPhanCa
    SET maNV        = @MaNV,
        maCa        = @MaCa,
        ngayLamViec = @NgayLamViec,
        trangThai   = @TrangThai
    WHERE maLich = @MaLich;
END
GO

-- 4.6  Xóa lịch phân ca
--      Chỉ xóa được nếu chưa có ChamCong / YeuCauDoiCa liên kết.
--      Java bắt lỗi FK violation và hiển thị thông báo phù hợp.
IF OBJECT_ID(N'sp_DeleteLichPhanCa', N'P') IS NOT NULL DROP PROCEDURE sp_DeleteLichPhanCa;
GO
CREATE PROCEDURE sp_DeleteLichPhanCa
    @MaLich VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM LichPhanCa WHERE maLich = @MaLich;
END
GO


--  PHẦN 5 – STORED PROCEDURES: CHẤM CÔNG
--
--  Quy tắc tính trạng thái:
--    Check-in : gioVao ≤ gioBatDau  + 5 phút → DungGio
--               gioVao >  gioBatDau  + 5 phút → DiMuon
--    Check-out: gioRa  <  gioKetThuc - 5 phút → VeSom
--               gioRa  ≥  gioKetThuc - 5 phút → giữ nguyên (DungGio / DiMuon)

-- 5.1  Check-in
--      Tự động tính DungGio / DiMuon dựa trên gioBatDau của ca.
--      Guard: nếu maLich đã có gioVao thì bỏ qua, không insert thêm.
IF OBJECT_ID(N'sp_CheckIn', N'P') IS NOT NULL DROP PROCEDURE sp_CheckIn;
GO
CREATE PROCEDURE sp_CheckIn
    @MaCong VARCHAR(20),
    @MaLich VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    -- Guard: không cho check-in 2 lần
    IF EXISTS (SELECT 1 FROM ChamCong WHERE maLich = @MaLich AND gioVao IS NOT NULL)
        RETURN;

    DECLARE @GioVao    DATETIME    = GETDATE();
    DECLARE @TrangThai NVARCHAR(50);

    -- Lấy gioBatDau của ca tương ứng với lịch này
    DECLARE @GioBatDau TIME;
    SELECT @GioBatDau = clv.gioBatDau
    FROM   LichPhanCa lpc
    INNER JOIN CaLamViec clv ON lpc.maCa = clv.maCa
    WHERE  lpc.maLich = @MaLich;

    IF @GioBatDau IS NULL
    BEGIN
        -- Không tìm thấy ca → vẫn cho check-in, mặc định DungGio
        SET @TrangThai = N'DungGio';
    END
    ELSE
    BEGIN
        -- Ghép ngày hôm nay với giờ bắt đầu ca để so sánh
        DECLARE @GioBatDauHom DATETIME;
        SET @GioBatDauHom = CAST(CAST(@GioVao AS DATE) AS DATETIME)
                          + CAST(@GioBatDau AS DATETIME);

        IF @GioVao <= DATEADD(MINUTE, 5, @GioBatDauHom)
            SET @TrangThai = N'DungGio';
        ELSE
            SET @TrangThai = N'DiMuon';
    END

    INSERT INTO ChamCong (maCong, maLich, gioVao, trangThai)
    VALUES (@MaCong, @MaLich, @GioVao, @TrangThai);
END
GO

-- 5.2  Check-out
--      Tự động tính VeSom hoặc giữ nguyên trangThai check-in.
--      Xử lý ca qua đêm: nếu gioKetThuc < gioBatDau thì kết
--      thúc rơi vào ngày hôm sau.
IF OBJECT_ID(N'sp_CheckOut', N'P') IS NOT NULL DROP PROCEDURE sp_CheckOut;
GO
CREATE PROCEDURE sp_CheckOut
    @MaLich VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @GioRa DATETIME = GETDATE();

    -- Lấy thông tin cần thiết để tính trạng thái
    DECLARE @GioKetThuc       TIME;
    DECLARE @TrangThaiHienTai NVARCHAR(50);
    DECLARE @GioVao           DATETIME;

    SELECT
        @GioKetThuc       = clv.gioKetThuc,
        @TrangThaiHienTai = cc.trangThai,
        @GioVao           = cc.gioVao
    FROM   ChamCong   cc
    INNER JOIN LichPhanCa lpc ON cc.maLich = lpc.maLich
    INNER JOIN CaLamViec  clv ON lpc.maCa  = clv.maCa
    WHERE  cc.maLich = @MaLich
      AND  cc.gioRa IS NULL;   -- chỉ lấy bản ghi chưa checkout

    -- Không tìm thấy bản ghi hợp lệ → không làm gì
    IF @GioKetThuc IS NULL OR @TrangThaiHienTai IS NULL
        RETURN;

    -- Ghép ngày của gioVao với gioKetThuc
    DECLARE @GioKetThucHom DATETIME;
    SET @GioKetThucHom = CAST(CAST(@GioVao AS DATE) AS DATETIME)
                       + CAST(@GioKetThuc AS DATETIME);

    -- Xử lý ca qua đêm: gioKetThuc rơi vào ngày hôm sau
    IF @GioKetThucHom < @GioVao
        SET @GioKetThucHom = DATEADD(DAY, 1, @GioKetThucHom);

    -- Về trước gioKetThuc - 5 phút → VeSom
    -- Ngược lại → giữ nguyên trangThai check-in (DungGio hoặc DiMuon)
    DECLARE @TrangThaiMoi NVARCHAR(50);
    IF @GioRa < DATEADD(MINUTE, -5, @GioKetThucHom)
        SET @TrangThaiMoi = N'VeSom';
    ELSE
        SET @TrangThaiMoi = @TrangThaiHienTai;

    UPDATE ChamCong
    SET gioRa     = @GioRa,
        trangThai = @TrangThaiMoi
    WHERE maLich = @MaLich
      AND gioRa IS NULL;
END
GO

-- 5.3  Kiểm tra đã check-in chưa
--      Trả về cột soLuong: 0 = chưa, ≥1 = đã check-in.
IF OBJECT_ID(N'sp_IsCheckedIn', N'P') IS NOT NULL DROP PROCEDURE sp_IsCheckedIn;
GO
CREATE PROCEDURE sp_IsCheckedIn
    @MaLich VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(*) AS soLuong
    FROM   ChamCong
    WHERE  maLich = @MaLich AND gioVao IS NOT NULL;
END
GO

-- 5.4  Lấy bản ghi chấm công mới nhất theo mã lịch
--      Dùng sau check-in / check-out để đọc trangThai thực tế
--      và hiển thị cảnh báo DungGio / DiMuon / VeSom lên UI.
IF OBJECT_ID(N'sp_GetChamCongByLich', N'P') IS NOT NULL DROP PROCEDURE sp_GetChamCongByLich;
GO
CREATE PROCEDURE sp_GetChamCongByLich
    @MaLich VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP 1 *
    FROM   ChamCong
    WHERE  maLich = @MaLich
    ORDER BY gioVao DESC;
END
GO

-- 5.5  Lịch sử chấm công của một nhân viên
--      Sắp xếp mới nhất trước để hiển thị trên bảng.
IF OBJECT_ID(N'sp_GetLichSuChamCong', N'P') IS NOT NULL DROP PROCEDURE sp_GetLichSuChamCong;
GO
CREATE PROCEDURE sp_GetLichSuChamCong
    @MaNV VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT cc.*
    FROM   ChamCong   cc
    INNER JOIN LichPhanCa lpc ON cc.maLich = lpc.maLich
    WHERE  lpc.maNV = @MaNV
    ORDER BY cc.gioVao DESC;
END
GO


--  PHẦN 6 – STORED PROCEDURES: YÊU CẦU ĐỔI CA

-- 6.1  Lấy toàn bộ yêu cầu đổi ca (mới nhất trước)
IF OBJECT_ID(N'sp_FindAllYeuCauDoiCa', N'P') IS NOT NULL DROP PROCEDURE sp_FindAllYeuCauDoiCa;
GO
CREATE PROCEDURE sp_FindAllYeuCauDoiCa
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM YeuCauDoiCa ORDER BY ngayTao DESC;
END
GO

-- 6.2  Tìm yêu cầu theo mã
IF OBJECT_ID(N'sp_FindYeuCauDoiCaById', N'P') IS NOT NULL DROP PROCEDURE sp_FindYeuCauDoiCaById;
GO
CREATE PROCEDURE sp_FindYeuCauDoiCaById
    @MaYeuCau VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM YeuCauDoiCa WHERE maYeuCau = @MaYeuCau;
END
GO

-- 6.3  Thêm yêu cầu đổi ca
--      @MaLichTarget = NULL  → nhờ làm giúp
--      @MaLichTarget ≠ NULL  → đổi ca cho nhau
IF OBJECT_ID(N'sp_InsertYeuCauDoiCa', N'P') IS NOT NULL DROP PROCEDURE sp_InsertYeuCauDoiCa;
GO
CREATE PROCEDURE sp_InsertYeuCauDoiCa
    @MaYeuCau     VARCHAR(20),
    @MaLichGoc    VARCHAR(20),
    @MaNVTarget   VARCHAR(20)  = NULL,
    @MaLichTarget VARCHAR(20)  = NULL,
    @LyDo         NVARCHAR(255),
    @TrangThai    NVARCHAR(30) = N'ChoDuyet'
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO YeuCauDoiCa
        (maYeuCau, maLichGoc, maNVTarget, maLichTarget, lyDo, trangThai, ngayTao)
    VALUES
        (@MaYeuCau, @MaLichGoc, @MaNVTarget, @MaLichTarget, @LyDo, @TrangThai, GETDATE());
END
GO

-- 6.4  Cập nhật trạng thái yêu cầu (duyệt / từ chối)
IF OBJECT_ID(N'sp_UpdateTrangThaiYeuCau', N'P') IS NOT NULL DROP PROCEDURE sp_UpdateTrangThaiYeuCau;
GO
CREATE PROCEDURE sp_UpdateTrangThaiYeuCau
    @MaYeuCau  VARCHAR(20),
    @TrangThai NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE YeuCauDoiCa SET trangThai = @TrangThai WHERE maYeuCau = @MaYeuCau;
END
GO

