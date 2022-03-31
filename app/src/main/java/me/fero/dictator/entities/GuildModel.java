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
    private Set<String> mutes;

    public GuildModel(Long guildId, String prefix, HashMap<String, String> variables, HashMap<String, Boolean> enabled, List<HashMap<String, String>> warns, Set<String> mutes) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.variables = variables;
        this.enabledLogs = enabled;
        this.warns = warns;
        this.mutes = mutes;
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

    public void addRecordToList(Document doc, String field) {
        if(Objects.equals(field, MongoDBFieldTypes.WARNS_FIELD)) {
            addWarns(doc);
        }
    }

    public List<HashMap<String, String>> getWarns() {
        return this.warns;
    }

    private void addWarns(Document doc) {
        HashMap map = new HashMap();
        for(String key : doc.keySet()) {
            map.put(key, doc.get(key).toString());
        }

        this.warns.add(map);
    }

    public void setStateOfLogging(String key, Boolean value) {
        this.enabledLogs.put(key, value);
    }

    public Boolean hasMute(String key) {
        return this.mutes.contains(key);
    }



    public void addMute(String key) {
        this.mutes.add(key);
    }

    public void removeMute(String key) {
        this.mutes.remove(key);
    }
}
