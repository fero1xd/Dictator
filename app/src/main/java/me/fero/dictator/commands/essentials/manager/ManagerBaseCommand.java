package me.fero.dictator.commands.essentials.manager;

import me.fero.dictator.objects.Command;
import me.fero.dictator.types.CommandCategory;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public abstract class ManagerBaseCommand extends Command {
    public ManagerBaseCommand() {
        this.category = CommandCategory.MANAGER;
        this.userPermissions = List.of(Permission.MANAGE_SERVER);
    }
}
