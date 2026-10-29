"""The consumer price index of the nations' paths, 2000-2025, 2015 = 100 (batch WN0, slice E1, 2026-10-28): WDI's
FP.CPI.TOTL rebased where WDI has it (mark 3), and the game's estimate for the years and the nations it has not (mark 1).

WHY: WDI has no index at all for Argentina, Turkmenistan, Somalia, Cuba, Eritrea, Taiwan, North Korea, Andorra, Monaco,
Liechtenstein and the Marshall Islands, and gaps of years for forty more (Venezuela's and Zimbabwe's are the hyper-
inflations). A hole is filled by the growth of the nation's GDP deflator in its own money (current dollar GDP times the
year's rate over constant dollar GDP), which sees a hyperinflation the way the exchange rate does; a micro-state that
uses another's money takes that nation's index. Penn World Table's price level pl_c is not used for Taiwan: PWT
estimates it from income across nations (Taiwan is not in the ICP), so it rises 25% against a flat exchange rate where
Taiwan's prices rose less than America's.
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import econ_paths_gdp as gdp

# The nations whose index, where WDI's has a gap or is empty, grows with another nation's: the issuer of the money they
# use (a micro-state's own GDP deflator is too noisy to be a price level), and for Somalia and Turkmenistan, whose rate
# or whose real growth is no measure of prices, the neighbour whose economy they share (est.).
SPLICE_SOURCE = {'AND': 'ESP', 'MCO': 'FRA', 'SMR': 'ITA', 'LIE': 'CHE', 'MHL': 'USA', 'PLW': 'USA', 'FSM': 'USA',
                 'TLS': 'USA', 'KIR': 'AUS', 'NRU': 'AUS', 'TUV': 'AUS', 'SOM': 'KEN', 'TKM': 'KAZ'}

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


def build_cpi(wdi_cpi, series, cont_rate):
    """{iso3: ([26 floats], [26 marks])}, 2015 = 100. series is build_gdp's output, cont_rate rates.continuous_rates'."""
    raw = {}
    plain = [n for n in common.NATIONS if n not in SPLICE_SOURCE]
    dependent = [n for n in common.NATIONS if n in SPLICE_SOURCE]
    for n in plain + dependent:
        pub = dict((y, v) for y, v in wdi_cpi.get(n, {}).items() if y in YEARS and v > 0)
        if n in SPLICE_SOURCE:
            growth = dict(raw[SPLICE_SOURCE[n]][0])
        else:
            growth = deflator(series['kd'][n][0], series['cd'][n][0], cont_rate[n])
        raw[n] = _splice(n, pub, growth)
    out = {}
    for n in common.NATIONS:
        vals, marks = raw[n]
        base = vals[2015]
        out[n] = ([vals[y] / base * 100.0 for y in YEARS], [marks[y] for y in YEARS])
    return out
