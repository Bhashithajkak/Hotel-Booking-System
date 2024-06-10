package com.example.hotel_booking.model;

import com.example.hotel_booking.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private String password;




}

