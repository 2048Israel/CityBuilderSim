# SaveDump.java - 133 lines · 4 methods · 0 constants · tools

`ham/citybuildersim/tools/SaveDump.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Looks inside a save without loading the game - or reading the file.
> 
>     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump 3
>     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump 3 sectors
>     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump 3 sectors.Bank.deposits
>     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump path\to\any.json people
> 
> A slot's save is megabytes of JSON, which is exactly the kind of file an
> assistant cannot open and a player cannot read. The first form prints
> the header (version, format, month, cash, population) and every top-level
> key with its shape - a number, a string, an object of N keys, an array of
> N - so the reader can see what the save carries. Each further dotted key
> descends one level, and an array index is a number ("sectors.3"). Arrays
> of numbers print their length, first, last, min, max and sum, which is
> usually the question.
> 
> Slot numbers resolve through GameFiles to the real saves folder, so this
> reads the player's actual city; it never writes anything. Slots 1-9 are
> Jerus's own cities and slot 10 is the assistant's, by standing agreement,
> and this tool cannot tell them apart - it only reads.

**Uses:** [GameFiles](GameFiles.md) (2), [SaveHeader](SaveHeader.md) (1)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 38 | 96 | **type** `public final class SaveDump` | Looks inside a save without loading the game - or reading the file. |
| 40 | 37 | `public static void main(String[] args) throws IOException` |  |
| 78 | 18 | `static void list(JsonElement e, String indent)` |  |
| 98 | 30 | `static String shape(JsonElement e)` | One line describing a value: its type, size and, for numbers, the number. |
| 129 | 4 | `static String num(double v)` |  |

