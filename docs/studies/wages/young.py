"""The young city's ladder (docs/studies/spec-wages.md, 2.3): for each WageProbe
CSV matched, in months 24-240 and 240-2400, how often a diploma pays the same as
none and how often that is one joined market above the floor; how far college's
wage sits from its target; and how often a university job pays less than a
college one, with or without scientist and policy posts.

    python -I young.py "OUT/today-s*.csv"
"""
import csv, sys, glob, os, statistics as st
for p in sorted(glob.glob(sys.argv[1])):
    rows = [{k: float(v) if v else float('nan') for k, v in r.items()} for r in csv.DictReader(open(p))]
    for lo, hi in ((24, 240), (240, 2400)):
        span = [r for r in rows if lo <= r['m'] < hi]
        eq = [r for r in span if abs(r['w_dip'] / r['w_nodip'] - 1) < .01]
        above = [r for r in eq if r['w_nodip'] > r['floor'] * 1.02]
        lag = [r['w_sci'] / r['t_sci'] - 1 for r in span if r['t_sci'] > 0 and r['p_sci'] > 0]
        lagc = [r['w_colb'] / r['t_colb'] - 1 for r in span if r['t_colb'] > 0 and r['p_colb'] > 0]
        far = sum(1 for x in lagc if abs(x) > .10)
        print(f"{os.path.basename(p):14} m{lo}-{hi}: dip=none {len(eq)}/{len(span)}, of which above the floor (one joined market) {len(above)};"
              f" college wage vs target: median {100*st.median(lagc) if lagc else float('nan'):+.1f}%, >10% apart in {far} months")

    rows = [{k: float(v) if v else float('nan') for k, v in r.items()} for r in csv.DictReader(open(p))]
    span = [r for r in rows if 24 <= r['m'] < 240]
    col = lambda r: max(r['w_colh'], r['w_colb'], r['w_cole'])
    uni = lambda r: max(r['w_sci'], r['w_pol'])
    inv = [r for r in span if uni(r) < col(r)]
    noposts = [r for r in span if r['p_sci'] + r['p_pol'] <= 0]
    inv_noposts = [r for r in inv if r['p_sci'] + r['p_pol'] <= 0]
    inv_t = [r for r in span if max(r['t_sci'], r['t_pol']) < max(r['t_colh'], r['t_colb'], r['t_cole'])]
    dipnone = [r for r in span if abs(r['w_dip']/r['w_nodip'] - 1) < .01]
    print(os.path.basename(p), f"months {len(span)}: uni<col {len(inv)} (of which no sci/pol posts {len(inv_noposts)}; at target {len(inv_t)}); no sci/pol posts {len(noposts)}; dip=none {len(dipnone)}; first inversion m{int(inv[0]['m']) if inv else '-'}", 
          (f"e.g. m{int(inv[0]['m'])}: col {col(inv[0]):.4g} uni {uni(inv[0]):.4g} floor {inv[0]['floor']:.4g} mult_uni {inv[0]['mult_uni']:.3g} mult_col {inv[0]['mult_col']:.3g} posts_uni {inv[0]['posts_uni']:.3g} p_doc {inv[0]['p_doc']:.3g}" if inv else ''))
