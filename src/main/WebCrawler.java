package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class WebCrawler {
    private static final int MAX_DEPTH = 2;
    private static final int MAX_VISIT = 100;
    private static final ArrayList<String> visitNext = new ArrayList<>();
    private static final ArrayList<String> visitAfter = new ArrayList<>();
    private static final Map<String, Integer> searchWords = new HashMap<>();
    private static final List<Integer> currentPageWordCount = new ArrayList<>();
    private static PrintWriter file;
    private static final ArrayList<String> visitedLinks = new ArrayList<>();

    /**
     * Main function of the program. Sets up the search keywords, CSV file and calls the
     * crawler function.
     */
    public static void main(String[] args) {
        searchWords.put("Tesla", 0);
        searchWords.put("Musk", 0);
        searchWords.put("Gigafactory", 0);
        searchWords.put("Elon Mask", 0);
        visitNext.add("https://en.wikipedia.org/wiki/Elon_Musk");
        file = CSVHandler.createCSV("results.csv", searchWords.keySet());
        WebCrawler crawler = new WebCrawler();
        crawler.crawlSites(visitNext, 0);
        generateTop10("results.csv", "top10.csv");
    }

    /**
     * Recursively crawls through the web. Gets all links on a page, calculates the required
     * keywords and saves the data to a CSV. Then continues to the next link in the list.
     *
     * @param toVisit the list of links to be visited in on the current depth
     * @param depth   current depth of the search
     */
    public void crawlSites(ArrayList<String> toVisit, int depth) {
        visitAfter.clear();
        if (depth <= MAX_DEPTH && visitedLinks.size() <= MAX_VISIT) {
            // for every URL in the toVisit array
            for (String URL : toVisit) {
                // if the URL hasn't been visited and the program has visited less than MAX_VISIT URLs
                if ((!visitedLinks.contains(URL)) && visitedLinks.size() < MAX_VISIT) {

                    visitedLinks.add(URL);
                    System.out.println("Visited: " + visitedLinks.size() + ", Current site: [" + URL + "]");

                    // Try to visit the page and get data
                    try {
                        Document document = Jsoup.connect(URL).ignoreContentType(true).get();
                        // Gets all links on the current page
                        Elements linksOnPage = document.select("a[href]");
                        // Adds linksOnPage to be visited in the next level of depth
                        for (Element page : linksOnPage) {
                            String currentURL = page.attr("abs:href");
                            // Does not add links to the same page (for example link to the search bar)
                            if (!currentURL.contains("#")) {
                                visitAfter.add(currentURL);
                            }
                        }
                        // Extracts all text from HTML file
                        Element body = document.body();
                        String allText = body.text();
                        // For every word in searchWords, counts that word in the website and increments
                        // the total counter
                        for (String key : searchWords.keySet()) {
                            int currentValue = searchWords.get(key);
                            int valueInPage = (allText.split(Pattern.quote(key), -1).length) - 1;
                            searchWords.put(key, currentValue + valueInPage);
                            currentPageWordCount.add(valueInPage);
                        }
                        if (file == null) {
                            System.out.println("Could not write to the file (file might be open)");
                        } else {
                            CSVHandler.writeToCSV(file, URL, currentPageWordCount);
                        }
                        currentPageWordCount.clear();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    // if the website was already visited, duplicates is incremented
                }
            }
            // recursively calls the function with the new array of URLs
            crawlSites(new ArrayList<>(visitAfter), ++depth);
        }
    }


    /**
     * Takes a file of all site data and generates a file with top 10 sites that have the most
     * key-words.
     *
     * @param readFrom file name of the file that holds all site data
     * @param fileName file name of the file that will be created with top 10 results
     */
    private static void generateTop10(String readFrom, String fileName) {
        // Creates a file to write top 10 results to
        PrintWriter top10 = CSVHandler.createCSV(fileName, searchWords.keySet());
        List<String> topSites = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(readFrom))) {
            String line;
            int totalWords;
            // for every line in the results file, reads line
            while ((line = reader.readLine()) != null) {
                totalWords = getTotal(line);
                // skips the header
                if (totalWords == -1) {
                    continue;
                }
                // adds first 10 lines
                if (topSites.size() < 10) {
                    topSites.add(line);
                } else {
                    // inserts the new line in its appropriate position in topSites
                    for (int i = 0; i <= topSites.size() - 1; i++) {
                        if (getTotal(topSites.get(i)) < totalWords) {
                            topSites.add(i, line);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // adds top ten sites to the top10 file
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println(getTotal(topSites.get(i)));
                top10.println(topSites.get(i));
                top10.flush();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * Gets the sum of all keywords in a website. Takes a line and adds the numbers
     * of all keyword counts together.
     *
     * @param line a String containing a line from the results file
     * @return returns an integer which is the total number of keywords in the site
     */
    public static int getTotal(String line) {
        List<String> currentLine = List.of(line.split(","));
        // if current line is header line, returns -1
        if (Objects.equals(currentLine.get(0), "URL")) {
            return -1;
        }
        int totalOnSite = 0;
        // calculates the total number of words (adds all word counts) and returns it
        for (int i = currentLine.size() - 4; i <= currentLine.size() - 1; i++) {
            totalOnSite += Integer.parseInt(currentLine.get(i));
        }
        return totalOnSite;
    }
}