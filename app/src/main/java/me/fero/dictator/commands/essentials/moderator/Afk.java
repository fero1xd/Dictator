package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.redis.RedisManager;
import me.fero.dictator.types.MongoDBFieldTypes;
import me.fero.dictator.utils.Embeds;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class Afk extends ModbaseCommand {

    public Afk() {
        this.name = "afk";
        this.help = "Sets the afk status";
        this.usage = "<message>";
    }

    @Override
    public void execute(@NotNull CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        String message = ctx.getArgs().isEmpty() ? "AFK" : String.join(" ", ctx.getArgs());
        Member member = ctx.getMember();
        Guild guild = ctx.getGuild();

        String key = guild.getId() + "-" + member.getId();

        Document doc = new Document("_id", ObjectId.get());
        doc.append(key, message);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        doc.append("timestamp", timestamp);

        RedisManager.INSTANCE.addRecordToList(guild.getIdLong(), MongoDBFieldTypes.AFK_FIELD,  doc);
        MongoDBManager.INSTANCE.addRecordToList(guild.getIdLong(), MongoDBFieldTypes.AFK_FIELD, doc);

        channel.sendMessageEmbeds(Embeds.createBuilder("Success!", "AFK status : " + message, null, null, null).build()).queue();
        if(!ctx.getSelfMember().canInteract(member)) return;
        member.modifyNickname("[AFK] " + member.getEffectiveName()).queue();
    }
}
