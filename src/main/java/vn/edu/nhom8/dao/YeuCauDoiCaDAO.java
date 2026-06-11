package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.YeuCauDoiCa;
import vn.edu.nhom8.util.DBConnection;

public class YeuCauDoiCaDAO implements IYeuCauDoiCaDAO {

    @Override
    public List<YeuCauDoiCa> findAll() {
        List<YeuCauDoiCa> list = new ArrayList<>();
        
        String sql = "SELECT * FROM Availability"; 
        DBConnection db = new DBConnection();
        
        try (Connection con = db.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                YeuCauDoiCa ycdc = new YeuCauDoiCa();
                
                // RÁP CHÍNH XÁC THEO 2 CỘT TIẾNG ANH CÓ THẬT TRONG DB:
                ycdc.setMaNVTarget(String.valueOf(rs.getInt("employee_id"))); // Lấy employee_id
                ycdc.setMaLichGoc(String.valueOf(rs.getInt("shift_id")));     // Lấy shift_id
                
                // Các cột còn lại Model tiếng Việt yêu cầu nhưng DB không có, 
               
                ycdc.setMaYeuCau("YCDC_" + rs.getInt("employee_id") + "_" + rs.getInt("shift_id"));
                ycdc.setTrangThai("PENDING"); 
                ycdc.setLyDo("Đổi ca làm việc");
                ycdc.setNgayTao(null); 
                
                list.add(ycdc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(YeuCauDoiCa ycdc) {
        return false;
    }

    @Override
    public boolean update(YeuCauDoiCa ycdc) {
        return false;
    }

    @Override
    public YeuCauDoiCa findById(String maYeuCau) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}