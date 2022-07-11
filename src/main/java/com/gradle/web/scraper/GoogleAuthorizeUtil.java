package com.gradle.web.scraper;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

public class GoogleAuthorizeUtil {
    public static Credential authorize() throws IOException, GeneralSecurityException {
        String credentialLocation = System.getProperty("user.dir") + "/src/main/resources/credentials.json";

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new FileReader(credentialLocation));

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File(System.getProperty("user.dir") + "/src/main/resources")))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
