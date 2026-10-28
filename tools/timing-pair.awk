# ---------------------------------------------------------------------------
#  The gate's arithmetic for a timing red (TM3, 2026-10-27): was the batch slower
#  than main, or was the PC busy? ship.sh times the red harness again with main's
#  classes and the merge's in turn, in the same minutes, and this script judges
#  the figures that came out. POSIX awk (gawk, mawk, busybox, the PC's Git Bash).
#
#      awk -v mode=dial -f tools/timing-pair.awk /dev/null        prints the margin, in percent
#      awk -v mode=harnesses -f tools/timing-pair.awk HARD.tsv RESIDUAL
#                  the harnesses with a timing red among RESIDUAL's lines, one a line
#      awk -v mode=judge [-v margin=N] -f tools/timing-pair.awk PAIRS.tsv HARD.tsv RESIDUAL
#                  one line for each line of RESIDUAL: PASS or FAIL, a tab, the line, a
#                  tab, why (the figures, or what is wrong with it)
#
#  The files:
#    HARD.tsv      target/checks/timing.tsv of the merge's hard run: what Timing writes,
#                  tab-separated - harness, label, measured, budget, unit, status
#                  (OK, SOFT or FAIL), part - one row for each timing assertion's figure
#    RESIDUAL      the hard run's red lines that are neither main's recorded reds nor the
#                  suite step's, as reds.txt has them: "Harness | <the failing line>",
#                  and "Harness | exit N" and "Harness | N FAILED" for a failed harness
#    PAIRS.tsv     the paired rounds' rows: HARD.tsv's columns with two in front - the
#                  side (main or batch) and the round
#
#  A red line is a timing red when a row of the hard run that missed its budget (status
#  FAIL) belongs to its harness and its label is in the line (digits masked, since a label
#  can print a figure). It passes when, for every such row, the batch's median figure over
#  the rounds is no more than MARGIN percent above main's, or main's own median misses the
#  budget too, or the batch's is within it (SP1 item 7, below). It FAILs when over that, when a
#  side has fewer than MIN_FIGURES figures to take a median of (main has no such
#  assertion; a run did not run), and when it is no timing red at all - a failure that
#  is not a clock's. "Harness | exit N" and "Harness | N FAILED" say a harness failed:
#  they pass when the harness has a timing red that passes and nothing else failed in it.
#
#  The dial, one place (TM3 item 2): MARGIN, 15 percent. A load that doubles a figure
#  does it to main's and the batch's alike in the same minutes; what is left is the
#  batch's. 15 is wide enough for the two sides' noise at three rounds and narrow
#  enough that a 30% slowdown (the self-test's) stops. ship.sh takes CBS_TIMING_MARGIN
#  for a test; nothing else moves it. TimingCheck holds the arithmetic.
#
#  WHY (TM3): the gate timed the merge once, hard, and judged a red by whether main's
#  own timing run, minutes later, had the same line. On 2026-10-26 three gates (HY1,
#  MS2, CC1-S) stopped on map lines main itself misses under Jerus's running game, and
#  two shipped only through a hand-patched copy of ship.sh.
#
#  SP1 item 7 (2026-10-27): a figure over the margin is still not the batch's when main's
#  median misses its budget too, or the batch's is within it (at most it; a budget that is
#  no number is not used). WHY: SC1, which only added a printout, stopped twice under a
#  shifting load: MapCheck's month at 5B (main 1.81 ms, batch 2.35, both within 5 ms) and
#  KeptCheck's chrome (main 10.4, batch 12.3, both over 8 ms). The reason follows the figures.
# ---------------------------------------------------------------------------

BEGIN {
    FS = "\t"
    if (margin == "") margin = 15
    if (mode == "dial") { print margin; exit }
    MIN_FIGURES = 2
    LSEP = "\035"   # between the keys of a list: a label has spaces, so a space will not do
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
    if (mode == "harnesses") {
        for (n = 1; n <= nl; n++) if (kind[n] == "timing" && !(head[n] in done)) { done[head[n]] = 1; print head[n] }
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
}
