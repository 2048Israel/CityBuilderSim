"""The world pipeline's shared ground (batch WN0, 2026-10-28): the nations, their numbers, the years and the dials
every stage reads. FORMAT.txt is the file these stages make.

WHY: four stages (geo.py, econ_paths.py, econ_trade.py, bake.py) are written side by side; a nation's number or a
freight dial written twice would drift. Run every stage with python -I (the sources are downloads); a stage puts
its own folder on sys.path itself to import this.
"""
import argparse
import hashlib
import io
import os
import struct
import time
import zlib

# The 196 (spec-nations 3.1, D1): the 193 UN members, Taiwan, Hong Kong and Macau, by ISO 3166 alpha-3, A to Z.
# A nation's number is its place here plus one.
NATIONS = ('AFG AGO ALB AND ARE ARG ARM ATG AUS AUT AZE BDI BEL BEN BFA BGD BGR BHR BHS BIH BLR BLZ BOL BRA BRB BRN '
           'BTN BWA CAF CAN CHE CHL CHN CIV CMR COD COG COL COM CPV CRI CUB CYP CZE DEU DJI DMA DNK DOM DZA ECU EGY '
           'ERI ESP EST ETH FIN FJI FRA FSM GAB GBR GEO GHA GIN GMB GNB GNQ GRC GRD GTM GUY HKG HND HRV HTI HUN IDN '
           'IND IRL IRN IRQ ISL ISR ITA JAM JOR JPN KAZ KEN KGZ KHM KIR KNA KOR KWT LAO LBN LBR LBY LCA LIE LKA LSO '
           'LTU LUX LVA MAC MAR MCO MDA MDG MDV MEX MHL MKD MLI MLT MMR MNE MNG MOZ MRT MUS MWI MYS NAM NER NGA NIC NLD '
           'NOR NPL NRU NZL OMN PAK PAN PER PHL PLW PNG POL PRK PRT PRY QAT ROU RUS RWA SAU SDN SEN SGP SLB SLE SLV '
           'SMR SOM SRB SSD STP SUR SVK SVN SWE SWZ SYC SYR TCD TGO THA TJK TKM TLS TON TTO TUN TUR TUV TWN TZA UGA '
           'UKR URY USA UZB VCT VEN VNM VUT WSM YEM ZAF ZMB ZWE').split()
assert len(NATIONS) == 196 and NATIONS == sorted(set(NATIONS))

# The three that are not UN members.
NOT_UN = ('HKG', 'MAC', 'TWN')

# The nations with no coast of the open sea (the Caspian's shores count as none): their freight starts by road.
LANDLOCKED = set(('AFG AND ARM AUT AZE BDI BFA BLR BOL BTN BWA CAF CHE CZE ETH HUN KAZ KGZ LAO LIE LSO LUX MDA MKD '
                  'MLI MNG MWI NER NPL PRY RWA SMR SRB SSD SVK SWZ TCD TJK TKM UGA UZB ZMB ZWE').split())

# The fixture's twelve (FORMAT.txt, THE FIXTURE): the earth fixture's windows' nations (Rotterdam's three, Paris,
# Perth, Halifax, Medellin), the largest economies, and one landlocked.
FIXTURE_NATIONS = 'AUS BEL CAN CHE CHN COL DEU FRA GBR JPN NLD USA'.split()


def number(iso3):
    """A nation's number in the full file, 1..196."""
    return NATIONS.index(iso3) + 1


FIRST_YEAR = 2000   # the paths' first year: Historical starts in January 2000 (D13)
LAST_DATA_YEAR = 2025   # the last year of published output, prices and rates
LAST_YEAR = 2100    # the people's last year (WPP 2024's horizon)
YEARS = LAST_YEAR - FIRST_YEAR + 1
MONTHS = (LAST_DATA_YEAR - FIRST_YEAR + 1) * 12   # 2000-01 to 2025-12: the monthly rates
BASE_YEAR = 2024    # the trade's year: BACI V202601's newest

# ----- the reference freight (FORMAT.txt, trade and freight); the dials go to meta with these sentences -----
# A tonne-km by sea, US$: between PortCheck's dry bulk rows and a container's (est.).
SEA_USD_TKM = 0.003
# A tonne-km by land, US$: a rail and lorry mix, under PortCheck's lorry at 0.080 (est.).
LAND_USD_TKM = 0.05
# A tonne's handling on a trade between two nations, US$: the ports, the papers, the legs to the quay (est.).
HANDLING_USD_T = 10.0
# The land km a sea km is worth on the cheapest route: the two rates' ratio.
LAND_PER_SEA = LAND_USD_TKM / SEA_USD_TKM
# A road's length over the great circle between two neighbours' nodes (est.).
LAND_DETOUR = 1.3
# A sea route's length over the great circle, for an island CERDI lacks (est.).
SEA_DETOUR = 1.25

DIALS = [('SEA_USD_TKM', SEA_USD_TKM, "A tonne-km by sea, US$: between PortCheck's dry bulk rows and a container's (est.)."),
         ('LAND_USD_TKM', LAND_USD_TKM, "A tonne-km by land, US$: a rail and lorry mix, under PortCheck's lorry at 0.080 (est.)."),
         ('HANDLING_USD_T', HANDLING_USD_T, "A tonne's handling on a trade between two nations, US$ (est.)."),
         ('LAND_PER_SEA', LAND_PER_SEA, "The land km a sea km is worth on the cheapest route: the two rates' ratio."),
         ('LAND_DETOUR', LAND_DETOUR, "A road's length over the great circle between two neighbours' nodes (est.)."),
         ('SEA_DETOUR', SEA_DETOUR, "A sea route's length over the great circle, for an island CERDI lacks (est.).")]


# ===== the paths and the container (G, 2026-10-28) =====
# Where the downloads are (read-only, bar the work folder) and where the pipeline's intermediates and result go.
SOURCES = 'C:/Users/Jerus/Documents/world-sources'
WORK = SOURCES + '/work'

MAGIC = b'WORLD-1\n'
FORMAT_VERSION = 1
# A kind 0 layer's tiles: 180 x 360 one-degree tiles, numbered (lat0 + 90) * 360 + (lon0 + 180).
TILES = 180 * 360
# The zlib level every blob is compressed at: the smallest file; the reader is Inflater whatever the level.
ZLIB_LEVEL = 9
# The owner layer's cells a degree (15 arc-seconds, the earth file's finest grid).
OWNER_CPD = 240
# A boundary line's coordinates are stored as whole multiples of 1/LINE_SCALE of a degree (about a metre).
LINE_SCALE = 100000


def tile_id(lat0, lon0):
    """The one-degree tile whose south-west corner is (lat0, lon0)."""
    return (lat0 + 90) * 360 + (lon0 + 180)


def tile_corner(tid):
    """The south-west corner (lat0, lon0) of a tile."""
    return tid // 360 - 90, tid % 360 - 180


def parser(description):
    """An argument parser every stage shares: --sources and --work, with their defaults."""
    p = argparse.ArgumentParser(description=description)
    p.add_argument('--sources', default=SOURCES, help='the downloads folder (read-only), default ' + SOURCES)
    p.add_argument('--work', default=None, help='the intermediates folder, default <sources>/work')
    return p


def work_dir(args):
    """The work folder an argument set names."""
    return args.work if args.work else args.sources + '/work'


_T0 = time.time()


def log(*msg):
    """A progress line with the seconds since the script started (the pipeline's only log)."""
    print('[%5.0fs] %s' % (time.time() - _T0, ' '.join(str(m) for m in msg)), flush=True)


def fnum(v):
    """A number as a text table holds it: a float by repr() (so Double.parseDouble gives the same double), an int
    by str(), None as '-'."""
    if v is None:
        return '-'
    if isinstance(v, float):
        return repr(v)
    return str(v)


def read_tsv(path):
    """A text table: (the column names, the rows as lists of strings); blank lines are skipped."""
    with open(path, encoding='utf-8', newline='') as f:
        lines = [ln for ln in f.read().split('\n') if ln.strip() != '']
    rows = [ln.rstrip('\r').split('\t') for ln in lines]
    return rows[0], rows[1:]


def write_tsv(path, header, rows):
    """Writes a text table, LF line endings, the first line the column names."""
    with open(path, 'w', encoding='utf-8', newline='\n') as f:
        f.write('\t'.join(header) + '\n')
        for r in rows:
            assert len(r) == len(header), (path, header, r)
            f.write('\t'.join(fnum(x) if not isinstance(x, str) else x for x in r) + '\n')


def utf(s):
    """java.io.DataOutputStream.writeUTF for text the pipeline writes (no NUL, no character beyond the BMP)."""
    b = s.encode('utf-8')
    assert len(b) < 65536 and '\0' not in s and all(ord(ch) < 0x10000 for ch in s), s
    return struct.pack('>H', len(b)) + b


class Layer:
    """One layer of the container: kind 2 holds one payload (stored as one zlib stream), kind 0 holds the 64,800
    tiles' entries and the blobs of the tiles that have one, in tile order (FORMAT.txt, HEADER and INDEX)."""

    def __init__(self, name, kind, cpd=0, payload=None, entries=None, blobs=None):
        self.name, self.kind, self.cpd = name, kind, cpd
        self.payload = payload      # kind 2: the uncompressed bytes
        self.entries = entries      # kind 0: 64,800 ints, e > 0 the blob's length, e <= 0 uniform at -e
        self.blobs = blobs          # kind 0: the blobs, tile order
        if kind == 0:
            assert len(entries) == TILES
            assert [e for e in entries if e > 0] == [len(b) for b in blobs]

    def stored(self):
        """(the index bytes, the data bytes) as written."""
        if self.kind == 2:
            return b'', zlib.compress(self.payload, ZLIB_LEVEL)
        idx = zlib.compress(struct.pack('>%di' % TILES, *self.entries), ZLIB_LEVEL)
        return idx, b''.join(self.blobs)


def text_layer(name, text):
    """A kind 2 layer holding UTF-8 text."""
    return Layer(name, 2, payload=text.encode('utf-8'))


def bytes_layer(name, payload):
    """A kind 2 layer holding binary bytes."""
    return Layer(name, 2, payload=payload)


def tile_layer(name, cpd, entries, blobs):
    """A kind 0 layer from its entries and blobs."""
    return Layer(name, 0, cpd=cpd, entries=list(entries), blobs=list(blobs))


def write_container(path, layers):
    """Writes world-1.bin (or its fixture): the header, then each layer's index and data. Returns the file's
    SHA-256 (hex) and {layer name: (index bytes, data bytes)}."""
    stored = [(L, L.stored()) for L in layers]

    def header(offsets):
        h = io.BytesIO()
        h.write(MAGIC)
        h.write(struct.pack('>ii', FORMAT_VERSION, len(layers)))
        for L, _ in stored:
            h.write(utf(L.name))
            io_, il, do, dl = offsets.get(L.name, (0, 0, 0, 0))
            h.write(struct.pack('>biqiqq', L.kind, L.cpd, io_, il, do, dl))
        return h.getvalue()

    pos = len(header({}))
    offsets = {}
    for L, (idx, data) in stored:
        io_ = pos
        pos += len(idx)
        do = pos
        pos += len(data)
        offsets[L.name] = (io_, len(idx), do, len(data))
    sha = hashlib.sha256()
    os.makedirs(os.path.dirname(os.path.abspath(path)), exist_ok=True)
    with open(path, 'wb') as f:
        for part in [header(offsets)] + [p for _, (idx, data) in stored for p in (idx, data)]:
            f.write(part)
            sha.update(part)
    assert os.path.getsize(path) == pos
    return sha.hexdigest(), {L.name: (len(idx), len(data)) for L, (idx, data) in stored}


def sha256_file(path):
    """A file's SHA-256, hex."""
    h = hashlib.sha256()
    with open(path, 'rb') as f:
        for chunk in iter(lambda: f.read(1 << 20), b''):
            h.update(chunk)
    return h.hexdigest()
