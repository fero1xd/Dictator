package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.objects.Command;
import me.fero.dictator.types.CommandCategory;
import me.fero.dictator.utils.Embeds;
import me.fero.dictator.utils.MessagingUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Purge extends Command {

    public Purge() {
        this.name = "purge";
        this.help = "deletes messages";
        this.usage = "<amount>";
        this.requiredArgs = true;
        this.aliases = List.of("clear");
        this.category = CommandCategory.MODERATION;
        this.botPermissions = List.of(Permission.MESSAGE_MANAGE);
        this.userPermissions = List.of(Permission.MESSAGE_MANAGE);

    }

    @Override
    public void execute(@NotNull CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        int amount = MessagingUtils.parseInt(ctx.getArgs().get(0));
        if(amount < 0) {
            channel.sendMessageEmbeds(Embeds.createBuilder("Error!", "Please specify the correct amount of messages", null, null, null).build()).queue();
            return;
        }
        ctx.getMessage().addReaction("✔").queue();

        List<Message> msgs = channel.getHistory().retrievePast(amount).complete();

        channel.deleteMessages(msgs).queue();
    }
}
