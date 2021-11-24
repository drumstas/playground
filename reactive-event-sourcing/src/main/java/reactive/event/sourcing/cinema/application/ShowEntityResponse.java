package reactive.event.sourcing.cinema.application;

import reactive.event.sourcing.cinema.domain.ShowCommandError;

import java.io.Serializable;

public sealed interface ShowEntityResponse extends Serializable {

    final class CommandProcessed implements ShowEntityResponse {}

    record CommandRejected(ShowCommandError error) implements ShowEntityResponse { }

}
