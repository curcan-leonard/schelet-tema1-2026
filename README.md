# Energex-Lite

The application simulates importing and querying electricity market data. It
reads commands from a JSON file and writes the responses to a single JSON array.

## How I organized the data

I started with the abstract class `EnergyPoint`, which stores the point type,
date, interval, and index. Each of the six subclasses has its own fields and
validation rules. `class_create` chooses the right class based on the input type.

In `MarketDataRepository`, I use a `HashMap` for each data type. The key is the
date, and the value is the document for that day. Each document has 96 slots,
one for each quarter hour. The interval index starts at 1, so the position in
the list is `index - 1`. An empty slot means the point is unavailable.

## How `get` works

- For an exact time, I look up the day in the `HashMap`, calculate the index
  from the hour and minute, and read the point at that position.
- For `LATEST`, I go through the days and scan each day's intervals backwards.
  I skip empty days and keep the most recent point I find. I calculate `from`
  and `to` from that point's interval.
- For a range with `start` and `end`, I use `LocalDateTime` to move forward in
  15-minute steps. It moves to the next day at midnight. I include the points
  found before `end` and group them by day in the response.

The response status is `SUCCESS` when I find data, `WARNING` when no points are
available, and `ERROR` when the subscription does not allow access. The
`timestamp` field stores the command time, while `from` and `to` describe the
requested period.

## Reading commands and applying updates

In `Main.action`, I read the file with `InputLoader` and Jackson, then process
the commands in file order. `SimulationClock` takes the time of each command,
and `startImport` and `stopImport` control when imports are accepted.

For `import`, I validate each point and store valid values. An invalid point
produces a `WARNING` and clears its slot. For `patch`, a valid value fills an
empty slot or replaces the stored point. An invalid patch leaves the stored
value in place. I can apply a `patch` after `stopImport`. For `GRID_LOAD`, I also
check the difference from the previous quarter hour if that point is available.

## AI assistance

I used AI assistance on;

  README documentation: Helped structure, write, and improve the project's README file.

  Java syntax: Helped clarify and correct Java syntax for certain parts of the implementation.

  Code modularization: Helped the files structure.

AI was used as a support tool, while the implementation and final decisions were reviewed and adapted by me.