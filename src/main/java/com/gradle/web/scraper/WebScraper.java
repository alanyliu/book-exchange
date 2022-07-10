package com.gradle.web.scraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;


public class WebScraper {
    public static void main(String[] args) throws IOException{
        try {
            // Create CSVPrinter class object
            Writer writer = Files.newBufferedWriter(Paths.get("book_prices.csv"));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT/*.withHeader("ISBN", "Avg New Price",
                    "Low New Price", "High New Price", "Avg Used Price", "Low Used Price", "High Used Price")*/);
            csvPrinter.printRecord("ISBN", "Avg New Price", "Low New Price", "High New Price", "Avg Used Price",
                    "Low Used Price", "High Used Price");

            HashSet<String> bookISBNs = getISBNs();
//            HashSet<String> bookISBNs = new HashSet<String>(Arrays.asList("9780205116140", "9780133687187",
//                    "9780544784680", "9781524710002", "9780030647895", "0030499682", "9787559539418", "9780030647727",
//                    "9781464156410", "9781457699917", "9780132041447", "9780321696816", "9780525568315",
//                    "9780525569008", "9780525569442", "9780525569688", "9780471488859", "9780030941931",
//                    "9780030941979", "9780030941955", "9781319194444", "1934780146", "9781559539418", "9780133669510",
//                    "9780131846616"));
            for (String isbn : bookISBNs) {
                String url = "https://www.bookfinder.com/search/?author=&title=&lang=en&isbn=" + isbn + "&new_used=*&destination=us&currency=USD&mode=basic&st=sr&ac=qr";
                double[] vals = webScrape(url);
                csvPrinter.printRecord(isbn, vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
            }

            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        try (
            Reader reader = Files.newBufferedReader(Paths.get("book_prices.csv"));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    /* .withHeader("ISBN", "Avg New Price", "Low New Price", "High New Price", "Avg Used Price",
                            "Low Used Price", "High Used Price").withIgnoreHeaderCase().withTrim()*/);
        ) {
            for (CSVRecord csvRecord : csvParser) {
                // Accessing values by the names assigned to each column
                String isbn = csvRecord.get("ISBN");
                String avgNewPrice = csvRecord.get("Avg New Price");
                String lowNewPrice = csvRecord.get("Low New Price");
                String highNewPrice = csvRecord.get("High New Price");
                String avgUsedPrice = csvRecord.get("Avg Used Price");
                String lowUsedPrice = csvRecord.get("Low Used Price");
                String highUsedPrice = csvRecord.get("High Used Price");

                System.out.println("Record No - " + csvRecord.getRecordNumber());
                System.out.println("---------------");
                System.out.println("ISBN : " + isbn);
                System.out.println("Avg New Price : " + avgNewPrice);
                System.out.println("Low New Price : " + lowNewPrice);
                System.out.println("High New Price : " + highNewPrice);
                System.out.println("Avg Used Price : " + avgUsedPrice);
                System.out.println("Low Used Price : " + lowUsedPrice);
                System.out.println("High Used Price : " + highUsedPrice);
                System.out.println("---------------\n\n");
            }
        }
    }

    public static HashSet<String> getISBNs() throws GeneralSecurityException, IOException {
        return GoogleSheetsObj.readBookISBNs();
    }

    public static double[] webScrape(String url) {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        // total price of new/used books
        double totalNew = 0.0;
        double totalUsed = 0.0;

        // minimum price of new/used books
        double minNew = Double.POSITIVE_INFINITY;
        double minUsed = Double.POSITIVE_INFINITY;

        // maximum price of new/used books
        double maxNew = 0;
        double maxUsed = 0.0;

        // total number of entries for new/used books
        int numNew = 0;
        int numUsed = 0;

        // average price of new/used books
        double avgNew = 0.0;
        double avgUsed = 0.0;
        try {
            HtmlPage page = webClient.getPage(url);

            List<HtmlDivision> priceCol = page.getByXPath("/html/body//div[@class='yui-t7']//div[@id='bd-isbn']//div");

            boolean flag = false;
            boolean isUsed = false;
            for (int i = 0; i < priceCol.size(); i++) {
                // break from HtmlDivision since all data is acquired
                if (priceCol.get(i).toString().equals("HtmlDivision[<div id=\"results-group-rental\" class=\"results-group-rental\" align=\"center\">]") || priceCol.get(i).toString().equals("HtmlDivision[<div class=\"search-footer-item\">]")) {
                    break;
                }
                if (flag) {
                    double price = Double.parseDouble(priceCol.get(i).asNormalizedText().substring(1));
                    if (!isUsed) {
                        if (price > maxNew) {
                            maxNew = price;
                        }
                        if (price < minNew) {
                            minNew = price;
                        }
                        totalNew += price;
                        numNew++;

                        // check if next element of data is for a used book not new book
                        double nextPrice = Double.parseDouble(priceCol.get(i+1).asNormalizedText().substring(1));
                        if (price > nextPrice) {
                            isUsed = true;
                        }
                    }
                    else {
                        if (price > maxUsed) {
                            maxUsed = price;
                        }
                        if (price < minUsed) {
                            minUsed = price;
                        }
                        totalUsed += price;
                        numUsed++;
                    }
                }

                // begin tracking price data at next index
                if (priceCol.get(i).toString().equals("HtmlDivision[<div itemprop=\"offers\" itemscope=\"\" itemtype=\"http://schema.org/AggregateOffer\">]")) {
                    flag = true;
                }
            }

            avgNew = totalNew / numNew;
            avgUsed = totalUsed / numUsed;

            webClient.getCurrentWindow().getJobManager().removeAllJobs();
            webClient.close();

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return new double[]{avgNew, minNew, maxNew, avgUsed, minUsed, maxUsed};
    }
