package internship.parser;

import edu.internship.parser.JsonFileProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFileProcessorTest {

  @TempDir Path tempDir;

  @Test
  @DisplayName("Should process single file and return aggregated status statistics")
  void testProcessFiles_singleFile() throws Exception {
    Path file = tempDir.resolve("order1.json");
    Files.writeString(
        file,
        """
        {
          "id": 1,
          "status": "NEW"
        }
        """);

    JsonFileProcessor processor = new JsonFileProcessor(2);
    Map<String, Integer> result = processor.processFiles(List.of(file), "status");

    assertEquals(1, result.size());
    assertEquals(1, result.get("NEW"));
  }

  @Test
  @DisplayName("Should process multiple files and merge statistics from all of them")
  void testProcessFiles_multipleFiles() throws Exception {
    Path file1 = tempDir.resolve("o1.json");
    Files.writeString(
        file1,
        """
        { "status": "NEW" }
        """);

    Path file2 = tempDir.resolve("o2.json");
    Files.writeString(
        file2,
        """
        { "status": "PROCESSING" }
        """);

    Path file3 = tempDir.resolve("o3.json");
    Files.writeString(
        file3,
        """
        { "status": "NEW" }
        """);

    JsonFileProcessor processor = new JsonFileProcessor(3);
    Map<String, Integer> result = processor.processFiles(List.of(file1, file2, file3), "status");

    assertEquals(2, result.get("NEW"));
    assertEquals(1, result.get("PROCESSING"));
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("Should return empty stats when files contain no status attribute")
  void testProcessFiles_noStatusField() throws Exception {
    Path file1 = tempDir.resolve("noStatus1.json");
    Files.writeString(
        file1,
        """
        { "id": 10 }
        """);

    Path file2 = tempDir.resolve("noStatus2.json");
    Files.writeString(
        file2,
        """
        { "orderNumber": "X-001" }
        """);

    JsonFileProcessor processor = new JsonFileProcessor(2);
    Map<String, Integer> result = processor.processFiles(List.of(file1, file2), "status");

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should return empty map when file list is empty")
  void testProcessFiles_emptyFileList() throws Exception {
    JsonFileProcessor processor = new JsonFileProcessor(2);
    Map<String, Integer> result = processor.processFiles(List.of(), "status");

    assertTrue(result.isEmpty());
  }
}
