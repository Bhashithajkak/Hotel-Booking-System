package com.example.hotel_booking.controller;

import com.example.hotel_booking.exception.InvalidBookingRequestException;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.request.BookingRequest;
import com.example.hotel_booking.response.BookingResponse;
import com.example.hotel_booking.service.IBookingService;
import com.example.hotel_booking.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final IBookingService bookingService;

    @GetMapping("/all-bookings")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookingRequest bookingRequest){
        try{
            BookingResponse saveBooking = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok(saveBooking);

        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookingResponse bookingResponse = bookingService.findByBookingReferenceCode(confirmationCode);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<BookingResponse> bookings = bookingService.getBookingsByUserEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }


}

