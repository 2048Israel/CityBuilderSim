# FX4 - notes

*0.8.10, 2026-10-19. [The brief](FX4.md). Diagnosed on scratch copies of
Jerus's autosave of 12:12 (a new Earth city, month 205, 0.8.8) and of slot 2
(0.8.6, month 10,572), read and never written.*

## The cause

It is not a regression in FR2, FR7, FR3, FR5 or FR6. The code is the same on 0.8.4.

- His new city's history reads `fxParity` 0.0000 from **month 2**, with the
  price index at 1.000 and the rate at 0.996. The rate then falls steadily
  (0.40 at month 18, 0.12 at 34, 0.036 at 205), pulled toward parity by the
  level-PPP term in `repriceCurrency()`. Trade is near nothing all along, so
  no flow is driving it.
- The save carries `denomination` [10,000,000, 3]: three currency reforms, in
  a city whose price index never left 1.0, which cannot reform
  (`Denomination.UNLOCK_AT` is 10). Slot 2, his long Halifax city saved on
  0.8.6, carries exactly that unit. Its saved parity, 8.13e-8, is
  `parityBase` 1e-7 × 0.904 / 1.116.
- **The mechanism.** `Game.denomination` is a final field. `buildWorld()`, the
  door that New Game, the load path and the constructor all go through,
  never reset it. The load path re-seeds the money constants
  (`seedConstants(unit)` on fifteen objects) only when the unit is not 1, and
  `ForeignAccounts.reset()` puts parity back but not `parityBase`. So a city
  founded in a window that had loaded a reformed city kept that city's unit
  and its constants. `parity = parityBase × index / world` read 1e-7, and the
  rate ran toward it. An unreformed save loaded after a reformed one would
  have kept the constants as well, because the unit is restored as 1 and
  then not re-seeded.
- Why it looked like 0.8.8: he loaded slot 2 and then founded a new city in
  the same window. An app opened fresh founds at unit 1. The playtest and the
  ensemble found every city in a new `Game`, which is why they never saw it.

## The fix

`Game.buildWorld()` now opens by putting the money back at the founding unit
when the unit is not 1: `denomination.reset()`, then `seedMoneyConstants(1)`.
That method is the load path's list of fifteen `seedConstants()` calls, made
into one method that both places call. It runs first, so whatever the
founding sets afterwards is set on top of it. The load path then restores the
saved unit and re-seeds, as before. At a unit of 1 nothing has been seeded
away from 1, so nothing runs, and a city never reformed plays exactly as it
did before.

## The harness

**NewGameCheck 15, a new city after a reformed one.** A city is run for three
months and reformed a hundredfold (`reformCurrencyForTest(FACTORS[1])`).
Then New Game in the same `Game`, run for 24 months beside a city never
reformed. The section asserts four things:
- the fixture: the first city's unit is no longer 1;
- the new city founds at unit 1, with no reforms;
- its parity, and its rate, sit within 5% of the plain city's.

On 0.8.8's `Game.java`, three FAIL: parity 0.0097 against 0.9744, and rate
0.8814 against 0.9967 after two years. That was a hundredfold reform; his was
ten million fold. With the fix, the two cities read the same to four places:
0.9744 and 0.9967.

## Process

The diagnosis and the fix were written by Opus, not handed to a Sonnet
subagent: the PC was going off at 12:45, and the change is about twenty lines.

## The checks

On the merged tree (FR6 in), `Batch check.bat --maps`: 120 harnesses, 119 passed. The one red is ScaleCheck's "...and it builds", which is red on main too. Stale: 0 firm findings. The playtest's trace is byte-identical to main's (896f3f5) except its run-time line (93 s against 94 s), and every month passes the audit, so there is no re-baseline. `--timing`, alone and hard: 8 harnesses, 7 passed. The one red is ScaleCheck's "...and it builds" again, not a timing figure.
