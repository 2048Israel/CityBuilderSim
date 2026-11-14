# BF1-M — A connected plan leaves a disconnected street raster

Found by BF1 on 7e29b76e, independently reproduced by compiling unchanged
e5642145 CityMap, DistrictPlan, TilePainter and MapCheck against the same
inputs. No BF1 code changes those classes. MapCheck's played city at month
121 has a connected district plan but two road components in its raster:
625 plots bounded by (371728,344352)–(371791,344416), and 67 plots bounded by
(371727,344406)–(371752,344416). Its own ground joins the two components, so
MapCheck 8's network assertion fails. No road plot is alone, and every
building is within reach. This is a rendering/planning fault reached by the
changed economic path, not money lost or a bank-capital exception.

Reproduce on BF1: call MapCheck.playedCity(TempDirs.create("bf1-map")), then
MapCheck.networkOn(city.getCityMap(), city.getMapCounts(), "played", false).
For the district plans, read CityMap.planInput, DistrictPlan.make and
PlanCheck.figures; the plan is one piece while MapCheck.Raster reports two.
The final-source replay is target/checks/bf1-main-mapper-7e29.log in BF1's
worktree. The same oracle runs in the retained BF1 gate.

Find which painted street/curve or covered crossing parts the raster,
cause it with a small fixture, and restore its connection without moving
placed buildings, widening bridges or relaxing the ground/network rule.
Done means MapCheck 8's network law is green on these inputs and the
existing planner/painter fixtures hold.

The same city has + junctions four plots apart, already PX1's class and
queued there. MapCheck 1's road-surface count miss is covered by its existing
DM3 record. This row does not transfer either of those repairs.
