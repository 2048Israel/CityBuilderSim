# WN5b - WN5's review follow-ups ({next}, 2026-11-02)

**Where it stands (paused 2026-11-02 on the orchestrator's word; resume when step 0's wait on DM1 ends):**
1. Slice 1 (the hover's quote off the FX thread) is done, UNREVIEWED: this WIP commit (LandMarket.Quoter, MapView's QUOTES thread, LandMap.QUOTING, LandNationsCheck 12, Timing.HARNESSES); LandNationsCheck and TimingCheck were green on it.
2. Slice 2 (island sentence, receiptLine by largest remainder, realEffectivePartners + Trade line, refill flag) was stopped mid-way: WIP commit 25e62936 on branch worktree-agent-a0f9245fffac0505d (its worktree .claude/worktrees/agent-a0f9245fffac0505d), uncompiled; its RealDollarCheck/LandNationsCheck 11 parts may be unfinished.
3. Next: branch batch/WN5b from origin/batch/DM1 (or main, step 0's rule), cherry-pick both WIP commits, finish and review slice 2, `./batch-check.sh --pick` with JDK_JAVA_OPTIONS naming the main checkout's data/earth-2.bin and data/world-1.bin, then these notes and the ship file.
4. Pending row to write: a click that picks a block still drafts (LandMarket.draft -> split) on the FX thread; WN5b-C the eye-check.
5. ★ to report: item 4 is moot on today's file (no nation's 1900 money lacks an opening rate: WN6's fallback fires for none); kept, held in RealDollarCheck.

## The measurements (scratch probes, 2026-11-02)

One cold `LandMarket.quote()` of a hover block (2^level plots a side), which since WN5 runs `LandSellers.split`:
across the Detroit River (Windsor) level 7 up to 17 ms, 8 45 ms, 9 152 ms, 10 419 ms, 12 752 ms; off Gibraltar's
coast level 8 76 ms, 9 159 ms, 10 449 ms, 11 1.3 s, 12 5.3 s. On a copy of Jerus's autosave (level 8, 3.76M plots,
90 purchases) up to 76 ms over 75 blocks along its edge. Without sellers the same quotes are under 20 ms.
`CityLand.copy()` on that city: 0.2-1.1 ms. The month runs on the FX thread (UserInterface's AnimationTimer), so the
worker quotes a copy of the land (Game.MapDraft's pattern), never the live one.

Item 4: on world-1.bin (LC3's), all 196 nations' first-January money has a rate in WorldMoneys' opening month
(Historical and Random), so `RealDollar.moneyAtFounding`'s fallback to the file's first-year rate is taken by none.
