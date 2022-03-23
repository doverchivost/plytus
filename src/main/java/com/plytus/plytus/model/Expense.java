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

    @Column(name = "expense_date", nullable = false)
    private Date date;

    @Column(name = "expense_name", unique = false, nullable = false)
    private String name;

    @Column(name = "expense_price", unique = false, nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)//(cascade = CascadeType.PERSIST)//(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "owned_by")
    private User owner;

    public Expense(String name, Date date, double price, Category category, User owner) {
        this.name = name;
        this.date = date;
        this.price = price;
        this.category = category;
        this.owner = owner;
    }

    public Expense() { super(); }

    public Long getId() { return id; }
    public Date getDate() { return date; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setDate(Date date) { this.date = date; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }

    public Category getCategory() { return category; }
    public User getOwner() { return owner; }
    public void setCategory(Category category) { this.category = category; }
    public void setOwner(User owner) { this.owner = owner; }
}
