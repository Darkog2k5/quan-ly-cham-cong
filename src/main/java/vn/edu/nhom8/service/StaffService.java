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

    /**
     * Lấy danh sách ca từ hôm nay trở đi của MỘT NV KHÁC (dùng để chọn ca muốn
     * đổi lấy của người được nhờ). Trả về list rỗng nếu maNV không hợp lệ
     * hoặc không có ca nào trong tương lai.
     */
    public List<LichPhanCa> getCaTuongLaiCuaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return new java.util.ArrayList<>();
        String target = maNV.trim();
        Date homNay = new Date(System.currentTimeMillis());
        List<LichPhanCa> ds = lichDAO.findAll();
        ds.removeIf(l ->
            !target.equals(l.getMaNV())
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
     *
     * @param maNV         NV gửi yêu cầu (NV1)
     * @param maLich       Ca của NV1 muốn đổi / nhờ làm giúp
     * @param maNVTarget   Mã NV được nhờ / đổi cùng (NV2). Có thể để trống.
     * @param maLichTarget Ca cụ thể của NV2 muốn đổi lấy.
     *                      - Để trống  -> TH "nhờ làm giúp": ca của NV1 sẽ chuyển hẳn cho NV2.
     *                      - Có giá trị -> TH "đổi ca cho nhau": hoán đổi ca của NV1 với ca này của NV2
     *                        (có thể khác ca, khác ngày so với ca gốc).
     * @param lyDo         Lý do đổi ca.
     * Trả null nếu thành công, trả message lỗi nếu không hợp lệ.
     */
    public String guiYeuCauDoiCa(String maNV, String maLich, String maNVTarget,
                                  String maLichTarget, String lyDo) {
        if (lyDo == null || lyDo.trim().isEmpty()) return "Vui lòng nhập lý do.";
        if (maLich == null) return "Vui lòng chọn ca cần đổi.";

        // Phải là ca tương lai
        LichPhanCa lich = lichDAO.findById(maLich);
        if (lich == null) return "Không tìm thấy lịch ca.";
        if (lich.getNgayLamViec().before(new Date(System.currentTimeMillis())))
            return "Chỉ được đổi ca trong tương lai.";

        String nvTarget = (maNVTarget == null || maNVTarget.trim().isEmpty()) ? null : maNVTarget.trim();
        String lichTarget = (maLichTarget == null || maLichTarget.trim().isEmpty()) ? null : maLichTarget.trim();

        if (nvTarget != null && nvTarget.equals(lich.getMaNV()))
            return "Không thể đổi ca với chính mình.";

        // Nếu chọn đổi lấy 1 ca cụ thể của NV target -> kiểm tra hợp lệ
        if (lichTarget != null) {
            if (nvTarget == null) return "Vui lòng nhập mã NV đổi cùng.";

            LichPhanCa lt = lichDAO.findById(lichTarget);
            if (lt == null) return "Không tìm thấy ca muốn đổi của NV đối phương.";
            if (!"DaPhan".equals(lt.getTrangThai())) return "Ca đối phương không còn hợp lệ để đổi.";
            if (!nvTarget.equals(lt.getMaNV())) return "Ca được chọn không thuộc nhân viên đã nhập.";
            if (lichTarget.equals(maLich)) return "Không thể đổi ca với chính ca này.";
            if (lt.getNgayLamViec().before(new Date(System.currentTimeMillis())))
                return "Ca muốn đổi của đối phương phải là ca trong tương lai.";

            // Ca đối phương không được đang có yêu cầu đổi khác chờ duyệt
            for (YeuCauDoiCa y : ycDAO.findAll()) {
                if (!"ChoDuyet".equals(y.getTrangThai())) continue;
                if (lichTarget.equals(y.getMaLichGoc()) || lichTarget.equals(y.getMaLichTarget()))
                    return "Ca đó của đối phương đã có yêu cầu đổi đang chờ duyệt.";
            }
        }

        // Không được gửi 2 yêu cầu cho cùng 1 ca gốc (cả khi ca đó đang là maLichTarget của 1 YC khác)
        for (YeuCauDoiCa yc : ycDAO.findAll()) {
            if (!"ChoDuyet".equals(yc.getTrangThai())) continue;
            if (maLich.equals(yc.getMaLichGoc()) || maLich.equals(yc.getMaLichTarget()))
                return "Ca này đã có yêu cầu đổi đang chờ duyệt.";
        }

        // Tạo và lưu yêu cầu
        String maYC = "YC" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        YeuCauDoiCa yc = new YeuCauDoiCa(
                maYC, maLich,
                nvTarget,
                lichTarget,
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