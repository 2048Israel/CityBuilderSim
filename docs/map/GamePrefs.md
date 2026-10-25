# GamePrefs.java - 216 lines · 19 methods · 3 constants · model

`ham/citybuildersim/GamePrefs.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [GameFiles](GameFiles.md) (3), [GameLog](GameLog.md) (2)

**Used by (4):** [HistoryScreen](HistoryScreen.md), [TruckCheck](TruckCheck.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 175 | THE FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `GamePrefs.FILE` | `"settings.json"` |  |
| 118 | `GamePrefs.DEFAULT_PINNED_LEFT` | `"realGdp"` | The two small charts pinned at the top of the Reports page, by series name - the name HistorySave and the page's own list know a line by. |
| 121 | `GamePrefs.DEFAULT_PINNED_RIGHT` | `"population"` | ...and the right-hand one, the population. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `private boolean fullScreen` | Full screen by default. |
| 59 | `private boolean panelDashboard` | Which of the two the city panel is showing. |
| 85 | `private boolean pauseOnEvents` | Whether the clock stops itself when something worth seeing happens. |
| 123 | `private String pinnedLeft` |  |
| 124 | `private String pinnedRight` |  |
| 145 | `private boolean drawerPinned` | Whether the City overview's drawer stays open across screens (0.7.24). |
| 156 | `private boolean constructionOpen` | Whether the construction panel is open over the stage or folded to its tab (0.7.24). |
| 170 | `private boolean trucksOnMap` | Whether Haulage's trucks are drawn on the map's roads at the near view (0.8.8, batch FR5). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 187 | **type** `public final class GamePrefs` | How the player likes the window, kept between runs. |
| 44 | 1 | `public boolean isFullScreen()` |  |
| 45 | 1 | `public void setFullScreen(boolean value)` |  |
| 61 | 1 | `public boolean isPanelDashboard()` |  |
| 62 | 1 | `public void setPanelDashboard(boolean value)` |  |
| 87 | 1 | `public boolean isPauseOnEvents()` |  |
| 88 | 1 | `public void setPauseOnEvents(boolean value)` |  |
| 126 | 3 | `public String getPinnedLeft()` |  |
| 129 | 3 | `public String getPinnedRight()` |  |
| 132 | 1 | `public void setPinnedLeft(String key)` |  |
| 133 | 1 | `public void setPinnedRight(String key)` |  |
| 147 | 1 | `public boolean isDrawerPinned()` |  |
| 148 | 1 | `public void setDrawerPinned(boolean value)` |  |
| 158 | 1 | `public boolean isConstructionOpen()` |  |
| 159 | 1 | `public void setConstructionOpen(boolean value)` |  |
| 172 | 1 | `public boolean isTrucksOnMap()` |  |
| 173 | 1 | `public void setTrucksOnMap(boolean value)` |  |

### THE FILE (lines 175-216)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 3 | `private static Path fileIn(GameFiles files)` |  |
| 184 | 16 | `public static GamePrefs load(GameFiles files)` | Whatever is on disk, or the defaults. |
| 207 | 9 | `public boolean save(GameFiles files)` | Writes them back, through the same atomic replace the saves use. |

