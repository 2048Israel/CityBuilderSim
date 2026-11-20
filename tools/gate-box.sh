#!/bin/sh
# tools/gate.sh on the gate box (GB1). gate.sh hands its call here when a box is named: CBS_GATE_HOST, else the lines of
# ~/.cbs-gate-box, ssh destinations tried in order (CBS_GATE_HOST= runs the gate here). The box runs this checkout's
# commit of tools/ (fetched from origin, under ~/cbs/tools) and its gate.sh as on any Linux host, its runs in ~/cbs/runs.
# Output and bus calls come back on the one connection, the calls posted here through bus.sh; the run's files but its
# clone are copied home at the end. A dropped connection stops the gate, as its caller's exit does (SR1c).
# WHY (GB1, 2026-11-20): gates held the laptop's heavy slot; Jerus: gates run on the box, started headless as before,
# and when it cannot be reached, "notify me then run on laptop". tools/nightly.sh hands its night here (--nightly) the
# same way; its verdict, log and records come home, its timing figures stay the box's (GB2, Jerus: "the nightly timing
# run needs to also move to the gatebox").
set -u
HERE=$(cd "$(dirname "$0")" && pwd)
enc() { printf '%s%s' "$1" "$(printf '%s' "$2" | base64 | tr -d '\n')"; }   # one word: a kind letter, then base64
dec() { printf '%s' "${1#?}" | base64 -d; }

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
    unset CBS_GATE_DIRECTORY TMP; set --; MINE=; P=gate.sh
    for w in $WORDS; do case $w in
        a*) set -- "$@" "$(dec "$w")" ;;
        f*) F=$(mktemp "$RUNS/file.XXXXXX") && dec "$w" > "$F" && MINE="$MINE $F" && set -- "$@" "$F" ;;
        d*) CBS_GATE_DIRECTORY=$RUNS/$(dec "$w"); export CBS_GATE_DIRECTORY ;;
        m*) CBS_ME=$(dec "$w"); export CBS_ME ;;
        p*) P=$(dec "$w") ;;
        z*) Z=$(dec "$w"); H=${Z#?}; case $Z in -????) TZ=UTC+${H%??}:${H#??} ;; +????) TZ=UTC-${H%??}:${H#??} ;; esac; export TZ ;;
    esac; done   # z: a night's caller's UTC offset, its verdict dated as day.sh asks (GB2: the box's UTC put a night after 20:00 tomorrow)
    case $P in gate.sh) ;; nightly.sh) cd "$HERE/.." || exit 1 ;; *) exit 2 ;; esac   # the nightly's checkout is its cwd
    # GB2: a gate holds the box shared and the nightly whole, each for its whole run; whichever is first runs and the
    # other waits, so the night never times beside a gate. A stop never waits. A flock dies with its holder, where a
    # killed gate's `active` file stays. A second nightly is refused, not queued behind the first.
    L=-s; [ "$P" = gate.sh ] || L=-x; [ "$P:${1:-}" != gate.sh:--stop ] || L=
    if [ -n "$L" ] && command -v flock > /dev/null; then
        if [ "$L" = -x ]; then exec 7>> "$HOME/cbs/nightly.lock"
            flock -n 7 || { echo 'O nightly: already running on the gate box'; echo '@@cbs-exit 3'; exit 0; }; fi
        WHO='the gate box runs the nightly; this gate waits for it'; [ "$L" = -s ] || WHO='a gate runs on the gate box; the night waits for it'
        exec 8>> "$HOME/cbs/box.lock"
        flock -n $L 8 || { echo "O ${P%.sh}: $WHO"; until flock $L -w 60 8; do echo "O ${P%.sh}: still waiting for the box"; done; }
    fi
    CBS_GATE_RELAY=$(mktemp "$RUNS/stream.XXXXXX") || exit 1
    [ -x "$HERE/gate-box.sh" ] || chmod +x "$HERE/gate-box.sh"
    TMPDIR=$RUNS CBS_GATE_ON_BOX=1 CBS_BUS=$HERE/gate-box.sh; export TMPDIR CBS_GATE_ON_BOX CBS_GATE_RELAY CBS_BUS
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

P=gate.sh; [ "${1:-}" != --nightly ] || { P=nightly.sh; shift; }; BY=${CBS_ME:-${P%.sh}}
[ "$#" -gt 0 ] || [ "$P" = nightly.sh ] || exec env CBS_GATE_HOST= sh "$HERE/gate.sh"
[ "${1:-}" != --stop ] || [ ! -d "${2:-}" ] || exec env CBS_GATE_HOST= sh "$HERE/gate.sh" "$@"   # a run here stops here
SSH=${CBS_GATE_SSH:-ssh}; OPTS='-o BatchMode=yes -o ConnectTimeout=8 -o ServerAliveInterval=30 -o ServerAliveCountMax=6'
BOXES=$(echo ${CBS_GATE_HOST-$(cat "$HOME/.cbs-gate-box" 2> /dev/null)}); HOST=
for b in $BOXES; do $SSH $OPTS -n "$b" true > /dev/null 2>&1 && { HOST=$b; break; }; done
if [ -z "$HOST" ]; then
    WHAT='the nightly'; [ "$P" = nightly.sh ] || { WHAT="$1's gate"; case " $* " in *" --dry-run "*) WHAT="$1's dry run" ;; esac; }
    [ "${1:-}" != --stop ] || WHAT="a stop"
    echo "${P%.sh}: no gate box answers ($BOXES); $WHAT runs on this machine"
    CBS_ME=$BY sh "$HERE/bus.sh" send jerus --kind info --urgent \
        --body "The gate box cannot be reached ($BOXES): $WHAT runs on the laptop." < /dev/null > /dev/null 2>&1
    exec env CBS_GATE_HOST= sh "$HERE/$P" "$@"
fi
C=$(git -C "$HERE/.." rev-parse HEAD) || exit 1
W="$(enc m "$BY") $(enc p "$P")"; [ "$P" = gate.sh ] || W="$W $(enc z "$(date +%z)")"; [ -z "${CBS_GATE_DIRECTORY:-}" ] || W="$W $(enc d "$(basename "$CBS_GATE_DIRECTORY")")"
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
R="$R; git worktree prune; [ -d \$T ] || git worktree add -q --detach \$T $C; touch \$T; exec 9>&-; exec sh \$T/tools/gate-box.sh --serve"
ST=$(mktemp "${TMPDIR:-/tmp}/gate-box.XXXXXX") || exit 1
echo "${P%.sh}: on the gate box $HOST, tools at $(git -C "$HERE/.." rev-parse --short HEAD)"
printf '%s\n' "$W" | $SSH $OPTS "$HOST" "$R" 2>&1 | while IFS= read -r line; do case "$line" in
    "O gate: run "*) printf '%s\n' "${line#O }"; printf '%s\n' "${line#O gate: run }" > "$ST.run" ;;
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
[ -n "$CODE" ] || { echo "${P%.sh}: $HOST gave no exit (why is above; a commit not on origin cannot run there, a dropped connection stops the run)"; exit 1; }
exit "$CODE"
