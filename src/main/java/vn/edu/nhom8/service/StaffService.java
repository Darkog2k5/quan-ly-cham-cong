package vn.edu.nhom8.service;

import vn.edu.nhom8.dao.ChamCongDAO;
import vn.edu.nhom8.dao.ILichPhanCaDAO;
import vn.edu.nhom8.dao.IYeuCauDoiCaDAO;
import vn.edu.nhom8.model.ChamCong;
import vn.edu.nhom8.model.LichPhanCa;
import vn.edu.nhom8.model.YeuCauDoiCa;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Business logic cho màn hình Nhân viên (F4).
 * UI gọi class này, không gọi DAO trực tiếp.
 */
public class StaffService {

    private final ChamCongDAO     chamCongDAO;
    private final ILichPhanCaDAO  lichDAO;
    private final IYeuCauDoiCaDAO ycDAO;

    public StaffService(ChamCongDAO chamCongDAO,
                        ILichPhanCaDAO lichDAO,
                        IYeuCauDoiCaDAO ycDAO) {
        this.chamCongDAO = chamCongDAO;
        this.lichDAO     = lichDAO;
        this.ycDAO       = ycDAO;
    }

    // ── Ca hôm nay ────────────────────────────────────────────────────────

    /** Lấy lịch ca hôm nay của NV. Trả null nếu không có ca. */
    public LichPhanCa getCaHomNay(String maNV) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        for (LichPhanCa l : lichDAO.findAll()) {
            if (maNV.equals(l.getMaNV())
                    && "DaPhan".equals(l.getTrangThai())
                    && today.equals(l.getNgayLamViec().toString())) {
                return l;
            }
        }
        return null;
    }

    /** Lấy lịch ca theo tháng của NV. */
    public List<LichPhanCa> getLichTheoThang(String maNV, int nam, int thang) {
        List<LichPhanCa> ds = lichDAO.findAll();
        ds.removeIf(l -> {
            if (!maNV.equals(l.getMaNV()) || !"DaPhan".equals(l.getTrangThai())) return true;
            Calendar c = Calendar.getInstance();
            c.setTime(l.getNgayLamViec());
            return c.get(Calendar.YEAR) != nam || c.get(Calendar.MONTH) + 1 != thang;
        });
        return ds;
    }

    /** Lấy danh sách ca từ hôm nay trở đi (dùng cho combobox đổi ca). */
    public List<LichPhanCa> getCaTuongLai(String maNV) {
        Date homNay = new Date(System.currentTimeMillis());
        List<LichPhanCa> ds = lichDAO.findAll();
        ds.removeIf(l ->
            !maNV.equals(l.getMaNV())
            || !"DaPhan".equals(l.getTrangThai())
            || l.getNgayLamViec().before(homNay)
        );
        return ds;
    }

    // ── Chấm công ─────────────────────────────────────────────────────────

    /** Check-in cho maLich đã cho. Trả message để hiện lên UI. */
    public String checkIn(String maNV, String maLich) {
        if (maLich == null) return "Bạn không có ca làm việc hôm nay.";
        if (chamCongDAO.isCheckedIn(maNV, maLich)) return "Bạn đã check-in rồi!";

        boolean ok = chamCongDAO.checkIn(maNV, maLich);
        if (ok) return null; // null = thành công
        return "Check-in thất bại. Vui lòng thử lại.";
    }

    /** Check-out. Trả null nếu thành công, trả message lỗi nếu thất bại. */
    public String checkOut(String maNV, String maLich) {
        if (maLich == null) return "Không tìm thấy ca làm việc.";
        if (!chamCongDAO.isCheckedIn(maNV, maLich)) return "Bạn chưa check-in!";

        boolean ok = chamCongDAO.checkOut(maNV, maLich);
        if (ok) return null;
        return "Check-out thất bại. Vui lòng thử lại.";
    }

    /** Đã check-in chưa? */
    public boolean isCheckedIn(String maNV, String maLich) {
        return chamCongDAO.isCheckedIn(maNV, maLich);
    }

    /** Lịch sử chấm công của NV. */
    public List<ChamCong> getLichSuChamCong(String maNV) {
        return chamCongDAO.getLichSuChamCong(maNV);
    }

    // ── Yêu cầu đổi ca ────────────────────────────────────────────────────

    /**
     * Gửi yêu cầu đổi ca.
     * Trả null nếu thành công, trả message lỗi nếu không hợp lệ.
     */
    public String guiYeuCauDoiCa(String maNV, String maLich, String maNVTarget, String lyDo) {
        if (lyDo == null || lyDo.trim().isEmpty()) return "Vui lòng nhập lý do.";
        if (maLich == null) return "Vui lòng chọn ca cần đổi.";

        // Phải là ca tương lai
        LichPhanCa lich = lichDAO.findById(maLich);
        if (lich == null) return "Không tìm thấy lịch ca.";
        if (lich.getNgayLamViec().before(new Date(System.currentTimeMillis())))
            return "Chỉ được đổi ca trong tương lai.";

        // Không được gửi 2 yêu cầu cho cùng 1 ca
        for (YeuCauDoiCa yc : ycDAO.findAll()) {
            if (maLich.equals(yc.getMaLichGoc()) && "ChoDuyet".equals(yc.getTrangThai()))
                return "Ca này đã có yêu cầu đổi đang chờ duyệt.";
        }

        // Tạo và lưu yêu cầu
        String maYC = "YC" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        YeuCauDoiCa yc = new YeuCauDoiCa(
                maYC, maLich,
                (maNVTarget == null || maNVTarget.trim().isEmpty()) ? null : maNVTarget.trim(),
                lyDo.trim(),
                "ChoDuyet",
                new Timestamp(System.currentTimeMillis())
        );

        boolean ok = ycDAO.insert(yc);
        if (ok) return null;
        return "Gửi yêu cầu thất bại. Vui lòng thử lại.";
    }

    /** Lịch sử yêu cầu đổi ca của NV. */
    public List<YeuCauDoiCa> getYeuCauCuaNV(String maNV) {
        List<YeuCauDoiCa> ds = ycDAO.findAll();
        ds.removeIf(yc -> {
            LichPhanCa l = lichDAO.findById(yc.getMaLichGoc());
            return l == null || !maNV.equals(l.getMaNV());
        });
        return ds;
    }
}