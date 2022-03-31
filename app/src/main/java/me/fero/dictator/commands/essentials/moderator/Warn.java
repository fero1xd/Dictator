package me.fero.dictator.commands.essentials.moderator;

import me.fero.dictator.database.MongoDBManager;
import me.fero.dictator.commands.setup.context.CommandContext;
import me.fero.dictator.entities.GuildModel;
import me.fero.dictator.redis.RedisDataStore;
import me.fero.dictator.types.Logging;
import me.fero.dictator.types.MongoDBFieldTypes;
import me.fero.dictator.types.Variables;
import me.fero.dictator.utils.MessagingUtils;
import me.fero.dictator.utils.ModerationUtils;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Warn extends ModbaseCommand{
    public Warn() {
        this.name = "warn";
        this.help = "warns a user";
        this.requiredArgs = true;
        this.requiredArgCount = 2;
        this.usage = "<mention> <reason>";

    }
    @Override
    public void execute(@NotNull CommandContext ctx) {
        List<Member> mentionedMembers = ctx.getMessage().getMentionedMembers();
        if(mentionedMembers.isEmpty()) {
            ModerationUtils.noMentionFoundEmbed(ctx, "warn");
            return;
        }

        Member target = mentionedMembers.get(0);
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();

        if(!ModerationUtils.canIntercat(member, target, selfMember, ctx, "warn", true)) return;

        String reason = ModerationUtils.parseReason(ctx);
        String dmText = "You have been warned in " + ctx.getGuild().getName() + ". Reason - " + (reason != null ? reason : "Not given");

        Document info = getDoc(member, target, reason);

        long idLong = ctx.getGuild().getIdLong();
        RedisDataStore.INSTANCE.addRecordToList(idLong, MongoDBFieldTypes.WARNS_FIELD, info);
        MongoDBManager.INSTANCE.addRecordToList(idLong, MongoDBFieldTypes.WARNS_FIELD, info);


        GuildModel guildModel = RedisDataStore.INSTANCE.getGuildModel(idLong);
        List<HashMap<String, String>> warns = guildModel.getWarns();

        List filtered = warns.stream().filter((warn) -> warn.get("punished_user_id").equalsIgnoreCase(target.getId())).collect(Collectors.toList());

        MessagingUtils.sendDm(target, null, dmText);

        if(ModerationUtils.checkThresholdReached(ctx, target, Variables.WARN_THRESHOLD, filtered.size())) return;

        ModerationUtils.logModChannel(ctx, ModerationUtils.successModerationLog(ctx, target, null, "warned", reason)
                .addField("Timestamp", info.get("timestamp").toString(), false)
                .addField("Total", String.valueOf(filtered.size()), false), Logging.WARN_LOG);


        ModerationUtils.successEmbed(ctx, "warned", target, null);

    }


    Document getDoc(Member member, Member target, String reason) {
        Document info = new Document("_id", ObjectId.get());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        info.put("punisher", member.getEffectiveName());
        info.put("punisher_user_id", member.getId());

        info.put("punished", target.getEffectiveName());
        info.put("punished_user_id", target.getId());
        info.put("reason", reason != null ? reason : "Not Available");
        info.put("timestamp", timestamp);
        return info;
    }
}

