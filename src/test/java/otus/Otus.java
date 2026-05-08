package otus;

import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.http.HttpDsl.*;

public class Otus {

  public static final HttpProtocolBuilder HTTP_PROTOCOL = http
      .baseUrl("http://webtours.load-test.ru:1080")
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
      .acceptEncodingHeader("gzip, deflate")
      .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:135.0) Gecko/20100101 Firefox/135.0");
}
