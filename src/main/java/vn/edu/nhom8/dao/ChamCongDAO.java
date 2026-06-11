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

    // ── Check-in ──────────────────────────────────────────────────────────────
    public boolean checkIn(String maNV, String maLich) {
        String maCong = "CC" + new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new java.util.Date()) + maLich.replaceAll("[^0-9A-Za-z]", "");
        if (maCong.length() > 20) maCong = maCong.substring(0, 20);

        String sql = "{CALL sp_CheckIn(?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maCong);
            cs.setString(2, maLich);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Check-out ─────────────────────────────────────────────────────────────
    public boolean checkOut(String maNV, String maLich) {
        String sql = "{CALL sp_CheckOut(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maLich);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
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