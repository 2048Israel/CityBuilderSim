#!/bin/sh
# GF1 review: cancellation reports compare origin refs and undo local publication work after a forced stop.
RUN=$(cd "$1" && pwd) || exit 1
CLONE="$RUN/clone"
REASON=$(tr -d '\r\n' < "$RUN/cancelled")
case "$REASON" in --stop) HOW='by --stop' ;; caller) HOW='with its caller' ;; *) HOW='by signal' ;; esac
LOCKED=0
for attempt in $(seq 1 100); do
    if mkdir "$RUN/rollback.lock" 2>/dev/null; then LOCKED=1; break; fi
    sleep 0.1
done
[ "$LOCKED" = 1 ] || { echo 'gate: cancellation rollback is still busy'; exit 1; }
trap 'rmdir "$RUN/rollback.lock" 2>/dev/null' EXIT
if [ -f "$RUN/head.before" ]; then
    BEFORE=$(cat "$RUN/head.before"); HEAD=$(git -C "$CLONE" rev-parse HEAD) || exit 1
    TAG=$(cat "$RUN/tag" 2>/dev/null)
    if [ "$HEAD" != "$BEFORE" ]; then
        if [ -n "$TAG" ] && [ "$(git -C "$CLONE" rev-parse -q --verify "refs/tags/$TAG")" = "$HEAD" ]; then
            git -C "$CLONE" tag -d "$TAG" >/dev/null || exit 1
        fi
        rm -f "$CLONE/.git/index.lock"
        git -C "$CLONE" reset -q --hard "$BEFORE" || exit 1
    fi
fi
rmdir "$RUN/rollback.lock"; trap - EXIT
SNAPSHOT="$RUN/refs.stop.$$"
git -C "$CLONE" ls-remote origin refs/heads/main "$(cat "$RUN/branch")" 'refs/tags/*' > "$SNAPSHOT" \
    || { echo "gate: stopped $HOW; origin refs could not be read"; exit 1; }
CHANGED=$(awk 'NR == FNR { before[$2]=$1; next }
    { after[$2]=$1 }
    END { for (r in before) if (before[r] != after[r]) print r " " before[r] " -> " (after[r] ? after[r] : "deleted")
          for (r in after) if (!(r in before)) print r " absent -> " after[r] }' "$RUN/refs.before" "$SNAPSHOT" | sort)
if [ -z "$CHANGED" ]; then
    echo "gate: stopped $HOW; origin refs unmoved"
else
    echo "gate: stopped $HOW; moved refs"
    printf '%s\n' "$CHANGED"
fi
