# PD1 - the drivers' petrol paid once ({next}, 2026-11-07)

## WHERE IT STANDS

**Ready.** Branch `batch/PD1` from `batch/R0g`'s ship file (RL1, 211c10f1); main with FD1 merged at 9860749a and with R0g
in (f93d97bd) at 9e89b5ea, the generated docs alone in conflict both times (main's side, regenerated). One slice, drafted by Sonnet 5.5 at
xhigh and reviewed here (71037d15): OilCheck 20 first, run red on R0g's tip, then the mend. The petrol WAS counted
twice, in the households' books and in the cells' cash, so the mend moves every city whose people drive: the
ensemble ran on the merge (section 4): 24/26, one more than FD1's baseline. `--pick` on the last merge: **exit 3**.

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

The 80 runs on the final model, the merge with main (FD1 in; label PD1, the classes of 9860749a, four at once, 18.4
min) against `docs/ensemble/0.9.41.md`, FD1's, the newest baseline: **24/26, one more than the baseline's 23**. A3
passes now (its longest deflation spell 11 months against 15); A2 and S1 are red as they were:

| criterion | PD1 on main | 0.9.41 |
|---|---|---|
| AUTO2 A1 inflation m240-end, a year | 2.35% [1.99 to 2.87], 16/16 in band | 2.34% [1.90 to 2.69], 16/16 |
| AUTO2 A2 longest spell of yoy > 10% (FAIL in both) | 20 months [11 to 33] | 16 [10 to 33] |
| AUTO2 A3 deflation spell, final/peak population | 4 [0 to 11]: PASS | 5 [0 to 15]: FAIL |
| ZERO_SLACK S1 inflation at 0% less the twin's (FAIL in both) | 2.14% [1.35 to 5.11], spell 102 months | 2.00% [1.04 to 4.89], spell 107 |
| HIGH R3 inflation m660-720 less the twin | -3.80%, 2 seeds above +1 pt (the most it allows) | -3.09%, 1 above |
| M1 months the money audit failed, every policy | 0 | 0 |
| T2 final population, AUTO2 / ZERO_SLACK / SHOCK / HIGH | 585,213 / 377,106 / 38,288 / 13,924 | 603,462 / 391,434 / 30,352 / 14,550 |

T1 16/16 seeds reach 40,000 people (median m862) and F1 no run with a playtest finding, as before. A2's median spell
is four months longer, its worst the same; R3 at its limit is the line to watch at the next ensemble. A first run on
R0g's tip, before FD1 (71037d15, against 0.9.26), scored 23/26 with the baseline's three reds; it was not the final
model, so this one is kept. The run's `baseline.md` is `docs/ensemble/PD1.md`; both runs' folders (81 MB each) are
deleted.

## 5. Checks

- OilCheck 20, seven lines: red on R0g's tip (two, each by exactly the petrol, section 1), green on the mend. OilCheck
  whole: all passed, 10 s. Forms1900Check (R0c's homes' fuel, the same identity), CarCheck, HouseholdCheck and
  SaveFileCheck: all passed.
- `./batch-check.sh --pick` on 71037d15 against R0g's tip (12 harnesses, `Game.java` left to the gate, LongPlaytest in
  its place): **exit 3**, main's recorded LongPlaytest red only (exit 1 with no FAIL line, as `docs/notes/gate.txt`
  records it; its one finding is a building drawn moved at month 2396, map code PD1 does not touch). Stale 0 firm.
- `--pick` on the merge with main 071ed050 (FD1 in), 0fc57978: 45 harnesses, **exit 3**, main's recorded reds only
  (MapCheck and LongPlaytest, `docs/notes/gate.txt`). `git merge-tree` with main: the generated docs alone in
  conflict (main's side, regenerated).
- `--pick` on the merge with main f93d97bd (R0g in), 9e89b5ea: 13 harnesses, **exit 3**, main's recorded LongPlaytest red
  only. Since the ensemble's classes (9860749a) the source changed only in `GameVersion.VERSION`, so the run stands.
