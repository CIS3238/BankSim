/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cis.c3238.banksim;

/**
 *
 * @author Daveed
 */
public class TestThread extends Thread {
    private final Bank bank;
    
    public TestThread(Bank b) {
        bank = b;
    }
    
        @Override
    public void run() {
        
        while (TransferThread.stopAllTransfers == false)
        {
            try {
                this.sleep(30);
            }
            catch (InterruptedException e) {}
            
            bank.test();
        }
        
        bank.test(); // one last test after all transfers stop
        
    }
    
}
