# earth-2's sea depth from GEBCO 2026 (public domain, credit): the layers' arithmetic, used by bake2.py and the study.
#   shallow   15", the share of a cell's sea no deeper than SHALLOW_M, in 16 levels: GEBCO bilinear between its cell
#             centres at 4 x 4 points a cell (q = the points' share above -SHALLOW_M), and the sea taken to fill the
#             deepest part of the cell, so of earth-1's sea share s the shallow part is clamp((q - (1 - s)) / s, 0, 1).
#   depth     DEPTH_CPD a degree (1'), a byte a cell: the depth of the cell's sea (GEBCO elevation below 0, averaged over
#             the cell's 15" cells weighted by their earth-1 sea share), coded by code_depth().
import os, sys, zlib
import numpy as np
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import terrain as T

SHALLOW_M = 30.0       # the deepest sea a bridge's piers stand in (the study, section 4: est.)
SUB = 4                # points a side a 15" cell is sampled at for the shallow share
DEPTH_CPD = 60         # the depth layer's cells a degree (1')
Q = 15


DEPTH_FINE_M = 200          # 1 m steps to here (platforms stand to sectors.Oil.PLATFORM_MAX_DEPTH_M, 150 m)
DEPTH_COARSE_M = 10         # then 10 m steps...
DEPTH_DEEP = 255            # ...to 750 m; this code is 750 m or deeper (the abyss is one code: it compresses to nothing)


def code_depth(d):
    """Metres below the sea's level to a byte: 0 under 0.5 m (or no sea); 1 to 200 the metres; 201 to 254 are
    200 m + 10 m a step; 255 is 750 m or deeper."""
    d = np.asarray(d, np.float64)
    v = np.where(d < 0.5, 0, np.where(d <= DEPTH_FINE_M, np.round(d), DEPTH_FINE_M + np.round((d - DEPTH_FINE_M) / DEPTH_COARSE_M)))
    return np.clip(v, 0, DEPTH_DEEP).astype(np.uint8)


def decode_depth(v):
    """A code's depth in metres (255: 750, the least it may be)."""
    v = np.asarray(v, np.float64)
    return np.where(v <= DEPTH_FINE_M, v, DEPTH_FINE_M + (v - DEPTH_FINE_M) * DEPTH_COARSE_M)


def subsample(gp, k=SUB):
    """A padded 15" tile (242 x 242, north first) bilinear at k x k points in each of its 240 x 240 inner cells:
    float32 (240 k, 240 k)."""
    g = gp.astype(np.float32)
    off = (np.arange(k) + 0.5) / k - 0.5                     # point offsets from the cell centre, in cells
    pos = (np.arange(240)[:, None] + 1 + off[None, :]).ravel()    # positions in the padded grid
    i0 = np.floor(pos).astype(int); f = (pos - i0).astype(np.float32)
    rows = g[i0] * (1 - f)[:, None] + g[i0 + 1] * f[:, None]
    return rows[:, i0] * (1 - f)[None, :] + rows[:, i0 + 1] * f[None, :]


def shallow_share(gp, sea15, limit=SHALLOW_M, k=SUB, sub=None):
    """The share (0..1) of each 15" cell's sea no deeper than limit; 0 where the cell has no sea. sea15 is earth-1's
    sea level, 0..15; sub, subsample(gp, k) when the caller has it."""
    sub = subsample(gp, k) if sub is None else sub
    q = (sub > -limit).reshape(240, k, 240, k).mean(axis=(1, 3))
    s = sea15.astype(np.float32) / Q
    with np.errstate(divide='ignore', invalid='ignore'):
        sh = np.clip((q - (1 - s)) / np.maximum(s, 1e-6), 0, 1)
    return np.where(s > 0, sh, 0).astype(np.float32)


def q4(x):
    return np.floor(np.asarray(x) * Q + 0.5).astype(np.uint8)


def sea_depth(gp, sea15, cpd=DEPTH_CPD):
    """The mean depth (m) of the sea in each cell of a cpd-a-degree grid over the tile, weighted by earth-1's sea share;
    0 where no sea. GEBCO at or above 0 under earth-1's sea counts as 0 m."""
    d = np.maximum(0, -gp[1:-1, 1:-1].astype(np.float32))
    w = sea15.astype(np.float32)
    k = 240 // cpd
    sw = w.reshape(cpd, k, cpd, k).sum(axis=(1, 3))
    sd = (d * w).reshape(cpd, k, cpd, k).sum(axis=(1, 3))
    with np.errstate(divide='ignore', invalid='ignore'):
        return np.where(sw > 0, sd / np.maximum(sw, 1e-9), 0).astype(np.float32)


def blob_size(a, level=9):
    """The bytes a share tile costs in earth-1's style: 0 when uniform (its value in the index), else its zlib stream."""
    a = np.ascontiguousarray(a, np.uint8)
    if a.min() == a.max():
        return 0
    return len(zlib.compress(a.tobytes(), level))
