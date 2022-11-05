package model.entity;

import java.util.Base64;

public class Dish {
    private String name,note,size,image;
    private double price;
    private int id, number;
    public Dish(String name, String note, String size, double price, byte[] image, int id) {
        this.name = name;
        this.note = note;
        this.size = size;
        this.price = price;
        this.image = Base64.getEncoder().encodeToString(image);
        this.id = id;
    }
    public Dish(String name, String note, String size, double price, String anhBase64, int id) {
        this.name = name;
        this.note = note;
        this.size = size;
        this.price = price;
        this.image = anhBase64;
        this.id = id;
    }

    public Dish(int id, String name, int number, String size, double price, String anhBase64, String note) {
        this.name = name;
        this.note = note;
        this.size = size;
        this.price = price;
        this.image = anhBase64;
        this.id = id;
        this.number = number;
    }

    public Dish() {
        super();
    }

    public String getTen() {
        return name;
    }
    public void setTen(String name) {
        this.name = name;
    }
    public String getMota() {
        return note;
    }
    public void setMota(String note) {
        this.note = note;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public double getGia() {
        return price;
    }
    public void setGia(double price) {
        this.price = price;
    }
    public String getAnh() {
        return image;
    }
    public void setAnh(byte[] image) {
        this.image = Base64.getEncoder().encodeToString(image);
    }

    public void setAnh(String imageBase64) {
        this.image = imageBase64;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Mon [ten=" + name + ", mota=" + note + ", size=" + size + ", gia=" + price + ", anh="
                + image + ", id=" + id + ", soluong=" + number + "]";
    }
}
