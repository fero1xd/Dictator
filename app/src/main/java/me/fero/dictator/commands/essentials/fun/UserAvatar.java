package me.fero.dictator.commands.essentials.fun;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAvatar extends Command {
    public UserAvatar(){
        this.name = "uavatar";
        this.help = "Gets you the avatar of a user";

        this.aliases = List.of("uav");

    }
    @Override
    public void execute(@NotNull CommandContext ctx) {
        Member target = ctx.getMember();
        if(ctx.getMessage().getMentionedMembers().size() > 0) {
            target = ctx.getMessage().getMentionedMembers().get(0);
        }

        EmbedBuilder builder = Embeds.createBuilder(null, null,
                null, null, null).setImage(target.getUser().getEffectiveAvatarUrl()).setAuthor(target.getUser().getName() + "'s avatar");
        ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
