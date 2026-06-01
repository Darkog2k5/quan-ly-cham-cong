/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.project.dao;


import vn.edu.project.model.NhanVien;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    
    public boolean login(String taiKhoan, String matKhau) {
        return true;
    }

    public NhanVien getNhanVienById(String maNV) {
        return new NhanVien("NV001", "Nguyen Van Khoi", "Admin", "khoinv", "hash_code", "HoatDong");
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        list.add(new NhanVien("NV001", "Nguyen Van Khoi", "Admin", "khoinv", "hash_code", "HoatDong"));
        list.add(new NhanVien("NV002", "Nguyen Van Huy", "Developer", "huynv", "hash_code", "HoatDong"));
        return list;
    }

    public boolean insertNhanVien(NhanVien nv) {
        return true;
    }

    public boolean updateNhanVien(NhanVien nv) {
        return true;
    }

    public boolean deactivateNhanVien(String maNV) {
        return true;
    }
}