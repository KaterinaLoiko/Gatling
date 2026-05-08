package otus;

import io.gatling.javaapi.core.Simulation;
import java.time.Duration;
import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static otus.Otus.HTTP_PROTOCOL;
import static otus.Scenario.createScenario;

public class OtusTest extends Simulation {

    {
        setUp(
                createScenario().injectOpen(
                        incrementUsersPerSec(1)
                                .times(6)
                                .eachLevelLasting(Duration.ofMinutes(5))
                                .separatedByRampsLasting(Duration.ofSeconds(15))
                                .startingFrom(0)
                ).protocols(HTTP_PROTOCOL)
        ).maxDuration(Duration.ofMinutes(60));
    }
}
