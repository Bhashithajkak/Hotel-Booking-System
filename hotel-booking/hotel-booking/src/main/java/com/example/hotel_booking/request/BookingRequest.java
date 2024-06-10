package com.example.hotel_booking.request;

import com.example.hotel_booking.response.RoomResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
public record BookingRequest(
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate,
        @NotNull String guestName,
        @NotNull String guestEmail,
        @NotNull String guestContactNumber,
        @NotNull int numOfAdults,
        @NotNull int numOfChildren,
        @NotNull int totalNumOfGuests,
        @NotNull Long roomId
) {

}
