# Timing pairs and nightly verdicts, POSIX awk (TM3, GR1).
# mode=dial prints the margin. mode=night reads yesterday.tsv and tonight.tsv.
# mode=night-pair reads PAIRS, with wantedh/wantedl/wantedp/wantedo identifying
# one assertion, and prints its verdict, medians, budget and unit. PAIRS rows
# prefix Timing's seven columns with side (main/batch) and round. Keys mask
# label digits and retain part and occurrence. At least two figures per side.
# mode=judge and mode=harnesses retain TimingCheck's arithmetic fixtures.
# WHY (TM3): prose labels and single measurements misjudged three map gates.

BEGIN {
    FS = "\t"
    if (margin == "") margin = 15
    if (mode == "dial") { print margin; exit }
    MIN_FIGURES = 2
    LSEP = "\035"   # between the keys of a list: a label has spaces, so a space will not do
}

# Operand identity works even when the baseline file is empty.
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

(mode == "judge" || mode == "night-pair") && stage == 1 {   # PAIRS.tsv
    side = $1; harness = $3; label = $4; part = $9
    k = harness SUBSEP mask(label) SUBSEP part
    occ = ++seenp[side SUBSEP $2 SUBSEP k]   # the same label twice in one run: the second is a figure of its own
    key = k SUBSEP occ
    if (isnum($5)) {
        np = ++cnt[side SUBSEP key]
        fig[side SUBSEP key SUBSEP np] = $5 + 0
    }
    if (mode != "night-pair" || side == "batch") { budget[key] = $6; unit[key] = $7 }
    next
}

# GR1: same keys and medians as the gate's pairs, with quiet nights as the baseline.
mode == "night" {
    k = $1 SUBSEP mask($2) SUBSEP $7
    occ = ++nightseen[stage SUBSEP k]; key = k SUBSEP occ
    if (stage == 1) { previous[key] = $3; next }
    if (!isnum($3) || !isnum($4)) { print "INVALID\t" $0; next }
    state = $3 <= $4 ? "OK" : !(key in previous) ? "FIRST" : previous[key] <= $4 || $3 * 100 > previous[key] * (100 + margin) ? "SLOWER" : "STANDING"
    printf "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", state, $1, $2, (key in previous ? previous[key] : "none"), $3, $4, $5, $7, nightseen[stage SUBSEP k]
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
    if (mode == "night") exit
    if (mode == "night-pair") {
        key = wantedh SUBSEP mask(wantedl) SUBSEP wantedp SUBSEP wantedo
        nm = cnt["main" SUBSEP key]; nb = cnt["batch" SUBSEP key]
        if (nm < MIN_FIGURES || nb < MIN_FIGURES || !isnum(budget[key])) { print "INCONCLUSIVE\tmissing paired figures"; exit 2 }
        for (i = 1; i <= nm; i++) a[i] = fig["main" SUBSEP key SUBSEP i]
        for (i = 1; i <= nb; i++) b[i] = fig["batch" SUBSEP key SUBSEP i]
        mm = median(a, nm); bm = median(b, nb)
        # A standing miss can worsen too (GR1), so both sides missing grants no exemption.
        state = bm > budget[key] && bm * 100 > mm * (100 + margin) ? "SLOWER" : "STEADY"
        printf "%s\t%s\t%s\t%s\t%s\n", state, num(mm), num(bm), budget[key], unit[key]
        exit
    }
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
