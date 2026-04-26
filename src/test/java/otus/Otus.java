package otus;

import io.gatling.javaapi.core.*;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.regex;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import io.gatling.javaapi.http.*;

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
      .exec(login("eloiko", "eloiko"))
      .exec(searchFlightsScenario)
      .exec(paymentScenario)
      .exec(returnToHomeScenario)
      .pause(1);

  {
    setUp(
        scn.injectOpen(
            rampUsers(1).during(10) // 5 пользователей за 10 секунд
        )
    ).protocols(httpProtocol);
  }
}
