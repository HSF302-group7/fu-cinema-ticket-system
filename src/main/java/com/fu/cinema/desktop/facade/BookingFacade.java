package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.BookingDetail;
import com.fu.cinema.desktop.model.BookingRow;
import com.fu.cinema.desktop.model.PageResult;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.entity.Ticket;
import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Facade xử lý nghiệp vụ Quản lý đơn đặt vé trên Desktop.
 * Đảm bảo chuyển đổi Entity sang Record bên trong transaction để chống LazyInitializationException.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingFacade {

    private final BookingService bookingService;
    private final DesktopSession session;

    public PageResult<BookingRow> search(BookingStatus status, LocalDate from, LocalDate to, String keyword, int page) {
        SessionUser current = session.getUser();
        if (current != null && current.isCustomer() && current.getCustomerId() != null) {
            String customerKeyword = current.getDisplayName() != null ? current.getDisplayName() : keyword;
            return PageResult.of(bookingService.search(status, from, to, customerKeyword, page, 15), BookingRow::from);
        }
        return PageResult.of(bookingService.search(status, from, to, keyword, page, 15), BookingRow::from);
    }

    public BookingDetail detail(Long id) {
        Booking b = bookingService.getDetail(id); // Gọi truy vấn Q5 JOIN FETCH
        Showtime s = b.getShowtime();
        List<String> seats = b.getTickets() != null
                ? b.getTickets().stream().map(Ticket::getSeatCode).toList()
                : List.of();

        return new BookingDetail(
                b.getBookingId(),
                b.getCustomer() != null ? b.getCustomer().getFullName() : "N/A",
                b.getCustomer() != null ? b.getCustomer().getMobile() : "N/A",
                s != null && s.getMovie() != null ? s.getMovie().getTitle() : "N/A",
                s != null && s.getRoom() != null ? s.getRoom().getRoomName() : "N/A",
                s != null ? s.getStartTime() : null,
                seats,
                b.getTotalAmount(),
                b.getStatus(),
                b.getBookingDate()
        );
    }

    @Transactional
    public void confirm(Long id) {
        session.requireAdmin();
        bookingService.confirm(id); // Tuân thủ BR-13
    }

    @Transactional
    public void cancel(Long id) {
        SessionUser current = session.getUser();
        if (current != null && current.isCustomer()) {
            Long customerId = current.getCustomerId() != null ? current.getCustomerId() : 1L;
            bookingService.cancelByCustomer(id, customerId);
        } else {
            session.requireAdmin();
            bookingService.cancelByAdmin(id); // Tuân thủ BR-13
        }
    }
}

