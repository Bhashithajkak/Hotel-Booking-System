package com.example.hotel_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomNumber;
    private String roomType;
    private double roomPrice;
    private String description;

    @Lob
    private Blob photo;

    @OneToMany(mappedBy="room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }
    public void addBooking(Booking booking){
        bookings.add(booking);
        booking.setRoom(this);

    }
}
