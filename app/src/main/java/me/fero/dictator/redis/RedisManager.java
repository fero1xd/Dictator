package me.fero.dictator.redis;

import me.fero.dictator.database.DatabaseManager;
import me.fero.dictator.entities.GuildModel;
import me.fero.dictator.types.MongoDBFieldTypes;
import org.bson.Document;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.*;


public class RedisManager implements RedisDataStore{
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);
    private final RedissonClient redisson;

    public RedisManager() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        this.redisson = Redisson.create(config);
    }

    @Override
    public RedissonClient getRedisson() {
        return this.redisson;
    }

    @Override
    public String getPrefix(Long guildId) {
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        GuildModel guild = (GuildModel) bucket.get();

        if(guild == null) {
            Document guildSettings = DatabaseManager.INSTANCE.getPrefix(guildId);
            String prefix = guildSettings.get("prefix").toString();


            Document variables = (Document) guildSettings.get("VARIABLES");
            Document enabledLogs = (Document) guildSettings.get("ENABLES");
            List<Document> warns = (List<Document>) guildSettings.get(MongoDBFieldTypes.WARNS_FIELD);
            List<String> mutes = (List<String>) guildSettings.get(MongoDBFieldTypes.MUTES_FIELD);

            HashMap<String, String> map = new HashMap<>();
            HashMap<String, Boolean> enabledMap = new HashMap<>();
            List<HashMap<String, String>> warnsList = new ArrayList<>();
            Set<String> mutesSet = new HashSet<>(mutes);

            // For variables
            for(String key : variables.keySet()) {
                if(variables.get(key) == null) {
                    map.put(key, null);
                    continue;
                }
                map.put(key, variables.get(key).toString());
            }

            // For enabled logs
            for(String key : enabledLogs.keySet()) {
                enabledMap.put(key, enabledLogs.getBoolean(key));
            }

            // For loading warnings
            for(Document doc : warns) {
                HashMap<String, String> info = new HashMap<>();
                for(String key : doc.keySet()) {
                    info.put(key, doc.get(key).toString());
                }
                warnsList.add(info);
            }

            bucket.set(new GuildModel(guildId, prefix,  map, enabledMap, warnsList, mutesSet));
            return prefix;
        }

        return guild.getPrefixOfGuild();
    }


    @Override
    public void setPrefix(Long guildId, String newPrefix) {
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        GuildModel guild = (GuildModel) bucket.get();
        guild.setPrefixOfGuild(newPrefix);
        bucket.set(guild);
    }

    @Override
    public GuildModel getGuildModel(Long guildId) {
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        return (GuildModel) bucket.get();
    }

    @Override
    public void setVariable(Long guildId, String key, String value){
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        GuildModel guild = (GuildModel) bucket.get();
        guild.setVariable(key, value);
        bucket.set(guild);
    }

    @Override
    public void addRecordToList(Long guildId, String field, Document doc) {
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        GuildModel guild = (GuildModel) bucket.get();
        guild.addRecordToList(doc, field);
        bucket.set(guild);
    }

    @Override
    public void addMute(Long guildId, String key) {
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        GuildModel guild = (GuildModel) bucket.get();
        guild.addMute(key);
        bucket.set(guild);
    }

    @Override
    public void removeMute(Long guildId, String key) {
        RBucket<Object> bucket = this.redisson.getBucket(String.valueOf(guildId));
        GuildModel guild = (GuildModel) bucket.get();
        guild.removeMute(key);
        bucket.set(guild);
    }
}
