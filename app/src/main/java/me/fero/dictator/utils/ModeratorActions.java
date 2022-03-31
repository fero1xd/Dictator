package me.fero.dictator.utils;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.redis.RedisManager;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.MongoDBFieldTypes;
import me.fero.dictator.types.Variables;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

public class ModeratorActions {
    public static Boolean kickMember(CommandContext ctx, Member memberToKick, String reason, Boolean logCurrentChannel, Boolean sendErrors, Boolean isAutomatic) {
        if(!ModerationUtils.canIntercat(ctx.getMember(), memberToKick, ctx.getSelfMember(), ctx, "kick", sendErrors)) return false;

        final AuditableRestAction<Void> kickAction = ctx.getGuild()
                .kick(memberToKick)
                .reason("Kicked by " + ctx.getAuthor().getAsTag());

        if(reason != null) kickAction.reason("Kicked by " + String.format("%#s: %s", ctx.getAuthor(), reason));

        kickAction.queue((__) -> {
            ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, memberToKick, null, "kicked", reason != null ? reason : "Not Available"),
                    Logging.KICK_LOG);
            if(logCurrentChannel) {
                ModerationUtils.successEmbed(ctx, "kicked " + (isAutomatic ? "for exceeding the threshold" : ""), memberToKick, null);
            }
        });
        return true;
    }

    public static Boolean muteMember(CommandContext ctx, Member target, String reason, Boolean logCurrentChannel, Boolean sendErrors, Boolean isAutomatic) {
        Role muteRole = ModerationUtils.getRole(ctx, "mute", Variables.MUTE_ROLE_ID, sendErrors);
        if(muteRole == null) return false;

        if(target.getRoles().contains(muteRole)) {
            if(sendErrors) {
                ModerationUtils.alreadyDone(ctx, "muted", target);
            }
            return false;
        }

        if(!ModerationUtils.canIntercat(ctx.getMember(), target, ctx.getSelfMember(), ctx, "mute", sendErrors)) {
            return false;
        }

        ctx.getGuild().addRoleToMember(target, muteRole).queue();

        // Add mutes to db
        String key = ctx.getGuild().getId() + "-" + target.getId();
        long idLong = ctx.getGuild().getIdLong();
        RedisManager.INSTANCE.addItemToList(idLong, MongoDBFieldTypes.MUTES_FIELD, key);
        MongoDBManager.INSTANCE.addItemToList(idLong, MongoDBFieldTypes.MUTES_FIELD, key);

        ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, target, null, "muted", reason), Logging.MUTE_LOG);
        if(logCurrentChannel) {
            ModerationUtils.successEmbed(ctx, "muted "  + (isAutomatic ? "for exceeding the threshold" : "" ), target, null);
        }
        return true;
    }

    public static Boolean banMember(CommandContext ctx, Member memberToBan, String reason, Boolean logCurrentChannel, Boolean sendErrors, Boolean isAutomatic) {
        if(!ModerationUtils.canIntercat(ctx.getMember(), memberToBan, ctx.getSelfMember(), ctx, "ban", sendErrors)) return false;

//        String reason = ModerationUtils.parseReason(ctx);
        User userToBan = memberToBan.getUser();
        String dmText = "You were banned from "  + ctx.getGuild().getName() + ". Reason - " + (reason != null ? reason : "N/A");
        MessagingUtils.sendDm(null, userToBan, dmText);

        ctx.getGuild().ban(userToBan.getId(), 1, String.format("%#s: %s", ctx.getAuthor(), reason))
                .reason(String.format("%#s: %s", ctx.getAuthor(), reason))
                .queue();

        ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, null, userToBan, "banned", reason != null ? reason : "Not given"), Logging.BAN_LOG);
        if(logCurrentChannel) ModerationUtils.successEmbed(ctx, "banned " + (isAutomatic ? "for exceeding the threshold" : "" ), null, userToBan);
        return true;
    }
}
