r"""Builds the boards page (the artifact "Central Bank Study") from boards.src.html
and the model's figures.

    python -I cb-model.py --json      writes boards.json
    python -I build-boards.py         writes central-bank-study.html

The page is static: the figures go in where the template says /*DATA*/null.
"""
import csv, json, os

HERE = os.path.dirname(os.path.abspath(__file__))
EPOCH_YEAR = 2000          # CityCalendar.EPOCH_YEAR: game month 1 is January 2000
SHORT = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]


def when(m):
    """CityCalendar.formatShort()."""
    e = max(0, m - 1)
    return "%s %d" % (SHORT[e % 12], EPOCH_YEAR + e // 12)


def main():
    data = json.load(open(os.path.join(HERE, "boards.json")))
    # the city twenty years on, from the copy played on the proposed rule (every 2 months)
    rows = list(csv.DictReader(open(os.path.join(HERE, "halifax-240-every2.csv"))))
    decisions = []
    prev = None
    for r in rows:
        d = float(r["dial"])
        if prev is not None and r["decision"]:
            decisions.append({"when": when(int(r["month"])), "from": prev, "to": d, "kind": r["decision"],
                              "inflation": float(r["inflation"])})
        prev = d
    last = rows[-1]
    data["now"] = {"month": int(last["month"]), "when": when(int(last["month"])), "dial": float(last["dial"]),
                   "inflation": float(last["inflation"]),
                   "next": when(int(last["month"]) + 1), "decisions": decisions[-6:],
                   "held": sum(1 for r in rows if r["decision"] == "") }
    data["whenOf"] = {str(m): when(m) for m in range(1, 800)}
    src = open(os.path.join(HERE, "boards.src.html"), encoding="utf-8").read()
    out = src.replace("/*DATA*/null", json.dumps(data, separators=(",", ":")))
    with open(os.path.join(HERE, "central-bank-study.html"), "w", encoding="utf-8", newline="\n") as f:
        f.write(out)
    print("wrote central-bank-study.html (%d bytes)" % len(out))


if __name__ == "__main__":
    main()
