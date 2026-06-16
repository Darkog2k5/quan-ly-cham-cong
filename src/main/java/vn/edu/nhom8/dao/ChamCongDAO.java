package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.ChamCong;
import vn.edu.nhom8.util.DBConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChamCongDAO {

    // ── Helper ────────────────────────────────────────────────────────────────
    private ChamCong mapRow(ResultSet rs) throws SQLException {
        ChamCong cc = new ChamCong();
        cc.setMaCong(rs.getString("maCong"));
        cc.setMaLich(rs.getString("maLich"));
        cc.setGioVao(rs.getTimestamp("gioVao"));
        cc.setGioRa(rs.getTimestamp("gioRa"));
        cc.setTrangThai(rs.getString("trangThai"));
        cc.setMinhChung(rs.getString("minhChung"));
        return cc;
    }

    // ── Kiểm tra đã check-in chưa ─────────────────────────────────────────────
    public boolean isCheckedIn(String maNV, String maLich) {
        String sql = "{CALL sp_IsCheckedIn(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maLich);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return rs.getInt("soLuong") > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Kiểm tra đã check-out chưa ───────────────────────────────────────────
    public boolean isCheckedOut(String maLich) {
        String sql = "SELECT COUNT(*) AS soLuong FROM ChamCong WHERE maLich = ? AND gioRa IS NOT NULL";
        try (Connection con = new DBConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLich);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("soLuong") > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Check-in – trả về ChamCong với trangThai thật (DungGio / DiMuon) ──────
    // Trả null nếu thất bại
    public ChamCong checkIn(String maNV, String maLich) {
        // Sinh mã chấm công ngắn gọn, đảm bảo <= 20 ký tự
        String maCong = "CC" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        if (maCong.length() > 20) maCong = maCong.substring(0, 20);

        String sql = "{CALL sp_CheckIn(?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maCong);
            cs.setString(2, maLich);
            cs.executeUpdate();
            // Đọc lại bản ghi vừa insert để lấy trangThai thực tế từ SP
            return getByLich(maLich);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Check-out – trả về ChamCong với trangThai thật (giữ DiMuon / set VeSom) ─
    // Trả null nếu thất bại
    public ChamCong checkOut(String maNV, String maLich) {
        String sql = "{CALL sp_CheckOut(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maLich);
            cs.executeUpdate();
            return getByLich(maLich);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Lấy bản ghi chấm công mới nhất theo maLich ───────────────────────────
    public ChamCong getByLich(String maLich) {
        String sql = "{CALL sp_GetChamCongByLich(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maLich);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Lịch sử chấm công của NV ─────────────────────────────────────────────
    public List<ChamCong> getLichSuChamCong(String maNV) {
        List<ChamCong> list = new ArrayList<>();
        String sql = "{CALL sp_GetLichSuChamCong(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maNV);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}