package com.example.hotel_booking.model;

import com.example.hotel_booking.response.RoomResponse;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate createdDate;
    private String guestFullName;
    private String guestEmail;
    private String guestContactNumber;
    private int numOfAdults;
    private int numOfChildren;
    private int totalNumOfGuest;
    private String bookingReferenceCode;
    private int bookingPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateBill(){
       Long noOfDays= this.checkOutDate.toEpochDay() - this.checkInDate.toEpochDay();
        this.bookingPrice = (BigDecimal.valueOf(room.getRoomPrice()).multiply(BigDecimal.valueOf(noOfDays))).intValue();
    }

    public void generateBookingReferenceCode(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String bookingCode = RandomStringUtils.randomNumeric(6)+timestamp;
        this.setBookingReferenceCode(bookingCode);
    }

    public void calculateTotalNumOfGuest(){
        this.setTotalNumOfGuest(this.numOfAdults+this.numOfChildren);
    }



}
