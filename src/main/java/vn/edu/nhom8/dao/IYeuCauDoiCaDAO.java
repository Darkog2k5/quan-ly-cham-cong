/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.YeuCauDoiCa;
import java.util.List;

public interface IYeuCauDoiCaDAO {
    boolean insert(YeuCauDoiCa ycdc);
    boolean update(YeuCauDoiCa ycdc);
    YeuCauDoiCa findById(String maYeuCau);
    List<YeuCauDoiCa> findAll();
}
