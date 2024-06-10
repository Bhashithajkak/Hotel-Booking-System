package com.example.hotel_booking.service;

import com.example.hotel_booking.model.Booking;
import com.example.hotel_booking.request.BookingRequest;
import com.example.hotel_booking.response.BookingResponse;

import java.util.List;

public interface IBookingService {
    void cancelBooking(Long bookingId);

    List<Booking> getAllBookingsByRoomId(Long roomId);

    BookingResponse saveBooking(Long roomId, BookingRequest bookingRequest);

    BookingResponse findByBookingReferenceCode(String confirmationCode);

    List<BookingResponse> getAllBookings();

    List<BookingResponse> getBookingsByUserEmail(String email);
}
