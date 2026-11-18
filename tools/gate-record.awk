# WHY (HA1): a partial gate must keep the reds of unpicked nightly harnesses.
BEGIN { FS = "\t" }
FILENAME == ARGV[1] { ran[$2] = 1; next }
/^#/ || !NF { next }
{
    harness = $0; sub(/ *[|].*/, "", harness)
    if (!(harness in ran) || (earth != "named" && harness ~ /^(Earth|Globe)/)) print
}
