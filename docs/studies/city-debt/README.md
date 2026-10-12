# city-debt/ - beside spec-city-debt.md

- `debt-model.py`: the study's model. The curve from the dial the market
  expects, every instrument's price at issue, the callable bond on a Ho-Lee
  tree, the auction, effective interest, the reverse auction and the round
  trip. It copies the game's constants by name and marks the study's own as
  PROPOSED. `python3 debt-model.py` prints every table the study quotes;
  `--json` also writes `figures.json` beside it.
- `book-data.py`: one city's book in January 2203, two years after a calm
  January 2201 and eighteen months into a hike, priced by `debt-model.py`'s
  arithmetic. It writes `boards-data.json` and builds the boards page from
  its template.
- `boards.src.html`: the boards' template (the page with `/*DATA*/null`
  where the figures go).
- `city-debt-study.html`: the five boards, built (the artifact "City Debt
  Study"): Finances › The book as a desk, a line's page, Close it now, Borrow
  as an auction, and Your rate. A static page; open it in a browser.

Python 3 with numpy. Nothing in the game or the harnesses reads this folder.
The two JSON files are left out of git: either script writes them again.
