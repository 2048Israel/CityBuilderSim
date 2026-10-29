"""BACI's countries as the 196 nations (batch WN0, E2): which of the 196 each BACI country code is, or none.

WHY: BACI counts about 238 reporters and partners, among them territories (Greenland, Guam, New Caledonia), groupings
("Europe EFTA, nes") and Taiwan as "Other Asia, nes"; the game has 196 nations and no territories (a territory's land
and trade are its sovereign's, spec-nations 3.1). The main pass and the samples pass map through this one
function, so a sample can only differ from the file by the arithmetic, never by the country table.
"""
import csv
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common

# BACI codes Natural Earth's owners table lacks, and the nation each belongs to (None: no nation of the 196). Natural
# Earth has no feature for Christmas, the Cocos Islands, Mayotte, Bonaire, Tokelau and the US Pacific islands, and BACI
# keeps "Other Asia, nes" for Taiwan; the rest are groupings and countries that ended before 2024.
OWN_TABLE = {
    'S19': 'TWN',   # Other Asia, nes: Taiwan (BACI's own note), the only large flow in it
    'CXR': 'AUS',   # Christmas Island: an Australian external territory
    'CCK': 'AUS',   # Cocos (Keeling) Islands: Australian
    'MYT': 'FRA',   # Mayotte: a French department
    'BES': 'NLD',   # Bonaire, Sint Eustatius and Saba: Dutch special municipalities
    'TKL': 'NZL',   # Tokelau: a New Zealand territory
    'PUS': 'USA',   # US Miscellaneous Pacific Islands: American
    'R20': None,    # Europe EFTA, nes: a grouping
    'CSK': None,    # Czechoslovakia, ended 1992
    'DDR': None,    # East Germany, ended 1990
    'ANT': None,    # Netherlands Antilles, ended 2010
    'SUN': None,    # USSR, ended 1990
    'SCG': None,    # Serbia and Montenegro, ended 2005
    'ZA1': None,    # Southern African Customs Union, ended 1999
}


def read_owners(path):
    """WORK/geo/owners.tsv (G's): {code: owner}, the owner an iso3 of the 196 or 'area:<code>'. {} when absent."""
    owners = {}
    if path is None or not os.path.exists(path):
        return owners
    with open(path, encoding='utf-8') as f:
        rd = csv.DictReader(f, delimiter='\t')
        for r in rd:
            owners[r['code']] = r['owner']
    return owners


def build(country_codes_csv_lines, owners):
    """Maps each BACI country to its nation. country_codes_csv_lines: the lines of country_codes_V202601.csv.

    Returns (by_code, report): by_code {BACI numeric code as bytes: nation number 1..196, or 0 for none}; report a list
    of (code, iso3, name, how, nation iso3 or '-') in code order, how one of direct, taiwan, owner, own table, area,
    unmapped.
    """
    nations = set(common.NATIONS)
    by_code = {}
    report = []
    for r in csv.DictReader(country_codes_csv_lines):
        code, name, iso3 = r['country_code'].strip(), r['country_name'].strip(), r['country_iso3'].strip()
        how, nation = 'unmapped', None
        if iso3 in nations:
            how, nation = 'direct', iso3
        elif name == 'Other Asia, nes':
            how, nation = 'taiwan', 'TWN'
        elif iso3 in owners:
            o = owners[iso3]
            if o in nations:
                how, nation = 'owner', o
            else:
                how = 'area'
        elif iso3 in OWN_TABLE:
            if OWN_TABLE[iso3] is not None:
                how, nation = 'own table', OWN_TABLE[iso3]
            else:
                how = 'own table (none)'
        by_code[code.encode()] = common.number(nation) if nation else 0
        report.append((code, iso3, name, how, nation or '-'))
    return by_code, report
