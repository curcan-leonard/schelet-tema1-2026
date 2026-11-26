package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;
import fileio.InputLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Main {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final ObjectWriter WRITER = MAPPER.writer().withDefaultPrettyPrinter();

    private Main() {
    }

    public static void action(final String inputPath,
                              final String outputPath) throws IOException {
        InputLoader inputLoader = new InputLoader(inputPath);
        ArrayNode output = MAPPER.createArrayNode();
        MarketDataRepository repository = new MarketDataRepository();
        SimulationClock clock = new SimulationClock();

        for (CommandInput command : inputLoader.getCommands()) {
            clock.advance(command.getTimestamp());
            switch (command.getCommand()) {
                case "startImport" -> {
                    clock.startImport();
                    repository.ensureDay(clock.getDate());
                }
                case "stopImport" -> clock.stopImport();
                case "import" -> ImportCommand.processImport(command, clock, repository, output);
                case "patch" -> PatchCommand.processPatch(command, repository, output);
                case "get" -> output.add(GetCommand.processGet(MAPPER, repository, command));
                default -> throw new IllegalArgumentException("Unknown command "
                        + command.getCommand());
            }
        }

        File outputFile = new File(outputPath);
        Files.createDirectories(outputFile.toPath().toAbsolutePath().getParent());
        WRITER.writeValue(outputFile, output);
    }
}
