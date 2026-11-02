"""The price index of the nations' paths, 2000-2025, 2015 = 100 (batch WN0, slice E1, 2026-10-28; LC3, 2026-11-02):
the World Bank's GDP deflator, NY.GDP.DEFL.ZS, rebased where the Bank has it (mark 3), and the game's estimate for the
years and the nations it has not (mark 1). The series keeps the name cpi (FORMAT.txt): it is the nation's price level.

WHY: WN0 took WDI's consumer price index, FP.CPI.TOTL, which is the IMF's figure (International Financial Statistics),
and the IMF asks permission for any commercial reuse, so a sold game may not ship it (LC1, docs/studies/licences.md).
ILOSTAT's consumer prices were the brief's first choice, but every headline figure of theirs since 2000 is marked
"Repository: International Monetary Fund", so they are the IMF's too; the deflator is the Bank's own national accounts
(CC BY 4.0). It has no figure for North Korea and Taiwan, and gaps of years for a dozen more. A hole is filled by the
growth of the nation's GDP deflator in its own money (current dollar GDP times the year's rate over constant dollar
GDP), which sees a hyperinflation the way the exchange rate does; a micro-state that uses another's money takes that
nation's index. Penn World Table's price level pl_c is not used for Taiwan: PWT estimates it from income across nations
(Taiwan is not in the ICP), so it rises 25% against a flat exchange rate where Taiwan's prices rose less than America's.
A GDP deflator carries the prices of what a nation sells abroad, so an oil exporter's swings with oil (Kuwait's, Brunei's)
where its consumer prices did not.
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import econ_paths_gdp as gdp

# The nations that use another nation's money, and its issuer: their index is the issuer's in every year (a micro-
# state's GDP deflator is the price of its exports, not its consumers': Nauru's phosphate and Timor-Leste's oil move
# theirs 13% a year against the US's 1%) (est.).
ISSUER = {'AND': 'ESP', 'MCO': 'FRA', 'SMR': 'ITA', 'LIE': 'CHE', 'MHL': 'USA', 'PLW': 'USA', 'FSM': 'USA',
          'TLS': 'USA', 'KIR': 'AUS', 'NRU': 'AUS', 'TUV': 'AUS'}

# The nations whose index, where the deflator has a gap, grows with a neighbour's: Somalia and Turkmenistan, whose rate
# or whose real growth is no measure of prices, take the neighbour whose economy they share (est.).
NEIGHBOUR = {'SOM': 'KEN', 'TKM': 'KAZ'}

# Both: the nations whose index follows another nation's.
SPLICE_SOURCE = dict(ISSUER, **NEIGHBOUR)

# The years of a nation's published deflator that are not prices in the money the file gives it, left to the deflator
# in its money: the Bank restates Zimbabwe's accounts before 2009 in today's money at market rates, so its deflator
# misses the Zimbabwe dollar's hyperinflation; Liberia's accounts are in US dollars, not in its dollar; Cuba's peso is
# held at 1 to the US$ (econ_paths_moneys.CUP_PER_USD) while its deflator rose ninefold after the unification of 2021.
NOT_OWN_MONEY = {'ZWE': (2000, 2008), 'LBR': (2000, 2025), 'CUB': (2021, 2025)}

YEARS = gdp.YEARS


def deflator(kd, cd, rate):
    """{year: price level} in the nation's money: current dollar GDP times the rate over constant dollar GDP."""
    return dict((y, cd[y] * rate[y - common.FIRST_YEAR] / kd[y]) for y in YEARS
                if y in cd and y in kd and kd[y] > 0 and cd[y] > 0 and rate[y - common.FIRST_YEAR] > 0)


def _splice(iso3, pub, growth):
    """The raw index ({year: value}, {year: mark}) over YEARS from pub (published) and growth ({year: level}, where
    known). Inside the published range log-linear; outside it anchored to the nearest published year and moved by the
    growth, else by the mean of the series' last (first) five steps."""
    src = 'index of ' + SPLICE_SOURCE[iso3] if iso3 in SPLICE_SOURCE else 'GDP deflator in its money'
    vals = dict(pub)
    marks = dict((y, 3) for y in vals)
    if not vals:
        base = growth[2015]
        for y in YEARS:
            vals[y] = growth[y] / base * 100.0
            marks[y] = 1
        gdp.note(iso3, 'cpi', YEARS, src)
        return vals, marks
    how = {}
    ys = sorted(vals)
    for a, b in zip(ys, ys[1:]):
        for y in range(a + 1, b):
            vals[y] = vals[a] * math.exp(math.log(vals[b] / vals[a]) * (y - a) / (b - a))
            marks[y] = 1
            how.setdefault('log-linear', []).append(y)
    last, first = ys[-1], ys[0]
    for y in range(last + 1, YEARS[-1] + 1):
        if y in growth and last in growth:
            vals[y] = vals[last] * growth[y] / growth[last]
            how.setdefault('growth of ' + src, []).append(y)
        else:
            tail = [x for x in range(y - gdp.MEAN_GROWTH_STEPS - 1, y) if x in vals]
            vals[y] = vals[y - 1] * math.exp(gdp.mean_step(vals, tail))
            how.setdefault('mean growth', []).append(y)
        marks[y] = 1
    for y in range(first - 1, YEARS[0] - 1, -1):
        if y in growth and first in growth:
            vals[y] = vals[first] * growth[y] / growth[first]
            how.setdefault('growth of ' + src, []).append(y)
        else:
            head = [x for x in range(y + 1, y + gdp.MEAN_GROWTH_STEPS + 2) if x in vals]
            vals[y] = vals[y + 1] / math.exp(gdp.mean_step(vals, head))
            how.setdefault('mean growth', []).append(y)
        marks[y] = 1
    for h, yy in how.items():
        gdp.note(iso3, 'cpi', yy, h)
    return vals, marks


def published(n, deflators):
    """The nation's published deflator {year: value} that the index is built on: none for a nation on its issuer's
    index, and none in its NOT_OWN_MONEY years."""
    if n in ISSUER:
        return {}
    a, b = NOT_OWN_MONEY.get(n, (0, -1))
    return dict((y, v) for y, v in deflators.get(n, {}).items() if y in YEARS and v > 0 and not a <= y <= b)


def build_cpi(deflators, series, cont_rate):
    """{iso3: ([26 floats], [26 marks])}, 2015 = 100. deflators is src.wdi('NY.GDP.DEFL.ZS'), series build_gdp's
    output, cont_rate rates.continuous_rates'."""
    for n, (a, b) in NOT_OWN_MONEY.items():   # an excluded run inside the published years would be bridged log-linear
        assert a == YEARS[0] or b == YEARS[-1], ('NOT_OWN_MONEY must reach an end of the years', n)
    raw = {}
    plain = [n for n in common.NATIONS if n not in SPLICE_SOURCE]
    dependent = [n for n in common.NATIONS if n in SPLICE_SOURCE]
    for n in plain + dependent:
        if n in SPLICE_SOURCE:
            growth = dict(raw[SPLICE_SOURCE[n]][0])
        else:
            growth = deflator(series['kd'][n][0], series['cd'][n][0], cont_rate[n])
        raw[n] = _splice(n, published(n, deflators), growth)
    out = {}
    for n in common.NATIONS:
        vals, marks = raw[n]
        base = vals[2015]
        out[n] = ([vals[y] / base * 100.0 for y in YEARS], [marks[y] for y in YEARS])
    return out


def own_money_gaps(deflators, series, cont_rate):
    """[(range, iso3)], widest first: how far each nation's published deflator (the years used) strays from its
    deflator in its own money, the range over the years of log10 of the one over the other. Wide where the Bank
    converts at a rate the file does not give the money (an official rate against a market one), or where the
    published figure is not in the nation's money (NOT_OWN_MONEY): the bake prints the widest to be read."""
    out = []
    for n in common.NATIONS:
        pub = published(n, deflators)
        own = deflator(series['kd'][n][0], series['cd'][n][0], cont_rate[n])
        r = [math.log10(pub[y] / own[y]) for y in YEARS if y in pub and y in own]
        if len(r) > 1:
            out.append((max(r) - min(r), n))
    return sorted(out, reverse=True)
