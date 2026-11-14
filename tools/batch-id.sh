#!/bin/sh
# Check before creating a branch, and again at the gate. Fetch main first.
# WHY (GG1, PD1): a reused merged branch accepted a fast-forward push.
ID=${1:-}; MAIN=${2:-origin/main}
case "$ID" in ''|*[!A-Za-z0-9_-]*) echo 'batch-id: invalid ID' >&2; exit 2 ;; esac
git rev-parse --verify "$MAIN^{commit}" >/dev/null 2>&1 || exit 2
for REF in "refs/remotes/origin/batch/$ID" "refs/heads/batch/$ID"; do
    if git rev-parse --verify "$REF^{commit}" >/dev/null 2>&1 && git merge-base --is-ancestor "$REF" "$MAIN"; then
        echo "batch-id: $ID already has a merged batch branch; choose a new ID" >&2; exit 1
    fi
done
if git show "$MAIN:docs/notes/changelog.md" | awk -v id="$ID" '
    /^### / { h = $0; sub(/^### /, "", h); gsub(/ — | - /, "\034", h); split(h, c, "\034")
        if (c[1] == id || c[2] == id || index(c[2], id ",") == 1) found = 1 }
    END { exit !found }' ||
    git log "$MAIN" --format=%s | grep -q -E "^Merge batch/$ID(:|,| |$)"; then
    echo "batch-id: $ID already shipped on $MAIN; choose a new ID" >&2; exit 1
fi
