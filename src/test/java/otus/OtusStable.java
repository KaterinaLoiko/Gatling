package otus;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static otus.Actions.*;

public class OtusStable extends Simulation {

    double PEAK_RPS = 15.0;
    double RELIABILITY_RPS = PEAK_RPS * 0.8;

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
                      rampUsersPerSec(0).to(RELIABILITY_RPS).during(Duration.ofMinutes(10)),
                      constantUsersPerSec(RELIABILITY_RPS).during(Duration.ofMinutes(50))
              ).protocols(httpProtocol)
      ).maxDuration(Duration.ofMinutes(60));
  }
}
