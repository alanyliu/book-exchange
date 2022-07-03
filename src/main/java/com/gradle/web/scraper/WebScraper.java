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
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT/*.withHeader("ISBN", "Avg New Price",
                    "Low New Price", "High New Price", "Avg Used Price", "Low Used Price", "High Used Price")*/);
            csvPrinter.printRecord("ISBN", "Avg New Price", "Low New Price", "High New Price", "Avg Used Price",
                    "Low Used Price", "High Used Price");

            // HashSet<String> bookISBNs = readBookISBNs();
//            HashSet<String> bookISBNs = new HashSet<>();
//            bookISBNs.add("9780205116140");
//            bookISBNs.add("9780133687187");
            HashSet<String> bookISBNs = new HashSet<String>(Arrays.asList("9780205116140", "9780133687187", "9780544784680", "9781524710002", "9780030647895",
                    "0030499682", "9787559539418", "9780030647727", "9781464156410", "9781457699917", "9780132041447", "9780321696816", "9780525568315", "9780525569008", "9780525569442", "9780525569688", "9780471488859", "9780030941931", "9780030941979", "9780030941955", "9781319194444",
                    "1934780146", "9781559539418", "9780133669510", "9780131846616", "9780812997316",
                    "1565770390", "9780321696816", "9780321879721", "9781559539418", "9780131846616", "9780133114751", "9781524710804", "9781438007915", "9781438007434", "9780030941962", "9780071763677", "9781464164743", "9781438009605", "9781543301397", "9781259863998", "9781133611103", "9781438011233", "9781438007427", "9781438008592", "9781438008592", "9781438076829", "9781438009049", "9781565771468", "9780132492539", "9780321879721", "9781524757977", "9780525568322", "9780525568384", "9781680043235", "9780134685762", "9781543301380", "9780133114751", "9781565771314", "9780936981468", "9780521659604", "9781107690639", "9780142401125", "9781591412854", "9781565775091", "9781565775091", "9780142407332", "9780764146824", "9780525567493", "9781101919903", "9781524758189", "9780135128442", "9781503262423", "9781559539418", "9780812987140", "9780821580103", "9780936981468", "9780521659604", "9780521659604", "9781426336713", "9781591417835", "9781591417835", "9781565775091", "9781565775091", "9781600325403", "9781600325403", "9781457604362", "9781457673825", "9780738612263", "9781438009193", "9781680046878", "9781260019995", "9781260019995", "9781260455854", "9781565770423", "9781565770423", "9781565770393", "9781565774858", "9781565771437", "9781500614195", "9781500965631", "9781501040559", "9781501040566", "9781503019706", "9781507544167", "9780030647741", "9780030647741", "9781438012629", "9781438012865", "9780738609713", "9780738609720", "9781438012926", "9780738611907", "9781438004976", "9781438008646", "9781438010670", "9781438010694", "9781438011684", "9781438076515", "9781118087886", "9780132492539", "9780132492539", "9780544784680", "9781441488879", "9781284055917", "9781524710002", "9781524710002", "9780525568179", "9781524710040", "9781101919897", "9780525568322", "9780525568322", "9781101920008", "9781680043235", "9780134324821", "9780135128442", "9781631891120", "9780030994135", "9780030500725", "9790030499325", "9780030500497", "9780030500497", "9781934124109", "9781934124116", "9781429218276", "9781559539418", "9781559539418", "9780030647727", "9780133687187", "9780321625922", "9781118853320", "9781934124215", "9780133651003", "9781337288828", "9780133691733", "9780865164871", "9780133114751", "9780821580097", "9780060956424", "9780078933141", "9780078298165", "9781565771482", "9781565770393", "9780822200161", "9780061130243", "9781133611097", "9781503262423", "9781930398191", "9781438011684", "9780525568193", "9780525568254", "9780970579515", "9781506349985", "9780821571118", "9781319194444", "9781565771406", "9781680043228", "9780525569428", "9780374534134", "9780684830957", "9781503219700", "9781680043235", "9780141439587", "9780300106541", "9781464109478", "9780393930252", "9781936023387", "9781565770393", "9781438010656", "9781438010717", "9780321879721", "9780525569510", "9780812987140", "9780142437339", "9780374104191", "9780199537129", "9780738609713", "9780990724308", "9780030941979", "9781613822913", "9780133114751", "9781260454918", "9781565770393", "9781438010663", "9781438010656", "9781438008684", "9781438008592", "9781438011080", "9780525567509", "9780525568070", "9781457312199", "9780195212099", "9781628455151", "9781628455021", "9781565771468", "9781457309328", "9780374104191", "9780738607061", "9781934780404", "9780865167780", "9780030941979", "9780852291634", "9781107615380", "9781559539418", "9781559539418", "9780891308713", "9780806136356", "9780132854580", "9780865167896", "9780865167896", "9780821580103", "9780821571118", "9780061997228", "9780394744124", "9781438007915", "9780061130243", "9780399588198", "9780321879721", "9780525567646", "9780525567646", "9780525567509", "9780525568070", "9780486264646", "9780300106541", "9781457312199", "9781628455151", "9781628455021", "9781429215978", "9780141393704", "9780521290012", "9780679760801", "9781457309328", "9780140444254", "9780312167042", "9780486268651", "9781565770393", "9781133611097", "9781133611097", "9780133669510", "9781464109478", "9781464109478", "9780133114751", "9781319195366", "9781438011684", "9780525568209", "9781680043228", "9781680043235", "9780030941979", "9781429218276", "9781133611103", "9780134143323", "9780470903247", "9780132854580", "9780812987140", "9780821580103", "9780874476545", "9781565771499", "9781438010649", "9781101919866", "9781524758158", "9780030796791", "9780030947308", "9780030499685", "9780064405775", "9780874478525", "9781453090497", "9781481414777", "9780821580073", "9781259588068", "9781260123296", "9781438007892", "9781600320156", "9781600320156", "9781680046878", "9781680043228", "9780470149270", "9781118230725", "9781506251127", "9781565771826", "9781565771482", "9781930398191", "9781457309212", "9780874479751", "9781260441994", "9781319194444", "9781565771406", "9781438010656", "9781680043228", "9781680043235", "9780132011556", "9780812987140", "9780026838047", "9780133651133", "9780133651164", "9781565770393", "9781133611103", "9781292277769", "9780395977279", "9780030941924", "9780030994135", "9781565775091", "9781319194444", "9781319194444", "939798379", "9781565771406", "9781565771437", "9781319195366", "9780521004343", "9780321696816", "9780133647495", "9780030941979", "9780134324821", "9780078742514", "9780133669510", "9780133687187", "9780195215502", "9781464109478", "9780061259180", "9780865164871",
                    "13035239", "9780838822661", "9781457699917", "9780060956424", "9780078933141", "9781438010656", "9780525569503", "9781421785103", "9781565770393", "9781565770393", "9780977810598", "9780977810598", "9780133669510", "9780131846616", "9780321625922",
                    "1565771346", "9780977810598", "9781680043198", "9781680044898", "9780030994173", "9780030648021", "9780078799815", "9780030994135", "9780030647840", "9780030648076", "9780030499579", "30501229", "9780328521005", "9780328521005", "9780415996518", "9780078281754", "9780133687187", "9780133669510", "9780131846616", "9780133647495", "9781591412519", "9781565775091", "9781591413288", "9780821580103", "9780821580097", "9780821580110", "9780030647895", "9780030647925", "9780078933141", "9780887277115", "9780887277245", "9780887277269", "9780887277276", "9780887277412", "9780887277696", "9781118087886", "9780132014083", "9780030941979", "9781626800335", "9781629745190", "9780495108351", "9780821580103", "9780061997228", "9781680043419", "9782809914122", "9780133687187", "9780495011637", "9780030941962", "9781429215978", "9780078933141", "9780525569596", "9781565770423", "9781565770393", "9781565771406", "9781565771406", "9780525569497", "9781118087886", "9780134322766", "9780078933141", "9780030796791", "9780030941955", "9780030255410", "9780030499326", "9790030499684", "9780030499579", "9780525569534", "9780887277245", "9780887277382", "9780887277399", "9781260466942", "9781506262116", "9781506258928", "9781429215978", "9780134685762", "9780132854580", "9781565771468", "9781133611103", "9780321879721", "9781118230725", "9780030648021", "9780078799815", "9780030941955", "9780030255410", "9780030499326", "9780030499579", "9780821580080"));
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
            //  List<HtmlDivision> priceCol = page.getByXPath("//div[contains(@class, 'results-table-center')]");

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

    public static HashSet<String> readBookISBNs() {
        HashSet<String> ids = new HashSet<>();
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
