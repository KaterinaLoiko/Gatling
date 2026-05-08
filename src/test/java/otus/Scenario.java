package otus;

import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static otus.Actions.*;

public class Scenario {

    public static ScenarioBuilder createScenario() {
        return scenario("Web Tours User Journey")
                .exec(openHomePage)
                .pause(5)
                .exec(login("eloiko", "eloiko"))
                .pause(5)
                .exec(searchFlightsScenario)
                .pause(5)
                .exec(paymentScenario)
                .pause(5)
                .exec(returnToHomeScenario);
    }
}
