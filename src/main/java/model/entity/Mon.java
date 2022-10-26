package model.entity;

import java.util.Base64;

public class Mon {
    private String ten,mota,size,anh;
    private double gia;
    private int id;
    public Mon(String ten, String mota, String size, double gia, byte[] anh, int id) {
        this.ten = ten;
        this.mota = mota;
        this.size = size;
        this.gia = gia;
        this.anh = Base64.getEncoder().encodeToString(anh);
        this.id = id;
    }
    public Mon(String ten, String mota, String size, double gia, String anhBase64, int id) {
        this.ten = ten;
        this.mota = mota;
        this.size = size;
        this.gia = gia;
        this.anh = anhBase64;
        this.id = id;
    }

    public Mon() {
        super();
    }

    public String getTen() {
        return ten;
    }
    public void setTen(String ten) {
        this.ten = ten;
    }
    public String getMota() {
        return mota;
    }
    public void setMota(String mota) {
        this.mota = mota;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public double getGia() {
        return gia;
    }
    public void setGia(double gia) {
        this.gia = gia;
    }
    public String getAnh() {
        return anh;
    }
    public void setAnh(byte[] anh) {
        this.anh = Base64.getEncoder().encodeToString(anh);
    }

    public void setAnh(String anhBase64) {
        this.anh = anhBase64;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Mon [ten=" + ten + ", mota=" + mota + ", size=" + size + ", gia=" + gia + ", anh="
                + anh + ", id=" + id + "]";
    }
}
