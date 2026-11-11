# LandSellersView.java - 117 lines · 5 methods · 2 constants · model

`ham/citybuildersim/LandSellersView.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> What the land office says of who sells an offer's ground: a pure function of the offer's sellers (LandParcel.getSellers()) and the nations' names
> (LandSellers.file()), so that the words are written in one place and a harness can rebuild them from getters (WN5, 2026-11-01).
> 
> WHY THIS EXISTS. Slice 2 of WN5 made every offer remember its sellers and priced each part at its seller's land factor, and left the card saying
> nothing of it: a plot at Windsor on the Detroit bank cost more than the one across the river and nothing on the card said whose it was. The receipt
> names the sellers after the sale (Game.buyLandParcel()); this is the line the office writes before it, under the size and beside the ground's and
> the island's lines (LandClassView, LandmassView). A screen lays it out and recomputes nothing. An offer of the host's own ground, an offer on a
> generated world and an offer with no seller named have nothing to say, and the card is drawn exactly as it was.

**Uses:** [LandParcel](LandParcel.md) (8), [LandSellers](LandSellers.md) (3)

**Used by (3):** [Game](Game.md), [LandNationsCheck](LandNationsCheck.md), [LandScreen](LandScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `LandSellersView.LEAD` | `"Sold by"` | The words the line opens with. |
| 27 | `LandSellersView.SEPARATOR` | `" · "` | What stands between two sellers: a middle dot. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 19 | 99 | **type** `public final class LandSellersView` | What the land office says of who sells an offer's ground: a pure function of the offer's sellers (LandParcel.getSellers()) and the nations' names (LandSellers.file()), so that the words are written in one place and a ... |
| 21 | 1 | `private LandSellersView()` |  |
| 35 | 13 | `public static String offerLine(LandParcel p, LandSellers sellers)` | The line under an offer's size: "Sold by United States 62% · Canada 38%" for an offer sold by several nations, each part's share of the offer's area in whole percents that add up to 100 (percents()), the sellers in th... |
| 54 | 32 | `public static int[] percents(List<LandParcel.Sale> sold)` | Each part's share of the offer in whole percents, in the order of the parts, adding up to 100 exactly: the share is the part's area over the offer's (its dollars over the offer's where the offer has no area, an equal ... |
| 96 | 12 | `public static String receiptLine(List<LandParcel.Sale> sold, LandSellers sellers, Function<LandParcel.Sale, String> money)` | "Sold by United States (62%, US$1.3M) and Canada (38%, C$1.1M)." - the receipt's sentence for an offer bought from several nations or from one foreign nation: each part's share of the offer's area (percents(), the car... |
| 110 | 7 | `private static String name(LandSellers sellers, int nation)` | A nation's name as the world file gives it, "nation N" for a number the file does not know. |

