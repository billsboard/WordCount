
import java.io.Serializable;
import java.util.HashMap;

public class ChannelMessagesObject implements Serializable {
    public static final long serialVersionUID = 43L;

    HashMap<String, Integer> wordMap = null;
    HashMap<String, Integer> dictWords = null;
    HashMap<String, Integer> nonDictWords = null;
    HashMap<Long, Integer> mentions = new HashMap<>();
    HashMap<String, Integer> symbols = new HashMap<>();

    long id;
    long totalWords = 0;


    ChannelMessagesObject(long id){
        this.id = id;
        wordMap = new HashMap<>();
        dictWords = new HashMap<>();
        nonDictWords = new HashMap<>();
    }

    ChannelMessagesObject(long id, HashMap<String, Integer> wordMap){
        this.id = id;
        this.wordMap = wordMap;
        dictWords = new HashMap<>();
        nonDictWords = new HashMap<>();
    }

    ChannelMessagesObject(long id, HashMap<String, Integer> wordMap, HashMap<String, Integer> dictWords, HashMap<String, Integer> nonDictWords){
        this.id = id;
        this.wordMap = wordMap;
        this.dictWords = dictWords;
        this.nonDictWords = nonDictWords;
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

    void addWord(String word){
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
    }


    void addSymbol(String symbol){
        if(symbols == null) {
            symbols = new HashMap<>();
        }
        addToMap(symbols, symbol);
        totalWords++;
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

    void addMention(long id){
        if(mentions == null){
            mentions = new HashMap<>();
        }
        totalWords++;
        addToMap(mentions, id);
    }
}
