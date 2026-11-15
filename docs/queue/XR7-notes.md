# XR7 - The world's pegs overshoot both ways, and the hold capped

Built on main ade96cfb (0.9.115), on batch/XR7. XR8 is folded in.
No known-reds entry names XR7 on this starting main.

## Decisions and scope

- The world's break adds ForeignAccounts.OVERSHOOT times the signed gap,
  as the city's break does. The hazard remains hazard(max(0, gap)): Jerus's
  call moves the landing, not the chance of a strong-side break.
- ★ A chosen peg breaks before a purchase greater than HOLD_PURCHASE_YEARS
  (one year's GDP in a month); a band gives way for that month at the same
  bound. Above this estimate the first bill alone exceeds annual city income.
  Test the lift against interventionMove(bound), before evaluating the
  exponential inverse. Keep the calendar's Canadian-gold peg outside this
  player-peg limit, as its exhaustion rule requires.
- This changes XR5's premise that a purchase never breaks a peg. Its usual
  held-month equations remain laws below the bound. A bound must release the
  hold: buying only part and still claiming the target would hold it for free.
- Main already saves holdFelt in ForeignAccounts slot 147, DataSave's
  foreignAccounts, Game.fillSave(), and Game's loaded foreign.restore().
  Prove a nonzero supported peg crosses those paths and pays only its fading
  support next month. Do not reconstruct missing support or convert old saves.
  Main's oldest readable format is already 53, newer than XR5's support.

## Fixtures before the behaviour change

target/checks/XR7-before.log, regression exit 1, with only the dial and appended
break cause introduced: 3x costs 8.939470420466048 years of output and 5x costs
114.96232536607569; both buy without breaking, the 1.01-bound and band cases fail.
Five seeded world breaks include four weak and one strong; the strong landing
has gap -0.1552103291150757 and excess zero, failing the signed landing and fade.
The same fixtures must turn green; no premise was changed to excuse a failure.

Initial and fixture builds: batch-check.sh --fast passed, Stale 0 firm findings.
