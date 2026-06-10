package vn.edu.nhom8.service;

import vn.edu.nhom8.dao.ILichPhanCaDAO;
import vn.edu.nhom8.dao.INhanVienDAO;
import vn.edu.nhom8.dao.IYeuCauDoiCaDAO;
import vn.edu.nhom8.model.LichPhanCa;
import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.model.YeuCauDoiCa;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Business logic cho màn hình Quản lý (F3.1 – F3.3).
 * UI gọi class này, không gọi DAO trực tiếp.
 */
public class ManagerService {

    private final ILichPhanCaDAO  lichDAO;
    private final INhanVienDAO    nvDAO;
    private final IYeuCauDoiCaDAO ycDAO;

    public ManagerService(ILichPhanCaDAO lichDAO,
                          INhanVienDAO nvDAO,
                          IYeuCauDoiCaDAO ycDAO) {
        this.lichDAO = lichDAO;
        this.nvDAO   = nvDAO;
        this.ycDAO   = ycDAO;
    }

    // ── F3.1: Xếp lịch ────────────────────────────────────────────────────

    /** Lấy toàn bộ nhân viên (hiển thị trong danh sách checkbox). */
    public List<NhanVien> getDanhSachNV() {
        return nvDAO.findAll();
    }

    /**
     * Xếp ca cho nhiều NV vào một ngày.
     * Trả về số NV xếp thành công.
     * Trả -1 nếu có NV bị trùng ca (và builtWarning sẽ chứa tên họ).
     */
    public int xepLich(List<String> dsMaNV, String maCa, java.util.Date ngay,
                       List<String> outTrungCa) {

        Date sqlDate = new Date(ngay.getTime());
        int soLuuOK  = 0;

        for (String maNV : dsMaNV) {
            // Kiểm tra trùng ca: xem NV đã có lịch ngày đó chưa
            boolean trung = false;
            for (LichPhanCa l : lichDAO.findAll()) {
                if (maNV.equals(l.getMaNV())
                        && sqlDate.toString().equals(l.getNgayLamViec().toString())
                        && "DaPhan".equals(l.getTrangThai())) {
                    trung = true;
                    break;
                }
            }

            if (trung) {
                NhanVien nv = nvDAO.findById(maNV);
                outTrungCa.add(nv != null ? nv.getHoTen() : maNV);
                continue; // bỏ qua NV này
            }

            // Tạo mã lịch và lưu
            String maLich = "L" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date())
                          + maNV.replaceAll("[^0-9]", "");
            if (maLich.length() > 20) maLich = maLich.substring(0, 20);

            LichPhanCa lich = new LichPhanCa(maLich, maNV, maCa, sqlDate, "DaPhan");
            if (lichDAO.insert(lich)) soLuuOK++;
        }

        return soLuuOK;
    }

    /** Lịch đã xếp theo tháng (dùng cho bảng bên phải tab Xếp lịch). */
    public List<LichPhanCa> getLichTheoThang(int nam, int thang) {
        List<LichPhanCa> ds = lichDAO.findAll();
        ds.removeIf(l -> {
            if (!"DaPhan".equals(l.getTrangThai())) return true;
            Calendar c = Calendar.getInstance();
            c.setTime(l.getNgayLamViec());
            return c.get(Calendar.YEAR) != nam || c.get(Calendar.MONTH) + 1 != thang;
        });
        return ds;
    }

    // ── F3.2 / F3.3: Duyệt đổi ca ─────────────────────────────────────────

    /** Lấy danh sách yêu cầu đang chờ duyệt. */
    public List<YeuCauDoiCa> getYeuCauChoDuyet() {
        List<YeuCauDoiCa> ds = ycDAO.findAll();
        ds.removeIf(yc -> !"ChoDuyet".equals(yc.getTrangThai()));
        return ds;
    }

    /**
     * Duyệt một yêu cầu đổi ca.
     * Khi duyệt: cập nhật trạng thái YC → 'DaDuyet',
     * và đổi MaNV trong LichPhanCa sang NV nhận (nếu có).
     * Trả null nếu thành công, trả message lỗi nếu thất bại.
     */
    public String duyetYeuCau(YeuCauDoiCa yc) {
        yc.setTrangThai("DaDuyet");
        boolean ok = ycDAO.update(yc);
        if (!ok) return "Duyệt thất bại. Vui lòng thử lại.";

        // Nếu có NV nhận → cập nhật lịch
        if (yc.getMaNVTarget() != null && !yc.getMaNVTarget().isEmpty()) {
            LichPhanCa lich = lichDAO.findById(yc.getMaLichGoc());
            if (lich != null) {
                lich.setMaNV(yc.getMaNVTarget());
                lichDAO.update(lich);
            }
        }
        return null; // thành công
    }

    /**
     * Từ chối một yêu cầu đổi ca.
     * Trả null nếu thành công, trả message lỗi nếu thất bại.
     */
    public String tuChoiYeuCau(YeuCauDoiCa yc) {
        yc.setTrangThai("TuChoi");
        boolean ok = ycDAO.update(yc);
        if (ok) return null;
        return "Từ chối thất bại. Vui lòng thử lại.";
    }

    // ── Lịch tổng ────────────────────────────────────────────────────────

    /** Lấy toàn bộ lịch trong 1 tuần (dùng cho tab Xem lịch tổng). */
    public List<LichPhanCa> getLichTrongTuan(Date tuNgay, Date denNgay) {
        List<LichPhanCa> ds = lichDAO.findAll();
        ds.removeIf(l ->
            l.getNgayLamViec().before(tuNgay) || l.getNgayLamViec().after(denNgay)
        );
        return ds;
    }
}