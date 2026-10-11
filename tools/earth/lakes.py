# HydroLAKES v1.0 (Messager et al. 2016, CC-BY 4.0): 1.43 M lake polygons of 10 ha or more. Ported from
# proto/lakes.py, with the shipped split of spec-earth 1:
#   - lakes of 10 km2 or more (Lake_area) become 15" shares: each 1-degree tile's lakes are filled at 10 x 10
#     sample points a cell (even-odd, so islands stay land), the share being the samples inside, x 255;
#   - smaller lakes become a point and an area each: the point is the polygon's area-weighted centroid
#     (proto/lakes.py used the pour point, which sits on the shore; the game draws a blob about the point),
#     the area a code of 1/8 octave from 0.1 km2 (proto/lakes.py's).
# The 820 MB zip is downloaded once to dl/hylak (kept); its .shp (1.1 GB) is parsed as a stream, never unpacked.
# Writes work/lakes_small.npz, work/lakes_big_geom.bin + work/lakes_big_index.npz, work/lakes_share.bin + .idx.
import sys, os, io, time, zlib, struct, json, zipfile
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np

ZIP = DL + '/hylak/HydroLAKES_polys_v10_shp.zip'
URL = 'https://data.hydrosheds.org/file/hydrolakes/HydroLAKES_polys_v10_shp.zip'
SHP = 'HydroLAKES_polys_v10_shp/HydroLAKES_polys_v10.shp'
DBF = 'HydroLAKES_polys_v10_shp/HydroLAKES_polys_v10.dbf'
BIG_KM2 = 10.0
S = 10                                    # sample points a 15" cell, each way


def read_dbf(z):
    data = z.read(DBF)
    nrec, hlen, rlen = struct.unpack('<IHH', data[4:12])
    fields = {}; o = 32; pos = 1
    while data[o] != 0x0D:
        name = data[o:o + 11].split(b'\0')[0].decode(); size = data[o + 16]
        fields[name] = (pos, size); pos += size; o += 32
    rec = np.frombuffer(data, np.uint8, count=nrec * rlen, offset=hlen).reshape(nrec, rlen)
    def col(name, kind):
        p, s = fields[name]
        return np.ascontiguousarray(rec[:, p:p + s]).view('S%d' % s).ravel().astype(kind)
    out = dict(id=col('Hylak_id', np.int64), area=col('Lake_area', np.float64), type=col('Lake_type', np.int64))
    p, s = fields['Lake_name']
    out['name'] = np.ascontiguousarray(rec[:, p:p + s]).view('S%d' % s).ravel()
    return out


def ring_centroid(xy):
    """Signed area and centroid of a closed ring (planar, in degrees about its first point)."""
    x0, y0 = xy[0]
    x = xy[:, 0] - x0; y = xy[:, 1] - y0
    cr = x[:-1] * y[1:] - x[1:] * y[:-1]
    A = cr.sum() / 2
    if A == 0:
        return 0.0, x0 + x.mean(), y0 + y.mean()
    cx = ((x[:-1] + x[1:]) * cr).sum() / (6 * A); cy = ((y[:-1] + y[1:]) * cr).sum() / (6 * A)
    return A, x0 + cx, y0 + cy


def phase_shapes():
    """Streams the .shp: every lake's centroid; the big lakes' rings to lakes_big_geom.bin."""
    if os.path.exists(WORK + '/lakes_small.npz.done'):
        return
    z = zipfile.ZipFile(ZIP)
    d = read_dbf(z)
    n = len(d['id'])
    log('lakes', 'dbf: %d lakes, %.0f km2; %d of 10 km2 or more' % (n, d['area'].sum(), (d['area'] >= BIG_KM2).sum()))
    f = z.open(SHP)
    head = f.read(100)
    assert struct.unpack('>i', head[:4])[0] == 9994
    cx = np.zeros(n); cy = np.zeros(n)
    geom = open(WORK + '/lakes_big_geom.bin', 'wb')
    big_rows = []
    t0 = time.time()
    for i in range(n):
        rh = f.read(8)
        num, clen = struct.unpack('>ii', rh)
        assert num == i + 1, (num, i)
        body = f.read(clen * 2)
        st, = struct.unpack('<i', body[:4])
        assert st == 5, st
        bbox = struct.unpack('<4d', body[4:36])
        npart, npt = struct.unpack('<ii', body[36:44])
        parts = np.frombuffer(body, '<i4', npart, 44)
        pts = np.frombuffer(body, '<f8', npt * 2, 44 + 4 * npart).reshape(npt, 2)
        bounds = list(parts) + [npt]
        At = 0.0; Sx = 0.0; Sy = 0.0
        for a, b in zip(bounds[:-1], bounds[1:]):
            A, x, y = ring_centroid(pts[a:b])
            At += A; Sx += A * x; Sy += A * y
        if At != 0:
            cx[i] = Sx / At; cy[i] = Sy / At
        else:
            cx[i], cy[i] = pts[:, 0].mean(), pts[:, 1].mean()
        if d['area'][i] >= BIG_KM2:
            off = geom.tell()
            geom.write(parts.astype('<i4').tobytes()); geom.write(pts.astype('<f8').tobytes())
            big_rows.append((i, bbox[0], bbox[1], bbox[2], bbox[3], off, npart, npt))
        if i % 200000 == 0:
            log('lakes', 'shapes %d/%d, %.0f s' % (i, n, time.time() - t0))
    geom.close()
    b = np.array(big_rows)
    with open(WORK + '/lakes_big_index.npz', 'wb') as fo:
        np.savez(fo, rec=b[:, 0].astype(np.int64), bbox=b[:, 1:5], off=b[:, 5].astype(np.int64),
                 npart=b[:, 6].astype(np.int64), npt=b[:, 7].astype(np.int64), id=d['id'][b[:, 0].astype(np.int64)],
                 area=d['area'][b[:, 0].astype(np.int64)], name=d['name'][b[:, 0].astype(np.int64)])
    with open(WORK + '/lakes_small.npz', 'wb') as fo:
        np.savez(fo, id=d['id'], area=d['area'], type=d['type'], lon=cx, lat=cy)
    with open(WORK + '/lakes_small.npz.done', 'w') as fo:
        fo.write('%d lakes, %d big, %.0f s\n' % (n, len(big_rows), time.time() - t0))
    log('lakes', 'shapes done: %d big lakes, %.0f s' % (len(big_rows), time.time() - t0))


def fill_tile(lat0, lon0, rings):
    """15" shares (bytes) of a 1-degree tile covered by the rings (even-odd), from S x S sample points a cell."""
    N = 240 * S
    lat_n = lat0 + 1.0
    E = []
    for xy in rings:
        v = (lat_n - xy[:, 1]) * N - 0.5            # sample row coordinate: row r is at v = r
        u = (xy[:, 0] - lon0) * N - 0.5             # sample column coordinate
        E.append(np.column_stack([u[:-1], v[:-1], u[1:], v[1:]]))
    E = np.concatenate(E)
    u1, v1, u2, v2 = E.T
    lo = np.minimum(v1, v2); hi = np.maximum(v1, v2)
    r0 = np.clip(np.ceil(lo), 0, N).astype(np.int64); r1 = np.clip(np.ceil(hi), 0, N).astype(np.int64)
    cnt = r1 - r0
    keep = cnt > 0
    u1, v1, u2, v2, r0, cnt = u1[keep], v1[keep], u2[keep], v2[keep], r0[keep], cnt[keep]
    if len(cnt) == 0:
        return None
    e = np.repeat(np.arange(len(cnt)), cnt)
    r = r0[e] + (np.arange(cnt.sum()) - np.repeat(np.cumsum(cnt) - cnt, cnt))
    ux = u1[e] + (r - v1[e]) * (u2[e] - u1[e]) / (v2[e] - v1[e])
    o = np.lexsort((ux, r))
    r = r[o]; ux = ux[o]
    assert len(r) % 2 == 0 and np.array_equal(r[0::2], r[1::2]), 'odd crossings'
    ca = np.clip(np.ceil(ux[0::2]), 0, N).astype(np.int64); cb = np.clip(np.ceil(ux[1::2]), 0, N).astype(np.int64)
    rr = r[0::2]
    m = cb > ca
    diff = np.zeros((N, N + 1), np.int32)
    np.add.at(diff, (rr[m], ca[m]), 1); np.add.at(diff, (rr[m], cb[m]), -1)
    cover = np.cumsum(diff[:, :N], axis=1)
    assert cover.min() >= 0 and cover.max() <= 1
    share = cover.reshape(240, S, 240, S).sum(axis=(1, 3), dtype=np.int32)
    return np.round(share / float(S * S) * 255).astype(np.uint8)


def phase_shares():
    if os.path.exists(WORK + '/lakes_share.idx.done'):
        return
    ix = np.load(WORK + '/lakes_big_index.npz')
    bbox = ix['bbox']
    t_lo = np.floor(bbox[:, 1]).astype(int); t_hi = np.floor(bbox[:, 3]).astype(int)       # lat
    s_lo = np.floor(bbox[:, 0]).astype(int); s_hi = np.floor(bbox[:, 2]).astype(int)       # lon
    tiles = {}
    for k in range(len(bbox)):
        for la in range(max(-90, t_lo[k]), min(89, t_hi[k]) + 1):
            for lo in range(max(-180, s_lo[k]), min(179, s_hi[k]) + 1):
                tiles.setdefault(tile_id(la, lo), []).append(k)
    log('lakes', '%d tiles touched by %d big lakes' % (len(tiles), len(bbox)))
    geom = open(WORK + '/lakes_big_geom.bin', 'rb')
    def rings_of(k):
        geom.seek(int(ix['off'][k]))
        np_, nt = int(ix['npart'][k]), int(ix['npt'][k])
        parts = np.frombuffer(geom.read(4 * np_), '<i4')
        pts = np.frombuffer(geom.read(16 * nt), '<f8').reshape(nt, 2)
        b = list(parts) + [nt]
        return [pts[a:c] for a, c in zip(b[:-1], b[1:]) if c - a >= 3]
    out = open(WORK + '/lakes_share.bin', 'wb')
    idx = []
    t0 = time.time(); area = 0.0
    cache = {}
    for n_done, (t, ks) in enumerate(sorted(tiles.items())):
        lat0, lon0 = tile_of(t)
        rings = []
        for k in ks:
            if k not in cache:
                cache[k] = rings_of(k)
            rings += cache[k]
        if len(cache) > 2000:
            cache.clear()
        sh = fill_tile(lat0, lon0, rings)
        if sh is None or not sh.any():
            continue
        blob = zlib.compress(sh.tobytes(), 6)
        idx.append((t, out.tell(), len(blob))); out.write(blob)
        cell_km2 = (6371.0072 * np.pi / 180 / 240) ** 2 * np.cos(np.radians(lat0 + 1 - (np.arange(240) + 0.5) / 240))
        area += float((sh.astype(np.float64) / 255 * cell_km2[:, None]).sum())
        if n_done % 1000 == 0:
            log('lakes', 'shares: tile %d/%d, %.0f s, lake area so far %.0f km2' % (n_done, len(tiles), time.time() - t0, area))
    out.close()
    with open(WORK + '/lakes_share.idx', 'wb') as fo:
        np.save(fo, np.array(idx, np.int64))
    with open(WORK + '/lakes_share.idx.done', 'w') as fo:
        fo.write('%d tiles with lake shares; rasterised area %.0f km2 (HydroLAKES Lake_area of these lakes %.0f km2); %.0f s\n'
                 % (len(idx), area, ix['area'].sum(), time.time() - t0))
    log('lakes', 'shares done: %d tiles, area %.0f km2 against %.0f km2 listed, %.0f s' % (len(idx), area, ix['area'].sum(), time.time() - t0))


if __name__ == '__main__':
    phase_shapes()
    phase_shares()
