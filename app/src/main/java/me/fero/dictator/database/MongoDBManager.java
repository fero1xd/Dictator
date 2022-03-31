package me.fero.dictator.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import me.fero.dictator.config.Config;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.MongoDBFieldTypes;
import me.fero.dictator.types.Variables;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoDBManager implements DatabaseManager{

    private final Logger LOGGER = LoggerFactory.getLogger(MongoDBManager.class);
    private final MongoDatabase db;

    public MongoDBManager() {
        MongoClient client = MongoClients.create(Config.get("MONGO_URI"));
        this.db = client.getDatabase("dictator_bot");
        LOGGER.info("Connected to Mongo DB");
    }


    @Override
    public Document getPrefix(Long guildId) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("guild_id", guildId);

        MongoCollection<Document> guild_settings = this.db.getCollection("guild_settings");

        Document cursor = guild_settings.find(whereQuery).first();
        if(cursor == null) {
            Document newGuildSettings = createNewGuildSettings(guildId);
            guild_settings.insertOne(newGuildSettings);

            return guild_settings.find(whereQuery).first();
        }

        return cursor;
    }

    @Override
    public void setPrefix(Long guildId, String newPrefix) {
        Bson filter = Filters.eq("guild_id", guildId);
        Bson updated = Updates.set("prefix", newPrefix);

        this.db.getCollection("guild_settings").updateOne(filter, updated);
    }

    @Override
    public void setVariable(Long guildId, String key, String value) {
        Bson filter = Filters.eq("guild_id", guildId);
        Bson updated = Updates.set("VARIABLES."+key, value);

        this.db.getCollection("guild_settings").updateOne(filter, updated);
    }

    @Override
    public void addRecordToList(Long guildId, String field, Document docToAdd) {
        Bson filter = Filters.eq("guild_id", guildId);
        Bson updated = Updates.push(field, docToAdd);

        this.db.getCollection("guild_settings").updateOne(filter, updated);
    }

    @Override
    public void addMuteToList(Long guildId, String key) {
        Bson filter = Filters.eq("guild_id", guildId);
        Bson updated = Updates.push(MongoDBFieldTypes.MUTES_FIELD, key);

        this.db.getCollection("guild_settings").updateOne(filter, updated);
    }
//
    @Override
    public void removeMuteFromList(Long guildId, String key) {
        Bson filter = Filters.eq("guild_id", guildId);

        MongoCollection<Document> guild_settings = this.db.getCollection("guild_settings");
        Document cursor = guild_settings.find(filter).first();

        List<String> mutes = (List<String>) cursor.get(MongoDBFieldTypes.MUTES_FIELD);

        int i = mutes.indexOf(key);
        if (i < 0) return;

        List<String> collect = mutes.stream().filter(mute -> !mute.equalsIgnoreCase(key)).collect(Collectors.toList());

        Bson updated = Updates.set(MongoDBFieldTypes.MUTES_FIELD, collect);
        guild_settings.updateOne(filter, updated);
    }

    private Document createNewGuildSettings(Long guildId) {
        Document newDoc = new Document("_id", ObjectId.get());
        newDoc.append("guild_id", guildId);
        newDoc.append("prefix", Config.get("default_prefix"));
        HashMap<String, String> variablesMap = new HashMap<>();
        HashMap<String, Boolean> enabledMap = new HashMap<>();


        variablesMap.put(Variables.MUTE_ROLE_ID, null);
        variablesMap.put(Variables.LOG_CHANNEL, null);
        variablesMap.put(Variables.AUTO_ROLE_ID, null);
        variablesMap.put(Variables.WARN_THRESHOLD, null);
        variablesMap.put(Variables.WARN_THRESHOLD_ACTION, Variables.ACTION_MUTE);

        enabledMap.put(Logging.KICK_LOG, false);
        enabledMap.put(Logging.MUTE_LOG, false);
        enabledMap.put(Logging.UNMUTE_LOG, false);
        enabledMap.put(Logging.ROLE_ADD_LOG, false);
        enabledMap.put(Logging.BAN_LOG, false);
        enabledMap.put(Logging.UNBAN_LOG, false);
        enabledMap.put(Logging.WARN_LOG, false);



        newDoc.append("VARIABLES", variablesMap);
        newDoc.append("ENABLES", enabledMap);
        newDoc.append(MongoDBFieldTypes.MUTES_FIELD, new BasicDBList());
        newDoc.append(MongoDBFieldTypes.WARNS_FIELD, new BasicDBList());
        return newDoc;
    }
}
