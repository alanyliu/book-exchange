package com.gradle.web.scraper;

import java.util.*;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleSheetsObj {
    private static Sheets sheetsService;
    private static final String SPREADSHEET_ID = "***********";

    public static void setup() throws IOException, GeneralSecurityException {
        sheetsService = SheetsServiceUtil.getSheetsService();
    }

    public static ValueRange readBookISBNs() throws IOException, GeneralSecurityException {
        setup();
        ValueRange readResult = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, "B:B")
                .execute();
        int numRows = readResult.getValues() != null ? readResult.getValues().size() : 0;
        System.out.printf("%d rows were retrieved.\n", numRows);
        return readResult;
    }

    public static void updatePriceData(List<List<Double>> priceStats) throws IOException, GeneralSecurityException {
//        for (int i = 0; i < priceStats.size(); i++) {
//            List<List<Object>> temp = new ArrayList<>();
//            for (int j = 0; j < priceStats.get(i).size(); j++) {
//                temp.add(Arrays.asList(priceStats.get(i).get(j)));
//            }
//            ValueRange body = new ValueRange()
//                    .setValues(temp)
//                    .setMajorDimension("COLUMNS");
//            UpdateValuesResponse result = sheetsService.spreadsheets().values()
//                    .update(SPREADSHEET_ID, "AM", body)
//                    .setValueInputOption("RAW")
//                    .execute();
//        }
    }
}
