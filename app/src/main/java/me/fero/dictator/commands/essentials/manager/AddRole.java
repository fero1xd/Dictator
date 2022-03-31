package me.fero.dictator.commands.essentials.manager;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.Variables;
import me.fero.dictator.utils.Embeds;
import me.fero.dictator.utils.ModerationUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class AddRole extends ManagerBaseCommand {
    public AddRole() {
        this.name = "addrole";
        this.help = "Adds a given role to member";
        this.requiredArgs = true;
        this.requiredArgCount = 2;
        this.usage = "<role> <mention>";
    }

    @Override
    public void execute(@NotNull CommandContext ctx) {
        if(ctx.getMessage().getMentionedMembers().isEmpty()) {
            ModerationUtils.noMentionFoundEmbed(ctx, "add role");
            return;
        }
        if(ctx.getMessage().getMentionedRoles().isEmpty()) {
            ModerationUtils.noRoleMentionFoundEmbed(ctx);
            return;
        }

        Member target = ctx.getMessage().getMentionedMembers().get(0);
        Member member = ctx.getMember();
        Role role = ctx.getMessage().getMentionedRoles().get(0);

        if(!ModerationUtils.canIntercat(member, target, ctx.getSelfMember(), ctx, "manage", true)) return;

        if(!member.canInteract(role)) {
            ModerationUtils.notEnoughPermissionsEmbed(ctx, List.of("You", "give this role to"));
            return;

        }
        if(!ctx.getSelfMember().canInteract(role)) {
            ModerationUtils.notEnoughPermissionsEmbed(ctx, List.of("I", "give this role to"));
            return;
        }

        if(target.getRoles().contains(role)) {
            ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Error!", ":x: **They already have this role**", null, null, null).build()).queue();
            return;
        }
        ctx.getGuild().addRoleToMember(target, role).queue();


        ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, target, null, "given " + role.getName() + " role", "Not available"), Logging.ROLE_ADD_LOG);
        ModerationUtils.successEmbed(ctx, "given the role", target, null);
    }
}
