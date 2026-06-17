USE HeThongQuanLyCaLamViec;
GO

--  1 – XÓA DỮ LIỆU CŨ
--  Thứ tự xóa NGƯỢC với thứ tự tạo bảng để tránh lỗi FK.
DELETE FROM YeuCauDoiCa;
DELETE FROM ChamCong;
DELETE FROM LichPhanCa;
DELETE FROM CaLamViec;
DELETE FROM NhanVien;
GO

--  2 – NHÂN VIÊN
INSERT INTO NhanVien (maNV, hoTen, vaiTro, taiKhoan, matKhau, trangThai) VALUES
('NV001', N'Nguyễn Văn An',   'admin',   'admin',   '123456', N'HoatDong'),
('NV002', N'Trần Minh Khang', 'manager', 'manager', '123456', N'HoatDong'),
('NV003', N'Lê Hoàng Nam',    'Staff',   'staff01', '123456', N'HoatDong'),
('NV004', N'Phạm Quốc Bảo',   'Staff',   'staff02', '123456', N'HoatDong'),
('NV005', N'Nguyễn Minh Tâm', 'Staff',   'staff03', '123456', N'HoatDong'),
('NV006', N'Đặng Quốc Huy',   'Staff',   'staff04', '123456', N'HoatDong'),
('NV007', N'Võ Thành Đạt',    'Staff',   'staff05', '123456', N'HoatDong'),
('NV008', N'Hoàng Gia Bảo',   'Staff',   'staff06', '123456', N'NgungHoatDong');
GO

--  3 – CA LÀM VIỆC
INSERT INTO CaLamViec (maCa, tenCa, gioBatDau, gioKetThuc) VALUES
('CA01', N'Ca sáng',  '07:00:00', '12:00:00'),
('CA02', N'Ca chiều', '13:00:00', '18:00:00'),
('CA03', N'Ca tối',   '18:00:00', '22:00:00');
GO

--  4 – LỊCH PHÂN CA
--
--  Chia làm 2 nhóm:
--
--  [A] TUẦN TRƯỚC (08–12/06/2026) – đã có chấm công
--      → Chỉ được SỬA, KHÔNG XÓA được (vi phạm FK ChamCong)
--      → Dùng để test màn hình báo cáo & xem lịch sử
--
--  [B] TUẦN NÀY  (15–19/06/2026) – chưa có chấm công
--      → Có thể SỬA và XÓA thoải mái
--      → Dùng để test tính năng xếp lịch của Quản lý
INSERT INTO LichPhanCa (maLich, maNV, maCa, ngayLamViec, trangThai) VALUES

-- [A] Thứ 2, 08/06 ------------------------------------------
('L001', 'NV003', 'CA01', '2026-06-08', N'DaPhan'),
('L002', 'NV004', 'CA02', '2026-06-08', N'DaPhan'),
('L003', 'NV005', 'CA03', '2026-06-08', N'DaPhan'),
('L004', 'NV006', 'CA01', '2026-06-08', N'DaPhan'),
('L005', 'NV007', 'CA02', '2026-06-08', N'DaPhan'),
('L006', 'NV008', 'CA03', '2026-06-08', N'DaHuy'),  -- NV008 bị khóa TK, lịch đã hủy

-- [A] Thứ 3, 09/06 ------------------------------------------
('L007', 'NV003', 'CA02', '2026-06-09', N'DaPhan'),
('L008', 'NV004', 'CA01', '2026-06-09', N'DaPhan'),
('L009', 'NV005', 'CA03', '2026-06-09', N'DaPhan'),
('L010', 'NV006', 'CA02', '2026-06-09', N'DaPhan'),
('L011', 'NV007', 'CA01', '2026-06-09', N'DaPhan'),
('L012', 'NV008', 'CA03', '2026-06-09', N'DaPhan'),

-- [A] Thứ 4, 10/06 ------------------------------------------
('L013', 'NV003', 'CA01', '2026-06-10', N'DaPhan'),
('L014', 'NV004', 'CA02', '2026-06-10', N'DaPhan'),
('L015', 'NV005', 'CA01', '2026-06-10', N'DaPhan'),
('L016', 'NV006', 'CA03', '2026-06-10', N'DaPhan'),

-- [A] Thứ 5, 11/06 ------------------------------------------
('L017', 'NV003', 'CA01', '2026-06-11', N'DaPhan'),
('L018', 'NV004', 'CA02', '2026-06-11', N'DaPhan'),
('L019', 'NV005', 'CA03', '2026-06-11', N'DaPhan'),
('L020', 'NV006', 'CA01', '2026-06-11', N'DaPhan'),
('L021', 'NV007', 'CA02', '2026-06-11', N'DaPhan'),
('L022', 'NV008', 'CA03', '2026-06-11', N'DaPhan'),

-- [A] Thứ 6, 12/06 ------------------------------------------
('L023', 'NV003', 'CA01', '2026-06-12', N'DaPhan'),
('L024', 'NV004', 'CA02', '2026-06-12', N'DaPhan'),
('L025', 'NV005', 'CA03', '2026-06-12', N'DaPhan'),
('L026', 'NV006', 'CA02', '2026-06-12', N'DaPhan'),
('L027', 'NV007', 'CA01', '2026-06-12', N'DaPhan'),

-- [B] Thứ 2, 15/06 ------------------------------------------
('L028', 'NV003', 'CA01', '2026-06-15', N'DaPhan'),
('L029', 'NV004', 'CA02', '2026-06-15', N'DaPhan'),
('L030', 'NV005', 'CA03', '2026-06-15', N'DaPhan'),

-- [B] Thứ 3, 16/06 ------------------------------------------
('L031', 'NV006', 'CA01', '2026-06-16', N'DaPhan'),
('L032', 'NV007', 'CA02', '2026-06-16', N'DaPhan'),
('L033', 'NV008', 'CA03', '2026-06-16', N'DaPhan'),

-- [B] Thứ 4, 17/06 ------------------------------------------
('L034', 'NV003', 'CA02', '2026-06-17', N'DaPhan'),
('L035', 'NV004', 'CA01', '2026-06-17', N'DaPhan'),

-- [B] Thứ 5, 18/06 ------------------------------------------
('L036', 'NV005', 'CA02', '2026-06-18', N'DaPhan'),
('L037', 'NV006', 'CA03', '2026-06-18', N'DaPhan'),

-- [B] Thứ 6, 19/06 ------------------------------------------
('L038', 'NV007', 'CA01', '2026-06-19', N'DaPhan'),
('L039', 'NV008', 'CA02', '2026-06-19', N'DaPhan');
GO

--  5 – CHẤM CÔNG
--
--  Chỉ insert cho các lịch TUẦN TRƯỚC (L001–L027).
--  trangThai được set thủ công cho phù hợp dữ liệu demo.
--  Trong thực tế, trangThai do sp_CheckIn / sp_CheckOut tính.
INSERT INTO ChamCong (maCong, maLich, gioVao, gioRa, trangThai, minhChung) VALUES
('CC001', 'L001', '2026-06-08 06:55:00', '2026-06-08 12:00:00', N'DungGio', NULL),
('CC002', 'L002', '2026-06-08 13:10:00', '2026-06-08 18:00:00', N'DiMuon',  NULL),
('CC003', 'L003', '2026-06-08 18:00:00', '2026-06-08 21:30:00', N'VeSom',   NULL),
('CC004', 'L004', '2026-06-08 07:06:00', '2026-06-08 12:00:00', N'DiMuon',  NULL),
('CC005', 'L007', '2026-06-09 13:00:00', '2026-06-09 18:00:00', N'DungGio', NULL),
('CC006', 'L008', '2026-06-09 06:58:00', '2026-06-09 12:00:00', N'DungGio', NULL),
('CC007', 'L013', '2026-06-10 07:02:00', '2026-06-10 12:00:00', N'DungGio', NULL),
('CC008', 'L017', '2026-06-11 07:00:00', '2026-06-11 12:00:00', N'DungGio', NULL),
('CC009', 'L018', '2026-06-11 13:05:00', '2026-06-11 18:00:00', N'DungGio', NULL),
('CC010', 'L023', '2026-06-12 06:50:00', NULL,                  N'DungGio', NULL);
GO

--  6 – YÊU CẦU ĐỔI CA
INSERT INTO YeuCauDoiCa (maYeuCau, maLichGoc, maNVTarget, maLichTarget, lyDo, trangThai, ngayTao) VALUES
('YC001', 'L020', 'NV004', NULL,   N'Bận việc gia đình', N'ChoDuyet',   GETDATE()),
('YC002', 'L011', 'NV003', 'L001', N'Đổi ca để đi học',  N'DaChapNhan', GETDATE()),
('YC003', 'L026', 'NV007', 'L027', N'Có lịch cá nhân',   N'ChoDuyet',   GETDATE());
GO

--  7 – KIỂM TRA NHANH
--  Bỏ comment từng lệnh SELECT để kiểm tra sau khi insert.

/*
-- Tổng số bản ghi từng bảng
SELECT 'NhanVien'    AS Bang, COUNT(*) AS SoBanGhi FROM NhanVien
UNION ALL
SELECT 'CaLamViec',           COUNT(*)             FROM CaLamViec
UNION ALL
SELECT 'LichPhanCa',          COUNT(*)             FROM LichPhanCa
UNION ALL
SELECT 'ChamCong',            COUNT(*)             FROM ChamCong
UNION ALL
SELECT 'YeuCauDoiCa',         COUNT(*)             FROM YeuCauDoiCa;

-- Kiểm tra đăng nhập (phải trả về 1 hàng)
EXEC sp_Login @TaiKhoan = 'staff01', @MatKhau = '123456';

-- Xem lịch phân ca của NV003 trong tháng 6
SELECT lpc.*, clv.tenCa, clv.gioBatDau, clv.gioKetThuc
FROM LichPhanCa lpc
INNER JOIN CaLamViec clv ON lpc.maCa = clv.maCa
WHERE lpc.maNV = 'NV003'
ORDER BY lpc.ngayLamViec;

-- Xem chấm công kèm trạng thái của NV003
SELECT cc.maCong, lpc.ngayLamViec, lpc.maCa,
       cc.gioVao, cc.gioRa, cc.trangThai
FROM ChamCong cc
INNER JOIN LichPhanCa lpc ON cc.maLich = lpc.maLich
WHERE lpc.maNV = 'NV003'
ORDER BY cc.gioVao DESC;

-- Xem yêu cầu đổi ca đang chờ duyệt
SELECT * FROM YeuCauDoiCa WHERE trangThai = N'ChoDuyet';

-- Test check-in thủ công (chạy trong giờ ca sáng 07:00–12:00 để thấy DungGio)
-- EXEC sp_CheckIn @MaCong = 'CC_TEST01', @MaLich = 'L031';
-- EXEC sp_GetChamCongByLich @MaLich = 'L031';

-- Test check-out (chạy sau check-in ở trên)
-- EXEC sp_CheckOut @MaLich = 'L031';
-- EXEC sp_GetChamCongByLich @MaLich = 'L031';
*/