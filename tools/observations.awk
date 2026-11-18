# PO1: per-line last-held commits. A miss in any sample wins over that night's holds.
BEGIN { FS = OFS = "\t" }
FILENAME == ARGV[1] { held[$1 FS $2] = $3; next }
NF == 3 && ($3 == "REACHED" || $3 == "MISSED") {
    key = $1 FS $2
    seen[key] = 1
    if ($3 == "MISSED") missed[key] = 1
}
END {
    for (key in seen) {
        if (missed[key]) print key, (held[key] == "" ? "-" : held[key])
        else held[key] = commit
    }
    for (key in held) if (held[key] != "") print key, held[key] > state
    close(state)
}
