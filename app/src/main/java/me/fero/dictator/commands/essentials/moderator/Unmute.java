package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.Variables;
import me.fero.dictator.utils.ModerationUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

public class Unmute extends ModbaseCommand{
    public Unmute() {
        this.name = "unmute";
        this.help = "Unmutes a user from the server";
        this.requiredArgs = true;
        this.usage = "<mention> <reason>";
    }

    @Override
    public void execute(@NotNull CommandContext ctx) {
        Member member = ctx.getMember();
        Member target = ModerationUtils.parseMember(ctx.getArgs().get(0), ctx.getGuild());


        if(target == null) {
            ModerationUtils.noMentionFoundEmbed(ctx, "unmute");
            return;
        }

        Member selfMember = ctx.getSelfMember();

        if(!ModerationUtils.canIntercat(member, target, selfMember, ctx, "unmute", true)) return;


        String muteRoleId = ModerationUtils.getVariableFromGuildModel(ctx, Variables.MUTE_ROLE_ID);
        if(muteRoleId == null) {
            ModerationUtils.roleNotFound(ctx, "mute");
            return;
        }

        Role muteRole = ctx.getGuild().getRoleById(muteRoleId);
        if(muteRole == null) {
            long idLong = ctx.getGuild().getIdLong();
            RedisDataStore.INSTANCE.setVariable(idLong, Variables.MUTE_ROLE_ID, null);
            MongoDBManager.INSTANCE.setVariable(idLong, Variables.MUTE_ROLE_ID, null);
            ModerationUtils.roleNotFound(ctx, "mute");
            return;
        }


        if(!target.getRoles().contains(muteRole)) {
            ModerationUtils.alreadyDone(ctx, "unmuted", target);
            return;
        }

        String reason = ModerationUtils.parseReason(ctx);
        ctx.getGuild().removeRoleFromMember(target, muteRole).queue((__) -> {
            ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, target, null, "unmuted", reason != null ? reason : "Not Available"), Logging.UNMUTE_LOG);
            ModerationUtils.successEmbed(ctx, "unmuted", target, null);
        });
    }
}
