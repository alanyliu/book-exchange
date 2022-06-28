package com.gradle.web.scraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.*;

import java.io.IOException;

import java.io.Writer;
import java.io.Reader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class WebScraper {
    public static void main(String[] args) throws IOException{
        try {
            // Create CSVPrinter class object
            Writer writer = Files.newBufferedWriter(Paths.get("book_prices.csv"));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ISBN", "Avg New Price",
                    "Low New Price", "High New Price", "Avg Used Price", "Low Used Price", "High Used Price"));

            // ArrayList<String> bookISBNs = readBookISBNs();
            ArrayList<String> bookISBNs = new ArrayList<>();
            bookISBNs.add("9780205116140");
            for (String isbn : bookISBNs) {
                String url = "https://www.bookfinder.com/search/?author=&title=&lang=en&isbn=" + isbn + "&new_used=*&destination=us&currency=USD&mode=basic&st=sr&ac=qr";
                double[] vals = webScrape(url);
                csvPrinter.printRecord(isbn, vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
            }

            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                Reader reader = Files.newBufferedReader(Paths.get("book_prices.csv"));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader("ISBN", "Avg New Price", "Low New Price", "High New Price", "Avg Used Price",
                                "Low Used Price", "High Used Price").withIgnoreHeaderCase().withTrim());
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

    public static double[] webScrape(String url) {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        double tot_new = 0.0;
        double min_new = Double.POSITIVE_INFINITY;
        double max_new = 0.0;
        double tot_used = 0.0;
        double min_used = Double.POSITIVE_INFINITY;
        double max_used = 0.0;
        double avg_new = 0.0;
        double avg_used = 0.0;
        try {
            HtmlPage page = webClient.getPage(url);

            List<HtmlDivision> priceCol = page.getByXPath("/html/body//div[@class='yui-t7']//div[@id='bd-isbn']//div");
            //  List<HtmlDivision> priceCol = page.getByXPath("//div[contains(@class, 'results-table-center')]");

            for (int i = 26; i < 76; i++) {
                double price = Double.parseDouble(priceCol.get(i).asNormalizedText().substring(1));
                tot_new += price;

                if (min_new > price) {
                    min_new = price;
                }
                if (max_new < price) {
                    max_new = price;
                }
            }
            avg_new = tot_new / 50;

            for (int j = 77; j < 126; j++) {
                double price = Double.parseDouble(priceCol.get(j).asNormalizedText().substring(1));
                tot_used += price;

                if (min_used > price) {
                    min_used = price;
                }
                if (max_used < price) {
                    max_used = price;
                }
            }
            avg_used = tot_used / 50;

//            System.out.println(priceCol);
//            System.out.println(priceCol.get(26).asNormalizedText());
//            System.out.println(priceCol.get(26).asNormalizedText().substring(1,6));
//            System.out.println(priceCol.get(76).asNormalizedText());

            webClient.getCurrentWindow().getJobManager().removeAllJobs();
            webClient.close();

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return new double[]{avg_new, min_new, max_new, avg_used, min_used, max_used};
    }

    public static ArrayList<String> readBookISBNs() {
        ArrayList<String> ids = new ArrayList<>();
        /* CODE HERE */
        return ids;
    }

//    private static Credential authorize() throws Exception {
//        String credentialLocation = System.getProperty("user.dir") + "/GoogleAPIKey/credentials.json";
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new FileReader(credentialLocation));
//
//        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
//
//        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow
//                .Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
//                .setDataStoreFactory(new FileDataStoreFactory(new File(System.getProperty("user.dir") + "/GoogleAPIKey")))
//                .setAccessType("offline")
//                .build();
//
//        return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, new LocalServerReceiver()).authorize("user");
//    }
//
//    public static String[][] getData(String spreadSheetId, String sheetName, String rangeDataToRead) throws Exception {
//        Sheets sheet = new Sheets(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), authorize());
//
//        List<List<Object>> data = sheet.spreadsheets().values()
//                .get(spreadSheetId, sheetName + "!" + rangeDataToRead)
//                .execute().getValues();
//
//        return convertToArray(data);
//    }
}
