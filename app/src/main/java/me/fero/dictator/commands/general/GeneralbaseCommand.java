package me.fero.dictator.commands.general;

import me.fero.dictator.objects.Command;
import me.fero.dictator.types.CommandCategory;

public abstract class GeneralbaseCommand extends Command {
    public GeneralbaseCommand() {
        this.category = CommandCategory.GENERAL;
    }
}
