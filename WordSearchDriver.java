//Shane Slattery (19235046)
import java.util.ArrayList;
import java.util.Arrays;

public class WordSearchDriver {
    public static void main(String[] args) {
        ArrayTest1();
        ArrayTest2();
        FileTest1();
        FileTest2();
    }

    public static void ArrayTest1() {
        final String words = "Animal Crossing New Horizons released March Amazing and Fantastic";
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(words.split(" ")));

        System.out.print("\n--Instance #1--\nCreating Wordsearch using a list of words.\n");
        WordSearchPuzzle puz1 = new WordSearchPuzzle(list);
        
        System.out.print("\nShowing Wordsearch #1 with solutions hidden:\n\n");
        puz1.showWordSearchPuzzle(true);
        
        System.out.print("\nNow showing Wordsearch #1 with solutions shown:\n\n");
        puz1.showWordSearchPuzzle(false);
        
        System.out.print("\nGetting list of words in Wordsearch #1 and printing:\n\n");
        System.out.println(puz1.getWordSearchList());
    }

    public static void ArrayTest2() {
        String words = "Starships were meant to fly Hands up and touch the sky Cant stop cause were so high Lets do this one more time";
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(words.split(" ")));

        System.out.print("\n--Instance #2--\nCreating Wordsearch #2 using a different list of words.\n\n");
        WordSearchPuzzle puz = new WordSearchPuzzle(list);

        System.out.print("\nShowing Wordsearch #2 with solutions hidden:\n\n");
        puz.showWordSearchPuzzle(true);

        System.out.print("\nNow showing Wordsearch #2 with solutions shown:\n\n");
        puz.showWordSearchPuzzle(false);

        System.out.print("\nGetting Wordsearch #2 as grid and printing:\n\n");
        System.out.println(Arrays.deepToString(puz.getPuzzleAsGrid()).replace("], ", "]\n").replace("[[", "["));

        System.out.print("\nGetting Wordsearch #2 as String and printing:\n\n");
        System.out.println(puz.getPuzzleAsString());
    }

    public static void FileTest1() {
        System.out.print("\n--Instance #3--\nCreating Wordsearch #3 using 12 words from \"BasicEnglish.txt\" between length 4 and 10 (inclusive).\n\n");
        WordSearchPuzzle filepuz1 = new WordSearchPuzzle("BasicEnglish.txt", 12, 4, 10);
        
        System.out.print("\nShowing Wordsearch #3 with solutions hidden:\n\n");
        filepuz1.showWordSearchPuzzle(true);

        System.out.print("\nNow showing Wordsearch #3 with solutions shown:\n\n");
        filepuz1.showWordSearchPuzzle(false);

        System.out.print("\nGetting Wordsearch #3 as grid and printing.\n\n");
        System.out.println(Arrays.deepToString(filepuz1.getPuzzleAsGrid()).replace("], ", "]\n").replace("[[", "["));
    }

    public static void FileTest2() {
        System.out.print("\n--Instance #4--\nCreating Wordsearch #4 using 35 words from \"BNCwords.txt\" between length 5 and 15 (inclusive).\n\n");
        WordSearchPuzzle filepuz2 = new WordSearchPuzzle("BNCwords.txt", 35, 5, 15);
        
        System.out.print("\nShowing Wordsearch #4 with solutions hidden:\n\n");
        filepuz2.showWordSearchPuzzle(true);

        System.out.print("\nNow showing Wordsearch #4 with solutions shown:\n\n");
        filepuz2.showWordSearchPuzzle(false);

        System.out.print("\nGetting list of words in Wordsearch #4 and printing:\n\n");
        System.out.println(filepuz2.getWordSearchList());

        System.out.print("\nGetting Wordsearch #4 as String and printing:\n\n");
        System.out.println(filepuz2.getPuzzleAsString());
    }
}