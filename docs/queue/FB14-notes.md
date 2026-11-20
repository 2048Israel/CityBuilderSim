# FB14 - gold: GD3's review and the start-on-gold box (2026-11-20)

The batch's record beside its brief, `FB14.md`: the two Pending rows GD3-R and
GD3-F. Built by one Claude session (Opus 5.5) on `batch/FB14`, cut from
origin/main 919c1fa41 (0.9.162). Line numbers are this branch's.

## 1. GD3's review, item by item

1. **KeptCheck's header red is gone, mended by FB2 (0.9.131).** The line is
   `KeptCheck.java:9596`, "the kept header's signature equals a header built
   new ... (18 differ)": the RATE tile's hidden wording "D$1,000,000,000.00
   per US$" at `MAX_RATE`. FB2's KS1-UI made the header's hidden wordings and
   sparkline stand where a tile built new leaves them. Its changelog block,
   `docs/notes/changelog.md:480`, names the 18 states. The line is in neither
   `docs/notes/gate.txt` nor `docs/notes/known-reds.txt`, so main's gate
   passes it. Nothing built here.
2. **ExchangeRateCheck 14 tests Gold under Gold.** Before, `setRegime(GOLD)`
   was refused on a fixture with no gold price, so the GOLD pass ran on Lean.
   Its Gold city is now set up as a Game city would be:
   - gold's price handed, `GoldStandardCheck.rates(PRICE)`, at the founding and
     at every month's top;
   - gold bought by name (100 x TRADE) and forgotten as set-up;
   - M0 handed at `FOURTEEN_GOLD_COVER`, ten times the law's cover. At three
     times, the city came off gold by doubt in month 63, its cover worn under a
     full one.

   A new fixture line, "the Gold city is on Gold in every month of the 120",
   is red on the old fixture (0 months). It is 120 now, and the seven lines
   and the hold sum to the move to 3.5e-18.
3. **The pace reads M0, not the unsaved base.** `ForeignAccounts.monthsToFloor`
   used to read `moneyBase`, which is not saved, so after a load it counted
   all the gold as free. It now takes M0:
   - the new signature, `monthsToFloor(double m0)`, is at `ForeignAccounts.java:1861`;
   - `Game.getMonthsToFloor()` (`Game.java:6215`) hands it `centralBank.m0()`,
     as FREE GOLD reads it;
   - Policy's three readers (`PolicyScreen.java:1515`, `1521`, `4439`) and
     the gold card (`4824`) read it there.

   Under Gold with a cover over 1, a sale also retires M0 (item 6), so the
   cover only rises. The pace is then the gold's whole holding over the
   sale, and the card says so.
4. **A gold break reads as gold's, and Gold has a thin-cover flag.**
   - `ForeignAccounts.lastBreakWasGold()` (`:1828`, set at `:1242`, saved in
     slot 158) carries the regime that broke, so a doubt under Gold no
     longer reads as a peg's.
   - Policy's flag (`PolicyScreen.java:1505`) is "The D$ came off gold" with
     "The gold gave way" for a gold break, and "The peg broke" for a peg's.
     Trade's break line (`TradeScreen.java:2989`) reads the same getter.
   - The new flag "The gold's cover is thin" (`PolicyScreen.java:1512`) shows
     under Gold below `DebtManager.GOLD_DEFENCE_FROM` (0.80). It is BAD under
     `COVER_RATIO` and WARN between the two, and gives the cover, the law's
     40% and the pace.
5. **A GD2-length array is loaded.** SaveFileCheck restores a 152-slot array
   (through gold's chosen share, 151) into freshly reset accounts, as a load
   does. The result has GD3's defaults: no mint price or rate, not following,
   no event. Canada's episode (156-157) and FB14's last break (158) are
   empty. GD2's gold balance and price come back, and saving again writes a
   full array.
6. **Selling gold shrinks M0 (★ the booking).** Before, Game called
   `CentralBank.dollarsSold` for every defence, Gold's too, which only records
   it: the bank's equity paid and M0 stayed. Now a reprice under Gold calls
   `centralBank.goldPaidOut(defence)` (`Game.java:14332`). The next month's
   top, right after `settleTheEdge()` and inside the audit window, calls
   `settleGoldRedeemed()` (`Game.java:13712`, `CentralBank.java:753`). It
   retires what is due from the reserves, through `retire()`, the path every
   other contraction uses. It takes no more than the reserves; any rest is
   `dollarsSold` as before.
   - It is the mirror of `buyDollarsForVault`, settled the same way a month
     later.
   - The audit says it twice (`MoneyAudit.java:1260`, `:1613`): RESERVE
     "+ centralbank TakenBackForGold" and MONEY "- centralbank RetiredForGold".
   - The vault's new money is now `vaultInNewMoney()`, the dollars bought less
     the gold retired (`CentralBank.java:779`). The equity identity reads it in
     LongPlaytest, CentralBankCheck, CurrencyCheck, CurrencyRegimeCheck and
     HoldersCheck.
   - Gold's cover test reads the cover after the sale over M0 after it
     (`ForeignAccounts.java:1187`).

   The proof is GoldStandardCheck 13, a town founded on gold through the box,
   over 120 months:
   - 78 months sold gold; each sale was due back, and each was retired whole
     wherever the reserves could pay twice over (55 of 55);
   - the audit carried both lines and closed every month, and the bank kept
     nothing in any month;
   - D$213.6k was retired and D$18.3k spent from equity where the young
     town's reserves could not pay: every sale booked once;
   - "most of it was retired" is red under GD3's booking, which retired none;
   - booked by hand, a sale lowers M0 by itself ("GD3 left it where it was"),
     and one past the reserves retires them all and spends the rest.

   GoldStandardCheck 4's and 9's fixtures are restated for it. Section 4's
   cover edges are set over M0 less the sale. Section 9's bracket includes
   the cover at the month's top, after the gold's last sale was retired; the
   dial reads that one, 0.109252 against 0.109888 at the month's end.

## 2. GD3-F: the start-on-gold box

- **The model.** `Founding.withGold(boolean)` / `isOnGold()`
  (`Founding.java:405`, `:455`): false in every preset and constructor.
  `goldUsd(rate)` (`:445`) is how much of the vault goes into gold.
- **The founding.** `Game.foundOnGold()` (`Game.java:1303`) is called by
  `newGame(Founding)` (`:854`) and by `run()` for a starting city (`:766`),
  after `unfitTheFounding()`. It is never called on the load path. In order:
  - it builds the world market and hands gold's price alone;
  - it moves `goldUsd(rate)` of the vault's dollars into gold through the new
    `ForeignAccounts.foundInGold` (`:2362`): an endowment kept in another
    metal, with no dealing and nothing booked;
  - it puts the city on Gold through `setCurrencyRegime`, which strikes the
    mint at the day's rate and sets Defend the gold, with the player's log
    held.

  Where the world cannot price gold, or the vault is empty (Insane), the city
  founds on dollars and a line is printed.
- **The screen.** "Start on gold" is under WHAT IT STARTS WITH
  (`ui/FoundingScreen.java:397`). Its line (`goldWords()`, `:928`) says how
  much goes into gold, from `Founding.goldUsd` at the opening rate, and how
  much stays in dollars. It is redrawn as the custom fields are typed.
- **★ How much (the orchestrator's call (a), 2026-11-20): the law's 40% of
  the treasury the city is founded with, never more than the vault.** That is
  all of the vault in every preset: Lean's and Wealthy's vaults are exactly
  40% of their treasuries and Standard's is 25%. A custom vault deeper than
  that keeps the rest in dollars. Read literally, "enough for the 40% cover"
  is 40% of M0, but a new city's M0 is what its bank has made: 0 at the
  founding and D$2.07k at month 1, against a D$648k vault. The probe, months
  to the first break off gold in 480 (none = on gold throughout):

  | founding | (a) the box | (b) 40% of month-1 M0 | (c) the whole vault |
  |---|---|---|---|
  | Lean | 175 (COVER) | 26 (COVER) | = (a) |
  | Standard | 195 (DOUBT) | 26 (COVER) | = (a) |
  | Wealthy | 438 (COVER) | 26 (COVER) | = (a) |
  | custom, D$2.59M and US$2.59M | 237 (COVER) | - | 274 (COVER) |
  | Lean, 1900 | 163 (COVER) | 38 (COVER) | = (a) |
  | Standard, 1900 | 410 (COVER) | 38 (COVER) | = (a) |
  | Wealthy, 1900 | none | 38 (COVER) | = (a) |
  | custom, 1900 | none | - | none |

  (b) puts D$0.8k in gold:
  - the cover sits on the floor;
  - Defend the gold puts the whole spread on the rate (15%);
  - interest on reserves makes M0 sixty times over in a year (2.07 to 119.8);
  - the cover falls to 0.011 and the city is off gold in month 26.

  (a) and (c) are the same in every preset. (a) and (b) ran with item 6 in;
  the earlier (b) run, before item 6, also broke at 26 and 38.
- **Proved through the founding model** (GoldStandardCheck 14):
  - Standard, Lean and Wealthy each put the whole vault in gold (none in
    dollars), on Gold, Defend the gold, the mint struck at the day's rate,
    nothing in the log. A month on, each holds the cover (still on Gold, the
    cover over 0.40).
  - The custom vault of US$2,588k holds 40% of the treasury in gold and
    US$1,553k in dollars.
  - The box off holds no gold, at the founding and a month on.
  - The other door (`new Game(files, founding)` and its `run()`) founds on
    gold too.
  - A 1900 founding with the box is on Gold, not on Canada's peg; without it,
    it is on Canada's peg with no gold.
  - Insane founds on dollars.

## 3. How the box meets OP1's gold years

A 1900 city is put on Canada's peg at par by `policyHearsTheMonth()` in
`buildWorld()`. The box's Gold leaves that peg: `setRegime` calls
`leaveCanadaGold()`. So:

- August 1914 floats nothing;
- July 1926 passes a city still on Gold by: `hearMoneyMonth` puts back on par
  only a floating dollar;
- the city meets the US$'s own dates as any city on Gold does (GD3: April
  1933, February 1934, August 1971);
- a box city whose gold broke before July 1926 is floating then, and Canada's
  return puts it on the peg at the old par, as it does every floating 1900
  dollar.

A default founding hears no Canada dates. The banner over `foundOnGold()`
says the same.

## 4. Saves

No format bump.

- **The box** is not saved. It acts once, at the founding, and what it did is
  in the save: the vault's gold and the regime, in the foreign accounts' own
  slots. A loaded city's founding reads it false, and nothing after the
  founding asks (`getFounding()`'s only later reader is PartnersView's
  lat/lon). DataSave is untouched (HS2's file).
- **Item 6** appends two figures to the bank's array: `goldRedeemDue` and
  `goldRedeemedLifetime`. Each is read only when present, so an older save
  reads 0 and 0, true of every city before FB14, none of which retired for
  gold.
- **Item 4** appends slot 158 to the foreign accounts' array. An older array
  reads the last break as a peg's, as every break was worded before. The
  Canada slots' guard is now `> CANADA_GOLD_EXIT_SLOT`, not `>= SAVE_SLOTS`,
  so a 158-slot save still reads them.
- SaveFileCheck asserts each round trip and each shorter array.

## 5. Findings left for the next gold batch

- **M0 can go below zero in a city on Gold.** In the probe, Lean's city stood
  at D$-4.07k at month 175, and Wealthy's at D$-12.1k by month 438. Item 6's
  retirement stops at the reserves. The bank's other contractions do not:
  `takeCoupon`, `takePrincipal`, `chargeWindow`, `chargeAdvances`,
  `sellPaper` and `settleRedemptions` retire uncapped. In a city whose
  sales have already retired much of M0, they take it under nothing. The
  cover reads an M0 at or under 0 as fully covered. Before FB14 nothing
  retired for gold, so this was not reached.
- **GD4's mint is not in the equity identity.** `CentralBank.mintGold` puts
  gold into the vault for new money, but `goldMintedLifetime` is not a term
  of `vaultInNewMoney()`. It is reached only under Gold with a gold mine; no
  harness plays that, so none is red. One term (`+ goldMintedLifetime`) and a
  harness month would settle it.
- **A Gold city's dollars do not defend it.** With gold all but gone, a sale
  gold cannot pay breaks Gold by its cover (`-usd > goldUsd`), even with
  dollars left in the vault. GD3 built it so: the custom city above comes off
  in month 237 holding US$1,553k in dollars. If a vault's dollars should back
  the gold, that is a design call for Jerus.
- **Finances' "spent defending the currency"** (`cb.vaultSpent()`) no longer
  counts the gold's sales the reserves took back: they are M0 retired, not
  equity spent.

## 6. Checks

- `./batch-check.sh --fast` is green.
- The named harnesses, each alone (`./batch-check.sh <name>`):
  - green: GoldStandardCheck, CurrencyRegimeCheck, VaultMoneysCheck,
    PegAnchorCheck, MoneyDatesCheck and CurrencyCheck;
  - exit 3, only main's known reds:
    - ExchangeRateCheck: section 8b's four, FS10;
    - SaveFileCheck: the basket fixture, FS17;
    - HoldersCheck.
- **CentralBankCheck** reached its end with 2 FAILED, main's section-10 pair
  (known-reds, FS10). AllChecks' 1,740 s limit then stopped it. The run shared
  the PC with HS2's ensemble (twelve JVMs) and, for its first five minutes,
  with an orphan of an earlier run. The earlier run took 1,639 s. It is not
  one of the gold's sections; the gate box judges its time.

## 7. The trace: main's, so no ensemble

The default playtest ran traced (`-Dplaytest.trace`, the Earth and world files
named), on main's classes (44e0aa664, the cut plus PZ1's PrizeCheck alone) and
on FB14's model (4430e11b5).

- The eight trace files are byte-identical.
- The report differs only in run-time lines: the run's seconds (1,594 and
  1,166), two clock stamps, and a `LandParcel`'s identity hash in a works
  decision's printout.
- The default city is on Lean and never enters Gold. Every change to the
  month acts only under Gold or for a founding with the box. Off Gold, the
  audit's two new lines are 0.

No ensemble (contract, section 4: the trace has not moved beyond runtime).
