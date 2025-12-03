# Aplikasi Manajemen Keuangan

Aplikasi desktop berbasis Java untuk membantu pengguna mencatat dan mengelola keuangan pribadi mereka. Aplikasi ini memungkinkan pengguna untuk mencatat pemasukan dan pengeluaran, melihat riwayat transaksi, serta memantau anggaran bulanan.

## Fitur Utama

*   **Pencatatan Transaksi**: Tambahkan transaksi pemasukan dan pengeluaran dengan mudah.
*   **Kategori Transaksi**: Kelompokkan transaksi berdasarkan kategori (Gaji, Makanan, Transportasi, dll).
*   **Riwayat Transaksi**: Lihat daftar lengkap transaksi yang telah dilakukan.
*   **Manajemen Anggaran**: Tetapkan dan pantau batas anggaran (budget) bulanan.
*   **Laporan & Notifikasi**: Dapatkan wawasan tentang kondisi keuangan Anda.
*   **Penyimpanan Data**: Data tersimpan secara lokal dalam format JSON.

## Teknologi yang Digunakan

*   **Java**: Bahasa pemrograman utama.
*   **Java Swing**: Framework untuk antarmuka pengguna (GUI).
*   **Gson**: Library untuk serialisasi dan deserialisasi data JSON.
*   **Maven**: Alat manajemen proyek dan dependensi.

## Struktur Data

Data transaksi disimpan dalam file `data/transactions.json`. Berikut adalah contoh struktur datanya:

```json
[
  {
    "id": "uuid-string",
    "date": "YYYY-MM-DD",
    "description": "Deskripsi transaksi",
    "amount": 100000.0,
    "type": "INCOME/EXPENSE",
    "category": "NAMA_KATEGORI"
  }
]
```

## Cara Menjalankan Aplikasi

1.  Pastikan Java (JDK) dan Maven sudah terinstal di komputer Anda.
2.  Clone atau download repository ini.
3.  Buka terminal atau command prompt di direktori proyek.
4.  Jalankan perintah berikut untuk mengompilasi dan menjalankan aplikasi:

    ```bash
    mvn clean compile exec:java
    ```

## Pengujian (Unit Testing)

Proyek ini menggunakan **JUnit 5** untuk pengujian unit. Pengujian mencakup validasi logika bisnis, factory pattern, dan strategi laporan.

Untuk menjalankan pengujian, gunakan perintah berikut:

```bash
mvn test
```

## Kriteria Teknis yang Dipenuhi

Proyek ini telah memenuhi kriteria teknis berikut:

1.  **Design Patterns**:
    *   **Factory Pattern**: `TransactionFactory` untuk pembuatan objek transaksi.
    *   **Singleton Pattern**: `StorageManager` untuk manajemen penyimpanan data tunggal.
    *   **Strategy Pattern**: `ReportStrategy` (Daily, Monthly, Yearly) untuk variasi laporan.
    *   **Observer Pattern**: `BudgetObserver` untuk notifikasi perubahan budget.
2.  **JUnit**: Implementasi unit test untuk komponen kritis.
3.  **Java Collections Framework (JCF)**: Penggunaan `List`, `Map`, `ArrayList`, `HashMap`.
4.  **Clean Code**: Penerapan prinsip penamaan yang jelas dan struktur kode yang rapi.
5.  **Generic Programming**: Penggunaan Generics pada `List<Transaction>` dan Gson `TypeToken`.
6.  **GUI**: Antarmuka pengguna berbasis Java Swing.

## Anggota Kelompok

| Nama | NIM |
| :--- | :--- |
| Dava Ramadhan | 241511070 |
| Hanifidin Ibrahim | 241511076 |
| Muhammad Raihan Abubakar | 241511084 |
