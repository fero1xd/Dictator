package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.types.Logging;
import me.fero.dictator.utils.MessagingUtils;
import me.fero.dictator.utils.ModerationUtils;
import me.fero.dictator.utils.ModeratorActions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Ban extends ModbaseCommand{
    public Ban() {
        this.name = "ban";
        this.help = "Bans a member from then server";
        this.requiredArgs = true;
        this.usage = "@nooblance";
        this.botPermissions = List.of(Permission.BAN_MEMBERS);
        this.cooldown = 10;
    }


    @Override
    public void execute(@NotNull CommandContext ctx) {
        Member selfMember = ctx.getSelfMember();
        Member member = ctx.getMember();

        User user = null;
        if(ctx.getMessage().getMentionedMembers().isEmpty()) {

            user = ModerationUtils.getUserById(ctx);
            if(user == null) {
                ModerationUtils.noMentionFoundEmbed(ctx, "ban");
                return;
            }
        }

        String reason = ModerationUtils.parseReason(ctx);

        if(user != null) {
            Member memberById = ctx.getGuild().getMemberById(user.getId());

            if(memberById != null && !ModerationUtils.canIntercat(member, memberById, selfMember, ctx, "ban", true)) return;
            banUser(user, ctx);
            return;
        }


        Member memberToBan = ctx.getMessage().getMentionedMembers().get(0);
        if(!ModerationUtils.canIntercat(member, memberToBan, selfMember, ctx, "ban", true)) return;

        ModeratorActions.banMember(ctx, memberToBan, reason, true, true, false);
    }

    void banUser(User userToBan, CommandContext ctx) {
        String reason = ModerationUtils.parseReason(ctx);
        String dmText = "You were banned from "  + ctx.getGuild().getName() + ". Reason - " + (reason != null ? reason : "N/A");
        MessagingUtils.sendDm(null, userToBan, dmText);


        ctx.getGuild().ban(userToBan.getId(), 1, String.format("%#s: %s", ctx.getAuthor(), reason))
                .reason(String.format("%#s: %s", ctx.getAuthor(), reason))
                .queue((__) -> {
                    ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, null, userToBan, "banned", reason != null ? reason : "Not given"), Logging.BAN_LOG);
                    ModerationUtils.successEmbed(ctx, "banned", null, userToBan);
                });
    }
}
