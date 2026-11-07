# ---------------------------------------------------------------------------
#  The gate's arithmetic for a timing red (TM3, 2026-10-27): was the batch slower
#  than main, or was the PC busy? POSIX awk (gawk, mawk, busybox, the PC's Git Bash).
#
#      awk -v mode=dial -f tools/timing-pair.awk /dev/null        the margin, in percent
#      awk -v mode=harnesses [-v margin=N -v rec=GATE -v today=DAY] -f tools/timing-pair.awk HARD RESIDUAL
#          the harnesses the rounds run, one a line: those with a timing red main's figure of the day leaves uncovered
#      awk -v mode=judge [-v margin=N -v rec=GATE -v today=DAY -v recout=FILE] -f tools/timing-pair.awk PAIRS HARD RESIDUAL
#          for each line of RESIDUAL: PASS or FAIL, a tab, the line, a tab, why; main's figures to RECOUT
#
#  HARD is the merge's hard run's target/checks/timing.tsv (Timing's rows: harness, label,
#  measured, budget, unit, status OK, SOFT or FAIL, part); PAIRS the rounds' rows, the same
#  with the side (main or batch) and the round in front; RESIDUAL the hard run's red lines
#  that are neither main's recorded reds nor the suite step's ("Harness | <line>", "Harness |
#  exit N", "Harness | N FAILED"); GATE origin/main's gate.txt, its "# date: DAY" line and its
#  "# figure" lines ("# figure", a tab, a HARD row, a tab, the row's place among its key's).
#
#  A red line is a timing red when a FAIL row of its harness has its label in the line
#  (digits masked: a label can print a figure). It passes when for every such row the batch's
#  median over the rounds is at most MARGIN percent over main's, or main's misses the budget
#  too, or the batch's is within it (SP1 item 7); or, with no round, main's figure of the day
#  covers it (GT2). It FAILs otherwise, with fewer than MIN_FIGURES figures a side, and when it
#  is no clock's. A harness's "exit N" and "N FAILED" pass with its timing reds that pass.
#
#  The dial, one place (TM3 item 2): MARGIN, 15 percent. A load that doubles a figure does it
#  to both sides in the same minutes; 15 is wide enough for their noise at three rounds and
#  narrow enough that the self-test's 30% stops. CBS_TIMING_MARGIN moves it for a test only.
#  TimingCheck 9 holds the arithmetic.
#
#  WHY (TM3): the gate judged a red by whether main's own run, minutes later, had the same
#  line; on 2026-10-26 three gates stopped on map lines main itself misses. SP1 item 7 (a
#  budget that is no number is not used): SC1, a printout, stopped twice on a shifting load.
#
#  GT2 (2026-11-07): the judge writes to RECOUT the merged main's figure of each hard miss
#  (the merge's median over the rounds, else its hard figure) while it still misses, and
#  ship.sh keeps them in gate.txt; a miss whose figure there, dated today, misses the budget
#  too, with the merge's at most MARGIN percent over it, needs no round. WHY: SaveSlotCheck
#  missed 25 ms on main at every gate since 11-05, and its rounds took 20-80 minutes a gate.
# ---------------------------------------------------------------------------

BEGIN {
    FS = "\t"
    if (margin == "") margin = 15
    if (mode == "dial") { print margin; exit }
    MIN_FIGURES = 2
    LSEP = "\035"   # between the keys of a list: a label has spaces, so a space will not do
    if (rec != "") while ((getline l < rec) > 0) {   # GT2: main's figures of the day, the "# figure" lines of a gate.txt dated today
        sub(/\r$/, "", l)
        if (l ~ /^# date: /) recday = (substr(l, 9) == today)
        if (recday && split(l, f, "\t") >= 9 && f[1] == "# figure" && isnum(f[4])) rfig[f[2] SUBSEP mask(f[3]) SUBSEP f[8] SUBSEP f[9]] = f[4] + 0
    }
}

function covered(key) {   # GT2: main's figure of the day misses the budget too, and the merge's hard figure is within the margin of it
    return (key in rfig) && isnum(hfig[key]) && isnum(hbud[key]) && rfig[key] > hbud[key] + 0 && hfig[key] * 100 <= rfig[key] * (100 + margin)
}

# which file this record is from, by its place among the operands (an empty file has no records, so counting files would slip)
{ stage = (FILENAME == ARGV[1]) ? 1 : (FILENAME == ARGV[2]) ? 2 : 3 }

function mask(s) { gsub(/[0-9]+/, "N", s); return s }

function isnum(s) { return s ~ /^-?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/ }

function median(arr, n,    i, j, t, a) {
    for (i = 1; i <= n; i++) a[i] = arr[i] + 0
    for (i = 2; i <= n; i++) { t = a[i]; for (j = i - 1; j >= 1 && a[j] > t; j--) a[j + 1] = a[j]; a[j + 1] = t }
    return n % 2 ? a[(n + 1) / 2] : (a[n / 2] + a[n / 2 + 1]) / 2
}

function num(v) {   # a figure as the notes print it: a whole number bare, else three significant places
    if (v == int(v)) return sprintf("%d", v)
    if (v >= 100) return sprintf("%.0f", v)
    if (v >= 10) return sprintf("%.1f", v)
    return sprintf("%.3g", v)
}

# the stages: judge reads PAIRS, HARD, RESIDUAL; harnesses reads HARD, RESIDUAL
function hardstage() { return mode == "judge" ? 2 : 1 }
function resstage() { return mode == "judge" ? 3 : 2 }

mode == "judge" && stage == 1 {   # PAIRS.tsv
    side = $1; harness = $3; label = $4; part = $9
    k = harness SUBSEP mask(label) SUBSEP part
    occ = ++seenp[side SUBSEP $2 SUBSEP k]   # the same label twice in one run: the second is a figure of its own
    key = k SUBSEP occ
    if (isnum($5)) {
        np = ++cnt[side SUBSEP key]
        fig[side SUBSEP key SUBSEP np] = $5 + 0
    }
    budget[key] = $6; unit[key] = $7
    next
}

stage == hardstage() {   # HARD.tsv: the figures that missed (hard, so FAIL)
    harness = $1; label = $2; part = $7
    m = mask(label)
    k = harness SUBSEP m SUBSEP part
    occ = ++seenh[k]
    if ($6 == "FAIL") {
        key = k SUBSEP occ; hfig[key] = $3; hbud[key] = $4; hlab[key] = label; hunit[key] = $5
        hmiss[harness SUBSEP m] = hmiss[harness SUBSEP m] (hmiss[harness SUBSEP m] == "" ? "" : LSEP) (k SUBSEP occ)
        hlabel[harness SUBSEP m] = label
        if (!((harness SUBSEP m) in hseen)) { hseen[harness SUBSEP m] = 1; hlist[++nh] = harness SUBSEP m }
    }
    next
}

stage == resstage() {   # RESIDUAL
    line = $0; sub(/\r$/, "", line)
    n = ++nl; text[n] = line
    p = index(line, " | ")
    if (p == 0) { kind[n] = "other"; next }
    h = substr(line, 1, p - 1); rest = substr(line, p + 3)
    head[n] = h
    if (rest ~ /^exit -?[0-9]+$/ || rest ~ /^[0-9]+ FAILED$/) { kind[n] = "agg"; next }
    kind[n] = "other"
    r = mask(rest); best = ""
    for (i = 1; i <= nh; i++) {   # the longest label of this harness that is in the line
        split(hlist[i], q, SUBSEP)
        if (q[1] != h || q[2] == "") continue
        if (index(r, q[2]) && length(q[2]) > length(best)) best = q[2]
    }
    if (best != "") { kind[n] = "timing"; mkey[n] = h SUBSEP best; timed[h] = 1 }
    next
}

END {
    if (mode == "dial") exit
    if (mode == "harnesses") {   # a harness whose every miss main's figure of the day covers runs no round (GT2)
        for (n = 1; n <= nl; n++) if (kind[n] == "timing" && !(head[n] in done))
            for (j = 1; j <= split(hmiss[mkey[n]], ks, LSEP); j++) if (!covered(ks[j])) { done[head[n]] = 1; print head[n]; break }
        exit
    }
    # judge: each timing red's rows, by the medians of the two sides
    for (n = 1; n <= nl; n++) {
        verdict[n] = "FAIL"; why[n] = ""
        if (kind[n] == "other") { why[n] = "not a timing red: a failure that is not a clock's"; continue }
        if (kind[n] == "agg") continue
        nk = split(hmiss[mkey[n]], ks, LSEP)
        ok = 1; msg = ""; anyover = 0
        for (j = 1; j <= nk; j++) {
            key = ks[j]
            nm = cnt["main" SUBSEP key]; nb = cnt["batch" SUBSEP key]
            if ((nm < MIN_FIGURES || nb < MIN_FIGURES) && covered(key)) {   # GT2: no round, main's figure of the day
                rm = rfig[key]; hf = hfig[key] + 0; u = hunit[key] == "" ? "ms" : hunit[key]
                msg = msg (msg == "" ? "" : "; ") "main " num(rm) " " u " (gate.txt, today), batch " num(hf) " " u sprintf(" (%+.1f%%)", (hf - rm) * 100 / rm) ", budget " hbud[key] " " u ": main over the budget too, no round"; continue
            }
            if (nm < MIN_FIGURES || nb < MIN_FIGURES) {
                ok = 0; msg = msg (msg == "" ? "" : "; ") "no pair to compare: " nm + 0 " figure(s) of main's, " nb + 0 " of the batch's (" MIN_FIGURES " needed)"
                continue
            }
            for (i = 1; i <= nm; i++) a[i] = fig["main" SUBSEP key SUBSEP i]
            mm = median(a, nm)
            for (i = 1; i <= nb; i++) b[i] = fig["batch" SUBSEP key SUBSEP i]
            bm = median(b, nb)
            over = bm * 100 > mm * (100 + margin)   # whole numbers where it can be: 115 against 100 at 15 is not over
            pct = mm > 0 ? (bm - mm) * 100 / mm : 0
            u = unit[key] == "" ? "ms" : unit[key]
            bud = isnum(budget[key])   # SP1 item 7: a budget that is no number leaves the margin alone to judge
            why1 = !over ? "within " margin "% of main's" : (bud && bm <= budget[key] + 0) ? "the batch within the budget" : (bud && mm > budget[key] + 0) ? "main over the budget too" : ""
            msg = msg (msg == "" ? "" : "; ") "main " num(mm) " " u ", batch " num(bm) " " u (mm > 0 ? sprintf(" (%+.1f%%)", pct) : "") ", budget " budget[key] " " u (why1 == "" ? "" : ": " why1)
            if (why1 == "") { ok = 0; anyover = 1 }
        }
        verdict[n] = ok ? "PASS" : "FAIL"
        why[n] = (anyover ? "over main's by more than " margin "%: " : "") msg
    }
    # a failed harness's own count and exit: with the timing reds that pass and nothing else red in it
    for (n = 1; n <= nl; n++) if (kind[n] == "timing" || kind[n] == "other") if (verdict[n] == "FAIL") bad[head[n]] = 1
    for (n = 1; n <= nl; n++) {
        if (kind[n] != "agg") continue
        if (head[n] in timed && !(head[n] in bad)) { verdict[n] = "PASS"; why[n] = "the harness's own count of the timing reds above" }
        else { verdict[n] = "FAIL"; why[n] = (head[n] in bad) ? "the harness's own count, with a red above that is not a timing red that passes" : "a harness that failed with no timing red of its own" }
    }
    for (n = 1; n <= nl; n++) printf "%s\t%s\t%s\n", verdict[n], text[n], why[n]
    if (recout != "") for (i = 1; i <= nh; i++) for (j = 1; j <= split(hmiss[hlist[i]], ks, LSEP); j++) {   # GT2: the merged main's figure of each miss, while it misses
        key = ks[j]; nb = cnt["batch" SUBSEP key]; v = hfig[key] + 0; split(key, q, SUBSEP)
        if (nb >= MIN_FIGURES) { for (x = 1; x <= nb; x++) b[x] = fig["batch" SUBSEP key SUBSEP x]; v = median(b, nb) }
        if (isnum(hbud[key]) && v > hbud[key] + 0) printf "# figure\t%s\t%s\t%s\t%s\t%s\tFAIL\t%s\t%s\n", q[1], hlab[key], v, hbud[key], hunit[key], q[3], q[4] > recout
    }
}
