# GamePrefs.java - 186 lines · 15 methods · 3 constants · model

`ham/citybuildersim/GamePrefs.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> How the player likes the window, kept between runs.
> 
> WHY THIS IS NOT IN THE SAVE FILE. Graphs and reports live in DataSave, which
> is right for them - they are settings ABOUT a city and a different city can
> reasonably want different ones. Whether the game runs full screen is a fact
> about the monitor it is running on, and filing it per city would mean loading
> slot 3 changed the shape of the window. So it lives beside the saves rather
> than inside one, and every city on this machine shares it.
> 
> WHY NOT java.util.prefs. That would work and takes about four lines, but on
> Windows it writes into the registry, where a player cannot find it, cannot
> back it up with the rest of the game folder, and cannot delete it when it
> goes wrong. Everything else this game keeps is a JSON file in one folder, and
> a settings file nobody can locate is a support problem waiting to happen.
> 
> NOTHING HERE IS LOAD-BEARING. A missing, empty or corrupt file is not an
> error - it is a player who has never changed a setting, and the defaults are
> the answer. That is why every method swallows and logs rather than throwing:
> a game that will not start because it could not read a preference is a worse
> game than one that starts windowed.

**Uses:** [WorldEconomy](WorldEconomy.md) (3), [GameFiles](GameFiles.md) (3), [GameLog](GameLog.md) (2)

**Used by (2):** [HistoryScreen](HistoryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 145 | THE FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `GamePrefs.FILE` | `"settings.json"` |  |
| 128 | `GamePrefs.DEFAULT_PINNED_LEFT` | `"realGdp"` | The two small charts pinned at the top of the Reports page, by series name - the name HistorySave and the page's own list know a line by. |
| 131 | `GamePrefs.DEFAULT_PINNED_RIGHT` | `"population"` | ...and the right-hand one, the population. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `private boolean fullScreen` | Full screen by default. |
| 59 | `private boolean panelDashboard` | Which of the two the city panel is showing. |
| 85 | `private boolean pauseOnEvents` | Whether the clock stops itself when something worth seeing happens. |
| 103 | `private double worldInflation` | The world the NEXT city is founded into. |
| 133 | `private String pinnedLeft` |  |
| 134 | `private String pinnedRight` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 157 | **type** `public final class GamePrefs` | How the player likes the window, kept between runs. |
| 44 | 1 | `public boolean isFullScreen()` |  |
| 45 | 1 | `public void setFullScreen(boolean value)` |  |
| 61 | 1 | `public boolean isPanelDashboard()` |  |
| 62 | 1 | `public void setPanelDashboard(boolean value)` |  |
| 87 | 1 | `public boolean isPauseOnEvents()` |  |
| 88 | 1 | `public void setPauseOnEvents(boolean value)` |  |
| 105 | 1 | `public double getWorldInflation()` |  |
| 106 | 4 | `public void setWorldInflation(double value)` |  |
| 136 | 3 | `public String getPinnedLeft()` |  |
| 139 | 3 | `public String getPinnedRight()` |  |
| 142 | 1 | `public void setPinnedLeft(String key)` |  |
| 143 | 1 | `public void setPinnedRight(String key)` |  |

### THE FILE (lines 145-186)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 3 | `private static Path fileIn(GameFiles files)` |  |
| 154 | 16 | `public static GamePrefs load(GameFiles files)` | Whatever is on disk, or the defaults. |
| 177 | 9 | `public boolean save(GameFiles files)` | Writes them back, through the same atomic replace the saves use. |

