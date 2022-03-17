package com.plytus.plytus.model;

import javax.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String name;

    @Column(name = "owned_by", nullable = false, updatable = false)
    private User owner;

    public Category(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public User getOwner() { return owner; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setOwner(User owner) { this.owner = owner; }
}
