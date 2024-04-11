import java.util.*;

/*
 * Author: Cayden Scott, Isaac Powell, Rochell Cole, Tristan Kitto
 * Description: Program which reads from stdin, parsing each line corresponding to 
 * a strip of carpet. It will then, from this stock and given parameters, output a piece
 * of carpet that can be made.
 */
public class MakeCarpets {

    // int to store the length of the carpet to be made
    private static int length = -1;
    // char to store the mode of the carpet to be made
    private static char mode = 'n';
    // int to store how many squares of carpet are in each row
    private static int carpetLength = 0;
    // HashMap to store previously computed carpets
    private static HashMap<String, String> prevCarpets = new HashMap<>();

    public static void main(String[] args) {
        HashMap<String, Integer> stock = new HashMap<>();
        Scanner in = new Scanner(System.in);
        int lineCount = 0; // Counter to be used in place of multiplier argument given nothing written
        while (in.hasNextLine()) {
            String line = in.nextLine();
            lineCount++;
            carpetLength = line.length() - 1;
            if (!stock.containsKey(line)) {
                stock.put(line, 1);
            } else {
                stock.replace(line, stock.get(line) + 1);
            }
        }
        balanced(stock);
        checkArgs(args, lineCount); // Organise arguments, allowing for any order or entry
        process(stock); //
        in.close();
    }

    /*
     * Decides which type of carpet to make, based on the argument
     * 
     * @param stock - HashMap containing the current stock from which the carpet
     * will be made
     */
    private static void process(HashMap<String, Integer> stock) {
        String output = "";
        switch (mode) {
            case 'n':
                output = noMatches(stock);
                break;
            case 'm':
                output = maxMatches(stock);
                break;
            case 'b':
                output = balanced(stock);
                break;
        }
        System.out.print(output);
    }

    /**
     * Creates a carpet from the stock which will have no matching pieces of carpet touching vertically
     * 
     * @param stock - HashMap containing the current stock from which the carpet will be made
     * @return carpet created in a String 'output', or "not possible" if there are matches
     */
    private static String noMatches(HashMap<String, Integer> stock) {
        String output = "";
        String prevCarpet = "";
        int remainingLength = length;
        while (remainingLength > 0) {
            String noMatchingCarpet = "";
            boolean foundCarpet = false;
            for (HashMap.Entry<String, Integer> entry : stock.entrySet()) {
                if (!entry.getKey().equals(prevCarpet)) {
                    String currentCarpet = entry.getKey();
                    if (!noMatchAux(prevCarpet, currentCarpet)) { // check there are no matches
                        noMatchingCarpet = currentCarpet;
                        foundCarpet = true;
                        break;
                    }
                }
            }
            if (!foundCarpet) {
                return "not possible\n"; // can't add the next carpet to it because it matches :(
            }
            output += noMatchingCarpet + "\n";
            stock.replace(noMatchingCarpet, stock.get(noMatchingCarpet) - 1);
            if (stock.get(noMatchingCarpet) == 0) {
                stock.remove(noMatchingCarpet);
            }
            prevCarpet = noMatchingCarpet;
            remainingLength--;
        }
        return output;
    }
    /**
     * Auxiliary Class for noMatches
     * @return a boolean whether or not it matches
     */
    private static boolean noMatchAux(String previous, String current) {
        if (previous.length() < carpetLength) {
            return false;
        }
        // Compare the last column of the previous carpet with the first column of the current carpet
        for (int i = 0; i < carpetLength; i++) {
            if (previous.charAt(previous.length() - carpetLength + i) == current.charAt(i)) {
                return true;
            }
        }
        return false;
    }


    /*
     * Creates a carpet from the stock which has as many matches as possible
     * 
     * @param stock - HashMap containing the current stock from which the carpet
     * will be made
     * 
     * @return carpet created in a String 'output', followed by the number of
     * matches on the next line at the end of a string
     */
    private static String maxMatches(HashMap<String, Integer> stock) {
        // find the best carpet using the carpet with the highest stock as the starting
        // point
        String output = "";
        int matches = 0;

        // find the carpet with the highest stock
        HashMap<String, Integer> stockCopy = new HashMap<>(stock);
        HashMap.Entry<String, Integer> carpet = Collections.max(stock.entrySet(), Map.Entry.comparingByValue());

        // add the highest stock carpet to the output
        output = carpet.getKey() + "\n";
        stockCopy.put(carpet.getKey(), carpet.getValue() - 1);

        // add the next carpets to the output
        for (int i = 0; i < length - 1; i++) {
            String nextCarpet = findMaxCarpet(stockCopy, output);
            int numMatches = countMatches(output, nextCarpet, true);
            matches += Math.abs(numMatches);
            if (numMatches < 0) {
                String reversedNextCarpet = new StringBuilder(nextCarpet).reverse().toString();
                output += reversedNextCarpet + "\n";
            } else {
                output += nextCarpet + "\n";
            }
            stockCopy.put(nextCarpet, stockCopy.get(nextCarpet) - 1);
            prevCarpets.put(output + (length - 1 - i), output + matches);
        }

        // if the carpet is already full, return the carpet
        if (matches == length * carpetLength) {
            return output + matches;
        }

        // find the carpet with the most matches
        output = dfs("", stock, 0, length);

        return output;
    }

    /**
     * Depth first search to find the carpet with the most matches
     * 
     * @param carpet          - the current carpet being built
     * @param stock           - the stock of carpet to be used
     * @param matches         - the number of matches in the current carpet
     * @param remainingLength - the number of rows left to be built
     * @return the carpet with the most matches
     */
    private static String dfs(String carpet, HashMap<String, Integer> stock, int matches, int remainingLength) {

        // base case: no more rows to build, return the max carpet + number of matches
        if (remainingLength == 0) {
            return carpet + matches;
        }

        // if the previous carpets has current carpet and remaining length, return that
        // carpet to avoid recomputation
        if (prevCarpets.containsKey(carpet + remainingLength)) {
            return prevCarpets.get(carpet + remainingLength);
        }

        String maxCarpet = carpet;
        int maxMatches = matches;

        // iterate through the stock to find the carpet with the most matches
        for (String nextCarpet : stock.keySet()) {

            // if the carpet is out of stock, skip
            if (stock.get(nextCarpet) > 0) {

                // find the number of matches between the current carpet and the next carpet
                int numMatches = countMatches(carpet, nextCarpet, true);

                // calculate potential number of matches if all further carpets give max matches
                int potentialMatches = matches + Math.abs(numMatches) + remainingLength * carpetLength;

                if (potentialMatches >= maxMatches) {

                    stock.put(nextCarpet, stock.get(nextCarpet) - 1);

                    // if the next carpet needs to be reversed, reverse it
                    if (numMatches < 0) {
                        nextCarpet = new StringBuilder(nextCarpet).reverse().toString();
                    }

                    // recursively call the dfs with the next carpet
                    String result = dfs(carpet + nextCarpet + "\n", stock, matches + Math.abs(numMatches),
                            remainingLength - 1);

                    // update the max carpet and matches if the current carpet has more matches
                    String matchesStr = result.substring(result.lastIndexOf("\n") + 1);
                    int newMatches = matchesStr.isEmpty() ? 0 : Integer.parseInt(matchesStr);
                    if (newMatches > maxMatches) {
                        maxMatches = newMatches;
                        maxCarpet = result;
                    }

                    // put the carpet back in the stock
                    if (numMatches < 0) {
                        nextCarpet = new StringBuilder(nextCarpet).reverse().toString();
                    }
                    stock.put(nextCarpet, stock.get(nextCarpet) + 1);
                }
            }
        }

        // store the current carpet and remaining length for future reference
        prevCarpets.put(carpet + remainingLength, maxCarpet);
        return maxCarpet;
    }

    /*
     * Creates a carpet from the stock which is as balanced as possible between
     * having matches
     * and having non matches.
     * Proposed solution alternates between matching carpet pieces and nonmatching
     * 
     * @param stock - HashMap containing the current stock from which the carpet
     * will be made
     * 
     * @return carpet created in a String 'output', followed by the absolute value
     * of the difference between the number of matches
     * and non-matches at the end of a string
     */
    private static String balanced(HashMap<String, Integer> stock) {
        String proposedSolution = ""; //initialised the String for the proposed carpet
        String[] baseCarpet = maxMatches(stock).split("\n"); //gets the carpet with the most macthes
        int baseCarpetIndex = 0; //to keep track of place in the carpet
        String[] stockOfNonMatchingCarpet = noMatches(stock).split("\n"); //gets stock of carpet pieces that dont match
        int remainingLength = length; //Stores the length of the carpet to be made
        int checkerBit = 0; //To check whether to add a matching piece or a non macthing piece
        //if statment to see if baseCarpet length is greater than half of the required length
            while (remainingLength > 0 ) {

                if(checkerBit == 0){ //add a line from baseCarpet to proposedSolution
                    proposedSolution += baseCarpet[baseCarpetIndex] + "\n";
                    System.out.println(proposedSolution);
                    baseCarpetIndex++;                    
                    checkerBit =1; //Change the checker bit back to 1 to indiciate need for nonMatching piece
                }    
                else if (checkerBit == 1 ) {
                    int indexTracker = 0;
                    for (String carpetPiece : stockOfNonMatchingCarpet) {
                        if (baseCarpet[baseCarpetIndex] != null &&  baseCarpet[baseCarpetIndex].equals(carpetPiece)) {
                            proposedSolution += carpetPiece + "\n";
                            stockOfNonMatchingCarpet[indexTracker] = null;
                            break;                            
                        }
                        indexTracker++;
                    }
                    if(baseCarpetIndex < baseCarpet.length){
                        checkerBit = 0;
                    }
                }
    
                remainingLength--;
    
            }        

        int matchDifferences = Math.abs(baseCarpet.length - stockOfNonMatchingCarpet.length);
        return proposedSolution + matchDifferences;
    }

    /*
     * Method to allow arguments to be in any order, and also ensure correcty usage
     * 
     * @param args - the arguments which will be evaluated
     * 
     * @param size - total number of lines that were read in, to be used if no
     * multiplier argument exists
     */
    private static void checkArgs(String[] args, int size) {
        if (args.length > 2) {
            printUsage();
        }
        if (args.length == 2) {
            if (Character.isDigit(args[0].charAt(0))) {
                length = Integer.parseInt(args[0]);
                mode = args[1].charAt(1);
            } else if (Character.isDigit(args[1].charAt(0))) {
                length = Integer.parseInt(args[1]);
                mode = args[0].charAt(1);
            }
        } else if (args.length == 1) {
            if (Character.isDigit(args[0].charAt(0))) {
                length = Integer.parseInt(args[0]);
            } else {
                mode = args[0].charAt(1);
            }
        }
        if (mode != 'b' && mode != 'n' && mode != 'm') {
            mode = 'n';
        }
        if (length <= 0) {
            length = size;
        }
    }

    /*
     * Method to simply print out usage of file when too many arguments, and
     * terminate program
     */
    private static void printUsage() {
        System.err.println("\n\tUsage: java MakeCarpets <flag> <multiplier> < inputfile.txt\n");
        System.exit(1);
    }

    /**
     * Finds the carpet in the stock which has the most matches with the current
     * 
     * @param stock  - HashMap containing the current stock of carpet
     * @param output - the current carpet being built
     * @return the carpet with the most matches
     */
    private static String findMaxCarpet(HashMap<String, Integer> stock, String output) {
        String nextCarpet = "";
        int maxMatches = 0;
        for (HashMap.Entry<String, Integer> entry : stock.entrySet()) {
            if (entry.getValue() <= 0) {
                continue;
            }
            int matches = countMatches(output, entry.getKey(), true);
            if (Math.abs(matches) > Math.abs(maxMatches)) {
                maxMatches = matches;
                nextCarpet = entry.getKey();
            }
        }
        return nextCarpet;
    }

    /**
     * Counts the number of matches between two carpets
     * 
     * @param carpet1 - first carpet to be compared
     * @param carpet2 - second carpet to be compared
     * @param max     - boolean to determine if the output should be the maximum
     *                matches (including reversed carpet2)
     * @return number of matches between the two carpets, will be negative if the
     *         second carpet needs to be reversed to find a max
     */
    private static int countMatches(String carpet, String carpet2, boolean max) {
        // find the last carpetLength characters of the current carpet
        int beginIndex = Math.max(0, carpet.length() - (carpet2.length() + 1));
        String carpet1 = carpet.substring(beginIndex, carpet.length());
        int matches = 0;
        int matchesReversed = 0;
        for (int i = 0; i < carpet1.length(); i++) {
            if (carpet1.charAt(i) != '\n' && carpet1.charAt(i) == carpet2.charAt(i)) {
                matches++;
            }
        }

        if (max) {
            String reversedCarpet2 = new StringBuilder(carpet2).reverse().toString();
            for (int i = 0; i < carpet1.length(); i++) {
                if (carpet1.charAt(i) != '\n' && carpet1.charAt(i) == reversedCarpet2.charAt(i)) {
                    matchesReversed++;
                }
            }
        }

        return matches > matchesReversed ? matches : (-1) * matchesReversed;
    }

    /**
     * Counts the number of matches between two carpets
     * 
     * @param carpet1 - first carpet to be compared
     * @param carpet2 - second carpet to be compared
     * @return number of matches between the two carpets
     */
    private static int countMatches(String carpet, String carpet2) {
        // find the last carpetLength characters of the current carpet
        int beginIndex = Math.max(0, carpet.length() - (carpet2.length() + 1));
        String carpet1 = carpet.substring(beginIndex, carpet.length());
        int matches = 0;
        for (int i = 0; i < carpet1.length(); i++) {
            if (carpet1.charAt(i) != '\n' && carpet1.charAt(i) == carpet2.charAt(i)) {
                matches++;
            }
        }

        return matches;
    }
}