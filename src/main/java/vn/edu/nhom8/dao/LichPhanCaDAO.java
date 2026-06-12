package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.LichPhanCa;
import vn.edu.nhom8.util.DBConnection;

public class LichPhanCaDAO implements ILichPhanCaDAO {

    // ── Helper ────────────────────────────────────────────────────────────────
    private LichPhanCa mapRow(ResultSet rs) throws SQLException {
        LichPhanCa lpc = new LichPhanCa();
        lpc.setMaLich(rs.getString("maLich"));
        lpc.setMaNV(rs.getString("maNV"));
        lpc.setMaCa(rs.getString("maCa"));
        lpc.setNgayLamViec(rs.getDate("ngayLamViec"));
        lpc.setTrangThai(rs.getString("trangThai"));
        return lpc;
    }

    // ── Find all ──────────────────────────────────────────────────────────────
    @Override
    public List<LichPhanCa> findAll() {
        List<LichPhanCa> list = new ArrayList<>();
        String sql = "{CALL sp_FindAllLichPhanCa}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    @Override
    public LichPhanCa findById(String maLich) {
        String sql = "{CALL sp_FindLichPhanCaById(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maLich);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Insert ────────────────────────────────────────────────────────────────
    @Override
    public boolean insert(LichPhanCa lpc) {
        String sql = "{CALL sp_InsertLichPhanCa(?, ?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, lpc.getMaLich());
            cs.setString(2, lpc.getMaNV());
            cs.setString(3, lpc.getMaCa());
            cs.setDate(4, lpc.getNgayLamViec());
            cs.setString(5, lpc.getTrangThai() != null ? lpc.getTrangThai() : "DaPhan");
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Update (chỉ cập nhật trạng thái) ─────────────────────────────────────
    @Override
    public boolean update(LichPhanCa lpc) {
        String sql = "{CALL sp_UpdateLichPhanCa(?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, lpc.getMaLich());
            cs.setString(2, lpc.getTrangThai());
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Update đầy đủ (NV, ca, ngày, trạng thái) – dùng cho Sửa lịch ─────────
    @Override
    public boolean updateFull(LichPhanCa lpc) {
        String sql = "{CALL sp_UpdateLichPhanCaFull(?, ?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, lpc.getMaLich());
            cs.setString(2, lpc.getMaNV());
            cs.setString(3, lpc.getMaCa());
            cs.setDate(4, lpc.getNgayLamViec());
            cs.setString(5, lpc.getTrangThai());
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @Override
    public boolean delete(String maLich) {
        String sql = "{CALL sp_DeleteLichPhanCa(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maLich);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}