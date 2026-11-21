#!/bin/bash
# The monetary ensemble (EN1), on a snapshot of target/classes.
# tools/ensemble/ensemble.sh <label> runs or resumes; --stop <label> ends that run.
# Do not edit while running. Needs compiled classes and pandas. README.md holds
# settings, outputs and cost. Logs and CSVs stay under $ENSEMBLE_OUT/<label>/.
# A named gate box runs the pushed commit instead (tools/gate-box.sh --ensemble: ENS_PART=runs there, no pandas,
# ENS_PART=score here on its copied runs; GB3). Exit 0 hard criteria pass, 1 build/compile failure, 2 scoring/
# dependency error, 3 missing runs, 4 hard criteria failed (EI1), 5 the box's runs yielded to a gate or the nightly
# (GB3). Scores are kept for exits 0/3/4.
# WHY (EN1): a runner outside the tree left scores later versions could not compare.
set -u

SELF="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"

case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) WINDOWS=1; SEP=';' ;;
    *) WINDOWS=0; SEP=':' ;;
esac
if [ "$WINDOWS" = 1 ]; then pwdn() { pwd -W; }; else pwdn() { pwd; }; fi
# a file or folder as Java and Python want it: absolute, and on Windows C:/... rather than /c/...
native() {
    if [ -d "$1" ]; then (cd "$1" && pwdn); else echo "$(cd "$(dirname "$1")" && pwdn)/$(basename "$1")"; fi
}

# --- stop a run: tools/ensemble/ensemble.sh --stop <label> ----------------------
# A task manager that stops the script's shell leaves xargs and the JVMs running; this stops the run's
# own shell, xargs, the runs' shells and their JVMs, found by the run's folder in their command lines,
# so another run (or the suite) on the machine is left alone. Resume with the same command as before.
if [ "${1:-}" = "--stop" ]; then
    LABEL=${2:?usage: tools/ensemble/ensemble.sh --stop <label>}
    cd "$(dirname "$SELF")/../.." || exit 1
    RUN="${ENSEMBLE_OUT:-target/ensemble}/$LABEL"
    [ -d "$RUN" ] || { echo "no such run: $RUN" >&2; exit 1; }
    RUN=$(cd "$RUN" && pwd); RUNW=$(native "$RUN")
    if [ "$WINDOWS" = 1 ]; then
        ENS_LABEL="$LABEL" ENS_B="$RUNW" \
            powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$(native tools/ensemble/stop.ps1)" | tr -d '\r'
        exit "${PIPESTATUS[0]}"   # 1: what outlived the stop is named above (HN1-C)
    else
        pkill -f -- "ensemble.sh $LABEL( |\$)"
        pkill -f -- "$RUN( |/|\$)"
        sleep 1
        pkill -f -- "$RUN( |/|\$)"
        echo "stop sent to run $LABEL"
    fi
    exit 0
fi

# --- one run: what xargs calls (the script itself, hidden mode) --------------
if [ "${1:-}" = "--one" ]; then
    RUN=$2; P=$3; S=$4
    [ -z "${ENS_YIELD:-}" ] || [ ! -e "$ENS_YIELD" ] || exit 0   # GB3: a gate or the nightly came; this run waits for the next pass
    DATA_ARGS=()
    [ -z "${ENS_EARTH:-}" ] || DATA_ARGS+=("-Dcbs.earth=$ENS_EARTH")
    [ -z "${ENS_WORLD:-}" ] || DATA_ARGS+=("-Dcbs.world=$ENS_WORLD")
    rm -f "$RUN/times/$P-$S.txt"
    LOCKCMD=""
    [ -n "${ENS_LOCK:-}" ] && LOCKCMD="flock $ENS_LOCK"
    # shellcheck disable=SC2086
    $LOCKCMD java -Xmx2g "-Djava.io.tmpdir=$ENS_RUNW/tmp" "${DATA_ARGS[@]}" ${ENS_JAVA_OPTS:-} \
        -cp "$ENS_RUNW/driver$ENS_SEP$ENS_RUNW/classes$ENS_SEP$ENS_DEPS" \
        ham.citybuildersim.Ensemble "$P" "$S" "$ENS_RUNW/$P-$S.csv" > "$RUN/logs/$P-$S.log" 2>&1
    # DONE <policy> seed <s> months <m> of <end> in <secs>s ...
    LINE=$(tr -d '\r' < "$RUN/logs/$P-$S.log" | grep -E '^DONE' | tail -1)
    if [ -n "$LINE" ]; then
        echo "$LINE" | awk '{sub(/s$/, "", $10); print $2, $4, $6, $10}' > "$RUN/times/$P-$S.txt"
        echo "$LINE" >> "$RUN/progress.txt"
    else
        echo "NO DONE $P $S" >> "$RUN/progress.txt"
    fi
    exit 0
fi

LABEL=${1:-}
case "$LABEL" in
    ""|-*|*/*|*\\*|*" "*) echo "usage: tools/ensemble/ensemble.sh <label>   (a plain name, e.g. the batch's id)" >&2; exit 1 ;;
esac

ORIGCWD=$PWD
cd "$(dirname "$SELF")/../.." || exit 1
REPO=$PWD

# --- the settings --------------------------------------------------------------
LOCK="${CBS_JVM_LOCK:-/home/claude/jvm.lock}"
LOCKED=""
if [ -z "${CBS_LOCKED:-}" ] && command -v flock >/dev/null 2>&1 && [ -d "$(dirname "$LOCK")" ]; then
    LOCKED=$LOCK      # the cloud workbench: two cores, one JVM at a time, as batch-check.sh
fi
JOBS=${JOBS:-$([ -n "$LOCKED" ] && echo 1 || echo 5)}
SEEDS=${SEEDS:-$(seq 0 15)}
POLICIES=${POLICIES:-"AUTO2 ZERO_SLACK ZERO_HOT SHOCK HIGH"}
RESUME=${RESUME:-1}
JAVA_OPTS=${JAVA_OPTS:-}
ENS_EARTH=; ENS_WORLD=; DATA_SHOWN=
# WHY (EN2): a worktree has no data folder, so the old ensemble scored a generated world.
if [ -z "${JDK_JAVA_OPTIONS:-}" ]; then
    MAIN=$(git worktree list --porcelain 2>/dev/null | sed -n '1s/^worktree //p')
    [ ! -f "$MAIN/data/earth-2.bin" ] || ENS_EARTH=$(native "$MAIN/data/earth-2.bin")
    [ ! -f "$MAIN/data/world-1.bin" ] || ENS_WORLD=$(native "$MAIN/data/world-1.bin")
    [ -z "$ENS_EARTH" ] || DATA_SHOWN="-Dcbs.earth=$ENS_EARTH"
    [ -z "$ENS_WORLD" ] || DATA_SHOWN="${ENS_EARTH:+$DATA_SHOWN }-Dcbs.world=$ENS_WORLD"
fi
case "$JAVA_OPTS ${JDK_JAVA_OPTIONS:-}" in
    *-Dcbs.earth=*|*-Dcbs.world=*) DATA_SHOWN="${DATA_SHOWN:+$DATA_SHOWN; }JAVA_OPTS=$JAVA_OPTS; JDK_JAVA_OPTIONS=${JDK_JAVA_OPTIONS:-}" ;;
esac
DATA_SHOWN=${DATA_SHOWN:-none}
ENSEMBLE_OUT=${ENSEMBLE_OUT:-target/ensemble}
PYLIB=${ENSEMBLE_PYLIB:-$HOME/.cache/cbs-ensemble/py}
if [ -z "${PYTHON:-}" ]; then
    if python3 -c 'import sys' >/dev/null 2>&1; then PYTHON=python3; else PYTHON=python; fi
fi
PIPLINE='python -m pip install --target "$HOME/.cache/cbs-ensemble/py" pandas'

command -v java >/dev/null 2>&1 && command -v javac >/dev/null 2>&1 || { echo "java and javac are not on the PATH" >&2; exit 1; }

RUN="$ENSEMBLE_OUT/$LABEL"
mkdir -p "$RUN/logs" "$RUN/times" "$RUN/tmp" || exit 1
RUN=$(cd "$RUN" && pwd)
RUNW=$(native "$RUN")

# pandas now, not after twenty minutes of runs
if [ "${ENS_PART:-}" != runs ] && ! ENSEMBLE_PYLIB="$PYLIB" "$PYTHON" -I -c "import os, sys; p = os.environ['ENSEMBLE_PYLIB']; os.path.isdir(p) and sys.path.insert(0, p); import pandas" > "$RUN/logs/pandas.txt" 2>&1; then
    echo "python ($PYTHON) cannot import pandas - $PIPLINE" >&2
    exit 2
fi

# --- the baseline: a summary.json or a docs/ensemble/<version>.md ------------------
BASELINE=${BASELINE-}
if [ -z "$BASELINE" ]; then
    NEWEST=$(ls docs/ensemble 2>/dev/null | grep -E '^[0-9][0-9.]*\.md$' | sort -V | tail -1)
    [ -n "$NEWEST" ] && BASELINE="docs/ensemble/$NEWEST" || BASELINE=none
fi
if [ "$BASELINE" != none ]; then
    case "$BASELINE" in
        /*|[A-Za-z]:*) ;;
        *) if [ -f "$ORIGCWD/$BASELINE" ]; then BASELINE="$ORIGCWD/$BASELINE"; fi ;;
    esac
    [ -f "$BASELINE" ] || { echo "baseline not found: $BASELINE" >&2; exit 1; }
    BASELINE_SHOWN=$BASELINE
    case "$BASELINE_SHOWN" in "$REPO"/*) BASELINE_SHOWN=${BASELINE_SHOWN#"$REPO"/} ;; esac
    BASELINEW=$(native "$BASELINE")
else
    BASELINE_SHOWN=""
    BASELINEW=""
fi
[ -n "${CBS_GATE_ON_BOX:-}${ENS_PART:-}" ] || [ -z "${CBS_GATE_HOST-$(cat "$HOME/.cbs-gate-box" 2>/dev/null)}" ] || exec sh tools/gate-box.sh --ensemble "$LABEL"

# --- the snapshot: the classes the runs use, copied once ------------------------------
if [ -d "$RUN/classes" ] || [ "${ENS_PART:-}" = score ]; then   # score: the box's runs, copied here without their classes
    KEPT=1
else
    KEPT=0
    [ -d target/classes/ham/citybuildersim ] || { echo "build first: target/classes/ham/citybuildersim is missing (mvn -q compile)" >&2; exit 1; }
    rm -rf "$RUN/classes.part"
    cp -r target/classes "$RUN/classes.part" && mv "$RUN/classes.part" "$RUN/classes" || { echo "could not copy target/classes" >&2; exit 1; }
    if [ -n "${ENSEMBLE_DEPS:-}" ]; then      # a classpath file of the caller's, instead of Maven's
        [ -s "$ENSEMBLE_DEPS" ] || { echo "ENSEMBLE_DEPS is empty or missing: $ENSEMBLE_DEPS" >&2; exit 1; }
        cp "$ENSEMBLE_DEPS" "$RUN/deps.txt"
    else
        # the dependency classpath (gson and JavaFX), asked of Maven once and again when the pom changes
        if [ ! -s target/classpath.txt ] || [ pom.xml -nt target/classpath.txt ]; then
            mvn -q -B dependency:build-classpath -Dmdep.outputFile=target/classpath.txt > "$RUN/logs/classpath.txt" 2>&1 \
                || { echo "classpath FAILED - $RUNW/logs/classpath.txt" >&2; exit 1; }
        fi
        cp target/classpath.txt "$RUN/deps.txt"
    fi
    VERSION=$(sed -n 's/.*VERSION = "\([^"]*\)".*/\1/p' src/main/java/ham/citybuildersim/GameVersion.java | head -1)
    COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo unknown)
    if [ -n "$(git status --porcelain -- src 2>/dev/null)" ]; then COMMIT="$COMMIT+dirty"; fi
    {
        echo "version: ${VERSION:-unknown}"
        echo "commit: $COMMIT"
        echo "date: $(date -u +%Y-%m-%dT%H:%MZ)"
        echo "label: $LABEL"
        echo "data: $DATA_SHOWN"
        echo "host: $(hostname)"
        echo "java: $(java -version 2>&1 | head -1 | tr -d '\r')"
    } > "$RUN/snapshot.txt"
fi
snap() { sed -n "s/^$1: //p" "$RUN/snapshot.txt" 2>/dev/null | head -1 | tr -d '\r'; }
VERSION=$(snap version); COMMIT=$(snap commit); SNAPDATE=$(snap date)
VERSION=${VERSION:-unknown}; COMMIT=${COMMIT:-unknown}; SNAPDATE=${SNAPDATE:-unknown}
DEPS=$(tr -d '\r\n' < "$RUN/deps.txt")

# --- the driver, compiled against the snapshot ----------------------------------------------
mkdir -p "$RUN/driver"
if [ "${ENS_PART:-}" != score ] && ! javac -nowarn -d "$RUNW/driver" -cp "$RUNW/classes$SEP$DEPS" tools/ensemble/Ensemble.java > "$RUN/logs/javac.txt" 2>&1; then
    echo "driver compile FAILED - $RUNW/logs/javac.txt" >&2
    exit 1
fi

# --- the runs --------------------------------------------------------------------------------------
: > "$RUN/jobs.txt"
TOTAL=0
for P in $POLICIES; do
    for S in $SEEDS; do
        TOTAL=$((TOTAL + 1))
        if [ "$RESUME" = 1 ] && [ -f "$RUN/$P-$S.csv" ] && [ -f "$RUN/times/$P-$S.txt" ]; then continue; fi
        echo "$P $S" >> "$RUN/jobs.txt"
    done
done
[ "${ENS_PART:-}" != score ] || : > "$RUN/jobs.txt"
TODO=$(wc -l < "$RUN/jobs.txt" | tr -d ' ')
SNAPDATA=$(snap data)
if [ "$KEPT" = 1 ] && [ "$TODO" -gt 0 ] && [ "$SNAPDATA" != "$DATA_SHOWN" ]; then
    echo "data settings differ from the snapshot; use a fresh label for these settings" >&2
    exit 1
fi
SNAPNOTE=""; [ "$KEPT" = 1 ] && SNAPNOTE=", snapshot kept from the first start"
[ "${ENS_PART:-}" != score ] || SNAPNOTE=", run on $(snap host), scored here"
AT="$JOBS at once, "; [ "${ENS_PART:-}" != score ] || AT=   # the home half plays nothing
echo "ensemble $LABEL: classes of $COMMIT (v$VERSION)$SNAPNOTE; $AT$TODO of $TOTAL runs to do"

START=$(date +%s)
if [ "$TODO" -gt 0 ]; then
    export ENS_RUNW="$RUNW" ENS_SEP="$SEP" ENS_DEPS="$DEPS" ENS_JAVA_OPTS="$JAVA_OPTS" ENS_LOCK="$LOCKED" ENS_EARTH ENS_WORLD
    xargs -P "$JOBS" -n 2 bash "$SELF" --one "$RUN" < "$RUN/jobs.txt"
fi
WALL=$(( $(date +%s) - START ))

# --- what finished --------------------------------------------------------------------------------------
DONE=0; MISSING=0
for P in $POLICIES; do
    for S in $SEEDS; do
        if [ -f "$RUN/$P-$S.csv" ] && [ -f "$RUN/times/$P-$S.txt" ]; then DONE=$((DONE + 1)); else MISSING=$((MISSING + 1)); fi
    done
done
WALLMIN=$(awk -v s="$WALL" 'BEGIN { printf "%.1f", s / 60 }')
if [ "$MISSING" -gt 0 ]; then
    echo "runs: $DONE of $TOTAL done, $MISSING missing (progress.txt and logs/ beside score.md say why); $WALLMIN min"
else
    echo "runs: $DONE of $TOTAL done, none missing; $WALLMIN min"
fi
if [ "${ENS_PART:-}" = runs ]; then   # GB3, the box: the runs alone, 5 when a gate or the nightly came before they were done
    [ "$MISSING" -gt 0 ] || exit 0
    [ -z "${ENS_YIELD:-}" ] || [ ! -e "$ENS_YIELD" ] || exit 5
    exit 3
fi

# --- the score -----------------------------------------------------------------------------------------------
cat "$RUN"/times/*.txt > "$RUN/times.txt" 2>/dev/null
[ -s "$RUN/times.txt" ] || rm -f "$RUN/times.txt"
BASE_ARGS=()
[ -z "$BASELINEW" ] || BASE_ARGS=(--baseline "$BASELINEW")
ENSEMBLE_PYLIB="$PYLIB" "$PYTHON" -I tools/ensemble/score.py "$RUNW" "${BASE_ARGS[@]}" --summary "$RUNW/summary.json" > "$RUN/score.md" 2> "$RUN/logs/score.txt"
SCORED=$?
case "$SCORED" in
0|4) ;;
*)
    echo "scoring FAILED - $RUNW/logs/score.txt: $(tail -n 1 "$RUN/logs/score.txt" | tr -d '\r')" >&2
    ls "$RUN"/AUTO2-*.csv > /dev/null 2>&1 || echo "no AUTO2 run here: score.py reads every other policy against its AUTO2 twin, so it needs them" >&2
    echo "if pandas is the cause: $PIPLINE" >&2
    exit 2
;;
esac
tr -d '\r' < "$RUN/score.md" | grep -E '^(criteria passed|batch verdict|changed verdict)'

# baseline.md, in the form docs/ensemble/<version>.md keeps: heading, a paragraph, the table, summary.json
FENCE='```'
{
    printf '# Ensemble baseline: %s\n\n' "$VERSION"
    printf 'The ensemble on the model of **%s**: classes snapshot of commit %s (%s), label %s, %s of %s runs.\n' \
        "$VERSION" "$COMMIT" "$SNAPDATE" "$LABEL" "$DONE" "$TOTAL"
    printf 'Data files named for this snapshot — `%s`.\n' "${SNAPDATA:-not recorded (legacy snapshot)}"
    [ -z "$(snap host)" ] || printf 'Run on %s, %s.\n' "$(snap host)" "$(snap java)"
    if [ -n "$BASELINE_SHOWN" ]; then
        printf 'Scored against `%s` for T1, T2 and F1. ' "$BASELINE_SHOWN"
    else
        printf 'Scored with no baseline: T1, T2 and F1 report this run only. '
    fi
    printf '\n'
    tr -d '\r' < "$RUN/score.md" | sed '1d' | sed '/./,$!d'
    printf '\n## summary.json\n\n'
    printf 'What `--baseline` reads: T1, T2 and F1 compare with it.\n\n'
    printf '%sjson\n' "$FENCE"
    tr -d '\r' < "$RUN/summary.json"
    printf '\n%s\n' "$FENCE"
} > "$RUN/baseline.md"
echo "score: $RUNW/score.md; baseline: $RUNW/baseline.md"

[ "$SCORED" = 4 ] && exit 4
[ "$MISSING" -gt 0 ] && exit 3
exit 0
