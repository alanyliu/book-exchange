# Alan Liu // Rice University
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

import pandas as pd
import math


class BuyingFormReader:

    def __init__(self, url):
        self.df = self.read_form_data(url)

    def read_form_data(self, url):
        """
        Reads data from given book buying form url and returns necessary data components in DataFrame.
        :param url: Google sheets book buying form url
        :return: df_final: DataFrame of data from Google sheets
        """
        # # title (2021) vs Book Title (2020)
        # # customDate (2021) not found in 2020
        # init_data = {'isbn': [], 'author': [], 'bookTitle': [], 'price': [], 'copyright': [], 'genre': [],
        #              'format': [], 'collection': [], 'customDate': []}

        df_init = pd.read_csv(url)
        df_final = df_init([['isbn', 'author', 'title', 'copyright', 'publisher', 'genre', 'collection',
                             'price', 'sold_at']])
        return df_final

    def get_price_of_book(self, isbn):
        """
        Returns price of book given an ISBN.
        :param isbn: ISBN of book in DataFrame
        :return: price of book OR an input call if entered ISBN not found
        """
        idx = 0
        for elem in self.df['isbn']:
            # if self.df.at[idx, 'price'] == isbn:
            if elem == isbn:
                break
            idx += 1
        if idx >= self.df.shape()[0]:
            return self.get_price_of_book(input("ISBN not found, please try again:"))
        return self.df.at[idx, 'price']

    def set_price_of_book(self, isbn, new_price):
        """
        Sets current price of book with corresponding ISBN to new manually assigned price.
        :param isbn: ISBN of book in DataFrame
        :param new_price: new assigned price value for given book
        :return: N/A OR an input call if entered ISBN not found
        """
        idx = 0
        for elem in self.df['isbn']:
            if elem == isbn:
                break
            idx += 1
        if idx >= self.df.shape()[0]:
            return self.get_price_of_book(input("ISBN not found, please try again:"))
        self.df.at[idx, 'price'] = new_price

    def set_price_with_bookfinder(self, isbn, is_new, price_list_idx):
        """
        Sets price of book based on quality of book and which item down the list on BookFinder to set price.
        :param isbn: ISBN of book in DataFrame
        :param is_new: boolean representing if book is new
        :param price_list_idx: number item on new/old bookfinder list to use that price
        :return:
        """
        # use math.round()?
        return

    def add_new_book(self, isbn, is_new, collection):
        """
        Adds new book entry with relevant columns including price, ISBN, etc. Scrape from BookFinder.
        :param isbn: ISBN of book in DataFrame
        :param is_new: boolean representing if book is new
        :param collection: designated owner of the book with ID in 'ID owner_name' format
        :return: N/A
        """
        self.df.loc[len(self.df.index)] = [isbn, '', '', '', '', '', collection, 0, 0]
        self.set_price_with_bookfinder(isbn, is_new, 1)
        return


def get_bool(prompt):
    while True:
        try:
            return {"true": True, "false": False}[input(prompt).lower()]
        except KeyError:
            print("Invalid input, please input True or False")


# 2021 master book list
sheet_id = "1dCgnjSlQAD8eJQxSKb85Eiul2ANbnlov3yg7w1TLmDw"
sheet_name = "Master book list 2021"
url = f'https://docs.google.com/spreadsheets/d/{sheet_id}/gviz/tq?tqx=out:csv&sheet={sheet_name}'

BFR = BuyingFormReader(url)
BFR.read_form_data(url)

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
    new_or_old = get_bool("'True' if book is new, 'False' otherwise:")
    bookfinder_idx = input("Enter which book down the BookFinder list to set price to (default 1):")
    BFR.set_price_with_bookfinder(set_isbn, new_or_old, bookfinder_idx)
elif res == "add book":
    add_isbn = input("Enter the ISBN number for the book to add:")
    new_or_old = get_bool("'True' if book is new, 'False' otherwise:")
    owner_info = input("Enter owner ID + name separated by a space:")
    BFR.add_new_book(add_isbn, new_or_old, owner_info)
