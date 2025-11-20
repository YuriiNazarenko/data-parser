package edu.internship.cli;

import java.nio.file.Files;
import java.nio.file.Path;

public class CommandLineArgsValidator {

  private static final String ERROR_THREADS_INVALID = "Threads number must be a positive integer: ";
  private static final String ERROR_FOLDER_NULL = "Folder path cannot be null";
  private static final String ERROR_FOLDER_EMPTY = "Folder path cannot be empty";
  private static final String ERROR_NOT_EXIST = "Folder does not exist: ";
  private static final String ERROR_NOT_FOLDER = "Path is not a directory: ";
  private static final String ERROR_NOT_READABLE = "Folder is not readable: ";
  private static final String ERROR_ARGS_LENGTH = "Expected at least 2 arguments: <folder> <attribute>";
  private static final String ERROR_ARGS_NULL = "Arguments array cannot be null";

  public static ValidatedArguments validate(String[] args) {
    validateArgsLength(args);

    Path folderPath = validateFolderArg(args[0]);
    String attribute = validateAttribute(args[1]);

    Integer threads = null;
    if (args.length >= 3) {
      threads = validateThreadsNumberArg(args[2]);
    }

    return new ValidatedArguments(folderPath, attribute, threads);
  }

  public static int validateThreadsNumberArg(String threadValue) {
    if (threadValue == null || threadValue.isBlank()) {
      throw new IllegalArgumentException(ERROR_THREADS_INVALID + threadValue);
    }

    try {
      int n = Integer.parseInt(threadValue.trim());
      if (n <= 0) {
        throw new IllegalArgumentException(ERROR_THREADS_INVALID + threadValue);
      }
      return n;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(ERROR_THREADS_INVALID + threadValue);
    }
  }

  public static void validateArgsLength(String[] args) {
    if (args == null) {
      throw new IllegalArgumentException(ERROR_ARGS_NULL);
    }
    if (args.length < 2) {

      throw new IllegalArgumentException(ERROR_ARGS_LENGTH);
    }
  }

  public static Path validateFolderArg(String folderPath) {
    if (folderPath == null) {
      throw new IllegalArgumentException(ERROR_FOLDER_NULL);
    }

    if (folderPath.trim().isEmpty()) {
      throw new IllegalArgumentException(ERROR_FOLDER_EMPTY);
    }

    Path folder = Path.of(folderPath);

    if (!Files.exists(folder)) {
      throw new IllegalArgumentException(ERROR_NOT_EXIST + folder.toAbsolutePath());
    }
    if (!Files.isDirectory(folder)) {
      throw new IllegalArgumentException(ERROR_NOT_FOLDER + folder.toAbsolutePath());
    }
    if (!Files.isReadable(folder)) {
      throw new IllegalArgumentException(ERROR_NOT_READABLE + folder.toAbsolutePath());
    }

    return folder;
  }

  public static String validateAttribute(String attribute) {
    if (attribute == null) {
      throw new IllegalArgumentException("Attribute cannot be null");
    }
    String trimmed = attribute.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Attribute cannot be empty");
    }
    return trimmed;
  }

  public static class ValidatedArguments {
    private final Path path;
    private final String attribute;
    private final Integer threads;

    public ValidatedArguments(Path path, String attribute, Integer threads) {
      this.path = path;
      this.attribute = attribute;
      this.threads = threads;
    }

    public Path getPath() {
      return path;
    }

    public String getAttribute() {
      return attribute;
    }

    public Integer getThreads() {
      return threads;
    }
  }
}
