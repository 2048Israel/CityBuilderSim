# Where.java - 71 lines · 1 methods · 0 constants · tools

`ham/citybuildersim/tools/Where.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Finds a member by name anywhere in the tree and, if asked, prints it.
> 
>     java -cp target/classes ham.citybuildersim.tools.Where nextMonth
>     java -cp target/classes ham.citybuildersim.tools.Where Game.nextMonth -print
>     java -cp target/classes ham.citybuildersim.tools.Where "THE BANK"
> 
> The first form lists every method, field, constant or type called that,
> with file and line range. The second prints one of them, line-numbered,
> so that a single method can be pasted into a conversation instead of the
> file it sits in. The third - anything with a space or in capitals that is
> not a member - searches the banner sections instead, so a screen in
> UserInterface can be found by the title it was given.
> 
> Matching is case-insensitive and on the whole name; a trailing * makes it
> a prefix ("Where get*" is long).

**Uses:** [SourceTree](SourceTree.md) (4), [JavaScan](JavaScan.md) (3)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 49 | **type** `public final class Where` | Finds a member by name anywhere in the tree and, if asked, prints it. |
| 25 | 46 | `public static void main(String[] args) throws IOException` |  |

