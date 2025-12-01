package com.financetracker.service;

import com.financetracker.patterns.observer.BudgetObserver;
import com.financetracker.storage.StorageManager;
import java.util.logging.Logger; // Import Logger
import java.util.logging.Level;  // Import Level

/**
 * Implementasi Observer Pattern.
 * Service ini "mendengarkan" perubahan budget (dari TransactionService)
 * dan mencatat notifikasi ke file log saat di-update.
 */
public class NotificationService implements BudgetObserver {

    // 1. Deklarasi Logger (biasanya static final)
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    
    private final StorageManager storageManager;

    public NotificationService() {
        this.storageManager = StorageManager.getInstance();
    }

    /**
     * Dipanggil oleh Subject (TransactionService) ketika budget terlampaui.
     */
    @Override
    public void update(String message) {
        // 2. Mengganti System.out.println dengan logger.info
        logger.log(Level.INFO, "NotificationService Menerima Update: {0}", message);
        
        storageManager.logNotification(message);
    }
}