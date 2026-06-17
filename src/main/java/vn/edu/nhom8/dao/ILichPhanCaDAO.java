package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.LichPhanCa;
import java.util.List;

public interface ILichPhanCaDAO {
    boolean insert(LichPhanCa lpc);
    boolean update(LichPhanCa lpc);
    boolean updateFull(LichPhanCa lpc);
    boolean delete(String maLich);
    LichPhanCa findById(String maLich);
    List<LichPhanCa> findAll();
}