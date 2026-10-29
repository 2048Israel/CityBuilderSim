"""BACI's own sums for the fixture's twelve nations, by a second and simple pass over its CSV (batch WN0, E2).

WHY: the file's flows come out of a long chain (the concordance, the country table, the partner rule, the domestic
estimate, the solve). WorldDataCheck needs one place to stand outside that chain: for every pair of the twelve, the sum of
BACI's rows through the concordance and nothing else, written by code that shares nothing with econ_trade.py but the
country table and the concordance file. A flow in the file on a kept pair must equal what this pass finds.

Run alone: python -I econ_trade_samples.py [SOURCES [WORK]] (writes WORK/econ/baci-samples.tsv).
"""
import csv
import os
import sys
import time
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import common
import econ_trade_countries

HERE = os.path.dirname(os.path.abspath(__file__))


def read_concordance(path):
    """{hs prefix: [(good key or None, vshare)]}, the rows of concordance.tsv; a '-' good is a prefix mapping to nothing."""
    conc = {}
    with open(path, encoding='utf-8') as f:
        lines = [l for l in f if l.strip() and not l.startswith('#')]
    for r in csv.DictReader(lines, delimiter='\t', quoting=csv.QUOTE_NONE):
        vs = r['share'] if r['vshare'] == '-' else r['vshare']
        conc.setdefault(r['hs'], []).append((None if r['good'] == '-' else r['good'], float(vs)))
    return conc


def read_goods(path):
    """The goods' keys in number order."""
    with open(path, encoding='utf-8') as f:
        lines = [l for l in f if l.strip() and not l.startswith('#')]
    return [r['key'] for r in csv.DictReader(lines, delimiter='\t', quoting=csv.QUOTE_NONE)]


def run(baci_zip, member, concordance, goods, by_code, out_path, log=print):
    """Writes out_path: good, seller, buyer, value for every pair of FIXTURE_NATIONS with a positive sum."""
    t0 = time.time()
    conc = read_concordance(concordance)
    keys = read_goods(goods)
    rank = {k: n for n, k in enumerate(keys)}
    twelve = {common.number(i) for i in common.FIXTURE_NATIONS}
    sums = {}
    rows = used = 0
    with zipfile.ZipFile(baci_zip) as z, z.open(member) as f:
        f.readline()
        for line in f:
            rows += 1
            p = line.split(b',')
            i = by_code.get(p[1], 0)
            if i not in twelve:
                continue
            j = by_code.get(p[2], 0)
            if j not in twelve or i == j:
                continue
            k = p[3].decode()
            m = conc.get(k)
            if m is None:
                m = conc.get(k[:4])
                if m is None:
                    m = conc.get(k[:2])
                    if m is None:
                        continue
            v = float(p[4])
            for g, vs in m:
                if g is None:
                    continue
                key = (rank[g], i, j)
                sums[key] = sums.get(key, 0.0) + v * vs
            used += 1
    out = ['good\tseller\tbuyer\tvalue']
    for (g, i, j) in sorted(sums):
        if sums[(g, i, j)] > 0:
            out.append('\t'.join([keys[g], common.NATIONS[i - 1], common.NATIONS[j - 1], repr(sums[(g, i, j)])]))
    with open(out_path, 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(out) + '\n')
    log('samples: %d rows read, %d between two of the twelve, %d pair sums written in %.0f s'
        % (rows, used, len(out) - 1, time.time() - t0))


if __name__ == '__main__':
    sources = sys.argv[1] if len(sys.argv) > 1 else 'C:/Users/Jerus/Documents/world-sources'
    work = sys.argv[2] if len(sys.argv) > 2 else sources + '/work'
    zpath = sources + '/baci/BACI_HS22_V202601.zip'
    with zipfile.ZipFile(zpath) as z:
        cc = z.read('country_codes_V202601.csv').decode('utf-8').splitlines()
    owners = econ_trade_countries.read_owners(work + '/geo/owners.tsv')
    by_code, _ = econ_trade_countries.build(cc, owners)
    os.makedirs(work + '/econ', exist_ok=True)
    run(zpath, 'BACI_HS22_Y%d_V202601.csv' % common.BASE_YEAR, HERE + '/concordance.tsv', HERE + '/goods.tsv',
        by_code, work + '/econ/baci-samples.tsv')
