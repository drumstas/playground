package reactive.event.sourcing.cinema.domain;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

import static reactive.event.sourcing.cinema.domain.ShowBuilder.showBuilder;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

class ShowTest {

    private final Clock clock = Clock.fixed(Instant.now(), UTC);

    @Test
    public void shouldReserveTheSeat() {
        //given
        var show = DomainGenerators.randomShow();
        var reserveSeat = ShowCommandGenerators.randomReserveSeat(show.id());

        //when
        var events = show.process(reserveSeat, clock).get();

        //then
        assertThat(events).containsOnly(new ShowEvent.SeatReserved(show.id(), clock.instant(), reserveSeat.seatNumber()));
    }

    @Test
    public void shouldReserveTheSeatWithApplyingEvent() {
        //given
        var show = DomainGenerators.randomShow();
        var reserveSeat = ShowCommandGenerators.randomReserveSeat(show.id());

        //when
        var events = show.process(reserveSeat, clock).get();
        var updatedShow = apply(show, events);

        //then
        var reservedSeat = updatedShow.seats().get(reserveSeat.seatNumber()).get();
        assertThat(events).containsOnly(new ShowEvent.SeatReserved(show.id(), clock.instant(), reserveSeat.seatNumber()));
        assertThat(reservedSeat.isAvailable()).isFalse();
    }

    @Test
    public void shouldNotReserveAlreadyReservedSeat() {
        //given
        var show = DomainGenerators.randomShow();
        var reserveSeat = ShowCommandGenerators.randomReserveSeat(show.id());

        //when
        var events = show.process(reserveSeat, clock).get();
        var updatedShow = apply(show, events);

        //then
        assertThat(events).containsOnly(new ShowEvent.SeatReserved(show.id(), clock.instant(), reserveSeat.seatNumber()));

        //when
        ShowCommandError result = updatedShow.process(reserveSeat, clock).getLeft();

        //then
        assertThat(result).isEqualTo(ShowCommandError.SEAT_NOT_AVAILABLE);
    }

    @Test
    public void shouldNotReserveNotExistingSeat() {
        //given
        var show = DomainGenerators.randomShow();
        var reserveSeat = new ShowCommand.ReserveSeat(show.id(), new SeatNumber(SeatsCreator.SEAT_RANGE + 1));

        //when
        ShowCommandError result = show.process(reserveSeat, clock).getLeft();

        //then
        assertThat(result).isEqualTo(ShowCommandError.SEAT_NOT_EXISTS);
    }

    @Test
    public void shouldCancelSeatReservation() {
        //given
        var reservedSeat = new Seat(new SeatNumber(2), SeatStatus.RESERVED, new BigDecimal("123"));
        var show = showBuilder().withRandomSeats().withSeat(reservedSeat).build();
        var cancelSeatReservation = new ShowCommand.CancelSeatReservation(show.id(), reservedSeat.number());

        //when
        var events = show.process(cancelSeatReservation, clock).get();

        //then
        assertThat(events).containsOnly(new ShowEvent.SeatReservationCancelled(show.id(), clock.instant(), reservedSeat.number()));
    }

    @Test
    public void shouldNotCancelReservationOfAvailableSeat() {
        //given
        var availableSeat = new Seat(new SeatNumber(2), SeatStatus.AVAILABLE, new BigDecimal("123"));
        var show = showBuilder().withRandomSeats().withSeat(availableSeat).build();
        var cancelSeatReservation = new ShowCommand.CancelSeatReservation(show.id(), availableSeat.number());

        //when
        var result = show.process(cancelSeatReservation, clock).getLeft();

        //then
        assertThat(result).isEqualTo(ShowCommandError.SEAT_NOT_RESERVED);
    }

    private Show apply(Show show, List<ShowEvent> events) {
        return events.foldLeft(show, Show::apply);
    }
}
