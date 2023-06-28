package com.gradle.web.scraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.security.GeneralSecurityException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import com.google.api.services.sheets.v4.model.ValueRange;


public class WebScraper {
    final static int totalStats = 8;

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        List<List<Double>> priceStats = new ArrayList<>();
        for (int i = 0; i < totalStats; i++) {
            priceStats.add(new ArrayList<>());
        }

        try {
            // Create instance of CSVPrinter class
            Writer writer = Files.newBufferedWriter(Paths.get("book_prices.csv"));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(
                    "ISBN", "Low New Price", "High New Price", "Avg New Price", "Median New Price", "Low Used Price",
                    "High Used Price", "Avg Used Price", "Median Used Price")
                    .build());

            // Get all book ISBNs for scraping
            List<String> bookISBNs = getISBNs();
            System.out.println(bookISBNs);

            // Scrape book price data for each ISBN-corresponding book
            for (String isbn : bookISBNs) {
                String url = "https://www.bookfinder.com/search/?author=&title=&lang=en&isbn=" + isbn + "&new_used=*&destination=us&currency=USD&mode=basic&st=sr&ac=qr";
                double[] vals = webScrape(url);
                csvPrinter.printRecord(isbn, vals[0], vals[1], vals[2], vals[3], vals[4], vals[5], vals[6], vals[7]);
                for (int i = 0; i < totalStats; i++) {
                    priceStats.get(i).add(vals[i]);
                }
            }

            // Flush data into CSV file
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read and print data from CSV file
        try (
            Reader reader = Files.newBufferedReader(Paths.get("book_prices.csv"));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader(
                    "ISBN", "Low New Price", "High New Price", "Avg New Price", "Median New Price", "Low Used Price",
                    "High Used Price", "Avg Used Price", "Median Used Price")
                    .build());
        ) {
            for (CSVRecord csvRecord : csvParser) {
                // Accessing values by the names assigned to each column
                String isbn = csvRecord.get("ISBN");
                String lowNewPrice = csvRecord.get("Low New Price");
                String highNewPrice = csvRecord.get("High New Price");
                String avgNewPrice = csvRecord.get("Avg New Price");
                String medianNewPrice = csvRecord.get("Median New Price");
                String lowUsedPrice = csvRecord.get("Low Used Price");
                String highUsedPrice = csvRecord.get("High Used Price");
                String avgUsedPrice = csvRecord.get("Avg Used Price");
                String medianUsedPrice = csvRecord.get("Median Used Price");

                if (isbn.equals("ISBN"))
                    continue;

                // Printing data to console
                System.out.println("Record No - " + (csvRecord.getRecordNumber() - 1));
                System.out.println("---------------");
                System.out.println("ISBN : " + isbn);
                System.out.println("Low New Price : " + lowNewPrice);
                System.out.println("High New Price : " + highNewPrice);
                System.out.println("Avg New Price : " + avgNewPrice);
                System.out.println("Median New Price : " + medianNewPrice);
                System.out.println("Low Used Price : " + lowUsedPrice);
                System.out.println("High Used Price : " + highUsedPrice);
                System.out.println("Avg Used Price : " + avgUsedPrice);
                System.out.println("Median Used Price : " + medianUsedPrice);
                System.out.println("---------------\n\n");
            }
        }

        // Update Google Sheets file with book price statistics
        try {
            GoogleSheetsObj.updatePriceData(priceStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getISBNs() throws GeneralSecurityException, IOException {
        // Obtain ValueRange object with ISBN entries from Google Sheets
        ValueRange result = GoogleSheetsObj.readBookISBNs();

        // Convert result into ArrayList with non-ISBN entries excluded
        ArrayList<Object> prelimISBNs = new ArrayList<>(result.getValues());
        ArrayList<String> retISBNs = new ArrayList<>();
        for (Object val : prelimISBNs) {
            try {
                @SuppressWarnings("unchecked")
                String possISBN = ((ArrayList<String>) val).get(0);
                if (possISBN.matches("^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$")) {
                    retISBNs.add(possISBN);
                }
            } catch (Exception ignored) {
            }
        }

        // Return ArrayList with comprehensive list of ISBNs
        return new ArrayList<>(retISBNs);
    }

    public static double[] webScrape(String url) {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        // minimum price of new/used books
        double minNew = Double.POSITIVE_INFINITY;
        double minUsed = Double.POSITIVE_INFINITY;

        // maximum price of new/used books
        double maxNew = 0.0;
        double maxUsed = 0.0;

        // total price of new/used books
        double totalNew = 0.0;
        double totalUsed = 0.0;

        // total number of entries for new/used books
        int numNew = 0;
        int numUsed = 0;

        // average price of new/used books
        double avgNew = 0.0;
        double avgUsed = 0.0;

        // lists for tracking the median prices
        List<Double> newList = new ArrayList<>();
        List<Double> usedList = new ArrayList<>();
        double medianNew = 0.0;
        double medianUsed = 0.0;

        try {
            HtmlPage page = webClient.getPage(url);

            // scrape HTML tags with prices
            List<HtmlDivision> priceCol = page.getByXPath("/html/body//div[@class='yui-t7']//div[@id='bd-isbn']//div");

            // scrape HTML tags with new and/or used book labels
            int length = page.getByXPath("/html/body//div[@class='yui-t7']//div[@id='bd-isbn']//div//table//h3").size();
            List<String> bookQualities = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                DomElement bookQuality = (DomElement) page.getByXPath("/html/body//div[@class='yui-t7']//div[@id='bd-isbn']//div//table//h3").get(i);
                if (bookQuality.getChildNodes().get(0).getNodeValue().toLowerCase().contains("new")) {
                    bookQualities.add("new");
                } else if (bookQuality.getChildNodes().get(0).getNodeValue().toLowerCase().contains("old")) {
                    bookQualities.add("old");
                }
            }

            boolean flag = false;
            boolean isUsed = false;
            if (bookQualities.size() == 1 && bookQualities.contains("old")) {
                isUsed = true;
            }
            for (int i = 0; i < priceCol.size(); i++) {
                // break from HtmlDivision since all data is acquired
                if (priceCol.get(i).toString().equals("HtmlDivision[<div id=\"results-group-rental\" class=\"results-group-rental\" align=\"center\">]") ||
                        priceCol.get(i).toString().equals("HtmlDivision[<div class=\"search-footer-item\">]")) {
                    break;
                }
                if (flag) {
                    double price = Double.parseDouble(priceCol.get(i).asNormalizedText().substring(1));
                    System.out.println(price);

                    // handle new book prices
                    if (!isUsed) {
                        // update max price for new book
                        if (price > maxNew) {
                            maxNew = price;
                        }

                        // update min price for new book
                        if (price < minNew) {
                            minNew = price;
                        }

                        // update variables for calculating average price of new book
                        totalNew += price;
                        numNew++;

                        // append new book price to newList
                        newList.add(price);

                        // check if next element of data is for a used book not new book
                        double nextPrice;
                        try {
                            nextPrice = Double.parseDouble(priceCol.get(i + 1).asNormalizedText().substring(1));
                        } catch (Exception e) {
                            break;
                        }
                        if (price > nextPrice) {
                            isUsed = true;
                        }
                    }
                    // handle used book prices
                    else {
                        // update max price of used book
                        if (price > maxUsed) {
                            maxUsed = price;
                        }

                        // update min price of used book
                        if (price < minUsed) {
                            minUsed = price;
                        }

                        // update variables for calculating average price of new book
                        totalUsed += price;
                        numUsed++;

                        // append used book price to usedList
                        usedList.add(price);
                    }
                }

                // begin tracking price data at next index
                if (priceCol.get(i).toString().equals("HtmlDivision[<div itemprop=\"offers\" itemscope=\"\" itemtype=\"http://schema.org/AggregateOffer\">]")) {
                    flag = true;
                }
            }

            // set low and high prices for new and/or used books to -1 if none recorded
            if (numNew == 0) {
                maxNew = -1;
                minNew = -1;
            }
            if (numUsed == 0) {
                maxUsed = -1;
                minUsed = -1;
            }

            // determine average prices for new and used books
            avgNew = numNew != 0 ? totalNew / numNew : -1;
            avgUsed = numUsed != 0 ? totalUsed / numUsed : -1;

            // determine median prices for new and used books
            if (newList.size() == 0) {
                medianNew = -1;
            } else {
                Collections.sort(newList);
                medianNew = newList.size() % 2 == 0 ? (newList.get(newList.size() / 2 - 1) + newList.get(newList.size() / 2)) / 2
                                                    : newList.get(newList.size() / 2);
            }
            if (usedList.size() == 0) {
                medianUsed = -1;
            } else {
                Collections.sort(usedList);
                medianUsed = usedList.size() % 2 == 0 ? (usedList.get(usedList.size() / 2 - 1) + usedList.get(usedList.size() / 2)) / 2
                                                      : usedList.get(usedList.size() / 2);
            }

            // cleanup
            webClient.getCurrentWindow().getJobManager().removeAllJobs();
            webClient.close();

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return new double[]{minNew, maxNew, avgNew, medianNew, minUsed, maxUsed, avgUsed, medianUsed};
    }
}
