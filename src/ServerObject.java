import jdk.nashorn.api.tree.Tree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ServerObject implements Serializable{

    private static final long serialVersionUID = 15L;

    long lastAccessTime = 0L;

    String serverID;

    BinarySearchTree serverOverallData = new BinarySearchTree();
    HashMap<String, Integer> serverOverallMap = new HashMap<>();

    HashMap<String, TreeMapPair> channelPairs = new HashMap<>();

    int totalWords = 0;

    ServerObject(String serverID){
        this.serverID = serverID;
    }

    void addChannel(String channelID){
        channelPairs.put(channelID, new TreeMapPair(new BinarySearchTree(), new HashMap<>()));
    }

    void addWord(String channelID, String word) throws IOException {

        Word w = new Word(word, serverOverallMap.getOrDefault(word, 0));
        serverOverallData.deleteKey(w);
        w = new Word(w.name, w.value + 1);
        serverOverallData.insert(w);
        serverOverallMap.put(w.name, w.value);

        loadChannel(channelID);

        TreeMapPair pair = channelPairs.get(channelID);
        Word w1 = new Word(word, pair.map.getOrDefault(word, 0));
        pair.tree.deleteKey(w1);
        w1 = new Word(w1.name, w1.value + 1);
        pair.tree.insert(w1);
        pair.map.put(w1.name, w1.value);

        if(!word.startsWith("SYMBOL::")){
            totalWords++;
            pair.totalWords++;
        }
    }

    void loadChannel(String channelID) throws IOException {
        if(!channelPairs.containsKey(channelID)){
            File serverPath = new File(DataProcessor.savePath, serverID);
            File channelData = new File(serverPath, channelID + ".ser");

            if(!serverPath.isDirectory()) serverPath.mkdir();

            try {
                ObjectInputStream stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(channelData)));
                TreeMapPair pair = (TreeMapPair) stream.readObject();
                channelPairs.put(channelID, pair);
            }catch (NoSuchFileException | FileNotFoundException e){
                addChannel(channelID);
            }
            catch (StreamCorruptedException | EOFException e) {
                System.err.println("Error reading data file for " + channelID + ", moving file and recreating...");
                File corruptedDir = new File(DataProcessor.savePath, "corruptedFiles");
                if(!corruptedDir.isDirectory()) corruptedDir.mkdir();
                File file = new File(corruptedDir, channelID + "-corrupted.ser");
                Files.copy(channelData.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                addChannel(channelID);
            } catch (ClassNotFoundException | IOException ignored) {
                System.err.println("[Read]: Random " + ignored.getClass().getName() + " has occurred while processing channel " + channelID);
            }
        }

        if (channelPairs.get(channelID) == null){
            addChannel(channelID);
        }

        channelPairs.get(channelID).lastAccessTime = new Date().getTime();

    }

    void saveChannel(String channelID){
        if(!channelPairs.containsKey(channelID)) channelPairs.put(channelID, new TreeMapPair(new BinarySearchTree(), new HashMap<>()));

        File serverPath = new File(DataProcessor.savePath, serverID);
        File channelData = new File(serverPath, channelID + ".ser");

        if(!serverPath.isDirectory()) serverPath.mkdir();

        try {
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(channelData)));
            output.writeObject(channelPairs.get(channelID));
            output.close();
            channelPairs.remove(channelID);
        } catch (IOException ignored) {
        }
    }

    void saveChannelNoRemove(String channelID){
        if(!channelPairs.containsKey(channelID)) channelPairs.put(channelID, new TreeMapPair(new BinarySearchTree(), new HashMap<>()));

        File serverPath = new File(DataProcessor.savePath, serverID);
        File channelData = new File(serverPath, channelID + ".ser");

        if(!serverPath.isDirectory()) serverPath.mkdir();

        try {
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(channelData)));
            output.writeObject(channelPairs.get(channelID));
            output.close();
        } catch (IOException ignored) {
        }
    }

    ArrayList<Word> getChannelDataOrdered(String channelID) throws IOException {
        loadChannel(channelID);
        TreeMapPair pair = channelPairs.get(channelID);
        return pair.tree.inorder();
    }

    TreeMapPair getChannelPair(String channelID) throws IOException {
        loadChannel(channelID);

        return channelPairs.get(channelID);
    }



    static class TreeMapPair implements Serializable {
        private static final long serialVersionUID = 11L;

        long lastAccessTime = 0L;
        int totalWords = 0;

        BinarySearchTree tree;
        HashMap<String, Integer> map;

        TreeMapPair(BinarySearchTree tree, HashMap<String, Integer> map){
            this.tree = tree;
            this.map = map;
        }

    }

}
