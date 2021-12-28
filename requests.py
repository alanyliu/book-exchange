import requests
from bs4 import BeautifulSoup

URL = "https://docs.google.com/spreadsheets/d/1dCgnjSlQAD8eJQxSKb85Eiul2ANbnlov3yg7w1TLmDw"
page = requests.get(URL)

# print(page.text)
soup = BeautifulSoup(page.content, "html.parser")
