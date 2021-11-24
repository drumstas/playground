package reactive.event.sourcing.cinema.application;

import akka.actor.typed.ActorRef;
import reactive.event.sourcing.cinema.domain.Show;
import reactive.event.sourcing.cinema.domain.ShowCommand;

import java.io.Serializable;

public sealed interface ShowEntityCommand extends Serializable {

    record ShowCommandEnvelope(ShowCommand command, ActorRef<ShowEntityResponse> replyTo) implements ShowEntityCommand { }

    record GetShow(ActorRef<Show> replyTo) implements ShowEntityCommand { }
}
