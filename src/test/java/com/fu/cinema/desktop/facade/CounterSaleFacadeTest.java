package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.CustomerItem;
import com.fu.cinema.desktop.model.MovieItem;
import com.fu.cinema.desktop.model.SaleResult;
import com.fu.cinema.desktop.model.ShowtimeItem;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.BookingRequest;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Room;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterSaleFacadeTest {

    @Mock private CustomerService customerService;
    @Mock private MovieService movieService;
    @Mock private ShowtimeService showtimeService;
    @Mock private BookingService bookingService;
    @Mock private DesktopSession session;

    private CounterSaleFacade facade;

    @BeforeEach
    void setUp() {
        facade = new CounterSaleFacade(customerService, movieService, showtimeService, bookingService, session);
    }

    @Test
    @DisplayName("Tìm kiếm khách hàng: Map từ Customer Entity sang CustomerItem record")
    void testSearchCustomers() {
        Customer c = new Customer();
        c.setCustomerId(10L);
        c.setFullName("Nguyễn Văn An");
        c.setMobile("0912345678");
        c.setBirthday(LocalDate.now().minusYears(20));

        when(customerService.search(eq("An"), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(c)));

        List<CustomerItem> items = facade.searchCustomers("An");
        assertEquals(1, items.size());
        assertEquals("Nguyễn Văn An", items.get(0).fullName());
        assertEquals(20, items.get(0).age());
    }

    @Test
    @DisplayName("Bán vé chưa thanh toán: Đơn vé ở trạng thái PENDING")
    void testSellUnpaid() {
        Booking booking = new Booking();
        booking.setBookingId(101L);
        booking.setTotalAmount(BigDecimal.valueOf(180000));
        booking.setStatus(BookingStatus.PENDING);

        when(bookingService.create(eq(10L), any(BookingRequest.class))).thenReturn(booking);

        SaleResult result = facade.sell(10L, 5L, List.of("A2", "A1"), false);

        assertNotNull(result);
        assertEquals(101L, result.bookingId());
        assertEquals(BookingStatus.PENDING, result.status());
        assertEquals(List.of("A1", "A2"), result.seats()); // Đã sắp xếp theo thứ tự ghế
        verify(bookingService, never()).confirm(anyLong());
    }

    @Test
    @DisplayName("Bán vé đã thanh toán (BR-D2): Tự động confirm() chuyển trạng thái sang CONFIRMED")
    void testSellPaidBR_D2() {
        Booking booking = new Booking();
        booking.setBookingId(202L);
        booking.setTotalAmount(BigDecimal.valueOf(90000));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingService.create(eq(10L), any(BookingRequest.class))).thenReturn(booking);

        SaleResult result = facade.sell(10L, 5L, List.of("B5"), true);

        assertNotNull(result);
        assertEquals(202L, result.bookingId());
        assertEquals(BookingStatus.CONFIRMED, result.status());
        verify(bookingService).confirm(202L); // Xác thực đã gọi confirm() ngay trong transaction
    }

    @Test
    @DisplayName("Sắp xếp mã ghế: Thứ tự tự nhiên A1 < A2 < A10 < B1")
    void testSeatOrderComparator() {
        List<String> seats = List.of("B1", "A10", "A2", "A1");
        List<String> sorted = seats.stream().sorted(CounterSaleFacade.SEAT_ORDER).toList();

        assertEquals(List.of("A1", "A2", "A10", "B1"), sorted);
    }
}

