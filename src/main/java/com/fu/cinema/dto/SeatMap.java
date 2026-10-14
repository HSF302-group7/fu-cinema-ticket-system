package com.fu.cinema.dto;

import com.fu.cinema.entity.Showtime;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class SeatMap {

    Showtime showtime;
    List<String> rowLabels;
    int seatsPerRow;
    Set<String> soldSeats;

    public boolean isSold(String seatCode) {
        return soldSeats.contains(seatCode);
    }
}
