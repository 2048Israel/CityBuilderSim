# LF1b - notes: the cells' share trades with the world, in the financial account (2026-11-16)

The brief: [LF1b.md](LF1b.md), queued 2026-11-08 by LF1 (its notes, 2). Built by a Claude session (Opus 5.5) from main 0f4ebb9c
(0.9.125, LF1c just shipped), after the night's Codex builders ran out of plan; one session, no slices.

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
stronger through m400-m1200, the trace moved from about m280 (the first month a cell sold abroad more than a cent), the late run as it was
in kind if not to the byte.
