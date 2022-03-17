package com.plytus.plytus.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_tg_id", nullable = false, updatable = false, unique = true)
    private long tg_id;

    public User(long tg_id) {
        this.tg_id = tg_id;
    }

    public Long getId() { return id; }
    public long getTg_id() { return tg_id; }

    public void setId(Long id) { this.id = id; }
    public void setTg_id(long tg_id) { this.tg_id = tg_id; }
}
