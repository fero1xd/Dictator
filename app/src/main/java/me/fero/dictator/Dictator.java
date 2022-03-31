
package me.fero.dictator;

import me.fero.dictator.database.DatabaseManager;
import me.fero.dictator.config.Config;
import me.fero.dictator.listeners.GuildListener;
import me.fero.dictator.redis.RedisDataStore;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

@SpringBootApplication
public class Dictator  {

    public Dictator(String[] args) throws LoginException {
        DatabaseManager instance1 = DatabaseManager.INSTANCE;
        RedisDataStore instance = RedisDataStore.INSTANCE;


        JDABuilder builder = JDABuilder.createDefault(
                Config.get("bot_token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES
                );

        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        // Listeners
        builder.addEventListeners(new GuildListener());
        builder.disableCache(EnumSet.of(
                CacheFlag.VOICE_STATE,
                CacheFlag.EMOTE
        ));


        builder.build();

    }


    public static void main(String[] args) throws LoginException {
        SpringApplication.run(Dictator.class, args);

//        new Dictator(args);

    }

}
