# BK2b - BK2's review follow-ups: each holder's own discount

Brief: [BK2b.md](BK2b.md); accounting decisions: [BK2-notes.md](BK2-notes.md).
Started from main 6114b00f8 on batch/BK2b. No known-reds entry names BK2b.

## Changes and calls

- The Game issue comment now describes each holder's discount remaining unchanged at a sale; nothing else in Game is changed.
- ★ Apply pass()'s HOLDING_ROUNDING rule in unaccretedOn(Holder, face), the discount quote retirePart uses. Keeping the quote and retirement together also hands the bank's buyback booking the same discount the line removes. A remainder above the model's relative floor still keeps proportional discount.
- The ulp-short fixture buys Math.nextDown(held) from each of BANK, HOUSEHOLDS, FIRMS and WORLD, on a settled five-holder piece, at today's and PriceLevel.FACTOR's scales. It proves the positive remainder is below the model's floor and the old proportional calculation leaves positive discount dust; it checks the discount quoted and retired exactly, the other holders unchanged, and a control above the floor.
- ★ Add a separate premium fixture using the same city and auction as 13a, with its first sale at .001 of spare household cash. Existing fixtures keep .15. This makes the next premium exceed the bank's positive discount, with a hard premise assertion. Its no-restrike twin proves the loss routes to retained and equity's movement/split, outside income, tax, dividends and paper gains.

## Checks

- Before the model fix: --fast green (zero firm Stale findings). HoldersCheck causes exactly 16 new failing assertions: all eight ulp-short holdings fail both full-discount handover and zero-discount-after-retirement. The new premium case already passes before the fix, as expected: it covers existing accounting, not a new behavior. Evidence: target/BK2b/holders-before.log and target/BK2b/check-before.log in this worktree.
- Final named checks, selection, trace and launcher dry run: pending.
