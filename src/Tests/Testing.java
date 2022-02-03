package Tests;

import main.CSVHandler;
import main.WebCrawler;

import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.*;

public class Testing {

    @Test
    public void checkCreateCSVReturnObject() {
        try {
            PrintWriter actualFile = CSVHandler.createCSV("test.csv", new HashSet<String>(Arrays.asList("a", "b", "c")));
            Assert.assertEquals(PrintWriter.class, actualFile.getClass());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testIfGetTotalReturnsCorrect() {
        int actualResult = WebCrawler.getTotal("Text,10,20,30,40");
        int expectedValue = 100;
        Assert.assertEquals(expectedValue, actualResult);
    }

    @Test
    public void testIfGetTotalIgnoresHeader() {
        int actualResult = WebCrawler.getTotal("URL,Data1,Data2,Data3");
        int expectedValue = -1;
        Assert.assertEquals(expectedValue, actualResult);
    }
}
