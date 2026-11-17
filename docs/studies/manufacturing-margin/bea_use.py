"""BEA's use tables for the manufacturing margin study (MA1, docs/studies/manufacturing-margin.md).

    python -I docs/studies/manufacturing-margin/bea_use.py SUMMARY.xlsx OUT.csv [YEAR ...]
    python -I docs/studies/manufacturing-margin/bea_use.py DETAIL.xlsx OUT.csv 2017

The workbooks are BEA's input-output use tables before redefinitions at producers' prices, from
    https://apps.bea.gov/industry/iTables%20Static%20Files/AllTablesIO.zip
(IOUse_Before_Redefinitions_PRO_1997-2023_Summary.xlsx, IOUse_Before_Redefinitions_PRO_2017_Detail.xlsx).
They are untrusted downloads: keep them in a folder of their own and run this with -I. Read with the
standard library alone (an xlsx is a zip of XML). For each industry in INDUSTRIES, each year's column:
every commodity row it buys, the value added rows and total industry output, US$ millions, one CSV
row each, with the row over the industry's output; report.py reads it.
Source: U.S. Bureau of Economic Analysis (US public domain; docs/studies/licences.md).
"""
import csv
import re
import sys
import zipfile
import xml.etree.ElementTree as ET

NS = {"m": "http://schemas.openxmlformats.org/spreadsheetml/2006/main",
      "r": "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
      "p": "http://schemas.openxmlformats.org/package/2006/relationships"}
INDUSTRIES = ["331", "332", "333", "3361MV", "3364OT", "331110", "332310", "332320", "332710",
              "333120", "33361A", "336111", "336112", "336120", "336300", "336310", "336320", "336350",
              "336360", "336370", "336390", "3363A0", "336500", "336510", "33631A"]


def col(ref):
    n = 0
    for ch in re.match(r"[A-Z]+", ref).group(0):
        n = n * 26 + ord(ch) - 64
    return n - 1


def sheets(path):
    z = zipfile.ZipFile(path)
    strings = []
    if "xl/sharedStrings.xml" in z.namelist():
        for si in ET.fromstring(z.read("xl/sharedStrings.xml")).findall("m:si", NS):
            strings.append("".join(t.text or "" for t in si.iter("{%s}t" % NS["m"])))
    wb = ET.fromstring(z.read("xl/workbook.xml"))
    rels = {r.get("Id"): r.get("Target") for r in ET.fromstring(z.read("xl/_rels/workbook.xml.rels")).findall("p:Relationship", NS)}
    for s in wb.find("m:sheets", NS).findall("m:sheet", NS):
        target = rels[s.get("{%s}id" % NS["r"])].lstrip("/")
        if not target.startswith("xl/"):
            target = "xl/" + target
        yield s.get("name"), z, target, strings


def grid(z, target, strings):
    rows = []
    for row in ET.fromstring(z.read(target)).find("m:sheetData", NS).findall("m:row", NS):
        out = {}
        for c in row.findall("m:c", NS):
            v = c.find("m:v", NS)
            if v is None:
                t = c.find("m:is", NS)
                val = "".join(x.text or "" for x in t.iter("{%s}t" % NS["m"])) if t is not None else None
            elif c.get("t") == "s":
                val = strings[int(v.text)]
            else:
                val = v.text
            out[col(c.get("r"))] = val
        rows.append(out)
    return rows


TOTALS = {"Total Intermediate": "T005", "Total Value Added": "T006", "Total Industry Output": "T018"}


def code_of(r):
    """A row's code: column A, or for BEA's total rows (no code) the code their label stands for."""
    code = str(r.get(0, "") or "").strip()
    return code or TOTALS.get(str(r.get(1, "") or "").strip(), "")


def num(x):
    try:
        return float(x)
    except (TypeError, ValueError):
        return 0.0


def main(path, out, years):
    with open(out, "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["year", "industry", "industry_name", "row_code", "row_name", "musd", "over_output"])
        for name, z, target, strings in sheets(path):
            if years and name not in years:
                continue
            if not re.fullmatch(r"\d{4}", name):
                continue
            g = grid(z, target, strings)
            head = next(i for i, r in enumerate(g) if any(str(v).strip() in INDUSTRIES for v in r.values()))
            codes = {k: str(v).strip() for k, v in g[head].items() if v is not None}
            names = {k: str(v).strip() for k, v in g[head + 1].items() if v is not None}
            cols = {k: c for k, c in codes.items() if c in INDUSTRIES}
            total = {}
            for r in g[head + 2:]:
                if code_of(r) == "T018":
                    for k in cols:
                        total[k] = num(r.get(k))
            for k, c in cols.items():
                for r in g[head + 2:]:
                    code = code_of(r)
                    if not code:
                        continue
                    v = num(r.get(k))
                    if v == 0:
                        continue
                    w.writerow([name, c, names.get(k, ""), code, str(r.get(1, "") or "").strip(), round(v, 1),
                                round(v / total[k], 5) if total.get(k) else ""])


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2], set(sys.argv[3:]))
