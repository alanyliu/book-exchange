class IsbnAdder:

    def __init__(self):

    def isbn_list(self, isbns):
        """
        Returns a list of distinct ISBNs to be added to the Google sheets sheet with pricing data.
        Args:
            isbns:
        Returns:
        """

# 2021 master book list
sheet_id = "1EXGdFpMzucnnHVIwVRnEh8onE5Gr82aZi7wD-lWb2ww"  # "1dCgnjSlQAD8eJQxSKb85Eiul2ANbnlov3yg7w1TLmDw"
sheet_name = "ForShinyOnly_NoEdit" # may have accessing issues
url = f'https://docs.google.com/spreadsheets/d/{sheet_id}/gviz/tq?tqx=out:csv&sheet={sheet_name}'