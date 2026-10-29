"""The three GDP series of the nations' paths, 2000-2025, filled (batch WN0, slice E1, 2026-10-28): gdp_2015usd, gdp_usd
and gdp_ppp a year for each of the 196, WDI's figures where WDI has them (mark 3) and the game's estimate where it has
not (mark 1).

WHY: WDI has nothing at all for North Korea and Taiwan, nothing recent for Cuba, Eritrea, South Sudan, Syria and Yemen,
and no PPP series for seven more; the leaderboard needs a figure for every nation and year (FORMAT.txt, paths), and a
hole filled one way here and another way in a later stage would make the base year's weights disagree with the series.
Every fill is recorded (FILLS) so the report can list them by nation and series.
"""
import math
import os
import statistics
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common

# The years of the series that are published (WDI's range): 2000-2025.
YEARS = list(range(common.FIRST_YEAR, common.LAST_DATA_YEAR + 1))

# How many of a series' latest (or earliest) yearly steps its mean growth is taken over, where no other source grows it.
MEAN_GROWTH_STEPS = 5

# The window of Taiwan's trend, the years PWT does not reach (2024, 2025) grow by: first and last year.
TAIWAN_TREND_FIRST = 2019
TAIWAN_TREND_LAST = 2023

# The base year of WDI's constant US dollars and of the CPI's index (WDI's NY.GDP.MKTP.KD is of 2015).
PRICE_BASE_YEAR = 2015

# North Korea has no figure in WDI or PWT: its level is Maddison's GDP times the median ratio of WDI's to Maddison's in
# these four nations of the same income, and its dollar prices follow their median (est.).
PRK_COMPARATORS = ('KHM', 'LAO', 'MMR', 'NPL')

# Where WDI has no PPP series and its dollar GDP is at a rate that is no measure of what the money buys (Cuba's peso at
# par, Venezuela's bolivar), PPP GDP is Maddison's times the median ratio of WDI's PPP GDP to Maddison's in these peers (est.).
PPP_FROM_MADDISON = {'CUB': ('DOM', 'JAM', 'HTI'), 'VEN': ('COL', 'ECU', 'PER')}

# Where WDI has no PPP series and nothing else does, the ratio of 2015 US dollars to PPP dollars is the median of these
# nations' (est.).
PPP_COMPARATORS = {'ERI': ('ETH', 'SDN', 'DJI'), 'LIE': ('CHE',), 'MCO': ('FRA',)}

# South Sudan before PWT's 2008 grows with the Sudan it was part of.
SSD_PEER = 'SDN'
SSD_PEER_LAST_YEAR = 2008

# Liechtenstein's dollar prices follow Switzerland's (it uses the franc).
LIE_PRICE_PEER = 'CHE'

# Nations whose PPP series WDI lacks but PWT has (and has sensibly): PPP GDP is PWT's rgdpna. PWT's Venezuela is not
# sensible (its 2021 PPP GDP is 4.7 billion: a price level of 24 times America's).
PWT_PPP_NATIONS = ('SSD', 'YEM')

# The paths' names of the three series.
SERIES_NAME = {'kd': 'gdp_2015usd', 'cd': 'gdp_usd', 'pp': 'gdp_ppp'}

# Every fill, for the report: (nation, series, years, how).
FILLS = []


def _ranges(years):
    """[2012, 2013, 2014, 2020] as '2012-2014,2020'."""
    ys = sorted(years)
    out = []
    s = p = ys[0]
    for y in ys[1:]:
        if y != p + 1:
            out.append((s, p))
            s = y
        p = y
    out.append((s, p))
    return ','.join('%d-%d' % o if o[0] != o[1] else str(o[0]) for o in out)


def note(iso3, series, years, how):
    """Records that these years of this nation's series are estimates, and how."""
    if years:
        FILLS.append((iso3, series, _ranges(years), how))


def mean_step(vals, years):
    """The mean log step over consecutive pairs of these years that vals has; 0.0 when it has none."""
    steps = [math.log(vals[b] / vals[a]) for a, b in zip(years, years[1:]) if a in vals and b in vals]
    return sum(steps) / len(steps) if steps else 0.0


def fill_series(iso3, series, pub, proxy, record=True):
    """pub {year: value} -> ({year: value}, {year: mark}) over YEARS, or None when pub is empty. A year inside the
    published range is log-linear; a year after it grows from the year before by proxy(a, b) (the level ratio of year b
    over year a, or None), else by the mean of the series' last MEAN_GROWTH_STEPS steps; a year before it likewise
    backwards, by the first steps."""
    if not pub:
        return None
    vals = dict((y, pub[y]) for y in YEARS if y in pub)
    marks = dict((y, 3) for y in vals)
    ys = sorted(vals)
    inner = []
    for a, b in zip(ys, ys[1:]):
        for y in range(a + 1, b):
            vals[y] = vals[a] * math.exp(math.log(vals[b] / vals[a]) * (y - a) / (b - a))
            marks[y] = 1
            inner.append(y)
    if record:
        note(iso3, series, inner, 'log-linear')
    how = {}
    for y in range(ys[-1] + 1, YEARS[-1] + 1):
        r = proxy(y - 1, y) if proxy else None
        if r is not None:
            how.setdefault(r[1], []).append(y)
            vals[y] = vals[y - 1] * r[0]
        else:
            tail = [x for x in range(y - MEAN_GROWTH_STEPS - 1, y) if x in vals]
            vals[y] = vals[y - 1] * math.exp(mean_step(vals, tail))
            how.setdefault('mean growth', []).append(y)
        marks[y] = 1
    for y in range(ys[0] - 1, YEARS[0] - 1, -1):
        r = proxy(y, y + 1) if proxy else None
        if r is not None:
            how.setdefault(r[1], []).append(y)
            vals[y] = vals[y + 1] / r[0]
        else:
            head = [x for x in range(y + 1, y + MEAN_GROWTH_STEPS + 2) if x in vals]
            vals[y] = vals[y + 1] / math.exp(mean_step(vals, head))
            how.setdefault('mean growth', []).append(y)
        marks[y] = 1
    if record:
        for h, yy in how.items():
            note(iso3, series, yy, h)
    return vals, marks


def chain(*proxies):
    """The first proxy that answers: each is (label, f(a, b) -> ratio or None); returns f(a, b) -> (ratio, label)."""
    def f(a, b):
        for label, p in proxies:
            r = p(a, b)
            if r is not None and r > 0:
                return (r, label)
        return None
    return f


def ratio_of(table):
    """f(a, b): the level of year b over the level of year a in {year: level}, or None where either is missing."""
    def f(a, b):
        if a in table and b in table and table[a] > 0 and table[b] > 0:
            return table[b] / table[a]
        return None
    return f


def build_gdp(wdi_kd, wdi_cd, wdi_pp, pwt, mad):
    """The three series: {'kd': {iso3: (vals, marks)}, 'cd': ..., 'pp': ...} with vals {year: float} over YEARS."""
    out = {'kd': {}, 'cd': {}, 'pp': {}}
    pwt_real = {}
    pwt_nom = {}
    for n, ys in pwt.items():
        pwt_real[n] = dict((y, d['rgdpna'] * 1e6) for y, d in ys.items() if 'rgdpna' in d)
        pwt_nom[n] = dict((y, d['cgdpo'] * d['pl_gdpo'] * 1e6) for y, d in ys.items() if 'cgdpo' in d and 'pl_gdpo' in d)

    def peer(n, key):
        """South Sudan's last resort before 2008: Sudan's own series, as far as 2008."""
        if n != 'SSD' or SSD_PEER not in out[key]:
            return []
        t = dict((y, v) for y, v in out[key][SSD_PEER][0].items() if y <= SSD_PEER_LAST_YEAR)
        return [('Sudan growth', ratio_of(t))]

    def real_proxy(n, key):
        return chain(('PWT growth', ratio_of(pwt_real.get(n, {}))), *(peer(n, key) + [('Maddison growth', ratio_of(mad.get(n, {})))]))

    def nom_proxy(n, key):
        return chain(('PWT growth', ratio_of(pwt_nom.get(n, {}))), *(peer(n, key) + [('Maddison growth', ratio_of(mad.get(n, {})))]))

    # the nations that WDI covers, as far as it does
    for n in common.NATIONS:
        for key, src, prox in (('kd', wdi_kd, real_proxy), ('cd', wdi_cd, nom_proxy), ('pp', wdi_pp, real_proxy)):
            if (n, key) == ('LIE', 'kd'):
                continue    # below: Liechtenstein's one published year is no series
            res = fill_series(n, SERIES_NAME[key], src.get(n, {}), prox(n, key))
            if res is not None:
                out[key][n] = res

    # Taiwan: PWT's growth of rgdpna on a 2015 level of cgdpo x pl_gdpo (GDP at market rates), PWT's own rgdpna as PPP GDP,
    # and the 2019-2023 trend for the years PWT does not reach
    tw_r = pwt_real['TWN']
    tw_n = pwt_nom['TWN']
    level15 = tw_n[PRICE_BASE_YEAR]
    tw = {'kd': dict((y, level15 * tw_r[y] / tw_r[PRICE_BASE_YEAR]) for y in YEARS if y in tw_r),
          'cd': dict((y, tw_n[y]) for y in YEARS if y in tw_n),
          'pp': dict((y, tw_r[y]) for y in YEARS if y in tw_r)}
    for key, d in tw.items():
        last = max(d)
        trend = math.exp(mean_step(d, list(range(TAIWAN_TREND_FIRST, TAIWAN_TREND_LAST + 1))))
        for y in range(last + 1, YEARS[-1] + 1):
            d[y] = d[y - 1] * trend
        out[key]['TWN'] = (d, dict((y, 1) for y in d))
        note('TWN', SERIES_NAME[key], YEARS, 'PWT (rgdpna growth on cgdpo x pl_gdpo of 2015; 2024-25 the %d-%d trend)'
             % (TAIWAN_TREND_FIRST, TAIWAN_TREND_LAST))

    # North Korea: Maddison's level and growth, the comparators' price ratios
    prk_m = mad['PRK']
    comp = PRK_COMPARATORS
    k = statistics.median(out['kd'][c][0][PRICE_BASE_YEAR] / mad[c][PRICE_BASE_YEAR] for c in comp)
    kd_prk = dict((y, k * prk_m[y]) for y in YEARS if y in prk_m)
    last = max(kd_prk)
    for y in range(last + 1, YEARS[-1] + 1):
        tail = [x for x in range(y - MEAN_GROWTH_STEPS - 1, y) if x in kd_prk]
        kd_prk[y] = kd_prk[y - 1] * math.exp(mean_step(kd_prk, tail))
    cd_prk = dict((y, kd_prk[y] * statistics.median(out['cd'][c][0][y] / out['kd'][c][0][y] for c in comp)) for y in YEARS)
    tau = statistics.median(out['kd'][c][0][PRICE_BASE_YEAR] / out['pp'][c][0][PRICE_BASE_YEAR] for c in comp)
    pp_prk = dict((y, kd_prk[y] / tau) for y in YEARS)
    for key, d in (('kd', kd_prk), ('cd', cd_prk), ('pp', pp_prk)):
        out[key]['PRK'] = (d, dict((y, 1) for y in d))
        note('PRK', SERIES_NAME[key], YEARS, 'Maddison level and growth, 2023-25 mean growth, price ratios of %s'
             % '+'.join(comp))

    # Liechtenstein's real series: its dollar GDP deflated by Switzerland's dollar deflator
    cd_l = out['cd']['LIE'][0]
    kd_l = dict((y, cd_l[y] * out['kd'][LIE_PRICE_PEER][0][y] / out['cd'][LIE_PRICE_PEER][0][y]) for y in YEARS)
    km = dict((y, 3 if y in wdi_kd.get('LIE', {}) else 1) for y in YEARS)
    kd_l.update((y, v) for y, v in wdi_kd.get('LIE', {}).items() if y in kd_l)
    out['kd']['LIE'] = (kd_l, km)
    note('LIE', SERIES_NAME['kd'], [y for y in YEARS if km[y] == 1], 'gdp_usd over Switzerland\'s dollar deflator')

    # PPP GDP where WDI has none
    for n in PWT_PPP_NATIONS:
        base = dict((y, v) for y, v in pwt_real[n].items() if y in YEARS)
        kd_n = out['kd'][n][0]
        vals, _ = fill_series(n, SERIES_NAME['pp'], base, chain(('gdp_2015usd growth', ratio_of(kd_n))), record=False)
        out['pp'][n] = (vals, dict((y, 1) for y in vals))
        note(n, SERIES_NAME['pp'], YEARS, 'PWT rgdpna (beyond PWT, gdp_2015usd\'s growth)')
    for n, cs in PPP_FROM_MADDISON.items():
        f = statistics.median(out['pp'][c][0][PRICE_BASE_YEAR] / mad[c][PRICE_BASE_YEAR] for c in cs)
        d = dict((y, mad[n][y] * f) for y in YEARS if y in mad[n])
        kd_n = out['kd'][n][0]
        for y in YEARS:
            if y not in d:
                d[y] = d[y - 1] * kd_n[y] / kd_n[y - 1]
        out['pp'][n] = (d, dict((y, 1) for y in d))
        note(n, SERIES_NAME['pp'], YEARS, "Maddison times its peers' median PPP ratio (%s); beyond Maddison gdp_2015usd's growth"
             % '+'.join(cs))
    for n, cs in PPP_COMPARATORS.items():
        tau = statistics.median(out['kd'][c][0][PRICE_BASE_YEAR] / out['pp'][c][0][PRICE_BASE_YEAR] for c in cs)
        d = dict((y, out['kd'][n][0][y] / tau) for y in YEARS)
        out['pp'][n] = (d, dict((y, 1) for y in d))
        note(n, SERIES_NAME['pp'], YEARS, 'gdp_2015usd over the median 2015-USD/PPP ratio of %s' % '+'.join(cs))
    return out
