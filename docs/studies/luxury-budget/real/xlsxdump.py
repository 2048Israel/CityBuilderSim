"""python -I xlsxdump.py file.xlsx [maxrows]: prints sheet names and the first rows."""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import xlsxlib
d = xlsxlib.sheets(sys.argv[1])
n = int(sys.argv[2]) if len(sys.argv) > 2 else 15
for k, rows in d.items():
    print("SHEET", k, len(rows))
    for r in rows[:n]:
        print([c[:30] for c in r[:6]], "...", [c[:12] for c in r[-3:]])
