package utilities;
import java.util.Scanner;

/**
 * ScannerUtility is a utility class that provides a static Scanner instance for user input.
 * This class is designed to be used throughout the application to avoid creating multiple Scanner instances.
 */
public class ScannerUtility {
    public static final Scanner SCANNER = new Scanner(System.in);
}