# A reader for earth-1.bin (the reference for the game's EarthData; FORMAT.txt has the layout in words).
import sys, os, struct, zlib, json
import numpy as np


class Earth:
    def __init__(self, path):
        self.f = open(path, 'rb')
        h = self.f.read(16)
        assert h[:8] == b'EARTH-1\n', h[:8]
        self.version, n = struct.unpack('>ii', h[8:16])
        self.layers = {}
        for _ in range(n):
            ln, = struct.unpack('>H', self.f.read(2)); name = self.f.read(ln).decode('utf-8')
            kind, cpd, io_, il, do, dl = struct.unpack('>biqiqq', self.f.read(1 + 4 + 8 + 4 + 8 + 8))
            self.layers[name] = dict(kind=kind, cpd=cpd, index_off=io_, index_len=il, data_off=do, data_len=dl)
        for name, L in self.layers.items():
            if L['kind'] in (0, 1):
                self.f.seek(L['index_off'])
                e = np.frombuffer(zlib.decompress(self.f.read(L['index_len'])), '>i4').astype(np.int64)
                L['entries'] = e
                L['offsets'] = L['data_off'] + np.concatenate([[0], np.cumsum(np.maximum(e, 0))[:-1]])

    def raw(self, name, i=0):
        """A tile's (or band's, or a whole layer's) inflated bytes; None when the tile holds no blob."""
        L = self.layers[name]
        if L['kind'] == 2:
            self.f.seek(L['data_off']); return zlib.decompress(self.f.read(L['data_len']))
        e = int(L['entries'][i])
        if e <= 0:
            return None
        self.f.seek(int(L['offsets'][i])); return zlib.decompress(self.f.read(e))

    def grid(self, name, i):
        """A share layer's tile (or band) as a uint8 array, uniform tiles filled with their value."""
        L = self.layers[name]; cpd = L['cpd']
        shape = (cpd, cpd) if L['kind'] == 0 else (cpd, 360 * cpd)
        b = self.raw(name, i)
        if b is None:
            return np.full(shape, -int(L['entries'][i]), np.uint8)
        return np.frombuffer(b, np.uint8).reshape(shape)

    def meta(self):
        return json.loads(self.raw('meta'))

    def landmass(self, t):
        """earth-2: a tile's landmass ids, uint32 (240, 240), 0 no land (FORMAT-2.txt)."""
        L = self.layers['landmass']
        e = int(L['entries'][t])
        if e <= 0:
            return np.full((240, 240), -e, np.uint32)
        b = self.raw('landmass', t)
        pos = 0; vals = []
        def uv():
            nonlocal pos
            n = 0; s = 0
            while True:
                x = b[pos]; pos += 1; n |= (x & 0x7f) << s; s += 7
                if not x & 0x80: return n
        n = uv(); prev = 0
        for _ in range(n):
            prev += uv(); vals.append(prev)
        dt = np.uint8 if n <= 256 else np.dtype('>u2')
        idx = np.frombuffer(b[pos:], dt).reshape(240, 240)
        return np.array(vals, np.uint32)[idx]

    def slope_est(self, t):
        """earth-2: whether a tile's slope shares are GEBCO's estimate rather than NASADEM's measure."""
        b = self.raw('slope.est')
        return bool((b[t // 8] >> (7 - t % 8)) & 1)

    @staticmethod
    def _varints(b):
        out = []; n = 0; s = 0
        for x in b:
            n |= (x & 0x7f) << s
            if x & 0x80:
                s += 7
            else:
                out.append(n); n = 0; s = 0
        return out

    def rivers(self, t):
        """A tile's river runs: a list of (n, 4) arrays of lat, lon (degrees), width (m), GRWL flag."""
        b = self.raw('rivers', t)
        if b is None:
            return []
        lat0 = t // 360 - 90; lon0 = t % 360 - 180
        pos = [0]
        def uv():
            n = 0; s = 0
            while True:
                x = b[pos[0]]; pos[0] += 1; n |= (x & 0x7f) << s; s += 7
                if not x & 0x80: return n
        def zz():
            n = uv(); return (n >> 1) ^ -(n & 1)
        out = []
        for _ in range(uv()):
            n = uv(); a = np.zeros((n, 4)); pa, po, pw = lat0 * 100000, lon0 * 100000, 0
            for k in range(n):
                pa += zz(); po += zz(); pw += zz(); fl = uv()
                a[k] = (pa / 1e5, po / 1e5, pw, fl)
            out.append(a)
        return out

    def small_lakes(self, t):
        """A tile's small lakes: (lat, lon, area km2) rows."""
        b = self.raw('lakes.small', t)
        if b is None:
            return np.zeros((0, 3))
        lat0 = t // 360 - 90; lon0 = t % 360 - 180
        pos = [0]
        def uv():
            n = 0; s = 0
            while True:
                x = b[pos[0]]; pos[0] += 1; n |= (x & 0x7f) << s; s += 7
                if not x & 0x80: return n
        def zz():
            n = uv(); return (n >> 1) ^ -(n & 1)
        m = uv(); out = np.zeros((m, 3)); pa, po = lat0 * 10000, lon0 * 10000
        for k in range(m):
            pa += zz(); po += zz(); c = b[pos[0]]; pos[0] += 1
            out[k] = (pa / 1e4, po / 1e4, 0.1 * 2 ** (c / 8))
        return out
