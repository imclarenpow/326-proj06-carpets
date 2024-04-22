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
    private int carpetLength = 0;

    /** Constructor for Balanced */
    public Balanced(HashMap<String, Integer> stock, int length, int carpetLength){
        this.stock = stock;
        this.length = length;
        this.carpetLength = carpetLength;
    }
    //java MakeCarpets.java < i1.txt -b 3
    public String task(){
        String proposedSolution = ""; //initialised the String for the proposed carpet
        HashMap<String, Integer> carpetsNotInCurrent = new HashMap<>();//Hashmap to keep track of carpets not in current carpet
        LinkedList<String> currentCarpet = new LinkedList<>(); //current carpet
        int remainingLength = length;//Length of carpet requested by customer
        int currentMatchDiff;//int to keep track of the matching current match differences
        int value; //int to store value of hashmap items in various for loops
        String carpet; //String to store carpet of hashmap items in various for loops
        int optimalDiff = (length % 2 != 0) ? 0 : 1;//Integer for Optimal differences for a carpet length, 0 for even, 1 for odd

        //organising the matching pieces together to limit how many pieces need to get checked each time
        int matchesComparison;
        boolean match;
        String carpetEntryKey;
        int carpetEntryValue;
        for (Map.Entry<String, Integer> stockItem : stock.entrySet()) { //cycle through every item in stock
            value = stockItem.getValue();
            carpet = stockItem.getKey();
            match = false;
            for(Map.Entry<String, Integer> carpetEntry : carpetsNotInCurrent.entrySet()){ //cycle through everything stored in carpetsNotInCurrent
                carpetEntryKey = carpetEntry.getKey();
                carpetEntryValue = carpetEntry.getValue();
                matchesComparison = countMatches(carpet, carpetEntryKey, true);//checks if it matches either forwards or reversed
                if(Math.abs(matchesComparison) == (carpetLength + 1)){//if the countmatches carpet length it must be a match either forward or reverse
                        carpetsNotInCurrent.put(carpetEntryKey, carpetEntryValue + value);//update the key with the correct value
                        match = true;
                    }
            }
            if (!match) {//if it doesn't match any others reversed then add to carpetsNotInCurrent
                    carpetsNotInCurrent.put(carpet, value);
                }
        }

        //Create a start carpet
        Iterator<Map.Entry<String, Integer>> iterator = carpetsNotInCurrent.entrySet().iterator();
        while(iterator.hasNext() && remainingLength>0){
            Map.Entry<String, Integer> entry = iterator.next();
            carpet = entry.getKey();
            value = entry.getValue();

            currentCarpet.add(carpet); //adds piece to current carpet
            remainingLength--;
            value--;
            
            if (value == 0) {
                iterator.remove(); // Remove the carpet from carpetsNotInCurrent
            } 
            else {
                entry.setValue(value); //otherwise update the value in carpetsNotInCurrent
            }
        }

        //Checking if the optimal balance has already been reached and if so output the carpet
        currentMatchDiff = matchDifferences(currentCarpet);
        if (currentMatchDiff== optimalDiff) {
            for (String string : currentCarpet) {
                proposedSolution += string + "\n";
            }
            return proposedSolution;
        }        
        String currentCarpetPiece;
        String swapCarpetpiece;
        String reversedSwapCarpetpiece;
        int newMatchDiff;
        int reverseMatchDiff;
        StringBuilder reversedBuilder;
        for (int i = 0; i < currentCarpet.size(); i++) {
            currentCarpetPiece = currentCarpet.get(i);
            //System.out.println(currentCarpetPiece);
            for(Map.Entry<String, Integer> entry : carpetsNotInCurrent.entrySet()){                
                swapCarpetpiece = entry.getKey();//retreive piece to swap
                currentCarpet.set(i, swapCarpetpiece);//swap piece into current carpet
                newMatchDiff = matchDifferences(currentCarpet); //calculate the match and non match differences
               
                //reverse carpet piece being swaped in
                reversedBuilder = new StringBuilder(swapCarpetpiece);
                reversedSwapCarpetpiece = reversedBuilder.reverse().toString();
                //swap piece into current carpet
                currentCarpet.set(i, reversedSwapCarpetpiece);
                //calculate the match and non match differences
                reverseMatchDiff = matchDifferences(currentCarpet);
                
                //checking if the difference is smaller than what already existed
                if(newMatchDiff<currentMatchDiff || reverseMatchDiff<currentMatchDiff){
                    if (reverseMatchDiff<newMatchDiff) {//if the reversed piece difference is smaller then do the following
                        carpetsNotInCurrent.put(swapCarpetpiece, entry.getValue()-1); //remove element to be swapped from the carpetsNotIncurrent
                        if (carpetsNotInCurrent.get(currentCarpetPiece) != null) { //add element being swapped out back into carpetsNotIncurrent
                            carpetsNotInCurrent.put(currentCarpetPiece, carpetsNotInCurrent.get(currentCarpetPiece)+1);
                        }
                        else{
                            carpetsNotInCurrent.put(currentCarpetPiece, 1);
                        }
                         
                        //not working beacuse for a piece that is individual it won't have a value
                        
                        if(reverseMatchDiff==optimalDiff){//if optimal output string
                            for (String string : currentCarpet) {
                                proposedSolution += string + "\n";
                            }
                            return proposedSolution + reverseMatchDiff;
                        }
                        else {                 
                            currentMatchDiff = reverseMatchDiff;//update current diff
                        }                        
                    } else{//if NewMatchDiff is smaller then do the following
                        carpetsNotInCurrent.put(swapCarpetpiece, entry.getValue()-1); //remove element to be swapped from the carpetsNotIncurrent
                        if (carpetsNotInCurrent.get(currentCarpetPiece) != null) { //add element being swapped out back into carpetsNotIncurrent
                            carpetsNotInCurrent.put(currentCarpetPiece, carpetsNotInCurrent.get(currentCarpetPiece)+1);
                        }
                        else{
                            carpetsNotInCurrent.put(currentCarpetPiece, 1);
                        } 
                        if(newMatchDiff==optimalDiff){
                            currentCarpet.set(i, swapCarpetpiece);//set the carpet pieces back again
                            for (String string : currentCarpet) {
                                proposedSolution += string + "\n";
                            }
                            return proposedSolution + newMatchDiff;
                        }
                        else{
                            currentCarpet.set(i, swapCarpetpiece);                            
                            currentMatchDiff = newMatchDiff;                            
                        }
                    }
                } else{ //if neither change is optimal make sure carpet isn't changed
                    currentCarpet.set(i, currentCarpetPiece);
                }
            }
            
        }

        for (String string : currentCarpet) {
            proposedSolution += string + "\n";
        }
        return proposedSolution + currentMatchDiff;
    }

    /**
     * Method to assist the balance method
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
}


