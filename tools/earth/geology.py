# USGS World Energy Project geologic provinces (WEP_PRVG; use constraints "None"): 1,023 polygons rasterised at
# 0.25 degree, each province classed by its name with a first-match rule list, and its known petroleum (KWN_PET,
# million barrels of oil equivalent). Ported from proto/geology.py unchanged: the same rules, the same order of
# drawing (largest province first, so a smaller one inside it shows), the same grid (rows from the north).
# The class is the rule list's ("auto"); E3 adds the hand-checked table (spec-earth 4: 219 provinces are "other").
# Writes work/provinces025.npy (int32, 720 x 1440, 0 = no province, else record + 1) and work/provinces.tsv.
import sys, os, re, collections
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *
import numpy as np, shapefile
from PIL import Image, ImageDraw

SHP = src('usgs/WEP_PRVG/WEP_PRVG.SHP')
RULES = [  # first match wins (proto/geology.py)
    ('ocean', r'abyssal|oceanic|ocean basin|plain$|rise$|ridge$|trench|fracture zone|seamount|deep\b|OCS$'),
    ('shield', r'shield|craton|massif|precambrian|cratonic|block$|nucleus|archean'),
    ('fold belt', r'fold|thrust|orogen|mountains|range|cordillera|alps|andes|himalaya|belt$|highland|arc\b|volcanic|uplift'),
    ('rift', r'rift|graben|trough'),
    ('shelf', r'shelf|slope|margin|offshore|delta|embayment|platform|coastal'),
    ('basin', r'basin|depression|syneclise|foredeep|foreland|plain|lowland|sag|monocline|arch|saddle|zone|sea$|gulf'),
]
CLASSES = ['ocean', 'shield', 'fold belt', 'rift', 'shelf', 'basin', 'other']


def main():
    r = shapefile.Reader(SHP)
    cls = []; kwn = []; names = []; codes = []
    for rec in r.iterRecords():
        n = rec['NAME'] or ''
        c = 'other'
        for k, pat in RULES:
            if re.search(pat, n, re.I):
                c = k; break
        cls.append(c); names.append(n); kwn.append(max(0.0, float(rec['KWN_PET']))); codes.append(int(rec['CODE']))
    W, H = 1440, 720
    img = Image.new('I', (W, H), 0); d = ImageDraw.Draw(img)
    order = sorted(range(len(r)), key=lambda i: -r.record(i)['AREA'])
    for i in order:
        s = r.shape(i); pts = s.points; parts = list(s.parts) + [len(pts)]
        for a, b in zip(parts[:-1], parts[1:]):
            xy = [((x + 180) * 4, (90 - y) * 4) for x, y in pts[a:b]]
            if len(xy) >= 3:
                d.polygon(xy, fill=i + 1)
    grid = np.array(img, np.int32)
    with open(WORK + '/provinces025.npy', 'wb') as f:
        np.save(f, grid)
    with open(WORK + '/provinces.tsv', 'w', encoding='utf-8') as f:
        for i in range(len(cls)):
            f.write('%d\t%d\t%s\t%s\t%.1f\n' % (i + 1, codes[i], names[i].replace('\t', ' '), cls[i], kwn[i]))
    log('geology', 'classes', dict(collections.Counter(cls)), 'cells with a province', int((grid > 0).sum()))


if __name__ == '__main__':
    main()
