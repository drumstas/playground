package reactive.event.sourcing.cinema.application;

import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import org.springframework.stereotype.Component;
import reactive.event.sourcing.cinema.domain.SeatNumber;
import reactive.event.sourcing.cinema.domain.Show;
import reactive.event.sourcing.cinema.domain.ShowCommand;
import reactive.event.sourcing.cinema.domain.ShowCommand.CancelSeatReservation;
import reactive.event.sourcing.cinema.domain.ShowId;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static reactive.event.sourcing.cinema.application.ShowEntity.SHOW_ENTITY_TYPE_KEY;
import static reactive.event.sourcing.cinema.application.ShowEntityCommand.GetShow;
import static reactive.event.sourcing.cinema.application.ShowEntityCommand.ShowCommandEnvelope;
import static reactive.event.sourcing.cinema.domain.ShowCommand.ReserveSeat;

@Component
public class ShowService {
    private static final Duration ASK_TIMEOUT = Duration.ofSeconds(2); //TODO should be configurable

    private final ClusterSharding sharding;


    ShowService(ClusterSharding sharding, Clock clock) {
        this.sharding = sharding;
        sharding.init(Entity.of(SHOW_ENTITY_TYPE_KEY, entityContext -> {
            ShowId showId = new ShowId(UUID.fromString(entityContext.getEntityId()));
            return ShowEntity.create(showId, clock);
        }));
    }

    public CompletionStage<Show> findShowBy(ShowId showId) {
        return getShowEntityRef(showId).ask(GetShow::new, ASK_TIMEOUT);
    }

    public CompletionStage<ShowEntityResponse> reserveSeat(ShowId showId, SeatNumber seatNumber) {
        return processCommand(new ReserveSeat(showId, seatNumber));
    }

    public CompletionStage<ShowEntityResponse> cancelReservation(ShowId showId, SeatNumber seatNumber) {
        return processCommand(new CancelSeatReservation(showId, seatNumber));
    }

    private CompletionStage<ShowEntityResponse> processCommand(ShowCommand showCommand) {
        return getShowEntityRef(showCommand.showId())
                .ask(replyTo -> new ShowCommandEnvelope(showCommand, replyTo), ASK_TIMEOUT);
    }

    private EntityRef<ShowEntityCommand> getShowEntityRef(ShowId showId) {
        return sharding.entityRefFor(SHOW_ENTITY_TYPE_KEY, showId.id().toString());
    }
}
