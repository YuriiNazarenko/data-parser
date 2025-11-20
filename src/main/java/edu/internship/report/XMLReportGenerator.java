package edu.internship.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

// TODO: sanitize symbols

/** Generates XML reports with statistics for processed JSON files. */
public class XMLReportGenerator {
  /**
   * Creates an XML file with statistics for the specified attribute.
   *
   * @param stats a statistics map: key is the attribute value, value is the number of occurrences
   * @param attribute the name of the attribute (author, year_published, etc.)
   * @param outputDir the directory where the file will be written
   */
  public static void writeStatisticsToFile(Map<String, Integer> stats, String attribute, Path outputDir)
      throws IOException, ParserConfigurationException, TransformerException {

    validateInputs(stats, attribute, outputDir);

    // Create output directory if it doesn't exist
    if (!Files.exists(outputDir)) {
      Files.createDirectories(outputDir);
    }

    Document document = createDocument();
    Element rootElement = buildStatisticsXml(document, stats, attribute);

    document.appendChild(rootElement);

    Path outputFile = outputDir.resolve("statistics_by_" + attribute + ".xml");

    saveDocumentToFile(document, outputFile);
  }

  private static Document createDocument() throws ParserConfigurationException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return documentBuilder.newDocument();
  }

  private static Element createStatsItem(Document document, Map.Entry<String, Integer> entry) {
    Element item = document.createElement("item");

    // Create <value> element with attribute value
    Element value = document.createElement("value");
    value.appendChild(document.createTextNode(entry.getKey()));
    item.appendChild(value);

    // Create <count> element with occurrence count
    Element count = document.createElement("count");
    count.appendChild(document.createTextNode(entry.getValue().toString()));
    item.appendChild(count);

    return item;
  }

  private static Element buildStatisticsXml(Document document, Map<String, Integer> stats, String attribute) {
    // Root element <statistics>
    Element rootElement = document.createElement("statistics");
    rootElement.setAttribute("attribute", attribute);
    rootElement.setAttribute("totalItems", String.valueOf(stats.size()));

    int totalCount = stats.values().stream().mapToInt(Integer::intValue).sum();
    rootElement.setAttribute("totalCount", String.valueOf(totalCount));

    // Add <item> only if the statistics are not empty.
    if (!stats.isEmpty()) {
      stats.entrySet().stream()
          .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
          .forEach(entry -> rootElement.appendChild(createStatsItem(document, entry)));
    }

    return rootElement;
  }

  private static void saveDocumentToFile(Document document, Path fileName) throws TransformerException, IOException {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();

    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    DOMSource source = new DOMSource(document);
    StreamResult result = new StreamResult(Files.newOutputStream(fileName));
    transformer.transform(source, result);

    System.out.println("File saved: " + fileName.toAbsolutePath());
  }

  /**
   * Validates input parameters for XML generation.
   *
   * @param stats statistics map to validate
   * @param attribute attribute name to validate
   * @param outputDir output directory to validate
   */
  private static void validateInputs(Map<String, Integer> stats, String attribute, Path outputDir) {
    Objects.requireNonNull(stats, "The stats cannot be null");
    Objects.requireNonNull(outputDir, "The outputDir cannot be null");

    if (attribute == null || attribute.isBlank()) {
      throw new IllegalArgumentException("The attribute cannot be empty.");
    }
  }
}
