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
        int matches = 0;
        
        // use highest stock as first carpet placed
        HashMap<String, Integer> workingStock = new HashMap<>(stock);
        HashMap.Entry<String, Integer> carpet = Collections.max(stock.entrySet(), Map.Entry.comparingByValue());
        String prevCarpet = carpet.getKey();
        output = prevCarpet + "\n";
        workingStock.put(carpet.getKey(), carpet.getValue()-1);
        // for loop that runs for as long as the length specified
        for (int i = 0; i < length - 1; i++) {
            String nextCarpet = noMatchCarpet(workingStock, prevCarpet);
            // if no match carpet returns not possible might as well stop the method
            if(nextCarpet == "Not Possible"){
                return nextCarpet;
            }
            output += nextCarpet + "\n";
            prevCarpet = nextCarpet;
            int amt = 0;
            boolean reversed = false;
            // checks if there is actually a key of the carpet
            try{
                amt = workingStock.get(nextCarpet);
            } catch (Exception e){
                // if not:reverse
                reversed = true;
            }
            // check both ways if still not possible its not possible
            if(amt<=0){
                amt = workingStock.get(new StringBuilder(nextCarpet).reverse().toString());
                reversed = true;
                if(amt<=0){
                    return "Not Possible";
                }
            }
            // if reversed re reverse string and use as key to remove 1 from. else as normal.
            if(reversed){
                workingStock.put(new StringBuilder(nextCarpet).reverse().toString(), amt -1);
                prevCarpets.put(output + (length - 1 - i), output + matches);
            } else {
                workingStock.put(nextCarpet, amt - 1);
                prevCarpets.put(output + (length - 1 - i), output + matches);
            }
        }
        return output;
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

    /**
     * Creates a carpet from the stock which is as balanced as possible between
     * having matches
     * and having non matches.
     * 
     * @param stock - HashMap containing the current stock from which the carpet
     * will be made
     * 
     * @return carpet created in a String 'output', followed by the absolute value
     * of the difference between the number of matches
     * and non-matches at the end of a string*/
     

     //java MakeCarpets.java < i0.txt -b 3
    private static String balanced(HashMap<String, Integer> stock) { //parses in stock that has the pieces and how many there are of each piece
        Balanced b = new Balanced(stock, length, carpetLength);
        return b.task();
    }

    /**
     * Method to assist the balance method
     *
     * @param currentCarpet
     * @return the difference of matches and non matches of the current carpet
     */
    private static int matchDifferences(LinkedList<String> currentCarpet){
        int currentCarpetLength = currentCarpet.size();
        int matchCount = 0;
        int nonmatchCount = 0;
        int currentCarpetsMatches = 0;
        for (int i = 1; i < currentCarpetLength; i++) {
            currentCarpetsMatches = countMatches(currentCarpet.get(i-1),currentCarpet.get(i) , false);     
            matchCount = matchCount + currentCarpetsMatches;
            nonmatchCount = nonmatchCount +( (carpetLength + 1) - currentCarpetsMatches);
            
        }
        currentCarpetsMatches = matchCount - nonmatchCount;//calculates the difference between matches and non matches
        return Math.abs(currentCarpetsMatches) ;// returns the difference betrweent the count of matches and non matches
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
     * Checks that these puppies don't match
     * @param 
     */
    public static String noMatchCarpet(HashMap<String, Integer> stock, String prevCarp){
        String[] previous = prevCarp.split("(?!^)");
        for(HashMap.Entry<String, Integer> entry : stock.entrySet()) {
            if(entry.getValue() <= 0){
                continue;
            }
            String checkingCarpet = entry.getKey();
            String[] current = checkingCarpet.split("(?!^)");
            boolean reverse = false;
            for(int i=0; i<previous.length; i++){
                if(!reverse){
                    if(previous[i].equals(current[i])){
                        reverse = true;
                        i = 0;
                    } else if(i==previous.length-1){
                        return checkingCarpet;
                    }
                }else if(reverse){
                    if(previous[i].equals(current[previous.length-1-i])){
                        break;
                    } else if(i==previous.length-1){
                        String reversedCarpet = new StringBuilder(checkingCarpet).reverse().toString();
                        return reversedCarpet;
                    }
                }
            }
        }
        return "Not Possible";
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

}