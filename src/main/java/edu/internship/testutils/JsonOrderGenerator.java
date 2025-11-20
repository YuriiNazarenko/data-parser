package edu.internship.testutils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/** Utility class for generating test JSON files containing a list of Order objects. */
public class JsonOrderGenerator {

  private static final List<String> TAGS =
      List.of(
          "electronics",
          "books",
          "home",
          "fashion",
          "premium",
          "discount",
          "toys",
          "gifts",
          "computers",
          "furniture",
          "education");

  private static final List<String> PAYMENT_METHODS =
      List.of("credit_card", "cash", "paypal", "apple_pay", "google_pay");

  private static final List<String> STATUSES = List.of("PENDING", "PROCESSING", "DELIVERED", "CANCELLED");

  private final Random random = new Random();
  private final ObjectMapper mapper = new ObjectMapper();
  private final JsonFactory jsonFactory = new JsonFactory();

  public static void main(String[] args) throws IOException {
    JsonOrderGenerator generator = new JsonOrderGenerator();

    File output = new File("src/main/resources/data/orders-test.json");
    generator.generateFile(output, 100);

    System.out.println("Generated: " + output.getAbsolutePath());
  }

  /**
   * Generates a JSON file containing multiple orders.
   *
   * @param output the output file to write
   * @param count number of orders to generate
   */
  public void generateFile(File output, int count) throws IOException {
    if (!output.getParentFile().exists()) output.getParentFile().mkdirs();

    try (JsonGenerator gen = jsonFactory.createGenerator(output, com.fasterxml.jackson.core.JsonEncoding.UTF8)) {
      gen.writeStartArray();
      for (int i = 0; i < count; i++) writeOrder(gen, i + 1);
      gen.writeEndArray();
    }
  }

  private void writeOrder(JsonGenerator gen, int id) throws IOException {
    gen.writeStartObject();

    gen.writeNumberField("id", id);
    gen.writeStringField("orderNumber", "ORD-" + (1000 + id));
    gen.writeStringField("orderDate", randomDate());
    gen.writeStringField("status", randomFrom(STATUSES));
    gen.writeNumberField("totalAmount", randomAmount());

    // deliveryAddress як об'єкт
    gen.writeObjectFieldStart("deliveryAddress");
    gen.writeStringField("city", randomCity());
    gen.writeStringField("street", "Street " + (1 + random.nextInt(120)));
    gen.writeNumberField("houseNumber", 1 + random.nextInt(120));
    gen.writeEndObject();

    gen.writeStringField("paymentMethod", randomFrom(PAYMENT_METHODS));
    gen.writeNumberField("clientId", 100 + random.nextInt(200));
    gen.writeStringField("createdAt", randomDate());
    gen.writeStringField("updatedAt", randomDate());
    gen.writeStringField("tags", randomTags());

    gen.writeEndObject();
  }

  private String randomTags() {
    int count = 1 + random.nextInt(3);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      if (i > 0) sb.append(",");
      sb.append(randomFrom(TAGS));
    }
    return sb.toString();
  }

  private String randomDate() {
    LocalDateTime dt =
        LocalDateTime.now()
            .minusDays(random.nextInt(60))
            .minusHours(random.nextInt(12))
            .minusMinutes(random.nextInt(60));
    return dt.toString();
  }

  private double randomAmount() {
    return Math.round((50 + random.nextDouble() * 1500) * 100.0) / 100.0;
  }

  private String randomCity() {
    String[] cities = {"Kyiv", "Lviv", "Dnipro", "Odesa", "Kharkiv", "Ternopil", "Vinnytsia"};
    return cities[random.nextInt(cities.length)];
  }

  private <T> T randomFrom(List<T> list) {
    return list.get(random.nextInt(list.size()));
  }
}
