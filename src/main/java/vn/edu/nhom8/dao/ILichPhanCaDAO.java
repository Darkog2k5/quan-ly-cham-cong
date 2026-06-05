/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.nhom8.dao;

/**
 *
 * @author VICTUS
 */


import vn.edu.nhom8.model.LichPhanCa;
import java.util.List;

public interface ILichPhanCaDAO {
    boolean insert(LichPhanCa lpc);
    boolean update(LichPhanCa lpc);
    LichPhanCa findById(String maLich);
    List<LichPhanCa> findAll();
}
