package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.DashboardStats;
import com.fu.cinema.dto.RoomShowtimeCount;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final MovieService movieService;
    private final CustomerService customerService;
    private final BookingService bookingService;
    private final ShowtimeService showtimeService;

    public AdminDashboardController(
            @Autowired(required = false) MovieService movieService,
            CustomerService customerService,
            BookingService bookingService,
            @Autowired(required = false) ShowtimeService showtimeService) {
        this.movieService = movieService;
        this.customerService = customerService;
        this.bookingService = bookingService;
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public String dashboard(Model model) {
        long showing = movieService != null ? movieService.countShowing() : 0L;
        long customers = customerService != null ? customerService.count() : 0L;
        long pending = bookingService != null ? bookingService.countPending() : 0L;
        List<RoomShowtimeCount> todayRooms = showtimeService != null ? showtimeService.countTodayByRoom() : List.of();

        model.addAttribute("stats", new DashboardStats(showing, customers, pending, todayRooms));
        return "admin/dashboard";
    }
}
