# BK2b - BK2's review follow-ups: each holder's own discount

Brief: [BK2b.md](BK2b.md); accounting decisions: [BK2-notes.md](BK2-notes.md).
Started from main 6114b00f8 on batch/BK2b. No known-reds entry names BK2b.

## Changes and calls

- The Game issue comment now describes each holder's discount remaining unchanged at a sale; nothing else in Game is changed.
- ★ Apply pass()'s HOLDING_ROUNDING rule in unaccretedOn(Holder, face), the discount quote retirePart uses. Keeping the quote and retirement together also hands the bank's buyback booking the same discount the line removes. A remainder above the model's relative floor still keeps proportional discount.
- The ulp-short fixture buys Math.nextDown(held) from each of BANK, HOUSEHOLDS, FIRMS and WORLD, on a settled five-holder piece, at today's and PriceLevel.FACTOR's scales. It proves the positive remainder is below the model's floor and the old proportional calculation leaves positive discount dust; it checks the discount quoted and retired exactly, the other holders unchanged, and a control above the floor.
- ★ Add a separate premium fixture using the same city and auction as 13a, with its first sale at .001 of spare household cash. Existing fixtures keep .15. This makes the next premium exceed the bank's positive discount, with a hard premise assertion. Its no-restrike twin proves the loss routes to retained and equity's movement/split, outside income, tax, dividends and paper gains.

## Checks

- Before the model fix: --fast green (zero firm Stale findings). HoldersCheck causes exactly 16 new failing assertions: all eight ulp-short holdings fail both full-discount handover and zero-discount-after-retirement. The new premium case already passes before the fix (premium 0.523920656k, positive own discount 0.070544306k, shortfall and retained loss 0.453376350k), as expected: it covers existing accounting, not a new behavior. Evidence: target/BK2b/holders-before.log and target/BK2b/check-before.log in this worktree.
- After the fix: ./batch-check.sh HoldersCheck BuybackCheck exits 0; both harnesses green, maps regenerated, zero firm Stale findings. Evidence: target/BK2b/holders-after.log and target/BK2b/check-after.log. The ulp-short assertions are now green with the same fixture; controls above the floor remain proportional. Existing HoldersCheck laws also pass.
- sh tools/pick.sh selects ALL 208 harnesses (Game has 172 readers), with money review required for Debt. Per the starter's one-ALL-run instruction, no local ALL pick is run; the normal launcher dry run supplies the full merge verdict.
- Only the specified comment changes in Game. All three edited source files retain LF. No save schema, save slot, process machinery or gameplay dial changed.
- Default trace and launcher dry run: pending.
