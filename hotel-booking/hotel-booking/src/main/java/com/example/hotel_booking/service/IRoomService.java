package com.example.hotel_booking.service;

import com.example.hotel_booking.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomService {
    Room addNewRoom(MultipartFile photo,Long roomNumber, String roomType, double roomPrice, String description) throws SQLException, IOException;

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId,byte[] photoBytes, Long roomNumber, String roomType, double roomPrice, String description );

    Optional<Room> getRoomById(Long roomId);

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}

