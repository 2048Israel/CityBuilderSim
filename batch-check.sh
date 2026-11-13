#!/bin/sh
# Compile, selected suite, maps and Stale. --fast skips the suite; --pick selects it.
# --timing is the hard nightly; --judge LOG uses the common judge without building.
# Exit 0 green, 3 known reds, 4 NEW reds, 1 incomplete/build failure, 2 usage.
# CBS_KNOWN_REDS overrides the combined record for --judge; CBS_REJUDGE ignores old tags.
# WHY (GR2): builders and gates need one verdict and the same repository approvals.

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
    MINGW*|MSYS*|CYGWIN*) SEP=';' ;;
    *) SEP=':' ;;
esac

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

# Read the fork's gate record. WHY (BP1) a later main fix otherwise made an older
# batch's existing red NEW. Repository parts are added by record_reds below.
recorded_reds() {
    for r in origin/main main; do
        mb=$(git merge-base HEAD "$r" 2>/dev/null) || continue
        if git cat-file -e "$mb:docs/notes/gate.txt" 2>/dev/null; then git show "$mb:docs/notes/gate.txt"; return 0; fi
    done
    [ -f docs/notes/gate.txt ] && cat docs/notes/gate.txt
    return 0
}

# GR2: the runner, saved-log verdict and publisher call the same Java judge.
record_reds() {
    if [ -n "$CBS_KNOWN_REDS" ]; then
        cat "$CBS_KNOWN_REDS"
    else
        recorded_reds
        [ ! -f docs/notes/known-reds.txt ] || cat docs/notes/known-reds.txt
    fi
}
suite_verdict() {
    java src/main/java/ham/citybuildersim/KnownReds.java "$SUITE" "$OUT/known-reds.txt" "$OUT" $CBS_REJUDGE
    SUITE_EXIT=$?
    case "$SUITE_EXIT" in 0|3|4) ;; *) FAILED=1 ;; esac
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
            ALL)  NIGHTLY_PICKED=$(sh tools/pick.sh --nightly) || { FAILED=1; RUNSUITE=0; }
                  set -- --gate $NIGHTLY_PICKED ;;   # nightly tier only by its direct rules (HA1)
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
    TIMED="-Dcbs.timing=soft"   # GR1: only --timing judges speed, nightly on main
fi

# --- the suite -------------------------------------------------------------
if [ "$FAST" = 0 ] && [ "$RUNSUITE" = 1 ] && [ "$#" = 0 ]; then
    # HA1's own gate may still be launched by the preceding publisher.
    if [ "${CBS_GATE_NIGHTLY+x}" != x ]; then
        CBS_GATE_NIGHTLY=$(sh tools/pick.sh --nightly) || { FAILED=1; RUNSUITE=0; }
    fi
    set -- --gate $CBS_GATE_NIGHTLY
fi
if [ "$FAST" = 0 ] && [ "$RUNSUITE" = 1 ]; then
    touch "$OUT/.started"   # the logs this run writes are newer; an earlier run's are not read
    record_reds > "$OUT/known-reds.txt"   # AllChecks tags its red lines against it (FW7); UTF-8 so a non-ASCII label reads the same to Java and to awk
    rm -f "$OUT/timing.tsv"   # the figures of this run alone (TM3)
    # -Xms64m: the runner holds little; its default initial heap was 616 MB of commit (SP1)
    java -Xms64m $TIMED -Dcbs.timing.tsv="$OUT/timing.tsv" -Dcbs.knownReds="$OUT/known-reds.txt" -Dstdout.encoding=UTF-8 -cp "$CP" ham.citybuildersim.AllChecks -f "$@" 2>&1 | quiet | tr -d '\r' > "$SUITE"
    suite_verdict
    grep ' SOFT, not counted ' "$SUITE" | sed 's/^/timing    /'
    grep '^memory: ' "$SUITE" | sed 's/^memory: /memory    /'   # the starts the commit charge held (SP1)
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
