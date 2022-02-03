package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class CSVHandler {

    /**
     * Writes given data to a CSV file.
     *
     * @param file   the file to which data will be appended
     * @param URL    the URL of the current website
     * @param values an array of integers, each representing the amount of keywords found on the website
     */
    public static void writeToCSV(PrintWriter file, String URL, List<Integer> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(URL);
        sb.append("\"");
        // appends all values to the StringBuilder
        for (int val : values) {
            sb.append(",");
            sb.append(val);
        }
        try {
            // Prints the StringBuilder to the file
            file.println(sb.toString());
            file.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Generates a CSV file with the provided fileName. The file contains search keywords as header.
     * If such file exists, it is overridden.
     *
     * @param fileName name of the file to be created
     * @return PrintWriter file, to which data can be appended
     */
    public static PrintWriter createCSV(String fileName, Set<String> searchWords) {
        // generates a StringBuilder of the header
        StringBuilder sb = new StringBuilder();
        sb.append("URL");
        for (String word : searchWords) {
            sb.append(",");
            sb.append(word);
        }

        try {
            Files.deleteIfExists(Path.of(fileName));
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(sb.toString());
            pw.flush();
            return pw;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
