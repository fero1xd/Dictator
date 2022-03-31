package me.fero.dictator.commands.essentials;

import me.fero.dictator.commands.setup.CommandManager;
import me.fero.dictator.commands.setup.ICommand;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Help extends Command {
    private final CommandManager manager;

    public Help(CommandManager manager) {
        this.name = "help";
        this.help = "Gets the list of command";
        this.manager = manager;
    }


    @Override
    public void execute(@NotNull CommandContext ctx) {
        HashMap<String, ICommand> commands = this.manager.getCommands();

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(Map.Entry<String, ICommand> entry : commands.entrySet()) {
            builder.append(i).append(". ").append(entry.getKey()).append("\n");
            i++;
        }

        ctx.getChannel().sendMessage(builder.toString()).queue();
    }
}
