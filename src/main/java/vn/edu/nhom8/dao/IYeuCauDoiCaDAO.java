package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.YeuCauDoiCa;
import java.util.List;

public interface IYeuCauDoiCaDAO {
    boolean insert(YeuCauDoiCa ycdc);
    boolean update(YeuCauDoiCa ycdc);
    YeuCauDoiCa findById(String maYeuCau);
    List<YeuCauDoiCa> findAll();
}
