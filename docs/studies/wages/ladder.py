"""The ladder after cost (docs/studies/spec-wages.md, section 2.5): for each
qualification, the wage it pays and the same worker's pay over a working life
once the school years, the fees, the interest and the wages given up while
studying are taken off, as a level month's wage.

Read from WageProbe's CSV, one month at a time:

    python -I ladder.py run.csv 60 600 1200 2400

The rules, each the game's own where it has one:
  - working life: AgeBand.ADULT, 18 to 70, 624 months; a course is taken from 18
    and shortens the working life by its length (EducationType.months());
  - high school: 60 months as a teen, before 18, no wage given up (teens hold
    no posts), the fee paid the whole time;
  - college and university: from a diploma; professional schools from a degree;
  - the fee: Education.feeFor() less the city's share (getTuitionSubsidy());
  - what a holder earns: the best-paid job they can hold, as Education.returnOn()
    reads it (bandWage(); for a degree the best UNGATED job, a licence its own
    job); every wage held at this month's level for the whole life (a level,
    not a forecast);
  - money at the student loan's rate (TaxPolicy.getStudentLoanRate()), what a
    student pays to borrow, or the city's policy rate where that is dearer, and
    never under FLOOR_RATE (the loan's default is 0% and the policy rate sits at
    0% for long spells, and time is never free to a family): ★est.
The level month's wage A is V / a, V the value at 18 and a the annuity of 624
months, so A of no diploma is its wage exactly.
"""
import csv
import sys

WORK = 624  # AgeBand.ADULT, 18 to 70
FLOOR_RATE = .03  # ★est.: a year; a family's own price of waiting when money is free

STEPS = [
    # name, from, months, fee column, wage columns a holder may take
    ("No diploma", None, 0, None, ["w_nodip"]),
    ("Diploma", "No diploma", 60, "fee_high", ["w_dip"]),
    ("College", "Diploma", 24, "fee_college", ["w_colh", "w_colb", "w_cole"]),
    ("University", "Diploma", 48, "fee_university", ["w_sci", "w_pol"]),
    ("Medicine", "University", 84, "fee_medical", ["w_doc"]),
    ("Law", "University", 36, "fee_law", ["w_law"]),
    ("Business", "University", 24, "fee_business", ["w_fin"]),
    ("Engineering", "University", 48, "fee_engineering", ["w_tech"]),
]


def annuity(n, r):
    return n if r <= 0 else (1 - (1 + r) ** -n) / r


def ladder(row):
    r = max(0.0, float(row["loan_rate"]), float(row["policy_rate"]), FLOOR_RATE) / 12
    s = float(row["subsidy"])
    wage = {name: max(float(row[c]) for c in cols) for name, _, _, _, cols in STEPS}
    out = {}
    study_before = {}  # months of adult study before a path's own course
    for name, frm, months, feecol, _ in STEPS:
        w = wage[name]
        if frm is None:
            study_before[name] = 0
            v = w * annuity(WORK, r)
            out[name] = dict(wage=w, after=w, months=0, fees=0.0, given_up=0.0, interest=0.0)
            continue
        fee = float(row[feecol]) * (1 - s)
        if name == "Diploma":
            # Paid as a teen, before 18: carried to 18 at r.
            fees_at_18 = fee * annuity(months, r) * (1 + r) ** months
            fees_plain = fee * months
            start = 0
            given_up_pv = 0.0
            given_up = 0.0
        else:
            start = study_before[frm]
            # While studying, the holder of `frm` gives up its wage.
            before = wage[frm]
            disc = (1 + r) ** -start
            fees_at_18 = fee * annuity(months, r) * disc
            fees_plain = fee * months
            given_up_pv = before * annuity(months, r) * disc
            given_up = before * months
        # High school is a teen's, before the working life starts; the rest are adults'.
        study_before[name] = 0 if name == "Diploma" else start + months
        working = WORK - study_before[name]
        earn = w * annuity(working, r) * (1 + r) ** -study_before[name]
        v = earn - fees_at_18 - given_up_pv
        # What the delay and the borrowing cost, beyond the plain sums: the interest.
        plain = w * working - fees_plain - given_up
        interest = max(0.0, (plain - v / annuity(WORK, r) * WORK) if r > 0 else 0.0)
        out[name] = dict(wage=w, after=v / annuity(WORK, r), months=months,
                         fees=fees_plain, given_up=given_up, interest=interest)
    return out


def main():
    rows = list(csv.DictReader(open(sys.argv[1])))
    want = {int(x) for x in sys.argv[2:]}
    for row in rows:
        m = int(float(row["m"]))
        if m not in want:
            continue
        lad = ladder(row)
        base = lad["No diploma"]["after"]
        print(f"month {m}: money at {max(float(row['loan_rate']), float(row['policy_rate']), FLOOR_RATE):.2%} a year, the city pays {float(row['subsidy']):.0%} of fees;"
              f" the floor {float(row['floor']):.5g}")
        print(f"  {'step':12} {'paid':>9} {'after cost':>11} {'x no dip.':>9} {'months':>6} {'fees':>9} {'given up':>9}")
        for name, v in lad.items():
            print(f"  {name:12} {v['wage']:9.4g} {v['after']:11.4g} {v['after'] / base:9.2f}"
                  f" {v['months']:6} {v['fees']:9.4g} {v['given_up']:9.4g}")


if __name__ == "__main__":
    main()
