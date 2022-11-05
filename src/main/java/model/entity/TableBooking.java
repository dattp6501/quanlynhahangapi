package model.entity;

import java.util.ArrayList;
import java.util.Date;

public class TableBooking {
    private int id,time;
    private float price;
    private String note;
    private Table table;
    private Date startTime;
    private ArrayList<BookingDish> dishs;
    public TableBooking() {
    }
    public TableBooking(int id, int time, float price, String note, Table table, Date startTime, ArrayList<BookingDish> dishs) {
        this.id = id;
        this.time = time;
        this.price = price;
        this.note = note;
        this.table = table;
        this.startTime = startTime;
        this.dishs = dishs;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Table getTable() {
        return table;
    }
    public void setTable(Table table) {
        this.table = table;
    }
    public ArrayList<BookingDish> getDishs() {
        return dishs;
    }
    public void setDishs(ArrayList<BookingDish> dishs) {
        this.dishs = dishs;
    }
    @Override
    public String toString() {
        return "TableBooking [id=" + id + ", time=" + time + ", price=" + price + ", note=" + note + ", table=" + table
                + ", startTime=" + startTime + ", dishs=" + dishs + "]";
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
