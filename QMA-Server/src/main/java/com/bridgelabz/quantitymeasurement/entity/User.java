package com.bridgelabz.quantitymeasurement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String googleId;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuantityRecord> conversionHistory;
}