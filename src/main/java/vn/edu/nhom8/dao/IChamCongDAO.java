package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.ChamCong;
import java.util.List;

public interface IChamCongDAO {
    boolean insert(ChamCong cc);
    boolean update(ChamCong cc);
    ChamCong findById(String maCong);
    List<ChamCong> findAll();
    boolean checkIn(String maLich);
    boolean checkOut(String maLich);
}