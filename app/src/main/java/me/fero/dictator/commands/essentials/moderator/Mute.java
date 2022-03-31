package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.redis.RedisManager;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.MongoDBFieldTypes;
import me.fero.dictator.types.Variables;
import me.fero.dictator.utils.MessagingUtils;
import me.fero.dictator.utils.ModerationUtils;
import me.fero.dictator.utils.ModeratorActions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Mute extends ModbaseCommand {

    public Mute() {
        this.name = "mute";
        this.help = "Mutes a user from the server";
        this.requiredArgs = true;
        this.usage = "<mention> (<duration>) (<reason>)";
    }

    @Override
    public void execute(@NotNull CommandContext ctx) {
        Member member = ctx.getMember();
        if(ctx.getMessage().getMentionedMembers().isEmpty()) {
            ModerationUtils.noMentionFoundEmbed(ctx, "mute");
            return;
        }

        Member target = ctx.getMessage().getMentionedMembers().get(0);
        Member selfMember = ctx.getSelfMember();


        if(!ModerationUtils.canIntercat(member, target, selfMember, ctx, "mute", true)) return;


        if(ctx.getMessage().getMentionedMembers().isEmpty()) {
            ModerationUtils.noMentionFoundEmbed(ctx, "mute");
            return;
        }


        Role muteRole = ModerationUtils.getRole(ctx, "mute", Variables.MUTE_ROLE_ID, true);
        if(muteRole == null) return;


        String reason = "Not available";
        if(ctx.getArgs().size() > 2) {
            reason = String.join(" ", ctx.getArgs().subList(2, ctx.getArgs().size()));
        }

        ModeratorActions.muteMember(ctx, target, reason, true, true, false);

        List<String> args = ctx.getArgs();

        if(args.size() > 1) {

            int i = MessagingUtils.parseTime(args.get(1));
            // i is the error code .. if any

            if(i < 0) return;

            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, target, null, "unmuted", "They were muted for a specific time"), Logging.UNMUTE_LOG);
                                ctx.getGuild().removeRoleFromMember(target, muteRole).queue();
                                String key = ctx.getGuild().getId() + "-" + target.getId();
                                RedisManager.INSTANCE.removeItemFromList(ctx.getGuild().getIdLong(), MongoDBFieldTypes.MUTES_FIELD, key);
                                MongoDBManager.INSTANCE.removeItemFromList(ctx.getGuild().getIdLong(), MongoDBFieldTypes.MUTES_FIELD, key);

                            } catch(Exception e) {
                                System.out.println("Error while removing mute role");
                            }
                        }
                    },
                    i * 1000L
            );
        }
    }
}
