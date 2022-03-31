package me.fero.dictator.redis;

import me.fero.dictator.entities.GuildModel;
import org.bson.Document;
import org.redisson.api.RedissonClient;

public interface RedisDataStore {
    RedisDataStore INSTANCE = new RedisManager();
    String getPrefix(Long guildId);
    RedissonClient getRedisson();
    void setPrefix(Long guildId, String newPrefix);
    GuildModel getGuildModel(Long guildId);
    void setVariable(Long guildId, String key, String value);
    void addRecordToList(Long guildId, String field, Document doc );
    void addMute(Long guildId, String key);
    void removeMute(Long guildId, String key);
}
