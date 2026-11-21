"""Each Manufacturing and Automotive works' month at the literals' prices (MA1, docs/studies/manufacturing-margin.md, 5).

    python -I docs/studies/manufacturing-margin/literal.py [WAGE]

Reads src/main/resources/buildings.json (run from the checkout's root) and prints, for each works, its month at
nameplate in 2025 k$: its sales, its goods, its payroll at WAGE US$ a post a year (default 60,000: what the
probe's cities paid a Manufacturing or Automotive worker at m2400, 49-62 thousand, against the census's 62 thousand
for fabricated metal and 72-79 thousand for machinery and cars), and what is left before its upkeep, power and
taxes, three ways: with no consumables (main), with proto.py's "rule" (its industry's purchases) and with its "1"
(the rest at its industry's share). Each at the literals' mid-band and at the exporter's corner: its good at its
export side (an import-only good at its import side: it sells at home, in place of an import) and what it buys at
the import side (fabricated steel, export-only, at its one side).
"""
import json
import sys

# Good's literal sides, 2025 k$ a unit: (import, export); None where the good has no such side.
SIDES = {"STEEL": (.650, .470), "FABRICATED_STEEL": (None, 1.545), "MACHINERY": (13.4, 8.1),
         "CARS": (41.3125, 33.3125), "VANS": (68.135, None), "ROLLING_STOCK": (2302.2, None), "TRUCKS": (215.0, None),
         "LUBRICANTS": (.0009637, .0008209)}
# proto.py's industries and BEA's shares: no line in the game; bought outside; booked elsewhere in the game.
BEA = {"332": (.22, .5330, .0727), "333": (.38, .5510, .0497), "3361MV": (.38, .6663, .0663)}
INDUSTRY = {49: "332", 51: "332", 50: "333", 65: "3361MV", 66: "3361MV", 67: "3361MV", 68: "3361MV", 140: "3361MV"}


def mid(g):
    a, b = SIDES[g]
    return (a + b) / 2 if a is not None and b is not None else (a if a is not None else b)


def sell(g):
    a, b = SIDES[g]
    return b if b is not None else a


def buy(g):
    a, b = SIDES[g]
    return a if a is not None else b


def main(wage):
    data = json.load(open("src/main/resources/buildings.json", encoding="utf-8"))
    print("| works | posts | sales a post a year, 2025 US$ | where | sales (k$ a month) | goods | payroll | left, no consumables | consumables, rule | left | consumables, the rest | left |")
    print("|---|---|---|---|---|---|---|---|---|---|---|---|")
    for b in data["buildings"]:
        if b["id"] not in INDUSTRY:
            continue
        rest, bought, elsewhere = BEA[INDUSTRY[b["id"]]]
        posts = sum(b["jobs"].values())
        (good, units), = b["makes"].items()
        sales_mid = units * mid(good)
        goods_mid = sum(q * mid(g) for g, q in b["uses"].items())
        rule = (bought - elsewhere) * sales_mid - goods_mid      # k$ a month, fixed at nameplate as the file's field is
        other = rest * sales_mid
        pay = posts * wage / 12 / 1000
        for where, sales, goods in (("mid-band", sales_mid, goods_mid),
                                    ("corner", units * sell(good), sum(q * buy(g) for g, q in b["uses"].items()))):
            pc = lambda x: "%.1f%%" % (100 * x / sales)
            print("| %s | %d | %s | %s | %.0f | %s | %s | %s | %s | %s | %s | %s |" % (
                b["name"], posts, "{:,}".format(int(round(sales * 12 / posts * 1000, -3))), where, sales, pc(goods), pc(pay),
                pc(sales - goods - pay), pc(rule), pc(sales - goods - pay - rule), pc(other), pc(sales - goods - pay - other)))


if __name__ == "__main__":
    main(float(sys.argv[1]) if len(sys.argv) > 1 else 60000)
