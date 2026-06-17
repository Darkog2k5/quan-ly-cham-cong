package vn.edu.nhom8.model;

import java.sql.Timestamp;

public class YeuCauDoiCa {
    private String maYeuCau;
    private String maLichGoc;
    private String maNVTarget;
    private String maLichTarget; // NULL = nhờ làm giúp; có giá trị = đổi ca cho nhau (ca cụ thể của NV target)
    private String lyDo;
    private String trangThai;
    private Timestamp ngayTao;

    public YeuCauDoiCa() {}

    public YeuCauDoiCa(String maYeuCau, String maLichGoc, String maNVTarget, String maLichTarget,
                        String lyDo, String trangThai, Timestamp ngayTao) {
        this.maYeuCau = maYeuCau;
        this.maLichGoc = maLichGoc;
        this.maNVTarget = maNVTarget;
        this.maLichTarget = maLichTarget;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public YeuCauDoiCa(String maYeuCau, String maLichGoc, String maNVTarget,
                        String lyDo, String trangThai, Timestamp ngayTao) {
        this(maYeuCau, maLichGoc, maNVTarget, null, lyDo, trangThai, ngayTao);
    }

    public String getMaYeuCau() { return maYeuCau; }
    public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }

    public String getMaLichGoc() { return maLichGoc; }
    public void setMaLichGoc(String maLichGoc) { this.maLichGoc = maLichGoc; }

    public String getMaNVTarget() { return maNVTarget; }
    public void setMaNVTarget(String maNVTarget) { this.maNVTarget = maNVTarget; }

    public String getMaLichTarget() { return maLichTarget; }
    public void setMaLichTarget(String maLichTarget) { this.maLichTarget = maLichTarget; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
}