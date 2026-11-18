# DistressBleedCheck.java - 492 lines · 12 methods · 8 constants · harnesses

`ham/citybuildersim/DistressBleedCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The distress rule for a price taker whose operating losses the bank's shortfall desk is still lending: it sells its plant over
> BusinessInvestment.BLEED_MONTHS once its plant has run at an operating loss DISTRESS_LOSS_MONTHS running while it owes the desk,
> and a quarter of its biggest holding a month only once it is overdrawn with no lender (AQ1).
> 
> WHY: on main's AUTO2 ensemble, seed 2, Business Services lost money 101 months running. The shortfall desk lent it
> $409M of the losses and the sector held every office through all of it, until the desk refused at month 1091; then the
> rule sold a quarter of its biggest holding a month, 6,000 posts in twelve months, and the town went from 8% to 38%
> out of work. The same happened in 8 of 16 seeds. A sector that makes nothing of what it sells and has no demand reading
> (Business Services, Automotive, Manufacturing: Sector.bleedsWhileLent()) has no rule but this one, so the wait for the
> refusal was a wait for a cliff. The first form of the rule read the net-loss count, which interest alone keeps climbing, and
> this month's operating income, and counted a sector living on its own cash as lent; it now reads its own saved
> operating-loss streak (BusinessInvestment.getOperatingLossMonths(), the old metal sold left out) and the desk's principal.
> 
> WHAT THIS HAS TO PROVE (the fixture is a town with eight Contact Centres, then the minimum wage lifted until the
> sector's own operations lose money; nothing is set but the cause):
> 
>   1. the fixture: Business Services loses money on its operations every month while the desk lends it (it owes the desk
>      throughout), its debt rising, until its operating-loss streak reaches DISTRESS_LOSS_MONTHS; and that streak is the
>      run of months its running result (operating income less old metal sold) was below zero
>   2. nothing is sold in any month that ends with fewer than DISTRESS_LOSS_MONTHS months of operating losses
>   3. plant goes while it is still lent: a centre is sold in the month the streak reaches DISTRESS_LOSS_MONTHS, and by
>      DISTRESS_LOSS_MONTHS + BLEED_MONTHS months of it at least half the centres are gone
>   4. never faster than the pace: at most max(1, held / BLEED_MONTHS) a month, on the months the dial names
>   5. a net-loss count carried past the dial by interest sells nothing: the floor lifted so the plant's own result comes
>      back while interest keeps the net count climbing, then raised again with the net count past the dial and the
>      operating streak begun again; in every month the net count is at the dial and the streak under it, nothing is sold
>      (the first form of the rule would have sold), and the centres go when the streak reaches the dial
>   6. overdrawn with no lender, the quarter as before
>   7. only a sector the spare-capacity rule cannot reach bleeds (the three overrides), and, as behaviour, a sector that
>      does not bleed, not overdrawn, sells nothing to the rule however long it has lost money (Freight, run on)
> 
> Not proved here, for want of a fixture that can cause it: that a sector living on its own cash (an operating streak past the
> dial, no principal, cash at or above zero) is not lent and sells nothing. No run of months in a founded town builds the cash
> that would carry two years of losses without the desk (measured: Business Services' cash stayed under $500k across five
> years of profit); the line in the rule is `lent = principal > 0`.
> 
> Every fixture here CAUSES its condition. The counts are never set, nor the cash, nor a statement: the wage floor is
> moved and the months are run.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (6), [Game](Game.md) (4), [Sector](Sector.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingManager](BuildingManager.md) (2), [GameFiles](GameFiles.md) (2), [BusinessServices](BusinessServices.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [OldMetal](OldMetal.md) (1), [PolicyDates](PolicyDates.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 208 | · 1. the fixture: the sector loses money on its operations while it is lent |
| 282 | · 2. nothing is sold below the dial |
| 295 | · 3. plant goes while it is still lent |
| 320 | · 4. never faster than the pace |
| 357 | · 5. a net-loss count carried past the dial by interest sells nothing |
| 429 | · 6. overdrawn with no lender, the quarter as before |
| 446 | · 7. only a sector the spare-capacity rule cannot reach bleeds |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `DistressBleedCheck.CONTACT` | `"Contact Centre"` |  |
| 71 | `DistressBleedCheck.CENTRES` | `8` | The centres the town founds the sector with: eight held is one sold every three months at the dial's pace, so the pace shows. |
| 77 | `DistressBleedCheck.FLOOR_TIMES` | `1.8` | How many times the founding minimum wage the floor is lifted to: past the point the sector's operations lose money, short of the three times at which the desk refuses within two years (measured: x3 loses $250k a month... |
| 84 | `DistressBleedCheck.LIFT_AT` | `12` | The net count at which section 5 lifts the floor: wages come back down over about a dozen months, so lifted this far ahead of DISTRESS_LOSS_MONTHS the sector's operating income crosses zero just as the net count passe... |
| 87 | `DistressBleedCheck.RECOVERED_MONTHS` | `3` | Months running the plant's own result must have held at or above zero (the operating streak at 0, the net count still climbing on interest) before section 5 raises the floor again. |
| 90 | `DistressBleedCheck.MOST_MONTHS` | `120` | Months a run may take before it is given up, whatever the sector does. |
| 92 | `DistressBleedCheck.DISTRESS` | `BusinessInvestment.DISTRESS_LOSS_MONTHS` |  |
| 93 | `DistressBleedCheck.BLEED` | `BusinessInvestment.BLEED_MONTHS` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `static int fails` |  |
| 54 | `static PrintStream out` |  |
| 55 | `static PrintStream quiet` |  |
| 98 | `int count, opCount` | The net-loss streak (interest in it) and the operating-loss streak the bleed reads (AQ1). |
| 99 | `long held, others` |  |
| 101 | `double opInc, oldMetal, running, interest, net, cash, debt` | running is the plant's own result: operating income less the old metal it sold that month. |
| 106 | `final Game city` |  |
| 107 | `final BusinessServices bs` |  |
| 108 | `final BuildingsTemplate contact` |  |
| 109 | `final BusinessInvestment plans` |  |
| 110 | `final BusinessDebtManager desk` |  |
| 111 | `final String key` |  |
| 112 | `double foundingFloor` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 51 | 442 | **type** `public class DistressBleedCheck` | The distress rule for a price taker whose operating losses the bank's shortfall desk is still lending: it sells its plant over BusinessInvestment.BLEED_MONTHS once its plant has run at an operating loss DISTRESS_LOSS_... |
| 57 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 62 | 5 | `static void quietly(Runnable r)` |  |
| 96 | 7 | **type** `static final class Row` | One month of the sector, read at the month's end. |
| 105 | 58 | **type** `static final class Town` | A scratch city with the sector's fixture in it, and what a month reads of it. |
| 114 | 8 | `Town(Game city)` _(in DistressBleedCheck.Town)_ |  |
| 123 | 18 | `Row read()` _(in DistressBleedCheck.Town)_ |  |
| 142 | 4 | `Row month()` _(in DistressBleedCheck.Town)_ |  |
| 148 | 8 | `void raiseFloor()` _(in DistressBleedCheck.Town)_ | The cause: the minimum wage law opened and the floor lifted. |
| 158 | 4 | `void liftFloor()` _(in DistressBleedCheck.Town)_ | The cause taken away again: the floor back where it was, no longer binding. |
| 165 | 18 | `static Town town(String prefix) throws Exception` | A town of houses, shops, power, water and road around CENTRES Contact Centres, a founding two years old. |
| 185 | 3 | `static long stepFor(long held)` | The months the dial lets pass between two sales when this many are held: one building every so many months of the losses. |
| 190 | 6 | `static boolean pastDialSolvent(Town t)` | Whether a sector that does not bleed has lost money DISTRESS_LOSS_MONTHS running and ends the month with cash at or above zero. |
| 197 | 5 | `static String list(List<Long> xs)` |  |
| 203 | 289 | `public static void main(String[] args) throws Exception` |  |

