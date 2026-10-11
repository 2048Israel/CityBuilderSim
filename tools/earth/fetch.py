# The whole-file sources earth-1 needs, fetched into dl/ unless already there or in the study's data/ (reused).
# The big sources are not fetched here: the stages read them by HTTP range as they go (WorldCover's tiles in
# wc_tiles.py, GRWL's .dbf members in rivers.py, GEBCO's elevation south of 60 S in gebco_antarctic.py).
# Every URL below was checked on 2026-10-08 (sizes in bytes as served then).
import sys, os, zipfile, subprocess
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *

GRWL_ZIP = 'https://zenodo.org/records/1297434/files/GRWL_vector_V01.01.zip?download=1'   # 3,104,998,836 bytes
FILES = [  # (path under dl/ or data/, url, size, members to unpack into that folder)
    ('esa_worldcover_grid.geojson', 'https://esa-worldcover.s3.eu-central-1.amazonaws.com/esa_worldcover_grid.geojson', 543674, None),
    ('hylak/HydroLAKES_polys_v10_shp.zip', 'https://data.hydrosheds.org/file/hydrolakes/HydroLAKES_polys_v10_shp.zip', 820295132, None),
    ('gebco/gebco_2026_tid.zip', 'https://dap.ceda.ac.uk/bodc/gebco/global/gebco_2026/type_identifier_grid/netcdf/gebco_2026_tid.zip?download=1',
     98994297, ['GEBCO_2026_TID.nc']),
    ('ne/ne_10m_populated_places_simple.zip', 'https://naciscdn.org/naturalearth/10m/cultural/ne_10m_populated_places_simple.zip',
     652145, ['pp/']),
    ('usgs/GeologicProvinc.zip', 'https://www.sciencebase.gov/catalog/file/get/60ad2fd7d34e4043c850edb3', None, ['']),
]


def have(rel):
    return os.path.exists(os.path.join(DL, rel)) or os.path.exists(os.path.join(DATA, rel))


def main():
    for rel, url, size, unpack in FILES:
        if have(rel):
            log('fetch', 'have', rel, '(%s)' % ('dl' if os.path.exists(os.path.join(DL, rel)) else "the study's copy")); continue
        dst = os.path.join(DL, rel); os.makedirs(os.path.dirname(dst), exist_ok=True)
        log('fetch', 'downloading', url)
        subprocess.run(['curl', '-sS', '-L', '--retry', '5', '-A', UA, '-o', dst, url], check=True)
        if size: assert os.path.getsize(dst) == size, (rel, os.path.getsize(dst), size)
        if unpack is not None:
            folder = os.path.dirname(dst)
            if rel.startswith('ne/'): folder = os.path.join(folder, 'pp')
            with zipfile.ZipFile(dst) as z:
                z.extractall(folder)
    tail = DL + '/grwl_tail.bin'
    if not os.path.exists(tail) and not os.path.exists(SCRATCH + '/tmp/tail.bin'):
        r = Remote(GRWL_ZIP); n = r.length()
        with open(tail, 'wb') as f:
            f.write(r.read(n - 998836, 998836))
        log('fetch', 'GRWL zip central directory: last 998,836 bytes of', n)
    log('fetch', 'sources ready')


if __name__ == '__main__':
    main()
