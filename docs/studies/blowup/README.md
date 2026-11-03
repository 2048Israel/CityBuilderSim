# The blow-up study's code (spec-blowup-7200.md)

Not game code: nothing in the tree reads this folder. Every city runs in `GameFiles.scratch()`, never a save slot.
The drivers listen to the wealth study's `WealthRun` (on its branch, `docs/studies/wealth/WealthRun.java`) through
its `hook`, so the city plays exactly the wealth study's path.

In Git Bash at the root, after `mvn -q compile` and the classpath file `batch-check.sh` writes:

    CP="target/classes;$(cat target/classpath.txt)"; D=<the PC checkout>/data; O=<a scratch folder>
    javac -nowarn -d $O/cls -cp "$CP" <WealthRun.java> docs/studies/blowup/*.java
    J="java -Xmx3g -Dcbs.earth=$D/earth-2.bin -Dcbs.world=$D/world-1.bin"
    $J -Dblowup.from=7200 -cp "$O/cls;$CP" ham.citybuildersim.BlowupRun 1 7600 $O/seed1.csv $O/seed1-lines.csv   # ~4 min
    $J -Dblowup.from=7378 -cp "$O/cls;$CP" ham.citybuildersim.SectorProbe 1 7388 $O/probe1.txt
    $J -Dblowup.from=7200 -cp "$O/cls;$CP" ham.citybuildersim.HaulageProbe 1 7390 $O/haul1.csv
    $J -Dblowup.from=99999 -Dblowup.every=12 -cp "$O/cls;$CP" ham.citybuildersim.HaulageProbe 1 8000 $O/year1.csv
    python -I docs/studies/blowup/report.py $O > docs/studies/blowup/out/report.txt

Seeds 0 and 1 the same (seed 0's blow-up is at month 7,515: `-Dblowup.from=7508`, 7,517 months).

- `BlowupRun`: the bank, the central bank's window and the audit's totals a row a month, and every audit line (long format).
- `SectorProbe`: every sector whose month moved more than `-Dblowup.gdp` (0.5) months of GDP, line by line, with what was forgiven.
- `HaulageProbe`: Haulage's price rule term by term (read by reflection) and Freight's books; `-Dblowup.every=12` adds a row a year before `blowup.from`.
- `prototype/make-haulage.py`: the scratch `Haulage.java` of the fix (section 4.1); `--no-floor` is HQ1's version. Compile it into a
  folder put ahead of `$O/cls` on the classpath and run `HaulageProbe` as above into `fix*.csv` (with the floor) or `fixB*.csv`.
