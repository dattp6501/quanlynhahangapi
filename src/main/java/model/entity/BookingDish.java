package model.entity;

public class BookingDish {
    private int id,number;
    private float price;
    private String size,note;
    private Dish dish;
    public BookingDish() {
    }
    public BookingDish(int id, int number, float price, String size, String note, Dish dish) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.size = size;
        this.note = note;
        this.dish = dish;
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
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Dish getDish() {
        return dish;
    }
    public void setDish(Dish dish) {
        this.dish = dish;
    }
}
