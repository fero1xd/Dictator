package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class Unlock extends ModbaseCommand {

    public Unlock() {
        this.name = "unlock";
        this.help = "Unlocks the mentioned channel or the current channel";
        this.usage = "<mention>";
        this.userPermissions = List.of(Permission.MESSAGE_MANAGE);
        this.botPermissions = List.of(Permission.MESSAGE_MANAGE);
    }


    @Override
    public void execute(@NotNull CommandContext ctx) {
        TextChannel channel = !ctx.getMessage().getMentionedChannels().isEmpty() ? ctx.getMessage().getMentionedChannels().get(0) : ctx.getChannel();
        TextChannel channel1 = ctx.getChannel();

        channel.getManager().putPermissionOverride(ctx.getGuild().getPublicRole(), EnumSet.of(Permission.MESSAGE_WRITE), null).queue();

        channel1.sendMessageEmbeds(Embeds.createBuilder("Success!", channel.getAsMention() + " was unlocked for everyone", null, null, null).build()).queue();
    }
}
