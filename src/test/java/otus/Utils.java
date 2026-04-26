package otus;

import java.util.List;
import java.util.Random;

public class Utils {

  public static String randomFromList(List<String> list) {
    return list.get(new Random().nextInt(list.size()));
  }
}
