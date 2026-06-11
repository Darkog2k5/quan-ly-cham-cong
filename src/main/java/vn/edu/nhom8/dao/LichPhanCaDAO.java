package vn.edu.nhom8.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.nhom8.model.LichPhanCa;
import vn.edu.nhom8.util.DBConnection;

public class LichPhanCaDAO implements ILichPhanCaDAO {

    @Override
    public List<LichPhanCa> findAll() {
        List<LichPhanCa> list = new ArrayList<>();
        // HACK NHANH: Gọi thẳng câu lệnh SQL truy vấn vào bảng Shifts
        String sql = "SELECT * FROM Shifts"; 
        DBConnection db = new DBConnection();
        
        try (Connection con = db.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                LichPhanCa lpc = new LichPhanCa();
                
                // RÁP CHUẨN GIỮA MODEL TIẾNG VIỆT VÀ CỘT DB TIẾNG ANH:
                lpc.setMaLich(String.valueOf(rs.getInt("schedule_id"))); // Cột schedule_id dưới DB
                lpc.setMaCa(String.valueOf(rs.getInt("shift_id")));       // Cột shift_id dưới DB
                lpc.setNgayLamViec(rs.getDate("work_date"));             // Cột work_date dưới DB
                lpc.setTrangThai(rs.getString("status"));                 // Cột status dưới DB
                
                // Vì bảng Shifts này không có sẵn cột mã nhân viên (employee_id thường nằm ở bảng Assignments),
                // nên tạm thời ông cứ để null ở đây để code chạy liên kết không bị quăng lỗi crash nhé.
                lpc.setMaNV(null); 
                
                list.add(lpc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(LichPhanCa lpc) {
        return false;
    }

    @Override
    public boolean update(LichPhanCa lpc) {
        return false;
    }

    @Override
    public LichPhanCa findById(String maLich) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}