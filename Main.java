import java.util.Scanner;

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
