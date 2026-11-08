# Ladder.java - 449 lines · 31 methods · 6 constants · interface

`ham/citybuildersim/ui/Ladder.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

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
> month and apply at once by design (PolicyScreen's ruleCard(), holdingsCard()
> and ceilingCard(); THE TARGET, AS A DIAL until 0.7.36).
> Either way the ladder knows neither the staged set nor the model: it hands
> a snapped value inside the ends to the callback it was given, and the
> callback redraws the screen.
> 
> THE READING TRACKS THE THUMB, THE CALLBACK WAITS FOR THE RELEASE.
> Restaging on every pixel of a drag would redraw the whole screen under the
> pointer, which is both slow and how you lose the thing you were dragging.
> Since 0.7.36 a caller can follow the thumb too (onTrack()): the Policy
> tab's dial cards redraw their own "before -> after" rows on it, and the
> page waits for the release as before.
> 
> AND THREE THINGS THE POLICY TAB ASKED FOR (0.7.36): named marks on the
> track (marks() - the three income rates once they have parted, the rule's
> rate, the clinic's break-even); words the reading says before the dial is
> moved (idle() - "three rates", where a figure would be one of three and
> read as the whole: the Policy spec's B1); and the onTrack() above.
> 
> KEPT, THE FIRST CONTROL (KP2). On a kept page (Infrastructure's fare) the
> slider is handed back month after month, and a month can land while the
> player has the thumb in hand. So the thumb and the range are set only
> where they differ and never while Kept.held() says the player holds it,
> the listener is added once and reads the ladder of the latest build from
> the slider's properties, and shown() tells a dial card what value to
> draw its effects at: the held thumb's, not the staged one.

**Uses:** [Palette](Palette.md) (21), [Kept](Kept.md) (12)

**Used by (16):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundScreen](FundScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [Levers](Levers.md), [MortgageCheck](MortgageCheck.md), [PolicyScreen](PolicyScreen.md), [ResearchScreen](ResearchScreen.md), [SectorScreen](SectorScreen.md), [ZoningScreen](ZoningScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Ladder.READING` | `118` | How wide the reading is held, so the slider does not shift as the figure's width does. |
| 76 | `Ladder.BUTTON` | `28` | How wide and tall each step button is: square, and room for the glyph. |
| 79 | `Ladder.GAP` | `10` | The gap between the buttons, the slider and the reading. |
| 82 | `Ladder.MARKS_ROOM` | `new javafx.geometry.Insets(0, 0, 0, BUTTON + GAP)` | The room left of the marks under a slider, as wide as the step button and the gap: one object (KP2b), since a padding made again at each draw is a change at each. |
| 166 | `Ladder.LADDER` | `"kept.ladder", READING_LABEL = "kept.reading"` | The keys a slider keeps the latest build's ladder and reading label under, for the listener added once to read. |
| 304 | `Ladder.Marks.INSET` | `8` | Half the slider's thumb: the track's travel starts and ends this far in. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `private final double min, max, step` |  |
| 85 | `private final DoubleFunction<String> reads` |  |
| 86 | `private DoubleFunction<String> stepReads` |  |
| 87 | `private double current` |  |
| 89 | `private double placed` | Where the thumb is asked to sit (showing()), NaN: at the city's value. |
| 91 | `private double drawn` | The value the dial showed at the last build(): see shown(). |
| 92 | `private DoubleConsumer stage, apply` |  |
| 93 | `private boolean offAtCurrent` |  |
| 94 | `private String itIs` |  |
| 95 | `private boolean greyed` |  |
| 96 | `private double wide` |  |
| 97 | `private double[] markAt` |  |
| 98 | `private String[] markNames` |  |
| 99 | `private DoubleConsumer track` |  |
| 100 | `private String idle` |  |
| 305 | `private double min, max` |  |
| 306 | `private double[] at` |  |
| 307 | `private final java.util.List<javafx.scene.layout.Region> ticks` |  |
| 308 | `private final java.util.List<Label> names` |  |
| 309 | `private boolean told` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 70 | 380 | **type** `public final class Ladder` | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying where the ends are, what one step is and what the city is charging. |
| 102 | 8 | `private Ladder(double min, double max, double step, DoubleFunction<String> reads)` |  |
| 116 | 3 | `public static Ladder of(double min, double max, double step, DoubleFunction<String> reads)` | A dial from min to max in steps of step, read by reads - the same formatter for the reading, the ends and, unless stepReads() says otherwise, the step. |
| 121 | 1 | `public Ladder current(double value)` | What the city is charging now: the reading is in accent when the thumb is off it. |
| 124 | 1 | `public Ladder showing(double value)` | Where the thumb sits when that is not the current value - a staged one. |
| 127 | 1 | `public Ladder stages(DoubleConsumer to)` | Staged: every move hands the value to this, which stages it (or unstages it) and redraws. |
| 130 | 1 | `public Ladder appliesAtOnce(DoubleConsumer to)` | At once: a move off the current value hands it to this, which sets it and redraws. |
| 133 | 1 | `public Ladder stepReads(DoubleFunction<String> words)` | How one step reads on the ends line, when the reading's own formatter would say it wrongly. |
| 141 | 1 | `public Ladder offAtCurrent(boolean yes)` | Marks the dial as moved even at its current value - "Every tax at once" once the three income bases have parted, "Every school at once" once the nine prices have: its current is one of several, and setting them all to... |
| 144 | 1 | `public Ladder itIs(String words)` | What "it is" says on the ends line once the dial has moved; the current value by default. |
| 147 | 1 | `public Ladder greyed(boolean yes)` | Drawn but not moveable, and dimmed: a dial with nothing under it, such as a kind of school the city has not built. |
| 150 | 1 | `public Ladder wide(double width)` | The whole ladder's width; the slider takes what the buttons and the reading leave. |
| 153 | 5 | `public Ladder marks(double[] at, String[] names)` | Named marks on the track, in the dial's units (0.7.36): drawn under the slider where each value sits, a mark outside the ends left out. |
| 160 | 1 | `public Ladder onTrack(DoubleConsumer to)` | Hands every value the thumb passes through to this while it is dragged or stepped (0.7.36), snapped and inside the ends - before the release that stages it. |
| 163 | 1 | `public Ladder idle(String words)` | What the reading says, in grey, while the thumb sits on the current value and nothing is staged (0.7.36): "three rates" for a dial whose current is one of several. |
| 169 | 83 | `public VBox build()` | The dial: the row of −, slider, + and reading over the ends line. |
| 258 | 3 | `public double shown()` | The value the dial shows once build() has run: the thumb's own snapped value when it was held at the draw (a month landed under the player's hand and the thumb was left where it was), else the value the build set - th... |
| 263 | 1 | **type** `private record Said(String text, String style)` | What a reading shows: its words and its style. |
| 266 | 4 | `private Said said(double v, boolean away)` | What the reading says for a thumb at v: the figure, in accent off the city's value, or the idle words in grey. |
| 272 | 5 | `private void read(Label reading, double v, boolean away)` | The reading told what it says for a thumb at v (the listener's, between draws; a draw sets the same two once, in build()). |
| 283 | 8 | `private static void settle(Slider bar, double min, double max, double at)` | A slider's range and value made these, only where they differ, in an order that never clamps the value through a passing range: the range widened to hold both the old and the new first, then the value, then the range ... |
| 302 | 99 | **type** `static final class Marks extends javafx.scene.layout.Pane` | The marks under the slider (0.7.36): a short tick where each value sits on the track - the thumb's travel, inset by half the thumb at either end - and its name under it, a name that would touch the one before dropped ... |
| 311 | 1 | `Marks()` _(in Ladder.Marks)_ |  |
| 314 | 37 | `void set(double min, double max, double[] at, String[] names, double width)` _(in Ladder.Marks)_ | What the marks show now: the dial's ends, where each mark sits and what it is called, and the width. |
| 352 | 4 | `private double x(double v, double w)` _(in Ladder.Marks)_ |  |
| 358 | 15 | `private double[][] placed(double w)` _(in Ladder.Marks)_ | Where each name sits, {left, line}, at a width; a name that would touch the last on its line goes one line down. |
| 374 | 5 | `private int lines(double w)` _(in Ladder.Marks)_ |  |
| 380 | 4 | `protected double computePrefHeight(double width)` _(in Ladder.Marks)_ |  |
| 385 | 1 | `protected double computeMinHeight(double width)` _(in Ladder.Marks)_ |  |
| 387 | 13 | `protected void layoutChildren()` _(in Ladder.Marks)_ |  |
| 403 | 3 | `private String tone(boolean off)` | The reading's colour: accent off the city's value, dimmed when greyed. |
| 408 | 3 | `private double bounded(double value)` | A value inside the ends. |
| 417 | 6 | `private void hand(double value)` | A move, snapped and held inside the ends, handed to the one callback: every move to a staged dial (back at the city's value it unstages), a move off the current value to an at-once one. |
| 433 | 16 | `static Button stepButton(String glyph, boolean live, Runnable go)` | One notch, in the units the dial is read in. |

