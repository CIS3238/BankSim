package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;

    /**
     * Maximum number of simultaneous transactions allowed.
     */
    private final int MAX_SYNCH_TRANSACTIONS = 10;

    /**
     * Track number of transactions currently executing.
     */
    private static Semaphore transactionsInProgress;

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;

        transactionsInProgress = new Semaphore(MAX_SYNCH_TRANSACTIONS);
    }

    /**
     * For use by transaction threads before acquiring lock, and test thread to halt transactions
     * @return Semaphore object storing number of threads currently conducting transactions
     * TODO: Don't pass object. Provide better wrapper methods.
     */
    protected static Semaphore getSemaphore() {
        return transactionsInProgress;
    }

    public void transfer(int from, int to, int amount) {

        boolean havePermit, haveFromLock, haveToLock;

        do { // Repeatedly try to establish complete exclusive access, until locked out test and other transfers.
            havePermit = getSemaphore().tryAcquire();
            accounts[from].lockAccount();
            haveFromLock = accounts[from].accountLockedInCurrentThread();
            accounts[to].lockAccount();
            haveToLock = accounts[to].accountLockedInCurrentThread();

            if(!havePermit || !haveFromLock || !haveToLock) { // failed to lock critical section -- back down
                accounts[from].releaseAccount();
                accounts[to].releaseAccount();
                if (havePermit) {
                    getSemaphore().release();
                }
            }
        } while(!havePermit || !haveFromLock || !haveToLock) ;// keep redundant check to improve readability
//        accounts[from].waitForAvailableFunds(amount);
        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
        }
        if (shouldTest()) test();

        // Release everything -- guaranteed to hold so no check needed.
        accounts[from].releaseAccount();
        accounts[to].releaseAccount();
        getSemaphore().release();
    }

    public void test() {

        try {
            getSemaphore().acquireUninterruptibly(MAX_SYNCH_TRANSACTIONS);

            int sum = 0;
            for (Account account : accounts) {
                System.out.printf("%s %s%n",
                        Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
            System.out.println(Thread.currentThread().toString() +
                    " Sum: " + sum);
            if (sum != numAccounts * initialBalance) {
                System.out.println(Thread.currentThread().toString() +
                        " Money was gained or lost");
                System.exit(1);
            } else {
                System.out.println(Thread.currentThread().toString() +
                        " The bank is in balance");
            }
        } finally {
            getSemaphore().release(MAX_SYNCH_TRANSACTIONS);
        }
    }

    public int size() {
        return accounts.length;
    }
    
    
    public boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}
