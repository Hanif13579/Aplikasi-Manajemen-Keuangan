package com.financetracker.storage;

import com.financetracker.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StorageManager — versi Refactored (Clean Code).
 * - Menggunakan Logger (bukan System.out).
 * 
 * - Singleton aman (Eager Initialization).
 * - Konstanta terpusat.
 */
@SuppressWarnings("java:S6548")
public class StorageManager {

    private static final Logger logger = Logger.getLogger(StorageManager.class.getName());

    private static final StorageManager INSTANCE = new StorageManager();

    private final Gson gson;

    // 3. Konstanta File & Direktori
    private static final String DATA_DIR = "data";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.json";
    private static final String NOTIFICATIONS_FILE = DATA_DIR + "/notifications.log";
    private static final String BUDGET_FILE = DATA_DIR + "/budget.txt";
    private static final String DEFAULT_BUDGET = "2000000";


    private static final DateTimeFormatter LOG_TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Constructor Private
    private StorageManager() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        ensureStorage();
    }

    /**
     * Akses satu-satunya ke instance StorageManager.
     */
    public static StorageManager getInstance() {
        return INSTANCE;
    }

    /**
     * Pastikan folder dan file penting ada.
     */
    private void ensureStorage() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                logger.info("Direktori 'data' dibuat.");
            }

            // Cek file transaksi
            Path transPath = Paths.get(TRANSACTIONS_FILE);
            if (!Files.exists(transPath)) {
                Files.write(transPath, "[]".getBytes(StandardCharsets.UTF_8));
            }

            // Cek file budget
            Path budgetPath = Paths.get(BUDGET_FILE);
            if (!Files.exists(budgetPath)) {
                Files.write(budgetPath, DEFAULT_BUDGET.getBytes(StandardCharsets.UTF_8));
            }

            // Cek file log
            Path logPath = Paths.get(NOTIFICATIONS_FILE);
            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }

        } catch (IOException e) {
            // Gunakan Logger Level SEVERE untuk error
            logger.log(Level.SEVERE, "Gagal memastikan direktori penyimpanan", e);
        }
    }

    // ============================================================
    //                  TRANSACTION STORAGE
    // ============================================================

    public void saveTransactions(List<Transaction> transactions) {
        try (Writer writer = new FileWriter(TRANSACTIONS_FILE, StandardCharsets.UTF_8)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan transaksi", e);
        }
    }

    public List<Transaction> loadTransactions() {
        try (Reader reader = new FileReader(TRANSACTIONS_FILE, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<Transaction>>() {}.getType();
            List<Transaction> transactions = gson.fromJson(reader, listType);
            return (transactions != null) ? transactions : new ArrayList<>();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal memuat transaksi", e);
            return new ArrayList<>();
        }
    }

    // ============================================================
    //                  NOTIFICATION LOGGING
    // ============================================================

    public void logNotification(String message) {
        // Menggunakan formatter static
        String timestamp = LocalDateTime.now().format(LOG_TIMESTAMP_FORMAT);
        String logEntry = String.format("[%s] %s%n", timestamp, message);

        try {
            Files.write(Paths.get(NOTIFICATIONS_FILE),
                    logEntry.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menulis log notifikasi", e);
        }
    }

    // ============================================================
    //                  BUDGET PERSISTENCE
    // ============================================================

    public void saveMonthlyBudget(double amount) {
        try {
            Files.write(
                    Paths.get(BUDGET_FILE),
                    String.valueOf(amount).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan budget", e);
        }
    }

    public Double loadMonthlyBudget() {
        try {
            String text = Files.readString(Paths.get(BUDGET_FILE));
            return Double.parseDouble(text.trim());
        } catch (IOException | NumberFormatException e) {
            logger.log(Level.WARNING, "Gagal memuat budget (gunakan default)", e);
            return null;
        }
    }
}