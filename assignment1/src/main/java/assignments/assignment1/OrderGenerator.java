package assignments.assignment1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class OrderGenerator {
    private static final Scanner input = new Scanner(System.in);

    /* 
    Anda boleh membuat method baru sesuai kebutuhan Anda
    Namun, Anda tidak boleh menghapus ataupun memodifikasi return type method yang sudah ada.
    */

    /*
     * Method  ini untuk menampilkan menu
     */
    public static void showMenu(){
        System.out.println(">>=======================================<<");
        System.out.println("|| ___                 ___             _ ||");
        System.out.println("||| . \\ ___  ___  ___ | __>___  ___  _| |||");
        System.out.println("||| | |/ ._>| . \\/ ._>| _>/ . \\/ . \\/ . |||");
        System.out.println("|||___/\\___.|  _/\\___.|_| \\___/\\___/\\___|||");
        System.out.println("||          |_|                          ||");
        System.out.println(">>=======================================<<");
        System.out.println();
        System.out.println("Pilih menu:");
        System.out.println("1. Generate Order ID");
        System.out.println("2. Generate Bill");
        System.out.println("3. Keluar");
    }

    /*
     * Method ini digunakan untuk membuat ID
     * dari nama restoran, tanggal order, dan nomor telepon
     * 
     * @return String Order ID dengan format sesuai pada dokumen soal
     */
    public static String generateOrderID(String namaRestoran, String tanggalOrder, String noTelepon) {
        // Buat variabel untuk membentuk order id
        StringBuilder orderId = new StringBuilder();

        // Ubah semua karakter menjadi lowercase
        namaRestoran = namaRestoran.toUpperCase();

        // Loop untuk membaca nama restoran dan menambahkan informasinya ke order id
        for (int i = 0; i < namaRestoran.length(); i++) {

            // Jika order id telah mencapai empat karakter, maka loop akan stop
            if (orderId.length() == 4) {
                break;
            }

            char currentChar = namaRestoran.charAt(i);

            // Menambahkan karakter saat ini ke orderId
            orderId.append(currentChar);
        }

        // Masukkan data tanggal pada order id
        for (int i = 0; i < tanggalOrder.length(); i++) {

            // Karakter '/' dikecualikan
            if (tanggalOrder.charAt(i) == '/') {
                continue;
            }

            orderId.append(tanggalOrder.charAt(i));
        }

        int jumlahNomorTelp = 0;

        for (int i = 0; i < noTelepon.length(); i++) {

            // Karakter digit pertama (0) memiliki kode 48 pada Ascii, sehingga untuk memparse digit 0-9
            // kita dapat mengurangi karakter saat ini dengan 48.
            jumlahNomorTelp += noTelepon.charAt(i) - 48;

        }

        // Tambahkan kode nomor telepon yang sudah diformat
        orderId.append(String.format("%02d", jumlahNomorTelp));

        return checksumGenerator(orderId.toString());
    }

    public static String checksumGenerator(String orderIdNoChecksum) {
        StringBuilder sb = new StringBuilder(orderIdNoChecksum);

        int checksum1 = 0;
        int checksum2 = 0;

        for (int i = 0; i < sb.length(); i++ ) {
            char currentChar = sb.charAt(i);

            // Mengecek apakah termasuk ke indeks genap atau ganjil
            if (i % 2 != 0) {
                checksum1 += encodeCode(currentChar);
            } else {
                checksum2 += encodeCode(currentChar);
            }
        }

        // Melakukan modulo 36
        checksum1 %= 36;
        checksum2 %= 36;

        // Melakukan decode dan menambahkan kode checksum ke order id
        sb.append(decodeCode(checksum2));
        sb.append(decodeCode(checksum1));

        // Mengembalikan order id
        return sb.toString();
    }

    public static boolean checksumValidator(String orderId) {
        // Pengecekan checksum

        // Melakukan pemisahan antara order id asli dan checksum
        String orderIdNoChecksum = orderId.substring(0, 14);
        String checksumChars = orderId.substring(14);

        // Mengembalikan apakah orderid asli atau tidak
        return checksumGenerator(orderIdNoChecksum).equals(orderIdNoChecksum + checksumChars);
    }

    public static String getDate(String orderId) throws Exception {
        // Fungsi untuk mengembalikan tanggal dari order id

        String dateString = orderId.substring(4, 12);

        // Memformat tanggal
        dateString = String.format("%s/%s/%s", dateString.substring(0,2), dateString.substring(2, 4), dateString.substring(4, 8));

        // Mengecek tanggal
        if (!dateChecker(dateString)) {
            throw new Exception();
        }

        // Mereturn tanggal
        return dateString;
    }

    public static boolean dateChecker(String tanggalOrder) {
        // Fungsi untuk mengecek format tanggal

        // Cek karakter slash ('/') pada tanggal
        if (tanggalOrder.charAt(2) != '/' || tanggalOrder.charAt(5) != '/') {
            return false;
        }

        int tanggal;
        int bulan;
        int tahun;

        try {
            tanggal = Integer.parseInt(tanggalOrder.substring(0, 2));
            bulan = Integer.parseInt(tanggalOrder.substring(3, 5));
            tahun = Integer.parseInt(tanggalOrder.substring(6));

            if (tanggal > 31 || tanggal < 0 || bulan > 12 || bulan < 0 || tahun < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean orderIdValidator(String orderId) {
        // Mengecek checksum
        if (!checksumValidator(orderId)) {
            return false;
        }

        // Mengecek tanggal
        try {
            getDate(orderId);
        } catch (Exception ignored) {
            return false;
        }

        // Mengembalikan nilai true jika berhasil melewati semua pengecekan
        return true;
    }


    public static int encodeCode(char c) {
        // Cek apakah karakter adalah digit atau huruf besar (asumsi input user selalu alfanumerik).
        if (c <= 57) {  // Jika digit
            c -= 48;  // Dikurang dengan kode ascii '0'
        } else {  // Jika huruf besar
            c -= 55;  // Dikurang dengan kode ascii 'A' + 10
        }

        return c;
    }

    public static char decodeCode(int i) {
        // Cek apakah kode termasuk digit atau huruf

        if (i < 10) {  // Apabila kode termasuk digit
            i += 48;  // Menambahkan 48 untuk mengubahnya kembali ke bentuk digit dalam kode ascii
        } else {  // Apabila kode termasuk huruf
            i += 55;  // Menambahkan 55 untuk mengubahnya kembali ke bentuk huruf besar dalam kode ascii
        }
        return (char) i;
    }



    /*
     * Method ini digunakan untuk membuat bill
     * dari order id dan lokasi
     * 
     * @return String Bill dengan format sesuai di bawah:
     *          Bill:
     *          Order ID: [Order ID]
     *          Tanggal Pemesanan: [Tanggal Pemesanan]
     *          Lokasi Pengiriman: [Kode Lokasi]
     *          Biaya Ongkos Kirim: [Total Ongkos Kirim]
     */
    public static String generateBill(String OrderID, String lokasi){

        String biayaOngkir;

        // Mengecek biaya ongkir sekaligus mengecek lokasi pengiriman
        switch (lokasi.toUpperCase()) {
            case "P":
                biayaOngkir = "Rp 10.000";
                break;
            case "U":
                biayaOngkir = "Rp 20.000";
                break;
            case "T":
                biayaOngkir = "Rp 35.000";
                break;
            case "S":
                biayaOngkir = "Rp 40.000";
                break;
            case "B":
                biayaOngkir = "Rp 60.000";
                break;
            default:
                biayaOngkir = "";
                break;
        }

        // Mengambil tanggal pemesanan
        String tanggalPemesanan = "";
        try {
            tanggalPemesanan = getDate(OrderID);
        } catch (Exception ignored) {}

        return String.format(
                "Bill:\nOrder ID: %s\nTanggal Pemesanan: %s\nLokasi Pengiriman: %s\nBiaya Ongkos Kirim: %s\n",
                OrderID,
                tanggalPemesanan,
                lokasi.toUpperCase(),
                biayaOngkir
        );
    }

    public static void main(String[] args) {
        // TODO: Implementasikan program sesuai ketentuan yang diberikan
        showMenu();

        while (true) {
            System.out.println("--------------------------------");
            System.out.print("Pilihan menu: ");

            int menu = input.nextInt();

            // Melakukan reset untuk meminta input nextLine
            input.nextLine();

            if (menu == 1) {
                String namaRestoran;
                String tanggalOrder;
                String noTelepon;

                // Menanyakan nama restoran
                while (true) {
                    System.out.print("Nama Restoran: ");
                    namaRestoran = input.nextLine().toUpperCase();

                    StringBuilder namaRestoranNoWhitespace = new StringBuilder();

                    // Memotong whitespace pada nama restoran
                    for (int i = 0; i < namaRestoran.length(); i++) {
                        if (namaRestoran.charAt(i) != ' ') {
                            namaRestoranNoWhitespace.append(namaRestoran.charAt(i));
                        }
                    }

                    namaRestoran = namaRestoranNoWhitespace.toString();

                    if (namaRestoran.length() < 4) {
                        System.out.println("Nama Restoran tidak valid\n");
                        continue;
                    }

                    // Menghentikan loop apabila nama restoran sudah valid
                    break;
                }

                // Menanyakan tanggal pemesanan
                while (true) {
                    System.out.print("Tanggal pemesanan: ");
                    tanggalOrder = input.nextLine();

                    if (!dateChecker(tanggalOrder)) {
                        System.out.println("Tanggal Pemesanan dalam format DD/MM/YYYY!\n");
                        continue;
                    }

                    break;
                }

                // Menanyakan nomor hp
                while (true) {
                    System.out.print("No. telepon: ");
                    noTelepon = input.nextLine();

                    // Cek apakah nomor hp valid
                    try {
                        long num = Long.parseLong(noTelepon);

                        if (num > 0) {
                            break;
                        }
                    } catch (NumberFormatException ignored) {}

                    System.out.println("Harap masukan nomor telepon dalam bentuk bilangan bulat positif.\n");
                }

                // Memparse informasi dari user untuk dijadikan order ID
                String orderId = generateOrderID(namaRestoran, tanggalOrder, noTelepon);

                String order = String.format("Order ID %s diterima!\n", orderId);

                System.out.println(order);

            } else if (menu == 2) {

                // Meminta order id
                System.out.print("Order ID: ");
                String orderId = input.nextLine();

                // Melakukan pengecekan order id
                if (orderId.length() != 16) {
                    System.out.println("Order ID harus 16 karakter\n");
                    continue;
                } else if (checksumValidator(orderId)) {
                    System.out.println("Silahkan masukkan Order ID yang valid!\n");
                    continue;
                }

                // Meminta lokasi pengiriman
                System.out.print("Lokasi Pengiriman: ");
                String lokasiPengiriman = input.nextLine();

                boolean isLokasiValid = false;

                // Pengecekan lokasi pengiriman
                String[] daftarLokasi = {"P", "U", "T", "S", "B"};
                for (String lok: daftarLokasi) {
                    if (lokasiPengiriman.toUpperCase().equals(lok)) {
                        isLokasiValid = true;
                        break;
                    }
                }

                // Apabila lokasi pengiriman tidak terdaftar program akan mengulang pertanyaan
                if (!isLokasiValid) {
                    System.out.println("Harap masukkan lokasi pengiirman yang ada pada jangkauan!\n");
                    continue;
                }

                System.out.println(generateBill(orderId, lokasiPengiriman));

            } else {
                System.out.println("Terima kasih telah menggunakan DepeFood!");
                break;
            }

            System.out.println();

            System.out.println("Pilih menu:");
            System.out.println("1. Generate Order ID");
            System.out.println("2. Generate Bill");
            System.out.println("3. Keluar");
        }
    }

    
}
