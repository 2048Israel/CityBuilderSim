# SignFont.java - 71 lines · 4 methods · 2 constants · interface

`ham/citybuildersim/ui/SignFont.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Which signs for the city's money the interface's own faces can draw.
> 
> WHY THIS EXISTS (FM1). The sign a player types for their money ("kr", "€",
> "₺") is written on every figure of the game, in the words' face (IBM Plex
> Sans) and in the figures' (IBM Plex Mono). A glyph neither face has is drawn
> by JavaFX as an empty box, or in whatever face the platform falls back to,
> and a money that reads "▯2.6M" on every screen is a worse mistake than a
> founding screen that says no. So the founding row offers only the signs both
> faces draw (offered()), and Found refuses a typed sign they do not (draws()).
> 
> THE TEST IS THE FONT FILE'S, NOT THE PLATFORM'S. java.awt.Font reads the same
> two regular files the interface loads (Palette.Fonts.FOLDER), so the answer
> is the same on every machine the game is shipped to, and a harness can ask it
> with no window and no toolkit. java.desktop is in the shipped runtime
> (Build EXE.bat's MODULES). The bold and medium cuts carry the same glyphs as
> their regular cut; they are not asked.
> 
> A face that cannot be read is skipped, not failed: draws() is true when
> neither can be, because a game that cannot read its own font files is not
> going to draw anything better with a sign refused.

**Uses:** [Palette](Palette.md) (1)

**Used by (2):** [FoundingScreen](FoundingScreen.md), [MoneySignCheck](MoneySignCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `SignFont.CANDIDATES` | `List.of("$", "€", "£", "¥", "₹", "₩", "₽", "₺", "₪", "ƒ", "kr", "zł", "Kč", "...` | The signs the founding row offers before the font is asked, in the order they are offered: the world's common ones, then the Nordic, Polish, Czech and Swiss, then Brazil's. |
| 38 | `SignFont.FILES` | `{ "IBMPlexSans-Regular.ttf", "IBMPlexMono-Regular.ttf" }` | The two regular faces' files, in the folder Palette.Fonts loads them from: the words' and the figures'. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `private static List<Font> faces` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 43 | **type** `public final class SignFont` | Which signs for the city's money the interface's own faces can draw. |
| 31 | 1 | `private SignFont()` |  |
| 43 | 14 | `private static synchronized List<Font> faces()` | The faces that could be read, loaded on the first question and kept. |
| 59 | 7 | `public static boolean draws(String sign)` | Whether every face that could be read draws every character of the sign (true when none could be read). |
| 68 | 3 | `public static List<String> offered()` | CANDIDATES the faces draw, in CANDIDATES' order: the signs the founding row offers. |

