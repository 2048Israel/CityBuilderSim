# ReplayGraphLayer.java - 194 lines · 8 methods · 6 constants · interface

`ham/citybuildersim/ui/ReplayGraphLayer.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The faint GDP and population lines behind the city in a replay (TR1, 2026-10-31): one mouse-transparent canvas laid over the
> window's content and under the time bar, drawn from the recording's first month to the month on screen and scaled to the whole
> recording, so a line rises as time plays - the 0:54-1:35 shot. It stays in cinematic mode: it is part of the shot. The
> arithmetic (which points, how high, the figure at the head) is {@link ReplayGraphs}; this draws it.
> 
> <p>The look: wide soft lines across the lower two thirds of the window at about a third of full opacity, a faint fill under each,
> a dot and a small label at each head with its figure (the game's own formatters, Money). The colours are the output and people
> areas' (Palette.BUSINESS and Palette.PEOPLE). Redrawn only when the month on screen, the window's size or the room the bar
> takes changes, never on a frame that has nothing new.
> 
> WHY (TR1): Jerus's trailer wants "GDP and population graphs rising faintly behind" the city while it expands; the screens' own
> charts are for reading, and these are for looking at.

**Uses:** [ReplayGraphs](ReplayGraphs.md) (7), [Palette](Palette.md) (3), [ReplayMode](ReplayMode.md) (2), [UserInterface](UserInterface.md) (2), [Money](Money.md) (2), [ReplayCity](ReplayCity.md) (1)

**Used by (1):** [ReplayMode](ReplayMode.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `ReplayGraphLayer.LINE_ALPHA` | `0.35` | How opaque a line is: 0.35 - the brief's "about 30-40%": faint enough to sit behind the city. |
| 37 | `ReplayGraphLayer.FILL_ALPHA` | `0.12` | The fill under a line is this opaque at the line and fades to nothing at the baseline: 0.12. |
| 40 | `ReplayGraphLayer.LINE_SHARE` | `0.0075` | A line's width as a share of the window's height, at least 3 px: 0.0075, 8 px in a 1080 window. |
| 43 | `ReplayGraphLayer.TOP_SHARE` | `0.34` | The lines stand between these shares of the window's height from its top: the lower two thirds, 0.34 to the baseline. |
| 46 | `ReplayGraphLayer.LEFT_SHARE` | `0.04, RIGHT_SHARE = 0.12` | The room kept at the left and the right of the window, as shares of its width: the head's label needs the right's. |
| 49 | `ReplayGraphLayer.POINTS_MOST` | `1500` | At most this many points are drawn a line; a recording of thousands of months is stepped over. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private final ReplayMode mode` |  |
| 52 | `private final UserInterface ui` |  |
| 53 | `private Canvas canvas` |  |
| 54 | `private boolean on` |  |
| 55 | `private ReplayGraphs.Line people, output` |  |
| 56 | `private double bottomInset` |  |
| 57 | `private int drawnMonth` |  |
| 58 | `private double drawnW` |  |
| 77 | `private int firstMonth, lastMonth` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 31 | 164 | **type** `final class ReplayGraphLayer` | The faint GDP and population lines behind the city in a replay (TR1, 2026-10-31): one mouse-transparent canvas laid over the window's content and under the time bar, drawn from the recording's first month to the month... |
| 60 | 4 | `ReplayGraphLayer(ReplayMode mode, UserInterface ui)` |  |
| 65 | 1 | `boolean isOn()` |  |
| 68 | 8 | `void load(ReplayCity city)` | The lines read from the recording's history (ReplayCity.master()), once: the recording's first and last month bound them. |
| 80 | 17 | `boolean toggle()` | G: the lines on or off. |
| 99 | 5 | `void leave()` | The replay is left: the canvas comes off the window, the switch goes back to off for the next one. |
| 106 | 11 | `void keepUnder(Node bar)` | The canvas kept just under the time bar in the window's stack, above the content and the expanded map. |
| 119 | 11 | `void frame(int month, double barRoom)` | A frame: the lines drawn again if the month on screen, the window or the bar's room moved. |
| 131 | 63 | `private void draw(int month, double w, double h)` |  |

