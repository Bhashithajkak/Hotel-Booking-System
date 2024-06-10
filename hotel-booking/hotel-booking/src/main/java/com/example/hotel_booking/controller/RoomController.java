package com.example.hotel_booking.controller;

import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.model.Booking;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.response.RoomResponse;
import com.example.hotel_booking.service.BookingService;
import com.example.hotel_booking.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final IRoomService roomService;
    private final BookingService bookingService;

    @PostMapping("/add/new-room")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomNumber") Long roomNumber,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") double roomPrice,
            @RequestParam("roomDescription") String description) throws SQLException, IOException {
        Room savedRoom = roomService.addNewRoom(photo,roomNumber, roomType, roomPrice,description);
        Blob photoBlob =savedRoom.getPhoto();
        RoomResponse response = getRoomResponse(savedRoom);
        if( photoBlob!= null){
            byte[] newPhoto;newPhoto = photoBlob.getBytes(1, (int) photoBlob.length());
            String base64Photo = Base64.encodeBase64String(newPhoto);
            response.setPhoto(base64Photo);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    }
    @DeleteMapping("/delete/room/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId){
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) Long roomNumber,
                                                   @RequestParam(required = false)  String roomType,
                                                   @RequestParam(required = false) double roomPrice,
                                                   @RequestParam(required = false) String description,
                                                   @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {
        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length >0 ? new SerialBlob(photoBytes): null;
        Room theRoom = roomService.updateRoom(roomId, photoBytes,roomNumber, roomType, roomPrice, description);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId){
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return  ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException {
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : availableRooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0){
                String photoBase64 = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                roomResponses.add(roomResponse);
            }
        }
        if(roomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(roomResponses);
        }
    }




//    private RoomResponse getRoomResponse(Room room) {
//        List<Booking> bookings = getAllBookingsByRoomId(room.getId());
//        List<BookingResponse> bookingInfo = bookings
//                .stream()
//                .map(booking -> new BookingResponse(
//                        booking.getCheckInDate(),booking.getCheckOutDate(),
//                         booking.getBookingReferenceCode())).toList();
//        byte[] photoBytes = null;
//        Blob photoBlob = room.getPhoto();
//        if (photoBlob != null) {
//            try {
//                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
//            } catch (SQLException e) {
//                throw new ImageRetrievalException("Error while retrieving Image");
//            }
//        }
//        return new RoomResponse(room.getId(),
//                room.getRoomType(), room.getRoomPrice(),
//                photoBytes, bookingInfo);
//    }
    private  RoomResponse getRoomResponse(Room room){
//        byte[] photoBytes = null;
//        Blob photoBlob = room.getPhoto();
//        if(photoBlob != null){
//            try{
//                photoBytes = photoBlob.getBytes(1,(int) photoBlob.length());
//            }catch(SQLException e){
//                throw new ImageRetrievalException("Error while retrieving Image");
//            }
//        }
        return RoomResponse.builder()
                .roomType(room.getRoomType())
                .roomNumber(room.getRoomNumber())
                .roomPrice(room.getRoomPrice())
                .roomDescription(room.getDescription())
                .build();

    }
    private List<Booking> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);

    }

}

