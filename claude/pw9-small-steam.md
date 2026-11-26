# A small steam station for an early town

A town's first extra power plant used to come from a catalogue sized for
large modern units. Before 1910's Diesel Station it could buy an 86 MW oil
plant or a much larger coal plant, with hydro possible only at a river site.
The planner correctly priced the idle part of those plants; it had no small
firm station to offer.

PW9 adds an 850 kW Small Steam Station. The central-station censuses of
1902 and 1907 put typical station capacities in hundreds of kilowatts, and
Peabody's utility records a station of this size before 1900. Period capital
and fuel efficiency make this an old coal-burning station, with a shorter
construction lead, rather than a modern cheap generator. It enters the same
dated catalogue and the same power planner as every other plant. Its
permanent id is new, so existing cities' plants retain their sizes.

The first played small station also exposed a capacity error. Its staff
fraction reduced the town's founding power supply, which owns no posts,
so the station could take away more power than it added. PW9 keeps that
endowment fixed in both the planner and native dispatch while each built
plant continues to depend on its own workers.

The smaller row also made a pending-work date mismatch visible. A later
industrial load was counted while generators opening before that load
were omitted. The planner now compares capacity and demand at the same
native completion dates, retaining any real interim shortage. It does
not count a whole construction queue when its first unit opens.

Sources, design estimates, the caused 1906-town harness and the measured
before/after played cities are in [the batch notes](../docs/queue/PW9-notes.md).
