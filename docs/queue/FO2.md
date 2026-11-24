# FO2 - the opening policies: a popup at founding

Jerus, 2026-11-24 08:49 (mail, urgent), answering again BO1-S's question of
2026-11-19 (the founding's retired have no income until someone sets the
pension; OP1 lets the player set it from month 1): "For your tests yes, for the
player just when they click found city a popup appears that of you want to put
up some policies before game start, aka pension food assistance amd ei,
subsidy. like same as in policy acreen just immediately available the pmayer
can just click and set or just skip and done". At 08:52: "Sorry i forgot these
past two questions were already asmwered". The "yes" was BO1-S's owner line,
switched on in 0.9.162; the popup is new, and queued here.

## What is true today

- `ui/FoundingScreen` founds the city ("Found the city", "Found with
  defaults"). The promises (pensions, EI, food assistance, subsidies) are set
  only afterwards, on `ui/PolicyScreen`'s PROMISES tab
  (`POLICY_PROMISE_PAGES`: Pensions, Out of work, Food, Health, Schools,
  Subsidies), where each dial is a dial card (`Levers.dialCard()`). OP1
  (0.9.161) opened every policy from the first month.

## The popup

- On "Found the city" (and "Found with defaults"), before the first month, a
  popup offers the four promises Jerus names: pensions, food assistance, EI
  ("Out of work") and subsidies.
- It uses the policy screen's own controls, not a copy: the popup draws the
  PROMISES pages' dial cards, so a value set there is the policy screen's
  value, and every figure comes from the model's public getters.
- The player sets any of them and starts, or skips and starts with today's
  defaults. Nothing else about the founding changes, and the test players are
  untouched.

## Bounds

- A harness causes both paths: set a promise and start (the value holds in the
  first month and on the policy screen), and skip (today's founding,
  unchanged).
- The promises' money is the model's own: no new payer or payee.
- A promise set at founding is saved as the policy screen's is; no format bump
  unless a new field is saved.
- The screen is drawn through `Kept` (`ui/Kept.java`'s rules). The
  screen-check session gives the popup an eye check (docs/orchestrator.md).
