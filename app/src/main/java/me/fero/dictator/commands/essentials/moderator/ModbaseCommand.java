package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.objects.Command;
import me.fero.dictator.types.CommandCategory;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public abstract class ModbaseCommand extends Command {
    public ModbaseCommand() {
        this.userPermissions = List.of(Permission.KICK_MEMBERS, Permission.BAN_MEMBERS);
        this.category = CommandCategory.MODERATION;

    }
}
