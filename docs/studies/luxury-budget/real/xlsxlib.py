"""Minimal stdlib xlsx reader: sheets(path) -> {sheetname: [[cell strings]]}. No third-party packages."""
import re, zipfile
import xml.etree.ElementTree as ET

NS = {"m": "http://schemas.openxmlformats.org/spreadsheetml/2006/main"}

def col(ref):
    s = re.match(r"[A-Z]+", ref).group(0)
    n = 0
    for ch in s:
        n = n * 26 + ord(ch) - 64
    return n - 1

def sheets(path):
    z = zipfile.ZipFile(path)
    ss = []
    if "xl/sharedStrings.xml" in z.namelist():
        for si in ET.fromstring(z.read("xl/sharedStrings.xml")).findall("m:si", NS):
            ss.append("".join(t.text or "" for t in si.iter("{%s}t" % NS["m"])))
    wb = ET.fromstring(z.read("xl/workbook.xml"))
    rels = ET.fromstring(z.read("xl/_rels/workbook.xml.rels"))
    rid = {r.get("Id"): r.get("Target") for r in rels}
    out = {}
    for s in wb.find("m:sheets", NS):
        target = rid[s.get("{http://schemas.openxmlformats.org/officeDocument/2006/relationships}id")]
        target = target.lstrip("/")
        if not target.startswith("xl/"):
            target = "xl/" + target
        rows = []
        for r in ET.fromstring(z.read(target)).find("m:sheetData", NS):
            row = []
            for c in r:
                i = col(c.get("r"))
                while len(row) <= i:
                    row.append("")
                v = c.find("m:v", NS)
                t = c.get("t")
                if t == "s" and v is not None:
                    row[i] = ss[int(v.text)]
                elif t == "inlineStr":
                    row[i] = "".join(x.text or "" for x in c.iter("{%s}t" % NS["m"]))
                elif v is not None:
                    row[i] = v.text
            rows.append(row)
        out[s.get("name")] = rows
    return out
