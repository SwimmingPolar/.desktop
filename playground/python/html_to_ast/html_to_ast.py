import sys
import json
import os
from bs4 import BeautifulSoup

def element_to_dict(soup):
    if soup.name in {"script", "link", "style", "meta", "noscript"}:
        return None

    element_dict = {"html_element": soup.name}

    data_attributes = {}  # Define the data_attributes variable

    if "id" in soup.attrs:
        element_dict["id"] = soup["id"]
    if "class" in soup.attrs:
        element_dict["class_name"] = " ".join(soup["class"])
        element_dict.update(data_attributes)

    texts = [
        text.strip()
        for text in soup.find_all(string=True, recursive=False)
        if text.strip()
    ]
    if texts:
        element_dict["text"] = " ".join(texts)

    children = []
    for child in soup.children:
        if child.name:
            child_dict = element_to_dict(child)
            if child_dict:
                children.append(child_dict)
    if children:
        element_dict["children"] = children

    return element_dict

def save_to_file(json_content, filename):
    output_dir = "./output"
    os.makedirs(output_dir, exist_ok=True)
    output_file_path = os.path.join(output_dir, os.path.splitext(filename)[0] + ".json")
    with open(output_file_path, "w", encoding="utf-8") as file:
        file.write(json_content)

def main():
    if len(sys.argv) < 2:
        print("Usage: python script.py filename.html [selector]")
        sys.exit(1)

    filename = sys.argv[1]
    selector = sys.argv[2] if len(sys.argv) > 2 else "body"

    try:
        with open(filename, "r", encoding="utf-8") as file:
            html_content = file.read()
    except FileNotFoundError:
        print("Error: File not found.")
        sys.exit(1)

    soup = BeautifulSoup(html_content, "html.parser")
    selected_element = soup.select_one(selector)
    if not selected_element:
        selected_element = soup.find("body")  # Default to body if selector not found

    html_json = element_to_dict(selected_element)
    json_output = json.dumps(html_json, indent=2, ensure_ascii=False)

    print(json_output)
    save_to_file(json_output, filename)

if __name__ == "__main__":
    main()
