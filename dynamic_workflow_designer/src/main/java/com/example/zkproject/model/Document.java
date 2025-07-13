package com.example.zkproject.model;


import java.util.Date;

public class Document {

    private Long id;
    private String soHieu;
    private String tieuDe;
    private String loaiVanBan;
    private String nguoiSoanThao;
    private String tepDinhKem;
    private Date ngayBanHanh;
    private TrangThai trangThai;
    private Date deadline;

    public enum TrangThai {
        CHO_XU_LY, DANG_XU_LY, HOAN_THANH, QUA_HAN
    }

    public Document() {}

    public Document(Long id, String soHieu, String tieuDe, String loaiVanBan, String nguoiSoanThao,
                    String tepDinhKem,Date ngayBanHanh, TrangThai trangThai, Date deadline) {
        this.id = id;
        this.soHieu = soHieu;
        this.tieuDe = tieuDe;
        this.loaiVanBan = loaiVanBan;
        this.nguoiSoanThao = nguoiSoanThao;
        this.tepDinhKem = tepDinhKem;
        this.ngayBanHanh = ngayBanHanh;
        this.trangThai = trangThai;
        this.deadline = deadline;
    }

    public Date getNgayBanHanh() {
        return ngayBanHanh;
    }

    public void setNgayBanHanh(Date ngayBanHanh) {
        this.ngayBanHanh = ngayBanHanh;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSoHieu() { return soHieu; }
    public void setSoHieu(String soHieu) { this.soHieu = soHieu; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getLoaiVanBan() { return loaiVanBan; }
    public void setLoaiVanBan(String loaiVanBan) { this.loaiVanBan = loaiVanBan; }

    public String getNguoiSoanThao() { return nguoiSoanThao; }
    public void setNguoiSoanThao(String nguoiSoanThao) { this.nguoiSoanThao = nguoiSoanThao; }

    public String getTepDinhKem() { return tepDinhKem; }
    public void setTepDinhKem(String tepDinhKem) { this.tepDinhKem = tepDinhKem; }

    public TrangThai getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
}
