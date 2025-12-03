package com.financetracker.patterns.strategy;

import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportStrategyTest {

    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        transactions = new ArrayList<>();
        transactions.add(new Transaction("1", LocalDate.now(), "Salary", 5000000, TransactionType.INCOME,
                Category.GAJI));
        transactions.add(new Transaction("2", LocalDate.now(), "Food", 50000, TransactionType.EXPENSE,
                Category.MAKANAN));
        transactions.add(new Transaction("3", LocalDate.now().plusMonths(1), "Bonus", 1000000, TransactionType.INCOME,
                Category.LAINNYA));
    }

    @Test
    void testDailyReportStrategy() {
        ReportStrategy strategy = new DailyReportStrategy();
        String report = strategy.generateReport(transactions);

        assertNotNull(report);
        assertTrue(report.contains("Laporan Harian"));
        assertTrue(report.contains("Salary")); // Should contain description
        assertTrue(report.contains("Total Pemasukan")); // Should contain summary label
    }

    @Test
    void testMonthlyReportStrategy() {
        ReportStrategy strategy = new MonthlyReportStrategy();
        String report = strategy.generateReport(transactions);

        assertNotNull(report);
        assertTrue(report.contains("Laporan Bulanan"));
        // Monthly report usually aggregates or lists by month.
        // Assuming implementation details, checking for presence of month names or
        // totals.
        // Since we don't see the exact implementation of MonthlyReportStrategy, we
        // check for basic non-empty output.
        assertFalse(report.isEmpty());
    }

    @Test
    void testYearlyReportStrategy() {
        ReportStrategy strategy = new YearlyReportStrategy();
        String report = strategy.generateReport(transactions);

        assertNotNull(report);
        assertTrue(report.contains("Laporan Tahunan"));
        assertFalse(report.isEmpty());
    }
}
