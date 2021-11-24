package reactive.event.sourcing.cinema.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public record Seat(SeatNumber number, SeatStatus status, BigDecimal price) implements Serializable {

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == SeatStatus.RESERVED;
    }

    Seat reserved() {
        return new Seat(number, SeatStatus.RESERVED, price);
    }

    Seat available() {
        return new Seat(number, SeatStatus.AVAILABLE, price);
    }

}
