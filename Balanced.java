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
    private int[] balance = {0,0};

    /** Constructor for Balanced */
    public Balanced(HashMap<String, Integer> stock, int length, int carpetLength){
        this.stock = stock;
        this.length = length;
        this.carpetLength = carpetLength;
    }
    // java MakeCarpets.java < i1.txt -b 3
    public String task(){
        String currentCarpet = largestStock();
        String outputCarpet = "";
        takeStock(currentCarpet);
        for(int i=0; i<length-1; i++){
            outputCarpet += currentCarpet + "\n";
            currentCarpet = findBestNext(currentCarpet);
        }
        outputCarpet += currentCarpet + "\n";
        int b = 0;
        if(balance[0] > balance[1]){
            b = balance[0] - balance[1];
        }else if(balance[0] < balance[1]){
            b = balance[1] - balance[0];
        }
        outputCarpet += b + "\n";
        return outputCarpet;
    }
    public String findBestNext(String prevCarpet){
        String fwd = forwardCheck(prevCarpet);
        String rev = reverseCheck(prevCarpet);
        int[] fwdBalance = {0,0};
        int[] revBalance = {0,0};
        // iterate through reverse and forward carpets and calculate the balance
        for(int i=0; i<prevCarpet.length(); i++){
            if(prevCarpet.charAt(i) == fwd.charAt(i)){
                fwdBalance[0]++;
            }else{
                fwdBalance[1]++;
            }
            if(prevCarpet.charAt(i) == rev.charAt(prevCarpet.length()-1-i)){
                revBalance[0]++;
            }else{
                revBalance[1]++;
            }
        }
        // if the forward balance is better than the reverse balance then return the forward carpet
        if(Math.abs(balance[0]+fwdBalance[0]) > Math.abs(balance[0]+revBalance[0])
            && Math.abs(balance[1]+fwdBalance[1]) > Math.abs(balance[1]+revBalance[1])){
            takeStock(fwd);
            balance[0] += fwdBalance[0];
            balance[1] += fwdBalance[1];
            return fwd;
        }else{
            takeStock(rev);
            StringBuilder sb = new StringBuilder(rev);
            balance[0] += revBalance[0];
            balance[1] += revBalance[1];
            return sb.reverse().toString();
        }
    }
    // takes in a piece of carpet and returns the best carpet to follow it for balance
    public String forwardCheck(String prevCarp){
        int[] bestBalance = {0,0};
        boolean first = true;
        String bestCarpet = "";
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            int[] workingBalance = {0,0};
            String key = entry.getKey();
            int value = entry.getValue();
            // in case empty (shouldn't be)
            if(value<1){
                continue;
            }
            for(int i=0; i<prevCarp.length(); i++){
                if(prevCarp.charAt(i) == key.charAt(i)){
                    workingBalance[0]++;
                }else{
                    workingBalance[1]++;
                }
            }
            // if first iteration then set bestBalance to the first workingBalance
            if(first){
                bestBalance = workingBalance;
                first = false;
                bestCarpet = key;
            }
            else{
                // if the new balance is better than the previous best balance then update bestCarpet
                if(Math.abs(balance[0]+bestBalance[0]) > Math.abs(balance[0]+workingBalance[0])
                    && Math.abs(balance[1]+bestBalance[1]) > Math.abs(balance[1]+workingBalance[1])){
                    bestBalance = workingBalance;
                    bestCarpet = key;
                }
            }
        }
        return bestCarpet;
    }
    // reverse takes in a piece of carpet and returns the best carpet to follow it for balance
    public String reverseCheck(String prevCarp){
        int[] bestBalance = {0,0};
        boolean first = true;
        String bestCarpet = "";
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            int[] workingBalance = {0,0};
            String key = entry.getKey();
            int value = entry.getValue();
            // in case empty (shouldn't be)
            if(value<1){
                continue;
            }
            for(int i=0; i<prevCarp.length(); i++){
                if(prevCarp.charAt(i) == key.charAt(prevCarp.length()-1-i)){
                    workingBalance[0]++;
                }else{
                    workingBalance[1]++;
                }
            }
            // if first iteration then set bestBalance to the first workingBalance
            if(first){
                bestBalance = workingBalance;
                first = false;
                bestCarpet = key;
            }
            else{
                // if the new balance is better than the previous best balance then update bestCarpet
                if(Math.abs(balance[0]+bestBalance[0]) > Math.abs(balance[0]+workingBalance[0])
                    && Math.abs(balance[1]+bestBalance[1]) > Math.abs(balance[1]+workingBalance[1])){
                    bestBalance = workingBalance;
                    bestCarpet = key;
                }
            }
        }
        return bestCarpet;
    }
    // takes in a piece of carpet and removes one from the stock.
    // ALWAYS RUN WHEN TAKING A CARPET FROM THE STOCK
    public void takeStock(String carpet){
        stock.put(carpet, stock.get(carpet)-1);
        if(stock.get(carpet) == 0){
            stock.remove(carpet);
        }     
    }
    // working!
    public String largestStock(){
        String largestKey = null;
        int largestValue = 0;
        
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            
            if (value > largestValue) {
                largestKey = key;
                largestValue = value;
            }
        }
        return largestKey;
    }
}
