package me.fero.dictator.objects;

import me.fero.dictator.commands.setup.ICommand;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.config.Config;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.types.CommandCategory;
import me.fero.dictator.utils.Embeds;
import me.fero.dictator.utils.ModerationUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public abstract class Command implements ICommand {
    protected String name = null;
    protected String help = null;
    protected boolean requiredArgs = false;
    protected int requiredArgCount = 1;
    protected List<Permission> userPermissions = new ArrayList<>();
    protected List<Permission> botPermissions = new ArrayList<>();
    protected String usage = null;
    protected int cooldown = 0;
    protected CommandCategory category;
    protected List<String> aliases = new ArrayList<>();


    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        String prefix = RedisDataStore.INSTANCE.getPrefix(ctx.getGuild().getIdLong());

        if (this.userPermissions.size() > 0 && !ctx.getMember().hasPermission(channel, this.userPermissions)) {
            final String permissionsWord = "permission" + (this.userPermissions.size() > 1 ? "s" : "");
            channel.sendMessageEmbeds(Embeds.createBuilder("Error!", "Not enough " + permissionsWord, null, null, null).build()).queue();
            return;
        }

        if (this.botPermissions.size() > 0 && !ctx.getSelfMember().hasPermission(channel, this.botPermissions)) {
            final String permissionsWord = "permission" + (this.botPermissions.size() > 1 ? "s" : "");

            channel.sendMessageEmbeds(Embeds.createBuilder("Error!", "I do not have enough " + permissionsWord, null, null, null).build()).queue();
            return;
        }

        if (this.requiredArgs &&
                (ctx.getArgs().isEmpty() || ctx.getArgs().size() < this.requiredArgCount)
        ) {
            channel.sendMessageEmbeds(Embeds.createBuilder("Error!", "Correct usage is  " + getUsage(prefix), null, null, null).build()).queue();
            return;
        }

        execute(ctx);
    }

    public abstract void execute(@Nonnull CommandContext ctx);


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getHelp() {
        return this.help;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public int cooldownInSeconds() {
        return this.cooldown;
    }

    @Override
    public CommandCategory getCategory() {
        return this.category;
    }

    public String getUsage(String prefix) {
        return "`" + prefix + this.name + " " + this.usage + "`";
    }
}
