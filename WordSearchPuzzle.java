import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Point;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;

public class WordSearchPuzzle {
    class WordExtraInfo {
        Point location; //Point.x is row; Point.y is column
        String direction;

        WordExtraInfo() { location = new Point(); }
    }

    private char[][] puzzle;
    private List<String> puzzleWords;
    private List<WordExtraInfo> puzzleWordsInfo; //Change to Map in future so the words can be used as keys rather than dpeending on being in same index

    private boolean canWordFit(String word, String direction, int row, int column) {
        //System.out.printf("Trying \"%s\"; Direction: %s; Row: %d; Col: %d\n", word, direction, row, column);
        switch (direction) { //switch case for the least amount of duped code
            case "Left":
            case "Right":
                for (int i = 0; i < word.length(); i++) {
                    if (puzzle[row][column+i] != '\0' && puzzle[row][column+i] != word.charAt(i)) {
                        //System.out.printf("\"%s\" cant fit; Direction: %s; Row: %d; Col: %d\n", word, direction, row, column);
                        return false;
                    }
                }
            return true;

            case "Up":
            case "Down":
                for (int i = 0; i < word.length(); i++) {
                    if (puzzle[row+i][column] != '\0' && puzzle[row+i][column] != word.charAt(i)) {
                        //System.out.printf("Up/Down: validSpaces (len: %d) != word.length() (len: %d) for word %s\n", i, word.length(), word);
                        return false;
                    }
                }
            return true;

            default:
                System.out.println("Hit default in switch! Direction: " + direction);
            return false;
        }
    }

    //return true if word added successfully, false if can't fit / error
    private boolean addWord(String word, String direction, WordExtraInfo extraInfo) {
        extraInfo.direction = direction;
        int row = extraInfo.location.x, column = extraInfo.location.y;

        switch (direction) { //switch case for the least amount of duped code
            case "Left":
                extraInfo.location.x += word.length(); //words going left are reversed earlier
            case "Right":
                for (int i = 0; i < word.length(); i++) { //No need to validate spaces again as done above
                    puzzle[row][column+i] = word.charAt(i);
                }
            return true;

            case "Up":
                extraInfo.location.y += word.length(); //words going up are reversed earlier;
            case "Down":
                for (int i = 0; i < word.length(); i++) { //No need to validate spaces again as done above
                    puzzle[row+i][column] = word.charAt(i);
                }
            return true;

            default:
                System.out.println("Hit default in switch! Direction: " + direction);
            return false;
        }
    }

    private void addWordToPuzzleBasic(String word, String direction) { //Left, Right, Up, Down
        boolean added = false;
        int rowMin, rowMax, columnMin, columnMax;
        String wordToAdd = new String(word);

        switch (direction) {
            case "Left":
                wordToAdd = new StringBuilder(word).reverse().toString(); //reverse word, then fallthrough to "Right"
            case "Right":
                rowMin = 0; rowMax = puzzle.length-1;
                columnMin = 0; columnMax = puzzle[0].length-1-wordToAdd.length()-1;
            break;
                
            case "Up":
                wordToAdd = new StringBuilder(word).reverse().toString(); // reverse word, then fallthrough to "Down"
            case "Down":
                rowMin = 0; rowMax = puzzle.length-1-wordToAdd.length()-1;
                columnMin = 0; columnMax = puzzle[0].length-1;
            break;
        
            default: 
                return;
        }

        WordExtraInfo info = new WordExtraInfo();
        int attempts;
        for (attempts = 0; !added && attempts < 100; attempts++) { //Try 100 times to add word
            info.location.x = rowMin + (int)(Math.random() * (rowMax - rowMin));
            info.location.y = columnMin + (int)(Math.random() * (columnMax - columnMin));
            if (canWordFit(wordToAdd, direction, info.location.x, info.location.y)) {
                if (addWord(wordToAdd, direction, info)) {
                    added = true;
                    puzzleWordsInfo.add(info);
                }
            }
        }

        if (attempts >= 100) {
            //System.out.printf("Couldn't fit word \"%s\" in direction: %s\n", word, direction);
        }
    }

    private void generateWordSearchPuzzle() {
        //final String[] directions = {"Left", "Right", "Up", "Down", "Diagonal Left Down", "Diagonal Left Up", "Diagonal Right Down", "Diagonal Right Up"};
        final String[] directions = {"Left", "Right", "Up", "Down"}; //Basic

        for (String word : puzzleWords) {
            int randDir = (int)(Math.random() * directions.length);
            String direction = directions[randDir];
            word = word.toUpperCase();

            switch (direction) {
                case "Left":
                case "Right":
                case "Up":
                case "Down":
                    addWordToPuzzleBasic(word, direction);
                break;
            
                default:
                    System.out.println("Hit default in switch! Direction: " + direction);
                break;
            }
        }

        // x is row; y is column; fill in the empty slots in wordsearch here
        for (int x = 0; x < puzzle.length; x++) {
            for (int y = 0; y < puzzle[x].length; y++) {
                if (puzzle[x][y] == '\0') {
                    puzzle[x][y] = (char)((int)(Math.random()*26)+'A');
                }
            }
        }        
    }

    private ArrayList<String> readWordsFromFile(String fileName) {
        try {
            ///*
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
            //*/
            
            //Path filePath = FileSystems.getDefault().getPath(fileName);
            //return Files.readAllLines(filePath, Charset.defaultCharset());
        } 
        
        catch (IOException x) {
            return null;
        }
    }

    private int getGridDimensions(List<String> words) {
        if (words.size() <= 0)
            return 0;

        double charTotal = 0;
        for (String string : words) {
            charTotal += string.length();
        }

        charTotal *= 2.25f; //multiply by scaling factor
        return (int)Math.ceil(Math.sqrt(charTotal)); //Math.sqrt gets square root of charTotal; Math.ceil rounds result of sqrt up to a whole number
    }


    // The dimensions of the puzzle grid should be set by summing the lengths of the words being used in the puzzle and 
    // multiplying the sum by 1.5 or 1.75 or some other (appropriate) scaling factor to ensure that the grid will have 
    // enough additional characters to obscure the puzzle words.

    // Once you have calculated how many characters you are going to have in the grid, you can calculate the grid dimensions by 
    // getting the square root (rounded up) of the character total.

    public WordSearchPuzzle(List<String> userSpecifiedWords) {
        int gridDim = getGridDimensions(userSpecifiedWords);
        puzzleWords = new ArrayList<String>(userSpecifiedWords);
        puzzleWordsInfo = new ArrayList<WordExtraInfo>();
        puzzle = new char[gridDim][gridDim]; //rectangular array
        generateWordSearchPuzzle();
    }

    // Puzzle generation using words from a file:
        // The user supplies the filename. In the file the words should appear one per line.
        // The wordCount specifies the number of words to (randomly) select from the file for use in the puzzle.
        // 'shortest' specify the shortest word length to be used and 'longest' specifies the longest word length to be used.

    // So, using the words in the file, randomly select 'wordCount' words with lengths between shortest and longest.
    public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
        ArrayList<String> words = readWordsFromFile(wordFile);
        puzzleWords = new ArrayList<String>();
        puzzleWordsInfo = new ArrayList<WordExtraInfo>();

        if (words != null && wordCount > 0 && shortest < longest) {
            for (int i = 0; i < words.size(); i++) { //Remove words that are too small or big
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

        int gridDim = getGridDimensions(puzzleWords);
        puzzle = new char[gridDim][gridDim]; // rectangular array
        generateWordSearchPuzzle();
    }

    public List<String> getWordSearchList() {
        return puzzleWords;
    }

    public char[][] getPuzzleAsGrid() {
        return puzzle;
    }

    public String getPuzzleAsString() {
        StringBuilder str = new StringBuilder(puzzle.length*puzzle[0].length);
        
        for (int row = 0; row < puzzle.length; row++) {
            for (int col = 0; col < puzzle[row].length; col++) {
                str.append(puzzle[row][col]);
            }
            str.append('\n');
        }

        return str.toString();
    }

    public void showWordSearchPuzzle(boolean hide) {
        System.out.println("WordSearch\n----------");
        System.out.println(getPuzzleAsString());

        //Now, print the words
        for (int i = 0; i < puzzleWords.size(); i++) {
            System.out.printf("%-20s", puzzleWords.get(i)); //pretty print the word

            if (!hide && i < puzzleWordsInfo.size()) { //if false, we print the location and direction info too
                Point pos = puzzleWordsInfo.get(i).location;
                System.out.printf(" Location: Row=[%02d] Column=[%02d]\tDirection: [%s]", pos.x, pos.y, puzzleWordsInfo.get(i).direction);
            }
            System.out.print('\n');
        }
    }


}
