# ER1Check.java - 279 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/ER1Check.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The growth hold when a full city's treasury cannot buy a whole land offer, and its repair: the offer funded on the
> land office's bond while a sector waits on ground (FB11).
> 
> WHY: ER1's Rotterdam replay has willing private investors, no free plots,
> and a builder with no orders: the player kept ground ahead out of a tenth of
> its cash and bought no whole offer for hundreds of months. FB11 (ER1-F)
> funds that ground on the funding page's bond while a sector waits on it and
> the player's own test for borrowing carries it (LongPlaytest, GROUND FUNDED
> WHEN A SECTOR WAITS ON IT). This fixture consumes the free ground and
> removes the city's purchasing cash, then proves, on the actual private build
> path each month: with nothing waiting on ground the share alone decides; a
> city its own test will not lend to still holds its jobs every month; a city
> it will lend to buys the whole offer on the bond, its cash paying no more
> than the share, the sellers paid, the month's money audit closed, and the
> held order released; land paid from the vault is not the bond's; and
> restoring the cash still buys through the share. The long replays'
> measurements belong in docs/queue/ER1-notes.md and docs/queue/FB11-notes.md.

**Uses:** [LongPlaytest](LongPlaytest.md) (41), [Game](Game.md) (17), [LandParcel](LandParcel.md) (6), [LandManager](LandManager.md) (5), [LandMarket](LandMarket.md) (4), [BuildingManager](BuildingManager.md) (3), [JobType](JobType.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Investor](Investor.md) (2), [DebtManager](DebtManager.md) (2), [BuildAdvice](BuildAdvice.md) (2), [MoneyAudit](MoneyAudit.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `ER1Check.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Suppresses the game's month narration while the fixture runs. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 26 | `static int fails` |  |
| 27 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 255 | **type** `public class ER1Check` | The growth hold when a full city's treasury cannot buy a whole land offer, and its repair: the offer funded on the land office's bond while a sector waits on ground (FB11). |
| 31 | 4 | `static void check(String label, boolean ok)` |  |
| 36 | 5 | `static void quietly(Runnable action)` |  |
| 42 | 4 | `static long posts(BuildingManager buildings)` |  |
| 48 | 10 | `static Game fullCity(String label)` | A scratch city with its free ground consumed and no cash in its treasury, one month played. |
| 60 | 4 | `static void fundShop(Game g, BuildingsTemplate shop)` | The shop's private buyer given twice its whole quote, ground included. |
| 66 | 3 | `static double gap(Game g, LandParcel offer)` | The part of the best offer for room a look's cash share cannot pay: its local price less a tenth of the cash. |
| 71 | 4 | `static boolean auditClosed(Game g)` | Whether the last month's money audit closed, by the ensemble's rule (tools/ensemble, auditResidual()): no residual past a cent and a part in ten million, no drift past a cent. |
| 76 | 9 | `public static void main(String[] args) throws Exception` |  |
| 87 | 121 | `static void held()` | The holds: nothing waiting on ground, then a city past its own test for borrowing; then the cash restored. |
| 210 | 69 | `static void funded()` | The repair: the same full city, its own test willing to lend, buys the demanded ground on the funding page's bond. |

