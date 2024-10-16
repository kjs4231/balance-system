package com.example.balancesystem.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
enum Role {
    USER, ADMIN
}