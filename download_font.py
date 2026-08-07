import urllib.request
import os

font_dir = "app/src/main/res/font"
os.makedirs(font_dir, exist_ok=True)

url = "https://raw.githubusercontent.com/google/fonts/main/ofl/hankengrotesk/HankenGrotesk%5Bwght%5D.ttf"
try:
    urllib.request.urlretrieve(url, os.path.join(font_dir, "hanken_grotesk.ttf"))
    print("Font downloaded successfully.")
except Exception as e:
    print(f"Error downloading font: {e}")
