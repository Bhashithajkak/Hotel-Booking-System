package com.example.hotel_booking.repository;

import com.example.hotel_booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomId(Long roomId);
    Optional<Booking> findByBookingReferenceCode(String bookingReferenceCode);
    List<Booking> findByGuestEmail(String email);
}
