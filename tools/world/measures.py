"""Builds world-2025.json, the Rankings' measures file (batch LB1, slice B, 2026-10-30): the real world's latest
figure for each of the 70 measures of tools/world/measures.tsv, for the 196 nations and the city regions of
tools/world/regions.tsv, each figure with its year, mark and source. FORMAT-2025.txt is the file's contract.

WHY: the Rankings set a city beside the world, and the game never fetches anything, so one script reads the downloads
(the World Bank's WDI, the OECD, the BIS, the UN's WUP 2025, the EIA) and the nations' world file (world-1.bin) and
writes the one file the game reads. Every figure is its place's latest in 2015-2025, never carried from before, and a
figure the sources do not publish is left out, never a 0 or a guess; an estimate carries its mark 1 and the rule that
made it. A city region follows ONE boundary for all its figures (the OECD's functional urban area, else the UN's urban
centre), so its share of its nation's output and its people describe the same place. The same inputs make the same
bytes. Run it with python -I: the downloads' folder is not on sys.path.

Usage: python -I measures.py [--sources DIR] [--world PATH] [--out PATH] [--made yyyy-mm-dd]
Writes the file and <sources>/work/measures/coverage.tsv (every measure's coverage), and prints a few lines.
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import csv
import gzip
import io
import json
import math
import re
import time
import zipfile

import common
import econ_paths_sources
import worldread
import xlsx

# This script's folder, where measures.tsv and regions.tsv sit.
HERE = os.path.dirname(os.path.abspath(__file__))

# The nations' world file, whose 2025 path figures fill a nation the WDI has no figure for.
WORLD_BIN = 'C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin'
# The file this script writes: the resources' copy, which the game reads.
OUT = os.path.normpath(os.path.join(HERE, '..', '..', 'src', 'main', 'resources', 'world', 'world-2025.json'))

# The board's year, and the window a figure's year must fall in: an older figure is left out, never carried.
BOARD_YEAR = 2025
WINDOW_FIRST = 2015
WINDOW_LAST = 2025
# The file stays under this many bytes (FORMAT-2025.txt).
SIZE_LIMIT = 1000000
# Every figure's value is rounded to this many significant digits.
SIG_DIGITS = 6
# The years a region's population growth is taken over: its latest year against this many before.
POP_GROWTH_YEARS = 5
# The day the downloads were made; every source row below carries it.
DOWNLOADED = '2026-10-30'
# The 20 members of the euro area in 2025 (Bulgaria joined on 2026-01-01); BIS has the euro area's rate under XM only.
EURO_AREA = 'AUT BEL CYP DEU ESP EST FIN FRA GRC HRV IRL ITA LTU LUX LVA MLT NLD PRT SVK SVN'.split()
# The BLS's series of the US consumer price index (all items, US city average, 1982-84 = 100) whose annual averages
# (period M13) measures_fetch.py downloads into <sources>/bls/: every year of the window, so a figure of any year
# becomes 2015 dollars (LB2's real US$), and 2015 over 2025 is the board's own ratio.
CPI_U_SERIES = 'CUUR0000SA0'


def read_cpi_u(sources):
    """The CPI-U's annual averages for the window's years, {'2015': 237.017, ...}, from the BLS files."""
    out = {}
    for name in sorted(os.listdir(sources + '/bls')):
        if name.startswith(CPI_U_SERIES) and name.endswith('.json'):
            with open('%s/bls/%s' % (sources, name), encoding='utf-8') as f:
                for row in json.load(f)['Results']['series'][0]['data']:
                    if row['period'] == 'M13':
                        out[row['year']] = float(row['value'])
    years = [str(y) for y in range(WINDOW_FIRST, WINDOW_LAST + 1)]
    assert all(y in out for y in years), 'the BLS files lack a year of the window: ' + str(sorted(out))
    return {y: out[y] for y in years}
# Bulgaria's lev is a currency board at this many to the euro; the WDI gives Bulgaria's PPP factor in euros (the
# GDP at PPP over the GDP at market rates says so in every year) and the OECD its minimum wage in euros, while the WDI's
# exchange rate is in levs.
BGN_PER_EUR = 1.95583
# Whether the OECD's FUA GDP (USD_PPP) is read as constant prices, so a region's real growth is its latest year over
# the year before: the dataflow's metadata does not say, but the US areas' implied national total falls 2.2% in 2020
# and rises 5.8% in 2021, tracking the WDI's real growth (-2.1, 6.2) and not the nominal (about -0.9, +10.7).
FUA_GDP_CONSTANT_PRICES = True

# The marks and their symbols (FORMAT-2025.txt): world-1.bin's marks, the same numbers.
MARKS = {'3': {'symbol': '\u25cf', 'name': 'published'},
         '2': {'symbol': '\u25d0', 'name': 'a published projection'},
         '1': {'symbol': '\u25cb', 'name': "the game's estimate"}}

# The GDP in each money, and the matching per-person key: the first is the measure's own, the others its variants.
MONEYS = [('gdp', 'gdp_pp', 'NY.GDP.MKTP.CD', 'NY.GDP.PCAP.CD', 'current US$'),
          ('gdp.k', 'gdp_pp.k', 'NY.GDP.MKTP.KD', 'NY.GDP.PCAP.KD', 'constant 2015 US$'),
          ('gdp.ppp', 'gdp_pp.ppp', 'NY.GDP.MKTP.PP.CD', 'NY.GDP.PCAP.PP.CD', 'current international $ (PPP)')]
# Where world-1.bin's path series that fill GDP and people sit, by series name.
WORLD_SERIES = {'people': 'people', 'gdp': 'gdp_usd', 'gdp.k': 'gdp_2015usd'}
# Measures whose nations' figures come from the nation series (WDI, else world-1, else the PPP rule) and not from
# measures.tsv's spec read alone.
MONEY_KEYS = ['people'] + [k for m in MONEYS for k in (m[0], m[1])]

# A wage in US$ a year outside these bounds is a unit slip, not a wage (the build stops).
WAGE_USD = (500, 300000)

# The only measures a city region carries: those of scope NC the sources publish for its boundary, and the GDP variants.
REGION_KEYS = ['gdp', 'gdp.k', 'gdp.ppp', 'gdp_pp', 'gdp_pp.k', 'gdp_pp.ppp', 'growth_real', 'people', 'pop_growth',
               'area', 'density']

# The names the OECD's Affordable Housing Database workbooks use for a nation that world-1's name differs from.
AHD_NAMES = {'Korea': 'KOR', 'Slovak Republic': 'SVK', 'T\u00fcrkiye': 'TUR', 'UK (England)': 'GBR'}

# What a measure's note says on top of measures.tsv's: how this file differs from the plain reading of the source.
NOTE_ADDITIONS = {
    'policy_rate': 'The 20 euro-area members, whose own series ended before the euro, take the euro area\'s rate '
                   '(BIS REF_AREA=XM), mark 3',
    'homeless': 'The counts differ by nation (a point in time or a flow, children in or out); Belgium and the UK, '
                'which the workbook splits by region, are left out',
    'homes_per_1000': 'The UK is England',
    'growth_real': 'A city region\'s is its functional urban area\'s GDP (OECD, USD_PPP) in its latest year over the '
                   'year before; the OECD\'s metadata does not say the series is at constant prices, but the US areas\' '
                   'implied national total falls 2.2% in 2020 and rises 5.8% in 2021, tracking the WDI\'s real growth '
                   '(-2.1, 6.2) and not the nominal (about -0.9, +10.7)',
    'min_wage': 'Bulgaria\'s, which the OECD gives in euros, is converted to levs at the peg first',
    'price_level': 'Bulgaria\'s PPP factor, which the WDI gives in euros, is converted to levs at the peg first',
    'fx_vs_parity': 'Bulgaria\'s PPP factor, which the WDI gives in euros, is converted to levs at the peg first',
}

# The sources a figure can name: id, name and version, url, licence (MEASURES-SOURCES.txt in the sources folder).
SOURCE_ROWS = {
    'wdi': ('World Bank, World Development Indicators, last updated 2026-10-08 (49 series; every series\' own metadata '
            'gives License_Type CC BY-4.0, checked 2026-10-30)',
            'https://api.worldbank.org/v2/country/all/indicator/', 'CC BY 4.0', DOWNLOADED),
    'oecd': ('OECD data, credit to the OECD per dataset: Earnings (average and minimum wages), Financial market '
             '(long-term interest rates), Social Expenditure (public old-age pensions), National Accounts at a '
             'Glance (households), Regions, cities and local areas (functional urban areas: economy, population, '
             'density), Affordable Housing Database (HM1.1 housing stock, HC3.1 homelessness)',
             'https://sdmx.oecd.org/public/rest/data/ and https://webfs.oecd.org/Els-com/Affordable_Housing_Database/',
             'OECD terms and conditions, Data: permitted for any purpose, even commercial, with credit '
             '(https://www.oecd.org/en/about/terms-conditions.html)', DOWNLOADED),
    'bis': ('Bank for International Settlements, central bank policy rates (WS_CBPOL), monthly, bulk file '
            'WS_CBPOL_csv_col.zip', 'https://data.bis.org/topics/CBPOL',
            'BIS terms of permitted use of statistics: unrestricted with the BIS cited, not misleading, and no '
            'additional charge to users of a commercial product (https://data.bis.org/help/legal)', DOWNLOADED),
    'wup': ('United Nations, DESA, Population Division, World Urbanization Prospects 2025 (the 22nd revision), '
            'cities of 50,000 people or more, Degree of Urbanization',
            'https://population.un.org/wup/', 'CC BY 3.0 IGO', DOWNLOADED),
    'eia': ('US Energy Information Administration, International Energy Statistics (INTL bulk file, series updated '
            '2026-10-16)', 'https://www.eia.gov/opendata/bulk/INTL.zip',
            'public domain (a US government work); source: U.S. Energy Information Administration', DOWNLOADED),
    'world-1': ("The game's nations' world file, world-1.bin (batch WN0): its 2025 path figures, made from the "
                "World Bank's WDI (CC BY 4.0), the UN's World Population Prospects 2024 (CC BY 3.0 IGO), the Penn "
                "World Table 11.0 (CC BY 4.0) and the Maddison Project Database 2023 (CC BY 4.0)",
                '-', 'CC BY 4.0 and CC BY 3.0 IGO, its sources\' own', '2026-10-28'),
    'bls': ('US Bureau of Labor Statistics, CPI-U, US city average, all items (CUUR0000SA0), annual averages',
            'https://www.bls.gov/cpi/', 'public domain (a US government work)', DOWNLOADED),
}

# How the game made each kind of estimate, for the drawer (FORMAT-2025.txt, THE RULES).
RULES = {
    'ppp-market': "A nation with no published figure at purchasing power parity takes its figure at market exchange "
                  "rates, the same value and year, as the best estimate there is.",
    'world-1': "A nation the World Bank has no figure for in 2015-2025 takes the game's own estimate for 2025, made "
               "for the nations' world file from the Penn World Table, the Maddison Project and the UN's population "
               "estimates.",
    'share-people': "A city region the OECD publishes no share of its nation's output for takes its share of the "
                    "nation's people as its share of the output.",
    'region-output': "A city region's GDP is its share of its nation's output times the nation's figure; where the "
                     "share and the nation's figure are not of one year, or the share is itself an estimate, the "
                     "result is the game's estimate, not a published figure.",
}

# The two city boundaries a region's def names (FORMAT-2025.txt).
DEFINITIONS = {
    'oecd': "The OECD's functional urban area: a city and its commuting zone.",
    'wup': "The UN's urban centre: contiguous 1 km\u00b2 cells of at least 1,500 people a km\u00b2, 50,000 people in "
           "all (World Urbanization Prospects 2025).",
}

# measures.tsv's source ids that are not read: UNODC's current terms allow personal non-commercial use only.
UNREAD = ('unodc',)


# ===================================================================== numbers and figures
def rnd(v):
    """A value to SIG_DIGITS significant digits: a float, or an int where the rounded value is whole."""
    assert isinstance(v, (int, float)) and math.isfinite(v), v
    r = float('%.*g' % (SIG_DIGITS, v))
    if r == int(r) and abs(r) < 1e15:
        return int(r)
    return r


def fig(value, year, mark, source, rule=None):
    """A FIGURE [value, year, mark, source(, rule)] (FORMAT-2025.txt): the rule exactly when the mark is 1."""
    assert WINDOW_FIRST <= year <= WINDOW_LAST, (value, year)
    assert mark in (1, 2, 3), (value, mark)
    assert (mark == 1) == (rule is not None), (value, year, mark, source, rule)
    assert source in SOURCE_ROWS and (rule is None or rule in RULES), (source, rule)
    out = [rnd(value), year, mark, source]
    if rule is not None:
        out.append(rule)
    return out


def latest_year(years):
    """The latest year of an iterable that falls in the window, or None."""
    ys = [y for y in years if WINDOW_FIRST <= y <= WINDOW_LAST]
    return max(ys) if ys else None


# ===================================================================== the measures and regions tables
def read_measures():
    """measures.tsv as a list of dicts, in order."""
    head, rows = common.read_tsv(HERE + '/measures.tsv')
    out = [dict(zip(head, r + [''] * (len(head) - len(r)))) for r in rows]
    assert len(out) == 70 and len(set(m['key'] for m in out)) == 70, len(out)
    return out


def read_regions():
    """regions.tsv as a list of dicts, in order."""
    head, rows = common.read_tsv(HERE + '/regions.tsv')
    return [dict(zip(head, r)) for r in rows]


def parse_spec(spec):
    """A measure's source spec as (kind, body, filters): kind wdi, wdi-cagr, none or a source folder id; for a
    folder source body is the file and filters the comma-separated KEY=value pairs after the bar."""
    kind, _, rest = spec.partition(':')
    if kind in ('wdi', 'wdi-cagr', 'none'):
        return kind, rest, {}
    body, _, filt = rest.partition('|')
    filters = {}
    for part in filt.split(','):
        k, _, v = part.partition('=')
        filters[k.strip()] = v.strip()
    return kind, body.strip(), filters


# ===================================================================== the WDI
class Wdi:
    """The World Bank's indicators, each read once as {iso3: {year: value}} for the 196 nations only."""

    def __init__(self, sources):
        self.sources = sources
        self.cache = {}

    def get(self, code):
        if code not in self.cache:
            raw = econ_paths_sources.wdi(code, self.sources)
            self.cache[code] = {iso: ys for iso, ys in raw.items() if iso in common.NATIONS}
            if code == 'PA.NUS.PPP' and 'BGR' in self.cache[code]:      # euros to levs, as the exchange rate is
                self.cache[code]['BGR'] = {y: v * BGN_PER_EUR for y, v in self.cache[code]['BGR'].items()}
        return self.cache[code]

    def values(self, spec):
        """{iso3: {year: value}} for a wdi: or wdi-cagr: spec, a year only where every part exists."""
        kind, body, _ = parse_spec(spec)
        if kind == 'wdi-cagr':
            code, n = body.rsplit(':', 1)
            n = int(n)
            out = {}
            for iso, ys in self.get(code).items():
                for y in range(WINDOW_FIRST, WINDOW_LAST + 1):
                    if y in ys and y - n in ys and ys[y] > 0 and ys[y - n] > 0:
                        out.setdefault(iso, {})[y] = ((ys[y] / ys[y - n]) ** (1.0 / n) - 1.0) * 100.0
            return out
        k = 1.0
        if '*' in body:
            body, kk = body.split('*')
            k = float(kk)
        if '/' not in body:
            return {iso: dict(ys) for iso, ys in self.get(body).items()}
        a, b = body.split('/')
        sa, sb = self.get(a), self.get(b)
        out = {}
        for iso, ys in sa.items():
            for y, v in ys.items():
                if iso in sb and y in sb[iso] and sb[iso][y] != 0:
                    out.setdefault(iso, {})[y] = v / sb[iso][y] * k
        return out


# ===================================================================== the nations' world file
class World:
    """world-1.bin through the reference reader: the nations' names and money, and the 2025 path figures."""

    def __init__(self, path):
        wf = worldread.WorldFile(path)
        head, rows = wf.nations()
        by = {r[1]: dict(zip(head, r)) for r in rows if r[3] == 'nation'}
        assert sorted(by) == common.NATIONS, 'world-1.bin nations differ from common.NATIONS'
        self.nation = by
        p = wf.paths()
        self.names = [s[0] for s in p['series']]
        self.years = p['years']
        self.offset = BOARD_YEAR - p['first_year']
        self.value = p['value']
        self.mark = p['mark']

    def money(self, iso):
        return self.nation[iso]['money']

    def fill(self, iso, series):
        """{BOARD_YEAR: (value, mark, 'world-1', rule)} from a path series, or {} when world-1 has none (mark 0)."""
        s = self.names.index(series)
        i = common.NATIONS.index(iso) * self.years + self.offset
        v, m = self.value[s][i], self.mark[s][i]
        if m == 0 or not (v > 0) or not math.isfinite(v):
            return {}
        return {BOARD_YEAR: (v, m, 'world-1', 'world-1' if m == 1 else None)}


# ===================================================================== the nations' own money and people series
def per_person(gdp, people):
    """A per-person series from a GDP series and a people series: the lower mark, source world-1 when either is."""
    out = {}
    for y, (g, gm, gs, _) in gdp.items():
        if y in people:
            p, pm, ps, _ = people[y]
            m = min(gm, pm)
            src = 'wdi' if gs == 'wdi' and ps == 'wdi' else 'world-1'
            out[y] = (g / p, m, src, 'world-1' if m == 1 else None)
    return out


def wdi_series(wdi, code, iso):
    """A nation's WDI series in the window as {year: (value, 3, 'wdi', None)}."""
    return {y: (v, 3, 'wdi', None) for y, v in wdi.get(code).get(iso, {}).items()
            if WINDOW_FIRST <= y <= WINDOW_LAST}


def market_as_ppp(series):
    """D13: a nation with no PPP series takes its market series, mark 1 (ppp-market), the market figure's source."""
    return {y: (v, 1, src, 'ppp-market') for y, (v, _, src, _) in series.items()}


def nation_series(wdi, world, iso):
    """A nation's series in each of the keys of MONEY_KEYS: {key: {year: (value, mark, source, rule)}}. WDI first;
    world-1's 2025 figure for people, GDP and constant GDP where the WDI has none in the window; per person over
    people in that case; the market figure for PPP where the WDI has none."""
    s = {}
    s['people'] = wdi_series(wdi, 'SP.POP.TOTL', iso) or world.fill(iso, WORLD_SERIES['people'])
    for key, pkey, code, pcode, _ in MONEYS:
        g = wdi_series(wdi, code, iso)
        if not g and key in WORLD_SERIES:
            g = world.fill(iso, WORLD_SERIES[key])
        pp = wdi_series(wdi, pcode, iso)
        if not g and key == 'gdp.ppp':              # no PPP GDP: the market GDP, mark 1 (D13)
            g, pp = market_as_ppp(s['gdp']), market_as_ppp(s['gdp_pp'])
        elif g and not pp:                          # a GDP the WDI does not give per person: over the people
            pp = per_person(g, s['people'])
        s[key], s[pkey] = g, pp
    return s


# ===================================================================== folder sources (OECD, BIS, EIA, AHD)
class Folder:
    """The files of the non-WDI sources, each read once."""

    def __init__(self, sources, wdi, world):
        self.sources = sources
        self.wdi = wdi
        self.world = world
        self.rows = {}
        self.unit_skips = {}

    def csv_rows(self, rel):
        if rel not in self.rows:
            with open('%s/%s' % (self.sources, rel), encoding='utf-8-sig', newline='') as f:
                self.rows[rel] = list(csv.DictReader(f))
        return self.rows[rel]

    def oecd_csv(self, key, fname, filters):
        """{iso3: {year: (value, mark)}} from an OECD SDMX csv: the rows whose columns hold the filters' values."""
        reserved = ('nation', 'time', 'value', 'mul', 'div')
        want = {k: v for k, v in filters.items() if k not in reserved}
        ncol, tcol, vcol = filters['nation'], filters['time'], filters['value']
        mul = float(filters.get('mul', 1))
        div = filters.get('div')
        divs = self.wdi.get(div.split(':', 1)[1]) if div else None
        out = {}
        seen = {}
        skipped = 0
        for r in self.csv_rows('oecd/' + fname):
            if any(r[k] != v for k, v in want.items()) or r[vcol] == '' or r[ncol] not in common.NATIONS:
                continue
            iso, y = r[ncol], int(r[tcol][:4])
            if not WINDOW_FIRST <= y <= WINDOW_LAST:
                continue
            v = float(r[vcol]) * mul
            if divs is not None:
                if iso not in divs or y not in divs[iso] or divs[iso][y] == 0:
                    continue
                if div == 'wdi:PA.NUS.FCRF' and r.get('UNIT_MEASURE') != self.world.money(iso):
                    if (r.get('UNIT_MEASURE'), self.world.money(iso)) == ('EUR', 'BGN'):
                        v *= BGN_PER_EUR
                    else:
                        skipped += 1                # a wage in a money the nation does not use
                        continue
                v = v / divs[iso][y]
            mark = 2 if r.get('OBS_STATUS') == 'F' else 3
            if (iso, y) in seen:
                assert abs(seen[(iso, y)] - v) <= 1e-9 * max(1.0, abs(v)), (key, iso, y, seen[(iso, y)], v)
            seen[(iso, y)] = v
            out.setdefault(iso, {})[y] = (v, mark)
        self.unit_skips[key] = skipped
        return out

    def policy_rate(self):
        """BIS: the last month with a figure of each year, {iso3: {year: (rate, 3)}}; the euro area's under XM goes
        to each euro-area member whose own series does not reach the euro area's latest year."""
        iso2 = econ_paths_sources.wdi_iso2(self.sources)
        inv = {iso2[n]: n for n in common.NATIONS if n in iso2}
        out = {}
        with zipfile.ZipFile(self.sources + '/bis/WS_CBPOL_csv_col.zip') as z:
            f = io.TextIOWrapper(z.open(z.namelist()[0]), encoding='utf-8-sig', newline='')
            rd = csv.reader(f)
            head = next(rd)
            ra, fr = head.index('REF_AREA'), head.index('FREQ')
            months = [(i, int(h[:4])) for i, h in enumerate(head) if re.fullmatch(r'\d{4}-\d{2}', h)
                      and WINDOW_FIRST <= int(h[:4]) <= WINDOW_LAST]
            for row in rd:
                if row[fr] != 'M' or (row[ra] not in inv and row[ra] != 'XM'):
                    continue
                ys = {}
                for i, y in months:                 # months run in time order, so the last cell of a year wins
                    if row[i] != '':
                        v = float(row[i])
                        if math.isfinite(v):
                            ys[y] = (v, 3)
                out['XM' if row[ra] == 'XM' else inv[row[ra]]] = ys
        xm = out.pop('XM')
        top = latest_year(xm)
        for iso in EURO_AREA:
            if latest_year(out.get(iso, {})) != top:
                out[iso] = dict(xm)
        return {iso: ys for iso, ys in out.items() if ys}

    def eia(self, series_id, mul, div):
        """EIA: {iso3: {year: (value, 3)}} from INTL.txt's series INTL.<id with <ISO3>>, times mul, over a WDI series."""
        divs = self.wdi.get(div.split(':', 1)[1]) if div else None
        pre, post = series_id.split('<ISO3>')
        wanted = {pre + n + post: n for n in common.NATIONS}
        out = {}
        with zipfile.ZipFile(self.sources + '/eia/INTL.zip') as z:
            f = io.TextIOWrapper(z.open('INTL.txt'), encoding='utf-8')
            for ln in f:
                if not ln.startswith('{"series_id":"' + pre):
                    continue
                sid = ln[len('{"series_id":"'):ln.index('"', 14)]
                if sid not in wanted:
                    continue
                iso = wanted[sid]
                for yr, val in json.loads(ln)['data']:
                    if val in (None, 'NA', '--', '') or not WINDOW_FIRST <= int(yr) <= WINDOW_LAST:
                        continue
                    v = float(val) * mul
                    if divs is not None:
                        if iso not in divs or int(yr) not in divs[iso]:
                            continue
                        v = v / divs[iso][int(yr)]
                    out.setdefault(iso, {})[int(yr)] = (v, 3)
        return out

    def ahd_names(self):
        """The workbooks' country names to ISO3: world-1's names and AHD_NAMES."""
        m = {n['name']: iso for iso, n in self.world.nation.items()}
        m.update(AHD_NAMES)
        return m

    def homes(self, fname):
        """HM1.1.A1: dwellings per 1,000, column J else I, its year column M else L."""
        names = self.ahd_names()
        out = {}
        for r in xlsx.rows('%s/oecd/%s' % (self.sources, fname), 'HM1.1.A1'):
            r = r + [''] * (13 - len(r))
            iso = names.get(r[0].strip())
            if iso is None:
                continue
            v, y = (r[9], r[12]) if r[9] != '' else (r[8], r[11])
            if v == '' or y == '' or not WINDOW_FIRST <= int(float(y)) <= WINDOW_LAST:
                continue
            out.setdefault(iso, {})[int(float(y))] = (float(v), 3)
        return out

    def homeless(self, fname, mul):
        """HC3.1.A1: column B is the share of the people (a fraction; a text cell is a percent; NA is skipped),
        column J the year (its first four digits); a nation the workbook splits by region is not in names."""
        names = self.ahd_names()
        out = {}
        for r in xlsx.rows('%s/oecd/%s' % (self.sources, fname), 'Table_HC3.1.A1'):
            r = r + [''] * (10 - len(r))
            iso = names.get(r[0].strip())
            if iso is None or r[1].strip() in ('', 'NA'):
                continue
            try:
                frac = float(r[1])
            except ValueError:
                m = re.match(r'\s*([0-9]+(?:[.,][0-9]+)?)\s*%', r[1])
                if not m:
                    continue
                frac = float(m.group(1).replace(',', '.')) / 100.0
            m = re.match(r'\d{4}', r[9].strip())
            if not m or not WINDOW_FIRST <= int(m.group(0)) <= WINDOW_LAST:
                continue
            out.setdefault(iso, {})[int(m.group(0))] = (frac * mul, 3)
        return out

    def series(self, m):
        """{iso3: {year: (value, mark)}} for a folder-sourced measure."""
        kind, body, f = parse_spec(m['source'])
        if kind in UNREAD:
            raise SystemExit('measure %s names %s, which is not read (UNODC\'s terms)' % (m['key'], kind))
        if kind == 'bis':
            return self.policy_rate()
        if kind == 'eia':
            mul = float(f.get('mul', 1))
            return self.eia(f['series_id'], mul, f.get('div'))
        if kind == 'oecd' and body.endswith('.csv'):
            return self.oecd_csv(m['key'], body, f)
        if kind == 'oecd' and body.startswith('AHD_HM1-1'):
            return self.homes(body)
        if kind == 'oecd' and body.startswith('AHD_HC3-1'):
            return self.homeless(body, float(f.get('mul', 1)))
        raise SystemExit('no reader for measure %s: %s' % (m['key'], m['source']))


# ===================================================================== the city regions
def prefix_of(code):
    """An OECD FUA code's nation prefix: the letters before the first digit (JPN01F is JPN, FR001F is FR)."""
    return re.match(r'[A-Z]+', code).group(0)


class Fua:
    """The OECD's functional urban areas: GDP (USD_PPP and PT_NAT), population and density by area and year."""

    def __init__(self, folder):
        self.eco = {}
        for r in folder.csv_rows('oecd/fua_economy.csv'):
            if r['MEASURE'] == 'GDP' and r['TERRITORIAL_LEVEL'] == 'FUA' and r['OBS_VALUE'] != '' \
                    and r['UNIT_MEASURE'] in ('USD_PPP', 'PT_NAT'):
                self.eco.setdefault(r['REF_AREA'], {}).setdefault(r['UNIT_MEASURE'], {})[int(r['TIME_PERIOD'])] = \
                    float(r['OBS_VALUE'])
        self.pop = self.by_year(folder, 'oecd/fua_population.csv', 'POP', 'PS')
        self.den = self.by_year(folder, 'oecd/fua_density.csv', 'POP_DEN', 'PS_KM2')
        self.by_prefix = {}
        for code in self.eco:
            self.by_prefix.setdefault(prefix_of(code), []).append(code)

    @staticmethod
    def by_year(folder, rel, measure, unit):
        out = {}
        for r in folder.csv_rows(rel):
            if r['MEASURE'] == measure and r['UNIT_MEASURE'] == unit and r['TERRITORIAL_LEVEL'] == 'FUA' \
                    and r['OBS_VALUE'] != '':
                d = out.setdefault(r['REF_AREA'], {})
                y, v = int(r['TIME_PERIOD']), float(r['OBS_VALUE'])
                assert d.get(y, v) == v, (rel, r['REF_AREA'], y)
                d[y] = v
        return out

    def has_gdp(self, code):
        return code in self.eco and bool(self.eco[code].get('USD_PPP'))

    def national_total(self, prefix, year):
        """The nation's GDP in USD_PPP millions: the sum of its FUAs' GDP over the sum of their PT_NAT times 100 (PT_NAT
        is printed to one decimal, and the sum over every FUA of the nation keeps that rounding off a small city)."""
        s_ppp = s_pt = 0.0
        for code in self.by_prefix[prefix]:
            e = self.eco[code]
            if e.get('PT_NAT', {}).get(year, 0) > 0 and year in e.get('USD_PPP', {}):
                s_ppp += e['USD_PPP'][year]
                s_pt += e['PT_NAT'][year]
        return s_ppp / s_pt * 100.0 if s_pt > 0 else None


def oecd_region(reg, fua, nser):
    """A def "oecd" region's share and figures. Returns (share, f)."""
    code, nation = reg['fua'], reg['nation']
    pop, den, eco = fua.pop[code], fua.den.get(code, {}), fua.eco[code]
    # share.output: the FUA's GDP over its nation's, the latest year with both
    year = None
    for y in sorted((y for y in eco['USD_PPP'] if WINDOW_FIRST <= y <= WINDOW_LAST), reverse=True):
        tot = fua.national_total(prefix_of(code), y)
        if tot is not None:
            year, so_v = y, eco['USD_PPP'][y] / tot
            break
    assert year is not None, 'no output share for ' + reg['id']
    npop = nser[nation]['people']
    assert year in npop, (reg['id'], year)
    # share.people: the FUA's population over its nation's of the share's year; the nearest year's, marked 1, if none
    if year in pop:
        sp_v, sp_mark, sp_rule = pop[year] / npop[year][0], 3, None
    else:
        near = min(pop, key=lambda y: (abs(y - year), y))
        sp_v, sp_mark, sp_rule = pop[near] / npop[year][0], 1, 'region-output'
    share = {'output': fig(so_v, year, 3, 'oecd'), 'people': fig(sp_v, year, sp_mark, 'oecd', sp_rule)}
    f = region_money(nser[nation], (so_v, year, 3), (sp_v, year, sp_mark), 'oecd')
    py = latest_year(pop)
    people = pop[py]
    f['people'] = fig(people, py, 3, 'oecd')
    if py - POP_GROWTH_YEARS in pop:
        f['pop_growth'] = fig(((people / pop[py - POP_GROWTH_YEARS]) ** (1.0 / POP_GROWTH_YEARS) - 1.0) * 100.0,
                              py, 3, 'oecd')
    ay = latest_year(y for y in pop if y in den and den[y] > 0)
    if ay is not None:
        area = pop[ay] / den[ay]
        f['area'] = fig(area, ay, 3, 'oecd')
        f['density'] = fig(people / area, py, 3, 'oecd')
    if FUA_GDP_CONSTANT_PRICES and year - 1 in eco['USD_PPP']:
        f['growth_real'] = fig((eco['USD_PPP'][year] / eco['USD_PPP'][year - 1] - 1.0) * 100.0, year, 3, 'oecd')
    return share, f


def wup_rows(sources, codes):
    """WUP 2025's rows of the wanted City_Codes for 2020 and 2025: {code: {year: row}}."""
    out = {}
    with gzip.open(sources + '/wup/WUP2025-DB-DEGURBA-Cities-Population-Surface-Data.csv.gz', 'rt',
                   encoding='utf-8-sig', newline='') as f:
        for r in csv.DictReader(f):
            if r['City_Code'] in codes and r['Year'] in ('2020', '2025'):
                out.setdefault(r['City_Code'], {})[int(r['Year'])] = r
    return out


def wup_region(reg, rows, nser):
    """A def "wup" region's share and figures. Returns (share, f)."""
    nation = reg['nation']
    by = rows[reg['wup']]
    people = float(by[2025]['Pop']) * 1000.0
    npop = nser[nation]['people']
    if BOARD_YEAR in npop and npop[BOARD_YEAR][2] == 'wdi':
        sp_v, sp_year, sp_mark, sp_rule = people / npop[BOARD_YEAR][0], BOARD_YEAR, 3, None
    else:
        sp_year = latest_year(npop)
        sp_v, sp_mark, sp_rule = people / npop[sp_year][0], 1, 'region-output'
    share = {'output': fig(sp_v, sp_year, 1, 'wup', 'share-people'), 'people': fig(sp_v, sp_year, sp_mark, 'wup', sp_rule)}
    f = region_money(nser[nation], (sp_v, sp_year, 1), (sp_v, sp_year, sp_mark), 'wup', per_person=False)
    f['people'] = fig(people, BOARD_YEAR, 3, 'wup')
    if 2020 in by and float(by[2020]['Pop']) > 0:
        f['pop_growth'] = fig(((float(by[2025]['Pop']) / float(by[2020]['Pop'])) ** 0.2 - 1.0) * 100.0,
                              BOARD_YEAR, 3, 'wup')
    area = float(by[2025]['AREA_km2'])
    if area > 0:
        f['area'] = fig(area, BOARD_YEAR, 3, 'wup')
        f['density'] = fig(people / area, BOARD_YEAR, 3, 'wup')
    return share, f


def region_money(ser, so, sp, source, per_person=True):
    """A region's GDP in each money and per person: its output share (value, year, mark) times the nation's figure of
    the share's year where the nation has one (the lower mark), else of the nation's latest year (mark 1); per person
    the nation's per-person figure times the output share over the people share, mark 3 only when every part is mark 3
    and of one year. All carry region-output at mark 1; the year is the one the nation's figure is of. A region whose
    output share is its people share (share-people) would only repeat the nation's per-person figure: per_person False
    leaves those out."""
    so_v, so_y, so_m = so
    sp_v, sp_y, sp_m = sp
    f = {}
    for key, pkey, _, _, _ in MONEYS:
        g = ser[key]
        if g:
            y = so_y if so_y in g else max(g)
            m = min(g[y][1], so_m) if y == so_y else 1
            f[key] = fig(so_v * g[y][0], y, m, source, 'region-output' if m == 1 else None)
        p = ser[pkey]
        if p and per_person:
            y = so_y if so_y in p else max(p)
            m = 3 if (y == so_y and y == sp_y and p[y][1] == 3 and so_m == 3 and sp_m == 3) else 1
            f[pkey] = fig(p[y][0] * so_v / sp_v, y, m, source, 'region-output' if m == 1 else None)
    return f


# ===================================================================== the whole file
def nation_figures(measures, wdi, folder, nser):
    """Every nation's {key: FIGURE}: the nation series for GDP and people, WDI and folder specs for the rest."""
    figs = {iso: {} for iso in common.NATIONS}
    for iso in common.NATIONS:
        for key, s in nser[iso].items():
            if s:
                y = latest_year(s)
                v, m, src, rule = s[y]
                figs[iso][key] = fig(v, y, m, src, rule)
    for m in measures:
        kind, _, _ = parse_spec(m['source'])
        if kind == 'none' or m['scope'] == 'Y' or m['key'] in MONEY_KEYS:
            continue
        if kind in ('wdi', 'wdi-cagr'):
            s = {iso: {y: (v, 3) for y, v in ys.items()} for iso, ys in wdi.values(m['source']).items()}
            src = 'wdi'
        else:
            s = folder.series(m)
            src = kind
        for iso, ys in s.items():
            y = latest_year(ys)
            if y is not None:
                figs[iso][m['key']] = fig(ys[y][0], y, ys[y][1], src)
        if m['key'] in ('avg_wage', 'min_wage'):
            for iso, f in figs.items():
                assert m['key'] not in f or WAGE_USD[0] < f[m['key']][0] < WAGE_USD[1], (m['key'], iso, f[m['key']])
    return figs


def band(people):
    """The size band: the number of digits in the people less one (Halifax, 4xx,xxx: 5)."""
    return len(str(int(people))) - 1


def ordered(f, order):
    """A place's figures in the file's key order."""
    return {k: f[k] for k in order if k in f}


def key_order(measures):
    """Measure keys in measures.tsv's order, each GDP key followed by its variants."""
    order = []
    for m in measures:
        order.append(m['key'])
        if m['key'] == 'gdp':
            order += ['gdp.k', 'gdp.ppp']
        if m['key'] == 'gdp_pp':
            order += ['gdp_pp.k', 'gdp_pp.ppp']
    return order


def build(args):
    measures = read_measures()
    regions = read_regions()
    wdi = Wdi(args.sources)
    world = World(args.world)
    folder = Folder(args.sources, wdi, world)
    notes = []
    order = key_order(measures)
    nser = {iso: nation_series(wdi, world, iso) for iso in common.NATIONS}
    nfig = nation_figures(measures, wdi, folder, nser)

    ids = [r['id'] for r in regions]
    assert len(set(ids)) == len(ids), 'two regions share an id'
    for r in regions:
        assert r['id'] not in common.NATIONS, 'region id %s is a nation\'s ISO3' % r['id']
        assert r['nation'] in common.NATIONS, 'region %s: nation %s is not one of the 196' % (r['id'], r['nation'])
    fua = Fua(folder)
    wup = wup_rows(args.sources, set(r['wup'] for r in regions))
    places = []
    for iso in common.NATIONS:
        f = ordered(nfig[iso], order)
        p = {'id': iso, 'kind': 'nation', 'name': world.nation[iso]['name']}
        if 'people' in f:
            p['band'] = band(f['people'][0])
        p['f'] = f
        places.append(p)
    wup_mismatch = []
    for r in regions:
        if fua.has_gdp(r['fua']) and r['fua'] in fua.pop:
            share, f = oecd_region(r, fua, nser)
            d = 'oecd'
        else:
            assert r['wup'] in wup and 2025 in wup[r['wup']], 'no WUP 2025 row for ' + r['id']
            if wup[r['wup']][2025]['ISO3_Code'] != r['nation']:
                wup_mismatch.append((r['id'], r['nation'], wup[r['wup']][2025]['ISO3_Code']))
            share, f = wup_region(r, wup, nser)
            d = 'wup'
        for k, v in list(share.items()):
            assert 0 < v[0] < 1, ('share out of range', r['id'], k, v)
        npeople = nser[r['nation']]['people']
        py = f['people'][1]
        assert f['people'][0] < npeople[py if py in npeople else max(npeople)][0], \
            ('region larger than its nation', r['id'])
        assert set(f) <= set(REGION_KEYS), (r['id'], sorted(set(f) - set(REGION_KEYS)))
        p = {'id': r['id'], 'kind': 'region', 'name': r['name'], 'band': band(f['people'][0]),
             'nation': r['nation'], 'def': d, 'share': share, 'f': ordered(f, order)}
        places.append(p)
    notes.append(('wup nation differs', wup_mismatch))
    return measures, regions, places, order, folder, notes


def measure_entry(m):
    """A measure's meta entry (FORMAT-2025.txt): money and w only when true."""
    e = {'key': m['key'], 'group': m['group'], 'name': m['name'], 'unit': m['unit'], 'arrow': m['arrow']}
    if m['money'] == '1':
        e['money'] = True
    e['scope'] = m['scope']
    if m['w'] == '1':
        e['w'] = True
    e['source'] = m['source']
    e['note'] = '. '.join(x for x in (m['note'], NOTE_ADDITIONS.get(m['key'], '')) if x)
    return e


def meta_of(measures, places, made, cpi_u):
    """The meta member."""
    figures = estimates = 0
    used = set(['bls'])
    for p in places:
        for fg in p['f'].values():
            figures += 1
            estimates += fg[2] == 1
            used.add(fg[3])
        for fg in p.get('share', {}).values():
            used.add(fg[3])
    return {
        'name': 'world-2025', 'made': made, 'year': BOARD_YEAR, 'window': [WINDOW_FIRST, WINDOW_LAST],
        'marks': MARKS,
        'us_prices_2015_over_2025': cpi_u['2015'] / cpi_u['2025'],
        'cpi_u': dict(cpi_u, series=CPI_U_SERIES, source='bls'),
        'sources': {i: {'name': r[0], 'url': r[1], 'licence': r[2], 'downloaded': r[3]}
                    for i, r in SOURCE_ROWS.items() if i in used},
        'rules': RULES, 'definitions': DEFINITIONS,
        'measures': [measure_entry(m) for m in measures],
        'variants': {
            'gdp.k': {'of': 'gdp', 'money': MONEYS[1][4], 'source': 'wdi:' + MONEYS[1][2]},
            'gdp.ppp': {'of': 'gdp', 'money': MONEYS[2][4], 'source': 'wdi:' + MONEYS[2][2]},
            'gdp_pp.k': {'of': 'gdp_pp', 'money': MONEYS[1][4], 'source': 'wdi:' + MONEYS[1][3]},
            'gdp_pp.ppp': {'of': 'gdp_pp', 'money': MONEYS[2][4], 'source': 'wdi:' + MONEYS[2][3]}},
        'counts': {'nations': len(common.NATIONS), 'regions': sum(1 for p in places if p['kind'] == 'region'),
                   'figures': figures, 'estimates': estimates}}


def render(meta, places):
    """The file's text: meta indented, each place on one line."""
    out = ['{\n"meta": {']
    items = []
    for k, v in meta.items():
        if k == 'measures':
            body = '[\n' + ',\n'.join('  ' + json.dumps(e, ensure_ascii=False, separators=(',', ':')) for e in v) + '\n ]'
        else:
            body = json.dumps(v, indent=1, ensure_ascii=False).replace('\n', '\n ')
        items.append(' %s: %s' % (json.dumps(k), body))
    out.append('\n' + ',\n'.join(items) + '\n},\n"places": [')
    out.append('\n' + ',\n'.join(json.dumps(p, ensure_ascii=False, separators=(',', ':')) for p in places))
    out.append('\n]\n}\n')
    return ''.join(out)


def coverage(measures, places, order):
    """Per measure key: nations and regions with a figure, the figures by mark, the years' range."""
    rows = []
    entry = {m['key']: m for m in measures}
    for key in order:
        base = entry[key.split('.')[0]] if key.split('.')[0] in entry else entry[key]
        n = [p['f'][key] for p in places if p['kind'] == 'nation' and key in p['f']]
        r = [p['f'][key] for p in places if p['kind'] == 'region' and key in p['f']]
        allf = n + r
        ys = [x[1] for x in allf]
        rows.append([key, base['scope'], base['source'].split(':')[0], len(n), len(r),
                     sum(1 for x in allf if x[2] == 3), sum(1 for x in allf if x[2] == 2),
                     sum(1 for x in allf if x[2] == 1), min(ys) if ys else '-', max(ys) if ys else '-'])
    return rows


def checks(places):
    """Identity checks over the nations; returns the lines to print. The US's price level and currency against parity
    must be 1; GDP at PPP over GDP is 1 over the price level; GDP per person is GDP over the people (within 1%)."""
    by = {p['id']: p['f'] for p in places if p['kind'] == 'nation'}
    lines = []
    usa = by['USA']
    for k in ('price_level', 'fx_vs_parity'):
        assert abs(usa[k][0] - 1) <= 0.001, (k, usa[k])
    n = bad = 0
    worst = (0.0, '')
    for iso, f in by.items():
        if all(k in f for k in ('gdp', 'gdp.ppp', 'price_level')) and f['gdp'][1] == f['gdp.ppp'][1] == f['price_level'][1] \
                and f['gdp.ppp'][4:] == [] and f['gdp'][3] == 'wdi':
            d = abs(f['gdp.ppp'][0] / f['gdp'][0] * f['price_level'][0] - 1)
            n += 1
            if d > 0.01:
                bad += 1
            if d > worst[0]:
                worst = (d, iso)
    lines.append('identity gdp.ppp/gdp x price_level = 1: %d nations compared, %d off by more than 1%%, worst %s %.4f'
                 % (n, bad, worst[1], worst[0]))
    n = bad = 0
    worst = (0.0, '')
    for iso, f in by.items():
        if all(k in f for k in ('gdp', 'gdp_pp', 'people')) and f['gdp'][1] == f['gdp_pp'][1] == f['people'][1] \
                and f['gdp'][3] == f['gdp_pp'][3] == f['people'][3] == 'wdi':
            d = abs(f['gdp_pp'][0] / (f['gdp'][0] / f['people'][0]) - 1)
            n += 1
            if d > 0.01:
                bad += 1
            if d > worst[0]:
                worst = (d, iso)
    lines.append('identity gdp_pp = gdp/people: %d nations compared, %d off by more than 1%%, worst %s %.4f'
                 % (n, bad, worst[1], worst[0]))
    return lines


def main():
    sys.stdout.reconfigure(encoding='utf-8')
    p = common.parser('Builds world-2025.json, the Rankings\' measures file.')
    p.add_argument('--world', default=WORLD_BIN, help="world-1.bin's path, default " + WORLD_BIN)
    p.add_argument('--out', default=OUT, help='the file to write, default ' + OUT)
    p.add_argument('--made', default=None, help="meta's made date yyyy-mm-dd, default today")
    args = p.parse_args()
    made = args.made or time.strftime('%Y-%m-%d')
    measures, regions, places, order, folder, notes = build(args)
    meta = meta_of(measures, places, made, read_cpi_u(args.sources))
    text = render(meta, places)
    data = text.encode('utf-8')
    assert len(data) < SIZE_LIMIT, 'the file is %d bytes' % len(data)
    json.loads(text)                                  # it parses
    os.makedirs(os.path.dirname(args.out), exist_ok=True)
    with open(args.out, 'w', encoding='utf-8', newline='\n') as f:
        f.write(text)
    cov = coverage(measures, places, order)
    cdir = common.work_dir(args) + '/measures'
    os.makedirs(cdir, exist_ok=True)
    common.write_tsv(cdir + '/coverage.tsv', ['key', 'scope', 'source', 'nations', 'regions', 'mark3', 'mark2', 'mark1',
                                              'year_min', 'year_max'], cov)
    print('wrote %s: %d bytes, %d nations, %d regions, %d figures, %d estimates' % (
        args.out, len(data), meta['counts']['nations'], meta['counts']['regions'], meta['counts']['figures'],
        meta['counts']['estimates']))
    cells = ['%s %d/%d/%d' % (c[0], c[3], c[4], c[7]) for c in cov]
    print('per key nations/regions/estimates:')
    for i in range(0, len(cells), 5):
        print('  ' + ' | '.join(cells[i:i + 5]))
    none_keys = set(m['key'] for m in measures if m['source'].startswith('none:'))
    print('no figure anywhere, not none: %s' % (', '.join(c[0] for c in cov if c[3] + c[4] == 0 and c[0] not in none_keys)
                                                or '(none)'))
    print('wages left out for a money the nation does not use: %s' % (', '.join(
        '%s %d' % kv for kv in sorted(folder.unit_skips.items()) if kv[1]) or '(none)'))
    for name, v in notes:
        print('%s: %s' % (name, v or '(none)'))
    for ln in checks(places):
        print(ln)


if __name__ == '__main__':
    main()
