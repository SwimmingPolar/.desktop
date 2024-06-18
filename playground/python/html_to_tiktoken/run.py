import tiktoken
from bs4 import BeautifulSoup

# extract tokens from command line input


def extract_text_from_html(html_content):
    soup = BeautifulSoup(html_content, 'html.parser')
    text = soup.get_text()
    return text

def count_tokens(text):
    enc = tiktoken.get_encoding('cl100k_base')  # Use 'gpt-4' or the specific model you want
    tokens = enc.encode(text)
    return len(tokens)

def main(html_file_path):
    with open(html_file_path, 'r', encoding='utf-8') as file:
        html_content = file.read()

    text = extract_text_from_html(html_content)
    token_count = count_tokens(text)

    print(f'Total tokens in the HTML content: {token_count}')

if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Count tokens in HTML content using tiktoken.')
    parser.add_argument('html_file_path', type=str, help='Path to the HTML file.')

    args = parser.parse_args()
    main(args.html_file_path)

