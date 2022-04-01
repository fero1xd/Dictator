package me.fero.dictator.entities;


import me.fero.dictator.types.MongoDBFieldTypes;
import org.bson.Document;

import java.io.Serializable;
import java.util.*;

public class GuildModel implements Serializable {
    private final Long guildId;
    private String prefix;
    private HashMap<String, String> variables;
    private HashMap<String, Boolean> enabledLogs;
    private List<HashMap<String, String>> warns;
    private List<HashMap<String, String>> afks;
    private Set<String> mutes;


    public GuildModel(Long guildId, String prefix, HashMap<String, String> variables, HashMap<String, Boolean> enabled,
                      List<HashMap<String, String>> warns, Set<String> mutes, List<HashMap<String, String>> afks) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.variables = variables;
        this.enabledLogs = enabled;
        this.warns = warns;
        this.mutes = mutes;
        this.afks = afks;
    }

    public String getPrefixOfGuild() {
        return this.prefix;
    }

    public void setPrefixOfGuild(String newPrefix) {
        this.prefix = newPrefix;
    }

    public String getVariable(String key) {
        return this.variables.get(key.toLowerCase());
    }

    public void setVariable(String key, String value) {
        this.variables.put(key, value);
    }

    public Boolean getStateOfLogging(String key) {
        return this.enabledLogs.get(key.toLowerCase());
    }

    public void addRecordToObjectList(Document doc, String field) {
        if(Objects.equals(field, MongoDBFieldTypes.WARNS_FIELD)) {
            addWarns(doc);
        }
        if(Objects.equals(field, MongoDBFieldTypes.AFK_FIELD)) {
            addAfk(doc);
        }
    }

    public void removeRecordFromList(HashMap<String, String> map, String field) {
        if(Objects.equals(field, MongoDBFieldTypes.WARNS_FIELD)) {
            this.warns.removeIf(warn -> warn.get("_id").equals(map.get("_id")));
        }
        if(Objects.equals(field, MongoDBFieldTypes.AFK_FIELD)) {
            this.afks.removeIf(afk -> afk.get("_id").equals(map.get("_id")));
        }
    }


    public HashMap<String, String> hasRecordInList(String field, String key) {
        if(Objects.equals(field, MongoDBFieldTypes.WARNS_FIELD)) {
            return this.warns.stream().filter(warn -> warn.containsKey(key)).findFirst().orElse(null);
        }
        if(Objects.equals(field, MongoDBFieldTypes.AFK_FIELD)) {
            return this.afks.stream().filter(afk -> afk.containsKey(key)).findFirst().orElse(null);
        }
        return null;
    }

    public List<HashMap<String, String>> getWarns() {
        return this.warns;
    }

//    public List<HashMap<String, String>> getAfks() { return  this.afks; }


    private void addWarns(Document doc) {
        HashMap map = new HashMap();
        for(String key : doc.keySet()) {
            map.put(key, doc.get(key).toString());
        }

        this.warns.add(map);
    }

    private void addAfk(Document doc) {
        HashMap map = new HashMap();
        for(String key : doc.keySet()) {
            map.put(key, doc.get(key).toString());
        }

        this.afks.add(map);
    }

    public void setStateOfLogging(String key, Boolean value) {
        this.enabledLogs.put(key, value);
    }

    public Boolean hasItemInList(String listName, String key) {
        if(listName.equalsIgnoreCase(MongoDBFieldTypes.MUTES_FIELD)) {
            return this.mutes.contains(key);
        }
        return false;
    }

    public void addItemToList(String listName, String key) {
        if(listName.equalsIgnoreCase(MongoDBFieldTypes.MUTES_FIELD)) {
            this.mutes.add(key);
            return;
        }
        throw new IllegalArgumentException("Not correct listName");
    }

    public void removeItemFromList(String listName, String key) {
        if(listName.equalsIgnoreCase(MongoDBFieldTypes.MUTES_FIELD)) {
            this.mutes.remove(key);
            return;
        }
        throw new IllegalArgumentException("Not correct listName");
    }
}
