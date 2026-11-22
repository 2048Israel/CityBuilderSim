# ReplayCity.java - 192 lines · 20 methods · 1 constants · model

`ham/citybuildersim/ReplayCity.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (5), [HistorySave](HistorySave.md) (5), [GameFiles](GameFiles.md) (4), [Recording](Recording.md) (3)

**Used by (6):** [MapView](MapView.md), [ReplayCamera](ReplayCamera.md), [ReplayCheck](ReplayCheck.md), [ReplayGraphLayer](ReplayGraphLayer.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `ReplayCity.SLOT` | `10` | The slot the copy loads the month from, in its own throwaway folder (GameFiles.scratch): never the player's folder. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `private final Recording recording` |  |
| 30 | `private final GameFiles files` |  |
| 31 | `private Game game` |  |
| 32 | `private HistorySave master` |  |
| 33 | `private boolean masterRead` |  |
| 34 | `private int shown` |  |
| 35 | `private boolean whole` |  |
| 36 | `private double lastLoadMs` |  |
| 37 | `private double lastStepMs` |  |
| 41 | `private HashMap<Integer, Prepared> prepared` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 169 | **type** `public final class ReplayCity` | The throwaway copy of a recorded city that replay shows (TR1, 2026-10-31): a Game of its own, in a scratch folder, that loads any recorded month and is marked a replay copy (Game.markReplayCopy) so that it never recor... |
| 40 | 1 | **type** `private record Prepared(Game game, Game.ReplayMap map)` | A loaded move's maps, and the whole copy each land epoch needs. |
| 44 | 4 | `public void beginMove()` | Begins retaining months for a camera move; ordinary replay steps keep their existing load path (CM2). |
| 50 | 1 | `public void endMove()` | Releases the move's maps and other land epochs on finish, refusal, replacement or cancellation. |
| 53 | 1 | `public int preparedMonths()` | The distinct months held by the loaded move, for ReplayCheck's fixture. |
| 56 | 3 | `public int preparedContexts()` | The distinct land contexts actually retained, for the real land-change glide fixture (CM2). |
| 60 | 3 | `private void keepMonth(int month)` |  |
| 65 | 4 | `public ReplayCity(Recording recording)` | A replay of {@code recording}: the copy's folder is made now, the copy itself on the first load. |
| 70 | 1 | `public Recording recording()` |  |
| 73 | 1 | `public Game game()` | The copy, or null before the first load. |
| 76 | 1 | `GameFiles files()` | The copy's own folder: where loadWhole writes (for a check that nothing else is). |
| 79 | 1 | `public int[] months()` | The months the recording holds, ascending. |
| 82 | 1 | `public int shown()` | The month on screen, or -1 before the first. |
| 85 | 1 | `public boolean wholeMonth()` | Whether the month on screen was loaded in full (false after a quick step, which swapped only the map). |
| 88 | 1 | `public double lastLoadMs()` | How long the last loadWhole took, in milliseconds. |
| 91 | 1 | `public double lastStepMs()` | How long the last stepQuick took, in milliseconds. |
| 100 | 31 | `public String loadWhole(int month)` | Loads a recorded month in full: its save and map written to the copy's folder as slot {@link #SLOT} (the history file deleted), loaded as the Load menu loads a save, then the recording's history cut at the month. |
| 136 | 27 | `public boolean stepQuick(int month)` | Swaps only the map to a recorded month's: false, and nothing changed, when the month has no map, there is no copy yet, or the land is not the one the map was drawn on (a purchase between) - the caller then loads the m... |
| 165 | 7 | `public HistorySave master()` | The recording's history, parsed once; null when the recording has none. |
| 174 | 4 | `public HistorySave historyAt(int month)` | The history as it stood at {@code month} (HistorySave.cutAt); an empty history when the recording has none. |
| 180 | 3 | `public void showHistoryAt(int month)` | Puts the history at {@code month} in the copy's charts: what a quick step leaves to its caller, because the cut is a copy of the whole history. |
| 185 | 7 | `public int nearest(int month)` | The recorded month nearest {@code month} (the earlier of two equally near), or -1 for a recording with none: a month the recorder could not keep is a gap. |

