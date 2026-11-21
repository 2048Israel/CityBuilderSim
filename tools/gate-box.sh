#!/bin/sh
# tools/gate.sh on the gate box (GB1). gate.sh hands its call here when a box is named: CBS_GATE_HOST, else the lines of
# ~/.cbs-gate-box, ssh destinations tried in order (CBS_GATE_HOST= runs the gate here). The box runs this checkout's
# commit of tools/ (fetched from origin, under ~/cbs/tools) and its gate.sh as on any Linux host, its runs in ~/cbs/runs.
# Output and bus calls come back on the one connection, the calls posted here through bus.sh; the run's files but its
# clone are copied home at the end. A dropped connection stops the gate, as its caller's exit does (SR1c).
# WHY (GB1, 2026-11-20): gates held the laptop's heavy slot; Jerus: gates run on the box, started headless as before,
# and when it cannot be reached, "notify me then run on laptop". tools/nightly.sh hands its night here (--nightly) the
# same way; its verdict, log and records come home, its timing figures stay the box's (GB2, Jerus: "the nightly timing
# run needs to also move to the gatebox"). tools/ensemble/ensemble.sh hands its runs here (--ensemble LABEL): the pushed
# commit runs there behind every gate and night, its runs come home and are scored here, and a box with two heavy jobs
# running or waiting sends it back to run here (GB3, Jerus: "Gatebox first", the laptop the overflow).
set -u
HERE=$(cd "$(dirname "$0")" && pwd)
enc() { printf '%s%s' "$1" "$(printf '%s' "$2" | base64 | tr -d '\n')"; }   # one word: a kind letter, then base64
dec() { printf '%s' "${1#?}" | base64 -d; }
# GB3, Jerus 2026-11-20: an ensemble goes to the box "unless two or more heavy jobs (gate, nightly, ensemble) are already
# running or waiting there".
FULL=2
# GB3: the box's runs at once, one a core; measured on BO1-S's commit (2026-11-20): 8 played the 80 in 17.5 min, 8.7 GB,
# AUTO2's longest in 277 s, what a gate that comes may wait; 16 played 28% more an hour, each run 57% longer.
BOX_JOBS=8
heavy() {   # GB3, on the box: its other gates, nights and ensembles, a "kind name" line each, from the serves' command lines
    ps -eww -o pid=,ppid=,args= 2> /dev/null | while read -r p q a; do case $a in *"/gate-box.sh --serve"*) ;; *) continue ;; esac
        tr '\0' '\n' < "/proc/$p/environ" 2> /dev/null | grep -q -x -F "HOME=$HOME" && echo "$p $q ${a#*--serve}"; done |
        awk -v me="${CBS_BOX_SERVE:-$$}" '{ up[$1] = $2; k[$1] = $3 == "" ? "gate" : $3; n[$1] = $4 }   # bare: before GB3
            END { for (i in up) if (i != me && !(up[i] in up) && k[i] != "stop") print k[i] (n[i] == "" ? "" : " " n[i]) }'
}
list() { paste -s -d, - | sed 's/,/, /g'; }

if [ "${1:-}" = --ensemble-here ]; then   # GB3, on the box, the serve's child: one ensemble at a time, gates and nights first
    LABEL=$2; Y=$PWD/target/ensemble/$LABEL.yield; unset ENSEMBLE_OUT ENSEMBLE_DEPS; mkdir -p target/ensemble; rm -f "$Y"
    trap 'bash tools/ensemble/ensemble.sh --stop "$LABEL" > /dev/null 2>&1; exit 143' TERM   # the caller is gone: so are the runs
    first() { heavy | grep -v '^ensemble ' | list; }
    lk() { ! command -v flock > /dev/null || flock "$@"; }   # flock is the box's, not Git Bash's (the selftest's)
    exec 5>> "$HOME/cbs/ensemble.lock" 8>> "$HOME/cbs/box.lock"
    lk -n 5 || { echo "ensemble: $LABEL waits for the box's other ensemble"; until lk -w 10 5; do :; done; }
    echo "ensemble: runs in $PWD/target/ensemble/$LABEL"
    while :; do   # a gate or night that comes stops the runs not started; those started finish, then the box is its
        I=0; until A=$(first); [ -z "$A" ] && lk -n -x 8; do
            [ $((I % 12)) != 0 ] || echo "ensemble: $LABEL waits; gates and the nightly go first (${A:-the box's lock held})"; I=$((I + 1)); sleep 5; done
        [ -n "${BUILT:-}" ] || { mvn -q -B compile > "$Y.mvn" 2>&1 || { tail -20 "$Y.mvn"; exit 1; }; BUILT=1; }   # the build too
        rm -f "$Y"; ENS_PART=runs ENS_YIELD=$Y JOBS=$BOX_JOBS bash tools/ensemble/ensemble.sh "$LABEL" & E=$!
        while kill -0 "$E" 2> /dev/null; do A=$(first)
            [ -e "$Y" ] || [ -z "$A" ] || { : > "$Y"; echo "ensemble: $A came; the runs started finish, then $LABEL waits"; }; sleep 5; done
        wait "$E"; CODE=$?; lk -u 8
        [ "$CODE" = 5 ] || break; RESUME=1   # a fresh run's next pass keeps what the last one finished
    done
    rm -f "$Y" "$Y.mvn"; exit "$CODE"
fi

if [ -n "${CBS_GATE_RELAY:-}" ] && [ "${1:-}" != --serve ]; then   # the box's cbs-bus: the call, one line, to the stream
    REC="@@cbs-bus $(enc m "${CBS_ME:-gate}")"; IN=0
    for a in "$@"; do REC="$REC $(enc a "$a")"; [ "$a" != - ] || IN=1; done
    [ "$IN" = 0 ] || REC="$REC s$(base64 | tr -d '\n')"   # the body's bytes, its last newline too
    T=$(mktemp "$CBS_GATE_RELAY.XXXXXX") && printf '%s\n' "$REC" > "$T" && cat "$T" >> "$CBS_GATE_RELAY"   # one append
    rm -f "$T"; exit 0
fi

if [ "${1:-}" = --serve ]; then   # on the box: the call's words on stdin; gate.sh's output, bus calls and beat streamed
    RUNS=$HOME/cbs/runs; mkdir -p "$RUNS" || exit 1
    [ ! -f "$HOME/cbs/gate.env" ] || . "$HOME/cbs/gate.env"   # the box's own settings: its JDK, the game's Temurin
    [ ! -d "$HOME/cbs/tools" ] || ( flock 9; find "$HOME/cbs/tools" -mindepth 1 -maxdepth 1 -type d -mtime +3 |
        while IFS= read -r t; do git -C "$HOME/cbs/main" worktree remove --force "$t"; done ) 9> "$HOME/cbs/tools.lock" 2> /dev/null
    IFS= read -r WORDS || exit 2
    unset CBS_GATE_DIRECTORY TMP CBS_BOX_SERVE; set --; MINE=; P=gate.sh
    for w in $WORDS; do case $w in
        a*) set -- "$@" "$(dec "$w")" ;;
        f*) F=$(mktemp "$RUNS/file.XXXXXX") && dec "$w" > "$F" && MINE="$MINE $F" && set -- "$@" "$F" ;;
        d*) CBS_GATE_DIRECTORY=$RUNS/$(dec "$w"); export CBS_GATE_DIRECTORY ;;
        m*) CBS_ME=$(dec "$w"); export CBS_ME ;;
        p*) P=$(dec "$w") ;;
        z*) Z=$(dec "$w"); H=${Z#?}; case $Z in -????) TZ=UTC+${H%??}:${H#??} ;; +????) TZ=UTC-${H%??}:${H#??} ;; esac; export TZ ;;
        e*) V=$(dec "$w"); case ${V%%=*} in SEEDS|POLICIES|RESUME|JAVA_OPTS) export "$V" ;; esac ;;   # an ensemble's settings
    esac; done   # z: a night's or an ensemble's caller's UTC offset, its verdict dated as day.sh asks (GB2: the box's UTC put a night after 20:00 tomorrow)
    case $P in gate.sh) ;; nightly.sh|ensemble.sh) cd "$HERE/.." || exit 1 ;; *) exit 2 ;; esac   # the night's checkout is its cwd
    if [ "$P" = ensemble.sh ] && command -v flock > /dev/null; then   # GB3: counted under a lock, so two ensembles count each other once
        exec 6>> "$HOME/cbs/heavy.lock"; flock 6; B=$(heavy)
        [ "$(printf '%s' "$B" | grep -c .)" -lt "$FULL" ] || { echo "@@cbs-full $(printf '%s\n' "$B" | list)"; echo '@@cbs-exit 0'; exit 0; }
        exec 6>&-
    fi
    # GB2: a gate holds the box shared and the nightly whole, each for its whole run; whichever is first runs and the
    # other waits, so the night never times beside a gate. A stop never waits. A flock dies with its holder, where a
    # killed gate's `active` file stays. A second nightly is refused, not queued behind the first.
    case $P:${1:-} in gate.sh:--stop|ensemble.sh:*) L= ;; gate.sh:*) L=-s ;; *) L=-x ;; esac   # an ensemble's locks are its own
    if [ -n "$L" ] && command -v flock > /dev/null; then
        if [ "$L" = -x ]; then exec 7>> "$HOME/cbs/nightly.lock"
            flock -n 7 || { echo 'O nightly: already running on the gate box'; echo '@@cbs-exit 3'; exit 0; }; fi
        exec 8>> "$HOME/cbs/box.lock"
        flock -n $L 8 || { echo "O ${P%.sh}: the gate box is held ($(heavy | list)); this ${P%.sh} waits for it"
            until flock $L -w 60 8; do echo "O ${P%.sh}: still waiting for the box"; done; }
    fi
    CBS_GATE_RELAY=$(mktemp "$RUNS/stream.XXXXXX") || exit 1
    [ -x "$HERE/gate-box.sh" ] || chmod +x "$HERE/gate-box.sh"
    TMPDIR=$RUNS CBS_GATE_ON_BOX=1 CBS_BUS=$HERE/gate-box.sh CBS_BOX_SERVE=$$; export TMPDIR CBS_GATE_ON_BOX CBS_GATE_RELAY CBS_BUS CBS_BOX_SERVE
    [ "$P" != ensemble.sh ] || { P=gate-box.sh; set -- --ensemble-here "$@"; }
    sh "$HERE/$P" "$@" >> "$CBS_GATE_RELAY" 2>&1 < /dev/null &
    GATE=$!
    tail --pid="$GATE" -n +1 -F "$CBS_GATE_RELAY" 2> /dev/null | sed -u '/^@@cbs-/!s/^/O /' &
    SED=$!
    N=0; while kill -0 "$GATE" 2> /dev/null; do   # a beat each 5 s goes out through sed; a dead sed means the caller is gone
        [ $((N % 5)) != 0 ] || echo @@cbs-beat >> "$CBS_GATE_RELAY"; sleep 1; N=$((N + 1))
        kill -0 "$SED" 2> /dev/null || { kill -TERM "$GATE" 2> /dev/null; break; }
    done
    wait "$GATE"; CODE=$?; wait "$SED"
    printf '@@cbs-exit %s\n' "$CODE"; rm -f "$CBS_GATE_RELAY" $MINE; exit 0
fi

P=gate.sh; case ${1:-} in --nightly) P=nightly.sh; shift ;; --ensemble) P=ensemble.sh; shift ;; esac; BY=${CBS_ME:-${P%.sh}}
[ "$#" -gt 0 ] || [ "$P" = nightly.sh ] || exec env CBS_GATE_HOST= sh "$HERE/gate.sh"
[ "${1:-}" != --stop ] || [ ! -d "${2:-}" ] || exec env CBS_GATE_HOST= sh "$HERE/gate.sh" "$@"   # a run here stops here
if [ "$P" = ensemble.sh ]; then   # GB3: the box runs the pushed commit, so that commit must be the whole model
    LABEL=$1; cd "$HERE/.." || exit 1
    here() { echo "ensemble: $1; $LABEL runs on this laptop"; mvn -q -B compile || exit 1; exec env CBS_GATE_HOST= bash tools/ensemble/ensemble.sh "$LABEL"; }
    [ -z "$(git status --porcelain -- src pom.xml tools/ensemble)" ] || { echo "ensemble: the box runs the pushed commit, and src, pom.xml or tools/ensemble holds changes not committed: commit and push them (CBS_GATE_HOST= runs this working tree here)"; exit 1; }
    git fetch -q origin 2> /dev/null; [ -n "$(git branch -r --contains HEAD 2> /dev/null)" ] || { echo "ensemble: $(git rev-parse --short HEAD) is not on origin, and the box runs the pushed commit: push it (CBS_GATE_HOST= runs it here)"; exit 1; }
    for o in ${JDK_JAVA_OPTIONS:-}; do case $o in -Dcbs.earth=*/earth-2.bin|-Dcbs.world=*/world-1.bin) ;; *) here "JDK_JAVA_OPTIONS holds $o, which the box does not take" ;; esac; done
    case " ${JAVA_OPTS:-}" in *" -Dcbs.earth="*|*" -Dcbs.world="*) here "JAVA_OPTS names data files, where the box has its own copies of main's" ;; esac
fi
SSH=${CBS_GATE_SSH:-ssh}; OPTS='-o BatchMode=yes -o ConnectTimeout=8 -o ServerAliveInterval=30 -o ServerAliveCountMax=6'
BOXES=$(echo ${CBS_GATE_HOST-$(cat "$HOME/.cbs-gate-box" 2> /dev/null)}); HOST=
for b in $BOXES; do $SSH $OPTS -n "$b" true > /dev/null 2>&1 && { HOST=$b; break; }; done
if [ -z "$HOST" ]; then
    case $P in nightly.sh) WHAT='the nightly' ;; ensemble.sh) WHAT="the ensemble $1" ;; *) WHAT="$1's gate"; case " $* " in *" --dry-run "*) WHAT="$1's dry run" ;; esac ;; esac
    [ "${1:-}" != --stop ] || WHAT="a stop"
    [ "$P" = ensemble.sh ] || echo "${P%.sh}: no gate box answers ($BOXES); $WHAT runs on this machine"
    CBS_ME=$BY sh "$HERE/bus.sh" send jerus --kind info --urgent \
        --body "The gate box cannot be reached ($BOXES): $WHAT runs on the laptop." < /dev/null > /dev/null 2>&1
    [ "$P" != ensemble.sh ] || here "no gate box answers ($BOXES)"
    exec env CBS_GATE_HOST= sh "$HERE/$P" "$@"
fi
C=$(git -C "$HERE/.." rev-parse HEAD) || exit 1
W="$(enc m "$BY") $(enc p "$P")"; [ "$P" = gate.sh ] || W="$W $(enc z "$(date +%z)")"; [ -z "${CBS_GATE_DIRECTORY:-}" ] || W="$W $(enc d "$(basename "$CBS_GATE_DIRECTORY")")"
[ "$P" != ensemble.sh ] || for v in SEEDS POLICIES RESUME JAVA_OPTS; do x=$(printenv "$v") && W="$W $(enc e "$v=$x")"; done
K=$(printf '%s' "${1:-}" | tr -cd 'A-Za-z0-9._-')   # GB3: the serve's command line names its job, which the box's heavy() reads
case $P:${1:-} in nightly.sh:*) K=nightly ;; ensemble.sh:*) K="ensemble $K" ;; *:--stop) K=stop ;; *) case " $* " in *" --dry-run "*) K="dry-run $K" ;; *) K="gate $K" ;; esac ;; esac
FILE=0; END=0
for a in "$@"; do   # -F BODY and --red-lines FILE send the file's text, not its path
    if [ "$FILE" = 1 ]; then [ -f "$a" ] || { echo "gate: no file $a"; exit 2; }; W="$W f$(base64 < "$a" | tr -d '\n')"; FILE=0; continue; fi
    W="$W $(enc a "$a")"
    case "$END:$a" in 0:--) END=1 ;; 0:-F|0:--red-lines) FILE=1 ;; esac
done
post() {   # a bus call the box's gate made, made here; its words name the box's paths, so Git Bash converts none of them
    WS=$1; set --; ME=gate; IN=; MSYS_NO_PATHCONV=1 MSYS2_ARG_CONV_EXCL='*'; export MSYS_NO_PATHCONV MSYS2_ARG_CONV_EXCL
    for w in $WS; do case $w in m*) ME=$(dec "$w") ;; s*) IN=$w ;; a*) set -- "$@" "$(dec "$w")" ;; esac; done
    if [ -n "$IN" ]; then dec "$IN" | CBS_ME=$ME sh "$HERE/bus.sh" "$@"; else CBS_ME=$ME sh "$HERE/bus.sh" "$@" < /dev/null; fi > /dev/null 2>&1
}
R="set -e; mkdir -p ~/cbs/tools; cd ~/cbs/main; exec 9> ../tools.lock; flock 9; git fetch -q origin; T=\$HOME/cbs/tools/$C"
R="$R; git worktree prune; [ -d \$T ] || git worktree add -q --detach \$T $C; touch \$T; exec 9>&-; exec sh \$T/tools/gate-box.sh --serve $K"
ST=$(mktemp "${TMPDIR:-/tmp}/gate-box.XXXXXX") || exit 1
echo "${P%.sh}: on the gate box $HOST, tools at $(git -C "$HERE/.." rev-parse --short HEAD)"
printf '%s\n' "$W" | $SSH $OPTS "$HOST" "$R" 2>&1 | while IFS= read -r line; do case "$line" in
    "O gate: run "*) printf '%s\n' "${line#O }"; printf '%s\n' "${line#O gate: run }" > "$ST.run" ;;
    "O ensemble: runs in "*) printf '%s\n' "${line#O }"; printf '%s\n' "${line#O ensemble: runs in }" > "$ST.ens" ;;
    "@@cbs-full "*) printf '%s\n' "${line#@@cbs-full }" > "$ST.full" ;;
    "O "*) printf '%s\n' "${line#O }" ;;
    "@@cbs-bus "*) post "${line#@@cbs-bus }" ;;
    "@@cbs-exit "*) printf '%s\n' "${line#@@cbs-exit }" > "$ST" ;;
    @@cbs-beat) ;;
    *) printf '%s\n' "$line" ;;
esac; done
CODE=$(cat "$ST" 2> /dev/null); RUN=$(cat "$ST.run" 2> /dev/null); rm -f "$ST" "$ST.run"
if [ -n "$RUN" ]; then
    L=${TMP:-${TMPDIR:-/tmp}}/$(basename "$RUN")
    mkdir -p "$L" && $SSH $OPTS -n "$HOST" "tar -C '$RUN' --exclude=./clone -cf - ." | tar -C "$L" -xf - &&
        echo "gate: the box's run $RUN is copied to $L"
fi
if [ "$P" = nightly.sh ] && [ -n "$CODE" ]; then   # GB2: the night's verdict, log and records home for day.sh; its paths read from here
    $SSH $OPTS -n "$HOST" 'cd && tar -cf - .cbs-nightly.txt .cbs-nightly.log .cbs-nightly-tier-green .cbs-observations 2> /dev/null' |
        tar -C "$HOME" -xf - && sed -i "s#[^ ;]*/\.cbs-timing/#$HOST:.cbs-timing/#g; s#[^ ;]*/\.cbs-observations/#$HOME/.cbs-observations/#g" \
        "$HOME/.cbs-nightly.txt" && echo "nightly: the box's verdict, log and records are copied to $HOME"
fi
if [ "$P" = ensemble.sh ]; then   # GB3: a full box sends it here; its runs come home, scored here as ever
    F=$(cat "$ST.full" 2> /dev/null); E=$(cat "$ST.ens" 2> /dev/null); rm -f "$ST.full" "$ST.ens"
    [ -z "$F" ] || here "the gate box has $F running or waiting (Jerus, 2026-11-20: the laptop is the overflow when two are)"
    if [ -n "$E" ] && { [ "$CODE" = 0 ] || [ "$CODE" = 3 ]; }; then
        O=${ENSEMBLE_OUT:-target/ensemble}/$LABEL; rm -rf "$O.box"; mkdir -p "$O.box" && $SSH $OPTS -n "$HOST" "tar -C '$E' \
            --exclude=./classes --exclude=./driver --exclude=./tmp -czf - ." | tar -C "$O.box" -xzf - && rm -rf "$O" && mv "$O.box" "$O" ||
            { echo "ensemble: $HOST:$E could not be copied to $O"; exit 1; }
        echo "ensemble: the box's runs are copied to $O, in place of what was there, to be scored here"
        exec env CBS_GATE_HOST= ENS_PART=score bash tools/ensemble/ensemble.sh "$LABEL"
    fi
fi
[ -n "$CODE" ] || { echo "${P%.sh}: $HOST gave no exit (why is above; a commit not on origin cannot run there, a dropped connection stops the run)"; exit 1; }
exit "$CODE"
