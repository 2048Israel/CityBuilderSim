"""BEA's industries set beside the game's books (MA1, docs/studies/manufacturing-margin.md).

    python -I docs/studies/manufacturing-margin/report.py use-summary.csv [YEARS]

use-summary.csv is bea_use.py's output for the summary use table. For each industry the game's two
sectors stand for (fabricated metal 332, machinery 333, motor vehicles and parts 3361MV), each year in
YEARS (default 1997, 2007, 2017, 2022, 2023), over the industry's output net of what it buys from
itself (the game's works make their own parts; a car plant here is the assembler and its suppliers
in one): what it buys outside, split into the goods the game's works buy (MODELLED), what the game
books on a line of its own (ELSEWHERE: power and water, repairs, freight, rent, finance), and the
rest, which the game has no line for; then compensation, taxes on production and the operating
surplus. Source: U.S. Bureau of Economic Analysis (US public domain; docs/studies/licences.md).
"""
import csv
import sys
from collections import defaultdict

# The goods the game's works buy, by the BEA commodity they stand for.
MODELLED = {"332": ["331"], "333": ["331"], "3361MV": ["331", "332", "333"]}
# What the game books on a line of its own: utilities (power, water), construction (repairs), transport and
# warehousing (freight legs), real estate and leasing (rent and land), finance and insurance (interest).
ELSEWHERE = ["22", "23", "481", "482", "483", "484", "486", "487OS", "493", "HS", "ORE", "532RL", "521CI", "523",
             "524", "525"]


def main(path, years):
    d = defaultdict(dict)
    for r in csv.DictReader(open(path, encoding="utf-8")):
        d[(r["year"], r["industry"])][r["row_code"]] = float(r["musd"])
    print("| industry | year | output net of own (US$bn) | bought outside | of it the modelled goods | booked elsewhere in the game | no line in the game | compensation | taxes on production | operating surplus |")
    print("|---|---|---|---|---|---|---|---|---|---|")
    for ind in ("332", "333", "3361MV"):
        for y in years:
            c = d.get((y, ind))
            if not c:
                continue
            own = c.get(ind, 0)
            net = c["T018"] - own
            outside = c["T005"] - own
            mod = sum(c.get(k, 0) for k in MODELLED[ind])
            els = sum(c.get(k, 0) for k in ELSEWHERE)
            rest = outside - mod - els
            f = lambda x: "%.1f%%" % (100 * x / net)
            print("| %s | %s | %.0f | %s | %s | %s | %s | %s | %s | %s |" % (
                ind, y, net / 1000, f(outside), f(mod), f(els), f(rest), f(c.get("V001", 0)), f(c.get("V002", 0)),
                f(c.get("V003", 0))))


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2].split(",") if len(sys.argv) > 2 else ["1997", "2007", "2017", "2022", "2023"])
