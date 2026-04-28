package otus;

import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.http.HttpDsl.*;
import static otus.Actions.login;
import static otus.Actions.openHomePage;
import static otus.Actions.paymentScenario;
import static otus.Actions.returnToHomeScenario;
import static otus.Actions.searchFlightsScenario;

public class Otus extends Simulation {

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
                incrementUsersPerSec(1)
                        .times(6)
                        .eachLevelLasting(Duration.ofMinutes(5))
                        .separatedByRampsLasting(Duration.ofSeconds(15))
                        .startingFrom(0)
    ).protocols(httpProtocol)
    ).maxDuration(Duration.ofMinutes(60));
  }
}
