import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class BotUtils {
    static final String BOT_PREFIX = ".";
    static final String DISCORDBOTLIST_PREFIX = "::";

    static Random rand = new Random();

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm.ss 'UTC'");

    static final String emoRegex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";

    static ArrayList<Consumer<EmbedCreateSpec>> helpEmbeds = new ArrayList<>();
    static ArrayList<String> helpCategories = new ArrayList<>();
    static Dictionary dictionary;

    static boolean isPositiveInteger(String s){
        return s.matches("\\d+?");
    }

    static boolean isNumeric(String s){
        return s.matches("-?[\\d]*\\.?[\\d][\\d]*");
    }

    static boolean isInteger(String s){
        return s.matches("[-\\d]\\d+?");
    }

    static boolean isAlphaNumeric(String s){
        return s.matches("-?[a-z\\d]+?");
    }

    static String makeAlphaNumeric(String s){
        return s.replaceAll("[^a-z \\d'-]+", " ");
    }

    static String removeAlphaNumeric(String s){
        return s.replaceAll("[a-z \\d'-]+", "");
    }

    static String getResource(String path){
        StringBuilder out = new StringBuilder();

        InputStream i = BotUtils.class.getResourceAsStream(path);
        Scanner scan = new Scanner(i);

        while (scan.hasNextLine()){
            out.append(scan.nextLine());
            out.append("\n");
        }

        return out.toString();
    }

    static Message sendMessage(MessageChannel channel, String message){
        if(message.length() > 2000){
            return channel.createMessage("```Resultant message greater than 2000 characters```").block();
        }
        else{
            return channel.createMessage(message).block();
        }
    }

    static Message sendMessage(MessageChannel channel, Consumer<EmbedCreateSpec> embed){
        return channel.createEmbed(embed).block();
    }

    static void sendArgumentsError(MessageChannel channel , String command, String... argType){
        String out = "Invalid arguments! Syntax is: `" + command;
        for (String s : argType) {
            out += " [" + s + "]";
        }
        sendMessage(channel, out + "`");
    }

    static String removeCommand(String s, String cmd){
        return s.replaceFirst(Pattern.quote(cmd), "").substring(1);
    }

    static String capitalizeFirst(String s){
        if(s.length() < 2) return s.toUpperCase();
        else {
            String[] x = s.split(" ");
            StringBuilder out = new StringBuilder();
            for (String y : x) {
                if(!y.isEmpty()){
                    y = y.toLowerCase();
                    if (y.length() == 1) {
                        out.append(y.toUpperCase());
                    } else {
                        out.append(y.substring(0, 1).toUpperCase() + y.substring(1));
                    }
                    out.append(" ");
                }

            }

            return out.toString().trim();
        }
    }

    static boolean hasLetter(String s){
        return s.matches(".*[a-z].*");
    }

    static String getUserFromMention(String s){
        return s.replaceAll("[^\\d]", "");
    }

    static LinkedHashMap<String, Integer> sortHashMapByValues(
            HashMap<String, Integer> passedMap) {

        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    static LinkedHashMap<Long, Integer> sortHashMapByValuesLong(
            HashMap<Long, Integer> passedMap) {

        List<Long> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<Long, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<Long> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                long key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    static void initData() throws IOException {
        String s = getResource("data/help.txt");
        Scanner scan = new Scanner(s);

        String in = scan.nextLine();
        while (scan.hasNextLine()){
            String title = "";

            String[] data;
            StringBuilder temp = new StringBuilder();

            if(in.startsWith("[[")){
                title = in.substring(2, in.length() - 2);
                in = scan.nextLine();
                while (!in.startsWith("[[")){
                    temp.append(in);
                    temp.append("\n\n");
                    if(scan.hasNextLine()){
                        in = scan.nextLine();
                    }
                    else{
                        break;
                    }
                }
            }

            data = temp.toString().split("\n\n");

            String finalTitle = title;
            String[] finalData = data;
            Consumer<EmbedCreateSpec> spec = e -> {
                e.setDescription(finalTitle);

                StringBuilder x = new StringBuilder();
                StringBuilder y = new StringBuilder();

                for (String finalDatum : finalData) {
                    String[] z = finalDatum.split(" :: ");
                    x.append(z[0]).append("\n");
                    y.append(z[1]).append("\n");
                }

                e.addField("Command", x.toString(), true);
                e.addField("\u200b", "\u200b", true);
                e.addField("Description", y.toString(), true);

                e.setFooter("[] denotes required parameter, () denotes optional parameter\nIf optional param is left blank, the sender's context will be used", "");
            };
            helpCategories.add(title);
            helpEmbeds.add(spec);

            dictionary = new Dictionary();
        }
        scan.close();
    }

    static boolean isUnicodePrintableChar( char c ) {
        if(Character.getType(c) == Character.CONTROL) return false;
        else if((int) c == 173) return false;
        else{
            return true;
        }
    }

}
