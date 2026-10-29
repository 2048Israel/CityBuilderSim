"""The downloads the nations' moneys and paths are made from (batch WN0, slice E1, 2026-10-28): the World Bank's WDI,
the UN's WPP 2024, the BIS's dollar rates, the Penn World Table 11.0 and the Maddison Project 2023, each read into
plain dicts by the standard library alone.

WHY: econ_paths.py is several hundred lines of fills and every fill reads one of these; keeping the readers apart
means the fills are about the numbers and not about file formats, and the list of sources (SOURCE_ROWS, which goes to
the file's meta) sits beside the code that reads them. Run through python -I: the downloads' folder is not on
sys.path.
"""
import csv
import gzip
import io
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import xlsx

# Where the downloads are (read-only here), and the folder the stages' intermediate files go in.
SOURCES = 'C:/Users/Jerus/Documents/world-sources'
WORK = SOURCES + '/work'

# The day the downloads were made.
DOWNLOADED = '2026-10-28'

# The meta file's source rows: id, name and version, url, licence, downloaded.
SOURCE_ROWS = [
    ('wdi', 'World Bank, World Development Indicators (NY.GDP.MKTP.KD, NY.GDP.MKTP.CD, NY.GDP.MKTP.PP.KD, FP.CPI.TOTL, '
            'PA.NUS.FCRF, SP.POP.TOTL), last updated 2026-10-08',
     'https://api.worldbank.org/v2/country/all/indicator/', 'CC BY 4.0', DOWNLOADED),
    ('wpp', 'United Nations, World Population Prospects 2024, total population by sex, medium variant',
     'https://population.un.org/wpp/', 'CC BY 3.0 IGO', DOWNLOADED),
    ('bis', 'Bank for International Settlements, US dollar exchange rates (WS_XRU), monthly averages',
     'https://data.bis.org/topics/XRU', 'BIS terms and conditions, https://www.bis.org/terms_conditions.htm', DOWNLOADED),
    ('pwt', 'Penn World Table 11.0 (Feenstra, Inklaar and Timmer)',
     'https://www.rug.nl/ggdc/productivity/pwt/', 'CC BY 4.0', DOWNLOADED),
    ('maddison', 'Maddison Project Database 2023',
     'https://www.rug.nl/ggdc/historicaldevelopment/maddison/', 'CC BY 4.0', DOWNLOADED),
    ('pinksheet', 'World Bank Commodity Markets, the Pink Sheet, annual prices (CMO-Historical-Data-Annual.xlsx, updated '
                  '2026-09-02): the nominal sheet and the MUV Index',
     'https://www.worldbank.org/en/research/commodity-markets', 'CC BY 4.0', DOWNLOADED),
    ('moneys', 'tools/world/moneys.tsv, authored by the game (ISO 4217 in 2025-12, euro changeover dates, the IMF '
               'exchange arrangements as of 2025)', '-', 'the game', '-'),
]


def month_index(period):
    """'2000-01' is 0 and '2025-12' is 311; None for a period outside the window or not a month."""
    m = re.fullmatch(r'(\d{4})-(\d{2})', period)
    if not m:
        return None
    i = (int(m.group(1)) - common.FIRST_YEAR) * 12 + int(m.group(2)) - 1
    return i if 0 <= i < common.MONTHS else None


def wdi(indicator, sources=SOURCES):
    """One WDI indicator as {iso3: {year: value}}, the missing values left out."""
    with open('%s/wdi/%s.json' % (sources, indicator), encoding='utf-8') as f:
        rows = json.load(f)[1]
    out = {}
    for r in rows:
        if r['value'] is not None:
            out.setdefault(r['countryiso3code'], {})[int(r['date'])] = float(r['value'])
    return out


def wdi_iso2(sources=SOURCES):
    """{iso3: iso2} for every WDI country, aggregates included."""
    with open('%s/wdi/countries.json' % sources, encoding='utf-8') as f:
        rows = json.load(f)[1]
    return {r['id']: r['iso2Code'] for r in rows}


def wpp_people(sources=SOURCES):
    """WPP 2024's medium variant total population in persons, {iso3: {year: persons}}, 1950-2100."""
    out = {}
    with gzip.open('%s/wpp/WPP2024_TotalPopulationBySex.csv.gz' % sources, 'rt', encoding='utf-8-sig', newline='') as f:
        for row in csv.DictReader(f):
            if row['Variant'] == 'Medium' and row['ISO3_code']:
                out.setdefault(row['ISO3_code'], {})[int(row['Time'])] = float(row['PopTotal']) * 1000.0
    return out


def bis_monthly(sources=SOURCES):
    """BIS's monthly averages (FREQ M, COLLECTION A) as {ref_area: (currency, {month number: units a US dollar})}, the
    months 2000-01 to 2025-12 only. REF_AREA is ISO 3166 alpha-2, XM the euro area, WA the WAEMU, XW the world (SDR)."""
    with zipfile.ZipFile('%s/bis/WS_XRU_csv_col.zip' % sources) as z:
        name = z.namelist()[0]
        f = io.TextIOWrapper(z.open(name), encoding='utf-8-sig', errors='replace', newline='')
        r = csv.reader(f)
        head = next(r)
        col = {h: i for i, h in enumerate(head)}
        months = [(i, month_index(h)) for i, h in enumerate(head)]
        months = [(i, m) for i, m in months if m is not None]
        out = {}
        for x in r:
            if x[col['FREQ']] != 'M' or x[col['COLLECTION']] != 'A':
                continue
            vals = {}
            for i, m in months:
                if x[i] != '':
                    v = float(x[i])
                    if v == v and v > 0:        # BIS writes NaN for a month it has no figure for
                        vals[m] = v
            out[x[col['REF_AREA']]] = (x[col['CURRENCY']], vals)
    return out


def pwt(sources=SOURCES):
    """The Penn World Table 11.0 Data sheet as {iso3: {year: {column: float}}}, the columns the fills use."""
    want = ('rgdpna', 'cgdpo', 'pl_gdpo', 'pl_c', 'xr', 'pop')
    it = xlsx.rows('%s/pwt/pwt110.xlsx' % sources, 'Data')
    head = next(it)
    ci = head.index('countrycode')
    yi = head.index('year')
    idx = {w: head.index(w) for w in want}
    out = {}
    for r in it:
        if len(r) <= yi or not r[ci]:
            continue
        d = {}
        for w, i in idx.items():
            if i < len(r) and r[i] != '':
                d[w] = float(r[i])
        out.setdefault(r[ci], {})[int(r[yi])] = d
    return out


def maddison(sources=SOURCES):
    """The Maddison Project's GDP a year, {iso3: {year: gdppc x pop}} in 2011 international dollars, from 1990."""
    it = xlsx.rows('%s/maddison/mpd2023_web.xlsx' % sources, 'Full data')
    head = next(it)
    ci, yi, gi, pi = (head.index(c) for c in ('countrycode', 'year', 'gdppc', 'pop'))
    out = {}
    for r in it:
        if len(r) <= max(gi, pi, yi) or r[gi] == '' or r[pi] == '':
            continue
        y = int(r[yi])
        if y >= 1990:
            out.setdefault(r[ci], {})[y] = float(r[gi]) * float(r[pi]) * 1000.0
    return out
