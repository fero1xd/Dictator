package me.fero.dictator.commands.setup;

import me.fero.dictator.commands.essentials.Help;
import me.fero.dictator.commands.essentials.fun.ServerAvatar;
import me.fero.dictator.commands.essentials.fun.UserAvatar;
import me.fero.dictator.commands.essentials.manager.AddRole;
import me.fero.dictator.commands.essentials.moderator.*;
import me.fero.dictator.commands.general.Ping;
import me.fero.dictator.commands.essentials.ChangePrefix;
import me.fero.dictator.commands.setup.context.CommandContext;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandManager {
    private final HashMap<String, ICommand> commands = new HashMap<>();
    private final HashMap<String, String> aliases = new HashMap<>();

    public CommandManager() {
        addCommand(new Ping());
        addCommand(new ChangePrefix());
        addCommand(new Kick());
        addCommand(new Ban());
        addCommand(new Unban());
        addCommand(new Mute());
        addCommand(new Unmute());
        addCommand(new Purge());
        addCommand(new AddRole());
        addCommand(new Warn());
        addCommand(new Lock());
        addCommand(new Unlock());
        addCommand(new Afk());

        addCommand(new ServerAvatar());
        addCommand(new UserAvatar());
        addCommand(new Help(this));
    }

    public HashMap<String, ICommand> getCommands() {
        return this.commands;
    }
    private void addCommand(ICommand cmd) {
        String name = cmd.getName();

        if (name.contains(" ")) {
            throw new IllegalArgumentException(" Name can't have spaces!");
        }

        final String cmdName = name.toLowerCase();

        if (this.commands.containsKey(cmdName)) {
            throw new IllegalArgumentException(String.format("Command %s already present", cmdName));
        }

        final List<String> lowerAliasses = cmd.getAliases().stream().map(String::toLowerCase).collect(Collectors.toList());

        if (!lowerAliasses.isEmpty()) {
            for (final String alias : lowerAliasses) {
                if (this.aliases.containsKey(alias)) {
                    throw new IllegalArgumentException(String.format(
                            "Alias %s already present (Stored for: %s, trying to insert: %s))",
                            alias,
                            this.aliases.get(alias),
                            name
                    ));
                }

                if (this.commands.containsKey(alias)) {
                    throw new IllegalArgumentException(String.format(
                            "Alias %s already present for command (Stored for: %s, trying to insert: %s))",
                            alias,
                            this.commands.get(alias).getClass().getSimpleName(),
                            cmd.getClass().getSimpleName()
                    ));
                }
            }

            for (final String alias : lowerAliasses) {
                this.aliases.put(alias, name);
            }
        }

        this.commands.put(cmdName, cmd);
    }



    @Nullable
    public ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        ICommand found = this.commands.get(searchLower);

        if (found == null) {
            final String forAlias = this.aliases.get(searchLower);

            if (forAlias != null) {
                found = this.commands.get(forAlias);
            }
        }

        return found;
    }

    public void handle(GuildMessageReceivedEvent event, String prefix) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);

        if(cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            event.getChannel().sendTyping().queue();
            cmd.handle(ctx);
        }
    }

}
