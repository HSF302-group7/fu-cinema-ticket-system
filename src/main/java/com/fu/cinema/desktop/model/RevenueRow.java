package com.fu.cinema.desktop.model;

import java.math.BigDecimal;

/**
 * Record đại diện cho một dòng doanh thu theo phim.
 */
public record RevenueRow(String title, long tickets, BigDecimal revenue) {}

