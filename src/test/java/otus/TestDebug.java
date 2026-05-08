package otus;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static otus.Otus.HTTP_PROTOCOL;
import static otus.Scenario.createScenario;

public class TestDebug extends Simulation {

    {
        double PEAK_USERS_PER_SEC = 7.0;   // максимум пользователей из калиборовки 7, но при 7 на 6 ступени начинают расти ошибки (testdebug-20260507144052555/index.html)
        double stepUsers = PEAK_USERS_PER_SEC * 0.1;
        int steps = 10;
        Duration levelDuration = Duration.ofMinutes(3);
        Duration rampDuration = Duration.ofSeconds(15);

        List<OpenInjectionStep> injectionSteps = new ArrayList<>();

        for (int i = 1; i <= steps; i++) {
            double targetUsers = stepUsers * i;
            injectionSteps.add(constantUsersPerSec(targetUsers).during(levelDuration));
            if (i < steps) {
                injectionSteps.add(nothingFor(rampDuration));
            }
        }

        setUp(
                createScenario().injectOpen(injectionSteps).protocols(HTTP_PROTOCOL)
        ).maxDuration(Duration.ofMinutes(35));
    }
}
