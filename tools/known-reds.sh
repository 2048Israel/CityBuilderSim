#!/bin/sh
# Registry plus ship-file transfers, with this batch's remaining entries vetoed.
# WHY (GG1, KS1/DM2): builder and gate must retire and re-file the same exemptions.
ID=$1; SHIP=${2:-/dev/null}
[ -f "$SHIP" ] || SHIP=/dev/null
awk -F ' [|] ' -v id="$ID" '
    FILENAME == ARGV[1] { base[++n] = $0; next }
    /^## / { on = ($0 == "## known-reds"); next }
    !on || /^#/ || /^[[:space:]]*$/ { next }
    NF != 5 || $1 !~ /^[A-Za-z0-9_]+$/ || $2 == "" || $3 !~ /^[A-Za-z0-9_-]+$/ || $4 !~ /^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$/ || $5 == "" { bad = 1; next }
    { key = $1 SUBSEP $2; replacement[key] = 1; added[++m] = $0 }
    function put(line, c) { split(line, c, " [|] "); print (c[3] == id ? "! " : "") line }
    END {
        if (bad) { print "malformed known-reds row" > "/dev/stderr"; exit 1 }
        for (i = 1; i <= n; i++) {
            split(base[i], c, " [|] ")
            if (!replacement[c[1] SUBSEP c[2]]) put(base[i])
        }
        for (i = 1; i <= m; i++) put(added[i])
    }' docs/notes/known-reds.txt "$SHIP"
