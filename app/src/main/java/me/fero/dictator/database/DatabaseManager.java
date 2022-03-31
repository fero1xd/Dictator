package me.fero.dictator.database;

import org.bson.Document;

public interface DatabaseManager {
    DatabaseManager INSTANCE = new MongoDBManager();
    Document getPrefix(Long guildId);
    void setPrefix(Long guildId, String newPrefix);
    void setVariable(Long guildId, String key, String value);
    void addRecordToList(Long guildId, String field, Document docToAdd);
    void addMuteToList(Long guildId, String key);
    void removeMuteFromList(Long guildId, String key);
}
