import os
import re
import json
from pathlib import Path

import requests
from playwright.sync_api import sync_playwright

START_URL = "https://grovecitycollege.sharepoint.com/sites/RegOffice/SitePages/2023-2024.aspx?csf=1&web=1&share=Eeipitst4INPlsY2LSZOwvcB1lw8-BaIcsxZtZ717mIIeQ&e=53ilDz"
STATE_FILE = "sharepoint_state.json"
DOWNLOAD_DIR = Path("degree_pdfs")

DOWNLOAD_DIR.mkdir(exist_ok=True)


def clean_filename(text: str) -> str:
    text = text.strip()
    text = re.sub(r"\s+", " ", text)
    text = re.sub(r'[<>:"/\\|?*]', "", text)
    return text[:180]


def make_unique(path: Path) -> Path:
    if not path.exists():
        return path

    stem = path.stem
    suffix = path.suffix
    i = 1
    while True:
        candidate = path.with_name(f"{stem}_{i}{suffix}")
        if not candidate.exists():
            return candidate
        i += 1


def build_session():
    with open(STATE_FILE, "r", encoding="utf-8") as f:
        state = json.load(f)

    session = requests.Session()
    for cookie in state.get("cookies", []):
        session.cookies.set(
            cookie["name"],
            cookie["value"],
            domain=cookie.get("domain"),
            path=cookie.get("path"),
        )
    return session


def scrape_and_download():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        context = browser.new_context()
        page = context.new_page()

        print("Opening page...")
        page.goto(START_URL, wait_until="domcontentloaded")
        page.wait_for_timeout(3000)

        print("\nLog in if needed.")
        print("Then manually expand ALL departments in the browser.")
        input("When everything is expanded and visible, press Enter here in the terminal... ")

        # Save login state after you are fully logged in
        context.storage_state(path=STATE_FILE)

        # Collect visible PDF links
        links = page.eval_on_selector_all(
            "a[href$='.pdf']",
            """els => els.map(a => ({
                text: a.innerText.trim(),
                href: a.href
            }))"""
        )

        browser.close()

    seen = set()
    pairs = []
    for item in links:
        text = clean_filename(item["text"])
        href = item["href"]
        if text and href and href not in seen:
            seen.add(href)
            pairs.append((text, href))

    print(f"\nFound {len(pairs)} PDFs")
    for name, url in pairs:
        print(f"{name} -> {url}")

    session = build_session()

    for i, (name, url) in enumerate(pairs, start=1):
        try:
            print(f"[{i}/{len(pairs)}] Downloading {name}")
            r = session.get(url, timeout=60)
            r.raise_for_status()

            out_path = make_unique(DOWNLOAD_DIR / f"{name}.pdf")
            with open(out_path, "wb") as f:
                f.write(r.content)

            print(f"  Saved {out_path}")
        except Exception as e:
            print(f"  Failed {name}: {e}")


if __name__ == "__main__":
    scrape_and_download()