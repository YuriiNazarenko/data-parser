package edu.internship.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static java.util.Objects.requireNonNull;

public class JsonFileProcessor {
	private final ExecutorService executor;
	private final JsonFileParser parser;

	public JsonFileProcessor(int threads) {
		if (threads <= 0) {
			throw new IllegalArgumentException("Thread count must be positive, got: " + threads);
		}
		this.executor = Executors.newFixedThreadPool(threads);
		this.parser = new JsonFileParser();
	}

	/**
	 * Processes a list of JSON files concurrently and aggregates statistics for the specified attribute (e.g., "status").
	 *
	 * @param files     list of JSON file paths
	 * @param attribute attribute name to extract and count
	 * @return a combined map of attribute values and their total occurrences
	 */
	public Map<String, Integer> processFiles(List<Path> files, String attribute) throws InterruptedException {
		requireNonNull(files, "File list cannot be null");
		if (files.isEmpty()) {
			return Collections.emptyMap();
		}
		if (attribute == null || attribute.isBlank()) {
			throw new IllegalArgumentException("Attribute cannot be null or blank");
		}

		List<Future<Map<String, Integer>>> processingFutures = new ArrayList<>(files.size());

		for (Path file : files) {
			processingFutures.add(executor.submit(() -> {
				try {
					return parser.processFile(file, attribute);
				} catch (IOException e) {
					System.err.println("Error reading file " + file + ": " + e.getMessage());
					return Collections.emptyMap();
				}
			}));
		}

		Map<String, Integer> global = new HashMap<>();

		// Merge statistics returned by each task
		for (Future<Map<String, Integer>> future : processingFutures) {
			try {
				Map<String, Integer> local = future.get();
				local.forEach((k, v) -> global.merge(k, v, Integer::sum));
			} catch (ExecutionException e) {
				System.err.println("Task execution failed: " + e.getCause().getMessage());
			}
		}

		shutdownExecutor();
		return global;
	}

	/**
	 * Shuts down the executor service.
	 *
	 * <p>Attempts a normal shutdown and waits up to 1 minute. If threads are still running, forces shutdown.
	 */
	private void shutdownExecutor() {
		executor.shutdown();
		try {
			boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
			if (!finished) {
				executor.shutdownNow();
				if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
					System.err.println("Executor did not terminate.");
				}
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}