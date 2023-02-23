/**
 * This class test various functionalilty of the program
 */
public class Tester {
    /**
     * Runs all tester methods
     * @param args command line args which won't be used
     */
    public static void main(String[] args){
        System.out.println("testUsers(): " + testUsers());
        System.out.println("testUserInit(): " + testUserInit());
        System.out.println("testSpendPoints(): " + testSpendPoints());
        System.out.println("testCorrectNegComps(): " + testCorrectNegComps());
    }

    /**
     * Tests getters, setters and compareTo of the Transaction class
     * 
     * @return true if all test cases pass and false otherwise
     */
    public static boolean testUsers(){
            Transaction dannon = new Transaction("DANNON", 1000, "2020-11-02T14:00:00Z");
            //CASE 1: Test getName()
            if(!dannon.getName().equals("DANNON")){
                System.out.println("ERROR 1");
                return false;
            }
            //CASE 2: Test getPoint()
            if(dannon.getPoints() != 1000){
                System.out.println("ERROR 2");
                return false;
            }
            //CASE 3: Test getDate()
            if(dannon.getDate() != 20201102140000L){
                System.out.println("ERROR 3");
                return false;
            }
            //CASE 4: Test setName()
            dannon.setName("dannon");
            if(!dannon.getName().equals("dannon")){
                System.out.println("ERROR 4");
                return false;
            }
            //CASE 5: Test getPoint()
            dannon.setPoints(500);
            if(dannon.getPoints() != 500){
                System.out.println("ERROR 5");
                return false;
            }
            //CASE 6: Test getDate()
            dannon.setDate("2020-12-02T14:00:00Z");
            if(dannon.getDate() != 20201202140000L){
                System.out.println("ERROR 6");
                return false;
            }
            //CASE 7: Test compareTo
            Transaction unilever = new Transaction("UNILEVER", 200, "2020-10-31T11:00:00Z");
            if(unilever.compareTo(dannon) >= 0){
                System.out.println("ERROR 7");
                return false;
            }
        return true;
    }

    /**
     * Tests the init() method and constructors of the User class
     * @return true if all tests pass and false otherwise
     */
    public static boolean testUserInit(){
        //CASE 1: Test valid constructor
        User user1 = new User(5000);
        if(user1.getPoints() != 5000){
            System.out.println("ERROR 1");
            return false;
        }
        //CASE 2: Test invalid constructor
        try{
            User user = new User(-1);
            System.out.println("ERROR 2.1");
            return false;
        } catch(IllegalArgumentException e){
            //correct
        } catch(Exception e){
            System.out.println("ERROR 2.2");
            return false;
        }
        //CASE 3: Test valid init()
        user1.init("transactions.csv");
        if(user1.getTransactions().size() != 5){
            System.out.println("ERROR 3.1");
            return false;
        }
        if(user1.getComps().size() != 3){
            System.out.println("ERROR 3.2");
            return false;
        }
        //CASE 4: Test invalid init()
        User user2 = new User(14000);
        try{
            user2.init("transactions.csv");
            System.out.println("ERROR 4.1");
            return false;
        } catch(IllegalArgumentException e){
            //correct
        } catch(Exception e){
            System.out.println("Error 4.2");
            return false;
        }
        return true;
    }

    public static boolean testSpendPoints(){
        //CASE 1: One negative in transactions.csv
        User user = new User(5000);
        user.init("transactions.csv");
        user.spendPoints();
        //check user points
        if(user.getPoints() != 0){
            System.out.println("ERROR 1.1");
            return false;
        }
        //check heap size;
        if(user.getTransactions().size() != 2 || user.getTransactions().peek().getPoints() != 5300){
            System.out.println("ERROR 1.2");
            return false;
        }
        //check company point totals
        if(!user.toString().equals("\"MILLER COORS\" : 5300,\n\"UNILEVER\" : 0,\n\"DANNON\" : 1000")){
            System.out.println("ERROR 1.3");
            return false;
        }

        //CASE 2: points requested = points possible 
        User user2 = new User(11300);
        user2.init("transactions.csv");
        user2.spendPoints();
        //check user points
        if(user2.getPoints() != 0){
            System.out.println("ERROR 2.1");
            return false;
        }
        //check heap size;
        if(user2.getTransactions().size() != 0){
            System.out.println("ERROR 2.2");
            return false;
        }
        //check company point totals
        if(!user2.toString().equals("\"MILLER COORS\" : 0,\n\"UNILEVER\" : 0,\n\"DANNON\" : 0")){
            System.out.println("ERROR 2.3");
            return false;
        }

        //CASE 3: 1 company fixes it's own negatives
        User user3 = new User(500);
        user3.init("oneCompany.csv");
        user3.spendPoints();
        //check user points
        if(user3.getPoints() != 0){
            System.out.println("ERROR 3.1");
            return false;
        }
        //check heap size;
        if(user3.getTransactions().size() != 1 || user3.getTransactions().peek().getPoints() != 200){
            System.out.println("ERROR 3.2");
            return false;
        }
        //check company point totals
        if(!user3.toString().equals("\"DANNON\" : 200")){
            System.out.println(user3);
            System.out.println("ERROR 3.3");
            return false;
        }
        //CASE 4: Multiple tries to fix negative transactions
        User user4 = new User(5000);
        user4.init("bigNegative.csv");
        user4.spendPoints();
        //check user points
        if(user4.getPoints() != 0){
            System.out.println("ERROR 4.1");
            return false;
        }
        //check heap size
        if(user4.getTransactions().size() != 2 || user4.getTransactions().peek().getPoints() != 4900){
            System.out.println("ERROR 4.2");
            return false;
        }
        //check company point totals
        if(!user4.toString().equals("\"MILLER COORS\" : 4900,\n\"UNILEVER\" : 0,\n\"DANNON\" : 1000")){
            System.out.println("ERROR 4.3");
            return false;
        }
        return true;
    }

    public static boolean testCorrectNegComps(){

        //CASE 1: spend points results in a negative company, takes one transaction to fix
        User user1 = new User(5000);
        user1.init("negComp.csv");
        user1.spendPoints();
        user1.correctNegComps();
        if(user1.getPoints() != 0){
            System.out.println("ERROR 5.1");
            return false;
        }
        if(user1.getTransactions().size() != 3 || user1.getTransactions().peek().getPoints() != 5000){
            System.out.println("ERROR: 5.2");
            return false;
        }
        if(!user1.toString().equals("\"MILLER COORS\" : 300,\n\"UNILEVER\" : 0,\n\"DANNON\" : 0")){
            System.out.println("ERROR 1.3");
            return false;
        }

        //CASE 2: spendPoints() results in a negative company, takes two transactions to fix it
        User user2 = new User(5000);
        user2.init("bigNegComp.csv");
        user2.spendPoints();
        user2.correctNegComps();
        if(user2.getPoints() != 0){
            System.out.println("ERROR 2.1");
            return false;
        }
        if(user2.getTransactions().size() != 2 || user2.getTransactions().peek().getPoints() != 15300){
            System.out.println("ERROR 2.2");
            return false;
        }
        if(!user2.toString().equals("\"MILLER COORS\" : 0,\n\"UNILEVER\" : 0,\n\"DANNON\" : 0")){
            System.out.println("ERROR 2.3");
            return false;
        }
        return true;
    }
}
