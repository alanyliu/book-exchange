# book-exchange
Created by Alan Liu, former co-leader of the BISV Book Exchange. 

## Description:
This Java Gradle project extrapolates key pricing information for new and used books to help automate the process of price setting of required texts at BISV by reading in book ISBNs from a Google sheets master book list and web scraping from BookFinder for each ISBN. The extracted information can give useful insights into setting prices for books, as well as store the data for future purposes to avoid the need for manual action. Increased efficiency in the backend of the book transaction process will help the Book Exchange raise funds more effectively for the school supplies drive, which donates supplies to schools across the California Bay Area.

Learn more about the Book Exchange here: https://the-bobcat-book-exchange.herokuapp.com.

(Legacy website: https://sites.google.com/view/book-exchange-portal/home?authuser=0)

## Data Structure:
The data is outputted into an Excel file. It is also stored in a supplemental Commons CSV file for ease of use.

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

## How to Use
### Java and Gradle Installation
Before getting started, check that Java is installed on your laptop. To do so, enter `java -version` into Terminal or Command Prompt, and if not installed, please visit https://www.java.com/en/download/. Furthermore, make sure the Gradle tool is installed on your laptop as well. Please follow instructions at https://gradle.org/install/ for installing Gradle with SDK or manual installation on MacOS or Windows.

### Clone the Repository + Install Java Dependencies
Use `git clone` to clone the repository locally. Then, in an IDE of your choice that supports Java and Gradle (e.g. VSCode, IntelliJ, Eclipse), open the project. Note that you will need to install some Java dependencies in order for the code to run properly. See the list of dependencies to do so.

### Connect with Google Sheets API
Since the program interacts with the Google Sheets API to modify Excel files, if you want to test this part of the program, you should navigate to https://developers.google.com/sheets/api/quickstart/java and follow the instructions there and enable the API for your specific project. As an important note, please make sure that Step 5 is complete with the email you are using to interact with the Excel file added as a user at O-Auth Consent Screen > Test Users (even if you are logged in to your account, a user must be added in order for the program to gain access to the file).

For the test Excel file you are using, you can modify the ranges in the src/main/java/com/gradle/web/scraper/GoogleSheetsObj file from 'B' to whatever column is used for ISBNs, and 'AM1' to whatever cell for where you would like the pricing data to start being copied into.

Note: The specific book list document that the Book Exchange is using is limited access.


## Credits:
  - Baeldung
  - Sheets API for Google Developers
