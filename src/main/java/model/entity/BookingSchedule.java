package model.entity;

import java.util.ArrayList;
import java.util.Date;

public class BookingSchedule {
    private int id;
    private Date date;
    private float depositMoney;
    private String note;
    private Customer customer;
    private ArrayList<TableBooking> tableBooking;
    public BookingSchedule() {
    }
    public BookingSchedule(int id, Date date, float depositMoney, String note, Customer customer,
            ArrayList<TableBooking> tableBooking) {
        this.id = id;
        this.date = date;
        this.depositMoney = depositMoney;
        this.note = note;
        this.customer = customer;
        this.tableBooking = tableBooking;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public float getDepositMoney() {
        return depositMoney;
    }
    public void setDepositMoney(float depositMoney) {
        this.depositMoney = depositMoney;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public ArrayList<TableBooking> getTableBooking() {
        return tableBooking;
    }
    public void setTableBooking(ArrayList<TableBooking> tableBooking) {
        this.tableBooking = tableBooking;
    }
}
