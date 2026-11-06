# R4 - research: prizes and the Research Lab ({next}, 2026-11-06)

## WHERE IT STANDS

**Ready.** Branch `batch/R4` from `batch/R3`'s ship file (RL1, 3944a1e4); main with R3 in (b67298b2) merged at 2c8a2fe8. Slice A
is 2216a605, slice B 4e5d86da. The default trace is R3's to the byte, so no ensemble. The full suite on the merge: **exit 3**, main's
recorded reds only, none NEW (§4). R3b's prize cards and the purse on the screens in the ship file's `## pending` (R4-R).

## 1. What R4 is, and where it splits

The brief is research-design §9 item 5, "R4, prizes and the Research Lab", with §5.3's rules: a prize dial per field, paid only for a
discovery made here; a Research Lab (Business Services, 100 posts of one field, D$ 12M to build) whose income is prizes; investors build
one while the purse pays its posts, for the cheapest open problem it could finish before the world; a lab with nothing left to chase idles
and is shed. R3 built the mechanism R4 adds to (`Research`, the grants, posts, piles and finds) and left the screen to R3b.

| part | what | why there |
|---|---|---|
| **R4, this batch** | **the mechanism**: the four prize dials; the chase list (an open problem the purse pays for and a team could finish before the world); labs' researchers on it cheapest first, after the city's own even spread; the work each kind piled; the purse split at a find, the labs' share paid by the treasury into their owners' till, the schools' a pot that buys posts; four lab templates; investors ordering a lab on the owners' money; idle labs shed; the save | the state and the money the screens read |
| R3b (queued by R3) | the four prize cards beside the grant cards, each with what it would draw and its preview | the screen, as R3 left the grants' |

## 2. The calls (★)

1. **★ The split above.** Recommended and taken, as R3 split its grants: nothing R4 adds is seen by a player until a city can be founded
   the 1900 way (R1d), and the prize cards belong on the Funding page R3b draws. R3b's row says "the four grant cards"; with R4 in they
   are the four fields' cards with both dials (the study's mockup 1), in the ship file's `## pending` for R3b.
2. **★ Four lab templates, one a field** (ids 148-151), not one template with a field of its own. A template's jobs are its own
   (`BuildingsStacks.getTotalJobs()`: no per-stack override) and the buildings are stacks keyed by template, so a lab's field is its
   template and the save keeps it with the stacks; one template would need a second job table and a saved field per building.
3. **★ The labs are Business Services'** (the study: "new, Business Services"): their wages are the sector's payroll and their money its
   till. The sector's own planner keeps out of them (`BusinessServices.plan()`), and its retirement rules never touch a building that
   makes nothing; investors order a lab on their own and idle labs are shed by their own rule (below).
4. **★ A lab is the owners' money, never a loan.** Investors are asked for the lab's cost and its stake, its posts' cost until the target
   would be finished at the team's limit; the bank lends against income and a lab has none until a find. What the owners do not put up is
   not built.
5. **★ A purse is a transfer, as a subsidy is** (`TreasuryLine.PRIZES`, paid into the labs' sector's till, beside the business subsidies
   in the national accounts and in the sector's books' "Subsidy from the city"), not a sale: no sales tax on it and no revenue line of
   the sector's statement. Refused past the central bank's ceiling and owed as arrears, as a subsidy is.
6. **★ A lab pays its posts' wages, power, water and repairs**, as every building of its sector does. The study's other costs of a post
   (38-40% in engineering and medicine, 20% in business and law) are in the post's cost the investors judge by and the stake they put
   up, not a bill a lab pays: no flow in the game carries a private firm's laboratory supplies, and a new one would be a new line out of
   the city. So a lab costs its owners less than they reckoned, the prudent side.
7. **★ The schools' share of a purse is a pot that buys posts beyond the grant** (§5.3: "it joins that field's grant pot, so it buys
   posts"), within the schools' room, the treasury paying them as it pays the grant's; the pot is a promise of the treasury's money, not
   money moved, so nothing is paid at the find for it.
8. **★ Whole labs.** The study's model fills a problem's room to the researcher (its big city's D$ 350M medical prize draws "130 lab
   posts"); a lab is 100 posts, so investors build the n-th while it would keep half its researchers at work (`LAB_ROOM_SHARE`): the
   young city's D$ 140M engineering prize still draws two labs, the big city's medical prize one.
9. **★ An idle lab is shed after six months**, `BusinessInvestment.RETIREMENT_LOSS_MONTHS`, counted as months its field has half a lab's
   researchers idle (nothing to chase, or more labs than the chase has room for), not by its losses: a lab loses money every month
   until its purse, so a loss rule would shed every one before it could finish.
10. **★ A lab on the player's build list.** The four templates are on Business Services' list for the player as every template is; in a
    city founded the default way nothing is ever on the chase, so a lab a player orders idles and is shed after six months. Recommended:
    R5's new-building cards say what a lab is for, or keep it off the player's list.

## 3. Slices

| slice | what | state |
|---|---|---|
| A | `Research`: the prize dials, the chase list, labs in the month, the purse, the pot, the investors' questions, idle labs, the save's getters and `restorePrizes()`; `PrizeCheck` 1-10 | 2216a605 |
| B | the four lab templates, Business Services' planner keeping out of them, the labs' researchers in the month, the pot drawn, the purse paid (`TreasuryLine.PRIZES`), investors' orders, idle labs shed, the inbox, the save; `PrizeCheck` 11-17 | 4e5d86da |

Both drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews found:

- **Slice A.** With no lab working, the month adds `(share + 0.0) / 12`, R3's own figure to the bit, in R3's order; the chase list
  is taken before the month's work is added, as the study's model reads its progress. `PrizeCheck` 10 holds the new month against an
  independent copy of R3's (the old overload now calls the new one, so comparing the two would prove nothing). The slice mutated
  `Research` 22 ways (`>` for `>=` in the purse's rule, the chase's order reversed, the pour ignoring the city's share, `restorePrizes()`
  leaving the grant's posts unset ...) and every one failed the harness.
- **Slice B.** The purse is paid in `finalUpdateEconomy()` and struck in the government's books the same month
  (`strikeGovernmentBooks()`, beside the subsidies), inside the money audit's window; the owners' raise is for what the till lacks of
  cost and stake (the whole sum would raise again every month a lab is held), and the investor it builds through cannot borrow.
  Business Services' own books needed the purse as cash from the city or `SectorBooks.unexplained()` failed in seven harnesses: it is
  in the books' "Subsidy from the city" line (`getPrizePaidTo()`), which is what ★5 means by a transfer. Two premises moved with the
  data, not to let a change through: DiscoveryCheck's "RESEARCH is the last treasury line" (now the last but PRIZES) and
  BuildingDataCheck's count of templates (148 to 152, with the new key compared).
- **A lab is wanted only once the chase has room for half a lab**: in the harness's village that is month 81 for engineering, so the
  city sections play 79 months first; the investors' order is caused there, and the labs at work, the purse and the save stand a lab
  up by hand (the village has no builders to finish one).
- **Idle labs are shed in a city founded the default way too** (`idleMonth()` counts them): a lab a player orders there (★10) has
  nothing to chase, ever, and goes after six months.

## 4. Checks

- **The trace.** The default playtest (2,405 months) traced on R3's tip (3944a1e4, built apart) and on slice B (4e5d86da), back to
  back: all eight CSVs byte-identical, the log but for "in 62s"/"in 46s" and the layout windows' "2.2 s"/"1.6 s". So no ensemble
  (`docs/batch.md` 6): with every prize at 0 and the default founding, R4 moves nothing; the four templates are never candidates.
- **`--pick` on the merge with main** (R3 in, b67298b2; merged at 2c8a2fe8): 119 of 181 harnesses for 16 files, BuildingManager,
  BuildingsTemplate, Game and Sector left to the gate with LongPlaytest in their place: **exit 4**, one NEW red, BuildCardCheck's
  "value added is what one makes less what it uses ... (77 makers, 7 offices ...)": it counted Business Services' offices as 3, and
  the four labs are offices too (each card's figures matched; a lab makes nothing, so its value added is nothing). The count now
  names them apart (`offices - labs == 3`, `labs` one a field), as BuildingDataCheck's count moved with the data; BuildCardCheck green.
  Everything else picked was green but main's recorded reds; timing SOFT.
- **The full suite** after it (`./batch-check.sh`, timing soft, 181 harnesses): **exit 3**, the six reds main's recorded ones and
  none NEW (SlopeCheck, MonetaryCheck, DenominationCheck, ScaleCheck, MapCheck, KeptCheck). `PrizeCheck`: 262 ok.
- An earlier `--pick`, on R3's tip, was stopped when R3 landed so that the merge's could run; its process went on as an orphan and
  wrote into the same log while the merge's compile replaced the classes, hence a log of ClassNotFound lines in
  `target/checks/run.txt` that are neither run's verdict (the merge's own lines are the ones above).
- Not a process batch: no scripts, checks' machinery or rules changed.
