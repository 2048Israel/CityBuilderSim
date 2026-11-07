package ham.citybuildersim;

/** Scratch (FD1): the churn probe on one ensemble run: FD1Ens <probe-out.txt> <POLICY> <seed> <out.csv>. */
public class FD1Ens {
    public static void main(String[] args) throws Exception {
        FD1Probe.log = new java.io.PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FD1Probe::month;
        Runtime.getRuntime().addShutdownHook(new Thread(FD1Probe::summary));
        Ensemble.main(new String[] { args[1], args[2], args[3] });
    }
}
