package reactive.event.sourcing.cinema.domain;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Clock;

import java.util.function.Function;

import static reactive.event.sourcing.cinema.domain.ShowCommandError.*;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public record Show(ShowId id, String title, Map<SeatNumber, Seat> seats) implements Serializable {

    public static final BigDecimal INITIAL_PRICE = new BigDecimal("100");

    public static Show create(ShowId showId) {
        return new Show(showId, "Show title " + showId.id(), SeatsCreator.createSeats(INITIAL_PRICE));
    }

    public Either<ShowCommandError, List<ShowEvent>> process(ShowCommand command, Clock clock) {
        return switch (command) {
            case ShowCommand.ReserveSeat reserveSeat -> handleReservation(reserveSeat, clock);
            case ShowCommand.CancelSeatReservation cancelSeatReservation -> handleCancellation(cancelSeatReservation, clock);
        };
    }

    private Either<ShowCommandError, List<ShowEvent>> handleReservation(ShowCommand.ReserveSeat reserveSeat, Clock clock) {
        SeatNumber seatNumber = reserveSeat.seatNumber();
        return handleSeatCommand(seatNumber, seat -> seat.isAvailable()
                ? right(List.of(new ShowEvent.SeatReserved(id, clock.instant(), seatNumber)))
                : left(SEAT_NOT_AVAILABLE)
        );
    }

    private Either<ShowCommandError, List<ShowEvent>> handleCancellation(ShowCommand.CancelSeatReservation cancelSeatReservation, Clock clock) {
        SeatNumber seatNumber = cancelSeatReservation.seatNumber();
        return handleSeatCommand(seatNumber, seat -> seat.isReserved()
                ? right(List.of(new ShowEvent.SeatReservationCancelled(id, clock.instant(), seatNumber)))
                : left(SEAT_NOT_RESERVED)
        );
    }

    private Either<ShowCommandError, List<ShowEvent>> handleSeatCommand(SeatNumber seatNumber, Function<Seat, Either<ShowCommandError, List<ShowEvent>>> function) {
        return seats.get(seatNumber).map(function).getOrElse(left(SEAT_NOT_EXISTS));
    }

    public Show apply(ShowEvent event) {
        return switch (event) {
            case ShowEvent.SeatReserved seatReserved -> applyReserved(seatReserved);
            case ShowEvent.SeatReservationCancelled seatReservationCancelled ->
                    applyReservationCancelled(seatReservationCancelled);
        };
    }

    private Show applyReserved(ShowEvent.SeatReserved seatReserved) {
        Seat seat = getSeatOrThrow(seatReserved.seatNumber());
        return new Show(id, title, seats.put(seat.number(), seat.reserved()));
    }

    private Show applyReservationCancelled(ShowEvent.SeatReservationCancelled seatReservationCancelled) {
        Seat seat = getSeatOrThrow(seatReservationCancelled.seatNumber());
        return new Show(id, title, seats.put(seat.number(), seat.available()));
    }

    private Seat getSeatOrThrow(SeatNumber seatNumber) {
        return seats.get(seatNumber).getOrElseThrow(() -> new IllegalStateException("Seat not exists %s".formatted(seatNumber)));
    }

}
