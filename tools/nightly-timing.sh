#!/bin/sh
# GR1: nightly ownership, data paths and timing away from the gate.
# Sourced by nightly.sh and its no-JVM self-test. All temporary builds stay in its clone.
nightly_owner() {
    case "$1" in ''|*[!0-9]*) return ;; esac
    case "$(uname -s)" in
        MINGW*|MSYS*|CYGWIN*)
            CBS_NIGHTLY_PID=$1 powershell.exe -NoProfile -Command '$p = Get-Process -Id $env:CBS_NIGHTLY_PID -ErrorAction SilentlyContinue; if ($p) { "{0}-{1}" -f $p.Id, $p.StartTime.ToUniversalTime().Ticks }' 2>/dev/null | tr -d '\r' ;;
        *) [ -r "/proc/$1/stat" ] || return
            boot=$(cat /proc/sys/kernel/random/boot_id) || return
            ticks=$(awk '{sub(/^.*\) /, ""); print $20}' "/proc/$1/stat") || return
            [ -z "$boot" ] || [ -z "$ticks" ] || printf '%s-%s-%s\n' "$1" "$boot" "$ticks" ;;
    esac
}
nightly_unlock() { rmdir "$LOCK_ENTRY" "$LOCK_PATH" 2>/dev/null || :; }
nightly_lock() {  # Publish a prepared, nonempty directory atomically (GR1 review: hard kills left the old lock forever).
    LOCK_PATH=$1; LOCK_PID=$$
    case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) LOCK_PID=$(ps -p $$ | awk 'NR == 2 {print $4}') ;; esac
    LOCK_TOKEN=$(nightly_owner "$LOCK_PID") || return 3
    [ -n "$LOCK_TOKEN" ] || return 3
    LOCK_STAGE=$(mktemp -d "$LOCK_PATH.XXXXXXXX") || return 3
    mkdir "$LOCK_STAGE/owner-$LOCK_TOKEN" || { rmdir "$LOCK_STAGE"; return 3; }
    LOCK_RECOVERED=0; [ ! -d "$LOCK_PATH" ] || LOCK_RECOVERED=1
    for attempt in 1 2 3; do
        if mv -T "$LOCK_STAGE" "$LOCK_PATH" 2>/dev/null; then
            LOCK_ENTRY=$LOCK_PATH/owner-$LOCK_TOKEN
            : > "$LOG" || { nightly_unlock; return 3; }
            [ "$LOCK_RECOVERED" = 0 ] || printf 'nightly recovered stale lock %s\n' "$LOCK_PATH" >> "$LOG" || { nightly_unlock; return 3; }
            return 0
        fi
        for owner in "$LOCK_PATH"/owner-*; do
            [ -d "$owner" ] || continue
            token=${owner##*/owner-}; pid=${token%%-*}
            if [ "$(nightly_owner "$pid")" = "$token" ]; then
                rmdir "$LOCK_STAGE/owner-$LOCK_TOKEN" "$LOCK_STAGE"; return 3
            fi
            LOCK_RECOVERED=1
            rmdir "$owner" 2>/dev/null || :
        done
        rmdir "$LOCK_PATH" 2>/dev/null || :
    done
    rmdir "$LOCK_STAGE/owner-$LOCK_TOKEN" "$LOCK_STAGE"; return 3
}
nightly_data() {
    [ -z "${JDK_JAVA_OPTIONS:-}" ] || return 0
    case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) MAIN=$(cygpath -m "$MAIN") ;; esac
    for f in earth-2:cbs.earth world-1:cbs.world; do
        [ ! -f "$MAIN/data/${f%%:*}.bin" ] || JDK_JAVA_OPTIONS="${JDK_JAVA_OPTIONS:+$JDK_JAVA_OPTIONS }-D${f#*:}=\"$MAIN/data/${f%%:*}.bin\""
    done
    export JDK_JAVA_OPTIONS
}
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
    printf '%s\n' "$1" > "$2/commit" || return 1
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
nightly_tier() {
    green=${1:-$HOME/.cbs-nightly-tier-green}
    : > "$CLONE/observations.tsv"
    java -Xmx2048m -Xms64m -Dcbs.timing=hard -Dcbs.observations=hard \
        -Dcbs.observations.tsv="$CLONE/observations.tsv" -Dstdout.encoding=UTF-8 \
        -cp "target/classes$SEP$(cat target/classpath.txt)" ham.citybuildersim.AllChecks -f --nightly --observations > "$CLONE/tier.log" 2>&1 || :
    cat "$CLONE/tier.log" >> "$LOG"
    tiercode=0
    CBS_KNOWN_REDS= CBS_REJUDGE=rejudge ./batch-check.sh --judge "$CLONE/tier.log" >> "$LOG" 2>&1 || tiercode=$?
    if [ "$tiercode" = 0 ] || [ "$tiercode" = 3 ]; then
        printf '%s\n' "$FULL" > "$green.new" && mv "$green.new" "$green" || return 1
        TIER='nightly tier OK'
    else
        previous=$(cat "$green" 2>/dev/null || :)
        if [ -n "$previous" ] && git merge-base --is-ancestor "$previous" "$FULL"; then
            TIER="nightly tier RED, exit $tiercode; merges since $previous..$FULL"
            merges=$(git log --first-parent --merges --format='%h %s' "$previous..$FULL")
        else
            TIER="nightly tier RED, exit $tiercode; no previous green tier run"
            merges=$(git log -1 --format='%h %s' "$FULL")
        fi
        printf '%s\n%s\n' "$TIER" "$merges" >> "$LOG"
        TIER="$TIER: $(printf '%s\n' "$merges" | paste -sd ';' -)"
    fi
    [ "$tiercode" = 3 ] && tiercode=0
    return "$tiercode"
}

# PO1: retain every line's last hold, even across incomplete or red nights.
nightly_observations() {
    history=${1:-$HOME/.cbs-observations}
    mkdir -p "$history" || return 1
    [ -s "$CLONE/observations.tsv" ] || { echo 'observations incomplete: no readings'; return 1; }
    touch "$history/held.tsv"
    awk -v commit="$FULL" -v state="$history/held.new" -f "$ORIG/tools/observations.awk" \
        "$history/held.tsv" "$CLONE/observations.tsv" > "$history/misses.tsv" || return 1
    [ -f "$history/held.new" ] || : > "$history/held.new"
    mv "$history/held.new" "$history/held.tsv" || return 1
    tab=$(printf '\\t')
    while IFS="$tab" read -r harness label last; do
        if [ "$last" != - ] && git merge-base --is-ancestor "$last" "$FULL"; then
            echo "OBSERVED $harness | $label | merges since last held $last..$FULL"
            git log --first-parent --merges --format='%h %s' "$last..$FULL"
        else
            echo "OBSERVED $harness | $label | no earlier held reading on this ancestry; merge range unavailable through $FULL"
            git log -1 --format='%h %s' "$FULL"
        fi
    done < "$history/misses.tsv"
    echo "observations: $(wc -l < "$history/misses.tsv" | tr -d ' ') miss(es); $history/misses.tsv"
}

nightly_timing() {  # optional record root for the no-JVM fixture
    MACHINE=$(hostname | tr -d '\r\n' | tr -c 'A-Za-z0-9_.-' '_')
    STORE=${1:-$HOME/.cbs-timing}/$MACHINE
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
