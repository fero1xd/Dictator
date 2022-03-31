package me.fero.dictator.database;

import org.bson.Document;

import java.util.HashMap;

public interface DatabaseManager {
    DatabaseManager INSTANCE = new MongoDBManager();
    Document getPrefix(Long guildId);
    void setPrefix(Long guildId, String newPrefix);
    void setVariable(Long guildId, String key, String value);
    void addRecordToList(Long guildId, String field, Document docToAdd);
    void removeRecordFromList(Long guildId, String field, HashMap<String, String> map);
    void addItemToList(Long guildId, String listName, String key);
    void removeItemFromList(Long guildId, String listName, String key);
}
