import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Tester {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /*
        BinarySearchTree binarySearchTree = new BinarySearchTree();

        binarySearchTree.insert(new Word("Test", 100));

        Random rand = new Random();

        System.out.println("Adding");

        for (int i = 0; i < 2000000; i++) {
            binarySearchTree.insert(new Word("" + rand.nextInt(Integer.MAX_VALUE), rand.nextInt()));
        }

        System.out.println("Done");
        ArrayList<Word> items = binarySearchTree.inorder();
        for (int i = items.size() - 1; i > items.size() - 11; i--) {
            System.out.println(items.get(i).name + ": " + items.get(i).value);
        }
        System.out.println("Done, updating one");
        binarySearchTree.deleteKey(new Word("Test", 100));
        binarySearchTree.insert(new Word("Test", Integer.MAX_VALUE));

           items = binarySearchTree.inorder();
        for (int i = items.size() - 1; i > items.size() - 11; i--) {
            System.out.println(items.get(i).name + ": " + items.get(i).value);
        }*/


        String serverID = "TestServer";
        String CID = "001";

        /*DataProcessor.addToFile(serverID, CID, "Ki");
        DataProcessor.addToFile(serverID, CID, "Ki");
        DataProcessor.addToFile(serverID, CID, "Ki");
        DataProcessor.addToFile(serverID, CID, "Ki");
        DataProcessor.addToFile(serverID, CID, "Rai");
        DataProcessor.addToFile(serverID, CID, "Rai");
        DataProcessor.addToFile(serverID, CID, "Rai");*/

        //DataProcessor.serverTop10(serverID).forEach(x -> {System.out.println(x.name + ": " + x.value);});

    }
}
