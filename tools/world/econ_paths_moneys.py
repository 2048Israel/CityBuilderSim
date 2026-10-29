"""The moneys layer of world-1.bin (batch WN0, slice E1, 2026-10-28): every money any nation used in 2000-2025 and its
average rate, units a US dollar, in each of the 312 months, from the BIS where it has one (mark 3) and the game's
estimate where it has not (mark 1).

WHY: the trade, the leaderboard and the nations' prices all need each nation's money in each month, and the sources
disagree about units (the BIS keeps the old ouguiya and dobra but the new leone, and changes Venezuela's unit at each
redenomination) and have holes (Afghanistan after 2021, Zimbabwe's dollar years, the dollarised years). The rules for
every hole are here, in one order, and every filled month is recorded in FILLS so the report can list them.
"""
import math
import os
import struct
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common

def month_name(m):
    """Month number -> 'yyyy-mm'."""
    return '%d-%02d' % (common.FIRST_YEAR + m // 12, m % 12 + 1)


def month_number(s):
    """'yyyy-mm' -> month number, 0 for 2000-01."""
    y, mo = s.split('-')
    return (int(y) - common.FIRST_YEAR) * 12 + int(mo) - 1


# The euro's fixed conversion rates (Council Regulations), units of the old money a euro: the legacy moneys' rate is
# BIS's series for their area (in euros, back-cast at this rate) times it.
EURO_CONVERSION = {'GRD': 340.750, 'SIT': 239.640, 'CYP': 0.585274, 'MTL': 0.429300, 'SKK': 30.1260, 'EEK': 15.6466,
                   'LVL': 0.702804, 'LTL': 3.45280, 'HRK': 7.53450}

# The BIS area whose euro series is each legacy money's history (the euro area's own series is the euro's).
LEGACY_AREA = {'GRD': 'GR', 'SIT': 'SI', 'CYP': 'CY', 'MTL': 'MT', 'SKK': 'SK', 'EEK': 'EE', 'LVL': 'LV', 'LTL': 'LT',
               'HRK': 'HR', 'BGN': 'BG'}

# Bulgaria's lev, a currency board at 1.95583 to the euro since 1999; BIS has Bulgaria in euros (back-cast at this).
BGN_PER_EUR = 1.95583

# Where a money's BIS series is under another code or area, or in other units: code -> (area, [(first month, factor)]),
# the factor taking BIS's units to the money's (the ISO code of December 2025). The ouguiya and the dobra were
# redenominated in 2018 (10:1 and 1000:1) and BIS kept the old units; the leone's (1000:1, 2022) it restated; Venezuela's
# bolivar was redenominated in 2018-08 (100,000:1) and 2021-10 (1,000,000:1) and BIS's series is in the unit of each
# period; Zimbabwe's old dollar is kept in the units of the 2006 redenomination (ZWN) and BIS's 2008-08 redenomination
# (ZWR, 10^10 ZWN) is restated into them.
UNITS = {'MRU': ('MR', [(0, 0.1)]),
         'STN': ('ST', [(0, 0.001)]),
         'SLE': ('SL', [(0, 1.0)]),
         'VES': ('VE', [(0, 1e-11), (month_number('2018-08'), 1e-6), (month_number('2021-10'), 1.0)]),
         'ZWN': ('ZW', [(0, 1.0), (month_number('2008-08'), 1e10)]),
         'ZWL': ('ZW', [(0, 1.0)])}

# BIS's Syrian series is the pre-war official rate of 11.225, unchanged to 2017-07 while WDI's official rate went from
# 47 to 515: the BIS months from here (2011-01) on are not used.
STALE_FROM = {'SYP': month_number('2011-01')}

# The 2019 RTGS dollar and the 2024 Zimbabwe Gold: the legal conversion 1 ZWG = 2498.7242 ZWL and the launch rate of
# 13.5616 ZWG a dollar on 2024-04-05 (Reserve Bank of Zimbabwe) join ZWL's end and ZWG's start; WDI's 2025 figure
# (26.60) is ZWG's year.
ZWG_PER_USD_AT_LAUNCH = 13.5616
ZWL_PER_ZWG = 2498.7242
ANCHORS = {'ZWL': {month_number('2024-04'): ZWG_PER_USD_AT_LAUNCH * ZWL_PER_ZWG},   # ZWL's value there is only a bridge
           'ZWG': {month_number('2024-04'): ZWG_PER_USD_AT_LAUNCH}}

# Malawi's kwacha has been held at 1751 a dollar since 2023-11 (Reserve Bank of Malawi): BIS ends in 2024-09.
PARITY = {'MWK': ('USD', 1751.0)}

# The sucre was fixed at 25,000 a dollar for dollarisation (the Law for the Economic Transformation of Ecuador,
# 2000-03): BIS and WDI have nothing for it.
ECS_PER_USD = 25000.0

# Cuba: no BIS or WDI rate. The official peso is held at 1 to the dollar, the CUC's parity that WDI's dollar GDP rests on
# to 2020 and that gdp_usd keeps after: the unification of 2021-01 at 24 (Banco Central de Cuba) is not applied, because
# the dollar GDP would have to fall 24-fold with it and no source here says by how much.
CUP_PER_USD = 1.0

# North Korea: no BIS or WDI rate. The won's official rate has been about 100 to the dollar since the 2009 redenomination
# (the rate of the UN's operational exchange rates and of the Bank of Korea's estimates of the North's GNI).
KPW_PER_USD = 100.0

# The years of WDI's rate that are the money's own where only some are: Zimbabwe Gold's is 2025 (WDI's 2024 is a mix of
# the RTGS dollar and the Gold).
WDI_ONLY_YEARS = {'ZWG': (2025, 2025)}

# The codes whose WDI figure is not the money's own (WDI gives the nation's money at the end of the period or a mixed
# year), so months of these are never filled from it.
WDI_SKIP = ('ECS', 'ZWN', 'ZWL', 'SVC')

# Names of the moneys, authored: every code any history names.
MONEY_NAMES = {
    'AED': 'UAE dirham', 'AFN': 'Afghan afghani', 'ALL': 'Albanian lek', 'AMD': 'Armenian dram',
    'AOA': 'Angolan kwanza', 'ARS': 'Argentine peso', 'AUD': 'Australian dollar', 'AZN': 'Azerbaijani manat',
    'BAM': 'Bosnia and Herzegovina convertible mark', 'BBD': 'Barbadian dollar', 'BDT': 'Bangladeshi taka',
    'BGN': 'Bulgarian lev', 'BHD': 'Bahraini dinar', 'BIF': 'Burundian franc', 'BND': 'Brunei dollar',
    'BOB': 'Bolivian boliviano', 'BRL': 'Brazilian real', 'BSD': 'Bahamian dollar', 'BTN': 'Bhutanese ngultrum',
    'BWP': 'Botswana pula', 'BYN': 'Belarusian ruble', 'BZD': 'Belize dollar', 'CAD': 'Canadian dollar',
    'CDF': 'Congolese franc', 'CHF': 'Swiss franc', 'CLP': 'Chilean peso', 'CNY': 'Chinese renminbi',
    'COP': 'Colombian peso', 'CRC': 'Costa Rican colon', 'CUP': 'Cuban peso', 'CVE': 'Cabo Verde escudo',
    'CYP': 'Cypriot pound', 'CZK': 'Czech koruna', 'DJF': 'Djiboutian franc', 'DKK': 'Danish krone',
    'DOP': 'Dominican peso', 'DZD': 'Algerian dinar', 'ECS': 'Ecuadorian sucre', 'EEK': 'Estonian kroon',
    'EGP': 'Egyptian pound', 'ERN': 'Eritrean nakfa', 'ETB': 'Ethiopian birr', 'EUR': 'Euro',
    'FJD': 'Fijian dollar', 'GBP': 'Pound sterling', 'GEL': 'Georgian lari', 'GHS': 'Ghanaian cedi',
    'GMD': 'Gambian dalasi', 'GNF': 'Guinean franc', 'GRD': 'Greek drachma', 'GTQ': 'Guatemalan quetzal',
    'GYD': 'Guyanese dollar', 'HKD': 'Hong Kong dollar', 'HNL': 'Honduran lempira', 'HRK': 'Croatian kuna',
    'HTG': 'Haitian gourde', 'HUF': 'Hungarian forint', 'IDR': 'Indonesian rupiah', 'ILS': 'Israeli new shekel',
    'INR': 'Indian rupee', 'IQD': 'Iraqi dinar', 'IRR': 'Iranian rial', 'ISK': 'Icelandic krona',
    'JMD': 'Jamaican dollar', 'JOD': 'Jordanian dinar', 'JPY': 'Japanese yen', 'KES': 'Kenyan shilling',
    'KGS': 'Kyrgyzstani som', 'KHR': 'Cambodian riel', 'KMF': 'Comorian franc', 'KPW': 'North Korean won',
    'KRW': 'South Korean won', 'KWD': 'Kuwaiti dinar', 'KZT': 'Kazakhstani tenge', 'LAK': 'Lao kip',
    'LBP': 'Lebanese pound', 'LKR': 'Sri Lankan rupee', 'LRD': 'Liberian dollar', 'LSL': 'Lesotho loti',
    'LTL': 'Lithuanian litas', 'LVL': 'Latvian lats', 'LYD': 'Libyan dinar', 'MAD': 'Moroccan dirham',
    'MDL': 'Moldovan leu', 'MGA': 'Malagasy ariary', 'MKD': 'Macedonian denar', 'MMK': 'Myanmar kyat',
    'MNT': 'Mongolian tugrik', 'MOP': 'Macanese pataca', 'MRU': 'Mauritanian ouguiya', 'MTL': 'Maltese lira',
    'MUR': 'Mauritian rupee', 'MVR': 'Maldivian rufiyaa', 'MWK': 'Malawian kwacha', 'MXN': 'Mexican peso',
    'MYR': 'Malaysian ringgit', 'MZN': 'Mozambican metical', 'NAD': 'Namibian dollar', 'NGN': 'Nigerian naira',
    'NIO': 'Nicaraguan cordoba', 'NOK': 'Norwegian krone', 'NPR': 'Nepalese rupee', 'NZD': 'New Zealand dollar',
    'OMR': 'Omani rial', 'PEN': 'Peruvian sol', 'PGK': 'Papua New Guinean kina', 'PHP': 'Philippine peso',
    'PKR': 'Pakistani rupee', 'PLN': 'Polish zloty', 'PYG': 'Paraguayan guarani', 'QAR': 'Qatari riyal',
    'RON': 'Romanian leu', 'RSD': 'Serbian dinar', 'RUB': 'Russian ruble', 'RWF': 'Rwandan franc',
    'SAR': 'Saudi riyal', 'SBD': 'Solomon Islands dollar', 'SCR': 'Seychellois rupee', 'SDG': 'Sudanese pound',
    'SEK': 'Swedish krona', 'SGD': 'Singapore dollar', 'SIT': 'Slovenian tolar', 'SKK': 'Slovak koruna',
    'SLE': 'Sierra Leonean leone', 'SOS': 'Somali shilling', 'SRD': 'Surinamese dollar', 'SSP': 'South Sudanese pound',
    'STN': 'Sao Tome and Principe dobra', 'SVC': 'Salvadoran colon', 'SYP': 'Syrian pound', 'SZL': 'Swazi lilangeni',
    'THB': 'Thai baht', 'TJS': 'Tajikistani somoni', 'TMT': 'Turkmenistani manat', 'TND': 'Tunisian dinar',
    'TOP': "Tongan pa'anga", 'TRY': 'Turkish lira', 'TTD': 'Trinidad and Tobago dollar', 'TWD': 'New Taiwan dollar',
    'TZS': 'Tanzanian shilling', 'UAH': 'Ukrainian hryvnia', 'UGX': 'Ugandan shilling', 'USD': 'US dollar',
    'UYU': 'Uruguayan peso', 'UZS': 'Uzbekistani som', 'VES': 'Venezuelan bolivar', 'VND': 'Vietnamese dong',
    'VUV': 'Vanuatu vatu', 'WST': 'Samoan tala', 'XAF': 'Central African CFA franc', 'XCD': 'Eastern Caribbean dollar',
    'XOF': 'West African CFA franc', 'YER': 'Yemeni rial', 'ZAR': 'South African rand', 'ZMW': 'Zambian kwacha',
    'ZWG': 'Zimbabwe Gold', 'ZWL': 'Zimbabwe dollar (RTGS)', 'ZWN': 'Zimbabwe dollar (2006)'}

# The BIS area whose series is the money's, where several areas share the code and one is the issuer's.
ISSUER_AREA = {'EUR': 'XM', 'XOF': 'WA', 'XAF': 'CM', 'XCD': 'AG', 'USD': 'US'}

# Every filled month, for the report: (code, '2012-01..2012-06', how).
FILLS = []


def read_moneys(path):
    """moneys.tsv -> {iso3: {'name', 'money', 'history': [(code, first month)], 'regime', 'note'}} in file order."""
    out = {}
    with open(path, encoding='utf-8') as f:
        head = f.readline().rstrip('\n').split('\t')
        for line in f:
            r = dict(zip(head, line.rstrip('\n').split('\t')))
            hist = [(e.split(':')[0], month_number(e.split(':')[1])) for e in r['money_history'].split(',')]
            assert hist[0][1] == 0 and [h[1] for h in hist] == sorted(set(h[1] for h in hist)), r['iso3']
            assert hist[-1][0] == r['money'], (r['iso3'], 'history ends in', hist[-1][0], 'not', r['money'])
            r['history'] = hist
            out[r['iso3']] = r
    assert list(out) == common.NATIONS
    return out


def money_in_use(history, m):
    """The code a history names in month m."""
    code = history[0][0]
    for c, first in history:
        if first <= m:
            code = c
    return code


def _record(code, months, how):
    """Notes that these months of this money are estimates, how: runs of consecutive months become one entry."""
    ms = sorted(months)
    if not ms:
        return
    s = p = ms[0]
    for m in ms[1:] + [None]:
        if m is None or m != p + 1:
            FILLS.append((code, month_name(s) if s == p else month_name(s) + '..' + month_name(p), how))
            s = m
        p = m


def bis_values(code, bis):
    """The BIS monthly series of this money in its own units, {month: value}, or {} where BIS has none."""
    if code == 'USD':
        return dict((m, 1.0) for m in range(common.MONTHS))
    if code in EURO_CONVERSION or code == 'BGN':
        f = EURO_CONVERSION.get(code, BGN_PER_EUR)
        return dict((m, v * f) for m, v in bis[LEGACY_AREA[code]][1].items())
    if code in UNITS:
        area, breaks = UNITS[code]
        out = {}
        for m, v in bis[area][1].items():
            fac = [f for first, f in breaks if first <= m][-1]
            out[m] = v * fac
        if code == 'ZWN':
            out = dict((m, v) for m, v in out.items() if m < month_number('2009-01'))
        if code == 'ZWL':
            out = dict((m, v) for m, v in out.items() if m >= month_number('2019-02'))
        return out
    cands = sorted(a for a, (cur, _) in bis.items() if cur == code)
    if not cands:
        return {}
    if code in ISSUER_AREA and ISSUER_AREA[code] in cands:
        area = ISSUER_AREA[code]
    else:
        area = next((a for a in cands if a == code[:2]), cands[0])
    out = dict(bis[area][1])
    if code in STALE_FROM:
        out = dict((m, v) for m, v in out.items() if m < STALE_FROM[code])
    return out


def interior(points, months):
    """Log-linear values for the months that lie between two points {month: value}; {month: value}."""
    ks = sorted(points)
    out = {}
    for m in months:
        if m in points or not ks or m < ks[0] or m > ks[-1]:
            continue
        a = max(k for k in ks if k < m)
        b = min(k for k in ks if k > m)
        out[m] = points[a] * math.exp(math.log(points[b] / points[a]) * (m - a) / (b - a))
    return out


def build_moneys(info, bis, wdi_fx):
    """info from read_moneys; bis from src.bis_monthly; wdi_fx from src.wdi('PA.NUS.FCRF').
    Returns (codes, rates, marks): codes in the layer's order (USD, then by code), rates {code: [312 floats, NaN where
    no nation used the money]}, marks {code: [312 ints]}."""
    users = {}
    for n, r in info.items():
        for c, _ in r['history']:
            users.setdefault(c, []).append(n)
    codes = ['USD'] + sorted(c for c in users if c != 'USD')
    for c in codes:
        assert c in MONEY_NAMES, c
    active = dict((c, set()) for c in codes)
    for n, r in info.items():
        for m in range(common.MONTHS):
            active[money_in_use(r['history'], m)].add(m)
    rates, marks = {}, {}
    for c in codes:
        act = sorted(active[c])
        got = {}                                  # month -> (value, mark)
        series = bis_values(c, bis)
        points = dict((m, v) for m, v in series.items() if m in active[c])
        for m, v in points.items():
            got[m] = (v, 3)
        anch = [m for m in ANCHORS.get(c, {}) if m in active[c]]
        for m, v in ANCHORS.get(c, {}).items():
            points.setdefault(m, v)
            if m in active[c]:
                got[m] = (v, 1)
        _record(c, anch, 'anchor (see the dials)')
        # 1. a gap inside the series: log-linear
        gap = dict((m, v) for m, v in interior(points, act).items() if m not in got)
        for m, v in gap.items():
            got[m] = (v, 1)
        _record(c, gap, 'log-linear gap inside the BIS series')
        # 2. no BIS figure: WDI's official rate of that year, every month of it
        wdi = {}
        if c not in WDI_SKIP:
            for m in act:
                if m in got:
                    continue
                y = common.FIRST_YEAR + m // 12
                if c in WDI_ONLY_YEARS and not WDI_ONLY_YEARS[c][0] <= y <= WDI_ONLY_YEARS[c][1]:
                    continue
                for n in users[c]:
                    if money_in_use(info[n]['history'], m) == c and y in wdi_fx.get(n, {}):
                        wdi[m] = wdi_fx[n][y]
                        break
        for m, v in wdi.items():
            got[m] = (v, 1)
        _record(c, wdi, "WDI PA.NUS.FCRF of the year")
        pts2 = dict(points)
        pts2.update((m, v) for m, (v, k) in got.items() if k == 1 and m in wdi)
        gap2 = dict((m, v) for m, v in interior(pts2, act).items() if m not in got)
        for m, v in gap2.items():
            got[m] = (v, 1)
        _record(c, gap2, 'log-linear between anchors and WDI years')
        # 3. a peg: the anchor's month times the parity
        if c in PARITY:
            anchor, par = PARITY[c]
            peg = [m for m in act if m not in got]
            for m in peg:
                got[m] = (rates[anchor][m] * par, 1)
            _record(c, peg, 'peg to %s at %s' % (anchor, par))
        # 4. constants named in this module
        const = {}
        for m in act:
            if m in got:
                continue
            if c == 'ECS':
                const[m] = ECS_PER_USD
            elif c == 'CUP':
                const[m] = CUP_PER_USD
            elif c == 'KPW':
                const[m] = KPW_PER_USD
        for m, v in const.items():
            got[m] = (v, 1)
        _record(c, const, 'constant (see the dials)')
        # 5. the nearest month that has a figure
        known = sorted(got)
        carry = [m for m in act if m not in got]
        for m in carry:
            near = min(known, key=lambda k: (abs(k - m), k))
            got[m] = (got[near][0], 1)
        _record(c, carry, 'nearest month carried')
        rates[c] = [got[m][0] if m in got else float('nan') for m in range(common.MONTHS)]
        marks[c] = [got[m][1] if m in got else 0 for m in range(common.MONTHS)]
        check_units(c, act, got, points, wdi)
    return codes, rates, marks, active, users


def check_units(code, act, got, points, wdi):
    """Prints a warning for a WDI-filled month whose value is more than 5x away from the nearest BIS month's: a unit
    that changed."""
    if not points or not wdi:
        return
    ks = sorted(points)
    bad = []
    for m in sorted(wdi):
        near = min(ks, key=lambda k: abs(k - m))
        if abs(near - m) <= 60:
            r = wdi[m] / points[near]
            if r > 5 or r < 0.2:
                bad.append(m)
    if bad and code != 'SYP':
        print('  WARNING: %s: WDI months far from BIS (units?): %s' % (code, _span(bad)))


def _span(ms):
    return month_name(min(ms)) + '..' + month_name(max(ms))


def check_nations(info, codes, rates):
    """Every nation, every month: the money its history names has a finite rate above zero."""
    for n, r in info.items():
        for m in range(common.MONTHS):
            c = money_in_use(r['history'], m)
            v = rates[c][m]
            assert math.isfinite(v) and v > 0, (n, c, month_name(m), v)


def write_moneys_bin(path, codes, rates, marks):
    """The layer's payload (FORMAT.txt, moneys): int M, int first month, int months; code and name a money; the rates;
    the marks. Big-endian, strings as DataOutputStream.writeUTF."""
    def utf(s):
        b = s.encode('utf-8')
        return struct.pack('>H', len(b)) + b
    out = bytearray(struct.pack('>iii', len(codes), 0, common.MONTHS))
    for c in codes:
        out += utf(c) + utf(MONEY_NAMES[c])
    for c in codes:
        out += struct.pack('>%dd' % common.MONTHS, *rates[c])
    for c in codes:
        out += bytes(marks[c])
    with open(path, 'wb') as f:
        f.write(bytes(out))
    return len(out)
