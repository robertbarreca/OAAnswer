
/**
 * This class will create Transaction objects, while containing getters and
 * setters for the Transaction's data fields.
 * 
 * @author robertbarreca
 *
 */
public class Transaction implements Comparable<Transaction> {
    private String name = ""; // the name of the transaction
    private int points = 0; // current amount of points a transaction has
    private long date = 0; // time transaction registered represented as a long

    /**
     * Constructor, creates a new transaction object
     * 
     * @param companyName the name of the transaction
     * @param pts         initial amount of points a transaction has
     * @param time        the timestamp of when a transaction registered
     */
    public Transaction(String companyName, int pts, String time) {
        this.name = companyName;
        this.points = pts;
        String t = time.replaceAll("[^0-9]", "");
        this.date = Long.parseLong(t);
    }

    /**
     * Gets the transaction's name
     * 
     * @return the transaction's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get's the transaction's current point total
     * 
     * @return the transaction's current point total
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Get's the date and time when the transaction was completed
     * 
     * @return a long representation of the date they registered
     */
    public long getDate() {
        return this.date;
    }

    /**
     * Give the current transaction a new name
     * 
     * @param companyName the current transaction's new transaction name
     */
    public void setName(String companyName) {
        this.name = companyName;
    }

    /**
     * Sets new point total for current transaction
     * 
     * @param pts what the transaction new point total will be
     */
    public void setPoints(int pts) {
        this.points = pts;
    }

    /**
     * Sets new point date for current transaction
     * 
     * @param pts what the transaction new date total will be
     */
    public void setDate(String time) {
        String t = time.replaceAll("[^0-9]", "");
        this.date = Long.parseLong(t);
    }

    /**
     * 
     * @param other the other transaction we're comparing the current transaction to
     * @return a negative number if current transaction's date is older than other
     *         transaction. A positive number if the other transaction is newer.
     *         otherwise if they have the same date, return a negative number if
     *         current transaction's points is greater than other transaction's
     *         points and a positive otherwise
     */
    @Override
    public int compareTo(Transaction other) {
        if (this.date < other.date)
            return -1;
        else if (this.date > other.date)
            return 1;
        else {
            return other.points - this.points;
        }
    }
}