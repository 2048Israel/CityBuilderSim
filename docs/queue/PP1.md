# PP1 - political points: rush a dated policy with points earned by growing

Jerus, 2026-11-18, after a slow run and ChatGPT's review ("Locking monetary
policy until 1914 and pensions until 1927 ... leaves you with very limited
options during an economic emergency"): the policy dates are "not a good
thing, it restricts the initial gameplay"; on the law-find route, "by the time
you have a law school, its already 2000 lol". His design: "add a point system,
such as political points, and basically keep the progression, but at certain
population milestones you get popups ... it shows how long until the next
thing becomes available but also gives the option to rush it ... each
political point would allow you to reach something a year earlier ... if you
have 30 points, and the year is 1914 and something unlocks on 1920 you could
use 6 points to unlock it now"; the popup "only appears and pauses the game
if you are going at 1-2x speed otherwise it doesnt appear, just in the inbox
as clickable"; "just enough points to only do specific stuff". Then, on the
recommendations below: "Yes, queue it".

## Why (read 2026-11-18 on main 2e862ae69)

`PolicyDates.Lever` dates 12 levers, from the policy rate (August 1914) to the
pension contribution (1966): 363 years in all from 1900. Taxes, the minimum
wage, zoning and the promises open on their dates in every city
(`TaxPolicy.isOpen`); Money's two only in a city founded on the "1900" chip
(`Game.isLeverOpen`, line 1182). The only early route is a law find, at most
`PolicyDates.MAX_EARLY_MONTHS` (48) early and never for Money, and a find
needs lawyers and a University (2,000 students), a Law School (360) or a Law
Research Lab, which a small town does not have for decades.

## What is wanted

1. **Points from population milestones**, front-loaded because a point is
   worth less each year (by 100,000 people nearly every date has passed by
   itself): 100 -> 5, 500 -> 10, 1,000 -> 10, 2,500 -> 10, 5,000 -> 15,
   10,000 -> 10; 60 in all, about a sixth of the locked years. Dials, one
   sentence each. A milestone is passed once, never again if the town shrinks
   and regrows.
2. **One point, one year early** (Jerus's rate): a rush opens the lever this
   month for the years left to its opening month, a part year rounded up
   (1914 to 1920: 6). No cap but the points. A rushed lever opens as a lever
   opens today (`PolicyDates.opensAtFoundingRate()`: the income taxes at the
   founding rate, the promises at zero).
3. **Money's two levers rush too**: they get a saved opening month as the
   others have in `TaxPolicy`, read by `Game.isLeverOpen` and
   `leverOpeningMonth`.
4. **The popup**, at a milestone: at 2x or slower (`UserInterface.SPEEDS`) it
   pauses the clock and shows the points gained, the balance, the next lever
   to open and its years left, and "Rush it: N points"; at 5x and faster it
   goes to the inbox only, clickable to the same card. The inbox's existing
   lever-opening lines stay.
5. **The Policy screen**: each shut lever shows its date, years left and a
   "Rush (N points)" button, disabled with the shortfall when the points are
   short; the balance on the screen. Every figure through a public getter.
6. **Law finds give points** in place of opening a lever: the Law School keeps
   a use for the late dates (EI 1940, the health premium 1947, the pension
   contribution 1966). How many a find gives is a dial, one sentence, its
   figure the builder's (recommended: 5).
7. **A founding choice** on the founding page: "Laws: history's dates with
   political points / all open", default the points mode; all open opens
   every lever from month 1 and hides the points. Separate from the equipment
   chip ("1900" / "All fitted").
8. ★1 (recommended (a), include): **emergency powers**, a one-time grant (a
   dial, recommended 5) the first time the city is in a real crisis:
   unemployment over a line or the treasury about to miss a payment, each
   line a dial against the model's own figures. Points come only from growth
   otherwise, and the city in trouble is the one ChatGPT's run needed a
   policy in. (b): leave it out.

## Where it touches, and the gate

- Files: `PolicyDates`, `TaxPolicy`, `Game` (`isLeverOpen`,
  `leverOpeningMonth`, the milestone in the month), `Inbox`, `Research` or
  `ResearchCatalog` (a law find's points), `ui/PolicyScreen`,
  `ui/FoundingScreen`, `ui/UserInterface` (the pause at 2x or slower),
  `DataSave` and the load path (the balance, the milestones passed, Money's
  opening months, the founding choice); `PolicyDatesCheck`, `SaveFileCheck`,
  and a new `PoliticalPointsCheck` (sections from the list above: each
  milestone once, the cost rounding, a rush opening at its founding rate,
  Money rushed in a "1900" city, the popup's pause at 2x and not at 5x, all
  open, ★1's grant once).
- **Moves the playtest?** The autopilot spends no points (recommended), so
  its dates are history's as now. Law finds stop opening levers, so a played
  city that makes law finds moves; the builder checks whether the playtest
  makes any.
- Saves change (`docs/contract.md` 6): a stranger's review. No `SAVE_FORMAT`
  bump if an old save reads as 0 points and its milestones passed from its
  population (the old-saves rule, `docs/contract.md` 1).
- Game classes: the whole suite at the gate. `Game` is the month's flow: not
  beside HL1 (`docs/contract.md` 3).
