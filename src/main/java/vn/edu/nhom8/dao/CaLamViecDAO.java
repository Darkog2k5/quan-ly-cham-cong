package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.CaLamViec;
import vn.edu.nhom8.util.DBConnection;

public class CaLamViecDAO implements ICaLamViecDAO {

    @Override
    public List<CaLamViec> findAll() {
        List<CaLamViec> list = new ArrayList<>();
        // Gọi thẳng câu lệnh SQL truy vấn vào bảng Shifts tiếng Anh luôn
        String sql = "SELECT * FROM Shifts"; 
        DBConnection db = new DBConnection();
        
        try (Connection con = db.getConnection();
            
             PreparedStatement pstmt = con.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                CaLamViec clv = new CaLamViec();
                
               
                clv.setMaCa(String.valueOf(rs.getInt("shift_id"))); // Lấy cột shift_id (int) ép sang String
                clv.setTenCa(rs.getString("shift_type"));           // Lấy cột shift_type điền vào tên ca
                clv.setGioBatDau(null);                             // Model yêu cầu nhưng DB không có cột giờ, để null tạm thời
                clv.setGioKetThuc(null);
                
                list.add(clv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(CaLamViec clv) {
        return false;
    }

    @Override
    public boolean update(CaLamViec clv) {
        return false;
    }

    @Override
    public CaLamViec findById(String maCa) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}