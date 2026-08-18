# Workspace Rules

## Communication and Action Rules
- **Tanya Dulu Sebelum Modifikasi**: Jika ada kalimat tanya dari pengguna, berarti pengguna hanya bertanya untuk diskusi. Jangan langsung mengubah kode! Berikan pendapat/analisis dulu dan minta persetujuan eksplisit dari pengguna sebelum melakukan aksi modifikasi kode apa pun.

## Android Mobile Rules
- **Integrasi UI/UX (Pro Max)**: Setiap ada modifikasi atau pembuatan antarmuka (UI) XML baru, wajib mengacu pada standar estetika dan komponen dari skill `ui-ux-pro-max` (perhatikan proporsi margin, padding, serta warna *primary* aplikasi). Jangan asal menaruh elemen tanpa memikirkan hierarki visual.
- **Standar API & Retrofit**: Jika ada penambahan atau modifikasi panggilan API melalui Retrofit, pastikan selalu menambahkan *error handling* yang proper (seperti mengecek koneksi, handling *timeout* 60 detik, dan memunculkan `Toast` atau `AlertDialog` ke pengguna jika server gagal dihubungi). Jangan biarkan aplikasi *force close* atau diam membeku (freeze) saat *loading*.
- **Pemisahan Logika (Clean Architecture)**: Selalu pisahkan logika pengolahan data dengan logika tampilan. Gunakan `Repository` (misal: `AbsensiTMRepository`) untuk urusan komunikasi data ke API, dan biarkan `Fragment` / `Activity` hanya bertugas untuk mengatur UI dan menerima *response*.
- **Anti Hardcode String**: Dilarang keras mengetikkan teks secara langsung (hardcode) di file XML maupun Java/Kotlin. Semua teks yang akan dibaca oleh pengguna wajib dimasukkan ke dalam file `res/values/strings.xml`.
- **Standar Penamaan (Naming Convention)**: Penamaan ID di XML wajib menggunakan format *snake_case* (contoh: `btn_submit_absen`), sedangkan variabel di dalam file Java/Kotlin wajib menggunakan *camelCase* (contoh: `btnSubmitAbsen`).
- **Pengecekan Izin (Runtime Permissions)**: Setiap kali membuat fitur yang membutuhkan akses sensitif (seperti Kamera atau Lokasi GPS), wajib memastikan adanya logika pengecekan *Permission* (*runtime permission*) sebelum fitur tersebut dieksekusi agar aplikasi tidak mengalami *Force Close*.
- **Keamanan Data (No Sensitive Logging)**: Dilarang keras melakukan pencetakan log (`Log.d`, `Log.e`, `System.out.println`) yang berisi data sensitif, seperti Token Login (JWT), password, atau hasil respons API yang mengandung data pribadi/karyawan, untuk mencegah kebocoran data di Logcat.
- **Pencegahan Kebocoran Memori (Memory Leak Prevention)**: Setiap kali membuat proses yang berjalan di latar belakang (seperti panggilan API Retrofit, *timer*, atau *listener* antarmuka perangkat keras seperti Kamera), wajib membatalkan atau membersihkan (*cleanup*) proses tersebut pada siklus hidup `onDestroy()` atau `onDestroyView()` dari Fragment/Activity bersangkutan.
