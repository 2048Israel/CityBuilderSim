## changelog
### {next} — XR5, the currency regimes' rest: the hold pays the pressure, the tied path, the overshoot both ways, a thin vault's warning — 2026-11-06, ready, see docs/queue/XR5-notes.md
Jerus's calls on XR2's ★4 and ★5 and FX8's two. Under a peg or a band the vault no longer meets the month's balance: it
pays what holding the rate takes, the interventions' rule run backwards (XI1's bounded inverse), and what it dealt
fades in the rate at INTERVENTION_HALF_LIFE (holdFelt, saved in slot 147), so a weak D$ drains the vault even in a
surplus month and a strong one fills it in a deficit month; a sale it cannot afford breaks a peg, or lets a band give
way for the month. Under a peg the curve's expected dial is the tied dial's (the US's rate plus the spread), not the
inflation rule's. A broken peg lands past the shadow on whichever side it was (the franc, 2015). Choosing a peg or a
band with a vault that would not hold two years against a 10% gap, or before the city has traded a month, shows an
amber warning; the choice goes ahead. Lean, the default, moves by nothing: the playtest's trace is byte-identical.
CurrencyRegimeCheck 14-18.
## queued
| KF1 | KeptCheck's late fixtures on a city whose path any model change moves: `ensureWorks()` to cause works that are drawn, not merely standing (on XR5's path the city, its D$ at the rate's clamp, could fund none), and the kept header's hidden rate label keeping the layout it had when shown (KeptCheck 98's signature counts it, 18 differ) (XR5's two KeptCheck reds, not its code) | [XR5-notes §6](docs/queue/XR5-notes.md) |
