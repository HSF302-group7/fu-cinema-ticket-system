package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.BookingDetail;
import com.fu.cinema.desktop.model.BookingRow;
import com.fu.cinema.desktop.model.PageResult;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.entity.*;
import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingFacadeTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private DesktopSession session;

    @InjectMocks
    private BookingFacade bookingFacade;

    @Test
    @DisplayName("BookingFacade: Tìm kiếm và phân trang đơn đặt vé chuyển đổi thành công sang PageResult<BookingRow>")
    void testSearchBookings() {
        Customer customer = new Customer();
        customer.setFullName("Nguyen Van An");

        Movie movie = new Movie();
        movie.setTitle("Biệt Đội Siêu Tốc");

        Room room = new Room();
        room.setRoomName("Room 01");

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(LocalDateTime.of(2026, 11, 8, 20, 0));

        Booking booking = new Booking();
        booking.setBookingId(10L);
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setTotalAmount(new BigDecimal("180000"));
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.of(2026, 11, 7, 10, 30));

        Ticket ticket1 = new Ticket();
        ticket1.setSeatCode("A1");
        Ticket ticket2 = new Ticket();
        ticket2.setSeatCode("A2");
        booking.setTickets(List.of(ticket1, ticket2));

        Page<Booking> page = new PageImpl<>(List.of(booking), PageRequest.of(0, 15), 1);
        when(bookingService.search(eq(BookingStatus.PENDING), any(), any(), any(), eq(0), eq(15))).thenReturn(page);

        PageResult<BookingRow> result = bookingFacade.search(BookingStatus.PENDING, LocalDate.now(), LocalDate.now(), "An", 0);

        assertNotNull(result);
        assertEquals(1, result.items().size());
        BookingRow row = result.items().get(0);
        assertEquals(10L, row.id());
        assertEquals("Nguyen Van An", row.customerName());
        assertEquals("Biệt Đội Siêu Tốc", row.movieTitle());
        assertEquals("Room 01", row.roomName());
        assertEquals(2, row.seatCount());
        assertEquals(new BigDecimal("180000"), row.total());
        assertEquals(BookingStatus.PENDING, row.status());
    }

    @Test
    @DisplayName("BookingFacade: Lấy chi tiết đơn đặt vé nạp đầy đủ thông tin vào BookingDetail")
    void testDetailBooking() {
        Customer customer = new Customer();
        customer.setFullName("Tran Thi Binh");
        customer.setMobile("0912345678");

        Movie movie = new Movie();
        movie.setTitle("Chú Mèo Phiêu Lưu");

        Room room = new Room();
        room.setRoomName("Room 02");

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(LocalDateTime.of(2026, 11, 9, 14, 0));

        Booking booking = new Booking();
        booking.setBookingId(25L);
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setTotalAmount(new BigDecimal("160000"));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.of(2026, 11, 7, 15, 0));

        Ticket t1 = new Ticket();
        t1.setSeatCode("B3");
        Ticket t2 = new Ticket();
        t2.setSeatCode("B4");
        booking.setTickets(List.of(t1, t2));

        when(bookingService.getDetail(25L)).thenReturn(booking);

        BookingDetail detail = bookingFacade.detail(25L);

        assertNotNull(detail);
        assertEquals(25L, detail.id());
        assertEquals("Tran Thi Binh", detail.customerName());
        assertEquals("0912345678", detail.mobile());
        assertEquals("Chú Mèo Phiêu Lưu", detail.movieTitle());
        assertEquals("Room 02", detail.roomName());
        assertEquals(List.of("B3", "B4"), detail.seats());
        assertEquals(BookingStatus.CONFIRMED, detail.status());
    }

    @Test
    @DisplayName("BookingFacade: Xác nhận đơn yêu cầu quyền Admin và gọi BookingService.confirm()")
    void testConfirmBooking() {
        bookingFacade.confirm(10L);

        verify(session).requireAdmin();
        verify(bookingService).confirm(10L);
    }

    @Test
    @DisplayName("BookingFacade: Huỷ đơn yêu cầu quyền Admin và gọi BookingService.cancelByAdmin()")
    void testCancelBooking() {
        bookingFacade.cancel(10L);

        verify(session).requireAdmin();
        verify(bookingService).cancelByAdmin(10L);
    }
}

