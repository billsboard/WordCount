import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Dictionary {
    Set<String> words = new HashSet<>();

    Dictionary() throws IOException {
        String[] wordData = getResource("/usr/share/dict/words").split("\n");
        for (String w : wordData) {
            w = w.toLowerCase();
            if(w.length() > 1 || (w.length() == 1 && (w.equals("i") || w.equals("a")))) words.add(w);
        }

    }

    String getResource(String path) throws IOException {

        StringBuilder out = new StringBuilder();

        InputStream s = new FileInputStream(new File(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        String l;
        while((l = br.readLine()) != null){
            out.append(l).append("\n");
        }
        br.close();

        return out.toString();
    }

    boolean isWord(String s){
        return words.contains(s.toLowerCase());
    }
}
