package model.entity;

import java.util.ArrayList;

public class Table {
    private int succhua,id;
    private double giathue;
    private ArrayList<FreeTime> freeTime;
    private String mota,ten;
    public Table(int succhua, int id, double giathue, ArrayList<FreeTime> freeTime, String mota,
            String ten) {
        this.succhua = succhua;
        this.id = id;
        this.giathue = giathue; 
        this.freeTime = freeTime;
        this.mota = mota;
        this.ten = ten;
    }

    public Table() {
        super();
    }
    
    public int getSucchua() {
        return succhua;
    }
    public void setSucchua(int succhua) {
        this.succhua = succhua;
    }
    public double getGiathue() {
        return giathue;
    }
    public void setGiathue(double giathue) {
        this.giathue = giathue;
    }
    public ArrayList<FreeTime> getFreeTime() {
        return freeTime;
    }
    public void setFreeTime(ArrayList<FreeTime> freeTime) {
        this.freeTime = freeTime;
    }
    public String getMota() {
        return mota;
    }
    public void setMota(String mota) {
        this.mota = mota;
    }
    public String getTen() {
        return ten;
    }
    public void setTen(String ten) {
        this.ten = ten;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ban [succhua=" + succhua + ", id=" + id + ", giathue=" + giathue + ", freeTime=" + freeTime + ", mota="
                + mota + ", ten=" + ten + "]";
    }
}
