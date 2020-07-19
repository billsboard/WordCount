import discord4j.core.object.entity.channel.MessageChannel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerMessagesObject implements Serializable {
    public static final long serialVersionUID = 42L;

    HashMap<String, Integer> wordMap = null;
    HashMap<String, Integer> dictWords = null;
    HashMap<String, Integer> nonDictWords = null;
    HashMap<Long, Integer> mentions = new HashMap<>();
    HashMap<String, Integer> symbols = new HashMap<>();

    long id;
    long totalWords = 0;

    ArrayList<ChannelMessagesObject> channels = null;

    ServerMessagesObject(long id, HashMap<String, Integer> wordMap, HashMap<String, Integer> dictWords, HashMap<String, Integer> nonDictWords){
        this.id = id;
        this.wordMap = wordMap;
        this.dictWords = dictWords;
        this.nonDictWords = nonDictWords;
        channels = new ArrayList<>();
    }

    ServerMessagesObject(long id, HashMap<String, Integer> wordMap, HashMap<String, Integer> dictWords, HashMap<String, Integer> nonDictWords, ArrayList<ChannelMessagesObject> channels){
        this.id = id;
        this.wordMap = wordMap;
        this.dictWords = dictWords;
        this.nonDictWords = nonDictWords;
        this.channels = channels;
    }

    ServerMessagesObject(long id){
        this.id = id;
        this.wordMap = new HashMap<>();
        this.dictWords = new HashMap<>();
        this.nonDictWords = new HashMap<>();
        this.channels = new ArrayList<>();
    }

    void addWord(MessageChannel c, String word){
        word = word.toLowerCase();
        if(BotUtils.dictionary.isWord(word)){
            addToMap(wordMap, word);
            addToMap(dictWords, word);
        }
        else{
            addToMap(wordMap, word);
            addToMap(nonDictWords, word);
        }
        totalWords++;
        ChannelMessagesObject obj = getChannelObject(c.getId().asLong());
        obj.addWord(word);
    }

    void addSymbol(MessageChannel c, String symbol){
        if(symbols == null){
            symbols = new HashMap<>();
        }
        addToMap(symbols, symbol);
        totalWords++;
        ChannelMessagesObject obj = getChannelObject(c.getId().asLong());
        obj.addSymbol(symbol);
    }


    void addMention(MessageChannel c, long id){
        if(mentions == null){
            mentions = new HashMap<>();
        }
        totalWords++;
        addToMap(mentions, id);
        ChannelMessagesObject obj = getChannelObject(c.getId().asLong());
        obj.addMention(id);
    }

    void addToMap(HashMap<String, Integer> map, String word){
        if(map == null){
            map = new HashMap<>();
        }
        if(map.containsKey(word)){
            map.put(word, map.get(word) + 1);
        }
        else {
            map.put(word, 1);
        }
    }

    void addToMap(HashMap<Long, Integer> map, long id){
        if(map == null){
            map = new HashMap<>();
        }
        if(map.containsKey(id)){
            map.put(id, map.get(id) + 1);
        }
        else {
            map.put(id, 1);
        }
    }

    ChannelMessagesObject getChannelObject(long id){
        ChannelMessagesObject obj = null;
        for (ChannelMessagesObject c : channels) {
            if(c.id == id) obj = c;
        }

        if(obj == null){
            obj = createNewChannel(id);
        }

        return obj;
    }

    ChannelMessagesObject createNewChannel(long id){
        ChannelMessagesObject obj = new ChannelMessagesObject(id);
        channels.add(obj);
        return obj;
    }

}
