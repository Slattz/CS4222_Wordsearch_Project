//Shane Slattery (19235046)
import java.util.*; //List, ArrayList, Comparator, Map, HashMap
import java.awt.Point;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.Charset;

public class WordSearchPuzzle {
    class WordExtraInfo { //Custom class to store the extra info in
        Point location; //Point.x is row; Point.y is column
        String direction;
        WordExtraInfo() { location = new Point(0,0); } //default ctor
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
                if (puzzle[row][column+i] != '\0' && puzzle[row][column+i] != word.charAt(i)) { //Basic character overlapping allowed
                    return false;
                }
            }
            return true;
        }

        else { //Has to be the other two directions
            for (int i = 0; i < word.length(); i++) {
                if (puzzle[row+i][column] != '\0' && puzzle[row+i][column] != word.charAt(i)) { //Basic character overlapping allowed
                    return false;
                }
            }
            return true;
        }
    }

    //This function tries to add a word to the puzzle
    private void addWordToPuzzle(String word) {
        final String[] directions = {"Left", "Right", "Up", "Down"}; //Basic, no diagonals
        WordExtraInfo extraInfo = new WordExtraInfo();
        String wordToAdd = new String(word);
        boolean added = false;

        for (int attempts = 0; !added && attempts < 100; attempts++) { //Try 100 times to add word in random places to make sure not unlucky
            String direction = directions[(int)(Math.random() * directions.length)];
            boolean horizontal = ("Left".equals(direction) || "Right".equals(direction));

            int rowMax = puzzle.length - (horizontal ? 1 : word.length()); //no need to -1 from each length as they minus each other
            int columnMax = puzzle[0].length - (!horizontal ? 1 : word.length());
            int row = (int)(Math.random() * rowMax); //no need for a minimum value as we always start from 0,0 for each word
            int column = (int)(Math.random() * columnMax);

            if ("Left".equals(direction) || "Up".equals(direction)) {
                wordToAdd = new StringBuilder(word).reverse().toString(); //reverse word so it's treated like right/down
            }

            if (canWordFit(wordToAdd, direction, row, column)) { //If the word can fit in the location, we add it to the wordsearch
                added = true;
                extraInfo.location.x = row;
                extraInfo.location.y = column;
                extraInfo.direction = direction;

                switch (direction) {
                    case "Left":
                        extraInfo.location.y += wordToAdd.length()-1; // Words going left are reversed so start pos is length; fall-through to "Right"
                    case "Right":
                        for (int i = 0; i < wordToAdd.length(); i++) {
                            puzzle[row][column+i] = wordToAdd.charAt(i);
                        }
                    break;

                    case "Up":
                        extraInfo.location.x += wordToAdd.length()-1; // Words going up are reversed so start pos is length; fall-through to "Down"
                    case "Down":
                        for (int i = 0; i < wordToAdd.length(); i++) {
                            puzzle[row+i][column] = wordToAdd.charAt(i);
                        }
                    break;
                }
                puzzleWordsInfo.put(word, extraInfo);
            }
        }
    }

    private void generateWordSearchPuzzle() {
        puzzleWords.sort(new Comparator<String>() { //sort in descending order so biggest words at beginning and easier to place
            public int compare(String x, String y) {
                return y.length() - x.length();
            }
        });

        float charTotal = 0;
        for (String string : puzzleWords) {
            charTotal += string.length();
        }

        int gridDim = Math.max((int)Math.ceil(Math.sqrt(charTotal*1.75f)), puzzleWords.get(0).length()); //Math.ceil rounds result of sqrt up to a whole number
        puzzle = new char[gridDim][gridDim]; // rectangular array

        for (int i = 0; i < puzzleWords.size(); i++) {
            puzzleWords.set(i, puzzleWords.get(i).toUpperCase()); //Make the words all uppercase
            addWordToPuzzle(puzzleWords.get(i));
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

    //WordSearchPuzzle ctor, using user-definied list of words
    public WordSearchPuzzle(List<String> userSpecifiedWords) {
        puzzleWords = new ArrayList<String>(userSpecifiedWords);
        puzzleWordsInfo = new HashMap<String, WordExtraInfo>();
        generateWordSearchPuzzle();
    }

    //WordSearchPuzzle ctor, using a file with a list of words
    public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
        puzzle = new char[0][0]; //Prevent crashing incase file not found
        puzzleWords = new ArrayList<String>();
        puzzleWordsInfo = new HashMap<String, WordExtraInfo>();
        ArrayList<String> words = null;
        try {
            Path filePath = FileSystems.getDefault().getPath(wordFile);
            words = new ArrayList<String>(Files.readAllLines(filePath, Charset.defaultCharset()));
        }
        catch (IOException x) {
            System.err.printf("File \"%s\" Not Found!\n", wordFile);
            return;
        }

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
                words.remove(rand); //So we dont get duplicates
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
        StringBuilder wordList = new StringBuilder(); //Faster to prepare string and print once rather than printing multiple times

        for (String word : puzzleWords) { //Get each word
            wordList.append(String.format("%-20s", word)); //pretty print the words

            if (!hide && puzzleWordsInfo.containsKey(word)) { //if hide is false, we print the location and direction info too (if it exists)
                WordExtraInfo info = puzzleWordsInfo.get(word);
                wordList.append(String.format(" [Location: Row %02d, Column %02d; Direction: %s]", info.location.x, info.location.y, info.direction));
            }
            wordList.append('\n');
        }
        System.out.print(wordList);
    }
}