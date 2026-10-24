# SaveCopy.java - 54 lines · 5 methods · 0 constants · model

`ham/citybuildersim/SaveCopy.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> The small copies a save's snapshot is made of: a list or map copied, with
> each element copied too where the elements can change. No state.
> 
> WHY IT EXISTS (UP1, 2026-10-22)
> 
> The autosave is written on SaveWriter's thread while the next months run, so
> DataSave may hold no object the model still changes. A setter that took a
> live list, map or state object kept it; the writer then turned into JSON a
> list the next month was adding to. Each copy() on a state class (a clone, so
> a field added later is in it, with the lists and what they hold copied here)
> and each DataSave setter that copies is the same rule written once per
> place; SaveSlotCheck's walk holds it. A map is copied into a LinkedHashMap
> in the source's iteration order, so the JSON text is the one the live map
> would have written; a null stays null.

**Used by (9):** [AutoBuilder](AutoBuilder.md), [BondMarket](BondMarket.md), [ConstructionControl](ConstructionControl.md), [CorporateBond](CorporateBond.md), [DataSave](DataSave.md), [Exchange](Exchange.md), [OrderBook](OrderBook.md), [SaveSlotCheck](SaveSlotCheck.md), [TreasuryFund](TreasuryFund.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 30 | **type** `final class SaveCopy` | The small copies a save's snapshot is made of: a list or map copied, with each element copied too where the elements can change. |
| 27 | 1 | `private SaveCopy()` |  |
| 30 | 6 | `static<T> List<T> list(List<T> from, UnaryOperator<T> each)` | A new list of `from` with each element copied by `each`; null stays null, and so does a null element. |
| 38 | 3 | `static<T> List<T> list(List<T> from)` | A new list of the same elements, for a list of values nobody changes (numbers, strings); null stays null. |
| 43 | 6 | `static<K, V> Map<K, V> map(Map<K, V> from, UnaryOperator<V> each)` | A new map in `from`'s iteration order, its values copied by `each`; null stays null. |
| 51 | 3 | `static<K, V> Map<K, V> map(Map<K, V> from)` | A new map in `from`'s iteration order, for values nobody changes; null stays null. |

