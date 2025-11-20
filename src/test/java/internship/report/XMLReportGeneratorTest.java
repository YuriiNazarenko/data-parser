package internship.report;

import edu.internship.report.XMLReportGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XMLReportGeneratorTest {

  @TempDir Path tempDir;

  @Test
  @DisplayName("Should throw NullPointerException when stats map is null")
  void testNullStats() {
    assertThrows(NullPointerException.class, () -> XMLReportGenerator.writeStatisticsToFile(null, "status", tempDir));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when attribute is blank")
  void testBlankAttribute() {
    assertThrows(
        IllegalArgumentException.class, () -> XMLReportGenerator.writeStatisticsToFile(Map.of(), " ", tempDir));
  }

  @Test
  @DisplayName("Should throw NullPointerException when output directory is null")
  void testNullOutputDir() {
    assertThrows(NullPointerException.class, () -> XMLReportGenerator.writeStatisticsToFile(Map.of(), "status", null));
  }

  @Test
  @DisplayName("Should create directory if it does not exist")
  void testCreatesDirectory() throws Exception {
    Path newDir = tempDir.resolve("nested");

    assertFalse(Files.exists(newDir));

    XMLReportGenerator.writeStatisticsToFile(Map.of(), "status", newDir);

    assertTrue(Files.exists(newDir));
  }

  @Test
  @DisplayName("Should create XML file with correct name")
  void testCreatesFileWithCorrectName() throws Exception {
    Map<String, Integer> stats = Map.of("Paid", 3, "Pending", 1);

    XMLReportGenerator.writeStatisticsToFile(stats, "paymentMethod", tempDir);

    Path file = tempDir.resolve("statistics_by_paymentMethod.xml");
    assertTrue(Files.exists(file));
  }

  @Test
  @DisplayName("Should generate XML with correct structure and values")
  void testXmlContent() throws Exception {
    Map<String, Integer> stats =
        Map.of(
            "Paid", 5,
            "Failed", 1,
            "Pending", 3);

    XMLReportGenerator.writeStatisticsToFile(stats, "status", tempDir);

    Path file = tempDir.resolve("statistics_by_status.xml");
    assertTrue(Files.exists(file));

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Files.newInputStream(file));

    Element root = doc.getDocumentElement();

    assertEquals("statistics", root.getNodeName());
    assertEquals("status", root.getAttribute("attribute"));
    assertEquals("3", root.getAttribute("totalItems"));
    assertEquals("9", root.getAttribute("totalCount"));

    var items = root.getElementsByTagName("item");
    assertEquals(3, items.getLength());
  }

  @Test
  @DisplayName("Should write XML with zero totals when stats map is empty")
  void testEmptyStats() throws Exception {
    XMLReportGenerator.writeStatisticsToFile(Map.of(), "attr", tempDir);

    Path file = tempDir.resolve("statistics_by_attr.xml");
    assertTrue(Files.exists(file));

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Files.newInputStream(file));

    Element root = doc.getDocumentElement();

    assertEquals("0", root.getAttribute("totalItems"));
    assertEquals("0", root.getAttribute("totalCount"));
    assertEquals(0, root.getElementsByTagName("item").getLength());
  }
}
