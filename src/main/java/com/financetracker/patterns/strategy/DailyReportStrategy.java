package com.financetracker.patterns.strategy;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementasi Strategy: Laporan Harian.
 */
public class DailyReportStrategy implements ReportStrategy {
    @Override
    public String getReportName() {
        return "Laporan Harian";
    }

    @Override
    public String generateReport(List<Transaction> transactions) {
        LocalDate today = LocalDate.now();
        List<Transaction> dailyTx = transactions.stream()
                .filter(t -> t.getDate().equals(today))
                // Mengganti .collect(Collectors.toList()) dengan .toList()
                .toList(); 

        if (dailyTx.isEmpty()) {
            // Mengganti \n dengan %n di pesan non-formatted string (opsional, tapi disarankan)
            return "Tidak ada transaksi hari ini (" + today + ").%n";
        }

        double income = dailyTx.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        double expense = dailyTx.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        double net = income - expense;

        StringBuilder report = new StringBuilder();
        
        // Menggabungkan Header Laporan dengan Text Block dan %n
        report.append(String.format(
            """
            Laporan Harian (%s):%n
            ----------------------------%n
            Total Pemasukan: Rp %,.2f%n
            Total Pengeluaran: Rp %,.2f%n
            Total Bersih: Rp %,.2f%n%n
            Detail Transaksi:%n
            """,
            today, income, expense, net
        ));

        for (Transaction tx : dailyTx) {
            // Mengganti \n dengan %n di dalam loop
            report.append(String.format("- (%s) %s: Rp %,.2f%n",
                    tx.getCategory(), tx.getDescription(), tx.getAmount()));
        }

        return report.toString();
    }
}