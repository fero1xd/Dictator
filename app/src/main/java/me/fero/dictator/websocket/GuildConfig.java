package me.fero.dictator.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuildConfig {
    private Long guildId;
    private String prefix;
    private HashMap<String, String> variables;
    private HashMap<String, Boolean> enables;
}
