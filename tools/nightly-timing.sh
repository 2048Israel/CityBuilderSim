#!/bin/sh
# GR1: timing away from the gate, retained per machine, confirmed before attribution.
# Sourced by nightly.sh and its no-JVM self-test. All temporary builds stay in its clone.
timing_quiet() {
    case "$(uname -s)" in
        MINGW*|MSYS*|CYGWIN*)
            powershell.exe -NoProfile -Command '$cpu = (Get-CimInstance Win32_Processor | Measure-Object LoadPercentage -Average).Average; if ($null -eq $cpu -or $cpu -gt 20 -or (Get-Process java,javaw -ErrorAction SilentlyContinue)) { exit 1 }' >/dev/null 2>&1 ;;
        *) ! pgrep -x 'java|javaw' >/dev/null 2>&1 && awk -v n="$(getconf _NPROCESSORS_ONLN)" '{exit !($1 < n * 0.2)}' /proc/loadavg ;;
    esac
}
timing_run() {  # commit, output directory, optional harness
    git -C "$CLONE" checkout -q --detach --force "$1" || return 1
    mkdir -p "$2" || return 1
    ( cd "$CLONE" && mvn -q -B clean ) > "$2/clean.log" 2>&1 || return 1
    ( cd "$CLONE" && ./batch-check.sh --timing ${3:+"$3"} ) > "$2/check.log" 2>&1
    code=$?
    case "$code" in 0|3|4) ;; *) return 1 ;; esac
    grep -qE '^[1-9][0-9]* harness(es)? run in ' "$CLONE/target/checks/suite.txt" &&
        [ -s "$CLONE/target/checks/timing.tsv" ] && cp "$CLONE/target/checks/timing.tsv" "$2/timing.tsv"
}
timing_pair() {  # candidate commit, assertion fields H/L/P/O inherited from the verdict
    PD=$(mktemp -d "$NIGHT/pair-XXXXXXXX") || return 2
    : > "$PD/pairs.tsv"
    round=1
    while [ "$round" -le "$ROUNDS" ]; do
        order='main batch'; [ $((round % 2)) = 1 ] || order='batch main'
        for side in $order; do
            ref=$PREV; [ "$side" = main ] || ref=$1
            timing_run "$ref" "$PD/$round-$side" "$H" || { echo "INCONCLUSIVE: $H at $ref"; return 2; }
            awk -v s="$side" -v r="$round" 'BEGIN {OFS="\t"} {print s,r,$0}' "$PD/$round-$side/timing.tsv" >> "$PD/pairs.tsv"
        done
        round=$((round + 1))
    done
    awk -v mode=night-pair -v margin="$MARGIN" -v wantedh="$H" -v wantedl="$L" -v wantedp="$P" -v wantedo="$O" -f "$PAIRAWK" "$PD/pairs.tsv"
}
timing_search() {  # first-parent merges, binary search against the preceding quiet commit
    git -C "$CLONE" rev-list --first-parent --merges --reverse "$PREV..$FULL" > "$NIGHT/merges"
    low=1; high=$(wc -l < "$NIGHT/merges")
    [ "$high" -gt 0 ] || { echo "attribution unavailable: no first-parent merge between $PREV and $FULL"; return 1; }
    while [ "$low" -lt "$high" ]; do
        mid=$(( (low + high) / 2 )); candidate=$(sed -n "${mid}p" "$NIGHT/merges")
        result=$(timing_pair "$candidate") || { echo "$result"; return 1; }
        printf 'search %s %s\n' "$candidate" "$result" >> "$NIGHT/search.log"
        case "$result" in SLOWER*) high=$mid ;; STEADY*) low=$((mid + 1)) ;; *) return 1 ;; esac
    done
    culprit=$(sed -n "${low}p" "$NIGHT/merges")
    result=$(timing_pair "$culprit") || return 1
    case "$result" in SLOWER*) ;; *) echo 'attribution inconclusive: boundary did not confirm'; return 1 ;; esac
    result=$(timing_pair "$(git -C "$CLONE" rev-parse "$culprit^1")") || return 1
    case "$result" in STEADY*) ;; *) echo 'attribution inconclusive: preceding commit already slower'; return 1 ;; esac
    subject=$(git -C "$CLONE" log -1 --format=%s "$culprit")
    batch=$(printf '%s\n' "$subject" | sed -n 's/^Merge batch\/\([^: ,]*\).*/\1/p')
    version=$(git -C "$CLONE" show "$culprit:src/main/java/ham/citybuildersim/GameVersion.java" | sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p')
    echo "${batch:-unknown batch} $version $culprit"
}
nightly_timing() {
    MACHINE=$(hostname | tr -d '\r\n' | tr -c 'A-Za-z0-9_.-' '_')
    STORE=$HOME/.cbs-timing/$MACHINE
    mkdir -p "$STORE" || return 1
    NIGHT=$(mktemp -d "$STORE/$(date +%Y-%m-%d)-$SHA-XXXXXXXX") || return 1
    PAIRAWK=$ORIG/tools/timing-pair.awk
    MARGIN=${CBS_TIMING_MARGIN:-$(awk -v mode=dial -f "$PAIRAWK" /dev/null)}
    ROUNDS=${CBS_TIMING_ROUNDS:-3}; WAIT=${CBS_TIMING_WAIT_SECONDS:-1800}
    case "$ROUNDS" in ''|*[!0-9]*|0|1) echo 'timing FAILED: CBS_TIMING_ROUNDS must be at least 2'; return 1 ;; esac
    case "$WAIT" in ''|*[!0-9]*) echo 'timing FAILED: CBS_TIMING_WAIT_SECONDS must be nonnegative'; return 1 ;; esac
    LOADED=1; looks=0; deadline=$(( $(date +%s) + WAIT ))
    while [ "$(date +%s)" -lt "$deadline" ]; do
        if timing_quiet; then looks=$((looks + 1)); else looks=0; fi
        [ "$looks" -lt 4 ] || { LOADED=0; break; }
        left=$((deadline - $(date +%s))); [ "$left" -gt 0 ] || break
        pause=15; [ "$left" -ge "$pause" ] || pause=$left
        sleep "$pause"
    done
    printf '%s\n' "$FULL" > "$NIGHT/commit"
    echo "$LOADED" > "$NIGHT/loaded"
    echo "timing: $FULL; loaded=$LOADED; figures $NIGHT"
    timing_run "$FULL" "$NIGHT" || { echo 'timing FAILED: hard run incomplete'; return 1; }
    timing_quiet || LOADED=1
    echo "$LOADED" > "$NIGHT/loaded"
    PREVIOUS=$(cat "$STORE/baseline" 2>/dev/null || :); PREV=; OLD=$NIGHT/empty.tsv; : > "$OLD"
    if [ -n "$PREVIOUS" ] && [ "$(cat "$PREVIOUS/loaded" 2>/dev/null)" = 0 ]; then
        PREV=$(cat "$PREVIOUS/commit"); OLD=$PREVIOUS/timing.tsv
        git -C "$CLONE" merge-base --is-ancestor "$PREV" "$FULL" || PREV=
    fi
    [ -n "$PREV" ] || { OLD=$NIGHT/empty.tsv; echo "no figure of last night's here"; }
    awk -v mode=night -v margin="$MARGIN" -f "$PAIRAWK" "$OLD" "$NIGHT/timing.tsv" > "$NIGHT/verdict.tsv"
    slower=0; standing=0; first=0; errors=0; DETAILS=
    TAB=$(printf '\t')
    while IFS="$TAB" read -r state H L before now budget unit P O; do
        echo "$state: $H | $L | previous $before, tonight $now, budget $budget $unit"
        case "$state" in
            SLOWER)
                result=$(timing_pair "$FULL"); paircode=$?
                echo "confirmation $result"
                if [ "$paircode" != 0 ]; then errors=$((errors + 1))
                elif [ "${result%%"$TAB"*}" = SLOWER ]; then
                    culprit=$(timing_search); searchcode=$?
                    echo "SLOWER $H | $L | paired $result | introduced by $culprit"
                    DETAILS="$DETAILS; $H | $L | $(printf '%s' "$result" | awk -F '\t' '{printf "previous %s, tonight %s, budget %s %s", $2, $3, $4, $5}') | $culprit"
                    slower=$((slower + 1)); [ "$searchcode" = 0 ] || errors=$((errors + 1))
                fi ;;
            STANDING) standing=$((standing + 1)) ;;
            FIRST) first=$((first + 1)) ;;
            INVALID) errors=$((errors + 1)) ;;
        esac
    done < "$NIGHT/verdict.tsv"
    # GR1: a loaded night's figures never replace the last quiet baseline.
    if [ "$LOADED" = 0 ] && [ "$errors" = 0 ]; then
        printf '%s\n' "$NIGHT" > "$STORE/baseline.new" && mv "$STORE/baseline.new" "$STORE/baseline" || return 1
    fi
    TIMING="timing: $slower SLOWER, $standing standing miss(es), $first first-night miss(es), $errors incomplete; loaded=$LOADED; $NIGHT$DETAILS"
    [ -n "$PREV" ] || TIMING="$TIMING; no figure of last night's here"
    echo "$TIMING"; [ "$slower" = 0 ] && [ "$errors" = 0 ]
}
