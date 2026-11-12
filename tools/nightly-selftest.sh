#!/bin/sh
# GR1: made-up figures and five merges, no JVM or network.
set -eu
ROOT=$(cd "$(dirname "$0")/.." && pwd)
. "$ROOT/tools/nightly-timing.sh"
PAIRAWK=$ROOT/tools/timing-pair.awk
TEST=$(mktemp -d "${TMPDIR:-/tmp}/cbs-night-test-XXXXXXXX")
trap 'rm -rf "$TEST"' EXIT
ok() { if "$@"; then echo "PASS $CASE"; else echo "FAIL $CASE"; exit 1; fi; }
row() { printf 'PlanCheck\tplan 42 ms\t%s\t100\tms\tFAIL\t1\n' "$1"; }
judge() { awk -v mode=night -f "$PAIRAWK" "$TEST/old" "$TEST/new"; }
row 120 > "$TEST/old"; row 125 > "$TEST/new"
CASE='standing miss is named'; ok test "$(judge | cut -f1)" = STANDING
row 90 > "$TEST/old"
CASE='new miss is a candidate'; ok test "$(judge | cut -f1)" = SLOWER
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
HOME=$TEST/home; mkdir "$HOME"; ORIG=$ROOT
timing_run() { mkdir -p "$2"; row 125 > "$2/timing.tsv"; }
timing_quiet() { return 0; }
sleep() { :; }
CBS_TIMING_WAIT_SECONDS=60
CASE='quiet first night becomes this machine baseline'; nightly_timing > "$TEST/quiet.log"
BASELINE=$(cat "$STORE/baseline"); ok test "$(cat "$BASELINE/loaded")" = 0
CBS_TIMING_WAIT_SECONDS=0
CASE='loaded night retains figures and leaves quiet baseline'; nightly_timing > "$TEST/loaded.log"
ok test "$(cat "$STORE/baseline")" = "$BASELINE"
CASE='loaded metadata and verdict are retained'; ok test "$(cat "$NIGHT/loaded")" = 1
CASE='machine data is isolated'; ok test "$STORE" = "$HOME/.cbs-timing/$(hostname | tr -d '\r\n' | tr -c 'A-Za-z0-9_.-' '_')"
echo 'nightly selftest OK'
