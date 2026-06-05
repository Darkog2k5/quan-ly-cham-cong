/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.project.dao;

import vn.edu.project.model.ChamCong;
import java.util.List;

public interface IChamCongDAO {
    boolean insert(ChamCong cc);
    boolean update(ChamCong cc);
    ChamCong findById(String maCong);
    List<ChamCong> findAll();
    boolean checkIn(String maLich);
    boolean checkOut(String maLich);
}