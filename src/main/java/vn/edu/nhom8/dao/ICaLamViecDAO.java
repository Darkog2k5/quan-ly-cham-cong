package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.CaLamViec;
import java.util.List;

public interface ICaLamViecDAO {
    boolean insert(CaLamViec clv);
    boolean update(CaLamViec clv);
    CaLamViec findById(String maCa);
    List<CaLamViec> findAll();
}