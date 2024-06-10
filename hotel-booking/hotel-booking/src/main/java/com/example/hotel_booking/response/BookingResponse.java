package com.example.hotel_booking.response;

import com.example.hotel_booking.model.Room;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
@Builder
public record BookingResponse (
       @NotNull LocalDate checkInDate,
       @NotNull LocalDate checkOutDate,
       @NotNull LocalDate createdDate,
       @NotNull String guestName,
       @NotNull String guestEmail,
       @NotNull String guestContactNumber,
       @NotNull int numOfAdults,
       @NotNull int numOfChildren,
       @NotNull int totalNumOfGuests,
       @NotNull String bookingReferenceCode,
       @NotNull RoomResponse roomResponse){}


