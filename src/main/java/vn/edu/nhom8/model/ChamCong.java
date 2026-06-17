package vn.edu.nhom8.model;

import java.sql.Timestamp;

public class ChamCong {
    private String maCong;
    private String maLich;
    private Timestamp gioVao;
    private Timestamp gioRa;
    private String trangThai;
    private String minhChung;

    public ChamCong() {}

    public ChamCong(String maCong, String maLich, Timestamp gioVao, Timestamp gioRa, String trangThai, String minhChung) {
        this.maCong = maCong;
        this.maLich = maLich;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.trangThai = trangThai;
        this.minhChung = minhChung;
    }

    public String getMaCong() { return maCong; }
    public void setMaCong(String maCong) { this.maCong = maCong; }

    public String getMaLich() { return maLich; }
    public void setMaLich(String maLich) { this.maLich = maLich; }

    public Timestamp getGioVao() { return gioVao; }
    public void setGioVao(Timestamp gioVao) { this.gioVao = gioVao; }

    public Timestamp getGioRa() { return gioRa; }
    public void setGioRa(Timestamp gioRa) { this.gioRa = gioRa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMinhChung() { return minhChung; }
    public void setMinhChung(String minhChung) { this.minhChung = minhChung; }
}