import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataProcessor {

    static final String savePath = "wordData";
    static boolean doingFullWrite = false;

    static Queue<QueueObject> writerQueue = new ConcurrentLinkedQueue<>();
    static List<WriterThread> writerThreadPool = new ArrayList<>();

    static Queue<ReadQueueObject> readerQueue = new ConcurrentLinkedQueue<>();
    static List<ReaderThread> readerThreadPool = new ArrayList<>();

    static HashMap<String, ServerObject> servers = new HashMap<>();

    static void add(String serverID, String channelID, String word) throws IOException, ClassNotFoundException, InterruptedException {
        loadServerObject(serverID);

        ServerObject server = servers.get(serverID);
        server.addWord(channelID, word);
    }

    static ArrayList<Word> topWords(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {

        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::") && !w.name.startsWith("EMOTE::")){
                    out.add(w);
                    x++;
                }
                i--;
            }
        }
        return out;
    }

    static ArrayList<Word> bottomWords(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {

        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = 0;
            int x = 0;
            while (i < data.size() && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::") && !w.name.startsWith("EMOTE::")){
                    out.add(w);
                    x++;
                }
                i++;
            }
        }
        return out;
    }

    static ArrayList<Word> topDict(String serverID, int amount) throws ClassNotFoundException, InterruptedException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::") && BotUtils.dictionary.isWord(w.name) && !w.name.startsWith("EMOTE::")){
                    out.add(w);
                    x++;
                }
                i--;
            }
        }
        return out;
    }

    static ArrayList<Word> nonDict(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::") && !BotUtils.dictionary.isWord(w.name) && !w.name.startsWith("EMOTE::")){
                    out.add(w);
                    x++;
                }
                i--;
            }
        }
        return out;
    }

    static ArrayList<Word> mentions(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("MENTION::")){
                    out.add(new Word(w.name.substring(9), w.value));
                    x++;
                }
                i--;
            }
        }
        return out;
    }

    static ArrayList<Word> symbols(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("SYMBOL::")){
                    out.add(new Word(w.name.substring(8), w.value));
                    x++;
                }
                i--;
            }
        }
        return out;
    }

    static ArrayList<Word> emotes(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("EMOTE::")){
                    out.add(new Word(w.name.substring(7), w.value));
                    x++;
                }
                i--;
            }
        }
        return out;
    }

    static ArrayList<Word> allTop(String serverID, int amount) throws IOException, ClassNotFoundException, InterruptedException {

        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.serverOverallData.inorder();

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("MENTION::")){
                    w = new Word("<@" + w.name.substring(9) + ">", w.value);
                }
                else if(w.name.startsWith("SYMBOL::")){
                    w = new Word(w.name.substring(8), w.value);
                }

                x++;
                i--;

                out.add(w);
            }
        }
        return out;
    }

    static Word getWord(String serverID, String word){
        loadServerObject(serverID);

        if(servers.get(serverID).serverOverallMap.get(word) == null) return null;

        return new Word(word, servers.get(serverID).serverOverallMap.get(word));
    }

    static Word getMention(String serverID, String word){
        loadServerObject(serverID);

        if(!word.startsWith("MENTION::")) {word = "MENTION::" + word;}

        if(servers.get(serverID).serverOverallMap.get(word) == null) return null;

        return new Word(word.substring(9), servers.get(serverID).serverOverallMap.get(word));
    }

    static Word getSymbol(String serverID, String symbol){
        loadServerObject(serverID);

        if(!symbol.startsWith("SYMBOL::")) {symbol = "SYMBOL::" + symbol;}

        if(servers.get(serverID).serverOverallMap.get(symbol) == null) return null;

        return new Word(symbol.substring(8), servers.get(serverID).serverOverallMap.get(symbol));
    }

    static Word getEmote(String serverID, String symbol){
        loadServerObject(serverID);

        if(!symbol.startsWith("EMOTE::")) {symbol = "EMOTE::" + symbol;}

        if(servers.get(serverID).serverOverallMap.get(symbol) == null) return null;

        return new Word(symbol.substring(7), servers.get(serverID).serverOverallMap.get(symbol));
    }

    static ServerObject getServerObject(String serverID){
        loadServerObject(serverID);
        return servers.get(serverID);
    }

    static ArrayList<Word> topWordsChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::")){
                    out.add(w);
                    x++;
                }
                i--;
            }
        }
        return out;


    }

    static ArrayList<Word> topSymbolChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("SYMBOL::")){
                    out.add(new Word(w.name.substring(8), w.value));
                    x++;
                }
                i--;
            }
        }
        return out;


    }

    static ArrayList<Word> topDictChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::") && BotUtils.dictionary.isWord(w.name)){
                    out.add(w);
                    x++;
                }
                i--;
            }
        }
        return out;


    }

    static ArrayList<Word> topNonDictChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::") && !BotUtils.dictionary.isWord(w.name)){
                    out.add(w);
                    x++;
                }
                i--;
            }
        }
        return out;


    }

    static ArrayList<Word> topMentionChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("MENTION::")){
                    out.add(new Word(w.name.substring(9), w.value));
                    x++;
                }
                i--;
            }
        }
        return out;


    }

    static ArrayList<Word> topAllChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("MENTION::")){
                    w = new Word(w.name.substring(9), w.value);
                }
                else if(w.name.startsWith("SYMBOL::")){
                    w = new Word(w.name.substring(8), w.value);
                }

                out.add(w);
                x++;
                i--;
            }
        }
        return out;


    }

    static ArrayList<Word> bottomWordsChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = 0;
            int x = 0;
            while (i < data.size() && x < amount){
                Word w = data.get(i);
                if(!w.name.startsWith("MENTION::") && !w.name.startsWith("SYMBOL::")){
                    out.add(w);
                    x++;
                }
                i++;
            }
        }
        return out;


    }

    static ArrayList<Word> topEmoteChannel(String serverID, String channelID, int amount) throws IOException {
        loadServerObject(serverID);
        ArrayList<Word> out = new ArrayList<>();

        if(!servers.containsKey(serverID)) return out;
        else {
            ServerObject server = servers.get(serverID);
            ArrayList<Word> data = server.getChannelDataOrdered(channelID);

            int i = data.size() - 1;
            int x = 0;
            while (i >= 0 && x < amount){
                Word w = data.get(i);
                if(w.name.startsWith("EMOTE::")){
                    out.add(new Word(w.name.substring(7), w.value));
                    x++;
                }
                i--;
            }
        }
        return out;


    }

    static Word getWordChannel(String serverID, String channelID, String word) throws IOException {
        loadServerObject(serverID);

        ServerObject server = servers.get(serverID);
        ServerObject.TreeMapPair pair = server.getChannelPair(channelID);

        if(pair.map.get(word) == null) return null;

        return new Word(word, pair.map.get(word));
    }

    static Word getSymbolChannel(String serverID, String channelID, String word) throws IOException {
        loadServerObject(serverID);

        if(!word.startsWith("SYMBOL::")) word = "SYMBOL::" + word;

        ServerObject server = servers.get(serverID);
        ServerObject.TreeMapPair pair = server.getChannelPair(channelID);

        if(pair.map.get(word) == null) return null;

        return new Word(word.substring(8), pair.map.get(word));
    }

    static Word getMentionChannel(String serverID, String channelID, String word) throws IOException {
        loadServerObject(serverID);

        if(!word.startsWith("MENTION::")) word = "MENTION::" + word;

        ServerObject server = servers.get(serverID);
        ServerObject.TreeMapPair pair = server.getChannelPair(channelID);

        if(pair.map.get(word) == null) return null;

        return new Word(word.substring(9), pair.map.get(word));
    }

    static Word getEmoteChannel(String serverID, String channelID, String word) throws IOException {
        loadServerObject(serverID);

        if(!word.startsWith("EMOTE::")) word = "EMOTE::" + word;

        ServerObject server = servers.get(serverID);
        ServerObject.TreeMapPair pair = server.getChannelPair(channelID);

        if(pair.map.get(word) == null) return null;

        return new Word(word.substring(7), pair.map.get(word));
    }





    static void writeToFile(String serverID) throws IOException {

        if(servers.containsKey(serverID)) {
            File f = new File(savePath, serverID);
            if (!f.isDirectory()) f.mkdir();

            File serverData = new File(f, "serverData.ser");
            ServerObject server = servers.get(serverID);


            for (String channelID : server.channelPairs.keySet()){
                server.saveChannel(channelID);
            }

            ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(serverData)));
            outputStream.writeObject(server);
            outputStream.close();

            servers.remove(serverID);
        }

    }

    static void writeToFileNoRemove(String serverID) throws IOException {
        if(servers.containsKey(serverID)) {
            File f = new File(savePath, serverID);
            if (!f.isDirectory()) f.mkdir();

            File serverData = new File(f, "serverData.ser");
            ServerObject server = servers.get(serverID);


            for (String channelID : server.channelPairs.keySet()){
                server.saveChannelNoRemove(channelID);
            }

            ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(serverData)));
            outputStream.writeObject(server);
            outputStream.close();
        }
    }

    static void loadServerObject(String serverID){
        if(!servers.containsKey(serverID) || servers.get(serverID) == null){
            System.out.println("[LOAD]: Loaded guild " + serverID + " into memory");

            File serverPath = new File(savePath, serverID);
            File serverData = new File(serverPath, "serverData.ser");

            if(!serverPath.isDirectory()) serverPath.mkdir();

            try {
                ObjectInputStream stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(serverData)));
                ServerObject serverObj = (ServerObject) stream.readObject();
                servers.put(serverID, serverObj);
            }catch (NoSuchFileException | FileNotFoundException e){
                servers.put(serverID, new ServerObject(serverID));
            }
            catch (StreamCorruptedException | EOFException  e) {
                System.err.println("Error reading data file for " + serverID + ", moving file and recreating...");
                File corruptedDir = new File(DataProcessor.savePath, "corruptedFiles");
                if(!corruptedDir.isDirectory()) corruptedDir.mkdir();
                File file = new File(corruptedDir, serverID + "-corrupted.ser");
                try {
                    Files.copy(serverData.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ignored) {}
                servers.put(serverID, new ServerObject(serverID));
            } catch (ClassNotFoundException | IOException ignored) {
            }
        }

        if(servers.get(serverID) == null){
            servers.put(serverID, new ServerObject(serverID));
        }

        servers.get(serverID).lastAccessTime = new Date().getTime();
    }
}

class QueueObject{
    String serverID;
    String channelID;
    String word;
    QueueObject(String serverID, String channelID, String word){
        this.serverID = serverID;
        this.channelID = channelID;
        this.word = word;
    }
}

class ReadQueueObject{
    MessageChannel channel;
    String serverID;
    ReadOperations type;
    Guild guild;

    int amount;

    String parameter;
    String[] additionalParameters;

    ReadQueueObject(MessageChannel channel, String serverID, ReadOperations type, int amount, Guild guild){
        this.channel = channel;
        this.serverID = serverID;
        this.type = type;
        this.amount = amount;
        this.guild = guild;
    }

    ReadQueueObject(MessageChannel channel, String serverID, ReadOperations type, int amount, Guild guild, String parameter){
        this.channel = channel;
        this.serverID = serverID;
        this.type = type;
        this.amount = amount;
        this.guild = guild;
        this.parameter = parameter;
    }

    ReadQueueObject(MessageChannel channel, String serverID, ReadOperations type, int amount, Guild guild, String parameter, String... additionalParameters){
        this.channel = channel;
        this.serverID = serverID;
        this.type = type;
        this.amount = amount;
        this.guild = guild;
        this.parameter = parameter;
        this.additionalParameters = additionalParameters;
    }


}

enum ReadOperations{
    ALL_WORDS,
    DICT,
    NONDICT,
    SYMBOLS,
    MENTIONS,
    EVERYTHING,
    BOTTOM,
    EMOTE,

    ALL_WORDS_CHANNEL,
    DICT_CHANNEL,
    NONDICT_CHANNEL,
    SYMBOLS_CHANNEL,
    MENTIONS_CHANNEL,
    EVERYTHING_CHANNEL,
    BOTTOM_CHANNEL,
    EMOTE_CHANNEL,

    READ_SINGLE_WORD,
    READ_SINGLE_SYMBOL,
    READ_SINGLE_MENTION,
    READ_SINGLE_EMOTE,

    READ_SINGLE_SYMBOL_CHANNEL,
    READ_SINGLE_MENTION_CHANNEL,
    READ_SINGLE_WORD_CHANNEL,
    READ_SINGLE_EMOTE_CHANNEL,

    REMOVE_REDUNDANCY,
    REMOVE_CHANNEL_REDUNDANCY
}
