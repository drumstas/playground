package reactive.event.sourcing.cinema.domain;

import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.math.BigDecimal;

final class SeatsCreator {

    static final int SEAT_RANGE = 10;

    static Map<SeatNumber, Seat> createSeats(BigDecimal price) {
        return List.rangeClosed(1, 10).toMap(
                SeatNumber::new,
                seatNumber -> new Seat(new SeatNumber(seatNumber), SeatStatus.AVAILABLE, price)
        );
    }
}
