# LD2 - the wanted posts' bounds and comments, after LD1

Queued 2026-10-31 from the stranger's review at LD1's gate ("ship +
follow-up": no save corrupted, no runaway, no cash moved). Small: Sonnet
whole, unless point 3 turns out large. LD1's notes are the background.

1. **The timing is described wrongly.** `startOfMonthUpdate()` runs
   `runPrivateInvestment()`, which ends in `settleWantedPosts()`, and then
   `simulateMonth()` reaches `repriceLabour()` (SimulationEngine about
   139), all inside one `nextMonth()`. So the asks are priced in the same
   month, not "next month", and the saved `wantedPosts` is replaced by the
   next settle before the model reads it. It only feeds the People caption
   between a load and the first month.
   - Correct DataSave's "A FLOW, AND A LAGGED ONE" and WantedPosts' "next
     month" wording.
   - WantedPostsCheck section 9 calls `repriceLabour()` straight after the
     load, an order the game never runs. Step a real month instead, so the
     section fails if the field is deleted (or say why the field can go).
2. **The 1.41x cap uses last month's posts** (`WantedPosts` about 128/137;
   `Game` about 15010). `repriceLabour` adds the latent posts to this
   month's recount, which comes after completions, `strikeBuildersCrews()`
   and `runRetirement()`'s closures. If a band's posts fall in the recount,
   the latent can exceed them for one month. Clamp the latent to
   `MOST_OF_POSTS x posts` inside `repriceLabour`.
3. **Lower bands priced on less supply** (`Game` about 15011,
   `supplyByBand(posts)`): `fillByBand` carries a band's surplus down into
   the bands below, and the latent posts absorb that carry. So a lower band
   with no ask of its own can be priced past 1.41x (diploma posts filled by
   college overflow), toward `MAX_MULTIPLE` in the limit. It is bounded by
   the asking band's spare and ends with the hold. Measure it in a real
   city first (WantedPostsCheck 4 measures only the band that asked). If it
   matters, keep the carry out of the latent.
4. **A band closed to migrants still bids** (`Migration.admits()` false): a
   lasting premium no migrant can answer. Recommended: a closed band asks
   nothing, since the wage is meant to bring migrants.

Each point's fixture causes it, and no premise moves.

## 5. A hands-off city's bank fails more often since LD1

Found by EP1's slice D (2026-11-01, EP1's notes §8) in TreasuryCheck's
setting: a hands-off default city's bank fails within 120 months in 0 of 4
seeds before LD1, 3 of 12 after it, 4 of 12 on EP1. Count it over the twelve
seeds on main first, then find which of LD1's bids (or points 2-4 above)
drains the bank: the wage bill outrunning the loans' income, or the deposits
leaving with the migrants the wage did not bring. A fix keeps LD1's purpose
(a business short of workers bids the wage up, Jerus 10-31) and brings the
count back to main's before LD1; the harness counts it in the twelve seeds.
