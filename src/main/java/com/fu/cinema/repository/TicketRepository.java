package com.fu.cinema.repository;

import com.fu.cinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            SELECT t.seatCode FROM Ticket t
            WHERE t.showtime.showtimeId = :showtimeId
              AND t.booking.status <> com.fu.cinema.enums.BookingStatus.CANCELLED
            """)
    List<String> findSoldSeatCodes(@Param("showtimeId") Long showtimeId);
}
