package otus;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static otus.Actions.*;

public class TestDebug extends Simulation {

    double peakRps = 15.0; // в 10:33 на 20.0 в testdebug-20260428084406308/index.html отказ, последняя стабильная 15
    double step = peakRps * 0.1;

    private static final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://webtours.load-test.ru:1080")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:135.0) Gecko/20100101 Firefox/135.0");

    ScenarioBuilder scn = scenario("Web Tours User Journey")
            .exec(openHomePage)
            .pause(5)
            .exec(login("eloiko", "eloiko"))
            .pause(5)
            .exec(searchFlightsScenario)
            .pause(5)
            .exec(paymentScenario)
            .pause(5)
            .exec(returnToHomeScenario);

    {
        setUp(
                scn.injectOpen(
                        constantUsersPerSec(step).during(Duration.ofMinutes(5)),
                        nothingFor(Duration.ofSeconds(5)),
                        constantUsersPerSec(2*step).during(Duration.ofMinutes(5)),
                        nothingFor(Duration.ofSeconds(5)),
                        constantUsersPerSec(3*step).during(Duration.ofMinutes(5)),
                        nothingFor(Duration.ofSeconds(5)),
                        constantUsersPerSec(4*step).during(Duration.ofMinutes(5))
//                        rampUsersPerSec(step).to(peakRps).during(Duration.ofMinutes(20))
                ).protocols(httpProtocol)
        );
    }
}
