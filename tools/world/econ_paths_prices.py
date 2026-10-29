"""The prices layer of world-1.bin (batch WN0, slice E1, 2026-10-28): each traded good's real price path 2000-2025,
the Pink Sheet's nominal annual price of the series the good follows over the US consumer price index, as a ratio to
2025's (so 2025 is 1).

WHY: WN1 moves each good's price with the world's (spec-nations 7.1) and wants prices in the unit of the game's own
money, which is dollars of the year, so a nominal series is deflated by the paths' CPI of the United States and
rebased to the last year the game has data for. The Pink Sheet has no series for most of the 60 goods; prices.tsv
names for each the one it follows and whether that is the good itself (mark 3) or a proxy (mark 1).
"""
import os
import struct
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import xlsx

# The sheet of nominal annual prices, and the sheet that carries the MUV Index (the Nominal sheet does not).
NOMINAL_SHEET = 'Annual Prices (Nominal)'
MUV_SHEET = 'Annual Prices (Real)'

# The title of the sheets' first series, the marker of the header row.
HEADER_MARK = 'Crude oil, average'

# The Pink Sheet series the manufactured goods follow: the World Bank's index of manufactures' export unit values.
MUV_TITLE = 'MUV Index'

# The year every price path is a ratio to, so that its value is 1 there.
RATIO_YEAR = 2025

# The years of the layer: 2000 to 2025.
YEARS = list(range(common.FIRST_YEAR, common.LAST_DATA_YEAR + 1))


def _table(path, sheet):
    """One Pink Sheet annual sheet as {series title: {year: value}}, the title stripped of trailing spaces; a cell the
    sheet writes as n/a is left out."""
    rows = list(xlsx.rows(path, sheet))
    h = next(i for i, r in enumerate(rows) if len(r) > 1 and r[1] == HEADER_MARK)
    out = dict((t.strip(), {}) for t in rows[h][1:] if t.strip())
    for r in rows[h + 2:]:
        if not r or not r[0].isdigit():
            continue
        for j, t in enumerate(rows[h]):
            if j == 0 or not t.strip() or j >= len(r):
                continue
            try:
                out[t.strip()][int(r[0])] = float(r[j])
            except ValueError:
                pass
    return out


def read_pinksheet(path):
    """{series title: {year: nominal price}} for the nominal sheet's series and the MUV Index."""
    out = _table(path, NOMINAL_SHEET)
    out[MUV_TITLE] = _table(path, MUV_SHEET)[MUV_TITLE]
    return out


def read_prices_tsv(path, goods_path):
    """prices.tsv -> [(key, series, mark)] in goods.tsv's order; stops if the two do not name the same goods."""
    with open(goods_path, encoding='utf-8') as f:
        goods = [ln.split('\t')[1] for ln in f.read().split('\n')[1:] if ln.strip()]
    with open(path, encoding='utf-8') as f:
        rows = [ln.split('\t') for ln in f.read().split('\n')[1:] if ln.strip()]
    keys = [r[0] for r in rows]
    assert keys == goods, ('prices.tsv and goods.tsv differ', [k for k in goods if k not in keys], [k for k in keys if k not in goods])
    return [(r[0], r[1], int(r[2])) for r in rows]


def build_prices(mapping, pink, cpi_usa):
    """mapping from read_prices_tsv, pink from read_pinksheet, cpi_usa the 26 yearly values of the paths' US CPI.
    Returns (series names, values [good][26], marks [good][26])."""
    names, values, marks = [], [], []
    for key, series, mark in mapping:
        nominal = pink[series]
        real = []
        for i, y in enumerate(YEARS):
            assert y in nominal and nominal[y] > 0, (key, series, y)
            real.append(nominal[y] / cpi_usa[i])
        base = real[YEARS.index(RATIO_YEAR)]
        v = [x / base for x in real]
        assert all(x == x and x > 0 and x < float('inf') for x in v), key
        names.append(series)
        values.append(v)
        marks.append([mark] * len(YEARS))
    return names, values, marks


def write_prices_bin(path, names, values, marks):
    """The layer's payload (FORMAT.txt, prices): int G, first year, years; per good the UTF series name and the doubles;
    then per good the marks."""
    out = bytearray(struct.pack('>iii', len(names), YEARS[0], len(YEARS)))
    for name, v in zip(names, values):
        b = name.encode('utf-8')
        out += struct.pack('>H', len(b)) + b + struct.pack('>%dd' % len(v), *v)
    for m in marks:
        out += bytes(m)
    with open(path, 'wb') as f:
        f.write(bytes(out))
    return len(out)
