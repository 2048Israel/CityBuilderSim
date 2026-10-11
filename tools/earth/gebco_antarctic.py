# Antarctica: GEBCO codes it type 70, not land (spec-earth 4), and WorldCover stops at 60 S. South of 60 S the
# land is GEBCO 2026's ice-surface elevation >= 0 (public domain), read by HTTP range from the contiguous int16
# grid in CEDA's GEBCO_2026.nc (rows from the south; 1.24 GB streamed, nothing kept but the mask).
# Writes work/antarctic_land.npy: 7,200 rows (90 S to 60 S, south row first) x 86,400 cells, bits packed.
import sys, os, time
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np, h5py

URL = 'https://dap.ceda.ac.uk/bodc/gebco/global/gebco_2026/ice_surface_elevation/netcdf/GEBCO_2026.nc?download=1'
OUTF = WORK + '/antarctic_land.npy'


def main():
    if os.path.exists(OUTF + '.done'):
        return
    r = Remote(URL)
    rf = RangeFile(r, cache_blocks=16); rf.BLOCK = 1 << 18
    f = h5py.File(rf, 'r')
    e = f['elevation']
    assert e.shape == (43200, 86400) and e.chunks is None and e.compression is None
    off = e.id.get_offset(); dt = e.dtype
    lat = f['lat'][:7201]
    assert abs(lat[0] + 90 - 1 / 480) < 1e-6 and abs(lat[7199] - (-60 - 1 / 480)) < 1e-6, lat[[0, 7199]]
    out = np.zeros((7200, 86400 // 8), np.uint8)
    t0 = time.time(); band = 120
    for i in range(0, 7200, band):
        raw = r.read(off + i * 86400 * 2, band * 86400 * 2)
        z = np.frombuffer(raw, dt).reshape(band, 86400)
        out[i:i + band] = np.packbits(z >= 0, axis=1)
        if i % 1200 == 0:
            log('antarctic', 'rows %d-%d of 7200, %.0f s, land so far %.4f' % (i, i + band, time.time() - t0, np.unpackbits(out[:i + band]).mean()))
    with open(OUTF, 'wb') as fo:
        np.save(fo, out)
    with open(OUTF + '.done', 'w') as fo:
        fo.write('streamed %d bytes in %.0f s\n' % (Remote.received, time.time() - t0))
    log('antarctic', 'done: streamed %.2f GB in %.0f s' % (Remote.received / 1e9, time.time() - t0))


if __name__ == '__main__':
    main()
