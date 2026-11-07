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

{ENSEMBLE}

## 5. Checks

{CHECKS}
