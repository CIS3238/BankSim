/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cis.c3238.banksim;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tuf65651
 */
public class BankTests {
    
    public static final int NumAccounts = 3;
    public static final int InitialBalance = 0;
    Bank b;
    
    public BankTests() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        b = new Bank(NumAccounts, InitialBalance);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class BankSimMain.
     */
    // @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        BankSimMain.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    /**
     * Check that threads can't withdraw simultaneously and send accounts negative.
     */
    @Test
    public void shouldPreventThreadsOverdrawing() {
        Account giver = new Account(b, 1, 100);
        Account taker1 = new Account(b, 2, InitialBalance);
        Account taker2 = new Account(b, 2, InitialBalance);
        Account taker3 = new Account(b, 2, InitialBalance);
        Account taker4 = new Account(b, 2, InitialBalance);
        // Account taker5 = new Account(b, 2, InitialBalance);
        
        Thread[] pool = new Thread[4];
        pool[0] = new ManualTransferThread(b, giver, taker1, 30);
        pool[1] = new ManualTransferThread(b, giver, taker2, 30);
        pool[2] = new ManualTransferThread(b, giver, taker3, 30);
        pool[3] = new ManualTransferThread(b, giver, taker4, 30);
        
        for(Thread t : pool) {
            t.start();
        }
        
        assertTrue("giver shouldn't go negative", giver.getBalance() >= 0);
    }
}
