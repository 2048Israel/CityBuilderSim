package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sets R0c's 1900 forms from the buildings still without the equipment that ends them: the steam share of each sector, the farms' horse share
 * and the carts' horse share, each the unfitted part of what it scales, and (R6b) Haulage's driven share of the depots, the part of their truck room still without the driverless kit.
 *
 * WHY (R1, 2026-11-05; docs/queue/R1-notes.md calls 4 and 6). R0c built the forms as shares nobody set, off in every played city, and its
 * call 3 said "R1 fits equipment building by building and sets the sector's share from its count; the forms read one number". This is that
 * number's one writer. A stack keeps how many of its buildings lack each piece of equipment (BuildingsStacks.getUnfitted()), and a piece names the
 * form it ends (Equipment.Form), so a form's share is the unfitted part of the sector, weighted by what the form scales: the steam share by each
 * building's grid draw (its electricity), the farms' horses by their land, the carts by the depots' truck room. Fitting a piece (R1b) is then a
 * count going down and nothing else.
 *
 * SET ONLY WHERE A COUNT IS OR WAS (call 6). A share is written only while some stack has an unfitted building that ends its form, and once more
 * when the last is fitted (to 0): a sector with no count, and none last time, is not touched, so the shares a harness sets by hand
 * (Forms1900Check) stand and a played city's month calls nothing. Which shares this has driven is kept in memory and not saved: after a load the
 * counts, which are saved, drive them again (Game's load path).
 *
 * Called where the Policy hears the month (Game.policyHearsTheMonth(): the founding, the top of every month, a load), before anything is made,
 * and once more in every month right after its sites advance (R1b; SimulationEngine.simulateMonth()), the last statement of the month that changes
 * a stack's quantity or its counts and before everything the month derives from them, so that the shares a city rests in between two presses are
 * its counts' at rest and not the month's top's: a save written there and the load that reads it hold the same shares and the same derived
 * state. clear() forgets which shares were driven, where Game rebuilds its world (R1b), so a city founded or loaded into a Game that played
 * another starts as a fresh process does.
 */
public final class EquipmentForms {

    /** The shares driven by the last apply(), each as the form's name and the sector's key ("STEAM:Industry"; "FARM_HORSES:Agriculture"; "CART_HORSES:Freight"): the ones a count that is gone still has to set back to 0. */
    private final Set<String> driven = new HashSet<>();

    /** What a form scales over a set of stacks: the whole, and the part of it that lacks the equipment. */
    private static final class Weight {
        double total, off;
    }

    /**
     * Sets every form's share from the counts, and Freight's driven share of the depots (R6b) from them whatever they are. Does nothing else, and
     * touches nothing else, in a city with no building unfitted that has never had one.
     */
    public void apply(Game game) {
        List<BuildingsStacks> stacks = game.getBuildingManager().getStacksView();
        setDriven(game, stacks);
        setOperated(game, stacks);
        setElectrolysers(game, stacks);
        boolean any = false;
        for (BuildingsStacks s : stacks) {
            if (s.hasUnfitted()) { any = true; break; }
        }
        if (!any && driven.isEmpty()) return;

        Sectors sectors = game.getSectors();
        Agriculture agriculture = sectors.agriculture();
        String farmKey = agriculture.key();
        Map<String, Weight> steam = new LinkedHashMap<>();
        Weight farm = new Weight(), carts = new Weight();
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            long quantity = stack.getQuantity();
            String owner = t.getSector();
            if (!owner.isEmpty()) {
                double draw = t.getElectricityConsumption();
                Weight w = steam.computeIfAbsent(owner, k -> new Weight());
                w.total += quantity * draw;
                w.off += unfitted(stack, Equipment.Form.STEAM) * draw;
                if (owner.equals(farmKey)) {
                    double land = t.getLandSqFt();
                    farm.total += quantity * land;
                    farm.off += unfitted(stack, Equipment.Form.FARM_HORSES) * land;
                }
            }
            double room = t.getTruckRoom();
            if (room > 0) {
                carts.total += quantity * room;
                carts.off += unfitted(stack, Equipment.Form.CART_HORSES) * room;
            }
        }

        for (Sector s : sectors.all()) {
            Weight w = steam.get(s.key());
            double share = drive(Equipment.Form.STEAM, s.key(), w);
            if (share >= 0) s.setSteamShare(share);
        }
        double horses = drive(Equipment.Form.FARM_HORSES, farmKey, farm);
        if (horses >= 0) agriculture.setHorseShare(horses);
        double carted = drive(Equipment.Form.CART_HORSES, sectors.freight().key(), carts);
        if (carted >= 0) sectors.freight().haulage().setHorseShare(carted);
    }

    /**
     * Sets the share of the depots' truck room still driven (R6b, call 1: the driverless kit's piece ends CAB_DRIVERS): in a city founded the 1900 way,
     * the truck room of the buildings lacking the kit over all the truck room, as the carts' weight is struck, and 1 where there is no truck room;
     * in a city founded today's way, always 1, a near-future piece being on no building of a city founded today's way (its depots stand "fitted"
     * only because every count there is). Not driven like the forms above: it is set at every call, and not saved, the counts being.
     */
    private static void setDriven(Game game, List<BuildingsStacks> stacks) {
        Sectors sectors = game.getSectors();
        if (sectors == null) return;
        double total = 0, off = 0;
        if (game.foundedUnfitted()) {
            for (BuildingsStacks stack : stacks) {
                double room = stack.getBuilding().getTruckRoom();
                if (room > 0) {
                    total += stack.getQuantity() * room;
                    off += unfitted(stack, Equipment.Form.CAB_DRIVERS) * room;
                }
            }
        }
        sectors.freight().haulage().setDriverShare(total > 0 ? Math.min(1, off / total) : 1);
    }

    /**
     * THE OPERATORS (R6d, FE8: the autonomy kit's piece ends FIELD_OPERATORS). Sets the share of the farms' machines still operated, a machine
     * being a tractor or a combine the template carries (Agriculture.machinesOf()): in a city founded the 1900 way, the machines of the buildings
     * lacking the kit over all of them, and 1 where there are none; in a city founded today's way, always 1 (the kit is on no building there).
     * Set at every call and not saved, as setDriven() is: the counts are.
     */
    private static void setOperated(Game game, List<BuildingsStacks> stacks) {
        Sectors sectors = game.getSectors();
        if (sectors == null) return;
        Agriculture agriculture = sectors.agriculture();
        double total = 0, off = 0;
        if (game.foundedUnfitted()) {
            for (BuildingsStacks stack : stacks) {
                BuildingsTemplate t = stack.getBuilding();
                if (!t.getSector().equals(agriculture.key())) continue;
                int machines = Agriculture.machinesOf(t);
                if (machines <= 0) continue;
                total += stack.getQuantity() * machines;
                // ...a kit nobody knows yet is on no farm, whatever its count says (a farm placed by hand carries none): every machine there is operated.
                off += (known(game, t, Equipment.Form.FIELD_OPERATORS) ? unfitted(stack, Equipment.Form.FIELD_OPERATORS) : stack.getQuantity()) * machines;
            }
        }
        agriculture.setOperatedShare(total > 0 ? Math.min(1, off / total) : 1);
    }

    /**
     * THE ELECTROLYSERS (R6d, FE10: the Hydrogen Steel Works' piece ends ELECTROLYSERS). Sets how many works carry the 46 kWh a kilogram system, which
     * HeavyIndustry takes off its draw: in a city founded the 1900 way, the works standing less those lacking the piece (never below 0), where the piece is known; in a
     * city founded today's way, 0. Set at every call and not saved: the counts are.
     */
    private static void setElectrolysers(Game game, List<BuildingsStacks> stacks) {
        Sectors sectors = game.getSectors();
        if (sectors == null) return;
        long fitted = 0;
        if (game.foundedUnfitted()) {
            for (BuildingsStacks stack : stacks) {
                BuildingsTemplate t = stack.getBuilding();
                if (!known(game, t, Equipment.Form.ELECTROLYSERS)) continue;
                fitted += Math.max(0, stack.getQuantity() - unfitted(stack, Equipment.Form.ELECTROLYSERS));
            }
        }
        sectors.heavyIndustry().setElectrolyserWorks(fitted);
    }

    /**
     * Whether a template carries a piece that ends this form and the city knows it this month (Equipment.knownAt(), by history's month or the city's own find):
     * a works with no count of a piece nobody knows yet is not one that has it, so a building placed by hand, with no count at all, saves nothing before the
     * piece's month. A building that opens in a city founded the 1900 way gets its counts (Game.fitWhatFinished()), so a real one is never in this case.
     */
    private static boolean known(Game game, BuildingsTemplate t, Equipment.Form form) {
        for (Equipment e : t.getEquipment()) if (e.getEnds() == form && e.knownAt(game.getMonth(), game.getResearch())) return true;
        return false;
    }

    /**
     * The share to set for one form of one sector, or -1 for none to set: set when some of it is unfitted or was last time (then to what the
     * counts say, 0 when they are gone), and remembered as driven only while some is.
     */
    private double drive(Equipment.Form form, String sectorKey, Weight w) {
        String key = form + ":" + sectorKey;
        double off = w == null ? 0 : w.off;
        boolean was = driven.contains(key);
        if (off > 0) driven.add(key);
        else driven.remove(key);
        if (off <= 0 && !was) return -1;
        return w != null && w.total > 0 ? Math.min(1, off / w.total) : 0;
    }

    /** How many of a stack's buildings lack a piece that ends this form: the pieces' counts added, never more than stand (a building lacking two such pieces is counted once). */
    private static long unfitted(BuildingsStacks stack, Equipment.Form form) {
        if (!stack.hasUnfitted()) return 0;
        long n = 0;
        for (Equipment e : stack.getBuilding().getEquipment()) {
            if (e.getEnds() == form) n += stack.getUnfitted(e.getId());
        }
        return Math.min(n, stack.getQuantity());
    }

    /** Forgets which shares were driven: Game.buildWorld() calls it, so that a city founded or loaded into a Game that played another city with counts sets nothing to 0 it never drove. */
    public void clear() {
        driven.clear();
    }

    /** The shares this has set and not yet set back to 0, as "FORM:sector key", sorted: for the harnesses. */
    public Set<String> driven() {
        return new java.util.TreeSet<>(driven);
    }
}
