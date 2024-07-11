package com.example.library.db;

public class Ticket {
    private String id;
    private String bookName;
    private String dataStart;
    private String dataEnd;
    private int quantity;
    private String status; // Добавлено поле для статуса билета

    public Ticket(String id, String bookName, String dataStart, String dataEnd, int quantity, String status) {
        this.id = id;
        this.bookName = bookName;
        this.dataStart = dataStart;
        this.dataEnd = dataEnd;
        this.quantity = quantity;
        this.status = status;
    }

    public Ticket() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public String getDataStart() {
        return dataStart;
    }

    public String getDataEnd() {
        return dataEnd;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() { // Метод доступа к статусу билета
        return status;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setDataStart(String dataStart) {
        this.dataStart = dataStart;
    }

    public void setDataEnd(String dataEnd) {
        this.dataEnd = dataEnd;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) { // Метод установки статуса билета
        this.status = status;
    }
}
