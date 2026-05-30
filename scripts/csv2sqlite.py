import csv
import sqlite3
import os

csv_path = os.path.join(os.path.dirname(__file__), '..', 'ecdict.csv')
db_path = os.path.join(os.path.dirname(__file__), '..', 'android', 'app', 'src', 'main', 'assets', 'ecdict.db')

if os.path.exists(db_path):
    os.remove(db_path)

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

cursor.execute('''
CREATE TABLE stardict (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word TEXT NOT NULL,
    phonetic TEXT,
    definition TEXT,
    translation TEXT,
    pos TEXT,
    collins INTEGER DEFAULT 0,
    oxford INTEGER DEFAULT 0,
    tag TEXT,
    bnc INTEGER,
    frq INTEGER,
    exchange TEXT,
    detail TEXT,
    audio TEXT
)
''')

cursor.execute('CREATE INDEX idx_word ON stardict(word COLLATE NOCASE)')

with open(csv_path, 'r', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    batch = []
    count = 0
    for row in reader:
        batch.append((
            row.get('word', ''),
            row.get('phonetic', ''),
            row.get('definition', ''),
            row.get('translation', ''),
            row.get('pos', ''),
            int(row.get('collins', 0) or 0),
            int(row.get('oxford', 0) or 0),
            row.get('tag', ''),
            int(row.get('bnc', 0) or 0),
            int(row.get('frq', 0) or 0),
            row.get('exchange', ''),
            row.get('detail', ''),
            row.get('audio', '')
        ))
        if len(batch) >= 10000:
            cursor.executemany(
                'INSERT INTO stardict (word, phonetic, definition, translation, pos, collins, oxford, tag, bnc, frq, exchange, detail, audio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                batch
            )
            count += len(batch)
            print(f'Imported {count} rows...')
            batch = []
    
    if batch:
        cursor.executemany(
            'INSERT INTO stardict (word, phonetic, definition, translation, pos, collins, oxford, tag, bnc, frq, exchange, detail, audio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
            batch
        )
        count += len(batch)

conn.commit()

cursor.execute('SELECT COUNT(*) FROM stardict')
total = cursor.fetchone()[0]
print(f'Done! Total rows: {total}')

cursor.execute("SELECT word, phonetic, translation FROM stardict WHERE word = 'hello'")
row = cursor.fetchone()
if row:
    print(f'Sample: {row[0]} | {row[1]} | {row[2][:50]}...')

conn.close()

db_size = os.path.getsize(db_path) / (1024 * 1024)
print(f'Database size: {db_size:.2f} MB')
