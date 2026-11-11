# ChipRender.java - 101 lines · 3 methods · 1 constants · tools

`ham/citybuildersim/tools/ChipRender.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> Renders the island chip for review (MT1, BK1-L): the chip as it was (one line, a 3 px dark stroke) and as it is (two
> lines on a dark rounded plate, IslandChip.draw()) over the amber hatch and tint of a landmass no port links.
> 
>     java -cp "target/classes;<gson.jar>;<javafx jars>" ham.citybuildersim.tools.ChipRender [outFile]
> 
> outFile is docs/queue/MT1/render-chip.png unless named. The ground is a stand-in (a green with the hatch of the same
> amber at the same strength); the chip is the game's own draw, in the game's face (Plex Mono, else Courier New).

**Uses:** [IslandChip](IslandChip.md) (4)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `ChipRender.AMBER` | `"#e3b341"` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 73 | **type** `public final class ChipRender` | Renders the island chip for review (MT1, BK1-L): the chip as it was (one line, a 3 px dark stroke) and as it is (two lines on a dark rounded plate, IslandChip.draw()) over the amber hatch and tint of a landmass no por... |
| 31 | 1 | `private ChipRender()` |  |
| 35 | 19 | `public static void main(String[] args) throws Exception` |  |
| 55 | 46 | `private static void render(Path out) throws Exception` |  |

