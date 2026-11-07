package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.MovieItem;
import com.fu.cinema.desktop.model.PageResult;
import com.fu.cinema.desktop.model.RoomItem;
import com.fu.cinema.desktop.model.RoomRow;
import com.fu.cinema.desktop.model.ShowtimeRow;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.RoomForm;
import com.fu.cinema.dto.ShowtimeForm;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Room;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.Role;
import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.RoomType;
import com.fu.cinema.enums.ShowtimeStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.RoomService;
import com.fu.cinema.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleFacadeTest {

    @Mock
    private RoomService roomService;

    @Mock
    private ShowtimeService showtimeService;

    @Mock
    private MovieService movieService;

    private DesktopSession session;
    private RoomFacade roomFacade;
    private ShowtimeFacade showtimeFacade;

    @BeforeEach
    void setUp() {
        session = new DesktopSession();
        roomFacade = new RoomFacade(roomService, session);
        showtimeFacade = new ShowtimeFacade(showtimeService, movieService, roomService, session);
    }

    private void loginAsAdmin() {
        session.login(new SessionUser(1L, null, "Admin User", Role.ADMIN));
    }

    @Test
    @DisplayName("RoomFacade: findAll chuyển đổi đúng Entity Room sang record RoomRow")
    void testRoomFacadeFindAll() {
        Room r = new Room("Cinema 1", RoomType.TWO_D, 8, 12, RoomStatus.ACTIVE);
        r.setRoomId(10L);
        when(roomService.findAll()).thenReturn(List.of(r));

        List<RoomRow> rows = roomFacade.findAll();
        assertEquals(1, rows.size());
        assertEquals(10L, rows.get(0).id());
        assertEquals("Cinema 1", rows.get(0).name());
        assertEquals(96, rows.get(0).capacity());
    }

    @Test
    @DisplayName("RoomFacade: save ném lỗi khi chưa đăng nhập Admin")
    void testRoomFacadeSaveRequiresAdmin() {
        session.logout();
        RoomForm form = new RoomForm();
        assertThrows(BusinessException.class, () -> roomFacade.save(form));
    }

    @Test
    @DisplayName("RoomFacade: save thành công khi đã đăng nhập Admin")
    void testRoomFacadeSaveSuccess() {
        loginAsAdmin();
        RoomForm form = new RoomForm();
        form.setRoomName("Cinema 2");
        form.setRoomType(RoomType.IMAX);
        form.setTotalRows(10);
        form.setSeatsPerRow(15);
        form.setStatus(RoomStatus.ACTIVE);

        assertDoesNotThrow(() -> roomFacade.save(form));
        verify(roomService).create(form);
    }

    @Test
    @DisplayName("ShowtimeFacade: schedulableMovies lấy phim chưa STOPPED (BR-07)")
    void testSchedulableMovies() {
        Movie m = new Movie();
        m.setMovieId(1L);
        m.setTitle("Avatar");
        m.setDuration(192);
        m.setAgeRating(AgeRating.T13);
        when(movieService.findSchedulable()).thenReturn(List.of(m));

        List<MovieItem> items = showtimeFacade.schedulableMovies();
        assertEquals(1, items.size());
        assertEquals("Avatar (192')", items.get(0).toString());
    }

    @Test
    @DisplayName("ShowtimeFacade: search trả về PageResult<ShowtimeRow> đúng format")
    void testSearchShowtimes() {
        Movie m = new Movie();
        m.setTitle("Dune 2");
        Room r = new Room("Cinema 1", RoomType.IMAX, 10, 10, RoomStatus.ACTIVE);

        Showtime s = new Showtime();
        s.setShowtimeId(100L);
        s.setMovie(m);
        s.setRoom(r);
        s.setStartTime(LocalDateTime.of(2026, 11, 10, 18, 0));
        s.setEndTime(LocalDateTime.of(2026, 11, 10, 20, 45));
        s.setTicketPrice(BigDecimal.valueOf(90000));
        s.setStatus(ShowtimeStatus.OPEN);

        when(showtimeService.search(eq(1L), eq(2L), any(), eq(0), eq(10)))
                .thenReturn(new PageImpl<>(List.of(s), PageRequest.of(0, 10), 1));

        PageResult<ShowtimeRow> result = showtimeFacade.search(1L, 2L, LocalDate.of(2026, 11, 10), 0);
        assertEquals(1, result.items().size());
        ShowtimeRow row = result.items().get(0);
        assertEquals(100L, row.id());
        assertEquals("Dune 2", row.movieTitle());
        assertEquals("Cinema 1", row.roomName());
        assertEquals(BigDecimal.valueOf(90000), row.price());
    }

    @Test
    @DisplayName("ShowtimeFacade (BR-09): Xoá suất chiếu yêu cầu quyền Admin")
    void testDeleteShowtimeRequiresAdmin() {
        session.logout();
        assertThrows(BusinessException.class, () -> showtimeFacade.delete(100L));
    }

    @Test
    @DisplayName("ShowtimeFacade (BR-09): Xoá suất chiếu thành công khi là Admin")
    void testDeleteShowtimeSuccess() {
        loginAsAdmin();
        when(showtimeService.delete(100L)).thenReturn(true);

        boolean deleted = showtimeFacade.delete(100L);
        assertTrue(deleted);
        verify(showtimeService).delete(100L);
    }
}

