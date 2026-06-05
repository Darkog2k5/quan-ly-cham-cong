package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.util.DBConnection; 

public class NhanVienDAO implements INhanVienDAO {

    @Override
    public NhanVien login(String taiKhoan, String matKhau) {
        String sql = "{CALL sp_Login(?, ?)}";
        DBConnection db = new DBConnection(); 
        
        try (Connection con = db.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {
            
            cstmt.setString(1, taiKhoan);
            cstmt.setString(2, matKhau);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("maNV"));
                    nv.setHoTen(rs.getString("hoTen"));
                    nv.setVaiTro(rs.getString("vaiTro"));
                    nv.setTaiKhoan(rs.getString("taiKhoan"));
                    nv.setTrangThai(rs.getString("trangThai"));
                    return nv;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deactivate(String maNV) {
        String sql = "{CALL sp_DeactivateNhanVien(?)}";
        DBConnection db = new DBConnection();
        
        try (Connection con = db.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {
            
            cstmt.setString(1, maNV);
            return cstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public NhanVien findById(String maNV) {
        String sql = "{CALL sp_FindNhanVienById(?)}";
        DBConnection db = new DBConnection();
        
        try (Connection con = db.getConnection();
             CallableStatement cstmt = con.prepareCall(sql)) {
            
            cstmt.setString(1, maNV);
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("maNV"));
                    nv.setHoTen(rs.getString("hoTen"));
                    nv.setVaiTro(rs.getString("vaiTro"));
                    nv.setTaiKhoan(rs.getString("taiKhoan"));
                    nv.setTrangThai(rs.getString("trangThai"));
                    return nv;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<NhanVien> findAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "{CALL sp_FindAllNhanVien}";
        DBConnection db = new DBConnection();
        
        try (Connection con = db.getConnection();
             CallableStatement cstmt = con.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setVaiTro(rs.getString("vaiTro"));
                nv.setTaiKhoan(rs.getString("taiKhoan"));
                nv.setTrangThai(rs.getString("trangThai"));
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(NhanVien nv) {
        return false;
    }

    @Override
    public boolean update(NhanVien nv) {
        return false;
    }
}