package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUser")
    private Integer idUser;
    
    @Column(name = "userName", nullable = false, unique = true, length = 255)
    private String userName;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "token", length = 500)
    private String token;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "createdAt")
    private Long createdAt;
    
    @Column(name = "updatedAt")
    private Long updatedAt;
}
