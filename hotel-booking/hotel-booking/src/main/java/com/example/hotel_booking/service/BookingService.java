package com.example.hotel_booking.service;

import com.example.hotel_booking.exception.InvalidBookingRequestException;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.model.Booking;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.repository.BookingRepository;
import com.example.hotel_booking.request.BookingRequest;
import com.example.hotel_booking.response.BookingResponse;
import com.example.hotel_booking.response.RoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final IRoomService roomService;


    @Override
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return bookingResponses;
    }
    @Override
    public List<BookingResponse> getBookingsByUserEmail(String email) {
        List<Booking> bookings = bookingRepository.findByGuestEmail(email);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return bookingResponses;
    }
    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
    @Override
    public List<Booking> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public BookingResponse saveBooking(Long roomId, BookingRequest bookingRequest) {
        if (bookingRequest.checkOutDate().isBefore(bookingRequest.checkInDate())){
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<Booking> existingBookings = room.getBookings();
        boolean isRoomAvailable = isRoomAvailable(bookingRequest,existingBookings);
        if (isRoomAvailable){
            Booking booking = createBooking(bookingRequest,room);
            room.addBooking(booking);
            bookingRepository.save(booking);
            return getBookingResponse(booking);
        }else{
            throw  new InvalidBookingRequestException("Sorry, This room is not available for the selected dates;");
        }

    }

    public Booking createBooking (BookingRequest bookingRequest,Room room){
        Booking booking = Booking.builder()
                .checkInDate(bookingRequest.checkInDate())
                .checkOutDate(bookingRequest.checkOutDate())
                .createdDate(LocalDate.now())
                .guestFullName(bookingRequest.guestName())
                .guestEmail(bookingRequest.guestEmail())
                .guestContactNumber(bookingRequest.guestContactNumber())
                .numOfAdults(bookingRequest.numOfAdults())
                .numOfChildren(bookingRequest.numOfChildren())
                .room(room)
                .build();
        booking.calculateTotalNumOfGuest();
        booking.calculateBill();
        booking.generateBookingReferenceCode();

        return booking;

    }

    @Override
    public BookingResponse findByBookingReferenceCode(String confirmationCode) {
        Booking booking= bookingRepository.findByBookingReferenceCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code :"+confirmationCode));
        return getBookingResponse(booking);
    }


    private boolean isRoomAvailable(BookingRequest bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.checkInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.checkOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.checkInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.checkInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.checkInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.checkOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.checkInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.checkOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.checkInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.checkOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.checkInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.checkOutDate().equals(bookingRequest.checkInDate()))
                );
    }

    private BookingResponse getBookingResponse(Booking booking) {
        Room room = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse roomResponse = RoomResponse.builder()
                .roomType(room.getRoomType())
                .roomNumber(room.getRoomNumber())
                .roomPrice(room.getRoomPrice())
                .roomDescription(room.getDescription())
                .build();
        return BookingResponse.builder()
                .checkInDate(booking.getCheckInDate()).checkOutDate(booking.getCheckOutDate())
                .createdDate(booking.getCreatedDate()).guestName(booking.getGuestFullName())
                .guestEmail(booking.getGuestEmail()).guestContactNumber(booking.getGuestContactNumber())
                .numOfAdults(booking.getNumOfAdults()).numOfChildren(booking.getNumOfChildren())
                .totalNumOfGuests(booking.getTotalNumOfGuest()).bookingReferenceCode(booking.getBookingReferenceCode())
                .roomResponse(roomResponse)
                .build();
    }




}
