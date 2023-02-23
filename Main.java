import java.util.Scanner;

/**
 * This class runs the whole thing. it first takes in input from the command 
 * line and then processes the user's request based on the csv file's input
 */
public class Main {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        int points = scnr.nextInt();
        String fileName = scnr.next();
        SpendPoints sp = new SpendPoints(points);
        sp.init(fileName);
        sp.spendPoints();
        sp.correctNegComps();
        System.out.println(sp.toString());
        scnr.close();
    }
 }
