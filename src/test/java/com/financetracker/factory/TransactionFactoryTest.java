package com.financetracker.factory;

import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {

    @Test
    void testCreateTransaction_ValidInput() {
        LocalDate date = LocalDate.now();
        String description = "Test Transaction";
        double amount = 100000;
        TransactionType type = TransactionType.INCOME;
        Category category = Category.GAJI;

        Transaction transaction = TransactionFactory.createTransaction(date, description, amount, type, category);

        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertEquals(date, transaction.getDate());
        assertEquals(description, transaction.getDescription());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertEquals(category, transaction.getCategory());
    }

    @Test
    void testCreateTransaction_InvalidInput() {
        LocalDate date = LocalDate.now();
        Category category = Category.MAKANAN;

        // Test null description
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createTransaction(date, null, 50000, TransactionType.EXPENSE, category);
        });

        // Test empty description
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createTransaction(date, "", 50000, TransactionType.EXPENSE, category);
        });

        // Test negative amount
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createTransaction(date, "Lunch", -100, TransactionType.EXPENSE, category);
        });

        // Test zero amount
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createTransaction(date, "Lunch", 0, TransactionType.EXPENSE, category);
        });

        // Test null type
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createTransaction(date, "Lunch", 50000, null, category);
        });
    }
}
