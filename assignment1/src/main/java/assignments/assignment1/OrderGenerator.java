package assignments.assignment1;

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
     * Method untuk membuat checksum
     */
    public static String generateChecksum(String orderId) {
        int firstChar = 0;
        int secondChar = 0;

        for (int i = 0; i < orderId.length(); i++) {
            char currentChar = orderId.charAt(i);

            int currentCode = currentChar - 48;  // Dikurangi karakter '0'

            if (currentCode > 9) {
                currentCode -= 7;  // Jika karakter adalah huruf besar, maka akan dikurangi lagi 7 agar cocok
            }

            if (i % 2 == 0) {
                firstChar += currentCode;
            } else {
                secondChar += currentCode;
            }
        }

        // Lakukan mod 36
        firstChar %= 36;
        secondChar %= 36;

        // Kembalikan kode menjadi ascii
        firstChar += 48;
        secondChar += 48;

        if (firstChar > 57) {
            firstChar += 7;
        }

        if (secondChar > 57) {
            secondChar += 7;
        }

        // Mengembalikan order id dengan checksum
        return orderId + Character.toString(firstChar) + Character.toString(secondChar);
    }

    /*
     * Method ini digunakan untuk memvalidasi tanggal
     */
    public static boolean checkDate(String tanggal) {
        // Set tanggal ke null
        Date date = null;

        // Jika tanggal berhasil diformat maka tanggal memiliki format yang benar
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD/MM/YYYY");
            date = simpleDateFormat.parse(tanggal);
        } catch (Exception e) {}

        return date != null;
    }

    /*
     * Method ini digunakan untuk membuat ID
     * dari nama restoran, tanggal order, dan nomor telepon
     * 
     * @return String Order ID dengan format sesuai pada dokumen soal
     */
    public static String generateOrderID(String namaRestoran, String tanggalOrder, String noTelepon) {
        // Buat variabel order id untuk menyimpan order id yang akan dikembalikan
        String orderID = "";

        // Untuk mengecek jumlah karakter order id ketika ditambahkan nama restoran
        int count = 0;

        // Menambahkan nama restoran pada order id
        for (int i = 0; i < namaRestoran.length(); i++) {
            if (count == 4) {
                break;
            }

            if (namaRestoran.charAt(i) == ' ') {
                continue;
            }

            // Menambahkan karakter nama restoran dan count
            orderID += namaRestoran.charAt(i);
            count += 1;
        } 

        // Menambahkan tanggal pada kode order id
        for (int i = 0; i < tanggalOrder.length(); i++) {
            if (tanggalOrder.charAt(i) == '/') {
                continue;
            }

            // Menambahkan angka
            orderID += tanggalOrder.charAt(i);
        }

        // Menambahkan 2 kode dari nomor telepon
        long jumlah = 0;

        for (int i = 0; i < noTelepon.length(); i++) {
            jumlah += noTelepon.charAt(i) - 48;  // Dikurangi kode dari '0' karena charAt berpatokan pada ascii
        }

        // Lakukan modulo 100
        jumlah %= 100;

        // Menambahkan kode nomor telepon
        orderID += String.format("%02d", jumlah);

        // Membuat semua order id menjadi huruf besar sekaligus menambahkan checksum
        orderID = generateChecksum(orderID.toUpperCase());

        return orderID;
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
        // Ambil tanggal pemesanan
        String tanggalOrder = OrderID.substring(4, 6) + "/" + OrderID.substring(6, 8) + "/" + OrderID.substring(8, 12);

        // Menghitung biaya ongkir berdasarkan lokasi
        String biayaOngkir = "";

        lokasi = lokasi.toUpperCase();

        switch (lokasi) {
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
        }

        // Mengembalikan bill
        return String.format("Bill:\n" +
                "Order ID: %s\n" +
                "Tanggal Pemesanan: %s\n" +
                "Lokasi Pengiriman: %s\n" +
                "Biaya Ongkos Kirim: %s\n", OrderID, tanggalOrder, lokasi, biayaOngkir);
    }

    public static void main(String[] args) {
        // Cetak menu awal
        showMenu();

        while (true) {
            System.out.println("-----------------------------------");
            System.out.print("Pilihan menu: ");

            // Meminta input menu
            int menu = input.nextInt();

            // Mereset input sebelumnya
            input.nextLine();

            // Jika menu == 3, program akan berhenti
            if (menu == 3) {
                System.out.println("Terima kasih telah menggunakan DepeFood!");
                break;
            }

            // Menampilkan menu pertama
            if (menu == 1) {

                // Mengambil data nama restoran, tanggal order dan nomor telepon
                String namaRestoran;
                String tanggalOrder;
                String noTelepon;

                while (true) {
                    System.out.print("Nama Restoran: ");
                    namaRestoran = input.nextLine();

                    // Mengecek jumlah karakter pada nama restoran
                    int characterCount = 0;

                    for (int i = 0; i < namaRestoran.length(); i++) {
                        if (characterCount >= 4) {
                            break;
                        }

                        if (namaRestoran.charAt(i) != ' ') {
                            characterCount++;
                        }
                    }

                    // Jika jumlah karakter lebih dari 4 huruf, maka program akan berhenti. Jika
                    // sebaliknya, maka program akan lanjut
                    if (characterCount >= 4) {
                        break;
                    } else {
                        System.out.println("Nama Restoran tidak valid!\n");
                    }
                }

                // Meminta tanggal order
                while (true) {
                    System.out.print("Tanggal Pemesanan: ");
                    tanggalOrder = input.nextLine();

                    // Jika format tanggal sudah benar, maka program akan berhenti meminta tnaggal
                    if (checkDate(tanggalOrder)) {
                        break;
                    }

                    System.out.println("Tanggal Pemesanan dalam format DD/MM/YYYY!\n");
                }

                // Meminta nomor hp
                while(true) {
                    System.out.print("No. Telpon: ");
                    noTelepon = input.nextLine();

                    // Melakukan pengecekan apakah nomor telepon merupakan angka dan bilangan positif
                    try {
                        long notelp = Long.parseLong(noTelepon);

                        if (notelp > 0) {
                            break;
                        }
                    } catch (Exception e) {}

                    System.out.println("Harap masukkan nomor telepon dalam bentuk bilangan bulat positif.\n");
                }

                // Melakukan print output order id
                System.out.println(generateOrderID(namaRestoran, tanggalOrder, noTelepon));

            } else if (menu == 2) {
                String orderId;
                String tanggalOrder;
                String lokasi;

                // Minta input order id
                while (true) {
                    System.out.print("Order ID: ");
                    orderId = input.nextLine();

                    // Melakukan pengecekan jumlah karakter
                    if (orderId.length() < 16) {
                        System.out.println("Order ID minimal 16 karakter");
                        continue;
                    }

                    // Mengambil tanggal order
                    tanggalOrder = orderId.substring(4, 6) + "/" + orderId.substring(6, 8) + "/" + orderId.substring(8, 12);

                    // Validasi order id
                    String noChecksum = orderId.substring(0, orderId.length() - 2);
                    String checksum = orderId.substring(orderId.length() - 2);

                    // Mengecek checksum dan kevalidan tanggal
                    if (!checksum.equals(generateChecksum(noChecksum)) || !checkDate(tanggalOrder)) {
                        System.out.println("Silahkan masukkan Order ID yang valid!");
                        continue;
                    }

                    break;
                }


                // Minta lokasi pengiriman pada user
                while (true) {
                    System.out.print("Lokasi Pengiriman: ");
                    lokasi = input.nextLine().toUpperCase();

                    // Mengecek apakah lokasi yang diminta terdapat pada daftar lokasi
                    if (!(lokasi.equals("P") || lokasi.equals("U") || lokasi.equals("S") || lokasi.equals("T") || lokasi.equals("B"))) {
                        System.out.println("Harap masukkan lokasi pengiriman yang ada pada jangkauan!\n");
                        continue;
                    }

                    break;
                }

                System.out.println("-----------------------------------");
                System.out.println("Pilih menu:");
                System.out.println("1. Generate Order ID");
                System.out.println("2. Generate Bill");
                System.out.println("3. Keluar");
            }

        }

    }

    
}
