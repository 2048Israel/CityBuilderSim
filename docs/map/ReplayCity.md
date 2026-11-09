# ReplayCity.java - 144 lines · 15 methods · 1 constants · model

`ham/citybuildersim/ReplayCity.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The throwaway copy of a recorded city that replay shows (TR1, 2026-10-31): a Game of its own, in a scratch folder, that loads
> any recorded month and is marked a replay copy (Game.markReplayCopy) so that it never records, and the recording and the
> player's folder are never written. The month on screen is loaded in full ({@link #loadWhole}), so every screen shows exactly
> that month; while the time bar plays, only the map's sidecar is swapped ({@link #stepQuick}), and the caller loads in full when
> it comes to rest. No JavaFX, and everything runs on the calling thread - the FX thread in the game, never a worker: the
> copy's Game is read by the screens as it loads.
> 
> WHY IT EXISTS (TR1): Jerus films the game's trailer from a recording, as often as he likes, and clicks around the city in
> navigation mode; "nothing sticks" - whatever a click changes in the copy is gone the next time a month is loaded, and moving
> in time reloads from the recording. A full load is 330 to 800 ms on a 360-month Earth city, too slow for play at 1 to 16
> months a second; the map is the part that moves, and it is tens of KB.
> 
> The copy's save slot is {@link #SLOT}, in its own folder (GameFiles.scratch), never the player's: loadWhole writes the month's
> JSON and map there and loads them as a Load does, so the same code that reads a player's save reads the recording's.

**Uses:** [HistorySave](HistorySave.md) (5), [GameFiles](GameFiles.md) (4), [Recording](Recording.md) (3), [Game](Game.md) (3)

**Used by (3):** [ReplayCheck](ReplayCheck.md), [ReplayGraphLayer](ReplayGraphLayer.md), [ReplayMode](ReplayMode.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `ReplayCity.SLOT` | `10` | The slot the copy loads the month from, in its own throwaway folder (GameFiles.scratch): never the player's folder. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `private final Recording recording` |  |
| 29 | `private final GameFiles files` |  |
| 30 | `private Game game` |  |
| 31 | `private HistorySave master` |  |
| 32 | `private boolean masterRead` |  |
| 33 | `private int shown` |  |
| 34 | `private boolean whole` |  |
| 35 | `private double lastLoadMs` |  |
| 36 | `private double lastStepMs` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 122 | **type** `public final class ReplayCity` | The throwaway copy of a recorded city that replay shows (TR1, 2026-10-31): a Game of its own, in a scratch folder, that loads any recorded month and is marked a replay copy (Game.markReplayCopy) so that it never recor... |
| 39 | 4 | `public ReplayCity(Recording recording)` | A replay of {@code recording}: the copy's folder is made now, the copy itself on the first load. |
| 44 | 1 | `public Recording recording()` |  |
| 47 | 1 | `public Game game()` | The copy, or null before the first load. |
| 50 | 1 | `GameFiles files()` | The copy's own folder: where loadWhole writes (for a check that nothing else is). |
| 53 | 1 | `public int[] months()` | The months the recording holds, ascending. |
| 56 | 1 | `public int shown()` | The month on screen, or -1 before the first. |
| 59 | 1 | `public boolean wholeMonth()` | Whether the month on screen was loaded in full (false after a quick step, which swapped only the map). |
| 62 | 1 | `public double lastLoadMs()` | How long the last loadWhole took, in milliseconds. |
| 65 | 1 | `public double lastStepMs()` | How long the last stepQuick took, in milliseconds. |
| 73 | 27 | `public String loadWhole(int month)` | Loads a recorded month in full: its save and map written to the copy's folder as slot {@link #SLOT} (the history file deleted), loaded as the Load menu loads a save, then the recording's history cut at the month. |
| 105 | 10 | `public boolean stepQuick(int month)` | Swaps only the map to a recorded month's: false, and nothing changed, when the month has no map, there is no copy yet, or the land is not the one the map was drawn on (a purchase between) - the caller then loads the m... |
| 117 | 7 | `public HistorySave master()` | The recording's history, parsed once; null when the recording has none. |
| 126 | 4 | `public HistorySave historyAt(int month)` | The history as it stood at {@code month} (HistorySave.cutAt); an empty history when the recording has none. |
| 132 | 3 | `public void showHistoryAt(int month)` | Puts the history at {@code month} in the copy's charts: what a quick step leaves to its caller, because the cut is a copy of the whole history. |
| 137 | 7 | `public int nearest(int month)` | The recorded month nearest {@code month} (the earlier of two equally near), or -1 for a recording with none: a month the recorder could not keep is a gap. |

