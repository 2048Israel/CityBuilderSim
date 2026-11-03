# LD2 - the wanted posts' bounds and comments, after LD1 ({next}, 2026-11-02)

The stranger's review at LD1's gate left four points on the wanted posts and
one on the bank (the brief, `docs/queue/LD2.md`). The four are mended; the
bank's failures turned out not to be LD1's, and their cause is named and
queued (BF1, section 5). Built on FC1's tip (RL1). The probes are in
`docs/queue/LD2/`: `Ld2Bank` counts a hands-off city's bank failures over
seeds, `Ld2Trace`, `Ld2Who`, `Ld2Wood` and `Ld2Shares` trace where its equity went,
`Ld2Hook` is point 3's measurement on the ensemble's AUTO2 driver (set up as
`docs/queue/LD1/setup.sh` does, the hook in place of LD1's). Every run names
`-Dcbs.world=` the PC's `data/world-1.bin`: without it the Random path is
never read, and the bank fails in none of twelve seeds.

## 1. The timing, as it runs

`runPrivateInvestment()` ends in `settleWantedPosts()` inside
`startOfMonthUpdate()`, and `simulateMonth()` reaches `repriceLabour()`
(`SimulationEngine.updatePopulation()`) in the same `nextMonth()`: the asks
are priced the month they are made. The saved `wantedPosts` is replaced by the
next settle before the next reprice reads it, so it feeds only the People
caption (`PeopleScreen.wantedNote()`) between a load and the next month.
Kept for that (a screen that needs a flow gets it saved), and every comment
that said "next month" now says what happens: `DataSave`, `WantedPosts`,
`Game`'s line before the settle, WantedPostsCheck's header.

WantedPostsCheck 9 no longer calls `repriceLabour()` straight after a load.
From one save: the latent posts come back to the bit (deleting the field
fails here); priced directly, a copy with them and a copy without them part
(so the saved posts would move the wage if the reprice read them); stepped one
real month, the two copies price alike to the bit, because the month settles
its own asks first.

## 2. The cap on this month's posts

`WantedPosts.pricedOn(posts)` clamps each band's latent posts to
`MOST_OF_POSTS` x the posts at the reprice (after completions, the builders'
strike and retirement), in place, so the caption and the save show what was
priced. Measured: the latent exceeded the month's posts at the month's end in
18 of the 628 priced months of four AUTO2 cities (section 3's probe).
WantedPostsCheck 12 causes it: the band's posts fall between the settle and
the reprice.

## 3. The carry kept out

`repriceLabour()` priced the supply on the posts plus the latent ones, so the
fill (`fillByBand()`) let the latent posts take the surplus a band carries
down to the bands below. Measured first, as the brief asked, in four AUTO2
cities (seeds 0-3, 1,600 months each, 628 months with latent posts):

| band | months it asked nothing | lifted by the carry | mean, max lift | asked | total lift past sqrt(2) |
|---|---:|---:|---|---:|---:|
| none | 159 | 4 | x1.020 | 469 | 55 (max x1.90) |
| diploma | 543 | 343 | x1.074, x1.476 | 85 | 43 (max x1.76) |
| college | 114 | 1 | x1.006 | 514 | 34 (max x1.57) |
| university | 209 | 8 | x1.071, x1.092 | 399 | 9 (max x1.52) |

It matters: the diploma band, which never asks alone (LD1's notes 1), was
priced up by the college band's asks in most of the months it asked nothing,
and the bands that asked went past LD1's own bound of sqrt(2) through the
carry. So the supply is the fill against the posts that exist
(`supplyByBand()`), and the latent posts move only their own band's
numerator. `repriceLabour()` is one path again (`pricedOn()` adds nothing
when there is nothing). WantedPostsCheck 13 causes it: a diploma band
supplied mostly by the college band's surplus, priced as it was.

## 4. A band closed to migrants asks nothing

`settle()` takes `Migration.admits()` per band; a closed band settles to no
latent posts, the hold still counted as an ask (★2). In play every band
admits (each has an arrival ceiling; `holdArrivals()` is a harness's), so
this moves no game, only a harness city that closes a band.
WantedPostsCheck 14 causes it from both sides.

## 5. The hands-off bank: counted, and not LD1's

**Counted** (`Ld2Bank`, TreasuryCheck's setting: `newGame()` on a Random path
seed, the bank's button unpressed, 120 months) on this tree:

| | seeds 0-11 | seeds 0-23 |
|---|---:|---:|
| as it is (LD1's bid on) | 3 (3, 4, 5) | 8 |
| LD1's bid off (no hold noted, nothing priced) | 5 (1, 4, 5, 7, 8) | 14 |

LD1's bid makes the bank fail less, not more. EP1's "none of four before
LD1" was four seeds of a tree before the 1900 start: at a rate near a third,
four clean seeds come up about one time in five. The brief's mend ("find
which of LD1's bids drains the bank") has nothing to mend; LD1 stays as it is.

**The cause** (`Ld2Trace`/`Ld2Who`, eight failing seeds against four that
live). Every bank opens on 828 of founding capital and loses 2-4 a month for
four years: the tellers and the lights cost more than a village of 250-950
pays in interest. What takes it under is **Wood Products' loans**: 345-852
written off in each of the four failing seeds traced (3, 4, 12, 23), against
109-197 in all of a surviving bank's provisions. In each:

- **The mill never sells.** The sector borrows for a Wood Pellet Mill (seed 4:
  398 owed, on mortgage and interim, against 811 of building). Its revenue is
  0 in every month of the run; once it runs it buys logs and pays wages into
  a growing inventory (seed 4: 6.4 of logs and 4.2 of wages in m68, 56 of
  stock by m73).
- **It is closed with its loans left.** Seed 4 m74 (seed 12 m75, six months
  after its mill opened): the mill goes (q1 to q0), the sector's assets fall
  from 906 to its 29 of cash, its debt stands against nothing, and the
  default slice takes most of it.
- **The next month it borrows again, past the bank's capital.** Seed 4 m75: a
  new Pellet Mill on site, the mortgage 193 to 1,568; seed 12 m76: a Panel
  Mill on 3,672 of credit, 4.4 times the bank's whole capital, to a sector
  that defaulted the month before. On site for months (the builders short),
  its interest capitalised, it defaults in turn (seed 4: 1,442 m76; seed 12:
  622 m94) and the bank goes under.
- **And the desk.** At month 50 in each failing seed the desk books a loss of
  119-149: in seed 4 the register's Wood Products shares fall from 15,206 to
  2 in one month (the world's 10,616 and the desk's 4,543 gone, the sector's
  book equity unmoved at 385), with no buyback recorded, no city take and no
  split (a split keeps value): the holders lose 116 (desk) and 272 (world)
  for nothing. The same shape as BankCheck 19's Manufacturing at month 34
  (the brief's second sighting).

It is a problem of its own (the materials planner, retirement, the bank's
lending to a sector in default, the share register), so per the orchestrator
it is queued, not mended here: **BF1** (★1). TreasuryCheck's comment on its
hands-off city, which said LD1 raised the failures, now says what was found.

## 6. The checks

{checks}

## 7. The ensemble

{ensemble}

## 8. The calls (★, each taken the recommended way)

- ★1 **The bank's failures are queued as BF1, LD1 kept as it is.**
  Recommended: LD1's bid lowers the count (8 of 24 against 14), so bending it
  would not help; the mend is in the materials planner, retirement and the
  bank's lending, BF1's. Alternative: hold LD2 until BF1 is done (the lane
  waits a night on it).
- ★2 **A band closed to migrants asks nothing** (the brief's recommendation).
  Alternative: it asks, since workers above it may step down for the wage.
  No game moves either way while every band admits.
- ★3 **The saved field kept** for the People caption between a load and the
  next month. Alternative: drop it (the model never reads it), and a loaded
  city's caption names no priced posts until its first month.

## 9. One in, one out

No process line changed: no script, check machinery or rule a session reads.
