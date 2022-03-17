package com.plytus.plytus.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @Column(name="expense_id", nullable = false, updatable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    //категория - category
    //пользователь, кому пренадлежит трата - user

    @Column(name = "expense_date", nullable = false)
    private Date date;

    @Column(name = "expense_name", unique = false, nullable = false)
    private String name;

    @Column(name = "expense_price", unique = false, nullable = false)
    private double price;

    //категория и юзер внешний ключ
    public Expense(String name, Date date, double price) {
        this.name = name;
        this.date = date;
        this.price = price;
    }

    public Long getId() { return id; }
    public Date getDate() { return date; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setDate(Date date) { this.date = date; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }

    //пример: https://github.com/ivan909020/shop-telegram-bot/blob/405c8a24fd6d60f0026831972f209685b5936592/admin-panel/src/main/java/ua/ivan909020/admin/models/entities/Client.java
}
