"""The manufacturing margin study's prototype (MA1, docs/studies/manufacturing-margin.md, section 6).

    python -I docs/studies/manufacturing-margin/proto.py SHARE OUTDIR
    python -I docs/studies/manufacturing-margin/proto.py rule OUTDIR [census] [crew]

Writes into OUTDIR a copy of src/main/resources/buildings.json in which each Manufacturing and
Automotive works but the Wire & Cable Works (which has its own since 0.7.133) buys "consumables",
and a copy of Sector.java whose estimatedMonthlyProfit weighs a Manufacturing or Automotive works'
consumables as the wire works' own estimate does (SpreadPlanner.ownCosts: nameplate times the
operating rate times the exchange factor). With a number, the consumables are SHARE times what its
industry buys that the game has no line for (BEA's use tables, 2017, 2022 and 2023, report.py), of its
sales at the literals' mid-band prices: the study's way (a), "the rest at its industry's share". With
"rule", they are what its industry buys outside (net of what it buys from itself) less what the game
books on a line of its own (power, water, freight, repairs, rent, finance) less the works' own goods
at the literals' mid-band prices: way (b), "its purchases at its industry's". With "census" too, each
works' build (its cashCost, constructionPoints and constructionMaterials together) is cut so its cash
cost is its industry's gross depreciable assets over a year's shipments (the 2022 Economic Census,
ASTEND over RCPTOT, census.py) of a year's sales at mid-band; never raised. With "crew", the Machine
Works' posts, and its recipe changes' undos with them, are cut to a machine shop's sales a worker (the
census's 332710, US$197.4 thousand a year) at mid-band. run.sh's PATCH=OUTDIR
compiles the class over the snapshot and lays the file ahead of it. Run from the checkout's root.
"""
import json
import sys

# Each industry, BEA's summary use tables, the mean of 2017, 2022 and 2023 over output net of what it buys from itself
# (report.py): what it buys that the game has no line for (to two places, as the runs had it: .2247, .3840, .3823);
# what it buys outside; what of that the game books on a line of its own.
BEA = {"332": (.22, .5330, .0727), "333": (.38, .5510, .0497), "3361MV": (.38, .6663, .0663)}
# Each works: its industry; units it makes a month; its good's mid-band literal price, 2025 k$ (a one-sided good its
# one side, as WorldChain reads it).
WORKS = {
    49: ("332", 1200, 1.545),       # Fabrication Shop: fabricated metal (332), fabricated steel
    51: ("332", 3000, 1.545),       # Fabrication Works
    50: ("333", 180, 10.75),        # Machine Works: machinery (333)
    65: ("3361MV", 120, 37.3125),   # Vehicle Works: motor vehicles, bodies and trailers, and parts (3361MV), cars
    66: ("3361MV", 500, 37.3125),   # Assembly Plant
    67: ("3361MV", 150, 68.135),    # Commercial Vehicle Plant, vans
    68: ("3361MV", 4, 2302.2),      # Locomotive Works, rolling stock (BEA's 3364OT; the vehicle makers' shares stand in)
    140: ("3361MV", 6, 215.0),      # Heavy Truck Plant, trucks
}
# Each industry's gross depreciable assets at the year's end over a year's shipments, the 2022 Economic Census
# (332 fabricated metal, 333 machinery, 3361 motor vehicles; census-2022-basic.csv).
ASSETS = {"332": .4072, "333": .3278, "3361MV": .2852}
# A machine shop's shipments a worker a year, US$ thousands (the census's 332710, 2022).
SHOP_SALES_A_WORKER = 197.4
MACHINE_WORKS = 50
# The goods the works buy, mid-band literal prices, 2025 k$ a unit (Good's two sides' mean).
MID = {"STEEL": .56, "FABRICATED_STEEL": 1.545, "MACHINERY": 10.75, "LUBRICANTS": (.0009637 + .0008209) / 2}
OLD = """        if (t == null) return maker;
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        return maker - ham.citybuildersim.sectors.ProcessingSector.cityLegs(t, rate, legPrice());
    }"""
NEW = """        if (t == null) return maker;
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        // MA1's prototype: a Manufacturing or Automotive works' consumables weighed as the wire works' are.
        double own = markets != null && (key.equals(Sectors.MANUFACTURING) || key.equals(Sectors.AUTOMOTIVE))
                ? ham.citybuildersim.sectors.SpreadPlanner.ownCosts(t, rate, markets.getExchangeRate(), 0) : 0;
        return maker - ham.citybuildersim.sectors.ProcessingSector.cityLegs(t, rate, legPrice()) - own;
    }"""


def main(mode, out, *flags):
    data = json.load(open("src/main/resources/buildings.json", encoding="utf-8"))
    for b in data["buildings"]:
        w = WORKS.get(b["id"])
        if not w:
            continue
        industry, units, price = w
        sales = units * price
        own = sum(q * MID[g] for g, q in b.get("uses", {}).items()) / sales
        rest, bought, elsewhere = BEA[industry]
        share = bought - elsewhere - own if mode == "rule" else float(mode) * rest
        b["consumables"] = round(share * sales, 1)
        print("%-26s goods %4.1f%%  consumables %4.1f%%  %8.1f k$ a month of %.0f" % (b["name"], 100 * own, 100 * share, b["consumables"], sales))
        if "census" in flags:
            k = min(1.0, ASSETS[industry] * 12 * sales / b["cashCost"])
            print("%-26s build cost %.0f -> %.0f k$ (%.2f -> %.2f of a year's sales)" % (
                b["name"], b["cashCost"], k * b["cashCost"], b["cashCost"] / 12 / sales, k * b["cashCost"] / 12 / sales))
            b["cashCost"] = round(k * b["cashCost"])
            b["constructionPoints"] = round(k * b["constructionPoints"])
            b["constructionMaterials"] = round(k * b["constructionMaterials"])
        if "crew" in flags and b["id"] == MACHINE_WORKS:
            posts = sum(b["jobs"].values())
            k = sales * 12 / SHOP_SALES_A_WORKER / posts
            b["jobs"] = {j: round(n * k) for j, n in b["jobs"].items()}
            for c in b.get("recipeChanges", []):
                jobs = c.get("without", {}).get("jobs")
                if jobs:
                    c["without"]["jobs"] = {j: n * k for j, n in jobs.items()}
            print("%-26s posts %d -> %d" % (b["name"], posts, sum(b["jobs"].values())))
    with open(out + "/buildings.json", "w", encoding="utf-8", newline="\n") as fh:
        json.dump(data, fh, indent=1, ensure_ascii=False)
    src = open("src/main/java/ham/citybuildersim/Sector.java", encoding="utf-8").read()
    if src.count(OLD) != 1:
        sys.exit("Sector.estimatedMonthlyProfit moved: the prototype's patch no longer applies")
    with open(out + "/Sector.java", "w", encoding="utf-8", newline="\n") as fh:
        fh.write(src.replace(OLD, NEW))


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2], *sys.argv[3:])
