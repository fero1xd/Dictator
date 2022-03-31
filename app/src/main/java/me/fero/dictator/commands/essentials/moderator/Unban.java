package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.types.Logging;
import me.fero.dictator.utils.Embeds;
import me.fero.dictator.utils.ModerationUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Unban extends ModbaseCommand{

    public Unban() {
        this.name = "unban";
        this.help = "Unbans a member from the server";
        this.requiredArgs = true;
        this.usage = "<user_id>";
        this.botPermissions = List.of(Permission.BAN_MEMBERS);
        this.cooldown = 10;
    }

    @Override
    public void execute(@NotNull CommandContext ctx) {
        String argsJoined = String.join(" ", ctx.getArgs());
        TextChannel channel = ctx.getChannel();

        ctx.getGuild().retrieveBanList().queue((list) -> {
            for (final Guild.Ban ban : list) {
                final User bannedUser = ban.getUser();
                final String userFormatted = bannedUser.getAsTag();


                if (bannedUser.getName().equalsIgnoreCase(argsJoined) || bannedUser.getId().equals(argsJoined) ||
                        userFormatted.equalsIgnoreCase(argsJoined)) {


                    ctx.getGuild().unban(bannedUser)
                            .reason(null)
                            .queue();

                    ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, null, bannedUser, "unbanned", "Not available"), Logging.UNBAN_LOG);
                    ModerationUtils.successEmbed(ctx, "unbanned", null, bannedUser);
                    return;
                }
            }

            channel.sendMessageEmbeds(Embeds.createBuilder("Error!", "**:x: No user found**", null, null, null).build()).queue();
        });
    }
}
