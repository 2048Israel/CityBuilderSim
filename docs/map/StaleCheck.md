# StaleCheck.java - 204 lines · 5 methods · 2 constants · harnesses

`ham/citybuildersim/StaleCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The prose still describes the code: the firm half of `tools.Stale`, asserted.
> 
> WHAT THIS HAS TO PROVE, and none of it is about what the game does - a
> finding here is a sentence that has stopped being true, not a mechanic that
> has stopped working:
> 
>   1. THE TOOL SEES A DEFECT WHEN THERE IS ONE. A fixture tree is written
>      with three planted lies in it - a javadoc with no member under it, a
>      comment naming a source file that is not in the tree, and a class
>      header that miscounts its own banners - and each one has to come back.
>      A checker that cannot fail is the failure mode this harness itself is
>      most exposed to, because the tree it reads is usually clean.
> 
>   2. AND NOTHING WHEN THERE IS NOT. The same fixture with a sound file in
>      it raises nothing at all, so a green run means agreement and not
>      silence.
> 
>   3. NO JAVADOC IN THE TREE DOCUMENTS NOTHING. Two javadocs with no member
>      between them, or one stranded above a section banner, means the lower
>      one is what every reader and every index will use and the upper one is
>      dead text that nothing will ever correct.
> 
>   4. NOTHING NAMES A FILE THAT IS NOT THERE. A comment or a document that
>      names a source file or a docs/ page which has been renamed or removed
>      sends the next reader looking for it.
> 
>   5. EVERY STATED COUNT IS THE COUNT. A class header that counts its own
>      banners, and the two front doors where they count the harnesses and the
>      interface files, have to agree with the files on disk. This is the one
>      the interface split of 2026-09-18 broke in a dozen places by hand.
> 
> The soft categories - a member named in a comment that nothing declares, and
> docs/map being older than the sources - are printed by the tool and are NOT
> asserted here: the first can be a library call this tree never makes, and the
> second is cleared by `Regenerate maps.bat` and is written wrongly by any
> checkout. Run the tool itself to see them:
> 
>     java -cp target/classes ham.citybuildersim.tools.Stale

**Uses:** [Stale](Stale.md) (30), [SourceTree](SourceTree.md) (4)

## Sections

| line | section |
|---:|---|
| 90 | · 1. the tool sees a defect |
| 112 | · 2. ...and nothing when there is none |
| 123 | · 3. the tree itself |
| 153 | · THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 158 | `StaleCheck.PLANTED` | `""" package ham.citybuildersim; /** * A fixture with three defects planted in...` | Three planted lies: a stranded javadoc, a file that is not there, and a header out by one. |
| 185 | `StaleCheck.SOUND` | `""" package ham.citybuildersim; /** * A fixture with nothing wrong with it. *...` | The same shapes, all of them true. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 152 | **type** `public class StaleCheck` | The prose still describes the code: the firm half of `tools.Stale`, asserted. |
| 57 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 63 | 6 | `static void none(String label, List<Stale.Finding> found)` | An assertion that a firm category is empty, printing what it found when it is not. |
| 71 | 5 | `static List<Stale.Finding> only(List<Stale.Finding> all, Stale.Cat cat, String pathPart)` | The findings of one category that came from one place, so a fixture's assertions are its own. |
| 77 | 67 | `public static void main(String[] args) throws Exception` |  |
| 145 | 7 | `static void cleanUp(Path root)` |  |

