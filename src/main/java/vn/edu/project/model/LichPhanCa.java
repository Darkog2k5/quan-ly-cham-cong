/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.project.model;

import java.sql.Date;

public class LichPhanCa {
    private String maLich;
    private String maNV;
    private String maCa;
    private Date ngayLamViec;
    private String trangThai;

    public LichPhanCa() {}

    public LichPhanCa(String maLich, String maNV, String maCa, Date ngayLamViec, String trangThai) {
        this.maLich = maLich;
        this.maNV = maNV;
        this.maCa = maCa;
        this.ngayLamViec = ngayLamViec;
        this.trangThai = trangThai;
    }

    public String getMaLich() { return maLich; }
    public void setMaLich(String maLich) { this.maLich = maLich; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public Date getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(Date ngayLamViec) { this.ngayLamViec = ngayLamViec; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
