#!/bin/sh
# Read-only morning report (--due: ready/stuck/nightly due, exit 1 if none).
# WHY (BP1, GG1): one report replaces hand probes; provisional ship records are working.
# Main/version/reds/nightly/queue, branch state and predicted conflicts; fetch failures are shown.
# tools/carries.sh keeps legacy predecessor branches waiting. No JVM.

usage() { echo "usage: sh tools/day.sh [--due]" >&2; exit 2; }
DUE=0
case "$1" in
    "") ;;
    --due) DUE=1 ;;
    *) usage ;;
esac
[ $# -le 1 ] || usage
git rev-parse --git-dir >/dev/null 2>&1 || { echo "day       not in a git checkout" >&2; exit 2; }
CARRIES="$(cd "$(dirname "$0")" && pwd)/carries.sh"
cd "$(git rev-parse --show-toplevel)" || exit 2
NIGHTLY=$HOME/.cbs-nightly.txt   # tools/nightly.sh's last verdict (GT1)

# --- origin ----------------------------------------------------------------
FETCHMSG=
if [ -z "$CBS_DAY_NOFETCH" ]; then
    FERR=$(GIT_TERMINAL_PROMPT=0 git fetch -q --prune --tags origin 2>&1) ||
        FETCHMSG="day       fetch failed ($(printf '%s\n' "$FERR" | head -n 1 | cut -c1-80)): origin as last fetched"
fi
M=origin/main
git rev-parse --verify -q "$M" >/dev/null || { echo "day       no $M here" >&2; exit 2; }
NOW=$(date +%s)
NL='
'
GEN='^docs/(map/|dials\.md|month-order\.md|harnesses\.md)'
ago() {   # seconds since the epoch time $1, said in minutes, hours or days
    s=$((NOW - $1)); [ "$s" -ge 0 ] || s=0
    if [ "$s" -lt 3600 ]; then echo "$((s / 60)) min"
    elif [ "$s" -lt 172800 ]; then echo "$((s / 3600)) h"
    else echo "$((s / 86400)) days"; fi
}
rows() {   # the ids in the first column of the table under "## $1" in QUEUE.md, from stdin
    awk -F'|' -v sec="$1" '/^## / { ins = (index($0, "## " sec) == 1); next }
        ins && /^\| [A-Za-z0-9]/ { gsub(/^ +| +$/, "", $2); if ($2 != "batch" && $2 != "item") print $2 }'
}
Q=$(git show "$M:QUEUE.md" 2>/dev/null)
QUEUED=$(printf '%s\n' "$Q" | rows Queued)

# --- the branches ----------------------------------------------------------
# Built in variables, not in a pipe: a loop in a pipe runs in a subshell and
# its counts are lost.
CAN_MT=0
git merge-tree --write-tree --no-messages "$M" "$M" >/dev/null 2>&1 && CAN_MT=1
mtnote() {   # what git predicts of merging branch ref $1 into main, as the tail of a ready line
    if [ "$CAN_MT" != 1 ]; then printf ' - conflicts not predicted'; return; fi
    R=$(git merge-tree --write-tree --name-only --no-messages "$M" "$1" 2>/dev/null); rc=$?
    if [ "$rc" -eq 1 ]; then
        C=$(printf '%s\n' "$R" | sed 1d | grep -v '^$' | grep -v -E "$GEN" | head -n 4 | tr '\n' ' ')
        [ -z "$C" ] || printf ' - BUT conflicts with main in %s' "${C% }"
    elif [ "$rc" -ne 0 ]; then printf ' - conflicts not predicted'
    fi
}
READY=; WAIT=; STUCK=; WORK=; OFFQ=; MERGED=0
for b in $(git for-each-ref --format='%(refname)' 'refs/remotes/origin/batch/*'); do
    id=${b#refs/remotes/origin/batch/}
    if git merge-base --is-ancestor "$b" "$M"; then MERGED=$((MERGED + 1)); continue; fi
    t=$(git log -1 --format=%ct "$b")
    age=$(ago "$t")
    if git show "$b:docs/queue/$id-ship.md" 2>/dev/null > /dev/null &&
        ! git show "$b:docs/queue/$id-ship.md" | grep -q -x '<!-- readiness: provisional -->'; then
        pred=$(sh "$CARRIES" "$b" | tr '\n' ' ')
        if [ -z "$pred" ]; then READY="${READY}ready     $id - ./ship.sh batch/$id (last commit $age ago)$(mtnote "$b")$NL"
        else WAIT="${WAIT}waiting   $id - ship file pushed, built on ${pred% }, not on main yet: gate that first$NL"; fi
    elif ! printf '%s\n' "$QUEUED" | grep -qxF "$id"; then
        OFFQ="${OFFQ:+$OFFQ, }$id ($age)"
    elif [ $((NOW - t)) -ge 86400 ]; then
        STUCK="${STUCK}stuck     $id - queued, no commit for $age$NL"
    else
        WORK="${WORK}working   $id - queued, last commit $age ago$NL"
    fi
done

# --- --due: only what needs the orchestrator --------------------------------
if [ "$DUE" = 1 ]; then
    [ -z "$FETCHMSG" ] || echo "$FETCHMSG"
    OUT="$READY$STUCK"
    case "$(date +%H)" in   # GT1: the nightly playtest is due once a day, 01:00 to 07:00
        0[1-6]) [ "$(cut -d ' ' -f1 "$NIGHTLY" 2>/dev/null)" = "$(date +%Y-%m-%d)" ] ||
                    OUT="${OUT}nightly   due - sh tools/nightly.sh, in the background$NL" ;;
    esac
    [ -n "$OUT" ] || exit 1
    printf '%s' "$OUT"
    exit 0
fi

# --- main, the record, the queue -------------------------------------------
[ -z "$FETCHMSG" ] || echo "$FETCHMSG"
V=$(git show "$M:src/main/java/ham/citybuildersim/GameVersion.java" | sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' | head -n 1)
LAST=$(git tag -l | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -t. -k1,1n -k2,2n -k3,3n | tail -n 1)
if [ "$V" = "$LAST" ]; then VS="$V = the last version tag"; else VS="$V BUT the last version tag is ${LAST:-none} (tag or mend main)"; fi
echo "main      $VS; merged $(ago "$(git log -1 --format=%ct "$M")") ago: $(git log -1 --format=%s "$M" | cut -c1-70)"
if G=$(git show "$M:docs/notes/gate.txt" 2>/dev/null); then
    # ship.sh's '# date:' line: the '# gate of' line above it carries the queue row's text, dates and all
    GDATE=$(printf '%s\n' "$G" | sed -n 's/^# date: \([0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}\).*/\1/p' | head -n 1)
    REDS=$(printf '%s\n' "$G" | awk '
        /^#/ || /^[ \t]*$/ { next }
        { i = index($0, " | "); if (i == 0) next
          h = substr($0, 1, i - 1); rest = substr($0, i + 3)
          if (!(h in seen)) { seen[h] = 1; order[++n] = h }
          if (rest !~ /^exit [0-9]+$/) cnt[h]++ }
        END { for (k = 1; k <= n; k++) { c = cnt[order[k]]; if (c == 0) c = 1
                                          printf "%s%s %d", (k > 1 ? ", " : ""), order[k], c } }')
    echo "reds      ${REDS:-none} - gate of ${GDATE:-an unknown day}, docs/notes/gate.txt"
else
    echo "reds      no gate record yet (docs/notes/gate.txt is not on $M)"
fi
if [ -f "$NIGHTLY" ]; then echo "nightly   $(head -n 1 "$NIGHTLY" | tr -d '\r'); log ${NIGHTLY%.txt}.log"
else echo "nightly   none yet: sh tools/nightly.sh"; fi
echo "queue     $(printf '%s\n' "$QUEUED" | grep -c .) queued, next: $(printf '%s\n' "$QUEUED" | head -n 4 | tr '\n' ' ' | sed 's/ $//')"
PEND=$(printf '%s\n' "$Q" | rows Pending)
echo "pending   $(printf '%s\n' "$PEND" | grep -c .): $(printf '%s\n' "$PEND" | tr '\n' ' ' | sed 's/ $//')"

# --- the branches, then the tidy count ---------------------------------------
printf '%s' "$READY$WAIT$STUCK$WORK"
[ -z "$OFFQ" ] || echo "off-queue unmerged and not queued: $OFFQ"
[ "$MERGED" = 0 ] || echo "tidy      $MERGED merged batch branches still on origin"
exit 0
