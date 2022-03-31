package me.fero.dictator.redis;

import me.fero.dictator.entities.GuildModel;
import org.bson.Document;
import org.redisson.api.RedissonClient;

import java.util.HashMap;

public interface RedisDataStore {
    RedisDataStore INSTANCE = new RedisManager();
    String getPrefix(Long guildId);
    RedissonClient getRedisson();
    void setPrefix(Long guildId, String newPrefix);
    GuildModel getGuildModel(Long guildId);
    void setVariable(Long guildId, String key, String value);
    void addRecordToList(Long guildId, String field, Document doc );
    void removeRecordFromList(Long guildId, String field, HashMap<String, String> map );
    void addItemToList(Long guildId, String listName, String key);
    void removeItemFromList(Long guildId, String listName, String key);
}
