/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author VICTUS
 */
package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.CaLamViec;
import java.util.List;

public interface ICaLamViecDAO {
    boolean insert(CaLamViec clv);
    boolean update(CaLamViec clv);
    CaLamViec findById(String maCa);
    List<CaLamViec> findAll();
}