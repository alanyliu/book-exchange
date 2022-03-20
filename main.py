# Alan Liu | Rice University '25
# /Users/alanliu/PycharmProjects/BookExchange/main.py
# Created 16-Dec-2021
# Code that parses through the master book list Google sheet(s) for the BISV Book Exchange organization.

# Can perform the following:
#   - Get price of a book
#   - Set price of a book, manually or using BookFinder
#   - Add a new book along with collection ID

# To-do:
#   - set_price_with_bookfinder
#   - More user-friendly (flexible options, other get/set methods if nec.)

# Useful websites:
# https://towardsdatascience.com/read-data-from-google-sheets-into-pandas-without-the-google-sheets-api-5c468536550
# https://realpython.com/python-requests/
# https://medium.com/daily-python/python-script-to-edit-google-sheets-daily-python-7-aadce27846c0


import pandas as pd
import math
import gspread
from oauth2client.service_account import ServiceAccountCredentials
import pprint


class BuyingFormReader:

    def __init__(self, url):
        self.df = self.read_form_data(url)

    def read_form_data(self, url):
        """
        Reads data from given book buying form url and returns necessary data components in DataFrame.
        :param url: Google sheets book buying form url
        :return: df_final: DataFrame of data from Google sheets
        """
        # title (2021) vs Book Title (2020)
        # customDate (2021) not found in 2020
        df_init = pd.read_csv(url)
        df_final = df_init([['ISBN', 'Book Name', 'Author', 'NewPriceL', 'NewPriceH', 'NewPriceMN', 'NewPriceMD',
                             'GoodPriceL', 'GoodPriceH', 'GoodPriceMN', 'GoodPriceMD', 'FairPriceL', 'FairPriceH',
                             'FairPriceMN', 'FairPriceMD', 'Price']])
        return df_final

    def authorize_api(self):
        # authorize API
        scope = [
            'https://www.googleapis.com/auth/drive',
            'https://www.googleapis.com/auth/drive.file'
        ]

        file_name = 'client_key.json'
        creds = ServiceAccountCredentials.from_json_keyfile_name(file_name, scope)
        client = gspread.authorize(creds)

        # fetch Google sheet
        sheet = client.open('PythonBookExchange').BookPricingTab
        py_sheet = sheet.get_all_records()  # fetches entire sheet in JSON
        pp = pprint.PrettyPrinter()  # beautify the JSON response
        pp.pprint(py_sheet)

        return None

    def get_price_of_book(self, isbn):
        """
        Returns price of book given an ISBN.
        :param isbn: ISBN of book in DataFrame
        :return: price of book OR an input call if entered ISBN not found
        """
        idx = 0
        for elem in self.df['ISBN']:
            # if self.df.at[idx, 'Price'] == isbn:
            if elem == isbn:
                break
            idx += 1
        if idx >= self.df.shape()[0]:
            return self.get_price_of_book(input("ISBN not found, please try again:"))
        return self.df.at[idx, 'Price']

    def set_price_of_book(self, isbn, new_price):
        """
        Sets current price of book with corresponding ISBN to new manually assigned price.
        :param isbn: ISBN of book in DataFrame
        :param new_price: new assigned price value for given book
        :return: N/A OR an input call if entered ISBN not found
        """
        idx = 0
        for elem in self.df['ISBN']:
            if elem == isbn:
                break
            idx += 1
        if idx >= self.df.shape()[0]:
            return self.get_price_of_book(input("ISBN not found, please try again:"))
        # update DataFrame with new price
        self.df.at[idx, 'Price'] = new_price

        # update sheet with new price
        cell = sheet.cell(idx + 2, 17)  # indices subject to change
        sheet.update_cell(idx + 2, 17, isbn)  # indices subject to change
        cell = sheet.cell(idx + 2, 17)

    def set_price_with_bookfinder(self, isbn, quality):
        """
        Sets price of book based on quality of book and which item down the list on BookFinder to set price.
        :param isbn: ISBN of book in DataFrame
        :param quality: boolean representing if book is new
        :param price_list_idx: number item on new/old bookfinder list to use that price
        :return:
        """
        # to utilize data on quality of book + low/high/mean/median of pricings on BookFinder
        return

    def add_new_book(self, isbn, is_new, title):
        """
        Adds new book entry with relevant columns including price, ISBN, etc. Scrape from BookFinder.
        :param isbn: ISBN of book in DataFrame
        :param is_new: boolean representing if book is new
        :param title: string representing book title # designated owner of the book with ID in 'ID owner_name' format
        :return: N/A
        """
        self.df.loc[len(self.df.index)] = [isbn, title, '', '', '', '', '', '', '', '', '', '', '', '', '']
        # self.set_price_with_bookfinder(isbn, is_new, 1)
        return


def get_bool(prompt):
    while True:
        try:
            return {"true": True, "false": False}[input(prompt).lower()]
        except KeyError:
            print("Invalid input, please input True or False")


# 2021 master book list
sheet_id = "1EXGdFpMzucnnHVIwVRnEh8onE5Gr82aZi7wD-lWb2ww"  # "1dCgnjSlQAD8eJQxSKb85Eiul2ANbnlov3yg7w1TLmDw"
sheet_name = "BookPricing Tab"
url = f'https://docs.google.com/spreadsheets/d/{sheet_id}/gviz/tq?tqx=out:csv&sheet={sheet_name}'

BFR = BuyingFormReader(url)
BFR.read_form_data(url)
BFR.authorize_api()

# User input
res = input("Enter 'book price' to find a book price, 'manual set' to set book price manually, "
            "'set price' to set book price based on BookFinder, or 'add book' to add new book.")
# find book price using ISBN
if res == "book price":
    check_isbn = input("Enter an ISBN number to find its book price:")
    BFR.get_price_of_book(check_isbn)
# set book price using ISBN
elif res == "manual set":
    set_isbn = input("Enter an ISBN number for a book to set new price:")
    updated_price = input("Enter a price to set for the book:")
    BFR.set_price_of_book(set_isbn, updated_price)
elif res == "set price":
    set_isbn = input("Enter an ISBN number for a book to set new price:")
    quality = input("Enter 'New' if book is new, 'Good' if book is in good quality, and 'Fair' if book is in average "
                    "quality")
    # bookfinder_idx = input("Enter which book down the BookFinder list to set price to (default 1):")
    BFR.set_price_with_bookfinder(set_isbn, quality)
elif res == "add book":
    add_isbn = input("Enter the ISBN number for the book to add:")
    new_or_old = get_bool("'True' if book is new, 'False' otherwise:")
    book_name = input("Enter the name of the book:")
    # owner_info = input("Enter owner ID + name separated by a space:")
    BFR.add_new_book(add_isbn, new_or_old, book_name)
else:
    input("Input did not match any of the acceptable prompts, please try again."
          "Enter 'book price' to find a book price, 'manual set' to set book price manually, "
          "'set price' to set book price based on BookFinder, or 'add book' to add new book.")
