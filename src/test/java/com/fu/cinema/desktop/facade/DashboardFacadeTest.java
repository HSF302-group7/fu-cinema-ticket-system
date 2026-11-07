package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.DashboardData;
import com.fu.cinema.dto.RoomShowtimeCount;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ShowtimeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardFacadeTest {

    @Mock
    private MovieService movieService;

    @Mock
    private CustomerService customerService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ShowtimeService showtimeService;

    @InjectMocks
    private DashboardFacade dashboardFacade;

    @Test
    @DisplayName("DashboardFacade: Nạp số liệu tổng quan và thống kê suất chiếu hôm nay theo phòng thành công")
    void testLoadDashboardDataSuccess() {
        when(movieService.countShowing()).thenReturn(5L);
        when(customerService.count()).thenReturn(120L);
        when(bookingService.countPending()).thenReturn(3L);

        RoomShowtimeCount room1 = mock(RoomShowtimeCount.class);
        when(room1.getRoomName()).thenReturn("Room 01");
        when(room1.getShowtimeCount()).thenReturn(4L);

        RoomShowtimeCount room2 = mock(RoomShowtimeCount.class);
        when(room2.getRoomName()).thenReturn("Room 02");
        when(room2.getShowtimeCount()).thenReturn(2L);

        when(showtimeService.countTodayByRoom()).thenReturn(List.of(room1, room2));

        DashboardData data = dashboardFacade.load();

        assertNotNull(data);
        assertEquals(5L, data.showingMovies());
        assertEquals(120L, data.customers());
        assertEquals(3L, data.pendingBookings());
        assertEquals(2, data.todayByRoom().size());

        assertEquals("Room 01", data.todayByRoom().get(0).roomName());
        assertEquals(4L, data.todayByRoom().get(0).count());

        assertEquals("Room 02", data.todayByRoom().get(1).roomName());
        assertEquals(2L, data.todayByRoom().get(1).count());

        verify(movieService).countShowing();
        verify(customerService).count();
        verify(bookingService).countPending();
        verify(showtimeService).countTodayByRoom();
    }

    @Test
    @DisplayName("DashboardFacade: Xử lý an toàn khi hôm nay chưa có suất chiếu nào")
    void testLoadDashboardDataEmptyRooms() {
        when(movieService.countShowing()).thenReturn(0L);
        when(customerService.count()).thenReturn(0L);
        when(bookingService.countPending()).thenReturn(0L);
        when(showtimeService.countTodayByRoom()).thenReturn(Collections.emptyList());

        DashboardData data = dashboardFacade.load();

        assertNotNull(data);
        assertEquals(0L, data.showingMovies());
        assertEquals(0L, data.customers());
        assertEquals(0L, data.pendingBookings());
        assertTrue(data.todayByRoom().isEmpty());
    }
}

