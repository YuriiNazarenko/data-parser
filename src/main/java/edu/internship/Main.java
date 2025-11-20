package edu.internship;

import edu.internship.cli.CommandLineArgsValidator;
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
	// Number of available CPU cores as default thread pool size
	private static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

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
			CommandLineArgsValidator.ValidatedArguments validatedArguments = CommandLineArgsValidator.validate(args);

			Path folder = validatedArguments.getPath();
			String attribute = validatedArguments.getAttribute();
			int threads =
				validatedArguments.getThreads() != null ? validatedArguments.getThreads() : DEFAULT_THREAD_POOL_SIZE;

			JsonFileProcessor jsonFileProcessor = new JsonFileProcessor(threads);

			// Collect all JSON files from the folder
			List<Path> files = getJsonFiles(folder);
			long startTime = System.currentTimeMillis();
			// Process files concurrently and aggregate statistics
			Map<String, Integer> stat = jsonFileProcessor.processFiles(files, attribute);

			// Write statistics to XML in the "statistics" folder
			System.out.println("✓ Successfully completed!\n");
			XMLReportGenerator.writeStatisticsToFile(stat, attribute, Path.of("statistics"));
			long endTime = System.currentTimeMillis();
			System.out.printf("\nThreads: %d, execution time: %d ms%n", threads, (endTime - startTime));

		} catch (IllegalArgumentException e) {
			printUsage();
			System.err.println("Argument validation error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Input/output error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.err.println("File processing error: " + e.getMessage());
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
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.json")) {

			List<Path> result = new ArrayList<>();
			for (Path p : stream) {
				result.add(p);
			}
			return result;

		} catch (IOException e) {
			throw new RuntimeException("Cannot read JSON files in folder: " + folder, e);
		}
	}

	private static void printUsage() {
		System.out.println("\nUsage:");
		System.out.println("  java -jar data-parser-1.0-SNAPSHOT-shaded.jar <folder> <attribute> [threads]");
		System.out.println("\nExamples:");
		System.out.println("  java -jar data-parser-1.0-SNAPSHOT-shaded.jar ./data status");
		System.out.println("  java -jar data-parser-1.0-SNAPSHOT-shaded.jar ./data status tags 8");
	}
}
