package me.fero.dictator.websocket;

import me.fero.dictator.entities.GuildModel;
import me.fero.dictator.redis.RedisManager;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class SocketController {

    @MessageMapping("/update-config")
    @SendTo("/topic/success")
    public String updateConfig(GuildConfig config) {
        System.out.println(config);

        RedissonClient redisson = RedisManager.INSTANCE.getRedisson();
        RBucket<Object> bucket = redisson.getBucket(String.valueOf(config.getGuildId()));

        GuildModel guildSettings = (GuildModel) bucket.get();
        guildSettings.setPrefixOfGuild(config.getPrefix());
        for(Map.Entry<String, String> entry : config.getVariables().entrySet()) {
            guildSettings.setVariable(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String, Boolean> entry : config.getEnables().entrySet()) {
            guildSettings.setStateOfLogging(entry.getKey(), entry.getValue());
        }

        bucket.set(guildSettings);
        return "Success";
    }

}
