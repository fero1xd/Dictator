package me.fero.dictator.commands.essentials.fun;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerAvatar extends Command {

    public ServerAvatar(){
        this.name = "avatar";
        this.help = "Gets you the avatar of a member";
        this.aliases = List.of("av");

    }
    @Override
    public void execute(@NotNull CommandContext ctx) {
        Member target = ctx.getMember();
        if(ctx.getMessage().getMentionedMembers().size() > 0) {
            target = ctx.getMessage().getMentionedMembers().get(0);
        }

        EmbedBuilder builder = Embeds.createBuilder(null, null,
                null, null, null).setImage(target.getEffectiveAvatarUrl()).setAuthor(target.getEffectiveName() + "'s avatar");

        ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
