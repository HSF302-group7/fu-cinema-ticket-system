package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.ReportData;
import com.fu.cinema.dto.MovieRevenueDTO;
import com.fu.cinema.dto.ReportResult;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportFacadeTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportFacade reportFacade;

    @Test
    @DisplayName("ReportFacade: Tạo báo cáo doanh thu thành công và chuyển đổi Entity sang Record an toàn")
    void testGenerateReportSuccess() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);

        MovieRevenueDTO movie1 = new MovieRevenueDTO("Huyền Thoại Cũ", 5L, new BigDecimal("350000"));
        MovieRevenueDTO movie2 = new MovieRevenueDTO("Chú Mèo Phiêu Lưu", 3L, new BigDecimal("210000"));

        Customer customer = new Customer();
        customer.setFullName("Nguyen Van An");

        Movie movie = new Movie();
        movie.setTitle("Huyền Thoại Cũ");

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.of(2026, 10, 9, 10, 30));
        booking.setTotalAmount(new BigDecimal("140000"));

        ReportResult mockResult = new ReportResult(
                start,
                end,
                List.of(movie1, movie2),
                List.of(booking),
                1L,
                8L,
                new BigDecimal("560000")
        );

        when(reportService.generate(start, end)).thenReturn(mockResult);

        ReportData data = reportFacade.generate(start, end);

        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertEquals(start, data.start());
        assertEquals(end, data.end());
        assertEquals(2, data.revenue().size());
        assertEquals("Huyền Thoại Cũ", data.revenue().get(0).title());
        assertEquals(5L, data.revenue().get(0).tickets());
        assertEquals(new BigDecimal("350000"), data.revenue().get(0).revenue());

        assertEquals(1, data.bookings().size());
        assertEquals(1L, data.bookings().get(0).id());
        assertEquals("Nguyen Van An", data.bookings().get(0).customerName());
        assertEquals("Huyền Thoại Cũ", data.bookings().get(0).movieTitle());
        assertEquals(new BigDecimal("140000"), data.bookings().get(0).total());

        assertEquals(1L, data.totalBookings());
        assertEquals(8L, data.totalTickets());
        assertEquals(new BigDecimal("560000"), data.totalRevenue());

        verify(reportService).generate(start, end);
    }

    @Test
    @DisplayName("ReportFacade: Xử lý an toàn khi kỳ báo cáo không có dữ liệu (isEmpty = true)")
    void testGenerateReportEmpty() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 15);

        ReportResult emptyResult = new ReportResult(
                start,
                end,
                Collections.emptyList(),
                Collections.emptyList(),
                0L,
                0L,
                BigDecimal.ZERO
        );

        when(reportService.generate(start, end)).thenReturn(emptyResult);

        ReportData data = reportFacade.generate(start, end);

        assertNotNull(data);
        assertTrue(data.isEmpty());
        assertEquals(0, data.revenue().size());
        assertEquals(0, data.bookings().size());
        assertEquals(0L, data.totalBookings());
        assertEquals(0L, data.totalTickets());
        assertEquals(BigDecimal.ZERO, data.totalRevenue());
    }

    @Test
    @DisplayName("ReportFacade: Ném BusinessException khi ngày bắt đầu sau ngày kết thúc (BR-15)")
    void testGenerateReportInvalidDatesThrows() {
        LocalDate start = LocalDate.of(2026, 10, 31);
        LocalDate end = LocalDate.of(2026, 10, 1);

        when(reportService.generate(start, end))
                .thenThrow(new BusinessException("Từ ngày phải trước hoặc bằng đến ngày"));

        BusinessException ex = assertThrows(BusinessException.class, () -> reportFacade.generate(start, end));
        assertTrue(ex.getMessage().contains("Từ ngày phải trước hoặc bằng đến ngày"));
    }
}

