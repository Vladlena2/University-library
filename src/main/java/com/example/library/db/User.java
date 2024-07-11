package com.example.library.db;

// Модель пользователя
public class User {
    private String login;
    private String password;
    private String surname;
    private String name;
    private String patronymic;
    private boolean admin;

    public User(String login, String password, String surname, String name, String patronymic, boolean admin) {
        this.login = login;
        this.password = password;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.admin = admin;
    }

    public User(String surname, String name, String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getFullName() {
        return surname + " " + name + " " + patronymic;
    }
}
