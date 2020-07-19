import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.http.client.ClientException;
import reactor.core.publisher.Flux;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EventProcessor {


    PrintStream logSteam = null;
    PrintStream commandLogStream = null;
    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");


    static void onMessageReceived(Message message) throws Exception{

        String body = message.getContent();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();
        discord4j.core.object.entity.User sender = message.getAuthor().isPresent() ? message.getAuthor().get() : Main.gateway.getSelf().block();

        if (message.getContent().isEmpty()) return;
        else if(sender.isBot()) return;

        String[] lowerArgs = body.toLowerCase().split(" ");
        String[] rawArgs = body.split(" ");

        boolean command =
                (lowerArgs[0].startsWith(BotUtils.DISCORDBOTLIST_PREFIX) && guild.getId().asLong() == 264445053596991498L) ||
                        (lowerArgs[0].startsWith(BotUtils.BOT_PREFIX) && guild.getId().asLong() != 264445053596991498L);

        String prefixUsed = lowerArgs[0].startsWith(BotUtils.DISCORDBOTLIST_PREFIX) ? BotUtils.DISCORDBOTLIST_PREFIX : BotUtils.BOT_PREFIX;

        if(!command && body.length() < 1200){
            for (String s : lowerArgs) {
                String y = s;
                s = s.replace("\n", " ");
                s = BotUtils.makeAlphaNumeric(s);
                String[] arr = s.split(" ");
                boolean triggeredMention = false;

                for (int i = 0; i < arr.length; i++) {
                    String a = arr[i].trim();
                    if(!a.isEmpty()) {
                        try {
                            if (!BotUtils.isNumeric(a) && BotUtils.hasLetter(a)) {
                                DataProcessor.writerQueue.offer(new QueueObject(guild.getId().asString(), channel.getId().asString(), a.toLowerCase().trim()));
                            } else if (a.length() == 18 && guild.getMemberById(Snowflake.of(a)).block() != null) {
                                Member m = guild.getMemberById(Snowflake.of(a)).block();
                                if(m.getId().asLong() != sender.getId().asLong()) {
                                    triggeredMention = true;
                                    DataProcessor.writerQueue.offer(new QueueObject(guild.getId().asString(), channel.getId().asString(), "MENTION::" + a));
                                }
                            }
                        }catch (Exception ignored){
                            //fall through
                        }
                    }
                }

                String[] raw = y.split(">");
                for (int i = 0; i < raw.length; i++) {
                    if(raw[i].matches("<[a]*:[a-zA-Z_]+:[\\d]+")){
                        try {
                            String[] data = raw[i].split(":");
                            if (guild.getGuildEmojiById(Snowflake.of(BotUtils.getUserFromMention(raw[i]))).block() != null) {
                                String name = "EMOTE::" + raw[i] + ">";
                                DataProcessor.writerQueue.offer(new QueueObject(guild.getId().asString(), channel.getId().asString(), name));
                            }
                        }catch (ClientException ignored){}
                    }
                    else {
                        Matcher matcher = Pattern.compile(BotUtils.emoRegex).matcher(raw[i]);
                        while (matcher.find()){
                            DataProcessor.writerQueue.offer(new QueueObject(guild.getId().asString(), channel.getId().asString(), "EMOTE::" + matcher.group()));
                        }
                    }
                }

                y = BotUtils.removeAlphaNumeric(y);
                if(!(y.contains("<@") || y.contains("<!") || y.contains("<#"))) {
                    for (String c : y.split("")) {
                        if (triggeredMention && (c.equals("<") || c.equals(">"))) {}
                        else if(c.isEmpty() || c.equals("\n") || !BotUtils.isUnicodePrintableChar(c.charAt(0)) || c.equals("\u200B") || c.equals("\u200b")) {}
                        else {
                            DataProcessor.writerQueue.offer(new QueueObject(guild.getId().asString(), channel.getId().asString(), "SYMBOL::" + c));
                        }
                    }
                }
            }
        }
        else if(command){
            switch (lowerArgs[0].replace(prefixUsed, "")){
                case "botstats":{
                    Consumer<EmbedCreateSpec> spec = e -> {
                        e.setDescription("Current bot information");
                        e.addField("Queue statistics:", "Reader queue length\nWriter queue length", true);
                        e.addField("\u200b", DataProcessor.readerQueue.size() + "\n" + DataProcessor.writerQueue.size(), true);
                        e.addField("\u200b", "\u200b", true);

                        e.addField("Other stats:", "Loaded servers\nActive ReaderThreads\nActive WriterThreads", true);
                        e.addField("\u200b", DataProcessor.servers.size() + "\n" + DataProcessor.readerThreadPool.size() + "\n" + DataProcessor.writerThreadPool.size(), true);
                    };

                    BotUtils.sendMessage(channel, spec);
                    break;
                }
                case "top": case "topwords":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.ALL_WORDS,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.ALL_WORDS_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "dict": case "dicttop": case "topdict":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.DICT,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.DICT_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "nondict": case "ndict":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.NONDICT,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.NONDICT_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "mentions":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.MENTIONS,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.MENTIONS_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "symbols":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.SYMBOLS,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.SYMBOLS_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "emotes": case "emojis":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.EMOTE,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.EMOTE_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "everything": case "all":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.EVERYTHING,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.EVERYTHING_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "bottom": case "bot":{
                    if(lowerArgs.length < 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.BOTTOM,10, guild));
                    }
                    else {
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.BOTTOM_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1])));
                    }
                    break;
                }
                case "server":{
                    Word top = DataProcessor.topWords(guild.getId().asString(), 1).get(0);
                    Word nondict = DataProcessor.nonDict(guild.getId().asString(), 1).get(0);
                    Word dict = DataProcessor.topDict(guild.getId().asString(), 1).get(0);
                    Word mention = DataProcessor.mentions(guild.getId().asString(), 1).get(0);
                    Word symbol = DataProcessor.symbols(guild.getId().asString(), 1).get(0);
                    ServerObject obj = DataProcessor.getServerObject(guild.getId().asString());

                    Consumer<EmbedCreateSpec> spec = e -> {
                        e.setDescription("Server statistics for " + guild.getName());



                        e.setDescription("Server statistics for " + guild.getName());
                        e.addField("Bot information", "Tracked Channels: \u200b\nTracking start time: \u200b", true);

                        StringBuilder z = new StringBuilder();
                        int i = 0;
                        List<GuildChannel> gList = guild.getChannels().collectList().block();
                        for (GuildChannel g : gList) {
                            if(!g.toString().contains("VoiceChannel{}")){
                                i++;
                            }
                        }
                        z.append(i).append("\n");
                        z.append(BotUtils.sdf.format(new Date(guild.getJoinTime().toEpochMilli())));


                        StringBuilder y = new StringBuilder();

                        e.addField("\u200b", z.toString(), true);
                        e.addField("\u200b", "\u200b", true);

                        e.addField("Word statistics", "Total words:\nMost-used word:\nMost-used dictionary word\nMost-used non-dictionary word\nMost-used symbol\n" +
                                "Most mentioned user", true);


                        y.append(obj.totalWords).append("\n").append(BotUtils.capitalizeFirst(top.name)).append("\n").append(BotUtils.capitalizeFirst(dict.name)).append("\n")
                                .append(BotUtils.capitalizeFirst(nondict.name)).append("\n").append(symbol.name).append("\n<@").append(mention.name).append(">");

                        e.addField("\u200b", y.toString(), true);

                        e.setFooter("Stats as of " + BotUtils.sdf.format(new Date(guild.getJoinTime().toEpochMilli())), null);
                    };

                    BotUtils.sendMessage(channel, spec);
                    break;
                }
                case "word": case "wordstats": case "wordstat":{
                    if(lowerArgs.length < 2){
                        BotUtils.sendArgumentsError(channel, "wordstats", "word", "optional channel");
                    }
                    else if(lowerArgs.length == 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.READ_SINGLE_WORD,10, guild, lowerArgs[1]));
                    }
                    else{
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.READ_SINGLE_WORD_CHANNEL,10, guild, lowerArgs[1]
                                , BotUtils.getUserFromMention(lowerArgs[2])));
                    }
                    break;
                }
                case "mention": case "mentionstat": case "usermentions": case "mentionstats":{
                    if(lowerArgs.length < 2){
                        BotUtils.sendArgumentsError(channel, "mentionstats", "user", "optional channel");
                    }
                    else if(lowerArgs.length == 2){
                        String mention = "MENTION::" + BotUtils.getUserFromMention(lowerArgs[1]);
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.READ_SINGLE_MENTION,10, guild, mention));
                    }
                    else{
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.READ_SINGLE_MENTION_CHANNEL,10, guild,
                                BotUtils.getUserFromMention(lowerArgs[1]), BotUtils.getUserFromMention(lowerArgs[2])));
                    }
                    break;
                }
                case "symbolstats": case "symbol":{
                    if(lowerArgs.length < 2){
                        BotUtils.sendArgumentsError(channel, "mentionstats", "user", "optional channel");
                    }
                    else if(lowerArgs.length == 2){
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.READ_SINGLE_SYMBOL,10, guild,
                                "SYMBOL::" + lowerArgs[1]));
                    }
                    else{
                        DataProcessor.readerQueue.offer(new ReadQueueObject(channel, guild.getId().asString(), ReadOperations.READ_SINGLE_SYMBOL_CHANNEL,10, guild, lowerArgs[1]
                                , BotUtils.getUserFromMention(lowerArgs[2])));
                    }
                    break;
                }
                case "help":{
                    for (Consumer<EmbedCreateSpec> e : BotUtils.helpEmbeds) {
                        BotUtils.sendMessage(channel, e);
                    }
                    break;
                }
                case "clearemotes":{
                    if(sender.getId().asLong() != 506696814490288128L) break;
                    ServerObject obj = DataProcessor.getServerObject(guild.getId().asString());

                    Iterator<Map.Entry<String, Integer>> iter = obj.serverOverallMap.entrySet().iterator();
                    while (iter.hasNext()){
                        Map.Entry<String, Integer> entry = iter.next();
                        if(entry.getKey().startsWith("EMOTE::")) {
                            Word w = new Word(entry.getKey(), obj.serverOverallMap.get(entry.getKey()));
                            obj.serverOverallData.deleteKey(w);
                            iter.remove();
                        }
                    }

                    break;
                }

            }
        }

    }

}