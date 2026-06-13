package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.YeuCauDoiCa;
import vn.edu.nhom8.util.DBConnection;

public class YeuCauDoiCaDAO implements IYeuCauDoiCaDAO {

    // ── Helper ────────────────────────────────────────────────────────────────
    private YeuCauDoiCa mapRow(ResultSet rs) throws SQLException {
        YeuCauDoiCa yc = new YeuCauDoiCa();
        yc.setMaYeuCau(rs.getString("maYeuCau"));
        yc.setMaLichGoc(rs.getString("maLichGoc"));
        yc.setMaNVTarget(rs.getString("maNVTarget"));
        yc.setMaLichTarget(rs.getString("maLichTarget"));
        yc.setLyDo(rs.getString("lyDo"));
        yc.setTrangThai(rs.getString("trangThai"));
        yc.setNgayTao(rs.getTimestamp("ngayTao"));
        return yc;
    }

    // ── Find all ──────────────────────────────────────────────────────────────
    @Override
    public List<YeuCauDoiCa> findAll() {
        List<YeuCauDoiCa> list = new ArrayList<>();
        String sql = "{CALL sp_FindAllYeuCauDoiCa}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    @Override
    public YeuCauDoiCa findById(String maYeuCau) {
        String sql = "{CALL sp_FindYeuCauDoiCaById(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maYeuCau);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Insert ────────────────────────────────────────────────────────────────
    @Override
    public boolean insert(YeuCauDoiCa yc) {
        String sql = "{CALL sp_InsertYeuCauDoiCa(?, ?, ?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, yc.getMaYeuCau());
            cs.setString(2, yc.getMaLichGoc());
            cs.setString(3, yc.getMaNVTarget());   // có thể null
            cs.setString(4, yc.getMaLichTarget()); // có thể null
            cs.setString(5, yc.getLyDo());
            cs.setString(6, yc.getTrangThai() != null ? yc.getTrangThai() : "ChoDuyet");
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Update (cập nhật trạng thái duyệt/từ chối) ───────────────────────────
    @Override
    public boolean update(YeuCauDoiCa yc) {
        String sql = "{CALL sp_UpdateTrangThaiYeuCau(?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, yc.getMaYeuCau());
            cs.setString(2, yc.getTrangThai());
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}