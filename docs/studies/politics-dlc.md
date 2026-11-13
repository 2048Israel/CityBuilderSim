# Civic Ledger Politics DLC

*Post-release concept · 2026-11-13*

Civic Ledger's Politics DLC would add a multiplayer lobby of persistent cities. Players take turns governing the same cities, compete for office with characters they develop over several elections, and can change the rules by which future governors take power. Each administration inherits the economy and political system its predecessors left behind. The regular game stays available while players wait for a city or campaign.

## The city lobby

- **Founding.** Start the lobby with one city and allow more as activity grows. The server aims for roughly 25 cities per 100 online players, with room to adjust. A founder gets 48 in-game months. Later founding opportunities depend on the time since the player last founded a city and the server's capacity. Players can seek an existing city when founding is unavailable.
- **Finding a city.** The city browser shows each city's status, how its next governor will be chosen, and any known election timing. In a democracy, a city opens for campaigning once its governor finishes the 48-month term. Players can enter up to 10 campaigns at a time and withdraw whenever they want. Random matchmaking groups players who are willing to compete for an available city.
- **Keeping cities.** As activity falls, the lobby can slow new city creation. Deleting an existing city requires its founder and a majority of its past governors to agree.

## Governing and handing off

In office, players use Civic Ledger's normal simulation controls. They can pause, advance one month at a time, or run a full 48-month term in seconds at maximum speed. An elected governor can seek up to three terms in the same city under the proposed democratic rules and must campaign in each election.

A separate real-world deadline limits how long a player holds a city. A new city might allow up to a month. An established city that many people want to govern might allow as little as two hours. The winner has a shorter window to open the city and still faces a deadline after opening it. On expiry, the city returns to matchmaking. The lobby keeps the city and its history.

## Live elections

In the default democracy, the election is a live multiplayer match over a replay of the outgoing governor's 48 months. The historical city follows the decisions that governor actually made. At a normal speed of one minute per simulated month, the race takes 48 minutes. Players can propose speeding up or slowing down the shared clock. Options for pausing include a vote or a brief pause before someone can resume.

Candidates set out a platform and make promises about what they will do if elected. As the replay unfolds, they campaign against other players and respond to what happened in the city. For example, a replay might show unemployment falling while rents rise. Candidates can argue for keeping the policies that brought jobs back or for changing course on housing. Lobbying is part of the concept, with its role in campaigns and in office still open.

The city's simulated voters weigh campaign performance, the proposed policies, and local priorities such as jobs, housing, infrastructure, or taxes. The winner takes over at the point the outgoing governor actually reached.

## Characters and trust

Players keep a roster of characters and choose who to run. Each character has their own stats, age, and political record. Campaigning and governing age the character. A player might spend a character's early career governing smaller cities and build a reputation strong enough to compete for a sought-after city at age 60. A fresh face can win on their platform and campaign, and can be a better choice than a character with a poor record on the issues that matter locally.

- **The character's record.** A rolling, issue-specific record compares promises with results. Success on employment travels with the character. A history of missed housing promises can hurt in a city where housing is a priority. Later results can rebuild trust.
- **The city's confidence in that record.** Each city learns how reliably candidates' records have predicted results there. A city that has repeatedly elected highly rated governors who came up short might give past ratings less weight. A city where candidates with modest records have delivered might be more willing to give them another chance.

One balance goal is for a standout term to give a character a credible shot at a competitive city. A longer career can provide evidence across several issues. The value of each result depends on what happened in office and what the next city needs.

Lifespan is open. One option is a guaranteed minimum age followed by a variable lifespan within a known range, with advance notice when the remaining time might cut a term short. Any age thresholds are for playtesting.

## Changing the political system

A governor can try to change the city's rules for governing and succession. How far they can go in a single term depends on the policies already in place and how hard they are to amend. A rigid system might take several reforms across several terms. Later governors can continue, reverse, or build on earlier reforms through the rules they inherit.

Under the right conditions, a character could use several terms to establish a dictatorship. The character can govern for the rest of their life. The player can set succession rules within the powers that system gives them. When the character dies, the city always returns to matchmaking. Its political system determines how the next governor is picked. A city might hold a shorter campaign covering 10 months, use a lottery, or give candidates a challenge to complete for the right to govern. A 48-month campaign is the default democratic format. The next governor can try to change the succession rules again.

## Reputation and staged recoveries

A player could deliberately damage a city with one character and repair it with another. Two players could trade the same favor. Scoring from each handoff alone would give a large reward to whoever happens to be in office when the damage is reversed.

One proposed approach is to give the city a record of where each decline started and how long the problem persisted. If unemployment quickly goes from 4% to 12% and the next governor gets it back to 5% by reversing a recent policy, local voters can value the recovery and credit a kept promise. The gain in reputation that travels to other cities would be smaller. Bringing unemployment down from 12% after years of stagnation and keeping it down could earn substantially more. This rule would follow the city's history across handoffs between players.

## Open design decisions

- How to schedule overlapping live campaigns, handle multiple wins, and resume matchmaking after a deadline expires mid-term.
- How founding eligibility, matchmaking, and the real-world deadlines respond to capacity, city age, and player demand.
- Whether the three-term cap follows the character or the player, and whether it counts consecutive or lifetime terms in a city.
- Which campaign actions, lobbying mechanics, and shared clock controls make a full live race worth playing.
- How regular and shortened campaigns age characters, how much lifespan information players see, and how long a character can govern under alternative systems.
- Which changes a constitution can permit, how alternate succession formats work, and how voters and reputation scoring distinguish durable results from short-lived improvements.
