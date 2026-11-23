# LP-G1: keep ground ahead across the player's long skips

Found while checking PW5 on the HC1/AP2 merge, 2026-11-22. This is the
reference player's ground-buying timing, not an electricity capacity or
repair-price exception. Keep the model's power, repair, financing and
conservation rules intact.

The default AUTO2 seed 0 on PW5 at aebb83eae reports 12.283026838 unplaced
households at m807, in a sixteen-month window m792-807. From m760 through
m800, the landlord keeps asking but is held for land: 5,570 m² needed,
922 m² free, no home pipeline. Power is fully served, the mortgage capital
room is unlimited and 114-139 construction points remain after repairs.
The last ground look is m743; the next is m805. At m805 the player buys
three parcels within the unchanged cash-share budget. Homes enter the
pipeline, 63 stand at m808, and no household remains unplaced.

Current main 1b89c5c4c shows the same class without PW5. A native replay
through m830, with read-only observations added to LongPlaytest only,
has ground looks at m741 and m804. At m760, m792 and m800 the landlord is
held for land (5,570 m² needed, 286 m² free), with no homes on site,
unlimited mortgage capital, full power and 268-367 points left for sites.
Main has more housing room and does not flag unplaced households in that
window. The deferred ground purchase and construction hold occur on both.
The diagnostic replay's limit does not change any controller input before
that limit; its later continuation findings are not treated as default
2,400-month findings.

`LongPlaytest.keepGroundAhead()` reserves room by the business investment
growth factor over `BuildAdvice.HORIZON`, six months, with the existing
slack. It runs at the advisor's looks. The player can then skip over sixty
months, leaving that reserve exhausted long before it next buys ground.
Do not fix this by increasing the cash-share dial or by relaxing the
housing finding. Make the player's look timing and its land horizon agree,
including a real hold when its unchanged budget cannot buy ground.

The harness must cause a growing town whose current ground covers the
short horizon but not the proposed skip. Show the old main's unserved
ground look and native housing order hold, then require a timely look or
adequate reserve across the skip. Cause an unaffordable offer as well;
keep that budget hold, land payments, every money audit and save/replay
comparison. The actual model inputs, not a picked played month, should
cause both sides. The default replay above is retained as an integration
finding and must cease flagging once the timing is mended.

Evidence and exact figures are in PW5-notes.md. Local diagnostic logs:
target/PW5-main191-traces and target/PW5-native-main-land. Both are m830
replays; the full default failure is in the sealed gate run
/home/jerus/cbs/runs/cbs-gate-PW5-MozBD0WW. Any other ensemble seed or
finding needs its own trace; this brief grants no blanket F1 exception.
