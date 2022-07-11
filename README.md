# book-exchange
Created by Alan Liu, Co-founder of the BISV Book Exchange. 

Start Date: Dec. 2021

## Description:
The goal is to extrapolate key pricing information to work toward automating the book purchasing process of textbooks and required texts at BISV by reading in data and/or book ISBNs from a Google sheets master book list and web scraping from BookFinder with Gradle and Java. The extracted information may give useful insights into setting prices for books, as well as storing data for future purposes to avoid the need for manual price setting. Increased efficiency in the backend of the book transaction process will help the Book Exchange raise funds more effectively for the school supplies drive, which donates supplies to schools across the Bay Area of California.

Learn more about the Book Exchange here: https://the-bobcat-book-exchange.herokuapp.com.

(Legacy website: https://sites.google.com/view/book-exchange-portal/home?authuser=0)

## Repositories:
  - Maven Central
  - Google
Note: Gradle can utilize dependencies that are found in the Google and/or Maven central repository.

## Dependencies:
  - Jupiter API
  - htmlunit
  - Apache Commons
  - Google API Services Sheet
  - Google API Client
  - Google OAuth Client
  
Data stored in Commons CSV format.
  
A portion of the project in Python is also available in this repo (see main.py, requests.py, and setup.py). Below are the necessary dependencies: 
  - pandas
  - requests
  - BeautifulSoup
  - gspread & ServiceAccountCredentials
  - pprint
 
## Credits:
  - Baeldung
  - Sheets API for Google Developers
