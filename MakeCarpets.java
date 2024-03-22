/*
 * Author: Cayden Scott, Isaac LASTNAME, Rochell LASTNAME, Tristan Kitto
 * Description: Program which reads from stdin, parsing each line corresponding to 
 * a strip of carpet. It will then, from this stock and given parameters output a piece
 * of carpet that can be made.
 */

import java.util.HashMap;
import java.util.Scanner;

public class MakeCarpets {

    private static int length = -1;
    private static char mode = 'n';

    public static void main(String[] args) {
        HashMap<String, Integer> stock = new HashMap<>();
        Scanner in = new Scanner(System.in);
        int lineCount = 0; // Counter to be used in place of multiplier argument given nothing written
        while (in.hasNextLine()) {
            String line = in.nextLine();
            lineCount++;
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
                output = maxMaches(stock);
                break;
            case 'b':
                output = balanced(stock);
                break;
        }
        System.out.print(output);
    }

    /*
     * Creates a carpet from the stock which will have no matching pieces of carpet
     * touching vertically
     * 
     * @param stock - HashMap containing the current stock from which the carpet
     * will be made
     * 
     * @return carpet created in a String 'output'
     */
    private static String noMatches(HashMap<String, Integer> stock) {
        return "";
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
    private static String maxMaches(HashMap<String, Integer> stock) {
        return "";
    }

    /*
     * Creates a carpet from the stock which is as balanced as possible between
     * having matches
     * and having non matches.
     * 
     * @param stock - HashMap containing the current stock from which the carpet
     * will be made
     * 
     * @return carpet created in a String 'output', followed by the absolute value
     * of the difference between the number of matches
     * and non-matches at the end of a string
     */
    private static String balanced(HashMap<String, Integer> stock) {
        return "";
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
        if (length == -1) {
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
}