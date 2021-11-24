package reactive.event.sourcing.cinema.application;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import akka.persistence.testkit.javadsl.EventSourcedBehaviorTestKit;
import reactive.event.sourcing.cinema.domain.Show;
import reactive.event.sourcing.cinema.domain.ShowCommand;
import reactive.event.sourcing.cinema.domain.ShowEvent;
import reactive.event.sourcing.cinema.domain.ShowId;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactive.event.sourcing.cinema.domain.*;

import java.time.Clock;
import java.time.Instant;

import static reactive.event.sourcing.cinema.domain.ShowCommandGenerators.randomReserveSeat;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

public class ShowEntityTest {

    public static final Config UNIT_TEST_AKKA_CONFIGURATION = ConfigFactory.parseString("""
                akka.actor.enable-additional-serialization-bindings = on
                akka.actor.allow-java-serialization = on
                akka.actor.warn-about-java-serializer-usage = off
                akka.loglevel = INFO
            """
    );

    private static final ActorTestKit testKit = ActorTestKit.create(
            EventSourcedBehaviorTestKit.config().withFallback(UNIT_TEST_AKKA_CONFIGURATION)
    );

    @AfterAll
    public static void cleanUp() {
        testKit.shutdownTestKit();
    }

    private Clock clock = Clock.fixed(Instant.now(), UTC);

    @Test
    public void shouldReserveSeat() {
        //given
        var showId = ShowId.of();
        EventSourcedBehaviorTestKit<ShowEntityCommand, ShowEvent, Show> showEntityKit = EventSourcedBehaviorTestKit.create(testKit.system(), ShowEntity.create(showId, clock));
        var reserveSeat = randomReserveSeat(showId);

        //when
        var result = showEntityKit.<ShowEntityResponse>runCommand(replyTo -> toEnvelope(reserveSeat, replyTo));

        //then
        assertThat(result.reply()).isInstanceOf(ShowEntityResponse.CommandProcessed.class);
        assertThat(result.event()).isInstanceOf(ShowEvent.SeatReserved.class);
        var reservedSeat = result.state().seats().get(reserveSeat.seatNumber()).get();
        assertThat(reservedSeat.isReserved()).isTrue();
    }

    @Test
    public void shouldNotReserveTheAlreadyReservedSeat() {
        //given
        var showId = ShowId.of();
        EventSourcedBehaviorTestKit<ShowEntityCommand, ShowEvent, Show> showEntityKit = EventSourcedBehaviorTestKit.create(testKit.system(), ShowEntity.create(showId, clock));
        var reserveSeat = randomReserveSeat(showId);

        //when
        showEntityKit.<ShowEntityResponse>runCommand(replyTo -> toEnvelope(reserveSeat, replyTo));
        var result = showEntityKit.<ShowEntityResponse>runCommand(replyTo -> toEnvelope(reserveSeat, replyTo));

        //then
        assertThat(result.reply()).isEqualTo(new ShowEntityResponse.CommandRejected(ShowCommandError.SEAT_NOT_AVAILABLE));
        assertThat(result.hasNoEvents()).isTrue();
    }

    @Test
    public void shouldCancelReservation() {
        //given
        var showId = ShowId.of();
        EventSourcedBehaviorTestKit<ShowEntityCommand, ShowEvent, Show> showEntityKit = EventSourcedBehaviorTestKit.create(testKit.system(), ShowEntity.create(showId, clock));
        var reserveSeat = randomReserveSeat(showId);
        var cancelSeatReservation = new ShowCommand.CancelSeatReservation(showId, reserveSeat.seatNumber());

        //when
        showEntityKit.<ShowEntityResponse>runCommand(replyTo -> toEnvelope(reserveSeat, replyTo));
        var result = showEntityKit.<ShowEntityResponse>runCommand(replyTo -> toEnvelope(cancelSeatReservation, replyTo));

        //then
        assertThat(result.reply()).isInstanceOf(ShowEntityResponse.CommandProcessed.class);
        assertThat(result.event()).isInstanceOf(ShowEvent.SeatReservationCancelled.class);
        var seat = result.state().seats().get(reserveSeat.seatNumber()).get();
        assertThat(seat.isReserved()).isFalse();
    }

    @Test
    public void shouldReserveSeat_WithProbe() {
        //given
        var showId = ShowId.of();
        var showEntityRef = testKit.spawn(ShowEntity.create(showId, clock));
        var commandResponseProbe = testKit.<ShowEntityResponse>createTestProbe();
        var showResponseProbe = testKit.<Show>createTestProbe();

        var reserveSeat = randomReserveSeat(showId);

        //when
        showEntityRef.tell(toEnvelope(reserveSeat, commandResponseProbe.ref()));

        //then
        commandResponseProbe.expectMessageClass(ShowEntityResponse.CommandProcessed.class);

        //when
        showEntityRef.tell(new ShowEntityCommand.GetShow(showResponseProbe.ref()));

        //then
        Show returnedShow = showResponseProbe.receiveMessage();
        assertThat(returnedShow.seats().get(reserveSeat.seatNumber()).get().isReserved()).isTrue();
    }

    private ShowEntityCommand.ShowCommandEnvelope toEnvelope(ShowCommand command, ActorRef<ShowEntityResponse> replyTo) {
        return new ShowEntityCommand.ShowCommandEnvelope(command, replyTo);
    }

}
