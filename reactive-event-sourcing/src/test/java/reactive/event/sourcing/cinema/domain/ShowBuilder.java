package reactive.event.sourcing.cinema.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public class ShowBuilder {

    private ShowId id = DomainGenerators.randomShowId();
    private String title = "Random title";
    private Map<SeatNumber, Seat> seats = HashMap.empty();

    public static ShowBuilder showBuilder() {
        return new ShowBuilder();
    }

    public ShowBuilder withRandomSeats() {
        seats = SeatsCreator.createSeats(DomainGenerators.randomPrice());
        return this;
    }

    public ShowBuilder withSeat(Seat seat) {
        seats = seats.put(seat.number(), seat);
        return this;
    }

    public Show build() {
        return new Show(id, title, seats);
    }


}

