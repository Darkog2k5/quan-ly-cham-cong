USE HeThongQuanLyCaLamViec;
GO

-- XÓA DỮ LIỆU CŨ (theo đúng thứ tự để tránh lỗi khóa ngoại)
DELETE FROM YeuCauDoiCa;
DELETE FROM ChamCong;
DELETE FROM LichPhanCa;
DELETE FROM CaLamViec;
DELETE FROM NhanVien;
GO

-- =====================================
-- NHÂN VIÊN
-- =====================================
INSERT INTO NhanVien
(maNV, hoTen, vaiTro, taiKhoan, matKhau, trangThai)
VALUES
('NV001', N'Nguyễn Văn An',    'admin',   'admin',   '123456', N'HoatDong'),
('NV002', N'Trần Minh Khang',  'manager', 'manager', '123456', N'HoatDong'),
('NV003', N'Lê Hoàng Nam',     'Staff',   'staff01', '123456', N'HoatDong'),
('NV004', N'Phạm Quốc Bảo',    'Staff',   'staff02', '123456', N'HoatDong'),
('NV005', N'Nguyễn Minh Tâm',  'Staff',   'staff03', '123456', N'HoatDong'),
('NV006', N'Đặng Quốc Huy',    'Staff',   'staff04', '123456', N'HoatDong'),
('NV007', N'Võ Thành Đạt',     'Staff',   'staff05', '123456', N'HoatDong'),
('NV008', N'Hoàng Gia Bảo',    'Staff',   'staff06', '123456', N'NgungHoatDong');
GO

-- =====================================
-- CA LÀM VIỆC
-- =====================================
INSERT INTO CaLamViec
(maCa, tenCa, gioBatDau, gioKetThuc)
VALUES
('CA01', N'Ca sáng',  '07:00:00', '12:00:00'),
('CA02', N'Ca chiều', '13:00:00', '18:00:00'),
('CA03', N'Ca tối',   '18:00:00', '22:00:00');
GO

-- =====================================
-- LỊCH PHÂN CA
-- Tuần trước (08/06 - 12/06/2026)  -> đã có chấm công / yêu cầu đổi ca
-- Tuần này   (15/06 - 19/06/2026)  -> chưa chấm công, có thể Sửa/Xóa thoải mái
-- =====================================
INSERT INTO LichPhanCa
(maLich, maNV, maCa, ngayLamViec, trangThai)
VALUES
-- ── Thứ 2, 08/06 ──────────────────────────────────────────────
('L001','NV003','CA01','2026-06-08',N'DaPhan'),
('L002','NV004','CA02','2026-06-08',N'DaPhan'),
('L003','NV005','CA03','2026-06-08',N'DaPhan'),
('L004','NV006','CA01','2026-06-08',N'DaPhan'),
('L005','NV007','CA02','2026-06-08',N'DaPhan'),
('L006','NV008','CA03','2026-06-08',N'DaHuy'),

-- ── Thứ 3, 09/06 ──────────────────────────────────────────────
('L007','NV003','CA02','2026-06-09',N'DaPhan'),
('L008','NV004','CA01','2026-06-09',N'DaPhan'),
('L009','NV005','CA03','2026-06-09',N'DaPhan'),
('L010','NV006','CA02','2026-06-09',N'DaPhan'),
('L011','NV007','CA01','2026-06-09',N'DaPhan'),
('L012','NV008','CA03','2026-06-09',N'DaPhan'),

-- ── Thứ 4, 10/06 ──────────────────────────────────────────────
('L013','NV003','CA01','2026-06-10',N'DaPhan'),
('L014','NV004','CA02','2026-06-10',N'DaPhan'),
('L015','NV005','CA01','2026-06-10',N'DaPhan'),
('L016','NV006','CA03','2026-06-10',N'DaPhan'),

-- ── Thứ 5, 11/06 ──────────────────────────────────────────────
('L017','NV003','CA01','2026-06-11',N'DaPhan'),
('L018','NV004','CA02','2026-06-11',N'DaPhan'),
('L019','NV005','CA03','2026-06-11',N'DaPhan'),
('L020','NV006','CA01','2026-06-11',N'DaPhan'),
('L021','NV007','CA02','2026-06-11',N'DaPhan'),
('L022','NV008','CA03','2026-06-11',N'DaPhan'),

-- ── Thứ 6, 12/06 (hôm nay) ────────────────────────────────────
('L023','NV003','CA01','2026-06-12',N'DaPhan'),
('L024','NV004','CA02','2026-06-12',N'DaPhan'),
('L025','NV005','CA03','2026-06-12',N'DaPhan'),
('L026','NV006','CA02','2026-06-12',N'DaPhan'),
('L027','NV007','CA01','2026-06-12',N'DaPhan'),

-- ── Tuần sau (15/06 - 19/06/2026) – chưa chấm công, chưa có yêu cầu đổi ca
--    => Quản lý có thể Sửa hoặc Xóa thoải mái các lịch này
('L028','NV003','CA01','2026-06-15',N'DaPhan'),
('L029','NV004','CA02','2026-06-15',N'DaPhan'),
('L030','NV005','CA03','2026-06-15',N'DaPhan'),
('L031','NV006','CA01','2026-06-16',N'DaPhan'),
('L032','NV007','CA02','2026-06-16',N'DaPhan'),
('L033','NV008','CA03','2026-06-16',N'DaPhan'),
('L034','NV003','CA02','2026-06-17',N'DaPhan'),
('L035','NV004','CA01','2026-06-17',N'DaPhan'),
('L036','NV005','CA02','2026-06-18',N'DaPhan'),
('L037','NV006','CA03','2026-06-18',N'DaPhan'),
('L038','NV007','CA01','2026-06-19',N'DaPhan'),
('L039','NV008','CA02','2026-06-19',N'DaPhan');
GO

-- =====================================
-- CHẤM CÔNG
-- Chỉ gắn cho các lịch ĐÃ QUA (08/06 - 12/06)
-- => các lịch L001-L027 sẽ KHÔNG xóa được (vướng FK), chỉ Sửa được
-- =====================================
INSERT INTO ChamCong
(maCong, maLich, gioVao, gioRa, trangThai, minhChung)
VALUES
('CC001','L001','2026-06-08 06:55:00','2026-06-08 12:00:00',N'DungGio',NULL),
('CC002','L002','2026-06-08 13:10:00','2026-06-08 18:00:00',N'DiMuon',NULL),
('CC003','L003','2026-06-08 18:00:00','2026-06-08 22:05:00',N'VeSom',NULL),
('CC004','L004','2026-06-08 07:05:00','2026-06-08 12:00:00',N'DiMuon',NULL),
('CC005','L007','2026-06-09 13:00:00','2026-06-09 18:00:00',N'DungGio',NULL),
('CC006','L008','2026-06-09 06:58:00','2026-06-09 12:00:00',N'DungGio',NULL),
('CC007','L013','2026-06-10 07:02:00','2026-06-10 12:00:00',N'DiMuon',NULL),
('CC008','L017','2026-06-11 06:55:00','2026-06-11 12:00:00',N'DungGio',NULL),
('CC009','L018','2026-06-11 13:05:00','2026-06-11 18:00:00',N'DiMuon',NULL),
('CC010','L023','2026-06-12 06:50:00',NULL,N'DungGio',NULL);
GO

-- =====================================
-- YÊU CẦU ĐỔI CA
-- =====================================
INSERT INTO YeuCauDoiCa
(maYeuCau, maLichGoc, maNVTarget, lyDo, trangThai, ngayTao)
VALUES
('YC001','L020','NV004',N'Bận việc gia đình',N'ChoDuyet',GETDATE()),
('YC002','L011','NV003',N'Đổi ca để đi học',N'DaChapNhan',GETDATE()),
('YC003','L026','NV007',N'Có lịch cá nhân',N'ChoDuyet',GETDATE());
GO