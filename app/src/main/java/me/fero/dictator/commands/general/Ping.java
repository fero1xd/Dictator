package me.fero.dictator.commands.general;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.types.CommandCategory;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

public class Ping extends Command {
    public Ping() {
        this.name = "ping";
        this.help = "Replies with pong";
        this.category = CommandCategory.GENERAL;
        this.cooldown = 5;
    }


    @Override
    public void execute(@NotNull CommandContext ctx) {
        JDA jda = ctx.getJDA();
        String prefix = RedisDataStore.INSTANCE.getPrefix(ctx.getGuild().getIdLong());
        jda.getRestPing().queue(
                (__) -> {
                    EmbedBuilder builder = Embeds.createBuilder("Pong!", null, "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null);
                    builder.addField("Current ping", "`" + jda.getGatewayPing() + " ms" + "`", false);
                    builder.setThumbnail(ctx.getSelfMember().getEffectiveAvatarUrl());
                    builder.addField("Prefix", "`" + prefix + "`", false);
                    builder.addField("Serving", "`" + ctx.getJDA().getGuilds().size() + " Guilds" + "`", false);
                    ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
                }
        );
    }
}
