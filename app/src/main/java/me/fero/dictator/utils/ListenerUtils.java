package me.fero.dictator.utils;

import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.entities.GuildModel;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.redis.RedisManager;
import me.fero.dictator.types.MongoDBFieldTypes;
import me.fero.dictator.types.Variables;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerUtils.class);

    public static boolean muteMemberOnRejoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        GuildModel guildModel = RedisManager.INSTANCE.getGuildModel(event.getGuild().getIdLong());
        String key = event.getGuild().getId() + "-" + member.getId();
        Boolean hasMute = guildModel.hasItemInList(MongoDBFieldTypes.MUTES_FIELD, key);

        if(hasMute) {
            RedissonClient redisson = RedisManager.INSTANCE.getRedisson();
            Guild guild = event.getGuild();
            Long guildId = guild.getIdLong();
            RBucket<Object> bucket = redisson.getBucket(guild.getId());

            GuildModel guildSettings = (GuildModel) bucket.get();
            if(guildSettings == null) return true;

            String roleId = guildSettings.getVariable(Variables.MUTE_ROLE_ID);
            if(roleId == null) return true;

            Role roleById = guild.getRoleById(roleId);
            if(roleById == null) {
                RedisDataStore.INSTANCE.setVariable(guildId, Variables.MUTE_ROLE_ID, null);
                MongoDBManager.INSTANCE.setVariable(guildId, Variables.MUTE_ROLE_ID, null);
                return true;
            }


            guild.addRoleToMember(event.getMember(), roleById).queue();
            LOGGER.info("Added mute role to a member because they were already muted");
            return true;
        }
        return false;
    }

    public static void autoRoleMember(GuildMemberJoinEvent event) {
        Member member = event.getMember();

        Guild guild = event.getGuild();
        long idLong = guild.getIdLong();
        GuildModel guildModel = RedisManager.INSTANCE.getGuildModel(idLong);


        String roleId = guildModel.getVariable(Variables.AUTO_ROLE_ID);
        if(roleId == null) return;

        Role roleById = guild.getRoleById(roleId);

        if(roleById == null) {
            RedisDataStore.INSTANCE.setVariable(idLong, Variables.AUTO_ROLE_ID, null);
            MongoDBManager.INSTANCE.setVariable(idLong, Variables.AUTO_ROLE_ID, null);
            return;
        }

        guild.addRoleToMember(member, roleById).queue();
    }
}
