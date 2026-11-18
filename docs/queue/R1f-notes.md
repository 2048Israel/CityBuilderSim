# R1f — The playtest's harnesses on the 1900 city

Built from d6844edc4 (0.9.150). This batch changes harnesses only.
`LongPlaytest.founding()` remains all fitted; every added historical city
asks for `founding().withUnfitted(true)` and uses scratch files or TempDirs.

## Findings and calls

1. ★ Count the existing hold move. `ForeignAccounts.getMoveHeld()` already
   names the difference between the actual rate's move and its shadow's
   move. ExchangeRateCheck 8 omitted it. Its separate 1900 section checks
   the float's moves plus the world and hold every month through July 1926,
   and measures the release of the peg-shadow gap in August 1914. No money
   model or saved state needed changing.
2. ★ Scale the unfitted counts with the buildings. ScaleCheck's JSON copy
   left `unfittedById` at one town's counts while multiplying its buildings:
   almost all the copied plant became fitted on load. The new section
   asserts every saved piece count is exactly K times the original, then
   runs the existing six-month tracking laws without changing a tolerance.
3. ★ Keep the historical fixture before tractors arrive: month 120, not
   month 225, when this town has already fitted them. Horse farm hands and
   carters round to whole posts: their maximum local rounding is half a
   post per tier, or half a carter. Hired teams themselves are divisible.
   A fitting delivers a whole building only when all its work is complete;
   the tracking fixture must have no fitting on site. No game rounding
   rule is changed to make cities scale.
4. ScaleCheck's posts identity now adds BuildingManager.getPostsPast() in
   both its printed and monthly identities. Horse hands are genuine posts
   outside the buildings' nameplates.
5. The five other harnesses keep their modern sections. AutoBuildCheck
   causes a 1900 power shortage, rather than demanding modern general-care
   supply. AuctionCheck causes a real player bill sale, rather than
   requiring the stalled young town to borrow spontaneously. SlopeCheck
   supplies owned steep ground explicitly. PriceLevelCheck compares a
   historical founding at both price levels, including the census ladder
   and team hire. BuildAdviceCheck checks the young historical pipeline:
   there is no class ready for its first university. R1g owns the city's
   stall; this batch does not change it.

## Checks

Work in progress; final results and the picked harnesses follow here.

## Default trace

No game class, resource or LongPlaytest line changes. The default model and
founding are identical to main. A standalone trace comparison is scheduled
by the orchestrator; no ensemble is owed for a harness-only change.
