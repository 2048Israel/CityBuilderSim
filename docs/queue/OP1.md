# OP1 - every policy open from the first month

Jerus, 2026-11-19 ~11:40, from his side session: "you know the queue about
PP1 i think it was, ya no, remove it, instead everything policy related is
available right away, and later ill think of an actual tutorial". PP1
(political points) is dropped; its brief is `git show
ddcb3400e:docs/queue/PP1.md`, and its "Why" section is the map of the dated
levers. The tutorial is his to design later: nothing here builds one.

## What is wanted

1. **Every lever open from month 1, in every city.** The 12 levers of
   `PolicyDates.Lever` (the policy rate 1914 to the pension contribution
   1966: taxes, the minimum wage, zoning, the promises; `TaxPolicy.isOpen`)
   and Money's two (`Game.isLeverOpen`, `leverOpeningMonth`, today open on
   their dates only in a city founded on the "1900" chip) are open at
   founding. A lever opens as a lever opens today
   (`PolicyDates.opensAtFoundingRate()`: the income taxes at the founding
   rate, the promises at zero), so the city starts where it does now and
   only the player moves them. The builder lists every other policy gate it
   finds (a screen's "opens in", a dated control) and opens those too, each
   named in the notes.
2. **The Policy screen** shows no opening dates, years left or shut levers;
   the inbox's "a lever opens" notices go.
3. **Law finds** no longer open levers (`PolicyDates.MAX_EARLY_MONTHS`,
   48). ★1 for Jerus, recommended (a): a find keeps whatever else it does
   and loses the opening, its text saying what it does now; a find whose
   only effect was the opening is listed in the notes with a proposal. (b):
   remove those finds.
4. ★2, recommended (a): history's regimes that are not levers stay
   history's (the gold peg's suspension and return, `PolicyDates.GOLD_END`,
   `GOLD_RETURN`; the equipment chip "1900" / "All fitted"). (b): open them
   too.

## Where it touches, and the gate

- Files: `PolicyDates`, `TaxPolicy`, `Game` (`isLeverOpen`,
  `leverOpeningMonth`), `Research` or `ResearchCatalog` (the law finds),
  `Inbox`, `ui/PolicyScreen`, `ui/FoundingScreen` if it states the dates;
  `PolicyDatesCheck` and every harness that asserts a shut lever or a date
  (`grep PolicyDates docs/harnesses.md`), each changed to assert the lever
  open at month 1.
- **Moves the playtest?** If the autopilot sets a lever once it is open, the
  default city moves from 1900: the builder checks, and an ensemble per
  `docs/contract.md` if it does.
- Saves: an old save's shut levers read as open on load (saves never matter,
  `docs/contract.md` 1); no `SAVE_FORMAT` bump unless an old save would read
  wrongly.
- Game classes: the whole suite at the gate.
