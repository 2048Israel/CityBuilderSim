# RobustnessCheck.java - 376 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/RobustnessCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> What the game does when something is already broken.
> 
> Every other harness checks that the game works. This one checks that it fails
> legibly - which matters more in a shipped build than in development, because
> the developer has a console and the player has a window that either explains
> itself or does not.
> 
> THE BUG THIS WAS WRITTEN FOR
> 
> A truncated save threw JsonSyntaxException out of loadGame(). That is a
> RuntimeException, so it went straight through the catch (IOException) sitting
> right there. readHeader() failed safely and the menu labelled the slot
> "Empty" - but slotIsEmpty() asks whether the FILE exists, and it did, so the
> Load button stayed enabled.
> 
> The player clicked Load on a slot that said Empty, and the game did nothing.
> No message, no error, no load: the exception reached the FX thread's default
> handler and a stderr that does not exist in a packaged build.

**Uses:** [Game](Game.md) (24), [GameLog](GameLog.md) (6), [GameFiles](GameFiles.md) (5), [GameVersion](GameVersion.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Construction](Construction.md) (1)

## Sections

| line | section |
|---:|---|
| 67 | · 1. a save cut in half |
| 92 | · 2. a file that is not JSON at all |
| 105 | · 3. an empty file, and an empty slot |
| 120 | · 4. the good save still loads |
| 137 | · 5. a save from a newer build |
| 153 | · 5b. a save from an OLDER build |
| 232 | · 5c. a city whose numbers have overflowed |
| 284 | · 5d. a parameter that used to do nothing |
| 336 | · 6. the log |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 351 | **type** `public class RobustnessCheck` | What the game does when something is already broken. |
| 30 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 35 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 46 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 53 | 315 | `public static void main(String[] args) throws Exception` |  |
| 369 | 7 | `static void cleanUp(Path root)` |  |

