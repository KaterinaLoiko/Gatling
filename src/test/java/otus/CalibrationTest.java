package otus;

import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static otus.Otus.HTTP_PROTOCOL;
import static otus.Scenario.createScenario;

public class CalibrationTest extends Simulation {

    {
        setUp(
                createScenario().injectOpen(
                        constantUsersPerSec(1).during(Duration.ofMinutes(2)) // получила 7 запросов в секунду на пике calibrationtest-20260507135107936/index.html
                ).protocols(HTTP_PROTOCOL)
        );
    }
}
