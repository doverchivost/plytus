package com.plytus.plytus.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owned_by")
    private User owner;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Expense> expenses;

    public Category(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public Category() {super(); }

    public Long getId() { return id; }
    public String getName() { return name; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    public Set<Expense> getExpenses() { return expenses; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}
