package edu.internship.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JsonFileParser {

	private final JsonFactory factory = new JsonFactory();

	/**
	 * Processes a JSON file and extracts statistics for the specified attribute. The attribute value may contain multiple
	 * comma-separated items.
	 *
	 * @param file the JSON file path
	 * @param attribute the attribute name to extract (e.g. "status")
	 * @return a map where keys are extracted attribute values and values are counts
	 */
	public Map<String, Integer> processFile(Path file, String attribute) throws IOException {
		Objects.requireNonNull(file, "File path cannot be null");
		if (attribute == null || attribute.isBlank()) {
			throw new IllegalArgumentException("Attribute name cannot be null or blank");
		}

		Map<String, Integer> stats = new HashMap<>();

		// Create a streaming parser for efficient file traversal
		try (JsonParser parser = factory.createParser(file.toFile())) {
			JsonToken token = parser.nextToken();

			if (token == JsonToken.START_ARRAY) {
				// Root is an array of objects
				processArray(parser, attribute, stats);
			} else if (token == JsonToken.START_OBJECT) {
				// Root is a single object
				processObject(parser, attribute, stats);
			} else {
				// Ignore unexpected root values
				parser.skipChildren();
			}
		}

		return stats;
	}

	private void processArray(JsonParser parser, String attribute, Map<String, Integer> stats) throws IOException {
		while (parser.nextToken() != JsonToken.END_ARRAY) {
			if (parser.currentToken() == JsonToken.START_OBJECT) {
				processObject(parser, attribute, stats);
			} else {
				parser.skipChildren();
			}
		}
	}

	/**
	 * Processes a JSON object and extracts attribute values. If the target attribute contains comma-separated values,
	 * each value is counted separately.
	 */
	private void processObject(JsonParser parser, String attribute, Map<String, Integer> stats) throws IOException {
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String name = parser.currentName();
			parser.nextToken();

			if (attribute.equals(name)) {
				String raw = parser.getValueAsString();
				if (raw != null && !raw.isBlank()) {
					for (String part : raw.split(",")) {
						String key = part.trim();
						if (!key.isEmpty()) {
							stats.merge(key, 1, Integer::sum);
						}
					}
				}
			} else {
				parser.skipChildren();
			}
		}
	}
}