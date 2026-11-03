"""The blow-up study's prototype fix (spec-blowup-7200.md, HQ1): writes a scratch
Haulage.java whose price is struck over today's trucks, never under one truck's
month, and re-struck when the fleet that carries a month is larger than the one
the quote was struck over. Not game code: compile it into a folder ahead of
target/classes on the classpath; the tree is untouched.

    python -I make-haulage.py src/main/java/ham/citybuildersim/sectors/Haulage.java OUT/Haulage.java [--no-floor]

--no-floor: the re-strike alone (the batch's recommendation), the divisor the trucks in force with no floor.
"""
import sys

src, out = sys.argv[1], sys.argv[2]
floor = "--no-floor" not in sys.argv[3:]
s = open(src, encoding="utf-8").read()


def swap(old, new):
    global s
    if s.count(old) != 1:
        sys.exit("not found once: " + old[:60])
    s = s.replace(old, new)


# the tonnes the quote was struck over, and the divisor never under one truck's month
swap("    private boolean quoteKnown;\n",
     "    private boolean quoteKnown;\n"
     "    /** PROTOTYPE: the tonnes a month the quote (and the steady quote) were struck over; 0 until struck. */\n"
     "    private double quotedOver;\n"
     "    /** PROTOTYPE: what the price is struck over - the trucks in force (with the floor: never under MIN_TRUCKS_A_MONTH trucks' month). */\n"
     + ("    private double divisor() { return Math.max(capacityInForce, MIN_TRUCKS_A_MONTH * TONNES_A_TRUCK_MONTH); }\n" if floor
        else "    private double divisor() { return capacityInForce; }\n"))

# the bill: a quote struck over fewer trucks than carried the month is re-struck over the trucks that did
swap("        double price = getQuote();\n",
     "        double price = getQuote();\n"
     "        if (quoteKnown && quotedOver > 0 && divisor() > quotedOver) price *= quotedOver / divisor();\n")

# the target over the divisor
swap("        return allowed / capacityInForce * (1 + tightness * (Freight.MAX_SCARCITY_MULTIPLE - 1));",
     "        return allowed / divisor() * (1 + tightness * (Freight.MAX_SCARCITY_MULTIPLE - 1));")

# the reprice: the money moves at REPRICE_SPEED, the tonnes it is spread over are today's
swap("        if (carried > 0 && capacityInForce > 0) {\n"
     "            double target = rawTarget();\n",
     "        if (carried > 0 && capacityInForce > 0) {\n"
     "            double rescale = quotedOver > 0 ? quotedOver / divisor() : 1;\n"
     "            quote *= rescale;\n"
     "            steadyQuote *= rescale;\n"
     "            quotedOver = divisor();\n"
     "            double target = rawTarget();\n")
swap("            double steady = steadyAllowed / capacityInForce;\n",
     "            double steady = steadyAllowed / divisor();\n")

open(out, "w", encoding="utf-8", newline="\n").write(s)
print("wrote", out)
