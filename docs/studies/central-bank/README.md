# central-bank/ - beside spec-central-bank.md

- `cb-model.py`: the study's model. The dial today and in steps on Halifax's
  own inflation, the same in the game (the 240-month runs), the corridor and
  the overnight rate, the remittance puzzle month by month, four months'
  statements three ways, and CD1's expected dial as a staircase. It copies the
  game's constants by name and marks the study's own as PROPOSED.
  `python -I cb-model.py` prints every table the study quotes (`report.txt` is
  that output); `--json` also writes `boards.json`.
- `build-boards.py`: builds `central-bank-study.html` from `boards.src.html`
  and `boards.json`.
- `boards.src.html`: the boards' template (the page with `/*DATA*/null` where
  the figures go).
- `central-bank-study.html`: the five boards, built (the artifact "Central Bank
  Study", https://claude.ai/artifact/7UCd3e8od9pPVY87aYB8jf). A static page.
- `extract.py`: reads a save's history file and writes the columns the model
  reads. It only reads.
- The data, all from Jerus's saves, read and never written:
  - `halifax.csv`: slot 3 ("for claude", Halifax, 0.8.10), its history month by
    month (`extract.py`).
  - `halifax-replay.csv`: a copy of slot 3 played 120 months on in a scratch
    folder, the central bank's profit split into its parts each month.
  - `bergen-replay.csv`: a copy of slot 1 (Bergen) played 3 months the same way.
  - `halifax-240-*.csv`: copies of slot 3 played 240 months on, the dial on
    today's autopilot (`today`) or in steps (`every2`, `every3`,
    `every3-cap1`).
- `probe/`: the two probes that played the copies, `CbProbe` and `CbSteps`,
  as `.java.txt` so nothing builds them. To run one, copy it to a folder of
  its own as `ham/citybuildersim/<Name>.java`, compile it against
  `target/classes` and Gson, and run it with `-Dcbs.earth=<earth-1.bin>`. Each
  copies a save into `GameFiles.scratch()` and loads it with
  `loadGameSave()`, never touching the saves folder.

Python 3, standard library only. Nothing in the game or the harnesses reads
this folder. `boards.json` is left out of git: `cb-model.py --json` writes it
again.
