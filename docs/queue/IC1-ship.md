## changelog
### {next} — IC1, the builders' idle crews free for other work — 2026-11-01, ready, see docs/queue/IC1-notes.md
After a boom the builders go idle at their core crew and lay off most of their posts, and the staffing gate counted those
posts as spoken for (0.7.17: they "are hired back when work returns"), so its spare was the unemployed less the crews:
8-11 sectors stood at "the city could staff 33%", nothing was ordered and the crews stayed laid off (XS1: seed 11 held
1,300 months at 20,000 people). Now an idle builder's laid-off posts are spare for every planner's gate (Sector.staffing());
a working builder's, and other sectors' withheld posts, stay spoken for. Over the 16 AUTO2 seeds the town is 77% larger at
m300 and passes 40,000 at m976 against m1,033, the median end unchanged (335,718); seed 11 passes it at m770. A 12- or
24-month grace before the crews are spare was measured and not taken: the slow seeds are the tail, reshuffled. A founding
depot's village now opens its shop. WantedPostsCheck 11 causes the trap and shows it released; nine harnesses' fixtures
cause their conditions again on the moved paths; ScaleCheck 9 (ST2's town) and KeptCheck 15 (KH1) red. Ensemble 22/26
(LD1 23/26: A3, one seed at its boundary).
## pending
| IC1-R | The ensemble's A3 turns red again on IC1 ({next}): the longest deflation spell from m240 is 5 months at the median [0-12], one seed at 12 against "< 12 in every seed" (LD1 4 [0-11], 0.8.88 6 [0-13]). R2 reads +0.19 points [-3.65 to +2.94] on IC1, LD1-R's reading (LD1 +0.38). ★ Recommended: accept, a seed at the boundary. | his call | docs/queue/IC1-notes.md §6 |
## queued
| KH1 | City History's kept page lays out 4 px shorter than a page built new (KeptCheck 15, met on IC1's path at m822: the scroller 731 px kept, 735 new): `HistoryScreen.frameOver` binds the scroller's height to the frame's, and when a kept draw shrinks the frame (133 to 129 px) the invalidation lands inside the column's own layout pass, where JavaFX drops the relayout request. Fix candidate (untested): once per frame, a listener on the frame's height that asks the scroller's parent for a layout in `Platform.runLater`; KeptCheck the proof. No model change. | [IC1 notes §6](docs/queue/IC1-notes.md) |
