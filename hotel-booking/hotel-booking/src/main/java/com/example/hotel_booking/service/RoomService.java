package com.example.hotel_booking.service;

import com.example.hotel_booking.exception.InternalServerException;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    @Override
    public Room addNewRoom(MultipartFile file,Long roomNumber, String roomType, double roomPrice,String description) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        room.setDescription(description);

        if (!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> room = roomRepository.findById(roomId);
        if(room.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }
        Blob photoBlob = room.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if(room.isPresent()){
            roomRepository.deleteById(roomId);
        }
    }

    @Override
    public Room updateRoom(Long roomId,byte[] photoBytes,Long roomNumber, String roomType, double roomPrice, String description) {
        Room room = roomRepository.findById(roomId).get();
        if(roomNumber!=0) room.setRoomNumber(roomNumber);
        if (!roomType.isEmpty()) room.setRoomType(roomType);
        room.setDescription(description);
        if (roomPrice != 0) room.setRoomPrice(roomPrice);
        if (photoBytes != null && photoBytes.length > 0) {
            try {
                room.setPhoto(new SerialBlob(photoBytes));
            } catch (SQLException e) {
                throw new InternalServerException("Fail updating image");
            }
        }
        roomRepository.save(room);
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }
}

