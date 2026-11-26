# DistressBleedCheck.java - 550 lines · 18 methods · 11 constants · harnesses

`ham/citybuildersim/DistressBleedCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
> WHAT THIS HAS TO PROVE:
> 
>   1. the streak is the run of months the plant's own result (operating income less old metal sold) was below zero, and it
>      reaches DISTRESS_LOSS_MONTHS after that many losing months; at DISTRESS_LOSS_MONTHS - 1 a lent, solvent sector sells
>      nothing and at DISTRESS_LOSS_MONTHS it sells a centre
>   2. nothing is sold at any operating streak under DISTRESS_LOSS_MONTHS, the sector lent
>   3. plant goes while it is still lent: a centre is sold at the count that reaches DISTRESS_LOSS_MONTHS, and by
>      DISTRESS_LOSS_MONTHS + BLEED_MONTHS months of it at least half the centres are gone
>   4. never faster than the pace: at most max(1, held / BLEED_MONTHS) a month, on the counts the dial names, for every holding
>   5. a net-loss count carried past the dial by interest sells nothing: with the net count at the dial or past it and the
>      operating streak under the dial (back at 0, a loss that is only interest, or begun again), nothing is sold where the first
>      form of the rule (e17ee67f9's: the net count alone, the plant losing money this month) would have sold; and the centres
>      go when the operating streak reaches the dial
>   6. overdrawn with no lender, the quarter as before
>   7. only a sector the spare-capacity rule cannot reach bleeds (the three overrides), and, as behaviour, a sector that
>      does not bleed, lent, not overdrawn, sells nothing to the rule however long it has lost money, where Business
>      Services in the same state sells
>   8. a sector living on its own cash (both streaks past the dial, no principal, cash at or above zero) is not lent and sells
>      nothing; the same sector once the desk lends it sells
> 
> WHAT IS SET, AND WHY (FS9: the played town moved under it): every section reads the rule, so the rule's own inputs are set
> through the model's own doors on a scratch town that holds CENTRES Contact Centres and no month played: the net-loss and
> operating-loss streaks (BusinessInvestment.restoreLossMonths(), restoreOperatingLossMonths()), the shortfall desk's principal
> (BusinessDebtManager.issueLoan()) and the sector's cash (setSectorCash()); the holding moves only by the model's own retire
> (BuildingManager.retire()), as the decision names. The streak's own formula is not set: a statement is written and
> recordOperatingResult() is asked of it, a month each way. The first form of this harness played a wage-floored town for
> its counts and waited, in section 5, for a net count (LIFT_AT) measured on main's town; the town moved under that timing on
> two other batches' paths and the fixture's own premises went red with the rule untouched. Nothing here is a timing: each
> state is the state, and each threshold is read from both sides against the model's constants.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (16), [Sector](Sector.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (5), [Game](Game.md) (4), [OldMetal](OldMetal.md) (4), [BuildingManager](BuildingManager.md) (3), [Statement](Statement.md) (2), [BusinessServices](BusinessServices.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 219 | · 1. the streak, and the dial from both sides |
| 292 | · 2. nothing is sold below the dial |
| 305 | · 3. plant goes while it is still lent |
| 340 | · 4. never faster than the pace |
| 372 | · 5. a net-loss count carried past the dial by interest sells nothing |
| 437 | · 6. overdrawn with no lender, the quarter as before |
| 458 | · 7. only a sector the spare-capacity rule cannot reach bleeds |
| 516 | · 8. a sector living on its own cash is not lent |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `DistressBleedCheck.CONTACT` | `"Contact Centre"` |  |
| 75 | `DistressBleedCheck.CENTRES` | `8` | The centres the town holds: eight held is one sold every two months at the dial's pace, so the pace shows. |
| 77 | `DistressBleedCheck.DISTRESS` | `BusinessInvestment.DISTRESS_LOSS_MONTHS` |  |
| 78 | `DistressBleedCheck.BLEED` | `BusinessInvestment.BLEED_MONTHS` |  |
| 81 | `DistressBleedCheck.NET_PAST` | `DISTRESS + BLEED` | The net-loss streak sections 1 to 4 set beside the operating streak: past the dial and the whole pace after it, so the rule's first question (the net count) is answered yes and only the operating streak decides. |
| 84 | `DistressBleedCheck.LOAN` | `1_000` | What the shortfall desk is lent against the sector: the rule reads only that the principal is above zero. |
| 87 | `DistressBleedCheck.CASH` | `1_000` | The cash a solvent sector is set at: the rule reads only that it is at or above zero. |
| 90 | `DistressBleedCheck.OVERDRAWN` | `- 1_000` | The cash an overdrawn sector is set at: below zero, which is all the quarter's rule reads. |
| 93 | `DistressBleedCheck.LOSS` | `- 50` | The operating income, in thousands, of a month the plant loses money on its operations. |
| 96 | `DistressBleedCheck.PROFIT` | `10` | The operating income, in thousands, of a month the plant's own result is above zero. |
| 99 | `DistressBleedCheck.INTEREST` | `40` | The interest, in thousands, a month's statement carries: more than PROFIT, so a month of operating profit can still lose money in net. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 58 | `static PrintStream out` |  |
| 59 | `static PrintStream quiet` |  |
| 103 | `final Game city` |  |
| 104 | `final BusinessServices bs` |  |
| 105 | `final BuildingsTemplate contact` |  |
| 106 | `final BusinessInvestment plans` |  |
| 107 | `final BusinessDebtManager desk` |  |
| 108 | `final String key` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 55 | 496 | **type** `public class DistressBleedCheck` | The distress rule for a price taker whose operating losses the bank's shortfall desk is still lending: it sells its plant over BusinessInvestment.BLEED_MONTHS once its plant has run at an operating loss DISTRESS_LOSS_... |
| 61 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 66 | 5 | `static void quietly(Runnable r)` |  |
| 102 | 59 | **type** `static final class Town` | A scratch city with the sector's fixture in it, and the doors the rule reads through. |
| 110 | 8 | `Town(Game city)` _(in DistressBleedCheck.Town)_ |  |
| 119 | 3 | `long held()` _(in DistressBleedCheck.Town)_ |  |
| 124 | 8 | `long others()` _(in DistressBleedCheck.Town)_ | What the sector holds of anything but the centres. |
| 134 | 6 | `void holdAt(long n)` _(in DistressBleedCheck.Town)_ | The centres held set to n: the surplus scrapped by the model's own retire, the shortfall stood by addStack. |
| 142 | 4 | `void streaks(String sector, int net, int op)` _(in DistressBleedCheck.Town)_ | Both streaks of one sector set: the restore methods replace the whole map, so the sector asked is the only one that holds a count. |
| 147 | 3 | `void cash(String sector, double cash)` _(in DistressBleedCheck.Town)_ |  |
| 152 | 3 | `void lend(String sector)` _(in DistressBleedCheck.Town)_ | The desk lends the sector LOAN: its principal is above zero from here. |
| 157 | 3 | `BusinessInvestment.Decision ask(Sector s)` _(in DistressBleedCheck.Town)_ | The rule asked as Game asks it each month: the sector's own cash, no order in flight. |
| 163 | 7 | `static Town town(String label)` | A town holding CENTRES Contact Centres and no month played: the rule reads counts, a principal, a cash and a holding, none of which a month builds. |
| 172 | 8 | `static void statement(Town t, double operating, double oldMetal, double interest)` | A month's statement written for the sector: its operating income, the old metal it sold that month (0 for none), the interest on its debt. |
| 182 | 3 | `static long sold(BusinessInvestment.Decision d)` | What a decision sells: its quantity when it builds (sells) at all, else none. |
| 187 | 3 | `static long stepFor(long held)` | The months the dial lets pass between two sales when this many are held: one building every so many months of the losses. |
| 192 | 3 | `static boolean oldRuleSells(int net, double operatingIncome, double cash, boolean lent, long held)` | The first form of the rule (e17ee67f9's): the net count at the dial, this month's operating income below zero, solvent and lent, the step read off the net count. |
| 197 | 7 | `static BuildingsTemplate freightPlant(Town t)` | A building of Freight's the sector may sell and that counts toward its measure: plant for a sector that does not bleed to hold. |
| 205 | 5 | `static String list(List<Long> xs)` |  |
| 212 | 1 | **type** `record Month(String what, double operating, double oldMetal, int streak)` | One month's statement for section 1's streak formula, and the streak it must leave from a streak one short of the dial. |
| 214 | 336 | `public static void main(String[] args) throws Exception` |  |

