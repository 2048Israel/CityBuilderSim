# Ladder.java - 225 lines · 16 methods · 3 constants · interface

`ham/citybuildersim/ui/Ladder.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> One dial, drawn the one way: a "−" worth one step, a slider that snaps to
> the step, a "+" worth one step, the reading, and a line under them saying
> where the ends are, what one step is and what the city is charging.
> 
> WHY THIS IS A CLASS (0.7.6). Jerus: "all the dials in the policy in the
> promises section, make them a slider with steps, and a + − on the ends,
> basically i think it's better if you create an object or class of slider,
> and then whenever you need it you just call that class and plug in the
> specific sensitivity, max, min and steps type and all no? or is that
> already in place?" Half of it was. PolicyScreen.taxLadder was this shape
> for the tax pages; stageSlider was the same slider without the buttons,
> for the wage floor, the policy rate, the promises and the fare; and the
> inflation target, the central bank's holdings and its ceiling were chips,
> one of them with an ad-hoc pair of buttons of its own. Three shapes of one
> control, each with its own idea of how wide the reading was and whether
> the ends were said. This replaced all three: every dial on the policy tab,
> and the fare on Services, is Ladder.of(...).build().
> 
> THE SENSITIVITY IS THE STEP, AND NOTHING ELSE. The buttons move one step,
> the slider snaps to it, and the ends line says what it is. There is no
> second knob - a drag coarser than a click would be two dials in one.
> 
> STAGED, OR AT ONCE - exactly one of the two. A staged ladder hands the
> value to its stage callback, which puts it in the policy tab's staged set
> (or takes it out, back at the city's value) and redraws; the foot bar, or
> the page's own apply bar, is what makes it real, as it always was. An
> apply-at-once ladder hands the value to the model straight away - the
> monetary page's target, holdings and ceiling, which move no price this
> month and apply at once by design (PolicyScreen, THE TARGET, AS A DIAL).
> Either way the ladder knows neither the staged set nor the model: it hands
> a snapped value inside the ends to the callback it was given, and the
> callback redraws the screen.
> 
> THE READING TRACKS THE THUMB, THE CALLBACK WAITS FOR THE RELEASE.
> Restaging on every pixel of a drag would redraw the whole screen under the
> pointer, which is both slow and how you lose the thing you were dragging.

**Uses:** [Palette](Palette.md) (16)

**Used by (5):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [MortgageCheck](MortgageCheck.md), [PolicyScreen](PolicyScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `Ladder.READING` | `118` | How wide the reading is held, so the slider does not shift as the figure's width does. |
| 58 | `Ladder.BUTTON` | `28` | How wide and tall each step button is: square, and room for the glyph. |
| 61 | `Ladder.GAP` | `10` | The gap between the buttons, the slider and the reading. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `private final double min, max, step` |  |
| 64 | `private final DoubleFunction<String> reads` |  |
| 65 | `private DoubleFunction<String> stepReads` |  |
| 66 | `private double current` |  |
| 67 | `private double shown` |  |
| 68 | `private DoubleConsumer stage, apply` |  |
| 69 | `private boolean offAtCurrent` |  |
| 70 | `private String itIs` |  |
| 71 | `private boolean greyed` |  |
| 72 | `private double wide` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 52 | 174 | **type** `public final class Ladder` | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying where the ends are, what one step is and what the city is charging. |
| 74 | 8 | `private Ladder(double min, double max, double step, DoubleFunction<String> reads)` |  |
| 88 | 3 | `public static Ladder of(double min, double max, double step, DoubleFunction<String> reads)` | A dial from min to max in steps of step, read by reads - the same formatter for the reading, the ends and, unless stepReads() says otherwise, the step. |
| 93 | 1 | `public Ladder current(double value)` | What the city is charging now: the reading is in accent when the thumb is off it. |
| 96 | 1 | `public Ladder showing(double value)` | Where the thumb sits when that is not the current value - a staged one. |
| 99 | 1 | `public Ladder stages(DoubleConsumer to)` | Staged: every move hands the value to this, which stages it (or unstages it) and redraws. |
| 102 | 1 | `public Ladder appliesAtOnce(DoubleConsumer to)` | At once: a move off the current value hands it to this, which sets it and redraws. |
| 105 | 1 | `public Ladder stepReads(DoubleFunction<String> words)` | How one step reads on the ends line, when the reading's own formatter would say it wrongly. |
| 113 | 1 | `public Ladder offAtCurrent(boolean yes)` | Marks the dial as moved even at its current value - "Every tax at once" once the three income bases have parted, "Every school at once" once the nine prices have: its current is one of several, and setting them all to... |
| 116 | 1 | `public Ladder itIs(String words)` | What "it is" says on the ends line once the dial has moved; the current value by default. |
| 119 | 1 | `public Ladder greyed(boolean yes)` | Drawn but not moveable, and dimmed: a dial with nothing under it, such as a kind of school the city has not built. |
| 122 | 1 | `public Ladder wide(double width)` | The whole ladder's width; the slider takes what the buttons and the reading leave. |
| 125 | 54 | `public VBox build()` | The dial: the row of −, slider, + and reading over the ends line. |
| 181 | 3 | `private String tone(boolean off)` | The reading's colour: accent off the city's value, dimmed when greyed. |
| 186 | 3 | `private double bounded(double value)` | A value inside the ends. |
| 195 | 6 | `private void hand(double value)` | A move, snapped and held inside the ends, handed to the one callback: every move to a staged dial (back at the city's value it unstages), a move off the current value to an at-once one. |
| 211 | 14 | `static Button stepButton(String glyph, boolean live, Runnable go)` | One notch, in the units the dial is read in. |

