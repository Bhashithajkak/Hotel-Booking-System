package com.example.hotel_booking.repository;

import com.example.hotel_booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query("SELECT r FROM Room r " +
            "WHERE r.roomType LIKE %:roomType% " +
            "AND r.id NOT IN (" +
            "SELECT b.room.id FROM Booking b " +
            "WHERE b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)")
    List<Room> findAvailableRoomsByDatesAndType(@Param("checkInDate") LocalDate checkInDate,
                                                @Param("checkOutDate") LocalDate checkOutDate,
                                                @Param("roomType") String roomType);
}
