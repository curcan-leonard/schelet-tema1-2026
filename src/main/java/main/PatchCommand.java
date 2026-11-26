package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;

final class PatchCommand {
    private PatchCommand() {
    }

    static void processPatch(final CommandInput command, final MarketDataRepository repository,
                             final ArrayNode output) {
        DataCommand.processData(command, repository, output, true);
    }
}
