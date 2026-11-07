# MG1 - the suite's memory guard by free GB, not 75% of the commit limit

Jerus, 2026-11-06, on the process write-up's bottlenecks ("Queue it", to "Queue
the batch that makes the suite's memory guard check free GB instead of a
percentage"). The figures below were measured on the PC that night.

**Why.** `AllChecks` starts no harness beside another while the commit charge
is over `COMMIT_PCT` (75%) of the commit limit (`commitPct()`, the wait in its
run loop). On 11-06 the charge sat at 80-91%, so the suite ran one harness at a
time: its harnesses' seconds summed to its wall time (2,738 s against 2,751 s).
On this PC the share is the wrong gauge:

- Windows sizes its automatic pagefile to the load. It had grown to 42 GB
  (limit 73 GB) with only 5.3 GB of it in use. When 26 idle sessions were
  archived, the limit shrank with the charge (73.0 to 69.7 GB), so the share
  fell only from 85% to 80%, with 14 GB free.
- Most of the charge is memory promised and never touched: the Claude
  processes held 26.6 GB committed and 8.9 GB in RAM.

SP1-E's larger pagefile is overtaken: the automatic one is already larger
than it asked for.

The guard must still prevent what it was made for: a start that cannot get
its memory (CreateProcess error 1450 at 43 of 54 GB, 2026-10-21), and three
starts that grow past the room together (79.8% to 85.9% within a minute,
2026-10-27). It must also prevent something new: paging when RAM itself is
short. RAM reached 89-91% in use on 11-06 while two KeptChecks ran.

**And the CPU** (Jerus, 2026-11-06: "Yes, add it", to a CPU condition in this
rule). Once a restart freed the memory, the CPU became the limit. At 23:20 the
commit charge was 41% and RAM 67%, but the CPU was at 94% of 20 threads, with
three batches checking (R3b, R0d, AN2) and XR5's gate running its suite. Two
KeptChecks were running at once. Over the 20 minutes after the restart the CPU
averaged 77% and sat at 85% or more for 8 of them. A harness started then only
splits the same threads: every run slows, and the gate's hard timing step runs
under more load, which costs paired rounds.

1. **The rule.** Another harness starts beside the running ones only while all
   three hold:
   - free commit (the limit less the charge) covers one more harness at its
     full size, `HEAP_MB` plus its measured non-heap, with the running ones'
     growth to their full size kept in reserve;
   - available RAM is at least a dial's GB;
   - the machine's CPU, averaged over the last few seconds, is under a dial's
     percent. Java reads it as `OperatingSystemMXBean.getCpuLoad()` on Windows
     and Linux alike.

   A run with nothing running always starts, as now. Each threshold is a dial
   in `AllChecks` with its sentence, valued from measurement (the notes give
   it). Elsewhere (Linux, the cloud) the memory figures come from
   `/proc/meminfo`, or the rule keeps today's swap reading: the batch's call.
2. **A harness for the rule**, as a pure function of free commit, available
   RAM, CPU load and what the running harnesses hold. Each threshold is caused
   from both sides against the dials, with no reading of the machine in the
   fixture.
3. **The line it prints** ("memory: N start(s) held until ...") names the
   figure that held them: GB for memory, percent for the CPU.
4. **Measured on the PC:** a soft suite run before and after, with its wall
   time and how often a start was held, and by which figure. The notes give
   both, with the commit, RAM and CPU figures at the time. MemWatch logs those
   a minute apart, in `C:\Users\Jerus\Tools\MemWatch\history-<date>.csv`.

**Done means:** the rule's harness is green. On the PC, under the load of the
day, the suite runs up to `PARALLEL` at once when the dials allow and holds
when they do not, no start fails for memory, and no start is made into a CPU
that is already full.

**Not this batch:** KeptCheck's run time (KT1); the timing lines main misses
under load (UP8, SS2, GT1-T). MG1 edits `AllChecks.java`, which KT1 may also
touch (`LONGEST_FIRST`), so the two run one after the other in a lane or on
files apart. It changes the checks' machinery, so one in, one out applies, and
its notes give the line count.

**A call for the orchestrator:** SP1-E (Pending, Jerus's pagefile) is settled
by this. Recommended: drop the row.
