package edu.internship.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Encapsulates application configuration.
 */
public class AppConfig {
	private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

	private final Path inputFolder;
	private final String attribute;
	private final int threads;

	public static AppConfig fromArgs(String[] args) {
		if (args == null || args.length < 2) {
			throw new IllegalArgumentException("Expected at least 2 arguments: <folder> <attribute> [threads]");
		}

		String folderRaw = args[0];
		String attributeRaw = args[1];
		int threads = DEFAULT_THREADS;

		if (args.length >= 3) {
			try {
				threads = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Threads argument must be an integer: " + args[2]);
			}
		}

		return new AppConfig(folderRaw, attributeRaw, threads);
	}

	// Constructor: Handles validation logic (Business Rules)
	public AppConfig(String folderPath, String attribute, int threads) {
		// 1. Validate Folder
		Objects.requireNonNull(folderPath, "Folder path cannot be null");
		if (folderPath.isBlank()) {
			throw new IllegalArgumentException("Folder path cannot be empty");
		}
		Path path = Path.of(folderPath);
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("Folder does not exist: " + path.toAbsolutePath());
		}
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path is not a directory: " + path.toAbsolutePath());
		}
		if (!Files.isReadable(path)) {
			throw new IllegalArgumentException("Folder is not readable: " + path.toAbsolutePath());
		}
		this.inputFolder = path;

		// 2. Validate Attribute
		if (attribute == null || attribute.isBlank()) {
			throw new IllegalArgumentException("Attribute cannot be null or empty");
		}
		this.attribute = attribute.trim();

		// 3. Validate Threads
		if (threads <= 0) {
			throw new IllegalArgumentException("Threads number must be a positive integer, got: " + threads);
		}
		this.threads = threads;
	}

	public Path getInputFolder() {
		return inputFolder;
	}

	public String getAttribute() {
		return attribute;
	}

	public int getThreads() {
		return threads;
	}
}