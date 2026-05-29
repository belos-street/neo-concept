"""
ECDICT database trimmer for mobile app bundling.

Reads the full ECDICT SQLite database (800+ MB) and produces a trimmed
version containing only high-frequency words with Chinese translations.

Uses ECDICT's built-in bnc/frq frequency fields to select top N words.
Optionally supplements with an external word list file.

Usage:
    python scripts/trim_ecdict.py <source_db> [output_db] [--top N] [--extra words.txt]

Example:
    python scripts/trim_ecdict.py assets/stardict.db android/app/src/main/assets/ecdict.db --top 20000
"""

import sqlite3
import os
import sys
import argparse


def load_extra_words(path):
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


def trim_database(src_path, dst_path, top_n, extra_words):
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
            translation TEXT,
            definition TEXT,
            pos TEXT,
            exchange TEXT
        )
    """)
    dst_cur.execute(
        "CREATE INDEX IF NOT EXISTS idx_word ON stardict(word COLLATE NOCASE)"
    )

    print(f"Reading from {src_path} ...")

    src_cur.execute("SELECT COUNT(*) FROM stardict WHERE bnc > 0 AND frq > 0")
    freq_count = src_cur.fetchone()[0]
    print(f"Words with frequency data: {freq_count}")

    if extra_words:
        print(f"Extra words from file: {len(extra_words)}")

    src_cur.execute(
        """
        SELECT word, phonetic, translation, definition, pos, exchange
        FROM stardict
        WHERE bnc > 0 AND frq > 0
        ORDER BY (bnc + frq) ASC
        LIMIT ?
    """,
        (top_n,),
    )

    freq_rows = src_cur.fetchall()
    print(f"Selected top {len(freq_rows)} words by frequency")

    seen = set()
    matched = 0

    for row in freq_rows:
        word = (row[0] or "").strip().lower()
        if not word or word in seen:
            continue
        seen.add(word)

        dst_cur.execute(
            "INSERT INTO stardict (word, phonetic, translation, definition, pos, exchange) "
            "VALUES (?, ?, ?, ?, ?, ?)",
            (
                word,
                row[1] or "",
                row[2] or "",
                row[3] or "",
                row[4] or "",
                row[5] or "",
            ),
        )
        matched += 1
        if matched % 1000 == 0:
            print(f"  {matched} words inserted ...")

    if extra_words:
        missing = extra_words - seen
        if missing:
            print(f"\nSupplementing {len(missing)} extra words ...")
            placeholders = ",".join(["?"] * len(missing))
            src_cur.execute(
                f"""
                SELECT word, phonetic, translation, definition, pos, exchange
                FROM stardict
                WHERE word IN ({placeholders})
            """,
                list(missing),
            )

            for row in src_cur:
                word = (row[0] or "").strip().lower()
                if not word or word in seen:
                    continue
                seen.add(word)

                dst_cur.execute(
                    "INSERT INTO stardict (word, phonetic, translation, definition, pos, exchange) "
                    "VALUES (?, ?, ?, ?, ?, ?)",
                    (
                        word,
                        row[1] or "",
                        row[2] or "",
                        row[3] or "",
                        row[4] or "",
                        row[5] or "",
                    ),
                )
                matched += 1

    dst.commit()
    src.close()
    dst.close()

    size_mb = os.path.getsize(dst_path) / (1024 * 1024)
    print(f"\nDone!")
    print(f"  Total words: {matched}")
    print(f"  Output: {dst_path} ({size_mb:.1f} MB)")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Trim ECDICT database for mobile bundling"
    )
    parser.add_argument("source", help="Source ECDICT SQLite database path")
    parser.add_argument(
        "output",
        nargs="?",
        default="ecdict_trimmed.db",
        help="Output database path (default: ecdict_trimmed.db)",
    )
    parser.add_argument(
        "--top",
        type=int,
        default=20000,
        help="Number of top frequency words to include (default: 20000)",
    )
    parser.add_argument(
        "--extra",
        type=str,
        default=None,
        help="Extra word list file to supplement (one word per line or CSV)",
    )

    args = parser.parse_args()

    extra_words = set()
    if args.extra:
        print(f"Loading extra word list: {args.extra}")
        extra_words = load_extra_words(args.extra)
        print(f"Loaded {len(extra_words)} extra words\n")

    trim_database(args.source, args.output, args.top, extra_words)
