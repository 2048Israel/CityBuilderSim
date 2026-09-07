# Money conserved, students in flight — 2026-09-06 (late)

Everything the evening analysis (`analysis-2026-09-06.md`) turned up, built in
one pass. Save format **17 → 18**. 31 source files changed, 3 new
(`MoneyAudit`, `MoneyCheck`, `AllChecks`), 1 to delete by hand
(`MenuManager.java` — I cannot delete on the PC from here; it compiles
harmlessly until you do).

**All 28 harnesses pass, including `BuildMenuCheck` against your JavaFX jars,
and the whole project compiles.** `LongPlaytest` (4,002 months): *"Nothing.
Every month passed the audit and every reload matched."* — and the audit now
includes money.

---

## The headline: money is conserved, and it was not

`MoneyAudit` strikes one identity at the bottom of every `nextMonth()`:

```
pooled(after) - pooled(before)  ==  inflows - outflows
```

where the pools are the treasury, the six sectors' cash, the builder's order
book and one cheque in the post (below), and the flows are every dollar that
crosses the city's boundary — wages out, rent and shopping in, imports out,
exports in, loans and bonds both ways, pensions, fees, upkeep. Internal flows
are deliberately not listed: they cancel if both sides booked the same number
and show up as residual if they did not.

On the first strike, a founding city leaked **16% of everything that moved in
a month**. Four leaks, each found by the residual and closed:

| leak | what it was | closed by |
|---|---|---|
| **Profit tax** | every sector banked its pre-tax profit while the city collected the tax — the same dollars twice (backlog 8, decided: deduct) | sectors bank net of tax; the after-tax line on the statement is what is banked |
| **VAT** | the city collected the ledger's total and nobody was debited; retail paid a purchase markup to nobody; imports were credited and never charged | the producer remits its net out of its own cash (refund credited); retail pays the bare price; an import is charged *and* credited so it nets |
| **Food** | the mills booked local sales as units × *today's* price a month after the shops paid units × *last month's* price | the mills book the shops' cheque (carried in the save); the cheque is a pool while it is in the post |
| **Utilities** | the utility booked the bill at the end-of-month ratio; the customers were charged at the start-of-month one | the utility books what its customers were charged; mining's power bill uses the basis ratio like everyone else |

Two more fell out of chasing it: construction's audit figures had to be
snapshotted at banking because `calculateExpenses()` runs again later in the
month, and the utility's bill had to be re-struck on the load path *after* the
carried statements land — `LongPlaytest` caught that one as "utility income
differs across a save" in five months of four thousand.

**`MoneyCheck`** (harness 29) plays two cities — one prospering with every
sector trading, one broke, banned, taxed at the ceiling and importing all its
food — and demands the residual stay under 0.01% of what moved. It is
**$0.00** in all four scenarios. `LongPlaytest` asserts a cent, every month.

### Balance moved, as expected

Sectors now pay ~15% of profit and their VAT. Same 4,002-month run, before and
after the whole batch:

| | before | after |
|---|---|---|
| population | 82,196 | 81,758 |
| treasury | $70.1B | $80.0B |
| business debt | $12.0B | $16.1B |
| GDP/mo | $28.6M | $25.6M |
| unemployment | 14.1% | 15.8% |
| moved in / left | 300k / 91k | 422k / 207k |

The treasury is up because it now receives what it was already recording;
business debt is up because the sectors now pay it. The churn line is the
adults-only fix (next section), not the taxes — see there. **One seed, one
trajectory; your own rule applies.**

---

## Skilled arrivals were counted 1.7× (children were graduates)

The arrival mix sums to everybody who moved in, of every age, and
`cohorts.migrate()` spreads them across the age bands — but the whole mix was
added to the *adult* skilled counts. Now scaled by the adult share the cohorts
gave the migrants; `LabourCheck` asserts the counts gain the adult share of
the skilled arrivals and not the whole mix. Skilled retirements also use the
healthcare-modified adult mortality rather than the base table, so an unserved
city's graduates die at the same rate as its labourers.

**This is what moved the churn line.** With fewer phantom graduates, more of
the workforce is unskilled, the unskilled band is pinned at the minimum wage
more often, and `SURPLUS_DEPARTURE_RATE` sheds more — arrivals then refill the
gap. Departures went 91k → 207k over 4,002 months in a city of ~80k, about
52 a month. The mechanism is doing what it was specified to do; the old count
was masking it. Whether 2%/month of a pinned surplus is the right rate is now
a question you can ask with honest numbers.

---

## A doctor shortage is priced on doctors

Gated jobs carry a licence premium on top of their band's: posts over licence
holders, same elasticity, never below 1, and the two together never exceed
`MAX_MULTIPLE`. Measured on a city with two hospitals and no medical school:
**licence tightness 2.23, doctor premium 1.26×, graduate band 0.83×** — the
band is in glut, the doctors are not, and the market now says so. The
arrival-licence share and `Education.returnOn` read that price, so the
"import while small" valve opens on a shortage for the first time.
`Migration` and the People screen read the *band* premium off an ungated job
now — the first university job in enum order is `UNIV_DOCTOR`, which would
have made every graduate look dear. The professions table has a premium column.

---

## Education has a pipeline

`Education.inFlight[type][monthsLeft]`: every adult course keeps a queue of
cohorts. Intake joins the back; graduates leave the front `months()` later;
seats bind on the stock, so a full school admits only as many as graduate.
Students thin at the adults' attrition rate. **Full-time students are out of
the labour supply** (your call) — `workforceByBand()` subtracts them, which is
the one place supply is read, so posts, wages, surplus and eligibility all see
it. Set on the load path too, or a reloaded city had more workers than the
live one until its first tick (caught by the new `EducationCheck` section).

Measured: a university built into a city of diploma-holders — **course 48
months, first graduates in month 49, peak 358 students**, and the 590 students
in the fixture city are out of an 8,732 workforce. Before this batch the same
school graduated people the month it opened. A format-17 city loads with
nobody in flight and its schools fill from empty.

---

## Housekeeping, all decided by you tonight

- **Two harnesses were overwriting your real autosave.** `EducationCheck` and
  `LabourCheck` built `new Game()` — the real `%APPDATA%` folder — and ran
  past the 12-month autosave. `GameFiles.scratch()` exists now and every
  harness `Game` uses it; `CreditCheck`, `SaveFileCheck` and `BuildMenuCheck`
  were on the same path and under twelve months, so were safe by luck.
- **Quick-debt screen** quotes the 6-month note it books, not a 3-month bill.
- **Reports and graphs default off.** The toggles stay on the Settings screen.
  The 2 MB log cap was filling with ASCII graphs before the crash you wanted.
- **Dead console UI deleted**: ~520 lines of `Game.java` behind a `getInput()`
  that returned 0, the commented-out T-bill block, `quickIssueDebt`, and
  `MenuManager`. `processBuildOrder()`'s unreachable else-branch now throws
  rather than falling into a stale T-bill issuer.
- **The borrowing ban is one ban.** A restructured sector could not borrow
  to keep the lights on but could borrow to expand; `canBorrow()` now asks the
  ledger, the advisor trims to what cash covers, and the refusal reads
  *"Holding: borrowing ban, N more months"*. `CreditCheck` bankrupts retail by
  hand and asserts it — and fails against the old code.
- **Rent follows the live unskilled wage** — `CommercialHandler.rentFor()`,
  neutral at the default; `LabourCheck` doubles the floor and watches rent.
- **The UI asks `Game` for its numbers.** `Game.quoteBuild()` is the one
  definition of the build quote (`calculateTotalCost()` is its total); the
  mill screen's wage tax is banded and its residents-per-job comes from
  `Migration`, not a hardcoded 2.25; the construction panel's ETA comes from
  `BuildingManager.monthsLeft()`. The red "cash is credited with the pre-tax
  figure" note is gone because it is no longer true.
- **`AllChecks`** runs every harness in its own JVM and exits with the number
  that failed. `java -cp <classes> ham.citybuildersim.AllChecks -q` for
  verdicts only, or a name fragment for a subset. Twenty-eight in 18 s.

Two harnesses had to change their minds: `BooksCheck`'s "tax double-count
(surfaced, not fixed)" now asserts the after-tax figure is banked, and
`PolicyCheck`'s "an importer reselling at cost remits nothing" now asserts it
remits exactly what the local chain does on the same goods — which is the
property its own heading claimed.

---

## Still open, surfaced by this batch

- The churn rate under a pinned unskilled surplus (above) is now measurable
  honestly and worth a look with varied seeds.
- The retail input credit is struck at the supplier's rate on a tax the
  supplier bore out of its margin. Conserved, and what the ledger's own
  design says, but it is a transfer industry → city → retail worth knowing
  about.
- Construction's materials: the payer pays for imported materials into the
  order book *and* the sector expenses the shortfall it imports. Conserved
  (it is a pass-through), but it is the "$30 quoted, $50 paid" gap from a
  different angle.
- `MenuManager.java` wants deleting from the PC.
