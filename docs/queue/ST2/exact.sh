#!/bin/sh
# exact.sh <label> <month>... : each month in a JVM of its own, the town played from month 0 and read live, as ScaleCheck
# reads it (ST2Scan with -Dst2.live=true): the same lines as ScaleCheck's sections 6 and 9 to the digit. ~25 s a month.
# A loaded copy of the grown town parts from the live one within six months (ST2-notes), so scan.sh is the coarse scan
# and this is the verdict. Appends to $ST2_SCRATCH/out/<label>.txt; ST2_LEVEL=1 for today's prices.
L=$1; shift
: > "$ST2_SCRATCH/out/$L.txt"
for M in "$@"; do
  ST2_OPTS="-Dst2.live=true $ST2_OPTS" sh "$(dirname "$0")/scan.sh" "$L-one" "$M" "$M" 1 > /dev/null
  grep '^m[0-9]' "$ST2_SCRATCH/out/$L-one.txt" >> "$ST2_SCRATCH/out/$L.txt"
done
echo "done - $ST2_SCRATCH/out/$L.txt"
