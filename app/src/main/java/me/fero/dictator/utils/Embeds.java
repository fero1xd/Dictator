package me.fero.dictator.utils;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;


public class Embeds {

    public static EmbedBuilder createBuilder(String title, String description, String footer, String footerUrl, Color color) {
        EmbedBuilder builder = EmbedUtils.getDefaultEmbed();

        if(title != null) {
            builder.setTitle(title);
        }
        if(description != null) {
            builder.setDescription(description);
        }
        if(footer != null) {
            if(footerUrl != null) {
                builder.setFooter(footer, footerUrl);
            }
            else {
                builder.setFooter(footer);
            }
        }
        if(color != null) {
            builder.setColor(color);
        }
        else {
            Color color1 = new Color((int) (Math.random() * 0x1000000));
            builder.setColor(color1);
        }


        return builder;
    }

}
