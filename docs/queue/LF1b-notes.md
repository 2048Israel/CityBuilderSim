# LF1b - notes: the cells' share trades with the world, in the financial account (2026-11-16)

The brief: [LF1b.md](LF1b.md), queued 2026-11-08 by LF1 (its notes, 2). Built by a Claude session (Opus 5.5) from main 0f4ebb9c
(0.9.125, LF1c just shipped), after the night's Codex builders ran out of plan; one session, no slices. Main's R1d (0.9.126, its
trace unmoved) was merged in before the final checks.

**This changes how the game plays.** The exchange rate reads the balance of payments' financial account (`ForeignAccounts`:
`-BALANCE_WEIGHT * (current + financial) / GDP`, a 12-month mean). Until now a household selling its shares to the world brought dollars
in that the rate never saw; now it sees them, as it has always seen the households' bonds. In a city whose households sell shares abroad
heavily - on main's default playtest, months 401-800, where those sales were a fifth of all the capital coming in - the currency stays
markedly stronger: the rate 53% lower in the mean month of that era, and with it a different city (it grew slowly while main's boomed on a
falling currency, then overtook it: 1.39M people at m2405 against 0.47M). A city whose households trade little abroad is as it was.
Section 4 has the figures, section 5 the ensemble's verdicts across 80 runs.

## 1. The size, before anything was built

Measured on main's own code, the default playtest (`LongPlaytest`, 2,400 months) with a probe that wrote, after each month, the
exchange's two figures (`Exchange.getHouseholdsSoldAbroad()`, `getHouseholdsBoughtAbroad()`) beside `ForeignAccounts`' month
(`getFinancialIn()`/`getFinancialOut()`) and its trailing means. The probe was a temporary line in `LongPlaytest`, never committed; with
it the run's eight trace CSVs were byte-identical to main's reference (R0h's, `cbs-gate-R0h-NrRv6F33/traces/`), so the probe moved
nothing.

| months | the cells sold shares abroad | bought abroad | the financial account's inflow | outflow | sales / inflow |
|---|---|---|---|---|---|
| 1-400 | 2,585 | 134 | 40,310 | 72,870 | 6.4% |
| 401-800 | 562,800 | 0 | 2.62M | 3.13M | **21.5%** |
| 801-1200 | 425,100 | 59,540 | 5.70M | 16.75M | 7.5% |
| 1201-1600 | 24,160 | 1,326 | 37.6M | 73.7M | 0.06% |
| 1601-2000 | 108,500 | 64,140 | 759M | 4,964M | 0.01% |
| 2001-2400 | 798 | 0 | 386B | 629B | 0.00% |

(Nominal money of the run, the month's own units.) The cells sold to the world in 968 of the 2,404 months and bought from it in 112
(their bonds: sold in 376, bought in 514). Over the whole run the shares' trades were 3.8% of the gross financial flows in the mean
month; the cells' net sale abroad was 0.78% of a month's trailing GDP in the mean month (the balance the rate reads,
`|current + financial| / GDP`, 53%).

**It is not small where it happens.** In months 401-800 the cells' net sales abroad (+562,800) are larger than the whole financial
account's net (-508,400): declared, that era's financial account turns from a deficit to a small surplus. Around m760 the cells sold
25,000-36,600 a month to the world, 41% of a month's trailing GDP at m762, against a financial inflow of 1,800-2,100 a month; months
740-800 sold 536,900 against an inflow of 681,800. The rate reads `-BALANCE_WEIGHT * (current + financial) / GDP` over a 12-month mean
(`ForeignAccounts.COVER_WINDOW`), so that spell alone moves its input by about a tenth of a month's GDP where the balance stood at 3%.
After m1200 the hot money and the bonds dwarf the shares and the pair is noise. Expectation, before building: the currency
stronger through m400-m1200, the trace moved from m32 (the first month a cell sold a share abroad, for 0.000017), the late run as it was
in kind if not to the byte.

## 2. What was built

**The pair (`MoneyAudit`)**, beside the fund's share lines, the same four as the cells' bonds have had since 0.7.12:

| line | scope | what |
|---|---|---|
| `+ shares HouseholdsSoldAbroad` | FINANCIAL (in) | a cell's share sold to the world: dollars in |
| `- shares HouseholdsSoldAbroad (into their savings)` | DOMESTIC (out) | ...into the cells' savings, outside the pools |
| `+ shares HouseholdsBoughtAbroad (from their savings)` | DOMESTIC (in) | a share bought from the world (its long, its short, the month's leavers) out of the savings |
| `- shares HouseholdsBoughtAbroad` | FINANCIAL (out) | ...the dollars out |

Read off `Exchange.getHouseholdsSoldAbroad()`/`getHouseholdsBoughtAbroad()`, the month's sums the settle has kept since 0.7.12 round 2
(`cellSells && worldBuys`, `abroadSells && cellBuys`; the world's short and the emigrants count as abroad, as the desk's own lines count
them). Each pair cancels in the cash identity, so the residual is unchanged, and `ForeignAccounts.takeMonth` reads `financialIn`/
`financialOut` as it always has: nothing else changed. Exchange's getter javadoc names the pair.

★ **The signs follow the bonds, not the brief's words.** The brief says "the sale into the cells' savings DOMESTIC in and FINANCIAL out,
the purchase the other way"; read as scopes that puts the world's payment for a cell's share on the financial *outflow*, against the
bonds' pair it says to copy (`+ bonds HouseholdsSoldAbroad` FINANCIAL, `- bonds HouseholdsSoldAbroad (into their savings)` DOMESTIC) and
against every other line where the world buys (`+ desk SharesSoldAbroad`, `+ fund SharesSoldAbroad`, `+ equity SubscribedAbroad`, all
FINANCIAL credits). I took the bonds' signs: a sale abroad is money arriving, the purchase money leaving.

★ **No save field, no format bump.** The exchange's two figures are month flows cleared by `startMonth()` and unsaved, like LF1c's two; the
audit that reads them is struck in the month that made them, and what the next month's screens read, `ForeignAccounts`' financial in and
out, is already saved (its state's 40 and 41). An old save loads as it did: nothing is read differently.

★ **No screen change.** The Trade screen's "capital in"/"capital out" tips list examples ("borrowed abroad, money parked here, the world
buying the businesses' bonds"), not a breakdown that must add up; their totals now hold the cells' shares. Left as they are.

## 3. The check (`ShortSellingCheck`, section 9 and section 7)

There is no `MoneyAuditCheck`; of the brief's two the section went in `ShortSellingCheck`, beside LF1c's valuation pairs.

**Section 9, `theCellsAbroad()`** (the 48-month town of sections 2-5): the company the ordinary cells hold most of by value
(Construction) and the cell holding most of it. Inside the real month, through `Game.settleProbeForTest` (after the exchange's
`startMonth`, before its step and the audit; CentralBankCheck, FloatLinkCheck and AuctionCheck use it the same way), the fixture rests a
world bid inside the spread the month finds and the cell sells half its holding into it, then a world ask at the same price and the same
cell buys half of that back with what it was paid (the town's savers are poor beside its shares: the richest cell's savings bought 11,
under the month's own financial outflow, so the purchase could not have shown on it). The town makes no cell-to-world trade by itself
(a 24-month probe, none), and the step after the fixture added none. It reads: the shares filled, the exchange's two figures risen by
`q x p`, the cell's savings up by the sale and down by the purchase, the world's holding up by the difference; the four lines equal to the
figures; the residual 0; the sale (175.0) over ten times the month before's whole financial inflow (0.0006) and the purchase (87.5) over
ten times its outflow (5.96); `financialIn >= sale` and `financialOut >= purchase`; `ForeignAccounts`' month equal to the audit's. 21
assertions.

**The control, by hand:** with the four lines commented out of `MoneyAudit` the section shows six failures, the residual still 0 (both
sides are outside the pools, which is why the lines are read directly):

    the audit's line for the cells' sale abroad is the exchange's figure      FAIL  0.000000000 != 174.984697487
    ...with its half into their savings, the same size                        FAIL  0.000000000 != 174.984697487
    the audit's line for the cells' purchase abroad is the exchange's figure  FAIL  0.000000000 != 87.492348744
    ...with its half out of their savings, the same size                      FAIL  0.000000000 != 87.492348744
    the balance of payments' financial inflow holds the sale: $0.0307         FAIL
    ...and its financial outflow the purchase: $17.0178                       FAIL

Restored, 21 of 21.

**Section 7** (the playtest's city, 1,000 months, shorts on) now counts the months the cells traded shares with the world and reads the
four lines against the two figures every month: sold to the world in 336 months (27,702), bought from it in 7 (217,245), no month's
lines off. ShortSellingCheck whole through `batch-check.sh`: exit 0, all checks passed (its two HS1 known reds did not show in this run).

## 4. The trace, and how far the rate moved

The default playtest on the branch at 816ac0e3 (with the uncommitted size probe; it moves nothing, section 1), `-Dplaytest.trace`, against
main's reference (R0h's, `cbs-gate-R0h-NrRv6F33/traces/`): **all eight CSVs differ**, each from month 37-64 on (month from m37, bank
m39, house m39, bonds m41, build m42, labour m52, pop m64; borrow from its second row, 39 papers where main issued 55), the first months
after the cells' first sale abroad (m32, 0.000017). The playtest itself passed (exit 0, no FAIL).

The rate (`trace-month.csv`'s `rate`, local money per dollar: lower is a stronger currency), branch against main:

| months | mean | range |
|---|---|---|
| 1-400 | -0.7% | -7.8% (m169) to +23.0% (m400) |
| 401-800 | **-53.2%** | -75.7% (m785) to +24.0% (m402) |
| 801-1200 | +5.3% | -75.0% (m801) to +131.4% (m1152) |
| 1201-1600 | +107.8% | up to +284.8% (m1536) |
| 1601-2000 | +44.8% | -17.4% (m1996) to +151.8% (m1632) |
| 2001-2400 | -71.7% | down to -81.7% (m2311) |

The first 300 months are the direct effect, small: the rate within a few per cent (m100 1.0209 against 1.0233, m300 1.0642 against
1.0876), the town the same size. By m400 the two cities have parted (population 2,896 against 3,647) and after that the ratio is mostly
the path, not the line: main's city boomed through m400-m800 with its currency falling from 1.28 to 21.0 (population 3,647 to 36,872);
the branch's, whose financial account now held the cells' sales abroad of that era (section 1), kept a stronger currency (1.58 to 5.29)
and grew slowly (2,896 to 5,681), then overtook it from about m1100 and ended at m2405 with 1,393,358 people against 471,729, the rate at
125.4 against 605.1 and the price index 9,395 against 46,342. On the branch the cells sold shares abroad in 1,086 months and bought in
111 (main: 968 and 112). One run is the weather plus the change; the ensemble (section 5) is what judges it.
