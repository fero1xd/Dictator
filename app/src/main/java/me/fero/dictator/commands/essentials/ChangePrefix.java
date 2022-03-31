package me.fero.dictator.commands.essentials;

import me.fero.dictator.database.DatabaseManager;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChangePrefix extends Command {

    public ChangePrefix() {
        this.name = "prefix";
        this.help = "Changes the bot prefix to the given one";
        this.requiredArgs = true;
        this.requiredArgCount = 1;
        this.userPermissions = List.of(Permission.MANAGE_SERVER);
        this.usage = "<new_prefix>";
        this.aliases = List.of("cp");
    }


    @Override
    public void execute(@NotNull CommandContext ctx) {
        String prefix = ctx.getArgs().get(0);
        TextChannel channel = ctx.getChannel();
        if(prefix.length() > 5) {
            channel.sendMessageEmbeds(Embeds.createBuilder("Error!", "You do not have manage server permission", null, null, null).build()).queue();
            return;
        }

        long guildId = ctx.getGuild().getIdLong();
        RedisDataStore.INSTANCE.setPrefix(guildId, prefix);
        DatabaseManager.INSTANCE.setPrefix(guildId, prefix);

        channel.sendMessageEmbeds(Embeds.createBuilder("Success!", "Prefix changed successfully to `" + prefix + "`", null, null, null).build()).queue();
    }
}
