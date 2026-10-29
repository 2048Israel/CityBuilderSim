"""The yearly exchange rates of the nations' paths (batch WN0, slice E1, 2026-10-28): the paths' `rate` series, and the
same rate carried through each change of money so that a price level built on it does not jump where the money
changed.

WHY: the paths' rate (FORMAT.txt) is the year's mean of the nation's money, which is a different unit before and after
a euro adoption or a dollarisation; a GDP deflator in the nation's money (the CPI's gap filler) needs one unit through
the whole period, or Ecuador's and Zimbabwe's price levels would fall off a cliff in the year the money changed.
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import econ_paths_moneys as mon

# El Salvador's colones a dollar, fixed by the Monetary Integration Law of 2001.
SVC_PER_USD = 8.75

# The legal conversions at a changeover, units of the new money for one of the old: the euro's, the sucre's and the
# colon's into the dollar, the Zimbabwe dollar's into the Gold. Other changeovers convert at the rates of the months.
FIXED_CONVERSION = dict((c, 1.0 / r) for c, r in mon.EURO_CONVERSION.items())
FIXED_CONVERSION.update({'ECS': 1.0 / mon.ECS_PER_USD, 'SVC': 1.0 / SVC_PER_USD, 'ZWL': 1.0 / mon.ZWL_PER_ZWG})

NYEARS = common.LAST_DATA_YEAR - common.FIRST_YEAR + 1


def annual_rates(info, rates, marks):
    """The paths' rate series: {iso3: ([26 floats], [26 marks])}, the mean over a year's months of the rate of the money
    the nation uses in December, over the months it was the nation's money (all twelve unless the money changed in the
    year). The mark is 3 when every month is a BIS figure, else 1."""
    out = {}
    for n, r in info.items():
        vals, mk = [], []
        for y in range(NYEARS):
            dec = mon.money_in_use(r['history'], y * 12 + 11)
            use = [m for m in range(y * 12, y * 12 + 12) if mon.money_in_use(r['history'], m) == dec]
            vals.append(sum(rates[dec][m] for m in use) / len(use))
            mk.append(3 if len(use) == 12 and all(marks[dec][m] == 3 for m in use) else 1)
        out[n] = (vals, mk)
    return out


def continuous_rates(info, rates):
    """{iso3: [26 floats]}: the nation's yearly rate in the units of its money of December 2025, the old moneys
    carried through each changeover (FIXED_CONVERSION or the rates of the months either side)."""
    out = {}
    for n, r in info.items():
        hist = r['history']
        factor = [1.0] * common.MONTHS          # units of the final money for one of the money in use that month
        for j in range(len(hist) - 1, 0, -1):
            new, s = hist[j]
            old = hist[j - 1][0]
            g = FIXED_CONVERSION.get(old)
            if g is None:
                g = rates[new][s] / rates[old][s - 1]
            for m in range(s):
                factor[m] *= g
        monthly = [rates[mon.money_in_use(hist, m)][m] * factor[m] for m in range(common.MONTHS)]
        out[n] = [sum(monthly[y * 12:y * 12 + 12]) / 12.0 for y in range(NYEARS)]
    return out
