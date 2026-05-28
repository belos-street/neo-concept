"""
ECDICT database trimmer for mobile app bundling.

Reads the full ECDICT SQLite database (800+ MB) and produces a trimmed
version (~1-3 MB) containing only words from a frequency word list.

Download a word frequency list first:
  - NGSL (2800 words): http://www.newgeneralservicelist.org/
  - BNC/COCA top 20000: https://www.wordfrequency.info/
  - Or any .txt file with one word per line

Usage:
    python scripts/trim_ecdict.py <source_db> <freq_file> [output_db]

Example:
    python scripts/trim_ecdict.py ecdict.db ngsl.txt android/app/src/main/assets/ecdict.db
"""

import sqlite3
import os
import sys


def load_freq_file(path):
    words = set()
    with open(path, "r", encoding="utf-8") as f:
        for i, line in enumerate(f):
            if not line.strip():
                continue
            w = line.strip().split(",")[0].strip().strip('"').lower()
            if i == 0 and not w.isalpha():
                continue
            if w and w.isalpha():
                words.add(w)
    return words


def trim_database(src_path, dst_path, freq_words):
    if not os.path.exists(src_path):
        print(f"Error: source database not found: {src_path}")
        sys.exit(1)

    src = sqlite3.connect(src_path)
    dst = sqlite3.connect(dst_path)

    src_cur = src.cursor()
    dst_cur = dst.cursor()

    dst_cur.execute("""
        CREATE TABLE IF NOT EXISTS stardict (
            id INTEGER PRIMARY KEY,
            word TEXT,
            phonetic TEXT,
            definition TEXT,
            pos TEXT,
            exchange TEXT
        )
    """)
    dst_cur.execute(
        "CREATE INDEX IF NOT EXISTS idx_word ON stardict(word COLLATE NOCASE)"
    )

    print(f"Reading from {src_path} ...")
    print(f"Frequency words: {len(freq_words)}")

    src_cur.execute("SELECT word, phonetic, definition, pos, exchange FROM stardict")

    matched = 0
    for row in src_cur:
        word = (row[0] or "").strip().lower()
        if word not in freq_words:
            continue

        dst_cur.execute(
            "INSERT INTO stardict (word, phonetic, definition, pos, exchange) "
            "VALUES (?, ?, ?, ?, ?)",
            (word, row[1] or "", row[2] or "", row[3] or "", row[4] or ""),
        )
        matched += 1
        if matched % 500 == 0:
            print(f"  {matched} words matched ...")

    dst.commit()
    src.close()
    dst.close()

    size_mb = os.path.getsize(dst_path) / (1024 * 1024)
    found_pct = (matched / len(freq_words) * 100) if freq_words else 0
    print(f"\nDone!")
    print(f"  Matched: {matched}/{len(freq_words)} words ({found_pct:.1f}%)")
    print(f"  Output: {dst_path} ({size_mb:.1f} MB)")


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)

    src = sys.argv[1]
    freq_path = sys.argv[2]
    dst = sys.argv[3] if len(sys.argv) > 3 else "ecdict_trimmed.db"

    print(f"Loading frequency file: {freq_path}")
    freq = load_freq_file(freq_path)
    print(f"Loaded {len(freq)} unique words\n")

    trim_database(src, dst, freq)
