#!/bin/sh
# ---------------------------------------------------------------------------
#  A batch's checks in one call, for a session on Linux (a cloud session, the
#  orchestrator's gate, ship.sh) or in Git Bash on the PC: compile, the suite,
#  the maps, Stale and the tree's state, each said in a line or two.
#  Everything long goes to a file under target/checks/ and the line says which.
#
#      ./batch-check.sh               compile, the whole suite, maps, Stale, git status
#      ./batch-check.sh money bank    the same, the suite only the harnesses named so
#      ./batch-check.sh --fast        compile, maps, Stale, git status: no suite
#      ./batch-check.sh --timing      compile and the timing harnesses alone, hard: the ship gate's
#      ./batch-check.sh --pick        the plain run, the suite only the harnesses tools/pick.sh picks
#                                     from the diff against main, plus any names typed after it
#      ./batch-check.sh --judge LOG   judge a saved AllChecks -f log: nothing built, nothing run, no lock
#
#  One of --fast, --timing and --pick, and harness names, in any order; --judge
#  takes its one log and nothing else. After a suite run the red lines are in
#  target/checks/reds.txt, one "Harness | exit N" per failed harness and one
#  "Harness | <failing line>" per failing line.
#
#  Not here: the 80-run ensemble, tools/ensemble/ensemble.sh, run only for a
#  batch whose playtest trace moves (EN1, 2026-10-20; tools/ensemble/README.md).
#
#  Exit status, which is what ship.sh reads:
#      0  it compiled, every harness run passed, Stale has no firm finding
#      1  the compile, the classpath, the maps, Stale or the line endings failed,
#         the pick's guard did, or the suite printed no summary or ran 0
#         harnesses (AllChecks skips a name it does not know without a word)
#      2  usage: an unknown option, two of --fast/--timing/--pick, --fast with
#         names, a name no harness file has - said before anything is built
#      3  nothing failed but the suite, and every red line is a known red: in
#         main's recorded reds (the non-# lines of docs/notes/gate.txt as it was at
#         the merge-base with origin/main, else with main, else this tree's:
#         at the gate HEAD is main, so that is main's own record; in a batch
#         it is the record of the main the batch forked from, whose reds its
#         tree can have - not the reds main has gained or mended since)
#      4  nothing else failed, but some red line is NEW: not recorded
#  When something else failed as well, 1. --judge LOG exits as the suite's
#  verdict does (0, 1, 3 or 4), and 2 for a usage error.
#
#  WHY (2026-10-13): a session paid for every line it read, and a batch's
#  checks were a dozen calls - each re-reading the whole conversation - and
#  their output: a line for every harness, a red one run again in full to find
#  its assertion, Stale's report whole. This prints about ten lines when all is
#  well and the failing assertions when it is not (AllChecks -f).
#  BP1 (2026-10-25): a batch ran the whole suite three times (15-22 minutes
#  each) and a person read every red to tell main's own from the batch's; now a
#  batch runs --pick (tools/pick.sh: the harnesses that read what its diff
#  changed), the one full suite is the gate's, and the red lines and the exit
#  statuses 3 and 4 let the gate pass main's recorded reds, word for word, with
#  nobody reading the log.
#  FW7 (2026-10-25): AllChecks -q hid a second red in a harness that was already
#  a known red, and only this script compared lines. AllChecks now reads the
#  record too (-Dcbs.knownReds, the file written to target/checks/known-reds.txt
#  before the suite runs) and says "known red" or "NEW red: N of M" of each
#  failed harness, tagging the lines; this script strips the tags (the red lines
#  are the same as before) and refuses a log whose N is not its own count, so
#  the two judges cannot disagree unseen. --judge LOG gives the verdict on a
#  saved log, from the folder the log is in, against the record named by
#  $CBS_KNOWN_REDS (read only there, never in a real run): what KnownRedsCheck
#  runs, from inside the suite, on the logs it builds.
#
#  On the cloud workbench (2 cores) one JVM at a time: under
#  the lock at $CBS_JVM_LOCK (default /home/claude/jvm.lock) when its folder
#  exists and flock is installed. The PC's twin is Batch check.bat.
#
#  Timing (TM1, 2026-10-18; BP1, 2026-10-25; GP1, 2026-10-27): every run but --timing
#  times soft (-Dcbs.timing=soft): a timing figure over its budget says SOFT on
#  its line and is not counted, and a line says when another batch's JVM or the
#  game is running (on the PC tools/jvms.ps1 counts them; elsewhere ps does).
#  Timing is judged once, hard, by --timing, the ship gate's, and it starts at
#  once. WHY (GP1): TM2 made it wait for a quiet minute (four empty looks in a
#  row, 30 minutes at most), so that a hard run did not share the machine with
#  another batch's JVM; with two lanes that was often the whole of a gate's
#  wait, and since TM3 a timing red is judged against main's own run in the same
#  minutes (ship.sh), which is what the wait only approximated.
#  In Git Bash the classpath is joined with ';', as Java on Windows wants.
#
#  The figures (TM3, 2026-10-27): every suite run writes each timing assertion's
#  measured figure and budget to target/checks/timing.tsv (Timing.TSV_PROPERTY; the
#  columns are in Timing), the old file removed first. ship.sh reads it, never a
#  label's prose, to judge a timing red side by side with main (tools/timing-pair.awk):
#  it runs `./batch-check.sh --timing HARNESS...` in main's worktree and in the
#  merge's tree in turn, back to back, to share one load.
# ---------------------------------------------------------------------------

ASKED_FROM=$(pwd)   # where the caller stood: a relative LOG of --judge is theirs, not the repository's
cd "$(dirname "$0")" || exit 1

# --- the options: read and checked first, before the lock and the compile (BP1) ---
FAST=0; TIMING=0; PICK=0; JUDGE=0; WANTLOG=0; LOGARG=""; NAMES=""
bad() { echo "batch-check: $1" >&2; echo "usage: ./batch-check.sh [--fast | --timing | --pick] [harness names]   (no names with --fast)" >&2; echo "       ./batch-check.sh --judge LOG   (a saved AllChecks -f log, nothing else)" >&2; exit 2; }
HARNESS_FILES=$(ls src/main/java/ham/citybuildersim 2>/dev/null | sed -n 's/\.java$//; /Check$/p; /^LongPlaytest$/p')
for a in "$@"; do
    if [ "$WANTLOG" = 1 ]; then LOGARG="$a"; WANTLOG=0; continue; fi   # the argument after --judge is its log, whatever it looks like
    case "$a" in
        --fast)   FAST=1 ;;
        --timing) TIMING=1 ;;
        --pick)   PICK=1 ;;
        --judge)  JUDGE=1; WANTLOG=1 ;;
        -*)       bad "unknown option $a" ;;
        '')       bad "an empty harness name" ;;
        *)        if [ -n "$HARNESS_FILES" ] && ! printf '%s\n' "$HARNESS_FILES" | grep -q -i -F -- "$a"; then
                      bad "no harness file is named like '$a' (AllChecks would run none and say nothing)"
                  fi
                  NAMES="$NAMES $a" ;;
    esac
done
[ $((FAST + TIMING + PICK)) -le 1 ] || bad "--fast, --timing and --pick exclude each other"
[ "$FAST" = 0 ] || [ -z "$NAMES" ] || bad "--fast runs no suite, so the names$NAMES select nothing"
if [ "$JUDGE" = 1 ]; then   # FW7: judge a saved log; exactly one log, no other option or name
    [ "$WANTLOG" = 0 ] && [ -n "$LOGARG" ] || bad "--judge needs the log to judge after it"
    [ "$#" = 2 ] || bad "--judge takes one log and no other option or name"
    case "$LOGARG" in /*|?:*) ;; *) LOGARG="$ASKED_FROM/$LOGARG" ;; esac
    [ -f "$LOGARG" ] || bad "--judge: $LOGARG is not a file"
    [ -z "$CBS_KNOWN_REDS" ] || [ -f "$CBS_KNOWN_REDS" ] || bad "CBS_KNOWN_REDS names no file: $CBS_KNOWN_REDS"
fi

LOCK="${CBS_JVM_LOCK:-/home/claude/jvm.lock}"
if [ "$JUDGE" = 0 ] && [ -z "$CBS_LOCKED" ] && command -v flock >/dev/null 2>&1 && [ -d "$(dirname "$LOCK")" ]; then
    export CBS_LOCKED=1
    exec flock "$LOCK" "$0" "$@"
fi

set -f; set -- $NAMES; set +f   # from here "$@" is the harness names alone
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) WINDOWS=1; SEP=';' ;;
    *) WINDOWS=0; SEP=':' ;;
esac

# --- who else runs: one look, for the soft run's line (TM1) ---
others() {   # "<batch JVMs> <game processes>" on the machine, this run not yet among them
    if [ "$WINDOWS" = 1 ]; then
        powershell.exe -NoProfile -ExecutionPolicy Bypass -File tools/jvms.ps1 2>/dev/null | tr -d '\r'
    else
        PROCS=$(ps -eo args 2>/dev/null)
        GAME=$(printf '%s\n' "$PROCS" | grep -c -E 'ham\.citybuildersim\.CityBuilderSim\b|CityBuilderSim-[^/"]*\.jar')
        BATCH=$(printf '%s\n' "$PROCS" | grep -E 'ham\.citybuildersim\.' | grep -c -v -E 'ham\.citybuildersim\.CityBuilderSim\b')
        echo "$BATCH $GAME"
    fi
}

if [ "$JUDGE" = 1 ]; then   # the folder of the log, so a suite writing to target/checks is never overwritten
    OUT=$(dirname "$LOGARG"); SUITE="$LOGARG"
else
    OUT=target/checks; SUITE="$OUT/suite.txt"
fi
mkdir -p "$OUT"
KNOWN_FROM=docs/notes/gate.txt   # what a message says the record is
FAILED=0
quiet() { grep -v '^Picked up JAVA_TOOL_OPTIONS'; }   # the cloud's proxy settings, said by every JVM
SUITE_EXIT=0   # 0 green or no suite, 3 only main's recorded reds, 4 reds not recorded (the exit status, if nothing else failed)
RUNSUITE=1

# the suite log's reds (BP1), one line each: "Harness | exit N" for a failed harness, "Harness | <line>" for each
# failing line AllChecks -f printed under it, its 4-space indent stripped; a section title is not a failing line.
# The exit may be negative (BP1, 2026-10-25): on Windows a JVM that dies natively reaches AllChecks as a negative int
# (0xC0000005 is -1073741819), and a header that did not match was dropped with its lines - a crash passed beside
# main's recorded red as exit 3.
reds_of() {
    awk '
    /^[A-Za-z0-9_]+ +FAILED - exit -?[0-9]+/ { h = $1; nofail = 0; infail = 1; print h " | exit " $5; next }
    !/^    / { infail = 0; next }
    !infail { next }
    {
        t = substr($0, 5)
        sub(/^[ \t]+/, "", t); sub(/[ \t\r]+$/, "", t)
        sub(/^\[(known|NEW)\] +/, "", t)                   # the tag AllChecks puts there (FW7); a log from before it has none
        if (t == "(no FAIL line; its last lines:)") { print h " | (no FAIL line)"; nofail = 1; next }
        if (nofail) next                                   # a harness that died some other way: its last lines are not failures
        if (t ~ /^---[ \t].*[ \t]---$/) next
        if (t ~ /^\.\.\. [0-9]+ failing lines in all: /) sub(/: .*$/, "", t)   # the log path differs by machine
        if (t != "") print h " | " t
    }' "$1"
}

# main's recorded reds: docs/notes/gate.txt as it was at the merge-base with origin/main, else with main, else this
# tree's, else none. At the gate HEAD is main (or main plus the batch), so the merge-base is origin/main's tip and this
# is ship.sh's own recorded(); in a batch it is the record of the main the batch forked from. WHY (BP1, 2026-10-25):
# the tip of origin/main was read first, so a batch that forked while MapCheck 8 was recorded saw it as a new red the
# day main's fix rewrote gate.txt without it - a red its own tree still has, and one it was told to mend twice.
recorded_reds() {
    for r in origin/main main; do
        mb=$(git merge-base HEAD "$r" 2>/dev/null) || continue
        if git cat-file -e "$mb:docs/notes/gate.txt" 2>/dev/null; then git show "$mb:docs/notes/gate.txt"; return 0; fi
    done
    [ -f docs/notes/gate.txt ] && cat docs/notes/gate.txt
    return 0
}

# the record the verdict is judged against: docs/notes/gate.txt as recorded_reds has it, or in --judge mode the file named
# by $CBS_KNOWN_REDS (read nowhere else), without its # and blank lines. Written to $OUT/known-reds.txt before the suite
# runs and given to AllChecks (FW7, 2026-10-25), which tags its red lines against that same file.
record_reds() {
    if [ "$JUDGE" = 1 ] && [ -n "$CBS_KNOWN_REDS" ]; then cat "$CBS_KNOWN_REDS"; else recorded_reds; fi \
        | tr -d '\r' | grep -v -e '^#' -e '^[[:space:]]*$'
}

# the suite is red: 3 when every red line is a known one (main's), else 4 with the lines that are NEW. Before that, a
# guard that does not depend on the header's form (BP1, 2026-10-25): the summary says how many harnesses failed, and
# reds.txt must name as many - a harness whose header reds_of could not read would otherwise be dropped, and the
# rest pass as main's recorded reds. And a guard on AllChecks' own verdict (FW7): when its headers say how many red
# lines are NEW, those counts must add up to this script's own, or the two judges disagree and neither is trusted.
judge_reds() {
    if [ -s "$OUT/known-reds.txt" ]; then
        grep -v -x -F -f "$OUT/known-reds.txt" "$OUT/reds.txt" > "$OUT/new-reds.txt"
    else
        cp "$OUT/reds.txt" "$OUT/new-reds.txt"
    fi
    NEW=$(grep -c . "$OUT/new-reds.txt")
    NAMED=$(grep -c -E '^[A-Za-z0-9_]+ \| exit -?[0-9]+$' "$OUT/reds.txt")
    SAID=$(sed -n 's/^[0-9][0-9]* harness.* run in .*, \([0-9][0-9]*\) failed$/\1/p' "$SUITE" | tail -1)
    if [ "$NAMED" = 0 ]; then
        FAILED=1
        echo "suite     FAILED - the log says harnesses failed but no red line could be read from it: $SUITE"
        return 0
    fi
    if [ "$NAMED" != "$SAID" ]; then
        FAILED=1
        echo "suite     FAILED - the log says ${SAID:-an unreadable number of} harness(es) failed, its red lines name $NAMED: $SUITE"
        return 0
    fi
    if grep -q -E '^[A-Za-z0-9_]+ +FAILED - exit -?[0-9]+.*  (known red|NEW red: [0-9]+ of [0-9]+ red lines)$' "$SUITE"; then
        COUNTED=$(sed -n 's/^[A-Za-z0-9_][A-Za-z0-9_]* *FAILED - exit -\{0,1\}[0-9][0-9]*.*  NEW red: \([0-9][0-9]*\) of [0-9][0-9]* red lines$/\1/p' "$SUITE" \
            | awk '{ n += $1 } END { print n + 0 }')
        if [ "$COUNTED" != "$NEW" ]; then
            FAILED=1
            echo "suite     FAILED - AllChecks counts $COUNTED NEW red line(s), this script $NEW: the two judges disagree ($SUITE)"
            return 0
        fi
    fi
    if [ "$NEW" = 0 ]; then
        SUITE_EXIT=3
        echo "suite     known red only - $(sed -n 's/ | exit -\{0,1\}[0-9]*$//p' "$OUT/reds.txt" | paste -sd, - | sed 's/,/, /g') ($KNOWN_FROM)"
    else
        SUITE_EXIT=4
        echo "suite     FAILED - NEW red: $NEW line(s), the known reds aside:"
        head -30 "$OUT/new-reds.txt" | sed 's/^/    /'
        [ "$NEW" -le 30 ] || echo "    ... $((NEW - 30)) more: $OUT/new-reds.txt"
    fi
}

# the verdict on the suite log $SUITE: what a run says of its own suite, and what --judge says of a saved one (FW7)
suite_verdict() {
    reds_of "$SUITE" > "$OUT/reds.txt"
    if ! grep -q -E '^[0-9]+ harness(es)? run in ' "$SUITE"; then
        FAILED=1
        echo "suite     FAILED - no summary line, the run did not finish; its last lines:"
        sed '/^$/d' "$SUITE" | tail -15 | sed 's/^/    /'
    elif grep -q '^0 harnesses run' "$SUITE"; then
        FAILED=1
        echo "suite     FAILED - 0 harnesses run: no name matches a harness AllChecks runs (it skips an unknown one without a word)"
        sed '/^$/d' "$SUITE" | head -5 | sed 's/^/    /'
    elif grep -q ' 0 failed$' "$SUITE"; then
        echo "suite     OK - $(grep 'harness.* run in ' "$SUITE" | sed 's/:.*//')"
    else
        judge_reds
    fi
}

# the end: a failure of anything but the suite's harnesses is 1; else the suite's own 0, 3 or 4
finish() { [ "$FAILED" = 0 ] || exit 1; exit "$SUITE_EXIT"; }

# --- --judge LOG: the verdict on a saved log, nothing built, nothing run, no lock (FW7) ---
if [ "$JUDGE" = 1 ]; then
    [ -z "$CBS_KNOWN_REDS" ] || KNOWN_FROM="$CBS_KNOWN_REDS"
    record_reds > "$OUT/known-reds.txt"
    suite_verdict
    finish
fi

# --- compile ---------------------------------------------------------------
t=$(date +%s)
if mvn -q -B compile > "$OUT/compile.txt" 2>&1; then
    echo "compile   OK ($(( $(date +%s) - t ))s)"
else
    echo "compile   FAILED - whole output: $OUT/compile.txt"
    if grep -q '\.java:\[' "$OUT/compile.txt"; then
        grep '\.java:\[' "$OUT/compile.txt" | sed 's#^.*/src/main/java/#    #' | sort -u | head -20
    else
        quiet < "$OUT/compile.txt" | tail -15 | sed 's/^/    /'
    fi
    exit 1
fi

# the classpath the suite runs on (gson and JavaFX), asked of Maven once and again when the pom changes
if [ ! -s target/classpath.txt ] || [ pom.xml -nt target/classpath.txt ]; then
    mvn -q -B dependency:build-classpath -Dmdep.outputFile=target/classpath.txt > "$OUT/classpath.txt" 2>&1 \
        || { echo "classpath FAILED - $OUT/classpath.txt"; exit 1; }
fi
CP="target/classes$SEP$(cat target/classpath.txt)"

# --- --pick: the suite narrowed to the harnesses the diff needs (tools/pick.sh, BP1) ----
if [ "$PICK" = 1 ]; then
    PICKED=$(sh tools/pick.sh 2> "$OUT/pick.txt"); PRC=$?
    cat "$OUT/pick.txt"
    if [ "$PRC" != 0 ]; then
        FAILED=1; RUNSUITE=0
        echo "pick      FAILED (exit $PRC) - no suite: $OUT/pick.txt"
    else
        case "$PICKED" in
            ALL)  set -- ;;   # the whole suite, whatever was typed
            NONE) if [ "$#" = 0 ]; then
                      RUNSUITE=0
                      echo "suite     none picked - no harness reads what changed; the gate runs the suite"
                  fi ;;
            *)    set -f; set -- $PICKED "$@"; set +f ;;
        esac
    fi
fi

# --- timing: hard for --timing alone, soft for every other run (TM1; BP1 made it always) --
TIMED=""
if [ "$TIMING" = 1 ]; then
    TIMED="-Dcbs.timing=hard"
    set -- --timing "$@"
    echo "timing    hard - the timing harnesses alone"
elif [ "$FAST" = 0 ]; then
    TIMED="-Dcbs.timing=soft"   # always: only --timing, the gate's, judges timing (BP1, 2026-10-25)
    read -r BATCH GAME <<EOF
$(others)
EOF
    BATCH=${BATCH:-0}; GAME=${GAME:-0}
    if [ "$BATCH" != 0 ] || [ "$GAME" != 0 ]; then
        WHO=""
        [ "$BATCH" = 0 ] || WHO="$BATCH other batch JVM(s)"
        [ "$GAME" = 0 ] || WHO="${WHO:+$WHO and }the game"
        echo "timing    soft - $WHO running: a timing miss says SOFT and is not counted"
    fi
fi

# --- the suite -------------------------------------------------------------
if [ "$FAST" = 0 ] && [ "$RUNSUITE" = 1 ]; then
    touch "$OUT/.started"   # the logs this run writes are newer; an earlier run's are not read
    record_reds > "$OUT/known-reds.txt"   # AllChecks tags its red lines against it (FW7); UTF-8 so a non-ASCII label reads the same to Java and to awk
    rm -f "$OUT/timing.tsv"   # the figures of this run alone (TM3)
    java $TIMED -Dcbs.timing.tsv="$OUT/timing.tsv" -Dcbs.knownReds="$OUT/known-reds.txt" -Dstdout.encoding=UTF-8 -cp "$CP" ham.citybuildersim.AllChecks -f "$@" 2>&1 | quiet | tr -d '\r' > "$SUITE"
    suite_verdict
    grep ' SOFT, not counted ' "$SUITE" | sed 's/^/timing    /'
    # what did not run here: the Earth harnesses' full-file sections, BuildMenuCheck without JavaFX
    find "$OUT" -name 'Earth*Check.log' -newer "$OUT/.started" -exec grep -l '^earth-2.bin not named' {} + 2>/dev/null | grep -q . \
        && echo "    note: no earth-2.bin here, so the Earth harnesses' full-file sections did not run"
    grep 'skipped (needs JavaFX' "$SUITE" | sed 's/^/    note: /'
fi
[ "$TIMING" = 0 ] || finish   # --timing: the timing harnesses and nothing more

# --- the maps ----------------------------------------------------------------
if java -cp target/classes ham.citybuildersim.tools.Maps > "$OUT/maps.txt" 2>&1; then
    echo "maps      regenerated"
else
    FAILED=1
    echo "maps      FAILED - $OUT/maps.txt"
    quiet < "$OUT/maps.txt" | tail -5 | sed 's/^/    /'
fi

# --- Stale -------------------------------------------------------------------
java -cp target/classes ham.citybuildersim.tools.Stale 2>&1 | quiet > "$OUT/stale.txt"
STALE=$(grep -E '^[0-9]+ firm finding' "$OUT/stale.txt" | sed 's/\..*//')
case "$STALE" in
    "0 firm findings"*) echo "stale     $STALE - report: $OUT/stale.txt" ;;
    *) FAILED=1; echo "stale     ${STALE:-FAILED} - report: $OUT/stale.txt" ;;
esac

# --- the tree ----------------------------------------------------------------
CHANGED=$(git status --porcelain | wc -l)
MAPS=$(git status --porcelain -- docs/map docs/dials.md docs/month-order.md docs/harnesses.md | wc -l)
echo "tree      $CHANGED changed ($MAPS of them the generated docs)"
git status --porcelain | grep -v -E ' docs/(map/|dials\.md|month-order\.md|harnesses\.md)' | head -25 | sed 's/^/    /'
CRLF=$(git ls-files --eol | grep 'w/crlf' | grep -v -i '\.bat$\|\.cmd$' | wc -l)
if [ "$CRLF" != 0 ]; then
    FAILED=1
    echo "endings   $CRLF file(s) CRLF in the tree, LF wanted:"
    git ls-files --eol | grep 'w/crlf' | grep -v -i '\.bat$\|\.cmd$' | head -10 | sed 's/^.*\t/    /'
fi

finish
