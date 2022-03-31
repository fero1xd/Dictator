package me.fero.dictator.commands.setup;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.types.CommandCategory;

import java.util.List;

public interface ICommand {
    void handle(CommandContext ctx);

    String getName();
    String getHelp();
    List<String> getAliases();

    int cooldownInSeconds();

    CommandCategory getCategory();
}
