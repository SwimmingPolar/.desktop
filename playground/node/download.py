import requests
from bs4 import BeautifulSoup
import os

# Base URL components
base_url = 'https://github.com/ultrafunkamsterdam/nodriver/tree/main/nodriver'
raw_base_url = 'https://raw.githubusercontent.com/ultrafunkamsterdam/nodriver/main/nodriver/'

# Function to get the file URLs and directory structure
def get_file_urls(url, current_dir=''):
    response = requests.get(url)
    soup = BeautifulSoup(response.content, 'html.parser')
    files = soup.find_all('a', {'class': 'js-navigation-open Link--primary'})

    file_urls = []
    for file in files:
        file_name = file['title']
        if file_name == '..':
            continue

        file_url = url + '/' + file_name
        raw_file_url = raw_base_url + current_dir + file_name
        
        if file_url.endswith('/'):  # Check if it's a directory
            file_urls.extend(get_file_urls(file_url, current_dir + file_name + '/'))
        else:
            file_urls.append((raw_file_url, current_dir + file_name))
    
    return file_urls

# Download files maintaining the directory structure
def download_files(file_urls):
    for raw_file_url, file_path in file_urls:
        full_path = os.path.join('nodriver', file_path)
        os.makedirs(os.path.dirname(full_path), exist_ok=True)
        
        response = requests.get(raw_file_url)
        with open(full_path, 'wb') as file:
            file.write(response.content)
        
        print(f'Downloaded: {file_path}')

# Get the file URLs and download them
file_urls = get_file_urls(base_url)
download_files(file_urls)

