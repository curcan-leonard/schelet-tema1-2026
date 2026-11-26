package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;

final class ImportCommand {
    private ImportCommand() {
    }

    static void processImport(final CommandInput command, final SimulationClock clock,
                              final MarketDataRepository repository, final ArrayNode output) {
        if (clock.valid()) {
            DataCommand.processData(command, repository, output, false);
        }
    }
}
