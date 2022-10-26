package model.entity;

import java.util.ArrayList;

public class KhachHang extends ThanhVien{
    private String sdt="",diachi="";
    public KhachHang(){
        super();
    }
    public KhachHang(String tendangnhap, String matkhau, String tendaydu, int id, String sdt, String diachi, ArrayList<Group> groups) {
        super(tendangnhap, matkhau, tendaydu, id, groups);
        this.sdt = sdt;
        this.diachi = diachi;
    }
    
    public String getSdt() {
        return sdt;
    }
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
    public String getDiachi() {
        return diachi;
    }
    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }
}