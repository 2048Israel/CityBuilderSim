# R0g - the causes of death: the review's follow-ups

Queued 2026-11-05 by the orchestrator, from the stranger's review at R0b's gate (SHIP + FOLLOW-UP: the saves, the month
order, determinism, money and the standing rules all held). R0b gave a city's dead their causes (`CausesOfDeath`) and the
1918 influenza (`Health`). Three things to do, each caused by a fixture first (`README.md`, *The checks*):

1. **A reload mid-influenza, played on.** CausesOfDeathCheck saves December 1918 and checks the state comes back, but no
   check plays January 1919 on the reloaded city against an unbroken run. The review reasoned it plays the same (every
   input is saved or comes from the month number); a twin proves it. Add it beside that section.
2. **The fixtures' influenza hold is lost on load.** `Game.loadGameSave` rebuilds the world, which makes a new `Health` and
   drops its influenza hold, while `CausesOfDeath`'s own hold survives. The fixtures that call `holdCausesOfDeathOff`
   before a load (AutoBuildCheck, DeskCheck, FundCheck, HoldersCheck and others) so get the influenza's sick back in a
   twin that plays October 1918 to March 1920. Keep the hold on `Game` (or re-apply it after the load), and show with a
   fixture that a held twin loaded across 1918 has no influenza sick.
3. **The very strict central bank on 1900's deaths.** CentralBankCheck's comment (about its line 2124) records that with
   the causes on, the probe's very strict twin ran -4.28% a year against the 2% target and failed "no spiral"; the
   fixture now holds the causes off, as R0b's call 7 says. Measure it over CB1's `STRICT_SHAPES` with the causes on: if
   very strict tips 1900 cities into a deflation spiral, that is a finding about the bank and gets its own queued row
   (with the numbers); if it is the one probe city, say so in the comment.

Also, minor (the review's 10, R0b's call 5): an influenza case still sick past two months can die again through Sickness,
about 1% on top of the 0.6% in December 1918 only. Count it once if the fix is small; otherwise note it.
