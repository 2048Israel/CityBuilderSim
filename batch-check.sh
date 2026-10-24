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
#
#  Not here: the 80-run ensemble, tools/ensemble/ensemble.sh, run only for a
#  batch whose playtest trace moves (EN1, 2026-10-20; tools/ensemble/README.md).
#
#  WHY (2026-10-13): a session paid for every line it read, and a batch's
#  checks were a dozen calls - each re-reading the whole conversation - and
#  their output: a line for every harness, a red one run again in full to find
#  its assertion, Stale's report whole. This prints about ten lines when all is
#  well and the failing assertions when it is not (AllChecks -f).
#
#  Exit status 0 only when it compiled, every harness run passed and Stale has
#  no firm finding. On the cloud workbench (2 cores) one JVM at a time: under
#  the lock at $CBS_JVM_LOCK (default /home/claude/jvm.lock) when its folder
#  exists and flock is installed. The PC's twin is Batch check.bat.
#
#  Timing (TM1, 2026-10-18): when another batch's JVM or Jerus's game runs
#  as the suite starts, the suite times soft (-Dcbs.timing=soft): a timing
#  figure over its budget says SOFT on its line and is not counted. On the PC
#  tools/jvms.ps1 counts them; elsewhere ps does. --timing is always hard: it
#  waits (up to 30 minutes) for the other batches' JVMs to finish, and says
#  so when the game is running, whose load a timing red may then be.
#  In Git Bash the classpath is joined with ';', as Java on Windows wants.
# ---------------------------------------------------------------------------

cd "$(dirname "$0")" || exit 1

LOCK="${CBS_JVM_LOCK:-/home/claude/jvm.lock}"
if [ -z "$CBS_LOCKED" ] && command -v flock >/dev/null 2>&1 && [ -d "$(dirname "$LOCK")" ]; then
    export CBS_LOCKED=1
    exec flock "$LOCK" "$0" "$@"
fi

FAST=0; TIMING=0
case "$1" in
    --fast) FAST=1; shift ;;
    --timing) TIMING=1; shift ;;
esac
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) WINDOWS=1; SEP=';' ;;
    *) WINDOWS=0; SEP=':' ;;
esac

OUT=target/checks
mkdir -p "$OUT"
FAILED=0
quiet() { grep -v '^Picked up JAVA_TOOL_OPTIONS'; }   # the cloud's proxy settings, said by every JVM

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

# --- timing: hard, or soft while another batch's JVM or the game runs (TM1) --
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
TIMED=""
if [ "$TIMING" = 1 ]; then
    TIMED="-Dcbs.timing=hard"
    set -- --timing "$@"
    waited=0
    while :; do
        read -r BATCH GAME <<EOF
$(others)
EOF
        [ "${BATCH:-0}" = 0 ] && break
        if [ "$waited" -ge 120 ]; then
            echo "timing    still $BATCH other batch JVM(s) after 30 minutes: hard all the same, a red may be their load"
            break
        fi
        [ "$waited" = 0 ] && echo "timing    waiting for $BATCH other batch JVM(s) to finish first..."
        waited=$((waited + 1))
        sleep 15
    done
    [ "${GAME:-0}" = 0 ] || echo "timing    the game is running: a timing red may be its load - close it if you can"
    echo "timing    hard - the timing harnesses alone"
elif [ "$FAST" = 0 ]; then
    read -r BATCH GAME <<EOF
$(others)
EOF
    BATCH=${BATCH:-0}; GAME=${GAME:-0}
    if [ "$BATCH" != 0 ] || [ "$GAME" != 0 ]; then
        TIMED="-Dcbs.timing=soft"
        WHO=""
        [ "$BATCH" = 0 ] || WHO="$BATCH other batch JVM(s)"
        [ "$GAME" = 0 ] || WHO="${WHO:+$WHO and }the game"
        echo "timing    soft - $WHO running: a timing miss says SOFT and is not counted"
    fi
fi

# --- the suite -------------------------------------------------------------
if [ "$FAST" = 0 ]; then
    touch "$OUT/.started"   # the logs this run writes are newer; an earlier run's are not read
    java $TIMED -cp "$CP" ham.citybuildersim.AllChecks -f "$@" 2>&1 | quiet | tr -d '\r' > "$OUT/suite.txt"
    if grep -q ' 0 failed$' "$OUT/suite.txt"; then
        echo "suite     OK - $(grep 'harness.* run in ' "$OUT/suite.txt" | sed 's/:.*//')"
        grep ' SOFT, not counted ' "$OUT/suite.txt" | sed 's/^/timing    /'
    else
        FAILED=1
        echo "suite     FAILED"
        sed '/^$/d' "$OUT/suite.txt" | sed 's/^/    /'
    fi
    # what did not run here: the Earth harnesses' full-file sections, BuildMenuCheck without JavaFX
    find "$OUT" -name 'Earth*Check.log' -newer "$OUT/.started" -exec grep -l '^earth-2.bin not named' {} + 2>/dev/null | grep -q . \
        && echo "    note: no earth-2.bin here, so the Earth harnesses' full-file sections did not run"
    grep 'skipped (needs JavaFX' "$OUT/suite.txt" | sed 's/^/    note: /'
fi
[ "$TIMING" = 0 ] || exit $FAILED   # --timing: the timing harnesses and nothing more

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

exit $FAILED
