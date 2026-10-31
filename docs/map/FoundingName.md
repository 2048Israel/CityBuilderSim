# FoundingName.java - 73 lines · 6 methods · 0 constants · model

`ham/citybuildersim/FoundingName.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The city's name as the Found-a-city page holds it: blank to begin with, filled by a place searched, and the player's own once they have typed one (FN1, 2026-10-30).
> 
> WHY. The page opened on "Danzik", the model's default city, so a player who
> wanted their own name had to select it away first, and a place searched
> on the Earth never named the city. Jerus: "remove the default name of
> danzik, have it just blank, or if you search for a city, its that city
> name (unless someone already typed something, then it doesnt change the
> city name)". The text field cannot say who put its text there, so this
> class does: the screen tells it each change, and it keeps the text, and
> whether the player wrote it. The default itself ({@link Founding#DEFAULT_CITY_NAME})
> stays in the model, which old saves and the harnesses found cities by.
> 
> THE RULE. The text is "" at first. {@link #placePicked(String)} puts a
> place's name in only while the player has not written one, and a name it
> put there is replaced by the next place picked. {@link #edited(String)} is
> a change the player made: a non-blank text is theirs and stays, a cleared
> (or blank) field is nobody's again, so the next place fills it. A change to
> the very text this class last set is the field echoing it, not an edit.

**Uses:** [Founding](Founding.md) (3)

**Used by (2):** [FoundingScreen](FoundingScreen.md), [NewGameCheck](NewGameCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `private String text` |  |
| 26 | `private boolean typed` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 51 | **type** `public final class FoundingName` | The city's name as the Found-a-city page holds it: blank to begin with, filled by a place searched, and the player's own once they have typed one (FN1, 2026-10-30). |
| 29 | 1 | `public String text()` | The name as it stands, "" while there is none. |
| 32 | 1 | `public boolean isTyped()` | True when the player wrote the name, so a place picked leaves it alone. |
| 35 | 4 | `public void reset()` | The page opening afresh: blank, and nobody's. |
| 41 | 6 | `public void edited(String now)` | The field changed to {@code now}: the player's edit, unless it is the text this class set itself. |
| 52 | 7 | `public boolean placePicked(String placeName)` | A place picked from the search. |
| 64 | 9 | `public static String fromPlace(String placeName)` | A place's name as a city's: trimmed and cut to {@link Founding#MAX_CITY_NAME_LENGTH} (Earth's data has names longer than a city may be called), or "" when the result would not pass {@link Founding#cityNameProblem(Stri... |

