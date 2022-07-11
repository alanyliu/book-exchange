package com.gradle.web.scraper;

import java.util.*;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
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
//        List<String> ranges = Arrays.asList("E1", "E4");
//        BatchGetValuesResponse readResult = sheetsService.spreadsheets().values()
//                .batchGet(SPREADSHEET_ID)
//                .setRanges(ranges)
//                .execute();
        ValueRange readResult = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, "A:A")
                .execute();
        int numRows = readResult.getValues() != null ? readResult.getValues().size() : 0;
        System.out.printf("%d rows were retrieved.", numRows);
        return readResult;
    }
}
