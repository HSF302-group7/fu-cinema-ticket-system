package com.fu.cinema.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record đại diện cho một đơn hàng đã xác nhận hiển thị trong bảng báo cáo.
 */
public record ReportBookingRow(
        Long id,
        LocalDateTime bookingDate,
        String customerName,
        String movieTitle,
        BigDecimal total
) {}

