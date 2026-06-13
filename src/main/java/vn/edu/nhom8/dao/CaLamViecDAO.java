package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.CaLamViec;
import vn.edu.nhom8.util.DBConnection;

public class CaLamViecDAO implements ICaLamViecDAO {

    // ── Helper ────────────────────────────────────────────────────────────────
    private CaLamViec mapRow(ResultSet rs) throws SQLException {
        CaLamViec clv = new CaLamViec();
        clv.setMaCa(rs.getString("maCa"));
        clv.setTenCa(rs.getString("tenCa"));
        clv.setGioBatDau(rs.getTime("gioBatDau"));
        clv.setGioKetThuc(rs.getTime("gioKetThuc"));
        return clv;
    }

    // ── Find all ──────────────────────────────────────────────────────────────
    @Override
    public List<CaLamViec> findAll() {
        List<CaLamViec> list = new ArrayList<>();
        String sql = "{CALL sp_FindAllCaLamViec}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    @Override
    public CaLamViec findById(String maCa) {
        String sql = "{CALL sp_FindCaLamViecById(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maCa);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Insert ────────────────────────────────────────────────────────────────
    @Override
    public boolean insert(CaLamViec clv) {
        String sql = "{CALL sp_InsertCaLamViec(?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, clv.getMaCa());
            cs.setString(2, clv.getTenCa());
            cs.setTime(3, clv.getGioBatDau());
            cs.setTime(4, clv.getGioKetThuc());
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @Override
    public boolean update(CaLamViec clv) {
        String sql = "{CALL sp_UpdateCaLamViec(?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, clv.getMaCa());
            cs.setString(2, clv.getTenCa());
            cs.setTime(3, clv.getGioBatDau());
            cs.setTime(4, clv.getGioKetThuc());
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    public boolean delete(String maCa) {
        String sql = "{CALL sp_DeleteCaLamViec(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maCa);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}