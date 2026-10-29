"""The nations' moneys, monthly exchange rates and 2000-2100 paths (batch WN0, slice E1, 2026-10-28): reads the BIS, WDI,
WPP, PWT and Maddison downloads and moneys.tsv and writes WORK/econ/gdp-base.tsv, moneys.bin, paths.bin,
nations-money.tsv, meta-paths.tsv, prices.bin and fills-paths.txt for bake.py to put into world-1.bin (FORMAT.txt, layers moneys,
paths and prices).

WHY: the leaderboard and the trade's weights need every one of the 196 nations to have a GDP, a population, a price
level and an exchange rate in every year of 2000-2025, and WDI alone has holes for North Korea, Taiwan, Syria, Yemen and
two dozen more; the holes are filled here, one way each, and every fill is written to fills-paths.txt so it can be
reviewed. gdp-base.tsv is written as soon as the GDP series are filled, because econ_trade.py reads it.

Run as:  python -I tools/world/econ_paths.py [--sources DIR] [--work DIR] [--gdp-only]    (about 10 seconds)
"""
import argparse
import math
import os
import struct
import sys
import time

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import econ_paths_cpi as cpi
import econ_paths_gdp as gdp
import econ_paths_moneys as mon
import econ_paths_prices as prc
import econ_paths_rates as rat
import econ_paths_sources as src

HERE = os.path.dirname(os.path.abspath(__file__))

# The paths' series, in FORMAT.txt's order: name, unit.
SERIES = [('people', 'persons'), ('gdp_2015usd', 'US$ of 2015'), ('gdp_usd', 'current US$'),
          ('gdp_ppp', 'international $ of 2021'), ('cpi', '2015 = 100'), ('rate', 'its money a US$, year average')]

# Every constant this stage used, for the file's meta: name, value, one sentence.
DIALS = [
    ('MEAN_GROWTH_STEPS', gdp.MEAN_GROWTH_STEPS, "How many of a series' latest (or earliest) yearly steps its mean growth is taken over, where no other source grows it."),
    ('TAIWAN_TREND_FIRST', gdp.TAIWAN_TREND_FIRST, "The first year of Taiwan's trend, which the years PWT does not reach (2024, 2025) grow by."),
    ('TAIWAN_TREND_LAST', gdp.TAIWAN_TREND_LAST, "The last year of Taiwan's trend, PWT's last year."),
    ('PRICE_BASE_YEAR', gdp.PRICE_BASE_YEAR, "The base year of WDI's constant US dollars and of the CPI's index."),
    ('PRK_COMPARATORS', '+'.join(gdp.PRK_COMPARATORS), "North Korea's level is Maddison's times the median WDI-to-Maddison ratio of these nations, and its dollar prices follow their median (est.)."),
    ('PPP_FROM_MADDISON', ' '.join('%s:%s' % (k, '+'.join(v)) for k, v in gdp.PPP_FROM_MADDISON.items()), "Where WDI has no PPP GDP and its dollar GDP is at a rate that measures nothing, PPP GDP is Maddison's times the median PPP-to-Maddison ratio of these peers (est.)."),
    ('PPP_COMPARATORS', ' '.join('%s:%s' % (k, '+'.join(v)) for k, v in gdp.PPP_COMPARATORS.items()), "Where WDI and PWT have no PPP GDP, the ratio of 2015 US dollars to PPP dollars is the median of these nations' (est.)."),
    ('LIE_PRICE_PEER', gdp.LIE_PRICE_PEER, "Liechtenstein's real GDP is its dollar GDP deflated by this nation's dollar deflator (it uses the franc)."),
    ('SSD_PEER', gdp.SSD_PEER, "South Sudan grows with this nation (the Sudan it was part of) before PWT's 2008."),
    ('PWT_PPP_NATIONS', '+'.join(gdp.PWT_PPP_NATIONS), "Nations WDI lacks a PPP GDP for and PWT has sensibly: PPP GDP is PWT's rgdpna."),
    ('CPI_SPLICE_SOURCE', ' '.join('%s:%s' % kv for kv in sorted(cpi.SPLICE_SOURCE.items())), "The nations whose price index, where WDI's has a gap, grows with the index of the issuer of the money they use (Somalia and Turkmenistan: a neighbour)."),
    ('PRICE_RATIO_YEAR', prc.RATIO_YEAR, "Each good's real price path is a ratio to this year's, so it is 1 there."),
    ('BGN_PER_EUR', mon.BGN_PER_EUR, "Bulgaria's lev, a currency board at this many to the euro; BIS has Bulgaria in euros."),
    ('ZWG_PER_USD_AT_LAUNCH', mon.ZWG_PER_USD_AT_LAUNCH, 'Zimbabwe Gold a US dollar at its launch, 2024-04 (Reserve Bank of Zimbabwe).'),
    ('ZWL_PER_ZWG', mon.ZWL_PER_ZWG, 'RTGS dollars to one Zimbabwe Gold at the changeover of 2024-04.'),
    ('MWK_PER_USD_PEG', mon.PARITY['MWK'][1], "Malawi's kwacha a dollar since 2023-11; BIS ends in 2024-09."),
    ('SVC_PER_USD', rat.SVC_PER_USD, "El Salvador's colones a dollar, fixed by the Monetary Integration Law of 2001."),
    ('ECS_PER_USD', mon.ECS_PER_USD, "Ecuador's sucre a dollar, fixed for dollarisation in 2000."),
    ('CUP_PER_USD', mon.CUP_PER_USD, "Cuba's peso a dollar, the CUC parity WDI's dollar GDP rests on; the unification of 2021-01 at 24 is not applied."),
    ('KPW_PER_USD', mon.KPW_PER_USD, "North Korea's official won a dollar since the 2009 redenomination (no BIS or WDI figure)."),
    ('MRU_PER_BIS_MRO', mon.UNITS['MRU'][1][0][1], "The ouguiya's new units for one of the old that BIS keeps (the 2018 redenomination)."),
    ('STN_PER_BIS_STD', mon.UNITS['STN'][1][0][1], "The dobra's new units for one of the old that BIS keeps (the 2018 redenomination)."),
    ('VES_PER_BIS_VEF', mon.UNITS['VES'][1][0][1], "Venezuela's 2021 bolivar for one of BIS's VEF units (to 2018-07)."),
    ('VES_PER_BIS_VES2018', mon.UNITS['VES'][1][1][1], "Venezuela's 2021 bolivar for one of BIS's 2018 bolivar soberano units (2018-08 to 2021-09)."),
    ('ZWN_PER_BIS_ZWR', mon.UNITS['ZWN'][1][1][1], "Zimbabwe dollars of the 2006 redenomination for one of BIS's 2008-08 redenominated dollars."),
    ('SYP_BIS_STALE_FROM', mon.month_name(mon.STALE_FROM['SYP']), "BIS's Syrian pound is the pre-war official 11.225 unchanged; its months from here on are not used."),
    ('ZWG_WDI_YEAR', mon.WDI_ONLY_YEARS['ZWG'][0], "The one year of WDI's Zimbabwean rate that is Zimbabwe Gold's alone."),
] + [('EURO_CONVERSION_' + c, v, 'Units of %s for one euro, fixed by Council Regulation.' % c) for c, v in sorted(mon.EURO_CONVERSION.items())]


def write_gdp_base(path, series):
    """gdp-base.tsv: the base year's GDP, current and 2015 US$, and the lower of the two marks, for E2's weights."""
    kd, cd = series['kd'], series['cd']
    y = common.BASE_YEAR
    with open(path, 'w', encoding='utf-8', newline='\n') as f:
        f.write('iso3\tgdp_usd\tgdp_2015usd\tmark\n')
        for n in common.NATIONS:
            mark = min(cd[n][1][y], kd[n][1][y])
            f.write('%s\t%r\t%r\t%d\n' % (n, cd[n][0][y], kd[n][0][y], mark))


def write_paths_bin(path, table, marks):
    """The paths layer's payload (FORMAT.txt, paths): int N, first year, years, S; name and unit a series; the doubles
    for each series (nation in number order, then year); the marks. table[s][i] and marks[s][i] are lists of `years`."""
    nat, years = len(common.NATIONS), common.YEARS
    out = bytearray(struct.pack('>iiii', nat, common.FIRST_YEAR, years, len(SERIES)))
    for name, unit in SERIES:
        for s in (name, unit):
            b = s.encode('utf-8')
            out += struct.pack('>H', len(b)) + b
    for s in range(len(SERIES)):
        for i in range(nat):
            out += struct.pack('>%dd' % years, *table[s][i])
    for s in range(len(SERIES)):
        for i in range(nat):
            out += bytes(marks[s][i])
    with open(path, 'wb') as f:
        f.write(bytes(out))
    return len(out)


def pad(vals, marks, years):
    """26 yearly values and marks -> 101, NaN and 0 after 2025."""
    nan = float('nan')
    return list(vals) + [nan] * (years - len(vals)), list(marks) + [0] * (years - len(marks))


def write_nations_money(path, info):
    with open(path, 'w', encoding='utf-8', newline='\n') as f:
        f.write('iso3\tname\tmoney\tmoney_history\tregime\n')
        for n, r in info.items():
            f.write('\t'.join((n, r['name'], r['money'], r['money_history'], r['regime'])) + '\n')


def fmt(v):
    """A dial's value as text: a whole float without its '.0', else Python's repr (Double.parseDouble reads both)."""
    if isinstance(v, float):
        return str(int(v)) if v == int(v) and abs(v) < 1e15 else repr(v)
    return str(v)


def write_meta(path):
    """meta-paths.tsv: the dial and source rows bake.py puts into the file's meta."""
    with open(path, 'w', encoding='utf-8', newline='\n') as f:
        for name, value, sentence in DIALS:
            f.write('dial\t%s\t%s\t%s\n' % (name, fmt(value), sentence))
        for row in src.SOURCE_ROWS:
            f.write('source\t%s\n' % '\t'.join(row))


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__.split('\n')[0])
    ap.add_argument('--sources', default=src.SOURCES)
    ap.add_argument('--work', default=None)
    ap.add_argument('--gdp-only', action='store_true', help='stop after gdp-base.tsv')
    args = ap.parse_args(argv)
    work = (args.work or args.sources + '/work') + '/econ'
    os.makedirs(work, exist_ok=True)
    t0 = time.time()

    print('reading WDI, PWT and Maddison')
    wdi_kd = src.wdi('NY.GDP.MKTP.KD', args.sources)
    wdi_cd = src.wdi('NY.GDP.MKTP.CD', args.sources)
    wdi_pp = src.wdi('NY.GDP.MKTP.PP.KD', args.sources)
    wdi_cpi = src.wdi('FP.CPI.TOTL', args.sources)
    pwt = src.pwt(args.sources)
    mad = src.maddison(args.sources)
    print('  %.1f s' % (time.time() - t0))

    print('filling the GDP series')
    series = gdp.build_gdp(wdi_kd, wdi_cd, wdi_pp, pwt, mad)
    for key in ('kd', 'cd', 'pp'):
        missing = [n for n in common.NATIONS if n not in series[key]]
        assert not missing, (key, missing)
    write_gdp_base(work + '/gdp-base.tsv', series)
    print('wrote gdp-base.tsv (%d nations, base year %d)' % (len(common.NATIONS), common.BASE_YEAR))
    if args.gdp_only:
        return

    print('building the moneys')
    info = mon.read_moneys(HERE + '/moneys.tsv')
    bis = src.bis_monthly(args.sources)
    wdi_fx = src.wdi('PA.NUS.FCRF', args.sources)
    codes, rates, marks, active, users = mon.build_moneys(info, bis, wdi_fx)
    mon.check_nations(info, codes, rates)
    size = mon.write_moneys_bin(work + '/moneys.bin', codes, rates, marks)
    nm = sum(len(active[c]) for c in codes)
    n3 = sum(1 for c in codes for m in active[c] if marks[c][m] == 3)
    print('wrote moneys.bin: %d moneys, %d bytes; money-months at mark 3: %d of %d (%.1f%%)'
          % (len(codes), size, n3, nm, 100.0 * n3 / nm))

    print('building the paths')
    annual = rat.annual_rates(info, rates, marks)
    cont = rat.continuous_rates(info, rates)
    prices = cpi.build_cpi(wdi_cpi, series, cont)
    people = src.wpp_people(args.sources)
    years = common.YEARS
    table = [[None] * len(common.NATIONS) for _ in SERIES]
    pmarks = [[None] * len(common.NATIONS) for _ in SERIES]
    for i, n in enumerate(common.NATIONS):
        pv = [people[n][y] for y in range(common.FIRST_YEAR, common.LAST_YEAR + 1)]
        pm = [3 if y <= 2023 else 2 for y in range(common.FIRST_YEAR, common.LAST_YEAR + 1)]
        table[0][i], pmarks[0][i] = pv, pm
        for s, key in ((1, 'kd'), (2, 'cd'), (3, 'pp')):
            vals, mk = series[key][n]
            table[s][i], pmarks[s][i] = pad([vals[y] for y in gdp.YEARS], [mk[y] for y in gdp.YEARS], years)
        table[4][i], pmarks[4][i] = pad(*prices[n], years)
        table[5][i], pmarks[5][i] = pad(*annual[n], years)
    # the asserts of the brief
    for i, n in enumerate(common.NATIONS):
        for y in range(years):
            v = table[0][i][y]
            assert math.isfinite(v) and v > 0, ('people', n, common.FIRST_YEAR + y)
        for s in range(1, len(SERIES)):
            for y in range(len(gdp.YEARS)):
                v = table[s][i][y]
                assert math.isfinite(v) and v > 0 and pmarks[s][i][y] > 0, (SERIES[s][0], n, common.FIRST_YEAR + y, v)
    size = write_paths_bin(work + '/paths.bin', table, pmarks)
    print('wrote paths.bin: %d bytes' % size)

    # the prices layer: the Pink Sheet over the US CPI, goods.tsv's goods (E2's) and prices.tsv's mapping
    goods_path = work + '/goods.tsv'
    if os.path.exists(goods_path):
        mapping = prc.read_prices_tsv(HERE + '/prices.tsv', goods_path)
        pink = prc.read_pinksheet(args.sources + '/pinksheet/CMO-Historical-Data-Annual.xlsx')
        names, pvals, pmk = prc.build_prices(mapping, pink, prices['USA'][0])
        size = prc.write_prices_bin(work + '/prices.bin', names, pvals, pmk)
        own = sum(1 for m in mapping if m[2] == 3)
        print('wrote prices.bin: %d goods (%d own series, %d proxies), %d bytes' % (len(mapping), own, len(mapping) - own, size))
    else:
        print('no goods.tsv yet (econ_trade.py): prices.bin not written')

    write_nations_money(work + '/nations-money.tsv', info)
    write_meta(work + '/meta-paths.tsv')
    with open(work + '/fills-paths.txt', 'w', encoding='utf-8', newline='\n') as f:
        f.write('# every estimate (mark 1) of the paths and the moneys, by nation and series, and how it was made\n')
        for n, s, yrs, how in gdp.FILLS:
            f.write('path\t%s\t%s\t%s\t%s\n' % (n, s, yrs, how))
        for c, ms, how in mon.FILLS:
            f.write('money\t%s\t%s\t%s\n' % (c, ms, how))
    print('wrote nations-money.tsv, meta-paths.tsv, fills-paths.txt')

    # the summary
    by = {}
    for n, s, yrs, how in gdp.FILLS:
        by.setdefault(s, set()).add(n)
    for s in ('gdp_2015usd', 'gdp_usd', 'gdp_ppp', 'cpi'):
        print('fills %s: %d nations: %s' % (s, len(by.get(s, ())), ' '.join(sorted(by.get(s, ())))))
    print('fills rate: %d nations with a year that is not all BIS months' % sum(1 for n in common.NATIONS if 1 in annual[n][1]))
    for n in ('CAN', 'USA', 'TWN'):
        i = common.NATIONS.index(n)
        for y in (2000, 2015, 2025):
            print(n, y, ' '.join('%s=%.6g/%d' % (SERIES[s][0], table[s][i][y - common.FIRST_YEAR], pmarks[s][i][y - common.FIRST_YEAR])
                                 for s in range(len(SERIES))))
    print('done in %.1f s' % (time.time() - t0))


if __name__ == '__main__':
    main()
