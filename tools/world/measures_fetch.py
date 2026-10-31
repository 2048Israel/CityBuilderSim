"""The downloads the Rankings measures are made from (batch LB1, slice A, 2026-10-30): every World Bank WDI indicator
that tools/world/measures.tsv names, and the files of the OECD, BIS, UNODC, UN WUP 2025 and US EIA sources it points to.

WHY: world-2025.json carries up to 70 measures for 196 nations, and WN0's six WDI files cover only the money and
population. Downloading in one script keeps the sources reproducible on the PC (the same URLs, the same folders) and
puts every URL beside the licence it was checked against (MEASURES-SOURCES.txt, in the sources folder). A WDI file
that WN0 baked world-1.bin from is never replaced, even with --force: its figures would move under that file's SHA.

Run through python -I: the downloads' folder is not on sys.path.
    python -I measures_fetch.py [--sources DIR] [--force]
"""
import argparse
import json
import os
import re
import sys
import time
import urllib.error
import urllib.request

HERE = os.path.dirname(os.path.abspath(__file__))
DEFAULT_SOURCES = 'C:/Users/Jerus/Documents/world-sources'

# The six WDI files WN0 baked world-1.bin from: never overwritten, whatever --force says.
WN0_WDI = ['FP.CPI.TOTL', 'NY.GDP.MKTP.CD', 'NY.GDP.MKTP.KD', 'NY.GDP.MKTP.PP.KD', 'PA.NUS.FCRF', 'SP.POP.TOTL']

# WDI series the measures do not name but the builder needs: GDP kept at constant 2015 US$ and at PPP, and the PPP rate.
WDI_EXTRA = ['NY.GDP.MKTP.KD', 'NY.GDP.MKTP.PP.CD', 'NY.GDP.PCAP.KD', 'NY.GDP.PCAP.PP.CD', 'PA.NUS.PPP']

WDI_URL = ('https://api.worldbank.org/v2/country/all/indicator/%s?format=json&per_page=20000&date=2000:2025')

OECD = 'https://sdmx.oecd.org/public/rest/data/'
AHD = 'https://webfs.oecd.org/Els-com/Affordable_Housing_Database/'
CSV = 'startPeriod=2015&format=csvfilewithlabels'

# (folder, file name, url): the non-WDI sources, one row per file. OECD dataflows are agency,id,version/key; the
# figures from 2015 on, because the Rankings screen shows the latest year and nothing older than 2015.
FILES = [
    ('oecd', 'earnings_AV_AN_WAGE.csv', OECD + 'OECD.ELS.SAE,DSD_EARNINGS@AV_AN_WAGE,1.0/all?' + CSV),
    ('oecd', 'earnings_MW_CURP.csv', OECD + 'OECD.ELS.SAE,DSD_EARNINGS@MW_CURP,1.0/all?' + CSV),
    ('oecd', 'earnings_MIN2AVE.csv', OECD + 'OECD.ELS.SAE,DSD_EARNINGS@MIN2AVE,1.0/all?' + CSV),
    ('oecd', 'finmark_IRLT.csv', OECD + 'OECD.SDD.STES,DSD_STES@DF_FINMARK,4.0/.A.IRLT.......?' + CSV),
    ('oecd', 'socx_PUB_OLD.csv', OECD + 'OECD.ELS.SPD,DSD_SOCX_AGG@DF_PUB_OLD,1.0/all?' + CSV),
    ('oecd', 'naag_V_households.csv', OECD + 'OECD.SDD.NAD,DSD_NAAG@DF_NAAG_V,1.0/all?' + CSV),
    ('oecd', 'fua_economy.csv', OECD + 'OECD.CFE.EDS,DSD_FUA_ECO@DF_ECONOMY,1.1/all?' + CSV),
    ('oecd', 'fua_population.csv', OECD + 'OECD.CFE.EDS,DSD_FUA_DEMO@DF_AGE_SEX,1.3/.A.POP.PS._T._T._Z..?' + CSV),
    ('oecd', 'fua_density.csv', OECD + 'OECD.CFE.EDS,DSD_FUA_TERR@DF_DENSITY,1.1/all?' + CSV),
    ('oecd', 'AHD_HM1-1-Housing-stock-and-construction.xlsx', AHD + 'HM1-1-Housing-stock-and-construction.xlsx'),
    ('oecd', 'AHD_HC3-1-Population-experiencing-homelessness.xlsx',
     AHD + 'HC3-1-Population-experiencing-homelessness.xlsx'),
    ('bis', 'WS_CBPOL_csv_col.zip', 'https://data.bis.org/static/bulk/WS_CBPOL_csv_col.zip'),
    ('unodc', 'data_cts_prisons_and_prisoners.xlsx',
     'https://data.unodc.org/sites/dataportal.unodc.org/files/2026-07/data_cts_prisons_and_prisoners.xlsx'),
    ('unodc', 'data_cts_access_and_functioning_of_justice.xlsx',
     'https://data.unodc.org/sites/dataportal.unodc.org/files/2026-07/data_cts_access_and_functioning_of_justice.xlsx'),
    ('wup', 'WUP2025-DB-DEGURBA-Cities-Population-Surface-Data.csv.gz',
     'https://population.un.org/wup/assets/Download/Cities/WUP2025-DB-DEGURBA-Cities-Population-Surface-Data.csv.gz'),
    ('wup', 'WUP2025_Cities_Indicators.csv',
     'https://population.un.org/wup/assets/Download/Cities/WUP2025_Cities_Indicators.csv'),
    ('eia', 'INTL.zip', 'https://www.eia.gov/opendata/bulk/INTL.zip'),
]


def wdi_codes():
    """Every WDI code measures.tsv names (wdi:A, wdi:A/B*k, wdi-cagr:A:10) plus WDI_EXTRA, in first-seen order."""
    out = []
    with open(os.path.join(HERE, 'measures.tsv'), encoding='utf-8') as f:
        rows = [line.rstrip('\n').split('\t') for line in f]
    col = rows[0].index('source')
    for r in rows[1:]:
        s = r[col]
        if s.startswith('wdi-cagr:'):
            found = [s.split(':')[1]]
        elif s.startswith('wdi:'):
            found = re.findall(r'[A-Z]{2}\.[A-Z0-9.]+', s.split(':', 1)[1])
        else:
            continue
        for c in found:
            if c not in out:
                out.append(c)
    for c in WDI_EXTRA:
        if c not in out:
            out.append(c)
    return out


def get(url, tries=4):
    """The bytes at url; a browser-like agent (some sites refuse the default one), a pause and a retry on a failure."""
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (CityBuilderSim measures_fetch)'})
    for i in range(tries):
        try:
            with urllib.request.urlopen(req, timeout=300) as r:
                return r.read()
        except (urllib.error.URLError, TimeoutError, ConnectionError) as e:
            if i == tries - 1:
                raise
            print('  retry %d (%s)' % (i + 1, e))
            time.sleep(3 * (i + 1))


def wdi_file(code):
    """One WDI indicator as the API's JSON: [header, rows] with every page's rows joined (the header is page 1's)."""
    first = json.loads(get(WDI_URL % code))
    if len(first) < 2:
        raise ValueError('WDI has no series %s: %s' % (code, first[0].get('message')))
    header, rows = first[0], list(first[1] or [])
    for page in range(2, int(header.get('pages', 1)) + 1):
        rows += json.loads(get(WDI_URL % code + '&page=%d' % page))[1] or []
    header['page'], header['pages'] = 1, 1
    return json.dumps([header, rows], separators=(',', ':')).encode('utf-8')


def save(path, data, force, protected=False):
    """Write data unless the file exists (and force is off, or it is protected); print one line either way."""
    name = path.replace('\\', '/').split('/')[-2:]
    if os.path.exists(path) and (protected or not force):
        print('skipped     %9d  %s/%s' % (os.path.getsize(path), name[0], name[1]))
        return
    body = data() if callable(data) else data    # fetched before the file is opened: a failure leaves no empty file
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'wb') as f:
        f.write(body)
    print('downloaded  %9d  %s/%s' % (os.path.getsize(path), name[0], name[1]))


def main():
    ap = argparse.ArgumentParser(description=__doc__.split('\n')[0])
    ap.add_argument('--sources', default=DEFAULT_SOURCES)
    ap.add_argument('--force', action='store_true', help='download again a file that exists (never a WN0 WDI file)')
    a = ap.parse_args()
    failed = []
    jobs = [('%s/wdi/%s.json' % (a.sources, c), (lambda c=c: wdi_file(c)), c in WN0_WDI) for c in wdi_codes()]
    jobs += [('%s/%s/%s' % (a.sources, d, n), (lambda u=u: get(u)), False) for d, n, u in FILES]
    for path, data, protected in jobs:
        try:
            save(path, data, a.force, protected)
        except (ValueError, OSError, urllib.error.URLError) as e:
            print('FAILED      %s: %s' % (os.path.basename(path), e))
            failed.append(path)
    return 1 if failed else 0


if __name__ == '__main__':
    sys.exit(main())
