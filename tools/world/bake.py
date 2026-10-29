"""Bakes world-1.bin (batch WN0, slice G, 2026-10-28): the layers the stages left in the work folder, joined and
written in FORMAT.txt's order (meta, nations, moneys, paths, goods, trade, freight, owner, lines).

WHY: each stage writes its own piece (geo.py the geography, econ_paths.py the moneys and paths, econ_trade.py the
goods and the trade); the file is one container read by WorldData, and its SHA-256 is its identity, so one script
puts the pieces together and nothing else touches the bytes. Run it with python -I; --partial leaves a missing
economic layer out (a trial bake), without it a missing piece stops the bake.
Reads (work = <sources>/work):
  geo/nations-geo.tsv geo/freight.bin geo/owner.index geo/owner.blobs geo/lines.bin geo/meta-*.tsv
  econ/nations-money.tsv econ/moneys.bin econ/paths.bin econ/goods.tsv econ/trade.bin econ/meta-*.tsv
Writes work/out/world-1.bin, and (not with --partial) tools/world/world-1.sha256.
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import glob
import json
import struct
import time

import common


def read_bytes(path):
    with open(path, 'rb') as f:
        return f.read()


def meta_rows(folder):
    """The dial and source rows of every meta-*.tsv in a folder, in file-name order."""
    rows = []
    for p in sorted(glob.glob(folder + '/meta-*.tsv')):
        with open(p, encoding='utf-8', newline='') as f:
            for ln in f.read().split('\n'):
                ln = ln.rstrip('\r')
                if ln.split('\t')[0] in ('dial', 'source'):
                    rows.append(ln.split('\t'))
    return rows


def wdi_regions(sources):
    """The World Bank's region of each country, WDI's countries list by iso3 (Taiwan, which the list lacks, is East
    Asia & Pacific, FORMAT.txt); used when E1's table has no region column."""
    with open(sources + '/wdi/countries.json', encoding='utf-8') as f:
        rows = json.load(f)[1]
    out = {r['id']: r['region']['value'].strip() for r in rows}
    out['TWN'] = 'East Asia & Pacific'
    return out


def build_nations(work, econ, sources, partial, missing):
    """The nations layer's rows: nations-geo.tsv joined with E1's nations-money.tsv on iso3 (E1's name wins, and its
    region when it has the column; else WDI's)."""
    gh, grows = common.read_tsv(work + '/geo/nations-geo.tsv')
    g = [dict(zip(gh, r)) for r in grows]
    money = {}
    mp = econ + '/nations-money.tsv'
    if os.path.exists(mp):
        mh, mrows = common.read_tsv(mp)
        money = {r[0]: dict(zip(mh, r)) for r in mrows}
    else:
        if not partial:
            raise SystemExit('missing ' + mp + ' (E1); run with --partial for a trial bake')
        missing.append('nations-money (the nations layer has "-" for money, money_history and regime)')
    header = ['number', 'iso3', 'name', 'kind', 'seller', 'money', 'money_history', 'regime', 'node_name', 'node_lat',
              'node_lon', 'label_lat', 'label_lon', 'landlocked', 'region']
    wdi = None
    out = []
    for r in g:
        m = money.get(r['iso3']) if r['kind'] == 'nation' else None
        if r['kind'] == 'nation' and money and m is None:
            raise SystemExit('nations-money.tsv has no row for ' + r['iso3'])
        name = m['name'] if m else r['name']
        if r['kind'] != 'nation':
            region = '-'
        elif m and 'region' in m:
            region = m['region']
        else:
            wdi = wdi or wdi_regions(sources)
            region = wdi[r['iso3']]
        out.append([r['number'], r['iso3'], name, r['kind'], r['seller'],
                    m['money'] if m else '-', m['money_history'] if m else '-', m['regime'] if m else '-',
                    r['node_name'], r['node_lat'], r['node_lon'], r['label_lat'], r['label_lon'], r['landlocked'],
                    region])
    return header, out


def main():
    p = common.parser('Bakes world-1.bin from the stages\' files.')
    p.add_argument('--partial', action='store_true', help='leave a missing economic layer out (a trial bake)')
    p.add_argument('--econ', default=None, help="the economic stages' folder, default <work>/econ")
    p.add_argument('--out', default=None, help='the file to write, default <work>/out/world-1.bin')
    p.add_argument('--made', default=None, help="meta's made date yyyy-mm-dd, default today")
    a = p.parse_args()
    work = common.work_dir(a)
    econ = a.econ or work + '/econ'
    out = a.out or work + '/out/world-1.bin'
    missing = []

    def need(path, what):
        if os.path.exists(path):
            return read_bytes(path)
        if not a.partial:
            raise SystemExit('missing %s (%s); run with --partial for a trial bake' % (path, what))
        missing.append(what)
        return None

    # the geography: always required
    header, nrows = build_nations(work, econ, a.sources, a.partial, missing)
    n_nations = sum(1 for r in nrows if r[3] == 'nation')
    n_areas = len(nrows) - n_nations
    assert n_nations == len(common.NATIONS)
    freight = read_bytes(work + '/geo/freight.bin')
    lines = read_bytes(work + '/geo/lines.bin')
    index = struct.unpack('>%di' % common.TILES, read_bytes(work + '/geo/owner.index'))
    blobdata = read_bytes(work + '/geo/owner.blobs')
    blobs, pos = [], 0
    for e in index:
        if e > 0:
            blobs.append(blobdata[pos:pos + e])
            pos += e
    assert pos == len(blobdata)

    # the economy
    moneys = need(econ + '/moneys.bin', 'moneys (E1)')
    paths = need(econ + '/paths.bin', 'paths (E1)')
    goods = need(econ + '/goods.tsv', 'goods (E2)')
    trade = need(econ + '/trade.bin', 'trade (E2)')
    prices = need(econ + '/prices.bin', "prices (FORMAT.txt's prices layer)")
    n_moneys = struct.unpack_from('>i', moneys, 0)[0] if moneys else 0
    n_goods = (goods.decode('utf-8').count('\n') - 1) if goods else 0
    if paths:
        assert struct.unpack_from('>i', paths, 0)[0] == n_nations, 'paths: N'
    if prices and goods:
        assert struct.unpack_from('>i', prices, 0)[0] == n_goods, 'prices: G'
    if trade:
        tn, tg, tb = struct.unpack_from('>iii', trade, 0)
        assert tn == n_nations and tg == n_goods and tb == common.BASE_YEAR, ('trade header', tn, tg, tb)
    assert struct.unpack_from('>i', freight, 0)[0] == n_nations

    made = a.made or time.strftime('%Y-%m-%d')
    mrows = [['key', 'fields'], ['name', 'world-1'], ['made', made], ['base_year', str(common.BASE_YEAR)],
             ['nations', str(n_nations)], ['areas', str(n_areas)], ['moneys', str(n_moneys)], ['goods', str(n_goods)]]
    seen = {}
    for r in meta_rows(work + '/geo') + meta_rows(econ):
        k = (r[0], r[1])
        if k in seen:   # a dial two stages both state (the freight dials): once, and the same
            assert seen[k][2:3] == r[2:3], 'two values for %s %s: %s and %s' % (k + (seen[k][2], r[2]))
            continue
        seen[k] = r
        mrows.append(r)
    layers = [common.text_layer('meta', '\n'.join('\t'.join(r) for r in mrows) + '\n'),
              common.text_layer('nations', '\n'.join('\t'.join(r) for r in [header] + nrows) + '\n')]
    if moneys:
        layers.append(common.bytes_layer('moneys', moneys))
    if paths:
        layers.append(common.bytes_layer('paths', paths))
    if goods:
        layers.append(common.Layer('goods', 2, payload=goods))
    if trade:
        layers.append(common.bytes_layer('trade', trade))
    layers.append(common.bytes_layer('freight', freight))
    layers.append(common.tile_layer('owner', common.OWNER_CPD, index, blobs))
    layers.append(common.bytes_layer('lines', lines))
    if prices:
        layers.append(common.bytes_layer('prices', prices))
    sha, sizes = common.write_container(out, layers)
    for L in layers:
        i, d = sizes[L.name]
        print('  %-8s index %8d bytes, data %10d bytes' % (L.name, i, d))
    common.log('baked %s: %d bytes, SHA-256 %s' % (out, os.path.getsize(out), sha))
    if missing:
        print('  PARTIAL: left out or filled with "-": ' + '; '.join(missing))
    elif a.out is None:
        with open(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'world-1.sha256'), 'w',
                  newline='\n') as f:
            f.write('%s  world-1.bin\n' % sha)
        common.log('wrote tools/world/world-1.sha256')


if __name__ == '__main__':
    main()
