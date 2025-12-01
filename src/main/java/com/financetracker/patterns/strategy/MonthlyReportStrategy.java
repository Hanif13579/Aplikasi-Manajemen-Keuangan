package com.financetracker.patterns.strategy;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementasi Strategy: Laporan Bulanan.
 */
public class MonthlyReportStrategy implements ReportStrategy {
    @Override
    public String getReportName() {
        return "Laporan Bulanan";
    }

    @Override
    public String generateReport(List<Transaction> transactions) {
        LocalDate today = LocalDate.now();
        Month currentMonth = today.getMonth();
        int currentYear = today.getYear();

        List<Transaction> monthlyTx = transactions.stream()
                .filter(t -> t.getDate().getMonth() == currentMonth && t.getDate().getYear() == currentYear)
                .toList();

        if (monthlyTx.isEmpty()) {
            return "Tidak ada transaksi bulan ini (" + currentMonth + " " + currentYear + ").";
        }

        double income = 0;
        double expense = 0;

        for (Transaction tx : monthlyTx) {
            if (tx.getType() == TransactionType.INCOME) {
                income += tx.getAmount();
            } else {
                expense += tx.getAmount();
            }
        }
        double net = income - expense;

        // Agregasi pengeluaran per kategori
        Map<String, Double> expenseByCategory = monthlyTx.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().toString(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        StringBuilder report = new StringBuilder();
        
        // Header Laporan menggunakan Text Block dan %n
        report.append(String.format(
            """
            Laporan Bulanan (%s %d):%n
            ----------------------------%n
            Total Pemasukan: Rp %,.2f%n
            Total Pengeluaran: Rp %,.2f%n
            Total Bersih: Rp %,.2f%n%n
            Pengeluaran per Kategori:%n
            """, 
            currentMonth, currentYear, income, expense, net
        ));

        expenseByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // Urutkan dari terbesar
                .forEach(entry -> report.append(String.format("- %s: Rp %,.2f%n", entry.getKey(), entry.getValue())));

        return report.toString(); // Menggunakan toString() adalah default, toList() ada di tempat lain
    }
}