package me.fero.dictator.utils;


import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.entities.GuildModel;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.Variables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;


import java.util.List;

public class ModerationUtils {
    public static boolean canIntercat(Member mod, Member target, Member self, CommandContext ctx, String action, Boolean sendErrors) {
        if(!mod.canInteract(target)) {
            if(sendErrors) notEnoughPermissionsEmbed(ctx, List.of("You", action));
            return false;
        }

        if(!self.canInteract(target)) {
            if(sendErrors) notEnoughPermissionsEmbed(ctx, List.of("I", action));
            return false;
        }

        return true;
    }

    public static void logModChannel(CommandContext ctx, EmbedBuilder embed, String action) {
        long idLong = ctx.getGuild().getIdLong();
        GuildModel guildModel = RedisDataStore.INSTANCE.getGuildModel(idLong);
        String modLogChannelId = guildModel.getVariable(Variables.LOG_CHANNEL);
        if(modLogChannelId == null) return;

        if(!guildModel.getStateOfLogging(action)) {
            return;
        }


        TextChannel textChannelById = ctx.getGuild().getTextChannelById(modLogChannelId);
        if(modLogChannelId.equals(ctx.getChannel().getId())) return;

        if(textChannelById == null) {
            RedisDataStore.INSTANCE.setVariable(idLong, Variables.LOG_CHANNEL, null);
            MongoDBManager.INSTANCE.setVariable(idLong, Variables.LOG_CHANNEL, null);
            return;
        }

        textChannelById.sendMessageEmbeds(embed.build()).queue();
    }


    public static Boolean checkThresholdReached(CommandContext ctx, Member target, String action, Integer amount) {
        long guildId = ctx.getGuild().getIdLong();
        GuildModel guildModel = RedisDataStore.INSTANCE.getGuildModel(guildId);
        if(guildModel.getVariable(action) == null || Integer.parseInt(guildModel.getVariable(action)) == 0) return false;

        Integer threshold = Integer.parseInt(guildModel.getVariable(action));
        if(amount < threshold) return false;

        String actionToTake = guildModel.getVariable(action + "_action");
        if(actionToTake == null) return  false;

        String name = action.substring(0, action.indexOf("_")).toLowerCase();
        if(actionToTake.equalsIgnoreCase(Variables.ACTION_MUTE)) {
            return ModeratorActions.muteMember(ctx, target, "Maximum " + name + " threshold reached", true, false, true);
        }
        else if(actionToTake.equalsIgnoreCase(Variables.ACTION_KICK)) {
            return ModeratorActions.kickMember(ctx, target, "Maximum " + name + " threshold reached", true, false, true);
        }
        else if(actionToTake.equalsIgnoreCase(Variables.ACTION_BAN)) {
            return ModeratorActions.banMember(ctx, target, "Maximum " + name + " threshold reached", true, false, true);
        }
        return false;
    }


    public static Role getRole(CommandContext ctx, String word, String action, Boolean sendErrors) {
        String roleId = getVariableFromGuildModel(ctx, action);
        if(roleId == null) {
            if(sendErrors) roleNotFound(ctx, word);
            return null;
        }

        Role role = ctx.getGuild().getRoleById(roleId);
        if(role == null) {
            long idLong = ctx.getGuild().getIdLong();
            RedisDataStore.INSTANCE.setVariable(idLong, action, null);
            MongoDBManager.INSTANCE.setVariable(idLong, action, null);
            if(sendErrors) roleNotFound(ctx, word);
            return null;
        }
        return role;
    }

    public static EmbedBuilder successModerationLog(CommandContext ctx, Member target, User user, String word, String reason) {
        return Embeds.createBuilder("Moderation Log!", "**:white_check_mark: " + (target != null ? target.getUser().getAsTag() : user.getAsTag()) + " was " + word + "!**",
                "Commanded by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).addField("Reason", reason, false);
    }

    public static String getVariableFromGuildModel(CommandContext ctx, String variable) {
        long guildId = ctx.getGuild().getIdLong();
        GuildModel guildModel = RedisDataStore.INSTANCE.getGuildModel(guildId);
        return guildModel.getVariable(variable);
    }

    public static void successEmbed(CommandContext ctx, String word, Member target, User targetUser) {
        ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Success!", "**:white_check_mark: " + (target != null ? target.getUser().getAsTag() : targetUser.getAsTag()) + " was " + word + "!**",
                "Commanded by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).build()).queue();
    }

    public static void notEnoughPermissionsEmbed(CommandContext ctx, List<String> words) {
        ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Error!", "**:x: " + words.get(0) + " cannot " + words.get(1) + " that member**",
                "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).build()).queue();
    }

    public static void roleNotFound(CommandContext ctx, String word) {
        ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Error!", "**:x: No " + word + " role setup **",
                "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).build()).queue();
    }

    public static void alreadyDone(CommandContext ctx, String word, Member target) {
        ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Error!", "**:x: " + target.getUser().getAsTag() + " is already " + word + "**",
                "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).build()).queue();
    }

    public static void noMentionFoundEmbed(CommandContext ctx, String word) {
        ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Error!", "**:x: Mention a member to " + word + "**",
                "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).build()).queue();
    }

    public static void noRoleMentionFoundEmbed(CommandContext ctx) {
        ctx.getChannel().sendMessageEmbeds(Embeds.createBuilder("Error!", "**:x: Mention a role to give **",
                "Requested by " + ctx.getMember().getEffectiveName(), ctx.getMember().getEffectiveAvatarUrl(), null).build()).queue();
    }


    public static String parseReason(CommandContext ctx) {
        if(ctx.getArgs().size() > 1) {
            return String.join(" ", ctx.getArgs().subList(1, ctx.getArgs().size()));
        }
        return null;
    }

    public static User getUserById(CommandContext ctx) {

        try {
            return ctx.getJDA().retrieveUserById(ctx.getArgs().get(0)).complete();
        } catch (Exception e) {
            return null;
        }

    }

}
