# GRWL v01.01 (Allen and Pavelsky 2018, CC-BY 4.0): every river 30 m or wider as its centreline, a vertex every
# 30 m with the river's width there. Ported from proto/rivers.py and the river part of proto/bake.py:
#   - group by segmentID, sort by segmentInd, cut where two vertices are more than 100 m apart;
#   - widths median-filtered over five vertices (fewer at a part's ends);
#   - Douglas-Peucker at 30 m (spec-earth 1: the error stays within a plot);
#   - vertices GRWL flags as lake or reservoir (lakeFlag 1) are dropped, splitting the line: the lakes come
#     from HydroLAKES; rivers (0), tidal reaches (2) and canals (3) stay, each vertex keeping its flag.
# The 3.1 GB zip on Zenodo is read by HTTP range, one member's .dbf at a time (the .dbf holds lat, lon,
# width, segment and flag): 1.36 GB streamed, inflated in memory, nothing kept but the simplified lines.
# Writes work/rivers/<GRWL tile>.npz (lat, lon, width, flag per vertex; part lengths) and a marker.
#   python3 -I rivers.py [tol_m]            the globe
import sys, os, io, time, zlib, struct, json
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np

URL = 'https://zenodo.org/records/1297434/files/GRWL_vector_V01.01.zip?download=1'
ZIPSIZE = 3104998836
TAIL = DL + '/grwl_tail.bin' if os.path.exists(DL + '/grwl_tail.bin') else SCRATCH + '/tmp/tail.bin'   # the zip's last 998,836 bytes (its central directory); the study's copy if fetch.py has not fetched one
OUTD = WORK + '/rivers'
TOL = 30.0
GAP = 100.0                                # metres: a longer step between vertices cuts the line
M_DEG = 111320.0                           # metres a degree, as the prototype


def members():
    tail = open(TAIL, 'rb').read()
    base = ZIPSIZE - len(tail)
    e = tail.rfind(b'PK\x05\x06')
    n, cd_size, cd_off = struct.unpack('<HII', tail[e + 10:e + 20])
    cd = tail[cd_off - base:cd_off - base + cd_size]
    out = {}; o = 0
    while o < len(cd) and cd[o:o + 4] == b'PK\x01\x02':
        method, = struct.unpack('<H', cd[o + 10:o + 12]); csize, usize = struct.unpack('<II', cd[o + 20:o + 28])
        nl, el, cl = struct.unpack('<HHH', cd[o + 28:o + 34]); loff, = struct.unpack('<I', cd[o + 42:o + 46])
        name = cd[o + 46:o + 46 + nl].decode()
        out[name] = (method, csize, usize, loff)
        o += 46 + nl + el + cl
    assert len(out) == n, (len(out), n)
    return out


def fetch_member(remote, m):
    method, csize, usize, loff = m
    head = remote.read(loff, 30)
    assert head[:4] == b'PK\x03\x04'
    nl, el = struct.unpack('<HH', head[26:30])
    raw = remote.read(loff + 30 + nl + el, csize)
    data = zlib.decompress(raw, -15) if method == 8 else raw
    assert len(data) == usize
    return data


def read_dbf(data):
    """The fields the pipeline uses from a GRWL .dbf, as numpy arrays (parsed from the fixed-width text)."""
    nrec, hlen, rlen = struct.unpack('<IHH', data[4:12])
    fields = {}; o = 32; pos = 1
    while data[o] != 0x0D:
        name = data[o:o + 11].split(b'\0')[0].decode(); size = data[o + 16]
        fields[name] = (pos, size); pos += size; o += 32
    rec = np.frombuffer(data, np.uint8, count=nrec * rlen, offset=hlen).reshape(nrec, rlen)
    def col(name, kind):
        p, s = fields[name]
        txt = np.ascontiguousarray(rec[:, p:p + s]).view('S%d' % s).ravel()
        return txt.astype(kind)
    return dict(sid=col('segmentID', np.int64), ind=col('segmentInd', np.int64), lon=col('lon', np.float64),
                lat=col('lat', np.float64), w=col('width_m', np.float64), f=col('lakeFlag', np.float64))


def lines(d, box=None):
    """proto/rivers.py lines(), vectorised: the parts as (n, 4) arrays of lat, lon, width (median of five), flag."""
    sid, ind, lat, lon, w, f = d['sid'], d['ind'], d['lat'], d['lon'], d['w'], d['f']
    if box is not None:
        k = (box[0] <= lat) & (lat <= box[1]) & (box[2] <= lon) & (lon <= box[3])
        sid, ind, lat, lon, w, f = sid[k], ind[k], lat[k], lon[k], w[k], f[k]
    if len(sid) == 0:
        return []
    o = np.lexsort((f, w, lon, lat, ind, sid))           # by segment, then the prototype's tuple order
    sid, lat, lon, w, f = sid[o], lat[o], lon[o], w[o], f[o]
    first = np.r_[True, sid[1:] != sid[:-1]]              # a segment's first vertex
    seg_start = np.nonzero(first)[0]
    cos = np.cos(np.radians(lat[seg_start]))[np.cumsum(first) - 1]
    d = np.hypot(np.diff(lat) * M_DEG, np.diff(lon) * M_DEG * cos[:-1])   # cos of the segment's first vertex
    cut = np.r_[True, first[1:] | (d > GAP)]              # a part starts here
    starts = np.nonzero(cut)[0]; ends = np.r_[starts[1:], len(sid)]
    # median of five, the window clipped to the part (np.median of the values there, as the prototype)
    n = len(sid); part_of = np.cumsum(cut) - 1
    win = np.full((n, 5), np.nan)
    for k, s in enumerate(range(-2, 3)):
        j = np.arange(n) + s
        ok = (j >= 0) & (j < n)
        ok[ok] &= part_of[j[ok]] == part_of[ok]
        win[ok, k] = w[j[ok]]
    wm = np.nanmedian(win, axis=1)
    out = []
    for a, b in zip(starts, ends):
        if b - a < 2:
            continue
        out.append(np.column_stack([lat[a:b], lon[a:b], wm[a:b], f[a:b]]))
    return out


def dp(a, tol):
    """proto/rivers.py dp(): Douglas-Peucker on (lat, lon) in local metres; keeps the ends; the kept indices."""
    cos = np.cos(np.radians(a[0, 0]))
    xy = np.column_stack([a[:, 1] * M_DEG * cos, a[:, 0] * M_DEG])
    keep = np.zeros(len(a), bool); keep[0] = keep[-1] = True
    stack = [(0, len(a) - 1)]
    while stack:
        i, j = stack.pop()
        if j <= i + 1:
            continue
        p, q = xy[i], xy[j]; v = q - p; L = np.hypot(*v)
        seg = xy[i + 1:j] - p
        dist = np.abs(seg[:, 0] * v[1] - seg[:, 1] * v[0]) / L if L > 0 else np.hypot(seg[:, 0], seg[:, 1])
        k = int(np.argmax(dist))
        if dist[k] > tol:
            m = i + 1 + k; keep[m] = True; stack += [(i, m), (m, j)]
    return np.where(keep)[0]


def simplify(parts, tol):
    """bake.py: Douglas-Peucker, then a run of lake vertices (flag 1) splits the line; runs of 2 or more kept."""
    rivers = []
    for a in parts:
        a = a[dp(a, tol)]
        run = []
        for v in a:
            if v[3] == 1:
                if len(run) >= 2: rivers.append(np.array(run))
                run = []
            else:
                run.append(v)
        if len(run) >= 2: rivers.append(np.array(run))
    return rivers


def main(tol=TOL):
    os.makedirs(OUTD, exist_ok=True)
    mem = members()
    dbfs = sorted(k for k in mem if k.endswith('.dbf'))
    log('rivers', '%d GRWL tiles' % len(dbfs))
    r = Remote(URL)
    import concurrent.futures
    ex = concurrent.futures.ThreadPoolExecutor(1)          # the next member downloads while this one is worked
    todo = [k for k in dbfs if not os.path.exists(os.path.join(OUTD, os.path.basename(k)[:-4] + '.done'))]
    fut = ex.submit(fetch_member, r, mem[todo[0]]) if todo else None
    t0 = time.time(); n_src = n_out = 0
    for i, k in enumerate(todo):
        data = fut.result()
        if i + 1 < len(todo):
            fut = ex.submit(fetch_member, Remote(URL), mem[todo[i + 1]])
        name = os.path.basename(k)[:-4]
        t1 = time.time()
        d = read_dbf(data); del data
        parts = lines(d)
        riv = simplify(parts, tol)
        lens = np.array([len(a) for a in riv], np.int64)
        v = np.concatenate(riv) if riv else np.zeros((0, 4))
        with open(os.path.join(OUTD, name + '.npz'), 'wb') as fo:
            np.savez_compressed(fo, v=v, lens=lens)
        st = dict(tile=name, source_vertices=int(len(d['sid'])), parts=len(parts), rivers=len(riv), vertices=int(len(v)),
                  seconds=round(time.time() - t1, 1))
        with open(os.path.join(OUTD, name + '.done'), 'w') as fo:
            json.dump(st, fo)
        n_src += st['source_vertices']; n_out += st['vertices']
        log('rivers', '%d/%d' % (i + 1, len(todo)), json.dumps(st), 'total %d -> %d vertices, %.0f s, streamed %.2f GB' %
            (n_src, n_out, time.time() - t0, Remote.received / 1e9))


if __name__ == '__main__':
    main(float(sys.argv[1]) if len(sys.argv) > 1 else TOL)
