# PD1 - the drivers' petrol paid once ({next}, 2026-11-07)

## WHERE IT STANDS

**{STATE}** Branch `batch/PD1` from `batch/R0g`'s ship file (RL1, 211c10f1). One slice, drafted by Sonnet 5.5 at
xhigh and reviewed here (71037d15): OilCheck 20 first, run red on R0g's tip, then the mend. The petrol WAS counted
twice, in the households' books and in the cells' cash, so the mend moves every city whose people drive: the
ensemble ran (section 4). `--pick`: {PICK}.

## 1. The finding (R0c's §3, settled)

`Retail.sellFuel()` books each litre as a sale of PETROL to `Trade.HOUSEHOLDS`, so its value is in Retail's
`salesToHouseholds`, which `Game.syncHouseholdAccounts()` handed the households as their shopping. The same month's
petrol, with the fuel duty in it, is their `fuel` line (`households.setCommute()`, Motoring's bill). Both were paid:

- **the books**: `HouseholdAccounts.getSpending()` adds the shopping and the fuel line, so the petrol was in it twice;
- **the cash**: each row's fuel is in its fees (`fees[r]`), and its shopping is `actualShopping[r]`; `Household.settle()`
  takes both out of the cell's month (`afterFixed = disposable - rent - fees - ...`, `gap = spent - afterFixed`). So
  the cells paid the petrol twice too. The audit never saw it: the households are outside the pools, and what Retail
  and the treasury took (once) is what the audit declares. The second payment came out of the households' savings
  and went to nobody.

OilCheck 20's fixture causes it: a 600-house town that drives, the duty set (`DUTY`, as section 17 does, so the bill
and the grocers' sale differ), played until the drivers buy petrol (6 months, no filling stations) and a month on.
On R0g's tip, two lines red, each by exactly the petrol:

    the households' shopping is the grocers' sales to them less the petrol ... FAIL  $7.119341k against $7.113987k, over by $0.005354k (the petrol $0.005354k)
    ...so the shopping and the fuel line foot to who took the money ...         FAIL  $7.126614k against $7.121261k, over by $0.005354k (the petrol $0.005354k)

The other five were green there and are green now: the fixture, the premise (the statement's petrol is the drivers'
bill less the duty, the households' fuel line the whole bill, so the two are the same month), the rows' shopping adds
to the shopping and each row's fees settled hold its fuel, the load path, and the audit. In this town the petrol is
small (0.08% of the shopping: its first cars); the identity is exact to 1e-9, so the gap is about a million times the
tolerance.

## 2. The mend

`Game.syncHouseholdAccounts()`, beside R0c's homes' lamp oil and coal: the statement's PETROL sold at home is taken out
of the shopping, read from the same statement the shopping is (struck and restored on load, so both paths read it),
only when above 0, so a city with no drivers is the shop's sales to the bit. Retail sells PETROL only in `sellFuel()`,
to the households (the slice's grep: everything else touching PETROL is a buyer, or the refiners' sale to Retail), so
the statement's petrol at home is all theirs. One javadoc sentence on `HouseholdAccounts.getFuel()`. Nothing else in
the model moved; the national accounts already counted Retail's sales once (`EconomyManager`'s C), and the fuel line
already carried the duty to the treasury.

## 3. The calls (★)

None. No format bump: nothing saved changes; a save's statement already holds the petrol it subtracts.

## 4. The ensemble

The trace moves. The default playtest on PD1's tip parts from R0g's at month 142 (a write-off added back, 182.29
against 182.28), is a person apart at month 261, and then plays its own 200 years, as a long city does on any change
to its money; the ensemble is what judges it.

The 80 runs (label PD1, the classes of 71037d15, four at once, 13.3 min) against `docs/ensemble/0.9.26.md`, the newest
baseline: **23/26, the same three reds as the baseline**, each about where it was:

| criterion | PD1 | 0.9.26 |
|---|---|---|
| AUTO2 A1 inflation m240-end, a year | 2.35% [2.03 to 2.52], 16/16 in band | 2.36% [1.87 to 2.79], 16/16 |
| AUTO2 A2 longest spell of yoy > 10% (FAIL in both) | 18 months [11 to 38] | 16 [10 to 35] |
| AUTO2 A3 deflation spell (FAIL in both) | 4 [0 to 15] | 4 [0 to 15] |
| ZERO_SLACK S1 inflation at 0% less the twin's (FAIL in both) | 2.17% [1.35 to 5.92], spell 92 months | 2.00% [1.11 to 4.79], spell 107 |
| HIGH R3 inflation m660-720 less the twin | -3.80%, 2 seeds above +1 pt (the most it allows) | -3.52%, 0 above |
| M1 months the money audit failed, every policy | 0 | 0 |
| T2 final population, AUTO2 / ZERO_SLACK / SHOCK / HIGH | 567,766 / 387,966 / 38,240 / 13,924 | 602,188 / 397,895 / 29,413 / 15,097 |

T1 16/16 seeds reach 40,000 people (median m864) and F1 no run with a playtest finding, as before. R3 at its limit is
the line to watch at the next ensemble. The run's `baseline.md` is `docs/ensemble/PD1.md`; its folder (81 MB) is
deleted.

## 5. Checks

- OilCheck 20, seven lines: red on R0g's tip (two, each by exactly the petrol, section 1), green on the mend. OilCheck
  whole: all passed, 10 s. Forms1900Check (R0c's homes' fuel, the same identity), CarCheck, HouseholdCheck and
  SaveFileCheck: all passed.
- `./batch-check.sh --pick` on 71037d15 against R0g's tip (12 harnesses, `Game.java` left to the gate, LongPlaytest in
  its place): **exit 3**, main's recorded LongPlaytest red only (exit 1 with no FAIL line, as `docs/notes/gate.txt`
  records it; its one finding is a building drawn moved at month 2396, map code PD1 does not touch). Stale 0 firm.
