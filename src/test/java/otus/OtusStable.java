package otus;

import io.gatling.javaapi.core.Simulation;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static otus.Otus.HTTP_PROTOCOL;
import static otus.Scenario.createScenario;

public class OtusStable extends Simulation {

    int PEAK_RPS = 16;     // Из нового ступенчатого теста в 18:38:41 упало response time
    int RELIABILITY_RPS = 12;
    int maxUsers = (PEAK_RPS * 10);

  {
      setUp(
              createScenario().injectOpen(
                              rampUsersPerSec(1).to(maxUsers).during(Duration.ofMinutes(2))
                      ).protocols(HTTP_PROTOCOL)
                      .throttle(
                              reachRps(RELIABILITY_RPS).in(Duration.ofMinutes(2)),
                              holdFor(Duration.ofMinutes(58))
                      )
      ).maxDuration(Duration.ofMinutes(62));
  }
}
