package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
public class Account {

    private volatile int balance;
    private final int id;
    private final Bank myBank;
    private ReentrantLock accountLock;

    public Account(Bank myBank, int id, int initialBalance) {
        this.myBank = myBank;
        this.id = id;
        balance = initialBalance;
        accountLock = new ReentrantLock();
    }

    public int getBalance() {
        return balance;
    }

    /**
     * Wrapper for acquiring instance's lock.
     */
    public void lockAccount(){
        accountLock.tryLock();
    }

    /**
     * Wrapper for releasing instance's lock. Will not throw exception.
     * post: current thread does not hold lock
     */
    public void releaseAccount() {
        if(accountLock.isHeldByCurrentThread()){
            accountLock.unlock();
        }
    }

    /**
     * Wrapper to check if account is locked by current thread.
     */
    public boolean accountLockedInCurrentThread() {
        return accountLock.isHeldByCurrentThread();
    }

    /**
     * Attempt withdrawal of given amount. Checks for lock held by current thread.
     * Checks if balance will go negative. Withdraws amount and returns true if
     * lock is held and balance is sufficient. Otherwise returns false.
     *
     * @param amount int amount of money to withdraw
     * @return true if withdrawal succeeded, else false.
     */
    public boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
//            Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempt deposit. If account is involved in another transaction, wait on it.
     * When account becomes available, lock it from other transactions,
     * deposit amount into it, and unlock it.
     *
     * @param amount int amount of money to deposit.
     */
    public void deposit(int amount) {
            int currentBalance = balance;
//        Thread.yield();   // Try to force collision
            int newBalance = currentBalance + amount;
            balance = newBalance;
    }
    
    protected int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}
