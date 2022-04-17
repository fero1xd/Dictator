package me.fero.dictator.commands.general;

import me.fero.dictator.commands.setup.CommandManager;
import me.fero.dictator.commands.setup.ICommand;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import me.fero.dictator.redis.RedisManager;
import me.fero.dictator.types.CommandCategory;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Help extends GeneralbaseCommand {
    private final CommandManager manager;

    public Help(CommandManager manager) {
        this.name = "help";
        this.help = "Gets the list of command";
        this.manager = manager;
    }


    @Override
    public void execute(@NotNull CommandContext ctx) {
        HashMap<String, ICommand> commands = this.manager.getCommands();

        EmbedBuilder builder = Embeds.createBuilder("Help Menu 💁‍♂", null, "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null);
//        StringBuilder builder = new StringBuilder();
        String prefix = RedisManager.INSTANCE.getPrefix(ctx.getGuild().getIdLong());



        StringBuilder moderatorBuilder = new StringBuilder();
        StringBuilder managerBuilder = new StringBuilder();
        StringBuilder generalBuilder = new StringBuilder();

        for(Map.Entry<String, ICommand> entry : commands.entrySet()) {
            ICommand cmd = entry.getValue();
            if(entry.getValue().getCategory().equals(CommandCategory.MODERATION)) {
                moderatorBuilder.append("`").append(prefix).append(cmd.getName()).append("` ").append(cmd.getHelp()).append("\n");
            }
            else if(entry.getValue().getCategory().equals(CommandCategory.MANAGER)) {
                managerBuilder.append("`").append(prefix).append(cmd.getName()).append("` ").append(cmd.getHelp()).append("\n");
            }
            else if(entry.getValue().getCategory().equals(CommandCategory.GENERAL)) {
                generalBuilder.append("`").append(prefix).append(cmd.getName()).append("` ").append(cmd.getHelp()).append("\n");
            }

        }

        builder.addField("Moderation 📳", moderatorBuilder.toString(), false);
        builder.addField("Manager 👩‍", managerBuilder.toString(), false);
        builder.addField("General 🧬", generalBuilder.toString(), false);

        ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
