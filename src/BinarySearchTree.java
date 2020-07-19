import java.io.Serializable;
import java.util.ArrayList;

class Word implements Serializable {
    protected static final long serialVersionUID = 51L;

    String name;
    int value;

    Word(String name, int value){
        this.name = name.trim();
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            Word wObj = (Word) obj;
            //
            return wObj.name.equals(name);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


}

class BinarySearchTree implements Serializable{
    protected static final long serialVersionUID = 53L;

    /* Class containing left and right child of current node and key value*/
    class Node implements Serializable{
        protected static final long serialVersionUID = 52L;

        Word word;
        Node left, right;

        public Node(Word item) {
            word = item;
            left = right = null;
        }
    }

    // Root of BST 
    Node root;

    // Constructor 
    BinarySearchTree() {
        root = null;
    }

    // This method mainly calls insertRec() 
    void insert(Word key) {
        root = insertRec(root, key);
    }

    void deleteKey(Word key)
    {
        root = deleteRec(root, key);
    }

    /* A recursive function to insert a new key in BST */
    Node deleteRec(Node root, Word key)
    {
        /* Base Case: If the tree is empty */
        if (root == null)  return root;

        /* Otherwise, recur down the tree */
        if(key.name.equals(root.word.name)){
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            // node with two children: Get the inorder successor (smallest
            // in the right subtree)
            root.word = minValue(root.right);

            // Delete the inorder successor
            root.right = deleteRec(root.right, root.word);
        }
        else if (key.value <= root.word.value)
            root.left = deleteRec(root.left, key);
        else if (key.value > root.word.value)
            root.right = deleteRec(root.right, key);


        return root;
    }

    Word minValue(Node root)
    {
        Word minv = root.word;
        while (root.left != null)
        {
            minv = root.left.word;
            root = root.left;
        }
        return minv;
    }

    /* A recursive function to insert a new key in BST */
    Node insertRec(Node root, Word key) {

        /* If the tree is empty, return a new node */
        if (root == null) {
            root = new Node(key);
            return root;
        }

        /* Otherwise, recur down the tree */
        if (key.value <= root.word.value)
            root.left = insertRec(root.left, key);
        else
            root.right = insertRec(root.right, key);


        /* return the (unchanged) node pointer */
        return root;
    }

    // This method mainly calls InorderRec() 
    ArrayList<Word> inorder()  {
        ArrayList<Word> out = new ArrayList<>();
        inorderRec(root, out);
        return out;
    }

    // A utility function to do inorder traversal of BST 
    void inorderRec(Node root, ArrayList<Word> outputData) {
        if (root != null) {
            inorderRec(root.left, outputData);
            outputData.add(root.word);
            inorderRec(root.right, outputData);
        }
    }


} 