# earth-2's slope stage: every NASADEM tile (14,520, 61 N to 56 S: its catalogue has a row at 60-61 N) streamed from the Planetary Computer's public
# mirror (SRTMGL1 v3 from ESA STEP where it has none), its heights averaged to 3" (90 m), the slope by Horn's method
# over that grid, and per 15" cell the histogram of the 25 slopes over terrain.BINS, plus the cell's mean height.
# A tile's download is deleted once read (about 9 MB a tile, 129 GB in all, est. from 80 tiles; nothing is kept).
# Writes <E2>/work/slope/<TILE>.npz (h: uint8 240 x 240 x 20, elev: int16 240 x 240, src) and a .done marker last;
# re-runs skip finished tiles. Downloads run on DL_THREADS threads, the arithmetic on this one.
#   python3 -I slope_tiles.py [threads] [part parts]      one process (the part-th of parts, by tile order)
#   CBS_PROCS=4 python slope_tiles.py 12                  four processes, the threads shared out (the PC's way)
import sys, os, time, json, queue, threading, random
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import numpy as np
import terrain as T

DL_THREADS = int(sys.argv[1]) if len(sys.argv) > 1 else 10
PART, PARTS = (int(sys.argv[2]), int(sys.argv[3])) if len(sys.argv) > 3 else (0, 1)
SLOPE = os.path.join(T.WORK2, 'slope')
TILES_JSON = os.path.join(T.WORK2, 'nasadem_tiles.json')


def tile_list():
    """NASADEM's tiles as (lat0, lon0), from the Planetary Computer's catalogue (cached in work/)."""
    if not os.path.exists(TILES_JSON):
        import urllib.request, ssl
        ctx = ssl.create_default_context(cafile=T.CA) if os.path.exists(T.CA) else None
        url = 'https://planetarycomputer.microsoft.com/api/stac/v1/search'
        body = {'collections': ['nasadem'], 'limit': 1000}; names = []; nxt = (url, body)
        while nxt:
            req = urllib.request.Request(nxt[0], data=json.dumps(nxt[1]).encode(), headers={'Content-Type': 'application/json'})
            with urllib.request.urlopen(req, context=ctx, timeout=120) as r:
                d = json.load(r)
            names += [f['id'] for f in d['features']]
            nxt = next(((l['href'], l.get('body', body)) for l in d.get('links', []) if l.get('rel') == 'next'), None)
        os.makedirs(T.WORK2, exist_ok=True)
        with open(TILES_JSON + '.part', 'w') as f:
            json.dump(sorted(names), f)
        os.replace(TILES_JSON + '.part', TILES_JSON)
    out = []
    for n in json.load(open(TILES_JSON)):
        s = n.split('_')[-1]
        la = int(s[1:3]) * (1 if s[0] == 'n' else -1); lo = int(s[4:7]) * (1 if s[3] == 'e' else -1)
        out.append((la, lo))
    return sorted(out)


def compute(z, lat0):
    z = T.fill_voids(z)
    if z is None:
        return None
    z3 = T.block_mean(z[:3600, :3600], 3)
    pct = T.slope_pct(z3, lat0 + 1 - 1 / 3600.0, 3 / 3600.0)
    h = T.cell_hist(pct, 5).astype(np.uint8)                   # 25 slopes a cell: fits a byte
    elev = np.round(T.block_mean(z[:3600, :3600], 15)).astype(np.int16)
    return h, elev


def main():
    procs = int(os.environ.get('CBS_PROCS', '1'))
    if procs > 1 and PARTS == 1:
        import subprocess
        tile_list()                                            # the catalogue once, before the parts read it
        env = dict(os.environ, CBS_PROCS='1')
        ps = [subprocess.Popen([sys.executable, '-I', os.path.abspath(__file__), str(max(2, DL_THREADS // procs)), str(i), str(procs)],
                               env=env) for i in range(procs)]
        sys.exit(max(p.wait() for p in ps))
    os.makedirs(SLOPE, exist_ok=True)
    todo = [t for i, t in enumerate(tile_list()) if i % PARTS == PART
            and not os.path.exists(os.path.join(SLOPE, T.tile_name(*t, 'srtm') + '.done'))]
    random.Random(1).shuffle(todo)                             # an even mix of mountain and plain, for the rate
    T.log('slope', '%d tiles to do on %d download threads' % (len(todo), DL_THREADS))
    q_in = queue.Queue(); q_out = queue.Queue(maxsize=3 * DL_THREADS)
    for t in todo: q_in.put(t)
    got = [0, 0]

    def worker():
        while True:
            try:
                t = q_in.get_nowait()
            except queue.Empty:
                q_out.put(None); return
            try:
                p = os.path.join(T.DL2, 'nasadem', 'NASADEM_HGT_%s.tif' % T.tile_name(*t))
                if os.path.exists(p) or T.fetch(T.nasadem_url(*t), p):
                    got[0] += os.path.getsize(p); q_out.put((t, p, 'nasadem')); continue
                q2 = os.path.join(T.DL2, 'srtm', '%s.SRTMGL1.hgt.zip' % T.tile_name(*t, 'srtm'))
                if os.path.exists(q2) or T.fetch(T.step_url(*t), q2):
                    got[0] += os.path.getsize(q2); q_out.put((t, q2, 'srtmgl1')); continue
                q_out.put((t, None, None))
            except Exception as e:
                T.log('slope', 'tile %s failed: %r' % (t, e)); q_out.put((t, None, 'failed'))

    for _ in range(DL_THREADS):
        threading.Thread(target=worker, daemon=True).start()
    t0 = time.time(); done = 0; ended = 0; failed = []
    while ended < DL_THREADS:
        item = q_out.get()
        if item is None:
            ended += 1; continue
        t, p, src = item
        name = T.tile_name(*t, 'srtm'); out = os.path.join(SLOPE, name)
        if p is None:
            if src == 'failed': failed.append(t)
            else:
                with open(out + '.done', 'w') as f: f.write('none\n')
            continue
        if src == 'nasadem':
            z = T.read_tif(p)
        else:
            import zipfile
            with zipfile.ZipFile(p) as zf:
                z = np.frombuffer(zf.read(zf.namelist()[0]), '>i2').reshape(3601, 3601).astype(np.int16)
        if not T.KEEP_TILES: os.remove(p)
        r = compute(z, t[0])
        if r is not None:
            np.savez_compressed(out + '.tmp.npz', h=r[0], elev=r[1], src=np.array(src))
            os.replace(out + '.tmp.npz', out + '.npz')
        with open(out + '.done', 'w') as f:
            f.write('%s\n' % (src if r is not None else 'void'))
        done += 1
        if done % 100 == 0:
            dt = time.time() - t0
            T.log('slope', '%d/%d tiles, %.1f GB in %.0f s (%.1f MB/s), %.2f s a tile, eta %.0f min' %
                  (done, len(todo), got[0] / 1e9, dt, got[0] / 1e6 / dt, dt / done, (len(todo) - done) * dt / done / 60))
    T.log('slope', 'finished %d tiles in %.0f s; %.1f GB streamed; failed %s' % (done, time.time() - t0, got[0] / 1e9, failed))
    if failed:
        sys.exit(1)


if __name__ == '__main__':
    main()
