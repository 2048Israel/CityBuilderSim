# CapSetting.java - 78 lines · 8 methods · 0 constants · model

`ham/citybuildersim/CapSetting.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> A cap on the yield an automatic sale takes, saved and said (D28).
> 
> WHY (CD2, slice 6): the rollover and automatic building each sell the city's
> paper with nobody at the button (Jerus, 2026-10-12: "each its own"), so each
> keeps the cap the player gave it - fair plus a point by default, another
> tail or yield, or Fill it. Auction.Cap is a record carrying NaN for Fill it,
> and Gson does not write a NaN, so a saved cap is the two plain fields of
> {@link Saved}: the mode's name and a value that is 0 where the mode has none.
> The words a log line and a decision use for a cap are here too, so the
> rollover and the builder say it the same way.

**Uses:** [Auction](Auction.md) (21)

**Used by (5):** [AuctionCheck](AuctionCheck.md), [AutoBuilder](AutoBuilder.md), [BuildScreen](BuildScreen.md), [DataSave](DataSave.md), [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `public String mode` |  |
| 24 | `public double value` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 62 | **type** `public final class CapSetting` | A cap on the yield an automatic sale takes, saved and said (D28). |
| 19 | 1 | `private CapSetting()` |  |
| 22 | 11 | **type** `public static final class Saved` | A cap as it is saved: the mode by name, and the tail or the yield (0 for Fill it). |
| 26 | 1 | `public Saved()` _(in CapSetting.Saved)_ |  |
| 28 | 4 | `public Saved(String mode, double value)` _(in CapSetting.Saved)_ |  |
| 35 | 4 | `public static Saved save(Auction.Cap cap)` | The cap, as two plain fields. |
| 41 | 12 | `public static Auction.Cap restore(Saved saved)` | ...and back. |
| 55 | 5 | `public static boolean same(Auction.Cap a, Auction.Cap b)` | Whether two caps are the same setting. |
| 62 | 9 | `public static String words(Auction.Cap cap)` | The cap in words: "fair plus 1 point", "no more than 4.50%", "Fill it". |
| 73 | 5 | `private static String places(double points)` | A number of points to as few places as it needs and never more than two: "1", "0.5", "1.25". |

