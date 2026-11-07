package com.fu.cinema.desktop.model;

import java.util.List;

/**
 * Record đóng gói toàn bộ dữ liệu hiển thị trên Dashboard.
 */
public record DashboardData(
        long showingMovies,
        long customers,
        long pendingBookings,
        List<RoomCount> todayByRoom
) {}

