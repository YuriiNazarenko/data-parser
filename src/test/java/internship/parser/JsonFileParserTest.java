package internship.parser;

import edu.internship.parser.JsonFileParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonFileParserTest {

	@TempDir
	Path tempDir;

	@Test
	@DisplayName("Should read a single Order object and extract status")
	void testProcessFile_singleOrder() throws IOException {
		Path file = tempDir.resolve("order_single.json");

		Files.writeString(
			file,
			"""
				{
				  "id": 1,
				  "orderNumber": "A-001",
				  "orderDate": "2024-10-01T10:00:00",
				  "status": "NEW",
				  "totalAmount": 150.0
				}
				""");

		JsonFileParser parser = new JsonFileParser();
		Map<String, Integer> result = parser.processFile(file, "status");

		assertEquals(1, result.size());
		assertEquals(1, result.get("NEW"));
	}

	@Test
	@DisplayName("Should read an array of Orders and count repeated status values")
	void testProcessFile_orderArray() throws IOException {
		Path file = tempDir.resolve("order_array.json");

		Files.writeString(
			file,
			"""
				[
				  {
				    "id": 1,
				    "orderNumber": "A-001",
				    "status": "NEW"
				  },
				  {
				    "id": 2,
				    "orderNumber": "A-002",
				    "status": "PROCESSING"
				  },
				  {
				    "id": 3,
				    "orderNumber": "A-003",
				    "status": "NEW"
				  }
				]
				""");

		JsonFileParser parser = new JsonFileParser();
		Map<String, Integer> result = parser.processFile(file, "status");

		assertEquals(2, result.get("NEW"));
		assertEquals(1, result.get("PROCESSING"));
		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("Should ignore empty or blank status values")
	void testProcessFile_blankStatus() throws IOException {
		Path file = tempDir.resolve("order_blank.json");

		Files.writeString(
			file,
			"""
				[
				  {
				    "id": 1,
				    "status": ""
				  },
				  {
				    "id": 2,
				    "status": " "
				  },
				  {
				    "id": 3,
				    "status": "CANCELLED"
				  }
				]
				""");

		JsonFileParser parser = new JsonFileParser();
		Map<String, Integer> result = parser.processFile(file, "status");

		assertEquals(1, result.size());
		assertEquals(1, result.get("CANCELLED"));
	}

	@Test
	@DisplayName("Should support comma-separated status values")
	void testProcessFile_commaSeparatedStatus() throws IOException {
		Path file = tempDir.resolve("order_multi_status.json");

		Files.writeString(
			file,
			"""
				{
				  "id": 10,
				  "status": "NEW, PROCESSING, SHIPPED"
				}
				""");

		JsonFileParser parser = new JsonFileParser();
		Map<String, Integer> result = parser.processFile(file, "status");

		assertEquals(1, result.get("NEW"));
		assertEquals(1, result.get("PROCESSING"));
		assertEquals(1, result.get("SHIPPED"));
		assertEquals(3, result.size());
	}

	@Test
	@DisplayName("Should throw NullPointerException when file is null")
	void testProcessFile_nullFile() {
		JsonFileParser parser = new JsonFileParser();
		assertThrows(NullPointerException.class,
			() -> parser.processFile(null, "status"));
	}

	@Test
	@DisplayName("Should throw IllegalArgumentException for empty attribute")
	void testProcessFile_emptyAttribute() {
		JsonFileParser parser = new JsonFileParser();
		Path testJson = tempDir.resolve("test-json.json");

		assertThrows(IllegalArgumentException.class, () -> parser.processFile(testJson, ""));
	}
}
