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
   On this main's 1900 harness city, August is 1.0000 to 0.8126
   (-18.74%): its shadow moves 0.8481 to 0.8126, a -0.042764 log move,
   while releasing the hold contributes -0.164797. The July 1926 return
   is 0.5808 to 1.0000, hold +0.566401. All 318 monthly decompositions
   close within 1.25e-16. These are measurements of this fixture, not a
   replay of R1d's older named run 6. In that record, 1 to 0.6619 is
   -0.412641 in logs: the reported 0.4116 missing contribution accounts
   for almost all of it, leaving about -0.001041 (-0.104%) as float news
   if that maximum miss is the suspension's. ★ The large first step is
   the release of an accumulated peg-shadow gap, a regime move. It is
   not a one-month 33.8% market shock and not a numerical fault requiring
   the float to be reset to par. The historical assertion measures the
   gap directly so it does not depend on that inference from rounded
   older figures.
2. ★ Scale the unfitted counts with the buildings. ScaleCheck's JSON copy
   left `unfittedById` at one town's counts while multiplying its buildings:
   almost all the copied plant became fitted on load. The new section
   asserts every saved piece count is exactly K times the original. Its
   six-month historical section uses exact identities and the arithmetic
   bound on whole posts, and also asserts all eight existing TRACK laws.
   No tolerance changes. The initial 171-person town is too small for those
   empirical relative bands (a half farm hand materially changes crop
   costs), so the tracking fixture is enlarged first, with all equipment
   and the census ladder preserved. Its size is derived from FREE_MONTHS,
   the number of job tiers, its posts and TRACK_PRICES.
   This gives 123 copies, 21,033 people, before the K=1,000 comparison.
   The worst horse-hand miss is 377 posts in the copy, or 0.377 post
   after dividing by K, inside the two half-post bounds. At month six the
   relative misses are people 4.45e-5, savings 2.45e-3, GDP 2.13e-3 and
   prices 1.15e-3; every existing TRACK law passes in every month. The
   small 171-person fixture's six-month price miss was 9.56e-2: these
   figures explain why asserting relative homogeneity on that tiny
   horse-worked town would be asserting away its whole hands.
3. ★ Keep the historical fixture before tractors arrive: month 120, not
   month 225, when this town has already fitted them. Horse farm hands and
   carters round to whole posts: their maximum local rounding is half a
   post per tier, or half a carter. Hired teams themselves are divisible.
   The fleet has bought motors by month 120, so its horse-form rounding is
   measured as a control on the same live haulage demand, then its actual
   share is restored. A fitting delivers a whole building only when all its work is complete;
   the tracking fixture must have no fitting on site. No game rounding
   rule is changed to make cities scale.
4. ScaleCheck's posts identity now adds BuildingManager.getPostsPast() in
   both its printed and monthly identities. Horse hands are genuine posts
   outside the buildings' nameplates.
5. The five other harnesses keep their modern sections. AutoBuildCheck
   causes a 1900 power shortage, rather than demanding modern general-care
   supply (four known Coal Power Plants ordered, with ample cash so this is
   not a test of credit). AuctionCheck causes a real player bill sale, rather than
   requiring the stalled young town to borrow spontaneously. SlopeCheck
   supplies owned steep ground explicitly. PriceLevelCheck compares a
   historical founding at both price levels, including the census ladder
   and team hire: 869 series over 61 months pass, real error at most
   1.4e-15 and live nominal error 7.2e-14. It draws zero partner lanes in
   those months; that mechanic does not fire in this young historical city.
   BuildAdviceCheck checks the young historical pipeline: only 0.45 would
   come, against the model's 600-place first-school threshold. Its control
   adds enough known degree jobs to fill a university, isolating the
   missing class; it still offers no row or card. R1g owns the city's
   stall; this batch does not change it.

## Checks

Named runs are in `target/R1f-*.txt` and `target/checks/<Harness>.log`.
The modern ScaleCheck savings miss, 2.83e-2 against .016, is main's HP2
known red, already in `docs/notes/known-reds.txt`; no new modern failure.
Modern PriceLevelCheck retains HP1's reload and cut-off reds and HP2's
absent lane. These are already recorded on main under their queued repairs;
the ship file does not repeat them.

- ExchangeRateCheck, AutoBuildCheck, AuctionCheck, SlopeCheck and
  BuildAdviceCheck pass. ScaleCheck and PriceLevelCheck have only those
  recorded reds; every new historical assertion passes.
- The final ScaleCheck / BuildAdviceCheck / AuctionCheck run used
  `-Dcbs.parallel=1`: 273 s, two passed and ScaleCheck known red only.
  Rejudging its saved suite returns 3. The seven-name development command
  initially used AllChecks' default three-way parallelism; I stopped only
  its own AuctionCheck PID 29672 after verifying parent PID 9296, and
  reran that harness in the sequential final command. The cancelled run's
  artificial exit -1 is superseded, not entered as a known red.
- The temporary `target/R1fMutationProbe.java` runs only ScaleCheck's new
  historical methods. It first passes the unmodified section, then removes
  `unfittedById` from TOP in that JVM only and copies the correctly enlarged
  fixture again. Twenty assertions fail: the farm-post bound and prices in
  all six played months, plus later unemployment laws. Month-six prices
  miss by 1.46e-2 against the unchanged .004. The probe detects the mutation
  and exits 0; log `target/R1f-mutation.txt`. No mutation or probe is shipped.
- Final `--fast` passes (exit 0): compile 4 s, maps regenerated, Stale zero
  firm findings, LF endings clean. `git diff --check` also passes.

All checks named the main worktree's `earth-2.bin` and `world-1.bin` by
their C:/ paths. Only scratch / TempDirs cities were written, using slot
10; ExchangeRateCheck's existing scratch replay cases were moved from
slot 2 to 10 as well. No real save slot was written.

`sh tools/pick.sh origin/main HEAD` picks 45 of 221 harnesses, not ALL.
Its review line is `not needed - no money, saves or threading in the diff`.
The machine coordinator authorized the seven named harnesses while other
gates run, so no full `--pick` or standalone playtest was started. The
orchestrator's gate runs the pick fresh on the main it merges onto. No dry
run or kept gate directory is owed for this harness-only pick.

The orchestrator gave the non-ALL go on 2026-11-18 at 14:53, asking that
the five duplicate known-red rows be removed before readiness. They are
already in main's known-red records. The corrected ship file passes
`tools/known-reds.sh`; the provisional marker is dropped last, in its own
commit, and nothing is pushed after reporting ready.

## Default trace

The diff touches only `*Check.java` and docs; no game class, resource or
LongPlaytest line changes. The default model and founding are identical to
main by construction. The orchestrator confirmed by bus on 2026-11-18 at
14:26 that no standalone default trace run is needed in this case. The
default trace cannot move; no ensemble or dry run is owed for this pick.
