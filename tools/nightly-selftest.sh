#!/bin/sh
# GR1: made-up figures and five merges, no JVM or network.
set -eu
ROOT=$(cd "$(dirname "$0")/.." && pwd)
. "$ROOT/tools/nightly-timing.sh"
PAIRAWK=$ROOT/tools/timing-pair.awk
TEST=$(mktemp -d "${TMPDIR:-/tmp}/cbs-night-test-XXXXXXXX")
trap 'rm -rf "$TEST"' EXIT
ok() { if "$@"; then echo "PASS $CASE"; else echo "FAIL $CASE"; exit 1; fi; }
LOG=$TEST/lock.log
CASE='fresh lock records the running process and its start time'; nightly_lock "$TEST/lock"; ok test -d "$LOCK_ENTRY"
printf 'live log\n' >> "$LOG"
CASE='live lock refuses with exit 3'
if (nightly_lock "$TEST/lock"); then exit 1; else ok test "$?" = 3; fi
CASE='refusal preserves the owner and log'; ok grep -q 'live log' "$LOG"; ok test -d "$LOCK_ENTRY"
nightly_unlock
for stale in owner-999999999-gone "owner-$LOCK_PID-wrong-start"; do
    mkdir -p "$TEST/lock/$stale"
    CASE='stale or recycled PID lock is taken over'; nightly_lock "$TEST/lock"
    ok test "$(grep -c '^nightly recovered stale lock ' "$LOG")" = 1
    nightly_unlock
done
mkdir "$TEST/lock"
CASE='legacy empty lock is taken over'; nightly_lock "$TEST/lock"; ok grep -q 'recovered stale lock' "$LOG"; nightly_unlock
(
    MAIN="$TEST/assets with spaces"; mkdir -p "$MAIN/data"; unset JDK_JAVA_OPTIONS
    CASE='absent data files add no properties'; nightly_data; ok test -z "${JDK_JAVA_OPTIONS:-}"
    touch "$MAIN/data/earth-2.bin"; nightly_data
    CASE='only the existing Earth file is named'; ok test "$JDK_JAVA_OPTIONS" = "-Dcbs.earth=\"$MAIN/data/earth-2.bin\""
    touch "$MAIN/data/world-1.bin"; unset JDK_JAVA_OPTIONS; nightly_data
    CASE='both existing files are named'; ok test "$JDK_JAVA_OPTIONS" = "-Dcbs.earth=\"$MAIN/data/earth-2.bin\" -Dcbs.world=\"$MAIN/data/world-1.bin\""
    JDK_JAVA_OPTIONS='-Dfixture=kept'; nightly_data
    CASE='caller Java options take precedence'; ok test "$JDK_JAVA_OPTIONS" = '-Dfixture=kept'
)
row() { printf 'PlanCheck\tplan 42 ms\t%s\t100\tms\tFAIL\t1\n' "$1"; }
judge() { awk -v mode=night -f "$PAIRAWK" "$TEST/old" "$TEST/new"; }
row 120 > "$TEST/old"; row 125 > "$TEST/new"
CASE='standing miss is named'; ok test "$(judge | cut -f1)" = STANDING
row 90 > "$TEST/old"
CASE='new miss is a candidate'; ok test "$(judge | cut -f1)" = SLOWER
row 120 | sed 's/100/130/' > "$TEST/old"
CASE='previous night uses its own budget'; ok test "$(judge | cut -f1)" = SLOWER
row 50 > "$TEST/old"; row 90 > "$TEST/new"
CASE='rise inside budget stays OK'; ok test "$(judge | cut -f1)" = OK
: > "$TEST/old"; row 125 > "$TEST/new"
CASE='first night judges budgets alone'; ok test "$(judge | cut -f1)" = FIRST
row 120 > "$TEST/old"; row 139 > "$TEST/new"
CASE='standing miss beyond margin is a candidate'; ok test "$(judge | cut -f1)" = SLOWER
row 138 > "$TEST/new"
CASE='exact margin stays standing'; ok test "$(judge | cut -f1)" = STANDING
CASE='paired medians reject noise and confirm standing deterioration'
for r in 1 2 3; do
    value=120; [ "$r" != 3 ] || value=900
    row "$value" | awk -v r="$r" 'BEGIN {OFS="\t"} {print "main",r,$0}'
    row 160 | awk -v r="$r" 'BEGIN {OFS="\t"} {print "batch",r,$0}'
done > "$TEST/pairs"
pairjudge() { awk -v mode=night-pair -v wantedh=PlanCheck -v wantedl='plan 42 ms' -v wantedp=1 -v wantedo=1 -f "$PAIRAWK" "$TEST/pairs"; }
ok test "$(pairjudge | cut -f1)" = SLOWER
sed 's/160/125/g' "$TEST/pairs" > "$TEST/steady"; mv "$TEST/steady" "$TEST/pairs"
CASE='paired standing miss inside margin is steady'; ok test "$(pairjudge | cut -f1)" = STEADY
: > "$TEST/pairs"
CASE='missing pairs are inconclusive'; if pairjudge >/dev/null; then exit 1; else echo "PASS $CASE"; fi
(
    NIGHT=$TEST; PREV=old; ROUNDS=3; MARGIN=15; H=PlanCheck; L='plan 42 ms'; P=1; O=1
    timing_run() { mkdir -p "$2"; value=90; [ "$1" = old ] || value=130; row "$value" > "$2/timing.tsv"; echo "$1" >> "$TEST/order"; }
    CASE='production pairs alternate sides and judge the medians'
    ok test "$(timing_pair new | cut -f1)" = SLOWER
    ok test "$(tr '\n' ' ' < "$TEST/order")" = 'old new new old old new '
)

CLONE=$TEST/repo; mkdir "$CLONE"; git -C "$CLONE" init -q
git -C "$CLONE" config core.autocrlf false
git -C "$CLONE" config user.name selftest; git -C "$CLONE" config user.email selftest@example.invalid
mkdir -p "$CLONE/src/main/java/ham/citybuildersim"
echo 'public static final String VERSION = "0.0.0";' > "$CLONE/src/main/java/ham/citybuildersim/GameVersion.java"
git -C "$CLONE" add .; git -C "$CLONE" commit -qm baseline
PREV=$(git -C "$CLONE" rev-parse HEAD); primary=$(git -C "$CLONE" branch --show-current)
for n in 1 2 3 4 5; do
    git -C "$CLONE" checkout -qb "batch/ST$n"
    echo "public static final String VERSION = \"0.0.$n\";" > "$CLONE/src/main/java/ham/citybuildersim/GameVersion.java"
    git -C "$CLONE" commit -qam "batch $n"
    git -C "$CLONE" checkout -q "$primary"
    git -C "$CLONE" merge -q --no-ff "batch/ST$n" -m "Merge batch/ST$n: fixture"
    [ "$n" != 3 ] || EXPECT=$(git -C "$CLONE" rev-parse HEAD)
done
FULL=$(git -C "$CLONE" rev-parse HEAD); SHA=$(git -C "$CLONE" rev-parse --short HEAD)
NIGHT=$TEST/search; mkdir "$NIGHT"; MARGIN=15
timing_pair() {
    if git -C "$CLONE" merge-base --is-ancestor "$EXPECT" "$1"; then printf 'SLOWER\t90\t130\t100\tms\n'
    else printf 'STEADY\t90\t90\t100\tms\n'; fi
}
CASE='halving five merges names the third batch and version'
ok test "$(timing_search)" = "ST3 0.0.3 $EXPECT"

# Exercise retention through the production entry point with stubbed measurements.
TEST_STORE=$TEST/history; ORIG=$ROOT
timing_run() { mkdir -p "$2"; row 125 > "$2/timing.tsv"; }
timing_quiet() { return 0; }
sleep() { :; }
CBS_TIMING_WAIT_SECONDS=60
CASE='quiet first night becomes this machine baseline'; nightly_timing "$TEST_STORE" > "$TEST/quiet.log"
BASELINE=$(cat "$STORE/baseline"); ok test "$(cat "$BASELINE/loaded")" = 0
CBS_TIMING_WAIT_SECONDS=0
CASE='loaded night retains figures and leaves quiet baseline'; nightly_timing "$TEST_STORE" > "$TEST/loaded.log"
ok test "$(cat "$STORE/baseline")" = "$BASELINE"
CASE='loaded metadata and verdict are retained'; ok test "$(cat "$NIGHT/loaded")" = 1
CASE='machine data is isolated'; ok test "$STORE" = "$TEST_STORE/$(hostname | tr -d '\r\n' | tr -c 'A-Za-z0-9_.-' '_')"
# HA1: the production tier entry point, with a no-JVM runner and the five merges above.
cd "$CLONE"
mkdir -p target; printf 'fixture\n' > target/classpath.txt
SEP=';'; LOG=$TEST/tier-report.log; : > "$LOG"
java() { printf '%s\n' "$*" >> "$TEST/tier-command"; return "${TIER_EXIT:-0}"; }
cat > batch-check.sh <<'EOF'
#!/bin/sh
echo "$*" >> "$TIER_JUDGE_LOG"
exit "${TIER_JUDGE_EXIT:-0}"
EOF
chmod +x batch-check.sh
TIER_JUDGE_LOG=$TEST/tier-judge-command; export TIER_JUDGE_LOG TIER_JUDGE_EXIT
CASE='the nightly runs its tier on the fetched main tip'; nightly_tier "$TEST/tier-green"
ok grep -q 'AllChecks -f --nightly' "$TEST/tier-command"
ok grep -q -- '--judge' "$TIER_JUDGE_LOG"
ok test "$(cat "$TEST/tier-green")" = "$FULL"
TIER_EXIT=5; TIER_JUDGE_EXIT=3
CASE='standing known reds are green under the common judge'; nightly_tier "$TEST/tier-green"
ok test "$(cat "$TEST/tier-green")" = "$FULL"
TIER_BASE=$(git rev-list --max-parents=0 "$FULL")
printf '%s\n' "$TIER_BASE" > "$TEST/tier-green"
TIER_EXIT=1; TIER_JUDGE_EXIT=4
CASE='a red tier reports all merges since its last green'
if nightly_tier "$TEST/tier-green"; then exit 1; else ok test "$?" = 4; fi
ok grep -q "merges since $TIER_BASE..$FULL" "$LOG"
for n in 1 2 3 4 5; do ok grep -q "Merge batch/ST$n:" "$LOG"; done
CASE='a red night cannot advance the green commit'; ok test "$(cat "$TEST/tier-green")" = "$TIER_BASE"
TIER_JUDGE_EXIT=1
CASE='an incomplete judged run also preserves the last green'
if nightly_tier "$TEST/tier-green"; then exit 1; else ok test "$?" = 1; fi
ok test "$(cat "$TEST/tier-green")" = "$TIER_BASE"
echo 'nightly selftest OK'
