# FC2b — the choices that use the foreign crews — notes

Builder: Claude (Opus 5.5), slices on Sonnet (contract 4). Branch batch/FC2b from origin/main b92af507f (0.9.197, SAVE_FORMAT 64).
Brief: docs/queue/FC2.md, "## FC2b" (Jerus, 2026-11-22: "allowing businesses to even decide to pay more to finish sooner (for
example, something finished in 20 months is better than something finished in 100 months, cause thats 80 more months of paying
interest...)"; "also let the city itself do it, like per building, aka for example if roads are causing the issues then you could pay
more to get just roads fixed").

## 1. What was built

- **A site's work abroad, dedicated.** A payer hands its share of one site's (one stack's) remaining work to the foreign crews. They
  build it on that site at a pace and price struck at the handover (BuildingManager.abroadTermsOf(), handSiteAbroad()), apart from the
  city's crews' rule and from FC2's pooled book. The record is ConstructionControl's G. ABROAD (Abroad: the part's points owed, its
  materials, pace, price a point, what was paid, the months home and abroad, the interest saved, the earnings brought forward), saved
  whole with the control's state.
- **The stack stays one** (BuildingsStacks): its buildings finish front first as ever, each its template's sector's whoever paid; its work
  splits into the home part (the crews' rule) and the abroad part (the crews' pace). The payer's contract moves to `abroadContracts` and
  is earned by the abroad part's fraction done; the other payers' by the home part's; the material likewise.
- **The month** (BuildingManager.advanceConstruction): the shares and the plan read each site's home work (homeOwedOf(), homeCapOf():
  PW2's build-time cap is the site's, both crews together); each handed site gets min(owed, pace) of the crews' own; the record ends
  when its part is built or the site empties. pointsBuilt stays the city's crews' and FC2's pooled work; getAbroadPointsBuilt() and
  getAbroadBill() are the crews on sites, billed by Construction.payCrewsOnSites() (an import of services, FOREIGN_CREWS, as FC1's),
  their points off the builders' backlog. queueAtHome() is less the work handed abroad; the crews' supply curve (excessAt) counts
  their month on the sites handed them (ConstructionControl.abroadMonth()).
- **The choices** (Game, THE CHOICES THAT USE THE FOREIGN CREWS): investorsHandAbroad() once a month before the crews' offer; the
  city's abroadPreview(key) card and handSiteAbroad(key); settleCrewsOnSites() after FC2's settle.
- **The arithmetic** (BuildersShort, THE CHOICE TO FINISH SOONER): sitePace(), monthsAbroad(), broughtForward(), interestSaved().

## 2. The calls (★ design)

- ★ **Dedicated, not pooled.** FC2's book pools his points into the sites' output and shares them by the crews' rule, so a payer could
  not buy its own site's speed through it. A handover is its own crews on its own site, at pace max(W/T, the largest depot) (FC2's
  own rule for his book: the work abroad waits no longer than T, the threshold's months; FC1's depot floor), never over W and never
  over PW2's build-time cap. So the part finishes within about T months: Jerus's "20 months vs 100".
- ★ **A shared stack.** The mover takes its own contract's share of the site's home work, by points: a contract now carries the
  points its orders put on site (Contract.points, noted at each of the four bookings), earned pro rata as its value is, the stack's
  own rule. Buildings are not named to a payer (they finish front first, each its template's sector's), so the order leaves as points
  of work; the other payers' contracts are earned only by the home part, so their progress is untouched. A site with work abroad is
  not isCitysOwn() (no reorder, rush, stop or cancel of it by the city). One handover a site while its record stands.
- ★ **The saving.** S = broughtForward(P, i, Wa, Wh) = P((1+i)^-Wa - (1+i)^-Wh)/i: the earnings P a month from the opening, brought
  forward from Wh to Wa, at the payer's monthly real rate (BA1's waitCarry rate, realTestRate/12). It is exactly the brief's two
  parts: the interest on the cost C for the months saved, interestSaved(C) = broughtForward(C·i), plus the earnings over that interest
  brought forward, broughtForward(P - C·i). Adding interest on the sunk cost to the whole earnings would count the loan's interest
  twice (it runs in both cases until the opening pays it). Money already spent is sunk: only the remaining work moves, and the extra
  is struck on it alone.
- ★ **The business** moves when the extra it pays, net of the builders' tax it gets back (buildFor's rule), is under S, with
  P = estimatedMonthlyProfit(sector, t) x its buildings on site, C = getCostOf(t, 1) x them, the rate judged(sector, shortfall).tested();
  it pays as fit() pays (borrows a shortfall if it can, else stays). The builders' own depots are not weighed (they go first already).
- ★ **The city** earns no money from a site, so its saving is the interest on its cost alone, at its long real rate
  (BuildAdvice.lifeRate()); the player decides from the card. The treasury pays (TreasuryLine.BUILDINGS, cityCapitalSpending) and
  refuses when short, as fit() does for the government. A rushed site's rush is stopped first (the overtime is the home crews').
- ★ **The extra** is the crews' price over the home price as it stands: points x base x max(0, excess - scarcityPremium()), grossed
  for the builders' tax. The builders keep the home price paid for that work and pass the crews' bill; the difference between the
  premium paid at the order and today's is theirs, as FC2's rule keeps quoted work at the price quoted. Where today's home premium is
  over the crews' excess, a move costs nothing extra and the crews' supply curve and the shorter queue at home price the next one.
- ★ **The waits** other than the shares (siteMonths and the order quotes) still count a handed site's abroad part at home: an
  over-estimate, conservative for the next payer's test.
- ★ getRemainingConstructionPoints() is unchanged: the dedicated work stays in the builders' backlog until built (they are its general
  contractor, as for FC2's book).

## 3. The checks

- Slice A (state): --fast green; ConstructionControlCheck, SaveFileCheck green (nothing handed yet).
- Slice B1 (month): --fast green; ForeignCrewsCheck, ConstructionControlCheck, ConstructionTrapCheck green, bit-identical with no record.
  The review caught one wrong read (the terms subtracted the control's record where FC2's pooled claim was meant), mended.
- Slice B2 (choices): --fast green; ForeignCrewsCheck, ConstructionControlCheck green. SaveFileCheck: one red, "REV-HC1 P5: the
  populated format-62 city continues its card stocks, services, loan book and capital", with 26 handovers in the harness, 17 in P5's
  city. The one leaf apart at the opening comparison is /sectors[1]/extras/familyRentWeight, live 14304.000000000002 against loaded
  14304.000000000004 (1 ulp). **Main's own load path:** the save keeps the total rent weight T and the studio weight S
  (Game.java ~18208-18209, DataSave.rentWeight, rentWeightStudio) but not the family weight F; the load rebuilds F as T - S (Game.java
  ~21221 and ~22813-22814), which is F only when the sums carry no dust. On FC2b's path P5's city has T = 18464.000000000004, S = 4160,
  F = 14304.000000000002, and T - S is the loaded figure. On main the same fixture sums exactly (T 15524, S 2240, F 13284), and main's
  own data/slot-02.json at m62 shows the same mismatch (F 3834.0000000000005, T - S 3834.0) with no FC2b code in it. None of FC2b's
  code touches the rent weights, FamilyModel, RealEstate or their save fields. So the known-reds row passes it to FRW1 (queued in the
  ship file): save F, or keep the value RealEstate.restoreExtras() already restores exactly. The review moved the card's "already
  with the foreign crews" before "not the city's own site" (a handed site is not isCitysOwn()).
