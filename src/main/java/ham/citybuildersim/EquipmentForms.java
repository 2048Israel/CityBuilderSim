package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sets R0c's 1900 forms from the buildings still without the equipment that ends them: the steam share of each sector, the farms' horse share
 * and the carts' horse share, each the unfitted part of what it scales.
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
 * Called where the Policy hears the month (Game.policyHearsTheMonth(): the founding, the top of every month, a load), before anything is made.
 */
public final class EquipmentForms {

    /** The shares driven by the last apply(), each as the form's name and the sector's key ("STEAM:Industry"; "FARM_HORSES:Agriculture"; "CART_HORSES:Freight"): the ones a count that is gone still has to set back to 0. */
    private final Set<String> driven = new HashSet<>();

    /** What a form scales over a set of stacks: the whole, and the part of it that lacks the equipment. */
    private static final class Weight {
        double total, off;
    }

    /** Sets every form's share from the counts. Does nothing, and touches nothing, in a city with no building unfitted that has never had one. */
    public void apply(Game game) {
        List<BuildingsStacks> stacks = game.getBuildingManager().getStacksView();
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
            int quantity = stack.getQuantity();
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
    private static int unfitted(BuildingsStacks stack, Equipment.Form form) {
        if (!stack.hasUnfitted()) return 0;
        int n = 0;
        for (Equipment e : stack.getBuilding().getEquipment()) {
            if (e.getEnds() == form) n += stack.getUnfitted(e.getId());
        }
        return Math.min(n, stack.getQuantity());
    }

    /** The shares this has set and not yet set back to 0, as "FORM:sector key", sorted: for the harnesses. */
    public Set<String> driven() {
        return new java.util.TreeSet<>(driven);
    }
}
