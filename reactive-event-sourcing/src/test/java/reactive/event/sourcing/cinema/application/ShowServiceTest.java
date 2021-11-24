package reactive.event.sourcing.cinema.application;

import akka.actor.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.persistence.testkit.PersistenceTestKitPlugin;
import akka.testkit.javadsl.TestKit;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reactive.event.sourcing.cinema.domain.ShowId;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static reactive.event.sourcing.cinema.application.ShowEntityResponse.*;
import static reactive.event.sourcing.cinema.domain.DomainGenerators.randomSeatNumber;

class ShowServiceTest {

    private static final Config config = PersistenceTestKitPlugin.config().withFallback(ConfigFactory.load());
    private static final ActorSystem system = ActorSystem.create("res-cinema", config);

    private final ClusterSharding sharding = ClusterSharding.get(Adapter.toTyped(system));
    private final Clock clock = Clock.fixed(Instant.now(), UTC);

    // subject
    private final ShowService showService = new ShowService(sharding, clock);

    @AfterAll
    public static void cleanUp() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    public void shouldReserveSeat() throws ExecutionException, InterruptedException {
        //given
        var showId = ShowId.of();
        var seatNumber = randomSeatNumber();

        //when
        var result = showService.reserveSeat(showId, seatNumber).toCompletableFuture().get();

        //then
        assertThat(result).isInstanceOf(CommandProcessed.class);
    }

    @Test
    public void shouldCancelReservation() throws ExecutionException, InterruptedException {
        //given
        var showId = ShowId.of();
        var seatNumber = randomSeatNumber();

        //when
        var reservationResult = showService.reserveSeat(showId, seatNumber).toCompletableFuture().get();

        //then
        assertThat(reservationResult).isInstanceOf(CommandProcessed.class);

        //when
        var cancellationResult = showService.cancelReservation(showId, seatNumber).toCompletableFuture().get();

        //then
        assertThat(cancellationResult).isInstanceOf(CommandProcessed.class);
    }

    @Test
    public void shouldFindShowById() throws ExecutionException, InterruptedException {
        //given
        var showId = ShowId.of();

        //when
        var show = showService.findShowBy(showId).toCompletableFuture().get();

        //then
        assertThat(show.id()).isEqualTo(showId);
    }
}