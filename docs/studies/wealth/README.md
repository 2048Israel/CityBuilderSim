# The wealth study's code (spec-wealth.md)

Not game code: nothing in the tree reads this folder. Every city runs in `GameFiles.scratch()`, never a save slot.

In Git Bash at the root, after `mvn -q compile` and the classpath file `batch-check.sh` writes:

    CP="target/classes;$(cat target/classpath.txt)"; D=<the PC checkout>/data
    javac -nowarn -d /tmp/wealth -cp "$CP" docs/studies/wealth/WealthRun.java
    java -Xmx3g -Dcbs.earth=$D/earth-2.bin -Dcbs.world=$D/world-1.bin -cp "/tmp/wealth;$CP" \
         ham.citybuildersim.WealthRun SEED MONTHS OUT.csv          # about 7 minutes for 8,000 months
    python -I docs/studies/wealth/report.py OUT.txt OUT.csv ...      # the study's tables (out/)

- `-Dwealth.rule=on` plays the prototype player who reads the government's wealth (section 2.7).
- `-Dwealth.monthly=PATH` writes the households' gap and every household flow a month.
- `probe/`: `make-probe.py` makes the probed scratch `Game.java`; `ProbeRun` says which statement of nextMonth()
  moved the savings and the pools; `LinesRun` writes every audit line a month (section 2.4).
- `fx-prototype/make-fx.py` makes the scratch `ForeignAccounts` whose balance builds pressure (section 2.5).
