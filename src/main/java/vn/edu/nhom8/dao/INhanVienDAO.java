/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vn.edu.project.dao;

import vn.edu.project.model.NhanVien;
import java.util.List;

public interface INhanVienDAO {
    boolean insert(NhanVien nv);
    boolean update(NhanVien nv);
    boolean deactivate(String maNV);
    NhanVien findById(String maNV);
    List<NhanVien> findAll();
    NhanVien login(String taiKhoan, String matKhau);
}
