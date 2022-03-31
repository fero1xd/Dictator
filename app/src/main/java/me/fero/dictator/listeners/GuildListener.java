package me.fero.dictator.listeners;

import me.fero.dictator.commands.setup.CommandManager;
import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.entities.GuildModel;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.redis.RedisManager;
import me.fero.dictator.types.Variables;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class GuildListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildListener.class);
    private final CommandManager commandManager;


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} has logged in", event.getJDA().getSelfUser().getAsTag());
        event.getJDA().getPresence().setActivity(Activity.watching("for bad people to punish"));
    }
    public GuildListener() {
        this.commandManager = new CommandManager();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        GuildModel guildModel = RedisManager.INSTANCE.getGuildModel(event.getGuild().getIdLong());
        String key = event.getGuild().getId() + "-" + member.getId();
        Boolean hasMute = guildModel.hasMute(key);

        if(hasMute) {
            RedissonClient redisson = RedisManager.INSTANCE.getRedisson();
            Guild guild = event.getGuild();
            Long guildId = guild.getIdLong();
            RBucket<Object> bucket = redisson.getBucket(guild.getId());

            GuildModel guildSettings = (GuildModel) bucket.get();
            if(guildSettings == null) return;

            if(!guildSettings.hasMute(key)) return;
            String roleId = guildSettings.getVariable(Variables.MUTE_ROLE_ID);
            if(roleId == null) return;

            Role roleById = guild.getRoleById(roleId);
            if(roleById == null) {
                RedisDataStore.INSTANCE.setVariable(guildId, Variables.MUTE_ROLE_ID, null);
                MongoDBManager.INSTANCE.setVariable(guildId, Variables.MUTE_ROLE_ID, null);
                return;
            }


            guild.addRoleToMember(event.getMember(), roleById).queue();
            LOGGER.info("Added mute role to a member because they were already muted");
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        List<Role> roles = event.getRoles();
        RedissonClient redisson = RedisManager.INSTANCE.getRedisson();
        Guild guild = event.getGuild();
        Long guildId = guild.getIdLong();
        RBucket<Object> bucket = redisson.getBucket(guild.getId());

        GuildModel guildSettings = (GuildModel) bucket.get();
        if(guildSettings == null) return;


        String roleId = guildSettings.getVariable(Variables.MUTE_ROLE_ID);
        if(roleId == null) return;

        Role roleById = guild.getRoleById(roleId);
        if(roleById == null) {
            RedisDataStore.INSTANCE.setVariable(guildId, Variables.MUTE_ROLE_ID, null);
            MongoDBManager.INSTANCE.setVariable(guildId, Variables.MUTE_ROLE_ID, null);
            return;
        }

        if(!roles.contains(roleById)) return;


        String key = guild.getId() + "-" + event.getMember().getId();
        guildSettings.removeMute(key);
        MongoDBManager.INSTANCE.removeMuteFromList(guildId, key);

        bucket.set(guildSettings);
        LOGGER.info("Removed mute key from redis and db");

    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        User author = event.getAuthor();
        if(author.isBot()) return;

        Long guildId = event.getGuild().getIdLong();
        String prefix = RedisDataStore.INSTANCE.getPrefix(guildId);
        String contentRaw = event.getMessage().getContentRaw();

        User selfUser = event.getGuild().getSelfMember().getUser();
        String mention = "<@!" + selfUser.getId() + ">";



        if(contentRaw.equalsIgnoreCase(mention)) {
            event.getChannel().sendMessageEmbeds(Embeds.createBuilder(null, "My prefix is `" + prefix + "`", null, null, null).build()).queue();
            return;
        }


        if(contentRaw.startsWith(prefix)) {

            commandManager.handle(event, prefix);
        }
    }
}
