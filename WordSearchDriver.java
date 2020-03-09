import java.util.ArrayList;
import java.util.Arrays;

public class WordSearchDriver {
    public static void main(String[] args) {
        String words = "animal crossing cowboard such amazing great game";
        ArrayList<String> lol = new ArrayList<String>(Arrays.asList(words.split(" ")));
        WordSearchPuzzle puz = new WordSearchPuzzle(lol);

        puz.showWordSearchPuzzle(false);
        System.out.println("\n\n" + puz.getPuzzleAsString() + "\n\n\n");


        WordSearchPuzzle fpuzzle = new WordSearchPuzzle("text.txt", 10, 4, 10);

        fpuzzle.showWordSearchPuzzle(false);
        System.out.println("\n\n" + fpuzzle.getPuzzleAsString() + "\n\n\n");
    }
}