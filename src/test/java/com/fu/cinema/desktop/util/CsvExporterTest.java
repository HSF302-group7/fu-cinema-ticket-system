package com.fu.cinema.desktop.util;

import com.fu.cinema.desktop.model.ReportBookingRow;
import com.fu.cinema.desktop.model.ReportData;
import com.fu.cinema.desktop.model.RevenueRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("CsvExporter: Xuất file CSV có UTF-8 BOM, đúng định dạng và escape ký tự đặc biệt")
    void testExportCsv() throws IOException {
        Path csvFile = tempDir.resolve("test_revenue.csv");

        ReportData data = new ReportData(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                List.of(
                        new RevenueRow("Phim Có Dấu Phẩy, Và Kép \"Vip\"", 2L, new BigDecimal("200000")),
                        new RevenueRow("Phim Thường", 1L, new BigDecimal("80000"))
                ),
                List.of(
                        new ReportBookingRow(
                                101L,
                                LocalDateTime.of(2026, 10, 15, 14, 30),
                                "Nguyen Van An",
                                "Phim Có Dấu Phẩy, Và Kép \"Vip\"",
                                new BigDecimal("200000")
                        )
                ),
                1L,
                3L,
                new BigDecimal("280000")
        );

        CsvExporter.write(csvFile, data);

        assertTrue(Files.exists(csvFile));
        String content = Files.readString(csvFile, StandardCharsets.UTF_8);

        // Kiểm tra UTF-8 BOM
        assertTrue(content.startsWith("\uFEFF"), "File CSV phải bắt đầu bằng UTF-8 BOM (\\uFEFF)");

        // Kiểm tra nội dung header và các dòng dữ liệu
        assertTrue(content.contains("Báo cáo doanh thu,2026-10-01,2026-10-31"));
        assertTrue(content.contains("Phim,Số vé,Doanh thu"));
        assertTrue(content.contains("\"Phim Có Dấu Phẩy, Và Kép \"\"Vip\"\"\",2,200000"));
        assertTrue(content.contains("Phim Thường,1,80000"));
        assertTrue(content.contains("Mã đơn,Ngày đặt,Khách hàng,Phim,Tổng tiền"));
        assertTrue(content.contains("101,2026-10-15T14:30,Nguyen Van An,\"Phim Có Dấu Phẩy, Và Kép \"\"Vip\"\"\",200000"));
        assertTrue(content.contains("TỔNG,1 đơn,3 vé,,280000"));
    }
}

