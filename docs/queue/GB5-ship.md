<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-GB5-AJMa9yLa -->
<!-- readiness: provisional -->
## changelog
### GB5 (untagged) — the gate box runs the harnesses that need a display — 2026-11-21, ready, see docs/queue/GB5-notes.md
The gate box has no screen, so since GB1 every gate skipped the nine harnesses that start the JavaFX toolkit, KeptCheck and KeptTimingCheck among them.
Its gates and nights now draw them on JavaFX's Monocle, headless and in software (pom.xml, test scope), which tools/gate-box.sh names to every JVM they
start; Linux lays text out through Pango (libpangoft2-1.0-0, which Jerus installed), and a box without it says so in one line.
Every display harness starts the toolkit through ui.FollowBench.startJavaFx. With no display named it skips as it did, on the PC and in the cloud;
with one named (-Dcbs.display) a toolkit that cannot start is a FAIL and exit 1, never a skip. The summary's display line lists those a run started
(AllChecks.DISPLAY), and TimingCheck 10 guards the one start, the list and the skip. On the box KeptCheck prints the PC's 4,034 lines but its heap
line, and KeptTimingCheck meets all 29 budgets, the worst at 0.42 of its own. The PC's runs are as they were.
## known-reds
KeptCheck | most months make no node at all (9 of 25) | FS5 | 2026-11-21 | Section 55's Bank Profit page, 9 of 25 quiet months: the played city moved after FS4's last KeptCheck (303830e24, green), OE1's order estimate among 36 source files; red on main's own KeptCheck on the PC, unjudged since the gate moved to the box (GB5-notes.md)
KeptCheck | ...the held slider is the same node, still held, at the thumb (0.005) | FS5 | 2026-11-21 | Section 78 (KRT1's 78, FS5's range): the same moved played city, red on main's own KeptCheck on the PC and on the box alike, unjudged since the gate moved to the box (GB5-notes.md)
KeptCheck | the full kept check leaves a quarter of AllChecks.HEAP_MB free | FS5 | 2026-11-21 | Section 102 on the gate box's software renderer (Monocle, prism.order=sw, GB5): a canvas once drawn keeps its render target on the Java heap while it lives, where the PC's Direct3D keeps it in video memory. The pages hold the same objects on both (43,202 Labels each, int[] 226 MB on the box against about 40); 1,554-1,719 MiB against 1,536 (KEPT_HEAP_SHARE .75 of 2,048), the PC 1,379, the PC in software 1,594-1,780 (GB5-notes.md, ★5)
