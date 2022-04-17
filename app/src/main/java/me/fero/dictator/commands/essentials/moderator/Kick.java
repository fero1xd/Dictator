package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.utils.MessagingUtils;
import me.fero.dictator.utils.ModerationUtils;
import me.fero.dictator.utils.ModeratorActions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String contentRaw = ctx.getArgs().get(0);

        Member memberToKick = ModerationUtils.parseMember(contentRaw, ctx.getGuild());

        if(memberToKick == null) {
            memberToKick = ModerationUtils.getMemberById(ctx);
            if(memberToKick == null) {
                ModerationUtils.noMentionFoundEmbed(ctx, "kick");
                return;
            }
        }

        Member selfMember = ctx.getSelfMember();


        if(!ModerationUtils.canIntercat(ctx.getMember(), memberToKick, selfMember, ctx, "kick", true)) return;


        String reason = ModerationUtils.parseReason(ctx);


        String dmText = "You were kicked from "  + ctx.getGuild().getName() + ". Reason - " + (reason != null ? reason : "N/A");
        MessagingUtils.sendDm(memberToKick, null,  dmText);

        ModeratorActions.kickMember(ctx, memberToKick, reason, true, true, false);
    }
}
