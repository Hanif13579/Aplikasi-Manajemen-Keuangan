package com.financetracker;

import com.financetracker.ui.MainFrame;

import javax.swing.*;
import java.io.File;
import java.util.logging.Logger;

/**
 * Kelas entri utama untuk aplikasi Personal Finance Tracker.
 * Memastikan direktori /data ada sebelum memulai GUI.
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName()); 
    public static void main(String[] args) {
        // Pastikan direktori 'data' ada
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            logger.info("Membuat direktori 'data'...");
            dataDir.mkdir();
        }

        // Jalankan GUI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}