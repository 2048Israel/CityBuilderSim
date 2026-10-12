# PT1 - from a played city (2026-10-11)

The brief for batch PT1, and the four model findings that wait on Jerus
beside it. `QUEUE.md` links here. Written by Claude Code on the PC after
playing the game by hand (Jerus: "yes with recommendations in a tiny batch at
the end of all the queued batches").

**What was played.** Build dd285f3 (0.7.125), the real Earth at Rotterdam
(51.92 N, 4.48 E; world seed 771,494,742), Standard start, world inflation
1.0%, 304 months (January 2000 to April 2025) at up to 20x. Automatic
building on from month 13 at its defaults; ground bought by hand at month 134
(10 blocks) and month 183 (15 blocks). It ended at 2,638 people, GDP $135.5M a
year, treasury $127.5M. The log held no exception, and a save to slot 10 at
month 183 loaded back the same city. The save folder was a scratch one, so no
trace is kept: every figure below was read off a screen or the log. Line
numbers are as of dd285f3.

## PT1

The tiny batch: screens, words and the log. No model change.

1. **Found a city, the default start card is cut off.** In a maximised window
   on a 1536 x 960 desktop the card's title reads "Stan..." and its chip
   "de..." (ui/FoundingScreen.java:751). Recommend the title has the row and
   the chip its own line, or the chip goes.
2. **Month 1 says there is no bank while one stands.** Before the first month
   runs, NEEDS YOU reads "THE BANK - there is none" (CityNeeds.java:522,
   `bank.getBranches() <= 0`), the header counts it, and the Bank page says
   "There is no bank in this city" with a "Build a Commercial Bank" button
   (ui/BankScreen.java:609) - while Build > Shops says "Commercial Bank - you
   have 1" and the overview "Shops 1 standing". It clears once a month has
   run. Recommend a standing bank that has not opened reads as that ("opens
   with the first month") and is not counted in NEEDS YOU: a new player's
   first click is otherwise a second bank for $10.4M.
3. **"Build a Commercial Bank" lands at the top of Build > Shops.** The bank's
   card is the last on the page, two scrolls down. Recommend it lands on the
   card.
4. **"Served" in a small town.** Month 13, 40 people: "power - 13333% served",
   "general care - 2968% served", and the advice "elementary from 0% to
   16910% served". Month 100: "water - 3006% served". Recommend that past a
   few hundred per cent SERVED's one verdict prints a multiple ("30x what is
   asked") or its words alone, so every gauge changes together.
5. **The city's money is written two ways.** "$" in the header (Treasury,
   GDP), on Build and on Sectors; "D$" on the founding page, the Land office
   and Finances. Recommend one, wherever ui/Money decides it - or a line
   saying the difference is meant.
6. **City History, the population chart's axis goes below zero.** Its floor
   was -100 at month 134 and -200 at month 183, with the founding in view.
   Recommend ChartModel's scale floors at 0 for a series that cannot go under
   it, and ChartCheck holds it.
7. **People > Why people come: "homes for 634"** beside the tile "SPARE HOMES
   0 - 35 doubled up anyway", at 406 residents (ui/PeopleScreen.java:1390).
   Recommend the box says what the 634 counts, in the tile's unit.
8. **The overview drawer's footnote is under the wrong list.** "Everything
   above is near a limit or past one, and every one of them has a fix" sits
   below HOW THE CITY IS, whose rows were all healthy. Recommend it moves up
   under NEEDS YOU's rows.
9. **A notice toasts again on load.** Loading the slot-10 save raised "Your
   businesses have nowhere to build" at once, with about 0.47 km2 just bought
   and free. Recommend a notice raised before the save does not toast on
   load, or is tested again first.
10. **The toast covers a button.** On the Build overview it sits over "Turn
    automatic building off", bottom right. Recommend it clears the page's
    controls.
11. **Two lines of the log.** "Debt-to-GDP Ratio: NaN%" in months 1 and 2,
    before there is any GDP (EconomyManager.java:2263): print a dash.
    "Interest Rate: %3.92" (Game.java:10183): the sign after the figure.

The four below are the model's, and wait on Jerus: they are not PT1's to
change.

## M1

**One Boutique opening moved the policy rate to 23%.** Month 131: Luxury
Retail issued $941k of bonds and borrowed $634k, and "1/1 Boutique(s)
finished construction". Month 132: Business Tax Revenue $240.9k against
$42.76k before and $43.16k after, monthly GDP $221.56k against $93.01k and
$95.64k. History > The basket: the luxury index from about 1.2 to about 2.35
in that month, the price level from about 1.15 to 1.30. At month 134, in a
town of 275: inflation 12.6% a year, the rate 22.97% (3.70% at month 100).
The level then fell back; at month 183, inflation -2.8%, the rate 0.00%, out
of work 28.3%, "The 2013 slump" 22 months old. How much of that slump was the
rate and how much the ground (M2) this run cannot say.

Recommend a harness first: a fixture town of a few hundred where a Boutique
opens, asserting what the index and the rate do against the rule's own
constants, and LongPlaytest counting how often one shop's opening moves the
rate. Then the choice is Jerus's: the rule reads the index through a
one-month jump in one component, or a new shop's first month is kept out of
the index, or it stays and the episode is named for the player.

**Jerus, 2026-10-12: "keep it for now the boutique thing".** Kept as built,
no batch; it left Pending.

## M2

**The ground runs out and the city stops, quietly.** The inbox's "Your
businesses have nowhere to build" is dated June 2006 (175 m2 free); the
population was 265 at month 100 and 275 at month 134. After 10 blocks: 406 by
month 183, and the notice again (Manufacturing "needs 0.0446 km2, 0.0347 km2
free"). After 15 more: 2,638 by month 304, the notice dated January 2022 with
four sectors waiting. The treasury was never under $90M, automatic building
was on, and pause-on-events was off (Jerus's own setting, copied).

Recommend one of: a NEEDS YOU row while a sector has waited on ground some
months, if there is not one; the toast again each year it lasts; or a choice
on automatic building to buy the businesses' ground within the debt limit.

**Jerus, 2026-10-12: "a notice repeated".** Do: the "Your businesses have
nowhere to build" notice comes again each year the wait lasts (a new inbox
notice, dated, naming the sectors waiting and the ground each needs), not
once. Queued as PT1-M2.

## M3

**The advice and automatic building disagree on the same page.** Month 304:
WHAT WOULD HELP MOST offers a Middle School ($32.6M) and a Police Station
($45.5M); crime is 1.5x Canada's, 175 children have no middle school, the
treasury holds $127.5M - and automatic building holds both back, the inbox
saying why ("it would not pay back: the 504 residents it would cover over its
20 years pay the city $255.38 a month each and cost it $1,317 each").

Recommend the advice card carries that verdict in the appraisal's own words;
neither rule changes.

**Jerus, 2026-10-12: "yes do rec".** Do as recommended. Queued as PT1-M3.

## M4

**Construction earns with nobody working.** Month 183: it kept $377k of the
month at a 72.2% margin and held $12.4M, with 0 workers, "no depot yet", an
order book of 0 and 3% busy - in a town producing $22.7M a year with 28.3%
out of work.

Recommend Jerus confirms it is meant; if it is, the card says who does the
work, and where that cash goes is a question for him.

**Jerus, 2026-10-12: not meant.** "its cause there is some construction points
base, so ya sure fix, perhaps a starting depot or something instead?" The
cause is `BuildingManager.BASE_CONSTRUCTION`, the works department's 400
points a month with no posts behind them (`getTotalConstructionCapacity()`;
`Game.getConstructionOutput()` keeps them whole whatever the fill), which
Construction is paid for. Queued as PT1-M4. Do: retire the free points and
found the city with a starting depot instead, staffed and paid like any
other, so the sector earns only for work its people do. ★ for the
implementer, each to measure, not assume:

- **A founding town must still build.** T1-C found that a depot opened among
  106 people fills at 0.16 and nothing is built again (BuildersFirstCheck 6,
  `runs/fixT1C-notes.md`), and `BASE_CONSTRUCTION` was raised from 100 to 400
  on 2026-09-09 because the opening decade was spent waiting on the first
  depot. The starting depot's size, posts and skills must let a founding
  town staff it; a new, smaller template is allowed if none fits (the rule:
  a template only for a new mechanic, and this is one).
- **What else reads the 400:** housing maintenance was calibrated against
  it (its javadoc), `BASE_MATERIALS` moved with it on 2026-09-09, and every
  harness and the playtests' openings stand on it. Say which move, and
  never move a harness's premise to let the change through.
- Old saves need not load (the standing rule): bump `SAVE_FORMAT` and the
  floor only if a save would load wrongly.
