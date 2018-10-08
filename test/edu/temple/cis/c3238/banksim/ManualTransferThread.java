/*
 * Created by Shmuel Jacobs for the purpose of testing Lab 5, Software Development
 */
package edu.temple.cis.c3238.banksim;

/**
 * Allow manual creation of transactions for testing purposes.
 * Requires Account objects rather than id numbers, so doesn't correspond to
 * TransferThread class.
 * @author tuf65651
 */
public class ManualTransferThread extends Thread {

    /**
     * Account to receive transfer.
     */
    private Account to;

    /**
     * Account to offer transfer.
     */
    private Account from;

    /**
     * Bank object that references both accounts.
     */
    private Bank b;

    /**
     * Amount transfered. Implemented for clarity. Could use existing max field
     * from super, but would be confusing since this isn't used as a max.
     */
    private int amount;

    public ManualTransferThread(Bank b, Account from, Account to, int amount) {
        this.b = b;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            b.transfer(from.getId(), to.getId(), amount);
            String status = String.format("Transfer %d from %d to %d.", from, to, amount);
            System.out.println( status );
        }
        catch (Exception e) {
            System.err.println(e.toString());
        }
        finally {
            System.out.println( from.toString() );
            System.out.println( to.toString() );
        }
    }

}
