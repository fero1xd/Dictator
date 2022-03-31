package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.utils.MessagingUtils;
import me.fero.dictator.utils.ModerationUtils;
import me.fero.dictator.utils.ModeratorActions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Kick extends ModbaseCommand{
    public Kick() {
        this.name = "kick";
        this.help = "Kicks a member from the server";
        this.requiredArgs = true;
        this.usage = "@nooblance <reason>";
        this.botPermissions = List.of(Permission.KICK_MEMBERS);
        this.cooldown = 10;
    }

    @Override
    public void execute(@NotNull CommandContext ctx) {

        if(ctx.getMessage().getMentionedMembers().isEmpty()) {
            ModerationUtils.noMentionFoundEmbed(ctx, "kick");
            return;
        }

        Member memberToKick = ctx.getMessage().getMentionedMembers().get(0);
        Member selfMember = ctx.getSelfMember();


        if(!ModerationUtils.canIntercat(ctx.getMember(), memberToKick, selfMember, ctx, "kick", true)) return;


        String reason = ModerationUtils.parseReason(ctx);


        String dmText = "You were kicked from "  + ctx.getGuild().getName() + ". Reason - " + (reason != null ? reason : "N/A");
        MessagingUtils.sendDm(memberToKick, null,  dmText);

        ModeratorActions.kickMember(ctx, memberToKick, reason, true, true, false);
    }
}
