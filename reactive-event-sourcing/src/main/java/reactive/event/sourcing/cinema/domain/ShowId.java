package reactive.event.sourcing.cinema.domain;

import java.io.Serializable;
import java.util.UUID;

public record ShowId(UUID id) implements Serializable {

    public static ShowId of() {
        return new ShowId(UUID.randomUUID());
    }

}

