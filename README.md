# 📱 Mobile Face Recognition Attendance App

Aplikasi absensi berbasis Android yang menggunakan teknologi deteksi wajah (Face Recognition & Liveness Detection) untuk memverifikasi kehadiran karyawan. Aplikasi ini terhubung dengan Backend Node.js dan Server Machine Learning Python.

## ✨ Fitur Utama

- **🔐 Autentikasi Pegawai**: Login aman menggunakan JWT Token.
- **📸 Liveness Detection**: Memastikan pegawai melakukan absensi secara langsung (bukan menggunakan foto cetak) menggunakan teknologi Face Detection dari MLKit & CameraX.
- **✅ Absensi Kehadiran**: Mendukung proses *Check In* dan *Check Out* dengan pengiriman foto *selfie* langsung ke server.
- **📊 Riwayat Absensi (History)**: Menampilkan log absensi pegawai lengkap dengan status persetujuan (*Pending*, *Approved*, *Rejected*), waktu absensi, dan tingkat akurasi kecocokan wajah.

## 🛠️ Teknologi yang Digunakan

- **Bahasa Pemrograman**: Java
- **Kamera & Pemrosesan Gambar**: AndroidX CameraX API
- **Deteksi Wajah (On-Device)**: Google MLKit Face Detection
- **Networking**: Retrofit2 & OkHttp3 (Pengiriman multipart/form-data)
- **UI & Layouting**: XML Layouts dengan Material Design Components

## ⚙️ Arsitektur Sistem

Aplikasi ini tidak bekerja sendirian, melainkan bagian dari arsitektur 3 lapis:
1. **Aplikasi Mobile (Android)**: Menangani UI, liveness detection, capture foto selfie, dan mengirim request absensi ke Backend.
2. **Backend (Node.js/Express)**: Menerima request absensi, memvalidasi token, dan meneruskan foto ke ML Server. Terhubung langsung ke database **Supabase**.
3. **ML Server (Python)**: Menerima foto dari Backend, melakukan ekstraksi fitur wajah, dan mengembalikan *similarity score* (0-100%).

## 🚀 Cara Menjalankan Project (Setup Lokal)

1. **Clone Repository**
   ```bash
   git clone https://github.com/Magang-Talangmas/mobile-recognition.git
   ```

2. **Buka di Android Studio**
   Buka folder project ini menggunakan Android Studio (versi Flamingo / Giraffe ke atas direkomendasikan).

3. **Konfigurasi API Endpoint (`local.properties`)**
   Aplikasi membutuhkan URL Backend untuk melakukan request API. Buat (atau buka) file `local.properties` di direktori *root* project (sejajar dengan file `build.gradle` project), lalu tambahkan baris berikut:

   ```properties
   # Ganti IP di bawah dengan IP komputer Backend (Node.js) kamu.
   API_BASE_URL="http://192.168.1.103:5000/api/v1/"
   ```
   *Catatan: Pastikan menggunakan tanda kutip `""` agar terbaca sebagai string di `BuildConfig`.*

4. **Sync Gradle & Build**
   Tunggu hingga proses sinkronisasi Gradle selesai, lalu jalankan (Run) ke perangkat Android fisik atau Emulator.

## 📌 Catatan Tambahan
- **Pengujian Kamera**: Untuk fitur absensi wajah, sangat disarankan menggunakan **Device Fisik (HP)** agar kamera dan *liveness detection* dapat berjalan dengan lancar.
- **Izin Akses**: Aplikasi membutuhkan izin akses Kamera (`CAMERA`) dan Akses Internet (`INTERNET`).

---
*Dibuat untuk keperluan Magang Talangmas*
