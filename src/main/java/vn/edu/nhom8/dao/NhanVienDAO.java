package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.util.DBConnection;

public class NhanVienDAO implements INhanVienDAO {

    // ── Helper ────────────────────────────────────────────────────────────────
    private NhanVien mapRow(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("maNV"));
        nv.setHoTen(rs.getString("hoTen"));
        nv.setVaiTro(rs.getString("vaiTro"));
        nv.setTaiKhoan(rs.getString("taiKhoan"));
        nv.setTrangThai(rs.getString("trangThai"));
        return nv;
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    @Override
    public NhanVien login(String taiKhoan, String matKhau) {
        String sql = "{CALL sp_Login(?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, taiKhoan);
            cs.setString(2, matKhau);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    @Override
    public NhanVien findById(String maNV) {
        String sql = "{CALL sp_FindNhanVienById(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maNV);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Find all ──────────────────────────────────────────────────────────────
    @Override
    public List<NhanVien> findAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "{CALL sp_FindAllNhanVien}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Insert ────────────────────────────────────────────────────────────────
    @Override
    public boolean insert(NhanVien nv) {
        String sql = "{CALL sp_InsertNhanVien(?, ?, ?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, nv.getMaNV());
            cs.setString(2, nv.getHoTen());
            cs.setString(3, nv.getVaiTro());
            cs.setString(4, nv.getTaiKhoan());
            cs.setString(5, nv.getMatKhau());
            cs.setString(6, nv.getTrangThai() != null ? nv.getTrangThai() : "HoatDong");
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @Override
    public boolean update(NhanVien nv) {
        String sql = "{CALL sp_UpdateNhanVien(?, ?, ?, ?, ?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, nv.getMaNV());
            cs.setString(2, nv.getHoTen());
            cs.setString(3, nv.getVaiTro());
            cs.setString(4, nv.getTaiKhoan());
            cs.setString(5, nv.getMatKhau());
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Deactivate (khóa tài khoản) ───────────────────────────────────────────
    @Override
    public boolean deactivate(String maNV) {
        String sql = "{CALL sp_DeactivateNhanVien(?)}";
        try (Connection con = new DBConnection().getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, maNV);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateThongTin(String maNV, String hoTenMoi) {
        String sql = "UPDATE NhanVien SET hoTen = ? WHERE maNV = ?";
        
        try (java.sql.Connection conn = new vn.edu.nhom8.util.DBConnection().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hoTenMoi);
            ps.setString(2, maNV);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Lỗi SQL khi cập nhật thông tin cá nhân:");
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String taiKhoan, String newPassword) {
        String sql = "UPDATE NhanVien SET matKhau = ? WHERE taiKhoan = ?";
        
        try (java.sql.Connection conn = new vn.edu.nhom8.util.DBConnection().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // LƯU TRỰC TIẾP MẬT KHẨU THÔ (Không dùng BCrypt nữa)
            ps.setString(1, newPassword); 
            ps.setString(2, taiKhoan); 
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Lỗi SQL khi đổi mật khẩu:");
            e.printStackTrace();
        }
        return false;
    }
}