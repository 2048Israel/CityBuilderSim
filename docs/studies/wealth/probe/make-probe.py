"""The wealth study's probe (docs/studies/spec-wealth.md section 2.4): a SCRATCH copy of Game.java with
WealthProbe.mark(line, Game.this) after every statement of nextMonth(), so ProbeRun can say which statement moved
the households' savings and the audit's pools. Never applied to the tree.

    python -I make-probe.py src/main/java/ham/citybuildersim/Game.java OUT/Game.java START LENGTH

START and LENGTH are nextMonth()'s line and length from docs/map/Game.md (12235 and 916 on 0.9.2). Then compile
OUT/Game.java, WealthProbe.java, ProbeRun.java and ../WealthRun.java against target/classes into a folder put first
on the classpath, and run ham.citybuildersim.ProbeRun 900 240 (months, first month counted).
"""
import re
import sys

src, dst, start, length = sys.argv[1], sys.argv[2], int(sys.argv[3]), int(sys.argv[4])
L = open(src, encoding="utf-8").read().split("\n")
end = start + length
out, n = [], 0
for i, line in enumerate(L, start=1):
    out.append(line)
    if i == start:
        out.append("WealthProbe.open(Game.this);")
        continue
    if not (start < i < end):
        continue
    t = line.strip()
    if not t.endswith(";") or t.startswith("//") or t.startswith("*"):
        continue
    if re.match(r"(return|throw|break|continue|case|default)\b", t) or "->" in t:
        continue
    j = i
    while j < len(L) and not L[j].strip():
        j += 1
    nxt = L[j].strip() if j < len(L) else ""
    if re.match(r"(else|catch|finally|while)\b", nxt) or nxt.startswith((".", "+", "-", "?", ":", "&&", "||")):
        continue
    out.append("WealthProbe.mark(%d, Game.this);" % i)
    n += 1
open(dst, "w", encoding="utf-8").write("\n".join(out))
print("probes", n)
