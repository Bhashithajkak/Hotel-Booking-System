package com.example.hotel_booking.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;

import java.sql.Blob;
import java.util.List;
@Setter
@Builder
public class RoomResponse {
    private Long roomNumber;
    private String roomType;
    private double roomPrice;
    private String roomDescription;
    private String photo;
    private List<BookingResponse> bookings;

    public RoomResponse(Long roomNumber, String roomType, double roomPrice, String roomDescription,
                        String photoBytes , List<BookingResponse> bookings) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.roomDescription= roomDescription;
        this.photo = photoBytes;
        this.bookings = bookings;
    }

}
