# ESA WorldCover 2021 v200 (CC-BY 4.0), one 3 x 3 degree tile at a time, read by HTTP range from the
# cloud-optimised GeoTIFFs on AWS (the study's three local copies are reused):
#   - the land cover at 2' from overview level 2 (40 m, 100 x 100 pixels a 2' cell): counts of forest
#     (classes 10 tree cover and 95 mangroves), bare (60), snow and ice (70) and water (80, and 0 = no data,
#     the open sea) a cell, as bake.py classes them;
#   - the sea share at 15" (bake.py's rule: WorldCover water in a cell of GEBCO's ocean grown by one cell)
#     at full resolution (10 m, 50 x 50 pixels a cell) on the coast: every 1,024-pixel block that holds a
#     cell of the grown ocean whose water is mixed at level 2 is read whole. A cell read whole has its
#     exact share, as bake.py's; any other cell of the grown ocean is uniform at level 2 and takes 0 or 1.
# Writes work/wc/<tile>.npz and a marker; the log says what each tile cost.
#   python3 -I wc_tiles.py <worker> <workers> [tile ...]
import sys, os, io, time, zlib, json
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np, tifffile, warnings
warnings.filterwarnings("ignore", message="Cannot close a netcdf_file")
from scipy.io import netcdf_file

URL = 'https://esa-worldcover.s3.eu-central-1.amazonaws.com/v200/2021/map/ESA_WorldCover_10m_2021_v200_%s_Map.tif'
LOCAL = DATA + '/wc/WC_%s.tif'          # the study's copies (byte-identical to the S3 objects; checked for N48E000)
GRID = DL + '/esa_worldcover_grid.geojson'
OUTD = WORK + '/wc'
PX = 12000                               # full-resolution pixels a degree
CELL = 50                                # full-resolution pixels a 15" cell
LV = 2                                   # the overview read whole: level 2, 4 x 4 pixels, 3,000 a degree
TID = src('gebco/GEBCO_2026_TID.nc')

LUT_WATER = np.zeros(256, np.uint8); LUT_WATER[[0, 80]] = 1
LUT_FOREST = np.zeros(256, np.uint8); LUT_FOREST[[10, 95]] = 1
LUT_BARE = np.zeros(256, np.uint8); LUT_BARE[60] = 1
LUT_ICE = np.zeros(256, np.uint8); LUT_ICE[70] = 1


def tiles():
    g = json.load(open(GRID))
    return sorted(f['properties']['ll_tile'] for f in g['features'])


def corner(name):
    """(south latitude, west longitude) of a WorldCover tile name like N51E003."""
    return (1 if name[0] == 'N' else -1) * int(name[1:3]), (1 if name[3] == 'E' else -1) * int(name[4:7])


def source(name):
    p = LOCAL % name
    return Local(p) if os.path.exists(p) else Remote(URL % name)


class Cog:
    def __init__(self, src):
        self.src = src
        rf = RangeFile(src, cache_blocks=4); rf.BLOCK = 1 << 15
        t = tifffile.TiffFile(rf)
        self.levels = []
        for p in t.pages:
            self.levels.append(dict(shape=p.shape, tw=p.tilewidth, th=p.tilelength, off=np.array(p.dataoffsets, np.int64),
                                    cnt=np.array(p.databytecounts, np.int64), pred=int(p.predictor), comp=int(p.compression)))
            assert int(p.compression) == 8 and int(p.predictor) == 1, (p.compression, p.predictor)
        self.bytes = 0

    def fetch(self, level, idx, gap=1 << 18):
        """Blocks `idx` of a level, as {index: uint8 array}; ranges less than `gap` apart are merged into one
        request, and a remote file's requests run on 4 connections at once (the time is the network's)."""
        L = self.levels[level]
        idx = sorted(set(int(i) for i in idx), key=lambda i: int(L['off'][i]))
        out = {}
        runs = []
        for i in idx:
            o, c = int(L['off'][i]), int(L['cnt'][i])
            if runs and o - runs[-1][1] <= gap and o >= runs[-1][0]:
                runs[-1][1] = max(runs[-1][1], o + c); runs[-1][2].append(i)
            else:
                runs.append([o, o + c, [i]])
        if isinstance(self.src, Remote) and len(runs) > 1:
            import concurrent.futures, threading
            tl = threading.local()
            def get(run):
                if not hasattr(tl, 'r'):
                    tl.r = Remote(self.src.url)
                return tl.r.read(run[0], run[1] - run[0])
            with concurrent.futures.ThreadPoolExecutor(4) as ex:
                bufs = list(ex.map(get, runs))
        else:
            bufs = [self.src.read(a, b - a) for a, b, _ in runs]
        for (a, b, members), buf in zip(runs, bufs):
            self.bytes += len(buf)
            for i in members:
                o, c = int(L['off'][i]), int(L['cnt'][i])
                raw = buf[o - a:o - a + c]
                out[i] = np.frombuffer(zlib.decompress(raw), np.uint8).reshape(L['th'], L['tw'])
        return out

    def level_image(self, level):
        L = self.levels[level]
        H, W = L['shape']; th, tw = L['th'], L['tw']
        ny, nx = -(-H // th), -(-W // tw)
        img = np.zeros((ny * th, nx * tw), np.uint8)
        blocks = self.fetch(level, range(ny * nx), gap=1 << 30)
        for i, a in blocks.items():
            y, x = divmod(i, nx)
            img[y * th:(y + 1) * th, x * tw:(x + 1) * tw] = a
        return img[:H, :W]


# the four classes' counts packed in one uint32 a pixel (a byte each): summed over a 2' cell's row of 100 pixels
# no field passes 100, so one look-up and one sum serve all four
LUT_PACK = (LUT_FOREST.astype(np.uint32) | (LUT_BARE.astype(np.uint32) << 8) | (LUT_ICE.astype(np.uint32) << 16)
            | (LUT_WATER.astype(np.uint32) << 24))


def count_lc(img, n2=90, k=100):
    """Counts of forest, bare, ice and water in each k x k block of img: 4 x n2 x n2, uint16."""
    out = np.zeros((4, n2, n2), np.uint32)
    for r in range(n2):
        p = LUT_PACK[img[r * k:(r + 1) * k]].reshape(k, n2, k).sum(axis=2, dtype=np.uint32)   # k x n2, fields <= k
        for f in range(4):
            out[f, r] = ((p >> (8 * f)) & 0xFF).sum(axis=0)
    return out.astype(np.uint16)


def grown_ocean(lat0, lon0, n=720):
    """GEBCO 2026's ocean (TID != 0) over the tile's 15" cells, grown by one cell (4-neighbours), rows from the north."""
    g = netcdf_file(TID, 'r', mmap=True)
    tid = g.variables['tid']
    i0 = (lat0 + 90) * 240
    rows = np.arange(i0 - 1, i0 + n + 1)
    c0 = (lon0 + 180) * 240 - 1; c1 = c0 + n + 2
    r0, r1 = max(0, rows[0]), min(43200, rows[-1] + 1)
    a = np.ones((n + 2, n + 2), bool)                       # beyond a pole: no cells; treated as ocean, never read
    if c0 >= 0 and c1 <= 86400:
        block = np.array(tid[r0:r1, c0:c1])
    else:                                                    # across the date line: whole rows, then wrap
        block = np.array(tid[r0:r1, :])[:, np.arange(c0, c1) % 86400]
    a[r0 - rows[0]:r1 - rows[0], :] = block != 0
    del block
    a = np.flipud(a)                                         # north row first
    grown = a[1:-1, 1:-1].copy()
    grown |= a[:-2, 1:-1]; grown |= a[2:, 1:-1]; grown |= a[1:-1, :-2]; grown |= a[1:-1, 2:]
    return grown


def overlap_sums(W, n_cells, f):
    """Sums of W (uint8, rows x cols) over each cell's overlapping pixels when a cell is f pixels (f = 12.5 at
    level 2): returns (sums, pixel counts), both n_cells x n_cells."""
    def axis_sum(A, axis):
        N = A.shape[axis]
        starts = np.floor(np.arange(n_cells) * f).astype(np.int64)
        ends = np.ceil(np.arange(1, n_cells + 1) * f).astype(np.int64)        # exclusive
        s = np.add.reduceat(A, starts, axis=axis, dtype=np.uint32)            # [start_k, start_{k+1})
        nxt = np.append(starts[1:], N)
        extra = ends > nxt                                                    # the cell also overlaps pixel nxt
        if extra.any():
            idx = nxt[extra]
            if axis == 1:
                s[:, extra] += A[:, idx]
            else:
                s[extra, :] += A[idx, :]
        cnt = (np.minimum(ends, N) - starts).astype(np.uint32)
        return s, cnt
    s1, c1 = axis_sum(W, 1)
    s2, c2 = axis_sum(s1, 0)
    return s2, np.outer(c2, c1)


def process(name):
    t0 = time.time()
    lat0, lon0 = corner(name)
    top = lat0 + 3
    src = source(name)
    cog = Cog(src)
    # ---- level 2, whole
    img = cog.level_image(LV)                                   # 9000 x 9000, north row first
    assert img.shape == (9000, 9000), img.shape
    n2 = 90                                                     # 2' cells a side
    lc = count_lc(img)                                          # 4 x 90 x 90, of 10,000
    W = LUT_WATER[img]
    del img
    s, c = overlap_sums(W, 720, 12.5)
    del W
    grown = grown_ocean(lat0, lon0)
    mixed = grown & (s > 0) & (s < c)
    # ---- full resolution where the grown ocean's water is mixed
    L0 = cog.levels[0]; nb = -(-L0['shape'][1] // L0['tw'])     # 36 blocks a side
    need = set()
    for i, j in zip(*np.nonzero(mixed)):
        for by in range((CELL * i) // 1024, (CELL * i + CELL - 1) // 1024 + 1):
            for bx in range((CELL * j) // 1024, (CELL * j + CELL - 1) // 1024 + 1):
                need.add(by * nb + bx)
    wcount = np.zeros((720, 720), np.int32); pcount = np.zeros((720, 720), np.int32)
    need = sorted(need)
    for k in range(0, len(need), 64):                           # a few block rows at a time: memory stays small
        blocks = cog.fetch(0, need[k:k + 64])
        for bi, a in blocks.items():
            by, bx = divmod(bi, nb)
            y0, x0 = by * 1024, bx * 1024
            h = min(1024, 36000 - y0); w = min(1024, 36000 - x0)
            a = LUT_WATER[a[:h, :w]]
            ry = np.unique(np.concatenate([[0], np.arange(-(y0 % CELL) % CELL, h, CELL)]))
            rx = np.unique(np.concatenate([[0], np.arange(-(x0 % CELL) % CELL, w, CELL)]))
            ry = ry[ry < h]; rx = rx[rx < w]
            sums = np.add.reduceat(np.add.reduceat(a, ry, axis=0, dtype=np.int32), rx, axis=1, dtype=np.int32)
            hy = np.diff(np.append(ry, h)); hx = np.diff(np.append(rx, w))
            ci = (y0 + ry) // CELL; cj = (x0 + rx) // CELL
            wcount[np.ix_(ci, cj)] += sums
            pcount[np.ix_(ci, cj)] += np.outer(hy, hx).astype(np.int32)
    full = pcount == CELL * CELL
    share = np.where(full, wcount / float(CELL * CELL), np.where(s == c, 1.0, 0.0))
    # bake.py: np.round(mean * 255) of water & seaish
    sea = np.where(grown, np.round(share * 255), 0).astype(np.uint8)
    os.makedirs(OUTD, exist_ok=True)
    with open(os.path.join(OUTD, name + '.npz'), 'wb') as f:
        np.savez_compressed(f, sea=sea, lc=lc, full=full, mixed=mixed, grown=grown)
    st = dict(tile=name, bytes=cog.bytes, full_blocks=len(need), mixed_cells=int(mixed.sum()), full_cells=int(full.sum()),
              seconds=round(time.time() - t0, 2), local=isinstance(src, Local))
    with open(os.path.join(OUTD, name + '.done'), 'w') as f:
        json.dump(st, f)
    return st


if __name__ == '__main__':
    worker, workers = int(sys.argv[1]), int(sys.argv[2])
    names = sys.argv[3:] or tiles()
    names = [n for k, n in enumerate(names) if k % workers == worker]
    done = 0
    for n in names:
        if os.path.exists(os.path.join(OUTD, n + '.done')):
            done += 1; continue
        for attempt in range(3):
            try:
                st = process(n); break
            except Exception as e:
                import traceback; log('wc%d' % worker, 'ERROR', n, repr(e), traceback.format_exc().replace('\n', ' | ')); time.sleep(10)
        else:
            continue
        done += 1
        log('wc%d' % worker, '%d/%d' % (done, len(names)), json.dumps(st), 'streamed %.2f GB, NoSuchBucket retries %d' % (Remote.received / 1e9, Remote.stale))
