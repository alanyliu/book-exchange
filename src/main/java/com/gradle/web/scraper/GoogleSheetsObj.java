package com.gradle.web.scraper;

import java.util.*;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleSheetsObj {
    private static Sheets sheetsService;
    private static final String SPREADSHEET_ID = "**********";

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
        // Pre-processing of statistics for input to update function
        List<List<Object>> temp = new ArrayList<>();
        for (List<Double> priceStat : priceStats) {
            List<Object> subTemp = new ArrayList<>();
            for (Double val : priceStat) {
                if (val == -1) {
                    subTemp.add("N/A");
                } else {
                    subTemp.add(val);
                }
            }
            temp.add(subTemp);
        }

        // Set ValueRange object containing statistics to be inputted into Sheets
        ValueRange content = new ValueRange()
                .setValues(temp)
                .setMajorDimension("COLUMNS");
        
        // Execute the update command
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "AM2", content)
                .setValueInputOption("RAW")
                .execute();
    }
}
