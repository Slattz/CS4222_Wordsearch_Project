import java.util.ArrayList;
import java.util.Arrays;

public class WordSearchDriver {
    public static void main(String[] args) {
        String words = "animal crossing cowboard such amazing great game";
        ArrayList<String> lol = new ArrayList<String>(Arrays.asList(words.split(" ")));
        WordSearchPuzzle puz = new WordSearchPuzzle(lol);

        puz.showWordSearchPuzzle(false);
        System.out.println("\n\n" + puz.getPuzzleAsString());
    }
}