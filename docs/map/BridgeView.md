# BridgeView.java - 98 lines · 11 methods · 0 constants · model

`ham/citybuildersim/BridgeView.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> What the build card says of a long bridge's crossing: the refusal of a survey in its own words, and the pieces of the
> line a surveyed crossing gets. A pure model of what a screen draws; the money and the points are formatted by the screen.
> 
> WHY THIS EXISTS. spec-earth2-terrain 6.3 gives the four refusals "word for word as the card shows them" (board 5), and
> the model, the card, the refusal page and the harness have to say them the same way: every figure in a sentence is the
> survey's, every name Landmasses.name()'s, and no screen works a length or a depth out again. The words are filled from
> the survey (the nearest deep plot's depth and distance, the length found, the landmass the bridge was asked toward).

**Uses:** [BridgeSurvey](BridgeSurvey.md) (5), [Landmasses](Landmasses.md) (3), [EarthData](EarthData.md) (1)

**Used by (4):** [BridgeCheck](BridgeCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [ReadPathCheck](ReadPathCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 87 | **type** `public final class BridgeView` | What the build card says of a long bridge's crossing: the refusal of a survey in its own words, and the pieces of the line a surveyed crossing gets. |
| 14 | 1 | `private BridgeView()` |  |
| 17 | 3 | `public static String landmassWords(Landmasses lms, int id)` | What a landmass is called in a sentence about a bridge: "the city's own landmass" for home, "Island 2" as it is named, and "Halifax's landmass" for a name the data gives (it comes possessive). |
| 22 | 4 | `public static String landmassWords(boolean home, String name)` | ...from the landmass's own name: home's is "the city's own landmass", a possessive name ("Halifax's") takes "landmass", any other ("Island 2") stands as it is. |
| 28 | 3 | `static String km(double metres)` | A length in kilometres to a tenth: "12.4 km". |
| 33 | 3 | `public static String lengthWords(double metres)` | A crossing's length as a card says it: "12.4 km", and under a kilometre in metres, "420 m". |
| 38 | 4 | `public static String capWords()` | The cap as the refusals say it: "36 km". |
| 44 | 3 | `public static String shallowWords()` | The depth the shallow sea is counted to as the refusals say it: "30 m". |
| 56 | 3 | `public static String refusal(BridgeSurvey.Result r, Landmasses lms)` | The survey's refusal in the card's words, or null when the crossing can be ordered: "No bridgeable path: deep water (41 m) 2.1 km out from Island 2. |
| 61 | 20 | `public static String refusal(BridgeSurvey.Result r, java.util.function.IntFunction<String> words)` | ...with the landmasses named by `words` (a landmass's id to how a sentence calls it): what the harness runs on a ground that has no Landmasses. |
| 86 | 3 | `public static String crossingWords(BridgeSurvey.Result r, Landmasses lms)` | The crossing of an order that can go ahead, without the money: "To Island 2: 12.4 km over water up to 24 m deep (mean 11 m)" or, over fresh water, "To Island 2: 3.2 km over fresh water". |
| 91 | 7 | `public static String crossingWords(BridgeSurvey.Result r, java.util.function.IntFunction<String> words)` | ...with the landmasses named by `words`. |

