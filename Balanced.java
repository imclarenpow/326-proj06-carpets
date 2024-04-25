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
     

import java.util.*;
public class Balanced {
    private int length;
    private HashMap<String, Integer> stock;
    private int carpetLength;
    private String carpetKey;
    private int carpetValue;
    private int remainingLength;
    private int currentMatchDiff;
    private StringBuilder builder;
    private String finalCarpet; //initialised the String for the proposed carpet
    private int optimalDiff = (length % 2 != 0) ? 0 : 1;//Integer for Optimal differences for a carpet length, 0 for even, 1 for odd

    /** Constructor for Balanced */
    public Balanced(HashMap<String, Integer> stock, int length, int carpetLength){
        this.stock = stock;
        this.length = length;
        this.carpetLength = carpetLength;
    }
    //java MakeCarpets.java < test.txt -b 3
    public String task(){
        HashMap<String, Integer> carpetsNotInCurrent = new HashMap<>();//Hashmap to keep track of carpets not in current carpet
        LinkedList<String> currentCarpet = new LinkedList<>(); //current carpet
        finalCarpet = "";
        currentMatchDiff = 0;//int to keep track of the matching current match differences
        //Organise the stock so the matching peices are together
        carpetsNotInCurrent = stockOrganiser(carpetsNotInCurrent);
        //Create a start carpet
        currentCarpet = startCarpet(carpetsNotInCurrent);
        //Checking if the optimal balance has already been reached and if so output the carpet
        currentMatchDiff = matchDifferences(currentCarpet);
        if (currentMatchDiff== optimalDiff) {
            return carpetBuilder(currentCarpet);
        }        

        finalCarpet = carpetPieceSwapper(carpetsNotInCurrent, currentCarpet, currentMatchDiff);
       
        return finalCarpet ;

    }

    /**
     * Method to organise the matching pieces together to limit how many pieces need to get checked when the stock is iterated through
     * @param stockToOrganise the stock passed in from the stdin file
     * @return carpetsNotInCurrent a hashmap represetning each carpet piece and how many are available
     */
    private HashMap<String, Integer> stockOrganiser(HashMap<String, Integer> stockToOrganise){
        // look at renaming some of these
        HashMap<String, Integer> organisedCarpets = new HashMap<>();//Hashmap to keep track of carpets not in current carpet
        int matchesComparison;
        boolean match;
        String carpetEntryKey;
        int carpetEntryValue;
        for (Map.Entry<String, Integer> stockItem : stock.entrySet()) { //cycle through every item in stock
            carpetValue = stockItem.getValue();
            carpetKey = stockItem.getKey();
            match = false;
            for(Map.Entry<String, Integer> carpetEntry : organisedCarpets.entrySet()){ //cycle through everything stored in carpetsNotInCurrent
                carpetEntryKey = carpetEntry.getKey();
                carpetEntryValue = carpetEntry.getValue();
                matchesComparison = countMatches(carpetKey, carpetEntryKey, true);//checks if it matches either forwards or reversed
                if(Math.abs(matchesComparison) == (carpetLength + 1)){//if the countmatches carpet length it must be a match either forward or reverse
                    organisedCarpets.put(carpetEntryKey, carpetEntryValue + carpetValue);//update the key with the correct value
                        match = true;
                    }
            }
            if (!match) {//if it doesn't match any others reversed then add to carpetsNotInCurrent
                    organisedCarpets.put(carpetKey, carpetValue);
                }
        }

        return organisedCarpets;
    }

    /**
     * method to create a start carpet
     * @param organisedCarpets
     * @return startingCarpet intitial carpet to work with
     */

     //might be worth trying to create optimal carpet here???
    private LinkedList<String> startCarpet(HashMap<String, Integer> organisedCarpets){
        LinkedList<String> startingCarpet = new LinkedList<>(); //starting point carpet
        
        Iterator<Map.Entry<String, Integer>> iterator = organisedCarpets.entrySet().iterator(); //iterator to move through the organised stock
        remainingLength = length;//Length of carpet requested by customer
        while(iterator.hasNext() && remainingLength>0){
            Map.Entry<String, Integer> entry = iterator.next();
            carpetKey = entry.getKey();
            carpetValue = entry.getValue();

            startingCarpet.add(carpetKey); //adds piece to current carpet
            remainingLength--;
            carpetValue--;
            
            if (carpetValue == 0) {
                iterator.remove(); // Remove the carpet from carpetsNotInCurrent
            } 
            else {
                entry.setValue(carpetValue); //otherwise update the value in carpetsNotInCurrent
            }
        }
        return startingCarpet;
    }
    /**
     * Method to cycle through the carpet and swap pieces when it ccreates a lower difference
     * 
     * @param carpetsNotInUse
     * @param carpetCurrent
     * @param matches
     * @return final carpet with the lowest diference of matches and non-matches and the difference
     */
    private String carpetPieceSwapper (HashMap<String, Integer> carpetsNotCurrentlyInUse, LinkedList <String> currentCarpet, int matchDiff){
        currentMatchDiff = matchDiff;
        String currentCarpetPiece;
        String carpetPieceToSwap;
        String carpetPieceToSwapReversed;
        int newMatchDiff;
        int reverseMatchDiff;
        Iterator<Map.Entry<String, Integer>> iterator ;
        Map.Entry<String, Integer> entry;
        int entryValue;
        //create carpet swapper and parse it current carpet and carpetsNotInCurrent
        for (int i = 0; i < currentCarpet.size(); i++) {
            currentCarpetPiece = currentCarpet.get(i);
            iterator = carpetsNotCurrentlyInUse.entrySet().iterator();
            while (iterator.hasNext()) {
                // if you reach optimal you need to break the while loop!!
                entry = iterator.next(); // gets the next entry in carpetsNotCurrentlyInUSe
                carpetPieceToSwap = entry.getKey();//retreive piece to swap
                //do up to if smaller and then have an outsource for the if smaller otherwise just loop back in
                currentCarpet.set(i, carpetPieceToSwap);//swap piece into current carpet
                newMatchDiff = matchDifferences(currentCarpet); //calculate the match and non match differences
                //reverse carpet piece being swaped in
                carpetPieceToSwapReversed = stringReverser(carpetPieceToSwap);
                currentCarpet.set(i, carpetPieceToSwapReversed);//swap piece into current carpet
                reverseMatchDiff = matchDifferences(currentCarpet);//calculate the match and non match differences

                //checking if the difference is smaller than what already existed
                if(newMatchDiff<currentMatchDiff || reverseMatchDiff<currentMatchDiff){
                    entryValue = entry.getValue();
                    if (reverseMatchDiff<newMatchDiff) {
                        currentMatchDiff = reverseMatchDiff;
                        if (currentMatchDiff == optimalDiff) {
                            return carpetBuilder(currentCarpet);
                        }
                        else{
                            currentCarpet.set(i, carpetPieceToSwapReversed);
                            
                            if (carpetsNotCurrentlyInUse.get(currentCarpetPiece) != null) { //add element being swapped out back into carpetsNotIncurrent
                                carpetsNotCurrentlyInUse.put(currentCarpetPiece, carpetsNotCurrentlyInUse.get(currentCarpetPiece)+1);
                            }
                            else{
                                carpetsNotCurrentlyInUse.put(carpetPieceToSwapReversed, 1);
                            }
                        }
                    }
                    else{// if newMatchdiffernece is smaller
                        currentMatchDiff = newMatchDiff;
                        if (currentMatchDiff ==optimalDiff) {
                            currentCarpet.set(i, carpetPieceToSwap);
                            return carpetBuilder(currentCarpet);
                        }
                        else{
                            currentCarpet.set(i, carpetPieceToSwap);
                            
                            if (carpetsNotCurrentlyInUse.get(currentCarpetPiece) != null) { //add element being swapped out back into carpetsNotIncurrent
                                carpetsNotCurrentlyInUse.put(currentCarpetPiece, carpetsNotCurrentlyInUse.get(currentCarpetPiece)+1);
                            }
                            else{
                                carpetsNotCurrentlyInUse.put(carpetPieceToSwap, 1);
                            }
                        }
                    }
                    if (entryValue == 1) {
                        iterator.remove();// Remove the carpet from carpetsNotCurrentlyInUse
                    }
                    else{
                        entry.setValue(entryValue-1);
                    }
                    
                }
                else{//if neither change is optimal make sure carpet isn't changed
                    currentCarpet.set(i, currentCarpetPiece);
                }
            }
        }

        return carpetBuilder(currentCarpet);
    }

    private String stringReverser (String toReverse){
        builder = new StringBuilder(toReverse);
        String reversedString = builder.reverse().toString();
        return reversedString;
    }

    /**
     * Method to assist the balance method and return the amount of differences in the carpet
     *
     * @param currentCarpet
     * @return the difference of matches and non matches of the current carpet
     */
    private int matchDifferences(LinkedList<String> currentCarpet){
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
    private int countMatches(String carpet, String carpet2, boolean max) {
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
     * Method to turn the carpet into a string
     * @param carpet
     * @return String representing the carpet and the match difference
     */
    private String carpetBuilder (LinkedList <String> carpet){
        builder = new StringBuilder();
        for (String string : carpet) {
            builder.append(string + "\n");
        }
        return builder.toString() + currentMatchDiff;
    }
}


