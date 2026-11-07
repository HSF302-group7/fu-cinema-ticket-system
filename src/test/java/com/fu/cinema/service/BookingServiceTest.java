package com.fu.cinema.service;

import com.fu.cinema.dto.BookingRequest;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Room;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.enums.MovieStatus;
import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.RoomType;
import com.fu.cinema.enums.ShowtimeStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.repository.BookingRepository;
import com.fu.cinema.repository.CustomerRepository;
import com.fu.cinema.repository.ShowtimeRepository;
import com.fu.cinema.repository.TicketRepository;
import com.fu.cinema.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TicketRepository ticketRepository;

    private BookingServiceImpl bookingService;

    private Showtime sampleShowtime;
    private Customer sampleCustomer;
    private Room sampleRoom;
    private Movie sampleMovie;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, showtimeRepository, customerRepository, ticketRepository);

        sampleRoom = new Room();
        sampleRoom.setRoomId(1L);
        sampleRoom.setRoomName("Room 01");
        sampleRoom.setRoomType(RoomType.TWO_D);
        sampleRoom.setTotalRows(8);
        sampleRoom.setSeatsPerRow(10);
        sampleRoom.setStatus(RoomStatus.ACTIVE);

        sampleMovie = new Movie();
        sampleMovie.setMovieId(1L);
        sampleMovie.setTitle("Test Movie");
        sampleMovie.setDuration(120);
        sampleMovie.setAgeRating(AgeRating.T13);
        sampleMovie.setStatus(MovieStatus.SHOWING);

        sampleShowtime = new Showtime();
        sampleShowtime.setShowtimeId(1L);
        sampleShowtime.setRoom(sampleRoom);
        sampleShowtime.setMovie(sampleMovie);
        sampleShowtime.setStartTime(LocalDateTime.now().plusDays(1));
        sampleShowtime.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        sampleShowtime.setTicketPrice(new BigDecimal("90000"));
        sampleShowtime.setStatus(ShowtimeStatus.OPEN);

        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setFullName("Nguyen Van A");
        sampleCustomer.setBirthday(LocalDate.now().minusYears(20));
    }

    @Test
    void create_validSeats_createsPendingBookingWithTickets() {
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("A1", "A2"));

        when(showtimeRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleShowtime));
        when(ticketRepository.findSoldSeatCodes(1L)).thenReturn(Collections.emptyList());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.create(1L, request);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("180000"), result.getTotalAmount());
        assertEquals(2, result.getTickets().size());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_emptySeats_throwsBusinessException() {
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("Vui lòng chọn ít nhất 1 ghế"));
    }

    @Test
    void create_exceedMaxSeats_throwsBusinessException() {
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("Mỗi đơn tối đa 8 ghế"));
    }

    @Test
    void create_showtimeCancelled_throwsBusinessException() {
        sampleShowtime.setStatus(ShowtimeStatus.CANCELLED);
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("A1"));

        when(showtimeRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleShowtime));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("Suất chiếu không còn mở bán"));
    }

    @Test
    void create_showtimeInPast_throwsBusinessException() {
        sampleShowtime.setStartTime(LocalDateTime.now().minusHours(2));
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("A1"));

        when(showtimeRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleShowtime));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("Suất chiếu không còn mở bán"));
    }

    @Test
    void create_seatNotInRoom_throwsBusinessException() {
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("Z99"));

        when(showtimeRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleShowtime));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("không tồn tại trong phòng"));
    }

    @Test
    void create_seatAlreadySold_throwsBusinessException() {
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("A1"));

        when(showtimeRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleShowtime));
        when(ticketRepository.findSoldSeatCodes(1L)).thenReturn(List.of("A1"));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("Ghế đã có người đặt"));
    }

    @Test
    void create_customerUnderAge_throwsBusinessException() {
        sampleMovie.setAgeRating(AgeRating.T18);
        sampleCustomer.setBirthday(LocalDate.now().minusYears(15));

        BookingRequest request = new BookingRequest();
        request.setShowtimeId(1L);
        request.setSeatCodes(List.of("A1"));

        when(showtimeRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleShowtime));
        when(ticketRepository.findSoldSeatCodes(1L)).thenReturn(Collections.emptyList());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.create(1L, request));
        assertTrue(ex.getMessage().contains("chỉ dành cho khán giả từ 18 tuổi trở lên"));
    }

    @Test
    void cancelByCustomer_withinTwoHours_throwsBusinessException() {
        Booking booking = new Booking();
        booking.setBookingId(10L);
        booking.setCustomer(sampleCustomer);
        booking.setStatus(BookingStatus.CONFIRMED);
        sampleShowtime.setStartTime(LocalDateTime.now().plusMinutes(90));
        booking.setShowtime(sampleShowtime);

        when(bookingRepository.findDetailById(10L)).thenReturn(Optional.of(booking));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.cancelByCustomer(10L, 1L));
        assertTrue(ex.getMessage().contains("Chỉ được huỷ đơn trước giờ chiếu ít nhất 2 giờ"));
    }

    @Test
    void cancelByCustomer_validTime_setsStatusCancelled() {
        Booking booking = new Booking();
        booking.setBookingId(10L);
        booking.setCustomer(sampleCustomer);
        booking.setStatus(BookingStatus.CONFIRMED);
        sampleShowtime.setStartTime(LocalDateTime.now().plusHours(4));
        booking.setShowtime(sampleShowtime);

        when(bookingRepository.findDetailById(10L)).thenReturn(Optional.of(booking));

        bookingService.cancelByCustomer(10L, 1L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void confirm_pendingBooking_setsConfirmed() {
        Booking booking = new Booking();
        booking.setBookingId(10L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findDetailById(10L)).thenReturn(Optional.of(booking));

        bookingService.confirm(10L);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void confirm_nonPendingBooking_throwsBusinessException() {
        Booking booking = new Booking();
        booking.setBookingId(10L);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findDetailById(10L)).thenReturn(Optional.of(booking));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookingService.confirm(10L));
        assertTrue(ex.getMessage().contains("Chỉ xác nhận được đơn đang ở trạng thái PENDING"));
    }
}
