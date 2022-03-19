import pandas as pd
import math
import gspread
from oauth2client.service_account import ServiceAccountCredentials
import pprint


class IsbnAdder:

    def __init__(self, url):
        self.df = self.read_form_data(url)

    def read_form_data(self, url):
        df = pd.read_csv(url)
        return df

    def isbn_list(self):
        """
        Returns a list of distinct ISBNs to be added to the Google sheets sheet with pricing data.
        Args:
            N/A
        Returns: list(isbn_set): list representation of all unique ISBNs in data frame of master book list
        """

        isbn_set = {}
        for isbn in self.df['ISBN']:
            isbn_set.add(isbn)
        return list(isbn_set)

    def update_sheet(self):
        """
        Updates Google sheet BookPricingTab with distinct ISBNs from master book list. Utilizes Google Sheets and Drive
        API to fetch Google sheet in JSON, then beautifies JSON format and updates sheet accordingly.
        Returns:
            None
        """

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

        # update Google sheet
        row_idx = 3
        for isbn in isbn_list():
            cell = sheet.cell(row_idx, 1)
            sheet.update_cell(row_idx, 1, isbn)
            cell = sheet.cell(row_idx, 1)
            row_idx += 1

        return None


# 2021 master book list
sheet_id = "1EXGdFpMzucnnHVIwVRnEh8onE5Gr82aZi7wD-lWb2ww"  # "1dCgnjSlQAD8eJQxSKb85Eiul2ANbnlov3yg7w1TLmDw"
sheet_name = "ForShinyOnly_NoEdit"  # may have accessing issues
url = f'https://docs.google.com/spreadsheets/d/{sheet_id}/gviz/tq?tqx=out:csv&sheet={sheet_name}'

IA = IsbnAdder(url)
IA.read_form_data(url)
