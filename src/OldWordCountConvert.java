import java.io.*;
import java.util.*;

public class OldWordCountConvert {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if(args.length < 1 ){
            System.out.println("Usage: convertFormat.jar [path/to/old/data/file]");
            return;
        }

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(args[0])));

        ArrayList<ServerMessagesObject> objects = (ArrayList<ServerMessagesObject>) in.readObject();

        File dir = new File("wordData");
        if(!dir.isDirectory()) dir.mkdir();

        for (ServerMessagesObject server : objects) {

            System.out.println("Now writing server with ID " + server.id);

            BinarySearchTree serverTree = new BinarySearchTree();
            HashMap<String, Integer> serverMap = new HashMap<>();

            int totalWords = 0;

            if(server.wordMap != null) {
                for (Map.Entry<String, Integer> entry : server.wordMap.entrySet()
                ) {
                    serverMap.put(entry.getKey().toLowerCase(), entry.getValue());
                    serverTree.insert(new Word(entry.getKey().toLowerCase(), serverMap.get(entry.getKey().toLowerCase())));
                    totalWords += serverMap.get(entry.getKey().toLowerCase());
                }
            }

            if(server.symbols != null) {
                for (Map.Entry<String, Integer> entry : server.symbols.entrySet()
                ) {
                    serverMap.put("SYMBOL::" + entry.getKey(), entry.getValue());
                    serverTree.insert(new Word("SYMBOL::" + entry.getKey(), serverMap.get("SYMBOL::" + entry.getKey())));
                }
            }

            if(server.mentions != null) {
                for (Map.Entry<Long, Integer> entry : server.mentions.entrySet()
                ) {
                    serverMap.put("MENTION::" + entry.getKey(), entry.getValue());
                    serverTree.insert(new Word("MENTION::" + entry.getKey(), serverMap.get("MENTION::" + entry.getKey())));
                    totalWords += serverMap.get("MENTION::" + entry.getKey());
                }
            }

            ServerObject obj = new ServerObject(server.id + "");
            obj.serverOverallData = serverTree;
            obj.serverOverallMap = serverMap;
            obj.totalWords = totalWords;

            for (ChannelMessagesObject c : server.channels) {
                System.out.println("      - Now writing channel with ID " + c.id);

                BinarySearchTree channelTree = new BinarySearchTree();
                HashMap<String, Integer> channelMap = new HashMap<>();
                int cTotal = 0;

                if(c.wordMap != null) {
                    for (Map.Entry<String, Integer> entry : c.wordMap.entrySet()
                    ) {
                        channelMap.put(entry.getKey().toLowerCase(), entry.getValue());
                        channelTree.insert(new Word(entry.getKey().toLowerCase(), channelMap.get(entry.getKey().toLowerCase())));
                        cTotal += channelMap.get(entry.getKey().toLowerCase());
                    }
                }

                if(c.symbols != null) {
                    for (Map.Entry<String, Integer> entry : c.symbols.entrySet()
                    ) {
                        channelMap.put("SYMBOL::" + entry.getKey(), entry.getValue());
                        channelTree.insert(new Word("SYMBOL::" + entry.getKey(), channelMap.get("SYMBOL::" + entry.getKey())));
                    }
                }

                if(c.mentions != null) {
                    for (Map.Entry<Long, Integer> entry : c.mentions.entrySet()
                    ) {
                        channelMap.put("MENTION::" + entry.getKey(), entry.getValue());
                        channelTree.insert(new Word("MENTION::" + entry.getKey(), channelMap.get("MENTION::" + entry.getKey())));
                        cTotal += channelMap.get("MENTION::" + entry.getKey());
                    }
                }

                ServerObject.TreeMapPair pair = new ServerObject.TreeMapPair(channelTree, channelMap);
                pair.totalWords = cTotal;

                File f = new File(DataProcessor.savePath, obj.serverID);
                if (!f.isDirectory()) f.mkdir();

                File serverPath = new File(DataProcessor.savePath, obj.serverID);
                File channelData = new File(serverPath,  c.id + ".ser");

                if(!serverPath.isDirectory()) serverPath.mkdir();

                try {
                    ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(channelData)));
                    output.writeObject(pair);
                    output.close();

                } catch (IOException ignored) {
                }
            }

            File f = new File(DataProcessor.savePath, obj.serverID);
            if (!f.isDirectory()) f.mkdir();

            File serverData = new File(f, "serverData.ser");


            ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(serverData)));
            outputStream.writeObject(obj);
            outputStream.close();
        }

    }
}
