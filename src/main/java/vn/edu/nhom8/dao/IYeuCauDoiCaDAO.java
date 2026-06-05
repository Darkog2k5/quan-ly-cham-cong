/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.project.dao;

import vn.edu.project.model.YeuCauDoiCa;
import java.util.List;

public interface IYeuCauDoiCaDAO {
    boolean insert(YeuCauDoiCa ycdc);
    boolean update(YeuCauDoiCa ycdc);
    YeuCauDoiCa findById(String maYeuCau);
    List<YeuCauDoiCa> findAll();
}
