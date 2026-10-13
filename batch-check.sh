#!/bin/sh
# ---------------------------------------------------------------------------
#  A batch's checks in one call, for a session on Linux (a cloud session, the
#  orchestrator's gate, ship.sh): compile, the suite, the maps, Stale and the
#  tree's state, each said in a line or two. Everything long goes to a file
#  under target/checks/ and the line says which.
#
#      ./batch-check.sh               compile, the whole suite, maps, Stale, git status
#      ./batch-check.sh money bank    the same, the suite only the harnesses named so
#      ./batch-check.sh --fast        compile, maps, Stale, git status: no suite
#
#  WHY (2026-10-13): a session paid for every line it read, and a batch's
#  checks were a dozen calls - each re-reading the whole conversation - and
#  their output: a line for every harness, a red one run again in full to find
#  its assertion, Stale's report whole. This prints about ten lines when all is
#  well and the failing assertions when it is not (AllChecks -f).
#
#  Exit status 0 only when it compiled, every harness run passed and Stale has
#  no firm finding. One JVM at a time on the machine: under the lock at
#  $CBS_JVM_LOCK (default /home/claude/jvm.lock, the cloud workbench's) when
#  its folder exists and flock is installed. The PC's twin is Batch check.bat.
# ---------------------------------------------------------------------------

cd "$(dirname "$0")" || exit 1

LOCK="${CBS_JVM_LOCK:-/home/claude/jvm.lock}"
if [ -z "$CBS_LOCKED" ] && command -v flock >/dev/null 2>&1 && [ -d "$(dirname "$LOCK")" ]; then
    export CBS_LOCKED=1
    exec flock "$LOCK" "$0" "$@"
fi

FAST=0
if [ "$1" = "--fast" ]; then FAST=1; shift; fi

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
CP="target/classes:$(cat target/classpath.txt)"

# --- the suite -------------------------------------------------------------
if [ "$FAST" = 0 ]; then
    java -cp "$CP" ham.citybuildersim.AllChecks -f "$@" 2>&1 | quiet > "$OUT/suite.txt"
    if grep -q ' 0 failed$' "$OUT/suite.txt"; then
        echo "suite     OK - $(grep 'harness.* run in ' "$OUT/suite.txt" | sed 's/:.*//')"
    else
        FAILED=1
        echo "suite     FAILED"
        sed '/^$/d' "$OUT/suite.txt" | sed 's/^/    /'
    fi
    # a harness that has nothing to check here says so in one line (the Earth file, JavaFX)
    grep -h -i 'skipped\|not found - \|without the earth' "$OUT"/*Check.log 2>/dev/null | sort -u | head -3 | sed 's/^/    note: /'
fi

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
