package com.fu.cinema.desktop.model;

import com.fu.cinema.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Record chứa toàn bộ thông tin chi tiết một đơn đặt vé để hiển thị trên Dialog.
 */
public record BookingDetail(
        Long id,
        String customerName,
        String mobile,
        String movieTitle,
        String roomName,
        LocalDateTime startTime,
        List<String> seats,
        BigDecimal total,
        BookingStatus status,
        LocalDateTime bookingDate
) {}

