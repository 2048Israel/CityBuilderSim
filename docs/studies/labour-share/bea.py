"""BEA's two tables for the labour-share study (LS1, docs/studies/spec-labour-share.md), from DBnomics' copy.

    python -I docs/studies/labour-share/bea.py share DIR OUT.csv   # GDPbyIndustry-7 -> bea-compensation-share.csv
    python -I docs/studies/labour-share/bea.py go DIR OUT.csv      # GDPbyIndustry-25 -> bea-va-gross-output.csv

DIR holds the pages of DBnomics' series listing as JSON, any file names, fetched with
    curl -s "https://api.db.nomics.world/v22/series/BEA/GDPbyIndustry-7?observations=1&limit=100&offset=N"
for N = 0, 100, ... while N < num_found (396 series for table 7, about 800 for table 25). The
pages are untrusted downloads: keep them in a folder of their own and run this with -I.

"share" writes each industry's components of value added as a percentage of value added,
1997-2025, one row an industry and component. "go" writes, for 2023-2025 summed, each
industry's gross output, value added, compensation and intermediate inputs (US$ bn), and value
added and compensation over gross output: the two halves report.py sets beside the game's.
Source: U.S. Bureau of Economic Analysis (US public domain; docs/studies/licences.md).
"""
import csv
import glob
import json
import sys

COMPONENTS = {"Compensation of employees": "compensation",
              "Taxes on production and imports less subsidies": "taxes on production less subsidies",
              "Gross operating surplus": "gross operating surplus"}
LAST = ("2023", "2024", "2025")


def series(folder):
    """Every series in the folder's pages, once: (industry code, industry, component, {year: value})."""
    seen = {}
    for path in sorted(glob.glob(folder + "/*.json")):
        try:
            docs = json.load(open(path, encoding="utf-8"))["series"]["docs"]
        except (KeyError, TypeError, ValueError):
            continue
        for x in docs:
            name = x["series_name"].replace("�", "–")
            parts = [p.strip() for p in name.split("–")]
            if len(parts) < 3:
                continue
            years = {p: v for p, v in zip(x["period"], x["value"]) if isinstance(v, (int, float))}
            seen[x["series_code"]] = (x["series_code"].rsplit("-", 2)[0], parts[0], parts[1], years)
    return seen.values()


def share(folder, out):
    rows = [(code, ind, COMPONENTS[comp], years) for code, ind, comp, years in series(folder) if comp in COMPONENTS]
    first = min(min(y) for *_, y in rows)
    last = max(max(y) for *_, y in rows)
    span = [str(y) for y in range(int(first), int(last) + 1)]
    w = csv.writer(open(out, "w", newline="", encoding="utf-8"), lineterminator="\n")
    w.writerow(["code", "industry", "component"] + span)
    for code, ind, comp, years in sorted(rows, key=lambda r: (r[0], r[2])):
        w.writerow([code, ind, comp] + [years.get(y, "") for y in span])
    print("rows", len(rows))


def go(folder, out):
    sums, names = {}, {}
    for code, ind, comp, years in series(folder):
        if all(y in years for y in LAST):
            sums.setdefault(code, {})[comp] = sum(years[y] for y in LAST)
            names[code] = ind
    w = csv.writer(open(out, "w", newline="", encoding="utf-8"), lineterminator="\n")
    w.writerow(["code", "industry", "gross_output_2023_25", "value_added", "compensation", "intermediate_inputs",
                "va_over_go", "comp_over_go"])
    n = 0
    for code, r in sorted(sums.items()):
        va, comp, ii = r.get("Value added"), r.get("Compensation of employees"), r.get("Intermediate inputs")
        # The industry's own line (named as the industry) is its gross output.
        gross = r.get(names[code], (va or 0) + (ii or 0))
        if not va or not comp or not gross:
            continue
        w.writerow([code, names[code], round(gross, 1), round(va, 1), round(comp, 1), round(ii or 0, 1),
                    round(va / gross, 4), round(comp / gross, 4)])
        n += 1
    print("industries", n)


if __name__ == "__main__":
    {"share": share, "go": go}[sys.argv[1]](sys.argv[2], sys.argv[3])
