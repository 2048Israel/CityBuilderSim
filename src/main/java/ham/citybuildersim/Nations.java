package ham.citybuildersim;

/**
 * The nations beyond the city, each on the world's path: its year - output,
 * people, prices and its money's rate to the US dollar - from the path
 * (Historical or Random), the seed and the world file (WN1; spec-nations 7).
 *
 * WHY THIS EXISTS. The world outside was a price list fixed at founding, and
 * the only thing out there that moved, the dollar's inflation, was drawn from
 * the month alone, so every city met the same world (spec-nations 1.4).
 * Jerus: "perhaps a seed for the path? one being historical and the other
 * being random". Now the world has a path, picked at founding (D9).
 */
public final class Nations {

    /** The world's path, picked at founding (D9): the real years from January 2000, or a path drawn from the seed. */
    public enum Path { HISTORICAL, RANDOM }

    /** The path a founding takes when nobody picks one: Historical, the founding screen's first chip (board 4). */
    public static final Path DEFAULT_PATH = Path.HISTORICAL;

    /** The path's seed when nobody rolls one: 48,213, the founding board's (spec-nations, board 4), so every harness and the playtest meet one world. */
    public static final long DEFAULT_SEED = 48_213L;
}
