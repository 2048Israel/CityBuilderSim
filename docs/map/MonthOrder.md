# MonthOrder.java - 167 lines · 6 methods · 1 constants · tools

`ham/citybuildersim/tools/MonthOrder.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The month as a numbered list: docs/month-order.md walks the top-level
> statements of the methods that make up a month, in order, each with its
> line and the comment above it, and says which method in the tree each
> call lands in.
> 
> WHY. README says "the month is a sequence, not a set", and that most of
> the hard bugs in this project's history were a line that made it into one
> phase and not the other. The sequence lives in Game.nextMonth() (540
> lines) and SimulationEngine.simulateMonth(), between paragraphs of prose
> that explain why each line is where it is. The prose is worth keeping and
> impossible to skim, so this is the skim: the lines alone, numbered, with
> one sentence each and a pointer to where each call goes. A change that
> has to land "after the wages are paid and before the shops buy" starts
> here.
> 
> Which methods are listed is a property, -Dmonth.methods=Class.method,...
> The default is the month's spine.

**Uses:** [JavaScan](JavaScan.md) (24), [SourceTree](SourceTree.md) (8), [Statement](Statement.md) (8)

**Used by (1):** [Maps](Maps.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `MonthOrder.DEFAULT` | `"Game.nextMonth,Game.startOfMonthUpdate,SimulationEngine.simulateMonth," + "S...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `int line` |  |
| 71 | `String text` |  |
| 71 | `String comment` |  |
| 71 | `final List<JavaScan.Tok> toks` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 140 | **type** `public final class MonthOrder` | The month as a numbered list: docs/month-order.md walks the top-level statements of the methods that make up a month, in order, each with its line and the comment above it, and says which method in the tree each call ... |
| 34 | 3 | `public static void main(String[] args) throws IOException` |  |
| 38 | 31 | `static void run(SourceTree tree) throws IOException` |  |
| 70 | 3 | **type** `static final class Statement` |  |
| 75 | 45 | `static List<Statement> statements(JavaScan f, JavaScan.Member m)` | The top-level statements of a method body. |
| 121 | 4 | `static JavaScan.Tok peek(List<JavaScan.Tok> toks, int from, int end)` |  |
| 127 | 9 | `static Map<String, String> fieldTypes(JavaScan f)` | field name -> declared type, from the file's field declarations, so "game.foo()" can be followed. |
| 138 | 29 | `static String targets(SourceTree tree, JavaScan here, Map<String, String> fieldTypes, Statement s)` | Where the calls in a statement go, as "Class.method (Lnnn)" links, for calls the tree can resolve. |

