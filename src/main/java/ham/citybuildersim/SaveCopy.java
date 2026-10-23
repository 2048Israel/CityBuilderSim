package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * The small copies a save's snapshot is made of: a list or map copied, with
 * each element copied too where the elements can change. No state.
 *
 * WHY IT EXISTS (UP1, 2026-10-22)
 *
 * The autosave is written on SaveWriter's thread while the next months run, so
 * DataSave may hold no object the model still changes. A setter that took a
 * live list, map or state object kept it; the writer then turned into JSON a
 * list the next month was adding to. Each copy() on a state class (a clone, so
 * a field added later is in it, with the lists and what they hold copied here)
 * and each DataSave setter that copies is the same rule written once per
 * place; SaveSlotCheck's walk holds it. A map is copied into a LinkedHashMap
 * in the source's iteration order, so the JSON text is the one the live map
 * would have written; a null stays null.
 */
final class SaveCopy {

    private SaveCopy() { }

    /** A new list of `from` with each element copied by `each`; null stays null, and so does a null element. */
    static <T> List<T> list(List<T> from, UnaryOperator<T> each) {
        if (from == null) return null;
        List<T> out = new ArrayList<>(from.size());
        for (T t : from) out.add(t == null ? null : each.apply(t));
        return out;
    }

    /** A new list of the same elements, for a list of values nobody changes (numbers, strings); null stays null. */
    static <T> List<T> list(List<T> from) {
        return from == null ? null : new ArrayList<>(from);
    }

    /** A new map in `from`'s iteration order, its values copied by `each`; null stays null. */
    static <K, V> Map<K, V> map(Map<K, V> from, UnaryOperator<V> each) {
        if (from == null) return null;
        Map<K, V> out = new LinkedHashMap<>();
        for (Map.Entry<K, V> e : from.entrySet()) out.put(e.getKey(), e.getValue() == null ? null : each.apply(e.getValue()));
        return out;
    }

    /** A new map in `from`'s iteration order, for values nobody changes; null stays null. */
    static <K, V> Map<K, V> map(Map<K, V> from) {
        return from == null ? null : new LinkedHashMap<>(from);
    }
}
