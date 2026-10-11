# Shared pieces of the earth-1 pipeline: paths, the HTTP range reader (through the session's proxy),
# logging, zigzag varints and the 1-degree tile numbering.
#
# Nothing here deletes or renames over anything: outputs are written once, under E0, and re-runs skip
# finished work (a unit's result is written first, its marker last).
import os, sys, ssl, time, json, http.client, urllib.parse, threading

SCRATCH = '/home/claude/scratch-earth'
DATA = SCRATCH + '/data'                 # what the study downloaded (read only)
E0 = SCRATCH + '/e0'
DL = E0 + '/dl'                          # what this pipeline downloads (kept, counted against the 8 GB)
WORK = E0 + '/work'                      # baked intermediates, a file per unit of work
LOG = E0 + '/log'
OUT = E0 + '/out'
PYLIB = SCRATCH + '/pylib'
if PYLIB not in sys.path:
    sys.path.insert(0, PYLIB)

CA = '/root/.ccr/ca-bundle.crt'
UA = 'curl/8.5.0 (CityBuilderSim earth-1 pipeline)'   # Zenodo answers 403 to a request with no User-Agent

# ------------------------------------------------------------------ the 1-degree tiles
# A tile is named by its south-west corner; its number is (lat0 + 90) * 360 + (lon0 + 180), 0 to 64,799,
# south row first. A 15" layer holds 240 x 240 cells a tile, rows from the north, cells centred.
TILES = 180 * 360
C15 = 240          # 15" cells a degree
C2 = 30            # 2' cells a degree


def tile_id(lat0, lon0):
    return (lat0 + 90) * 360 + (lon0 + 180)


def tile_of(tid):
    return tid // 360 - 90, tid % 360 - 180


# ------------------------------------------------------------------ logging
def log(name, *msg):
    os.makedirs(LOG, exist_ok=True)
    line = time.strftime('%H:%M:%S ') + ' '.join(str(m) for m in msg)
    with open(os.path.join(LOG, name + '.log'), 'a') as f:
        f.write(line + '\n')
    print(line, flush=True)


# ------------------------------------------------------------------ zigzag varints
def varint(buf, n):
    n = (n << 1) ^ (n >> 63)
    while True:
        b = n & 0x7f
        n >>= 7
        if n:
            buf.append(b | 0x80)
        else:
            buf.append(b)
            return


def uvarint(buf, n):
    assert n >= 0
    while True:
        b = n & 0x7f
        n >>= 7
        if n:
            buf.append(b | 0x80)
        else:
            buf.append(b)
            return


# ------------------------------------------------------------------ HTTP range reads
class Remote:
    """Byte ranges of one URL over a kept-alive HTTPS connection through the proxy, with retries.
    Counts every byte received (the stream total is reported; nothing is kept unless the caller writes it)."""
    received = 0
    lock = threading.Lock()

    def __init__(self, url):
        self.url = url
        u = urllib.parse.urlsplit(url)
        self.host = u.hostname
        self.path = u.path + ('?' + u.query if u.query else '')
        self.conn = None
        self.size = None

    def _connect(self):
        proxy = os.environ.get('HTTPS_PROXY') or os.environ.get('https_proxy')
        ctx = ssl.create_default_context(cafile=CA)
        if proxy:
            p = urllib.parse.urlsplit(proxy)
            c = http.client.HTTPSConnection(p.hostname, p.port, context=ctx, timeout=120)
            c.set_tunnel(self.host, 443)
        else:
            c = http.client.HTTPSConnection(self.host, 443, context=ctx, timeout=120)
        self.conn = c

    stale = 0          # requests answered NoSuchBucket and retried on a new connection (S3, through the proxy)

    def _request(self, method, headers):
        attempt = 0; stale = 0
        while True:
            try:
                if self.conn is None:
                    self._connect()
                self.conn.request(method, self.path, headers=dict(headers, **{'User-Agent': UA}))
                r = self.conn.getresponse()
                if r.status in (301, 302, 303, 307, 308):
                    loc = r.getheader('Location'); r.read()
                    nu = urllib.parse.urljoin(self.url, loc)
                    self.__init__(nu)
                    continue
                body = r.read()
                if r.status >= 500 or r.status == 429:
                    raise IOError('HTTP %d' % r.status)
                if r.status == 404 and b'NoSuchBucket' in body and stale < 40:
                    # Measured 2026-10-08: about half the connections to esa-worldcover's S3 endpoint answer every
                    # request with NoSuchBucket, the other half serve the object. A new connection is a new draw.
                    stale += 1
                    with Remote.lock:
                        Remote.stale += 1
                    self.conn.close(); self.conn = None
                    time.sleep(0.1)
                    continue
                if r.status in (403, 404) and attempt < 3:
                    log('http', 'HTTP %d on %s %s; body %r' % (r.status, self.host, headers.get('Range'), body[:300]))
                    raise IOError('HTTP %d' % r.status)
                return r, body
            except (OSError, http.client.HTTPException) as e:
                try:
                    self.conn.close()
                except Exception:
                    pass
                self.conn = None
                attempt += 1
                if attempt >= 8:
                    raise IOError('giving up on %s: %r' % (self.url, e))
                time.sleep(min(60, 2 ** attempt))

    def length(self):
        if self.size is None:
            r, body = self._request('GET', {'Range': 'bytes=0-0'})
            cr = r.getheader('Content-Range')
            self.size = int(cr.split('/')[1]) if cr else len(body)
        return self.size

    def read(self, start, n):
        """n bytes from start (fewer only at the end of the file)."""
        if n <= 0:
            return b''
        r, body = self._request('GET', {'Range': 'bytes=%d-%d' % (start, start + n - 1)})
        if r.status == 404:
            raise FileNotFoundError(self.url)
        if r.status == 200:            # the server ignored the range
            body = body[start:start + n]
        with Remote.lock:
            Remote.received += len(body)
        return body


class Local:
    """The same interface over a local file (the study's downloads are reused this way)."""
    def __init__(self, path):
        self.path = path
        self.f = open(path, 'rb')

    def length(self):
        return os.path.getsize(self.path)

    def read(self, start, n):
        self.f.seek(start)
        return self.f.read(n)


class RangeFile:
    """A read-only, seekable file object over a Remote, with a block cache: lets h5py and pyshp read a
    remote file without downloading it."""
    BLOCK = 1 << 20

    def __init__(self, remote, cache_blocks=64):
        self.r = remote; self.pos = 0; self.cache = {}; self.order = []; self.max = cache_blocks
        self.size = remote.length()

    def seekable(self): return True
    def readable(self): return True
    def tell(self): return self.pos

    def seek(self, off, whence=0):
        self.pos = off if whence == 0 else self.pos + off if whence == 1 else self.size + off
        return self.pos

    def _block(self, b):
        if b not in self.cache:
            self.cache[b] = self.r.read(b * self.BLOCK, self.BLOCK)
            self.order.append(b)
            if len(self.order) > self.max:
                self.cache.pop(self.order.pop(0), None)
        return self.cache[b]

    def read(self, n=-1):
        if n is None or n < 0:
            n = self.size - self.pos
        n = max(0, min(n, self.size - self.pos))
        if n > 4 * self.BLOCK:                       # big reads go straight through
            out = self.r.read(self.pos, n); self.pos += len(out); return out
        out = bytearray()
        while n > 0:
            b, o = divmod(self.pos, self.BLOCK)
            chunk = self._block(b)[o:o + n]
            if not chunk:
                break
            out += chunk; self.pos += len(chunk); n -= len(chunk)
        return bytes(out)

    def readinto(self, buf):
        data = self.read(len(buf)); buf[:len(data)] = data; return len(data)

    def close(self): pass


def src(rel):
    """A whole-file source: this pipeline's download (dl/<rel>, by fetch.py) if there, else the study's copy (data/<rel>)."""
    p = os.path.join(DL, rel)
    return p if os.path.exists(p) else os.path.join(DATA, rel)


def done_marker(path):
    return os.path.exists(path)


def write_json(path, obj):
    with open(path, 'w') as f:
        json.dump(obj, f, indent=1, sort_keys=True)
