package com.example.library.db;

public class Application {
    private String bookName;
    private int quantity;
    private String status;

    public Application() {
        // Пустой конструктор нужен для работы с Firebase
    }

    public Application(String bookName, int quantity, String status) {
        this.bookName = bookName;
        this.quantity = quantity;
        this.status = status;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
