import java.util.*; //List, ArrayList, Collections, Comparator, Map, HashMap
import java.awt.Point;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.Charset;

public class WordSearchPuzzle {
    class WordExtraInfo { //Custom class to store the extra info in
        Point location; //Point.x is row; Point.y is column
        String direction;
        WordExtraInfo(String dir) { direction = dir; location = new Point(0,0); } //default ctor
    }

    /* Member Variables */
    private char[][] puzzle;
    private List<String> puzzleWords;
    private Map<String, WordExtraInfo> puzzleWordsInfo; //map the words to extra info 

    /* Private Functions */
    //This helper function checks if a word can fit on the grid in a specific location
    private boolean canWordFit(String word, String direction, int row, int column) {
        if (row < 0 || row >= puzzle.length || column < 0 || column >= puzzle[row].length) { //OOB checks
            return false;
        }

        if ("Left".equals(direction) || "Right".equals(direction)) {
            for (int i = 0; i < word.length(); i++) {
                if (puzzle[row][column+i] != '\0' && puzzle[row][column+i] != word.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        else if ("Up".equals(direction) || "Down".equals(direction)) {
            for (int i = 0; i < word.length(); i++) {
                if (puzzle[row+i][column] != '\0' && puzzle[row+i][column] != word.charAt(i)) {
                    return false;
                }
            }
            return true;
        
        }
        return false;
    }

    //This function tries to add a word to the puzzle is a specific direction
    private void addWordToPuzzle(String word, String direction) { //Left, Right, Up, Down
        boolean added = false;
        int rowMax, columnMax;
        String wordToAdd = word;
        WordExtraInfo extraInfo = new WordExtraInfo(direction);

        switch (direction) {
            case "Left":
                wordToAdd = new StringBuilder(word).reverse().toString(); //reverse word
                extraInfo.location.x = wordToAdd.length(); // Words going left are reversed so start pos is length; fall-through to "Right"
            case "Right":
                rowMax = puzzle.length-1;
                columnMax = puzzle[0].length-wordToAdd.length()-2; //-2 as need to minus 1 from each length
            break;
                
            case "Up":
                wordToAdd = new StringBuilder(word).reverse().toString(); //reverse word
                extraInfo.location.y = wordToAdd.length(); // Words going up are reversed so start pos is length; fall-through to "Down"
            case "Down":
                rowMax = puzzle.length-wordToAdd.length()-2; //-2 as need to minus 1 from each length
                columnMax = puzzle[0].length-1;
            break;
        
            default: //Anything invalid (e.g. memory edits/corruption) return so nothing messes up
                return;
        }

        for (int attempts = 0; !added && attempts < 100; attempts++) { //Try 100 times to add word in random places, within the bounds we set
            int row = (int)(Math.random() * rowMax); //no need for a minimum value as we always start from 0,0 for each word
            int column = (int)(Math.random() * columnMax);
            if (canWordFit(wordToAdd, direction, row, column)) { //If the word can fit in the location, we add it to the wordsearch
                added = true;
                extraInfo.location.x += row; //+= as may already have a value if word is reversed
                extraInfo.location.y += column;
                puzzleWordsInfo.put(word, extraInfo);

                if ("Left".equals(direction) || "Right".equals(direction)) {
                    for (int i = 0; i < wordToAdd.length(); i++) {
                        puzzle[row][column+i] = wordToAdd.charAt(i);
                    }
                }

                else { //No need to seperately check "Up" and "Down" again: direction is already checked for invalidness in the switch statement
                    for (int i = 0; i < wordToAdd.length(); i++) {
                        puzzle[row+i][column] = wordToAdd.charAt(i);
                    }
                }
            }
            //else System.out.printf("[DEBUG] Attempt: %d; Couldn't add %s to row %d col %d, dir %s\n", attempts, wordToAdd, row, column, extraInfo.direction);
        }
    }

    private void generateWordSearchPuzzle() {
        final String[] directions = {"Left", "Right", "Up", "Down"}; //Basic, no diagonals

        Collections.sort(puzzleWords, new Comparator<String>() { //sort in descending order so biggest words at beginning and easier to place
                public int compare(String x, String y) {
                return y.length() - x.length();
            }
        });

        float charTotal = 0;
        for (String string : puzzleWords) {
            charTotal += string.length();
        }

        charTotal *= 1.5f; //multiply by scaling factor
        int gridDim = Math.max((int)Math.ceil(Math.sqrt(charTotal)), puzzleWords.get(0).length()); //Math.sqrt gets square root of charTotal; Math.ceil rounds result of sqrt up to a whole number
        puzzle = new char[gridDim][gridDim]; // rectangular array

        for (int i = 0; i < puzzleWords.size(); i++) {
            puzzleWords.set(i, puzzleWords.get(i).toUpperCase()); //Make the words all uppercase
            addWordToPuzzle(puzzleWords.get(i), directions[(int)(Math.random() * directions.length)]);
        }

        // x is row; y is column; fill in the empty slots in wordsearch here
        for (int x = 0; x < puzzle.length; x++) {
            for (int y = 0; y < puzzle[x].length; y++) {
                if (puzzle[x][y] == '\0') {
                    puzzle[x][y] = (char)((int)(Math.random()*26)+'A'); //Generate a random letter
                }
            }
        }        
    }

    //This helper function is used to read a text file based on fileName and return an ArrayList
    private ArrayList<String> readWordsFromFile(String fileName) {
        try {
            /*
            FileReader aFileReader = new FileReader(fileName);
            BufferedReader aBufferReader = new BufferedReader(aFileReader);

            ArrayList<String> wordList = new ArrayList<String>();

            String aWord = aBufferReader.readLine();
            while (aWord != null) {
                wordList.add(aWord.toUpperCase());
                aWord = aBufferReader.readLine();
            }

            aBufferReader.close();
            aFileReader.close();
            return wordList;
            */
            
            Path filePath = FileSystems.getDefault().getPath(fileName);
            return new ArrayList<String>(Files.readAllLines(filePath, Charset.defaultCharset()));
        } 
        
        catch (IOException x) {
            return null;
        }
    }

    //WordSearchPuzzle ctor, using user-definied list of words
    public WordSearchPuzzle(List<String> userSpecifiedWords) {
        puzzleWords = new ArrayList<String>(userSpecifiedWords);
        puzzleWordsInfo = new HashMap<String, WordExtraInfo>();
        generateWordSearchPuzzle();
    }

    //WordSearchPuzzle ctor, using a file with a list of words
    public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
        ArrayList<String> words = readWordsFromFile(wordFile);
        puzzleWords = new ArrayList<String>();
        puzzleWordsInfo = new HashMap<String, WordExtraInfo>();

        if (words != null && wordCount > 0 && shortest <= longest) {
            for (int i = 0; i < words.size(); i++) { //Remove words that are too small or big first
                int len = words.get(i).length();
                if (len < shortest || len > longest) {
                    words.remove(i--); //i-- is needed as everything in the ArrayList gets shifted up when we remove, so i would be ahead
                }
            }

            while (puzzleWords.size() < wordCount && !words.isEmpty()) {
                int rand = (int)(Math.random()*words.size());
                puzzleWords.add(words.get(rand));
                words.remove(rand);
            }
        }
        generateWordSearchPuzzle();
    }

    public List<String> getWordSearchList() {
        return puzzleWords;
    }

    public char[][] getPuzzleAsGrid() {
        return puzzle;
    }

    public String getPuzzleAsString() {
        StringBuilder str = new StringBuilder(); //give StringBuilder rough size so we don't repeatedly allocate memory
        
        for (int row = 0; row < puzzle.length; row++) {
            str.append('|');
            for (int col = 0; col < puzzle[row].length; col++) {
                str.append(puzzle[row][col]);
            }
            str.append("|\n");
        }

        return str.toString();
    }

    public void showWordSearchPuzzle(boolean hide) {
        System.out.printf("WordSearch:\n\n");
        System.out.println(getPuzzleAsString());

        //Now, print the words
        for (String word : puzzleWords) {
            System.out.printf("%-20s", word); //pretty print the words

            if (!hide && puzzleWordsInfo.containsKey(word)) { //if hide is false, we print the location and direction info too (if it exists)
                WordExtraInfo info = puzzleWordsInfo.get(word);
                System.out.printf(" [Location: Row %02d, Column %02d; Direction: %s]", info.location.x, info.location.y, info.direction);
            }
            System.out.println();
        }
    }
}