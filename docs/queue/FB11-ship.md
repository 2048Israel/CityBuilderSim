<!-- readiness: provisional -->
## changelog
### {next} — FB11, held towns: why no works pays, and the advisor's land funding — 2026-11-25, ready, see docs/queue/FB11-notes.md
The playtest's player buys the ground a sector waits on with a bond, where its cash share could not. While the city's landlords or a works wait for
ground, the player pays what the share allows from the cash and raises the rest, an overdraft with it, on the funding page's bond (the land office's
own route). It does so only when its own test for borrowing carries it: the interest on the whole debt, the new bond in it, at the bond's quoted
rate, at most a quarter of the tax (main's rule, no principal in it). Never when the land is paid from the vault; never for ground nobody asked for.
LandMarket's whole offers, prices and sellers are untouched; `-Dplaytest.groundFunding=off` plays the old player. In Rotterdam's 1900 city, held
283 of 401 months for ground on 0.9.206 with up to 394,000 in its treasury, the landlords waited 4 months and it held 443,756 people at month
1,302 (42,503 on main). Measured again on 0.9.206, the held towns of seeds 3, 11 and 13 (RE3-J) and seed 14's housing finding (FB11H) are gone
on both foundings, so those two rows left the batch.
## queued
| ARL1 | The test player's servicing test with principal (REV-FB11): LongPlaytest.canService() counts only the interest on the whole debt at the marginal quote, no principal and not the book's own coupons, and FB11's funded Rotterdam (today's founding) pays 32-37% of its tax in interest at m800-1200. Test principal and interest at the book's own coupons against the city's own revenue, as Ontario's Annual Repayment Limit (at most 25% of own-source revenue for principal and interest) | [FB11 notes 8](docs/queue/FB11-notes.md#8-the-review-rev-fb11-and-its-mends) |
## known-reds
CopperRefiningCheck | seed 1's 2,400 months stand at most ROD_MILLS_STANDING (1) Rod Mill | FS8 | 2026-11-25 | FB11's seed 1 ends mid-wave with 23 Rod Mills (main's player, -Dplaytest.groundFunding=off: 0); main's own run stood 17 at m1353, 16 at m1693 and 9 at m2028, built and shed by SpreadPlanner on rod's real 1-2% over the city's exported cathode, Copper Refining never held for ground in either run (FB11-notes 7); FS8 already takes section 11's premise
