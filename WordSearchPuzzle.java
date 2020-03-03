import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Point;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;

public class WordSearchPuzzle {
    class WordExtraInfo {
        Point location;
        String direction;
    }

    private char[][] puzzle;
    private List<String> puzzleWords;
    private List<WordExtraInfo> puzzleWordsInfo; //Change to Map in future so the words can be used as keys rather than dpeending on being in same index

    //return true if word added successfully, false if can't fit / error
    private boolean addWord(String word, String direction, int xPos, int yPos, WordExtraInfo extraInfo) {
        int tempX = xPos, tempY = yPos, validSpaces = 0;

        extraInfo.direction = direction;
        switch (direction) { //switch case for the least amount of duped code
            case "Right":
            case "Left":
                extraInfo.location.x = (direction.equals("Right") ? xPos+word.length() : xPos); //words going right are reversed earlier
                extraInfo.location.y = yPos;

                for (int i = 0; i < word.length(); i++) {
                    if (puzzle[xPos][yPos+i] == '\0' || puzzle[xPos][yPos+i] == word.charAt(i)) {
                        validSpaces++;
                    }
                }

                if (validSpaces != word.length()) {
                    System.out.printf("Left/Right: validSpaces (len: %d) != word.length() (len: %d) for word %s\n", validSpaces, word.length(), word);
                    return false;
                }

                for (int i = 0; i < word.length(); i++) { //No need to validate spaces again as done above
                    puzzle[xPos][yPos+i] = word.charAt(i);
                }
            return true;

            case "Up":
            case "Down":
                extraInfo.location.x = xPos;
                extraInfo.location.y = (direction.equals("Up") ? yPos+word.length() : yPos); //words going up are reversed earlier;

                for (int i = 0; i < word.length(); i++) {
                    if (puzzle[xPos+i][yPos] == '\0' || puzzle[xPos+i][yPos] == word.charAt(i)) {
                        validSpaces++;
                    }
                }

                if (validSpaces != word.length()) {
                    System.out.printf("Up/Down: validSpaces (len: %d) != word.length() (len: %d) for word %s\n", validSpaces, word.length(), word);
                    return false;
                }

                for (int i = 0; i < word.length(); i++) { //No need to validate spaces again as done above
                    puzzle[xPos+i][yPos] = word.charAt(i);
                }
            return true;

            case "Diagonal Left Down":
            case "Diagonal Left Up":
            case "Diagonal Right Down":
            case "Diagonal Right Up":
                System.out.println(direction + " Not Implemented! Return false for now!");
            return false;

            default:
                System.out.println("Hit default in switch! Direction: " + direction);
            return false;
        }
    }

    private void addWordToPuzzleBasic(String word, String direction) { //Left, Right, Up, Down
        boolean added = false;
        int xMin = 0, xMax = 0, yMin = 0, yMax = 0;
        String wordToAdd = new String(word);

        switch (direction) {
            case "Right":
                wordToAdd = new StringBuilder(word).reverse().toString(); //reverse word, then fallthrough to "Left"
            case "Left":
                xMin = 0; xMax = puzzle.length-wordToAdd.length();
                yMin = 0; yMax = puzzle[0].length;
            break;

            case "Up":
                wordToAdd = new StringBuilder(word).reverse().toString(); // reverse word, then fallthrough to "Down"
            case "Down":
                xMin = 0; xMax = puzzle.length;
                yMin = 0; yMax = puzzle[0].length-wordToAdd.length();
            break;
        
            default: 
                return;
        }

        WordExtraInfo info = new WordExtraInfo();
        for (int attempts = 0; !added && attempts < 100; attempts++) { //Try 100 times to add word
            int x = xMin + (int)(Math.random() * (xMax - xMin));
            int y = yMin + (int)(Math.random() * (yMax - yMin));
            if(addWord(wordToAdd, direction, x, y, info)) {
                added = true;
                puzzleWordsInfo.add(info);
            }
        }
    }

    private void addWordToPuzzleDiagonal(String direction) { //"Diagonal Left Down", "Diagonal Left Up", "Diagonal Right Down", "Diagonal Right Up"

    }

    private void generateWordSearchPuzzle() {
        ArrayList<String> words = new ArrayList<String>(puzzleWords); //make a copy
        final String[] directions = {"Left", "Right", "Up", "Down", "Diagonal Left Down", "Diagonal Left Up", "Diagonal Right Down", "Diagonal Right Up"};

        for (String word : words) {
            int randDir = (int)(Math.random() * directions.length);
            String direction = directions[randDir];

            switch (direction) {
                case "Left":
                case "Right":
                case "Up":
                case "Down":
                    addWordToPuzzleBasic(word, direction);
                break;

                case "Diagonal Left Down":
                case "Diagonal Left Up":
                case "Diagonal Right Down":
                case "Diagonal Right Up":
                    System.out.println(direction + " Not Implemented! Adding as Left");
                    addWordToPuzzleBasic(word, "Left");
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

        charTotal *= 1.75f; //multiply by scaling factor
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
        //return Arrays.deepToString(puzzle);
        StringBuilder str = new StringBuilder(puzzle.length*puzzle[0].length);
        
        //x is row; y is column
        for (int x = 0; x < puzzle.length; x++) {
            for (int y = 0; y < puzzle[x].length; y++) {
                str.append(puzzle[x][y]);
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
            System.out.print(puzzleWords.get(i));
            if (!hide) { //if false, we print the location and direction info too
                Point pos = puzzleWordsInfo.get(i).location;
                System.out.printf("\tLocation: X=[%d] Y=[%d]\t\tDirection: [%s]", pos.x, pos.y, puzzleWordsInfo.get(i).direction);
            }
            System.out.print('\n');
        }
    }


}
