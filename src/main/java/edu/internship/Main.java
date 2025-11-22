package edu.internship;

import edu.internship.config.AppConfig;
import edu.internship.parser.JsonFileProcessor;
import edu.internship.report.XMLReportGenerator;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
	/**
	 * Entry point of the application. Validates command line arguments, reads JSON files from a folder, processes them
	 * concurrently, and writes statistics to an XML file.
	 */
	public static void main(String[] args) {
		System.out.println("=".repeat(80));
		System.out.println("  JSON STATISTICS PARSER  ");
		System.out.println("=".repeat(80));

		try {
			// Validate command line arguments
			AppConfig config = AppConfig.fromArgs(args);

			JsonFileProcessor jsonFileProcessor = new JsonFileProcessor(config.getThreads());

			// Collect all JSON files from the folder
			List<Path> files = getJsonFiles(config.getInputFolder());

			long startTime = System.currentTimeMillis();

			// Process files concurrently and aggregate statistics
			Map<String, Integer> stat = jsonFileProcessor.processFiles(files, config.getAttribute());

			// Write statistics to XML in the "statistics" folder
			System.out.println("✓ Successfully completed! \n");
			XMLReportGenerator.writeStatisticsToFile(stat, config.getAttribute(), Path.of("statistics"));

			long endTime = System.currentTimeMillis();
			System.out.printf("\nThreads: %d, execution time: %d ms%n", config.getThreads(), (endTime - startTime));

		} catch (IllegalArgumentException e) {
			System.err.println("Configuration error: " + e.getMessage());
			printUsage();
		} catch (IOException e) {
			System.err.println("Input/output error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.err.println("File processing interrupted: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
		}
	}

	/**
	 * Reads all JSON files from a given folder.
	 *
	 * @param folder directory to scan
	 * @return list of JSON file paths
	 */
	private static List<Path> getJsonFiles(Path folder) {
		List<Path> result = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.json")) {
			for (Path p : stream) {
				result.add(p);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot read JSON files in folder: " + folder, e);
		}
		return result;
	}

	private static void printUsage() {
		System.out.println("\nUsage:");
		System.out.println("  java -jar app.jar <folder> <attribute> [threads]");
		System.out.println("\nExamples:");
		System.out.println("  java -jar app.jar ./data status");
		System.out.println("  java -jar app.jar ./data status 8");
	}
}