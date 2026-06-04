/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.nhom8.dao;

import vn.edu.nhom8.model.ChamCong;
import java.util.ArrayList;
import java.util.List;

public class ChamCongDAO {

    public boolean isCheckedIn(String maNV, String maLich) {
        return false;
    }

    public boolean checkIn(String maNV, String maLich) {
        return true;
    }

    public boolean checkOut(String maNV, String maLich) {
        return true;
    }

    public List<ChamCong> getLichSuChamCong(String maNV) {
        return new ArrayList<>();
    }
}