package com.plytus.plytus.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "TGuser")
public class User {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_tg_id", nullable = false, updatable = false, unique = true)
    private long tg_id;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Category> categories;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Expense> expenses;


    public User(long tg_id) {
        this.tg_id = tg_id;
    }

    public User() { super(); }

    public Long getId() { return id; }
    public long getTg_id() { return tg_id; }

    public void setId(Long id) { this.id = id; }
    public void setTg_id(long tg_id) { this.tg_id = tg_id; }

    public Set<Category> getCategories() { return categories; }
    public Set<Expense> getExpenses() { return expenses; }
}
