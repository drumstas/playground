package reactive.event.sourcing.cinema.domain;

public class ShowCommandGenerators {

    public static ShowCommand.ReserveSeat randomReserveSeat(ShowId showId) {
        return new ShowCommand.ReserveSeat(showId, DomainGenerators.randomSeatNumber());
    }

    public static ShowCommand.CancelSeatReservation randomCancelSeatReservation(ShowId showId) {
        return new ShowCommand.CancelSeatReservation(showId, DomainGenerators.randomSeatNumber());
    }
}
