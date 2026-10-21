# exchange-rates/ - beside spec-exchange-rates.md

The boards, each one self-contained, drawn in the game's palette (`ui/Palette`,
as the nations boards); open them in a browser:

- `1-the-currency.html`: Trade & the world > The currency under XR1: the rate,
  parity on traded goods and the fair level, the chart of his slot 3 replayed,
  what moved it this month in five lines, where the fundamentals hold it.
- `2-the-regime.html`: Policy > The currency under XR2: float, lean, band or
  peg; a peg's shadow rate and cost, the vault's months to the floor; the band;
  buying or selling the D$; the dial under a peg.
- `3-the-worlds-moneys.html`: Trade & the world > Partners and a nation's card
  under XR3 and XR4: the world's moneys this month, the dollar 2000-2040, a
  risk-off reaching the D$, Canada's card.

The model and its data:

- `RatesModel.java`: the prototype. Java 21, no build: `java RatesModel.java
  slot3-series.csv`, here. It writes `rates-model-output.txt` (today's rule
  taken apart, the lag, the proposal replayed on slot 3, the world's moneys over
  1,000 seeded paths, pegs, a month's cost) and the boards' data,
  `board-1-path.csv` and `board-3-world.csv`.
- `slot3-series.csv`: Jerus's slot 3 (his new city, 476 months), the monthly
  series the model reads, taken from a COPY of `slot-03-history.json` by
  `extract.py` (`python -I extract.py <copy of slot-03-history.json>
  slot3-series.csv`). The save itself was never opened for writing.
- `FxInterventionProbe.java`: the probe behind spec §1.4, three twins of
  CurrencyCheck's city (one buys dollars, one sells, one waits). It is in the
  game's package; from a compiled worktree:

      javac -cp "target/classes;<gson.jar>" -d <out> docs/studies/exchange-rates/FxInterventionProbe.java
      java -cp "<out>;target/classes;<gson.jar>" ham.citybuildersim.FxInterventionProbe

- `research-notes.md`: the sources, gathered for §2 (section per topic, each
  fact with its number and source).
