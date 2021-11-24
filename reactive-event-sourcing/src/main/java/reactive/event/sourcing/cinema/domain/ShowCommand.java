package reactive.event.sourcing.cinema.domain;

import java.io.Serializable;

public sealed interface ShowCommand extends Serializable {

    ShowId showId();

    record ReserveSeat(ShowId showId, SeatNumber seatNumber) implements ShowCommand { }

    record CancelSeatReservation(ShowId showId, SeatNumber seatNumber) implements ShowCommand { }


}
