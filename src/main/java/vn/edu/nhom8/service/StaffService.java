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

    /** Lấy danh sách ca tương lai của một NV khác. */
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

    /**
     * Check-in cho maLich đã cho.
     * @return CheckInResult chứa trangThai thực tế và message hiển thị, hoặc errorMsg nếu thất bại.
     */
    public CheckInResult checkIn(String maNV, String maLich) {
        if (maLich == null)
            return CheckInResult.error("Bạn không có ca làm việc hôm nay.");
        if (chamCongDAO.isCheckedIn(maNV, maLich))
            return CheckInResult.error("Bạn đã check-in rồi!");

        ChamCong cc = chamCongDAO.checkIn(maNV, maLich);
        if (cc == null)
            return CheckInResult.error("Check-in thất bại. Vui lòng thử lại.");

        String trangThai = cc.getTrangThai();
        String msg;
        if ("DiMuon".equals(trangThai)) {
            String gioVao = new SimpleDateFormat("HH:mm:ss").format(cc.getGioVao());
            msg = "Check-in thành công lúc " + gioVao + ".\nBạn đã đi muộn!";
        } else {
            String gioVao = new SimpleDateFormat("HH:mm:ss").format(cc.getGioVao());
            msg = "Check-in thành công lúc " + gioVao + ". Đúng giờ!";
        }
        return CheckInResult.success(trangThai, msg);
    }

    /**
     * Check-out.
     * @return CheckOutResult chứa trangThai thực tế và message, hoặc errorMsg nếu thất bại.
     */
    public CheckOutResult checkOut(String maNV, String maLich) {
        if (maLich == null)
            return CheckOutResult.error("Không tìm thấy ca làm việc.");
        if (!chamCongDAO.isCheckedIn(maNV, maLich))
            return CheckOutResult.error("Bạn chưa check-in!");
        if (chamCongDAO.isCheckedOut(maLich))
            return CheckOutResult.error("Bạn đã check-out rồi!");

        ChamCong cc = chamCongDAO.checkOut(maNV, maLich);
        if (cc == null)
            return CheckOutResult.error("Check-out thất bại. Vui lòng thử lại.");

        String trangThai = cc.getTrangThai();
        String msg;
        String gioRa = new SimpleDateFormat("HH:mm:ss").format(cc.getGioRa());
        switch (trangThai) {
            case "VeSom":
                msg = "Check-out thành công lúc " + gioRa + ".\nBạn về sớm trước giờ kết thúc ca!";
                break;
            case "DiMuon":
                msg = "Check-out thành công lúc " + gioRa + ". (Bạn đã vào muộn nhưng ra đúng giờ.)";
                break;
            default:
                msg = "Check-out thành công lúc " + gioRa + ". Hoàn thành ca!";
                break;
        }
        return CheckOutResult.success(trangThai, msg);
    }

    /** Đã check-in chưa? */
    public boolean isCheckedIn(String maNV, String maLich) {
        return chamCongDAO.isCheckedIn(maNV, maLich);
    }

    /** Đã check-out chưa? */
    public boolean isCheckedOut(String maLich) {
        return chamCongDAO.isCheckedOut(maLich);
    }

    /** Lịch sử chấm công của NV. */
    public List<ChamCong> getLichSuChamCong(String maNV) {
        return chamCongDAO.getLichSuChamCong(maNV);
    }

    // ── Yêu cầu đổi ca ────────────────────────────────────────────────────

    public String guiYeuCauDoiCa(String maNV, String maLich, String maNVTarget,
                                  String maLichTarget, String lyDo) {
        if (lyDo == null || lyDo.trim().isEmpty()) return "Vui lòng nhập lý do.";
        if (maLich == null) return "Vui lòng chọn ca cần đổi.";

        LichPhanCa lich = lichDAO.findById(maLich);
        if (lich == null) return "Không tìm thấy lịch ca.";
        if (lich.getNgayLamViec().before(new Date(System.currentTimeMillis())))
            return "Chỉ được đổi ca trong tương lai.";

        String nvTarget   = (maNVTarget  == null || maNVTarget.trim().isEmpty())  ? null : maNVTarget.trim();
        String lichTarget = (maLichTarget == null || maLichTarget.trim().isEmpty()) ? null : maLichTarget.trim();

        if (nvTarget != null && nvTarget.equals(lich.getMaNV()))
            return "Không thể đổi ca với chính mình.";

        if (lichTarget != null) {
            if (nvTarget == null) return "Vui lòng nhập mã NV đổi cùng.";
            LichPhanCa lt = lichDAO.findById(lichTarget);
            if (lt == null) return "Không tìm thấy ca muốn đổi của NV đối phương.";
            if (!"DaPhan".equals(lt.getTrangThai())) return "Ca đối phương không còn hợp lệ để đổi.";
            if (!nvTarget.equals(lt.getMaNV())) return "Ca được chọn không thuộc nhân viên đã nhập.";
            if (lichTarget.equals(maLich)) return "Không thể đổi ca với chính ca này.";
            if (lt.getNgayLamViec().before(new Date(System.currentTimeMillis())))
                return "Ca muốn đổi của đối phương phải là ca trong tương lai.";
            for (YeuCauDoiCa y : ycDAO.findAll()) {
                if (!"ChoDuyet".equals(y.getTrangThai())) continue;
                if (lichTarget.equals(y.getMaLichGoc()) || lichTarget.equals(y.getMaLichTarget()))
                    return "Ca đó của đối phương đã có yêu cầu đổi đang chờ duyệt.";
            }
        }

        for (YeuCauDoiCa yc : ycDAO.findAll()) {
            if (!"ChoDuyet".equals(yc.getTrangThai())) continue;
            if (maLich.equals(yc.getMaLichGoc()) || maLich.equals(yc.getMaLichTarget()))
                return "Ca này đã có yêu cầu đổi đang chờ duyệt.";
        }

        String maYC = "YC" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        YeuCauDoiCa yc = new YeuCauDoiCa(
                maYC, maLich, nvTarget, lichTarget,
                lyDo.trim(), "ChoDuyet",
                new Timestamp(System.currentTimeMillis())
        );
        return ycDAO.insert(yc) ? null : "Gửi yêu cầu thất bại. Vui lòng thử lại.";
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

    // ── Result types ──────────────────────────────────────────────────────

    /** Kết quả check-in: trangThai thật + message hoặc errorMsg. */
    public static class CheckInResult {
        public final boolean  ok;
        public final String   trangThai;  // "DungGio" | "DiMuon" — null nếu lỗi
        public final String   message;    // message hiện thị cho user

        private CheckInResult(boolean ok, String trangThai, String message) {
            this.ok        = ok;
            this.trangThai = trangThai;
            this.message   = message;
        }

        public static CheckInResult success(String trangThai, String msg) {
            return new CheckInResult(true, trangThai, msg);
        }
        public static CheckInResult error(String msg) {
            return new CheckInResult(false, null, msg);
        }
    }

    /** Kết quả check-out: trangThai thật + message hoặc errorMsg. */
    public static class CheckOutResult {
        public final boolean  ok;
        public final String   trangThai;  // "DungGio" | "DiMuon" | "VeSom" — null nếu lỗi
        public final String   message;

        private CheckOutResult(boolean ok, String trangThai, String message) {
            this.ok        = ok;
            this.trangThai = trangThai;
            this.message   = message;
        }

        public static CheckOutResult success(String trangThai, String msg) {
            return new CheckOutResult(true, trangThai, msg);
        }
        public static CheckOutResult error(String msg) {
            return new CheckOutResult(false, null, msg);
        }
    }
}