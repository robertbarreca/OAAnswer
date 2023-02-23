import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 * This class take in a given file and point value. If able to spend all the
 * points given the
 * transactions in the file it will spend them in order from oldest to newest
 * transaction,
 * all while making sure no company's point totals go negative
 */
public class User {
    private PriorityQueue<Transaction> transactions; // Min heap of transactions by completion time
    private Hashtable<String, Integer> comps; // list of companies mapped to total pts left
    private int points = 0; // points user has left to spend

    /**
     * Constructor for this SpendPoints Class
     * 
     * @param points the amount of points a user wants to spend
     * @throws IllegalArgumentException if points is negative
     */
    public User(int points) throws IllegalArgumentException {
        if (points < 0) {
            throw new IllegalArgumentException("Can't spend negative points");
        }
        this.points = points;
        this.transactions = new PriorityQueue<>();
        this.comps = new Hashtable<>();
    }

    /**
     * Creates User objects based on every line of the CSV file and adds them to a
     * min-heap
     * 
     * @param points Points user wants to spend
     * @throws IllegalArgumentException if the amount of points available to spend
     *                                  is
     *                                  less than the number of points requested to
     *                                  spned
     */
    public void init(String fileName) throws IllegalArgumentException {
        int sum = 0;
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(",");
                this.transactions.add(new Transaction(vals[0], Integer.parseInt(vals[1]), vals[2]));
                // add to sum to check for exception
                sum += Integer.parseInt(vals[1]);
                // add company to hash table if not already in
                if (!comps.containsKey(vals[0]))
                    comps.put(vals[0], Integer.parseInt(vals[1]));
                // otherwise update point total
                else {
                    comps.put(vals[0], comps.get(vals[0]) + Integer.parseInt(vals[1]));
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // If user requests more points than available we cannot process request
        if (this.points > sum) {
            throw new IllegalArgumentException("Impossible to spend all points");
        }
    }

    /**
     * Takes away points from company's and user's points balance; done from
     * oldest transaction to newest transaction. Also removes transactions when all
     * points were spent.
     */
    public void spendPoints() {
        while (!this.transactions.isEmpty() && this.points > 0) {
            Transaction curr = this.transactions.peek();
            // current transaction has negative points
            if (curr.getPoints() < 0) {
                // correct issue so we can move forward
                correctNegPoints();
            }
            // more points to spend than oldest transaction has
            else if (this.points >= curr.getPoints()) {
                // remove points from curr transaction from total company points
                this.comps.put(curr.getName(), this.comps.get(curr.getName()) - curr.getPoints());
                // remove points available to user and remove transaction
                this.points -= this.transactions.poll().getPoints();
            }
            // oldest transaction has enough points spend rest of points
            else {
                // remove points from transaction
                curr.setPoints(curr.getPoints() - points);
                // remove points from company
                this.comps.put(curr.getName(), this.comps.get(curr.getName()) - this.points);
                // set available user points to zero
                this.points = 0;
            }
        }
    }

    /**
     * Corrects negative point transactions by taking away from next oldest
     * non-negative point transactions and adding that to current transaction
     * until the negative transactions point balance is zero
     */
    public void correctNegPoints() {
        ArrayList<Transaction> negPts = new ArrayList<>();
        // list of negative point and same-company transactions so we can move forward
        // in heap
        negPts.add(this.transactions.poll());
        // new val of company points so we can correct it at end
        while (!this.transactions.isEmpty()) {
            Transaction curr = this.transactions.peek();
            // current transaction is negative or has same company as current transaction
            if (curr.getPoints() < 0) {
                // store transaction to the side so we can continue iterating
                negPts.add(this.transactions.poll());
            }
            // more points required to fix current transaction
            else if (negPts.get(0).getPoints() * -1 >= curr.getPoints()) {
                // add current transaction's point total to negative transaction's
                negPts.get(0).setPoints(negPts.get(0).getPoints() + curr.getPoints());
                // add points to negative transaction company's point total
                this.comps.put(negPts.get(0).getName(), this.comps.get(negPts.get(0).getName()) + curr.getPoints());
                // subtract points from current transaction company's point total
                this.comps.put(curr.getName(), this.comps.get(curr.getName()) - curr.getPoints());
                // remove current transaction
                this.transactions.poll();
            }
            // can fix current transaction
            else {
                // set negative transaction's company point total
                this.comps.put(negPts.get(0).getName(),
                        this.comps.get(negPts.get(0).getName()) - negPts.get(0).getPoints());
                // set current transaction's points
                curr.setPoints(curr.getPoints() + negPts.get(0).getPoints());
                // set current transation's company point total
                this.comps.put(curr.getName(), this.comps.get(curr.getName()) + negPts.get(0).getPoints());
                break;
            }
        }
        // Add negative transactions back to heap
        for (int i = 1; i < negPts.size(); i++) {
            this.transactions.add(negPts.get(i));
        }
    }

    /**
     * Iterate through hashmap of company's and if total points is negative it
     * corrects it
     * by going through transactions and adding the positive ones that aren't from
     * the same
     * company to the negative company's point total until it is zero.
     * 
     */
    public void correctNegComps() {
        for (HashMap.Entry<String, Integer> mapElement : this.comps.entrySet()) {
            ArrayList<Transaction> negPts = new ArrayList<>(); // list of negative transactions or transactions from
                                                               // same company
            if (mapElement.getValue() < 0) {
                // create new transaction for company
                Transaction transaction = new Transaction(mapElement.getKey(), 0,
                        this.transactions.peek().getDate() + "");
                while (!this.transactions.isEmpty() && mapElement.getValue() < 0) {
                    Transaction curr = this.transactions.peek();
                    // current transaction has same company as current transaction or it's negative,
                    if (curr.getName().equals(mapElement.getKey()) || curr.getPoints() < 0) {
                        // put it to the side and add it back to min heap after done fixing current
                        negPts.add(this.transactions.poll());
                    }
                    // more points needed to fix company
                    else if (mapElement.getValue() * -1 > curr.getPoints()) {
                        // subtract points from current transaction's company
                        this.comps.put(curr.getName(), this.comps.get(curr.getName()) - curr.getPoints());
                        // add points for new transaction from current transaction
                        transaction.setPoints(transaction.getPoints() + curr.getPoints());
                        // remove transaction and add pts val to company with neg points
                        mapElement.setValue(mapElement.getValue() + this.transactions.poll().getPoints());
                    }
                    // can set company's points back to zero
                    else {
                        // add money to new transaction for negative company
                        transaction.setPoints(transaction.getPoints() + -1 * mapElement.getValue());
                        // subtract points taken from other company
                        this.comps.put(curr.getName(), this.comps.get(curr.getName()) + mapElement.getValue());
                        // subtract points from transaction
                        curr.setPoints(curr.getPoints() + mapElement.getValue());
                        // set company's points to zero
                        mapElement.setValue(0);
                        // remove curr if point total is zero
                        if (curr.getPoints() == 0)
                            this.transactions.poll();

                    }
                }
                // add transaction into heap
                this.transactions.add(transaction);
            }
            // add removed transactions back to heap
            for (int i = 0; i < negPts.size(); i++) {
                this.transactions.add(negPts.get(i));
            }
        }

    }

    /**
     * Writes a given SpendPoints object as a string by spitting out all of the
     * companies names and current respective point values
     * 
     * @return The string representation of a give SpendPoints Object
     */
    @Override
    public String toString() {
        // build string to return final point totals of companies
        StringBuilder sb = new StringBuilder();
        this.comps.forEach((k, v) -> {
            sb.append(k + " : " + v + ",\n");
        });
        String res = sb.toString();
        res = res.substring(0, res.length() - 2);
        return res;
    }

    /**
     * gets point for given User object
     * 
     * @return the amount of points a user has left to spend
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * gets remaing transactions from a user object
     * 
     * @return the heap of transaction left
     */
    public PriorityQueue<Transaction> getTransactions() {
        return this.transactions;
    }

    /**
     * A mapping of companies to the amount of points each has remaining
     * 
     * @return a Hashtable of companies and the remaing amount of points each has
     */
    public Hashtable<String, Integer> getComps() {
        return this.comps;
    }
}