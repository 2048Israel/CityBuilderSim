# FD1 slice A - the last station is kept while it has a station's freight

You are a coding slice of batch FD1 of CityBuilderSim (Java 21, one Maven project). Edit the
worktree you are in; commit nothing. CLAUDE.md is loaded; it binds you (LF line endings, a dial
asserted against its constant, a harness fixture that causes the condition).

## Why

The default playtest's Freight sector sells its only station (a Rail Depot) by the DISTRESS rule
(`BusinessInvestment.planDistressRetirement`, `BusinessInvestment.java:736`: 24 months of losses,
overdrawn) while the freight offered is two to four times what plan() orders a first station at,
then buys one again when credit returns: 4 last-station sales in 4,000 months, bought again 27 and
79 months later at $339m and $990m against plots of $0 and $20m; the fourth (m3844) gave up a
railway billing $261m a month against $31m running. The one sale with too little freight (10,940
tonnes, under the 30,000 rung) was rightly not bought back for 646 months.

`Freight.mayRetire()` (`sectors/Freight.java:1840`) keeps the last station only while a spur or a
line stands (FR7). plan() (`Freight.java:1634`) orders a first station when
`gap.station() >= firstStation.getStationTonnes() * MIN_LINE_UTILISATION`, `firstStation` the
smallest station in the catalogue (the loop near line 1700), `gap = shortfalls()` (line 1115).

## The change (sectors/Freight.java)

1. A helper `public BuildingsTemplate firstStation()`: the smallest station of this sector's
   catalogue by `getStationTonnes()` (the loop plan() has now), null with no catalogue
   (`buildings == null`) or no station. plan() calls it in place of its own loop; nothing else in
   plan() moves.
2. A helper `public boolean wouldOrderAgain(BuildingsTemplate station)`: whether selling this
   station would leave the stations short by the first station's rung, the tonnes plan() orders a
   first station at: `shortfalls().station() + station.getStationTonnes() >=
   firstStation().getStationTonnes() * MIN_LINE_UTILISATION`; false for a non-station, a null
   `firstStation()` or `buildings == null`. Javadoc: what it is, then that it is plan()'s rung so
   the two cannot drift.
3. `mayRetire()`: the last station (stations standing <= 1) is also refused when
   `wouldOrderAgain(t)`, as well as while a spur or a line stands. Keep the existing branch's
   shape; one condition grows `(track standing > 0 || wouldOrderAgain(t))`. Extend its javadoc
   with a paragraph `...nor while the freight it carries would order a station again (FD1, {date
   2026-11-07})` and a WHY sentence with the figures above (4 sales, bought again 27 and 79
   months later at $339m and $990m against plots of $0 and $20m; MF1 counted 16 in 3,140 months
   before BA1, RT1 and R1b). With a second station standing either may still go.

## The harness (RailDepotCheck.java)

Add item `9. THE LAST STATION IS KEPT WHILE IT HAS A STATION'S FREIGHT (FD1)` to the header's
"what it has to prove" list, and a section 9 after section 8 (before the `fails` exit), in the
file's style (`/* ===== 9. ... */`, `System.out.println("\n--- ... ---")`, `assertTrue`/`report`
with labels that read as sentences, every figure from the templates and constants: `depotNeeds`
is already `depot.getStationTonnes() * Freight.MIN_LINE_UTILISATION`). Fixtures with `Fx`
(`stand`, `offer(bulk, goods)`; goods tonnes are not served by sidings, use `offer(0, tonnes)`
so the sidings take none):

- a lone Rail Depot offered `depotNeeds` goods tonnes may not be sold (`!mayRetire`); a tonne
  under (`depotNeeds - 1`) it may.
- the premise, measured from both sides: a city with no station offered `depotNeeds` orders a
  Rail Depot (`plan()`), and a tonne under orders nothing: the sale the guard refuses would be
  bought back.
- the distress rule itself: `plans.recordSectorResult(fr.key(), -1)` called
  `BusinessInvestment.DISTRESS_LOSS_MONTHS` times, then
  `plans.planDistressRetirement(fr, -1, 0)`: with `depotNeeds` offered it sells nothing
  (`!decision.build`); a tonne under it sells the Rail Depot (`build` and `template == depot`).
  This is the assertion that was red before the change: check it is (stash nothing; reason it
  from the old code, or run it once against the old mayRetire by hand and say so).
- a lone Rail Terminal offered `depotNeeds` may not be sold either (a depot would be ordered).
- two Rail Depots standing offered far past the rung (say `3 * depotNeeds`): one may go (the
  guard is the last station's).
- section 8's assertions stay exactly as they are (their fixtures offer nothing).

## Run

    export JDK_JAVA_OPTIONS="-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin"
    ./batch-check.sh RailDepotCheck RailCheck HomeRailCheck

(in Git Bash, Bash tool timeout 600000). Logs in `target/checks/`; grep them, never cat them.
Never open Game.java whole; `java -cp target/classes ham.citybuildersim.tools.Where Name -print`
prints one method.

## Report (15 lines at most)

What changed, file:line; the checks' result; whether section 9's distress assertion was red on the
old rule and how you know; anything you were unsure of.
