package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.MovieItem;
import com.fu.cinema.desktop.model.PageResult;
import com.fu.cinema.desktop.model.RoomItem;
import com.fu.cinema.desktop.model.SeatMapData;
import com.fu.cinema.desktop.model.ShowtimeRow;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.SeatMap;
import com.fu.cinema.dto.ShowtimeForm;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.RoomService;
import com.fu.cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShowtimeFacade {

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final DesktopSession session;

    public PageResult<ShowtimeRow> search(Long movieId, Long roomId, LocalDate date, int page) {
        return PageResult.of(showtimeService.search(movieId, roomId, date, page, 10), s -> new ShowtimeRow(
                s.getShowtimeId(), s.getMovie().getTitle(), s.getRoom().getRoomName(),
                s.getStartTime(), s.getEndTime(), s.getTicketPrice(), s.getStatus()));
    }

    public List<MovieItem> schedulableMovies() { // không có phim STOPPED (BR-07)
        return movieService.findSchedulable().stream()
                .map(m -> new MovieItem(m.getMovieId(), m.getTitle(), m.getDuration(), m.getAgeRating()))
                .toList();
    }

    public List<RoomItem> activeRooms() { // không có phòng MAINTENANCE
        return roomService.findActive().stream()
                .map(r -> new RoomItem(r.getRoomId(), r.getRoomName()))
                .toList();
    }

    public ShowtimeForm getForm(Long id) {
        return showtimeService.getForm(id);
    }

    public SeatMapData seatMap(Long showtimeId) {
        SeatMap m = showtimeService.getSeatMap(showtimeId); // BR-10: suất không OPEN/đã bắt đầu -> BusinessException
        Showtime s = m.getShowtime();
        return new SeatMapData(showtimeId, s.getMovie().getTitle(), s.getRoom().getRoomName(), s.getStartTime(),
                s.getTicketPrice(), List.copyOf(m.getRowLabels()), m.getSeatsPerRow(), Set.copyOf(m.getSoldSeats()));
    }

    @Transactional
    public void save(ShowtimeForm form) {
        session.requireAdmin();
        if (form.getShowtimeId() == null) {
            showtimeService.create(form); // BR-07, BR-08
        } else {
            showtimeService.update(form.getShowtimeId(), form);
        }
    }

    /**
     * @return true = xoá hẳn, false = huỷ suất + huỷ toàn bộ đơn (BR-09)
     */
    @Transactional
    public boolean delete(Long id) {
        session.requireAdmin();
        return showtimeService.delete(id);
    }
}

