## changelog
### {next} — SS2, SaveSlotCheck 7d's hold on the swap's own count, not a clock — 2026-11-07, ready, see docs/queue/SS2-notes.md
No player sees this one: a check went red under load and no longer can. LB3's section 7d (a save's swap over a file another
program has open) held the file on a reader thread that let go after 100 ms of the wall clock, against the swap's ten tries
fifty milliseconds apart; under load the thread and the tries slipped apart, and the check went red at RT1's gate on whether
the swap "was refused and landed on a later try". The swap now calls a test seam, `GameFiles.onSwapRefused`, after each refused
try, and 7d lets go of the file from inside it on the second, so the release is a step of the swap itself and no time is
compared with a dial. The assertions are the same, with one added: the swap was refused exactly twice first. SaveSlotCheck ran
three times beside two other harness JVMs and 7d held every time; the one red in each, 7c's median due month, is main's
recorded timing red. Nothing in the model, a save or a screen changed, so the trace is PD1's and the ensemble was not run.
