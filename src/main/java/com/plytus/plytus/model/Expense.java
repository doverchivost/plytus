package com.plytus.plytus.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //категория - category
    //пользователь, кому пренадлежит трата - user

    @Column(name = "expense_name", unique = false, nullable = false)
    private String name;

    @Column(name = "expense_price", unique = false, nullable = false)
    private double price;

    public Expense(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }

    //по хорошему Override equals, hashCode и toString
    //пример: https://github.com/ivan909020/shop-telegram-bot/blob/405c8a24fd6d60f0026831972f209685b5936592/admin-panel/src/main/java/ua/ivan909020/admin/models/entities/Client.java

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense o_expense = (Expense) o;
        return Objects.equals(id, o_expense.id) &&
                Objects.equals(name, o_expense.name) &&
                Objects.equals(price, o_expense.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }

    @Override
    public String toString() {
        return "Expense: {" +
                "id=" + id +
                ", name=" + name +
                ", price=" + price +
                "}";
    }
}
