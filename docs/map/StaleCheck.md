# StaleCheck.java - 331 lines · 6 methods · 6 constants · harnesses

`ham/citybuildersim/StaleCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The prose still describes the code: the firm half of `tools.Stale`, asserted,
> and `tools.Names` proved on a fixture.
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
>   6. tools.Names TELLS A REAL NAME FROM A MADE-UP ONE (RV1). A canned diff
>      over a fixture tree names members declared the ways a regex misses
>      (`final`, `double[]`, a generic return, a record's component, an
>      inherited method), a made-up method and constant, a member put on the
>      wrong class and a type that is not there; only the second kind may come
>      back, at the line the diff gives it, and a proposal, a file name, code
>      and a generated document are not read as claims. Its run on the tree's
>      own branches is ship.sh's, and a warning: a name in a passage about the
>      past, or the future, is not a defect of the tree's.
> 
> The soft categories - a member named in a comment that nothing declares, and
> docs/map being older than the sources - are printed by the tool and are NOT
> asserted here: the first can be a library call this tree never makes, and the
> second is cleared by `Regenerate maps.bat` and is written wrongly by any
> checkout. Run the tool itself to see them:
> 
>     java -cp target/classes ham.citybuildersim.tools.Stale

**Uses:** [Stale](Stale.md) (30), [Names](Names.md) (7), [SourceTree](SourceTree.md) (6), [TempDirs](TempDirs.md) (2)

## Sections

| line | section |
|---:|---|
| 102 | · 1. the tool sees a defect |
| 124 | · 2. ...and nothing when there is none |
| 135 | · 3. the tree itself |
| 153 | · 6. tools.Names on a fixture |
| 208 | · THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 213 | `StaleCheck.PLANTED` | `""" package ham.citybuildersim; /** * A fixture with three defects planted in...` | Three planted lies: a stranded javadoc, a file that is not there, and a header out by one. |
| 240 | `StaleCheck.ALPHA` | `""" package ham.citybuildersim; /** A fixture class for tools.Names.*/ public...` | Section 6's tree: members declared the ways a regex over source misses. |
| 259 | `StaleCheck.BETA` | `""" package ham.citybuildersim; /** A fixture subclass for tools.Names.*/ pub...` | A subclass: what it inherits resolves on it, and what it declares does not resolve on Alpha. |
| 270 | `StaleCheck.GAMMA` | `""" package ham.citybuildersim; /** Added on the branch; this comment names m...` | A file the branch adds, read as the branch has it. |
| 281 | `StaleCheck.NAMES_DIFF` | `""" diff --git a/fixture/names.md b/fixture/names.md --- a/fixture/names.md +...` | The diff: a document's six lines (2 to 7), the Java file whole, and a generated page. |
| 312 | `StaleCheck.SOUND` | `""" package ham.citybuildersim; /** * A fixture with nothing wrong with it. *...` | The same shapes, all of them true. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 267 | **type** `public class StaleCheck` | The prose still describes the code: the firm half of `tools.Stale`, asserted, and `tools.Names` proved on a fixture. |
| 69 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 75 | 6 | `static void none(String label, List<Stale.Finding> found)` | An assertion that a firm category is empty, printing what it found when it is not. |
| 83 | 5 | `static List<Stale.Finding> only(List<Stale.Finding> all, Stale.Cat cat, String pathPart)` | The findings of one category that came from one place, so a fixture's assertions are its own. |
| 89 | 70 | `public static void main(String[] args) throws Exception` |  |
| 161 | 38 | `static void names() throws IOException` | Section 6: the fixture tree, a diff over it, and what Names makes of each line. |
| 200 | 7 | `static void cleanUp(Path root)` |  |

