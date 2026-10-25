# LandmassView.java - 35 lines · 3 methods · 1 constants · model

`ham/citybuildersim/LandmassView.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that
> the words are written in one place and a harness can rebuild them from getters (ET5, 2026-10-25).
> 
> WHY THIS EXISTS. A screen must not recompute a model figure, and a refusal the investors write ("no land - needs ...")
> and the Build card's gate must say the same thing about ground across the water in the same words. LandClassView is
> the shape: a pure class beside the model that the interface calls. Slice A of ET5 puts here the two things the model's
> own words and the pickers need - the investors' clause and the ISLAND tag's share; the screens' heads, rows, chips and
> link sentences join them with the interface.

**Uses:** [LandParcel](LandParcel.md) (1), [LandManager](LandManager.md) (1)

**Used by (3):** [BusinessInvestment](BusinessInvestment.md), [Game](Game.md), [LandmassCheck](LandmassCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `LandmassView.ISLAND_TAG_SHARE` | `0.5` | The share of an offer's dry ground that must lie on landmasses the city has not linked for the land office to tag the offer ISLAND (before BEST VALUE and the rest): half. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 21 | **type** `public final class LandmassView` | What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that the words are written in one place and a harness can rebuild them from getters (ET5, 2026-10-25). |
| 17 | 1 | `private LandmassView()` |  |
| 23 | 3 | `public static boolean islandTagged(LandParcel p)` | Whether the land office tags an offer ISLAND: it has dry ground on a landmass the city has not linked, and at least ISLAND_TAG_SHARE of its dry ground is such. |
| 32 | 3 | `public static String unlinkedWords(double freeSqFt, String names)` | The clause the investors' land refusals end with when the city owns ground it cannot build on: "12.0 km² is free on Island 2, which no port links". |

