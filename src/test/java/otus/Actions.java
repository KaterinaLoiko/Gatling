package otus;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.css;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import java.util.List;
import static otus.Utils.randomFromList;

public class Actions {

  public static ChainBuilder openHomePage = exec(http("Open Home Page")
      .get("/cgi-bin/nav.pl?in=home")
      .check(css("input[name='userSession']", "value").saveAs("userSession")));

  public static ChainBuilder login(String username, String password) {
    return exec(http("Login Request")
        .post("/cgi-bin/login.pl")
        .formParam("userSession", "${userSession}")
        .formParam("username", username)
        .formParam("password", password)
    );
  }

  public static ChainBuilder searchFlightsScenario = group("Select Flight")
      .on(exec(http("Get Flight Selection Page")
              .get("/cgi-bin/reservations.pl?page=welcome")
              .check(css("select[name='depart'] option", "value")
                  .findAll()
                  .saveAs("departureCities"))
              .check(css("select[name='arrive'] option", "value")
                  .findAll()
                  .saveAs("arrivalCities"))
          )

              .exec(session -> {
                List<String> departures = session.getList("departureCities");
                List<String> arrivals = session.getList("arrivalCities");

                String randomDeparture = randomFromList(departures);
                List<String> filteredArrivals = arrivals.stream()
                    .filter(city -> !city.equals(randomDeparture))
                    .toList();
                String randomArrival = randomFromList(filteredArrivals);
                return session
                    .set("randomDeparture", randomDeparture)
                    .set("randomArrival", randomArrival);
              })

              .exec(session -> {
                System.out.println(
                    "Выбранный город вылета: " + session.getString("randomDeparture"));
                System.out.println(
                    "Выбранный город прилёта: " + session.getString("randomArrival"));
                return session;
              })

              .exec(http("Send Flights Request")
                      .post("/cgi-bin/reservations.pl")
                      .formParam("advanceDiscount", "0")
                      .formParam("depart", "#{randomDeparture}")
                      .formParam("departDate", "04/28/2026")
                      .formParam("arrive", "#{randomArrival}")
                      .formParam("returnDate", "04/29/2026")
                      .formParam("numPassengers", "1")
                      .formParam("seatPref", "None")
                      .formParam("seatType", "Coach")
                      .formParam("findFlights.x", "63")
                      .formParam("findFlights.y", "7")
                      .formParam(".cgifields", "roundtrip")
                      .formParam(".cgifields", "seatType")
                      .formParam(".cgifields", "seatPref")
                      .check(css("input[name='outboundFlight']", "value").findAll()
                          .saveAs("availableFlights"))
                      .check(bodyString().transform(s -> s.contains("Flight Selections")).is(true))
              )
              .pause(2)

              .exec(session -> {
                List<String> flights = session.getList("availableFlights");
                String randomFlight = randomFromList(flights);
                System.out.println("Выбран случайный рейс: " + randomFlight);

                return session
                    .set("selectedOutboundFlight", randomFlight)
                    .set("flightsCount", flights.size());
              })

              .exec(http("Reserve Flight")
                      .post("/cgi-bin/reservations.pl")
                      .formParam("outboundFlight", "#{selectedOutboundFlight}")
                      .formParam("numPassengers", "1")
                      .formParam("advanceDiscount", "0")
                      .formParam("seatType", "Coach")
                      .formParam("seatPref", "None")
                      .formParam("reserveFlights.x", "70")
                      .formParam("reserveFlights.y", "8")
                      .check(
                          css("input[name='outboundFlight']", "value").saveAs("outboundFlightFromPayment"))
                      .check(bodyString().transform(s -> s.contains("Flight Reservation")).is(true))
              )
      );

  public static ChainBuilder paymentScenario = exec(
      http("Buy Flights")
          .post("/cgi-bin/reservations.pl")
          .formParam("firstName", "Ekaterina")
          .formParam("lastName", "Loiko")
          .formParam("address1", "Minina str")
          .formParam("address2", "Nighni Novgorod")
          .formParam("pass1", "Ekateria Loiko")
          .formParam("creditCard", "4111111111111111")
          .formParam("expDate", "12/28")
          .formParam("oldCCOption", "")
          .formParam("numPassengers", "1")
          .formParam("seatType", "Coach")
          .formParam("seatPref", "None")
          .formParam("outboundFlight", "#{outboundFlightFromPayment}")
          .formParam("advanceDiscount", "0")
          .formParam("returnFlight", "")
          .formParam("buyFlights.x", "34")
          .formParam("buyFlights.y", "5")
          .formParam("JSFormSubmit", "off")
          .check(bodyString().transform(s -> s.contains("Reservation Made!")).is(true))
  );

  public static ChainBuilder returnToHomeScenario = exec(
      http("Return to Home")
          .get("/webtours/")
          .check(status().is(200))
  );
}
