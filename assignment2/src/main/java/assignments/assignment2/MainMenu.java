package assignments.assignment2;

import assignments.assignment1.OrderGenerator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class MainMenu {
    private static final Scanner input = new Scanner(System.in);
    private static ArrayList<Restaurant> restoList = new ArrayList<>();
    private static ArrayList<User> userList = new ArrayList<>();
    private static ArrayList<Order> orderList = new ArrayList<>();
    private static User userLoggedIn = null;

    public static void main(String[] args) {
        initUser();
        boolean programRunning = true;
        while(programRunning){
            printHeader();
            startMenu();
            int command = input.nextInt();
            input.nextLine();

            if(command == 1){
                System.out.println("\nSilakan Login:");
                System.out.print("Nama: ");
                String nama = input.nextLine();
                System.out.print("Nomor Telepon: ");
                String noTelp = input.nextLine();

                userLoggedIn = getUser(nama, noTelp);

                // Jika user tidak ada, maka loop akan diulang
                if (userLoggedIn == null) {
                    continue;
                }

                boolean isLoggedIn = true;
                System.out.printf("Selamat Datang %s!\n", userLoggedIn.getNama());

                if(Objects.equals(userLoggedIn.getRole(), "Customer")){
                    while (isLoggedIn){
                        menuCustomer();
                        int commandCust = input.nextInt();
                        input.nextLine();

                        switch(commandCust){
                            case 1 -> handleBuatPesanan();
                            case 2 -> handleCetakBill();
                            case 3 -> handleLihatMenu();
                            case 4 -> handleUpdateStatusPesanan();
                            case 5 -> isLoggedIn = false;
                            default -> System.out.println("Perintah tidak diketahui, silakan coba kembali");
                        }
                    }
                }else{
                    while (isLoggedIn){
                        menuAdmin();
                        int commandAdmin = input.nextInt();
                        input.nextLine();

                        switch(commandAdmin){
                            case 1 -> handleTambahRestoran();
                            case 2 -> handleHapusRestoran();
                            case 3 -> isLoggedIn = false;
                            default -> System.out.println("Perintah tidak diketahui, silakan coba kembali");
                        }
                    }
                }
            }else if(command == 2){
                programRunning = false;
            }else{
                System.out.println("Perintah tidak diketahui, silakan periksa kembali.");
            }
        }
        System.out.println("\nTerima kasih telah menggunakan DepeFood ^___^");
    }

    /*
    * Fungsi untuk mengambil data user
    * */
    public static User getUser(String nama, String nomorTelepon){

        // Melakukan looping pada userList untuk mengecek user satu per satu
        for (User user : userList) {

            // Jika nama user dan nomor telepon sesuai, maka akan dikembalikan user saat ini
            if (user.getNama().equals(nama) && user.getNomorTelepon().equals(nomorTelepon)) {
                return user;
            }
        }

        // Jika tidak ditemukan, fungsi akan mereturn null
        return null;
    }

    /*
    * Fungsi untuk menghandle pesanan dari user
    *
    * */
    public static void handleBuatPesanan(){
        System.out.println("--------------Buat Pesanan--------------");

        // Menyiapkan variabel untuk dikerjakan nanti
        String restaurantName = "";
        Restaurant resto = null;
        String orderDate = "";
        int totalOrder = 0;
        ArrayList<String> menuStrings = new ArrayList<>();
        ArrayList<Menu> menuList = new ArrayList<>();
        boolean isMenuValid = false;


        while (!isMenuValid) {

            // Meminta input nama restoran
            System.out.print("Nama Restoran: ");
            restaurantName = input.nextLine();

            // Cek apakah nama restoran terdaftar pada sistem
            for (Restaurant restaurant: restoList) {

                // Jika ada nama restoran yang cocok, akan diolah
                if (restaurant.getNama().equalsIgnoreCase(restaurantName)) {
                    resto = restaurant;
                    break;
                }

            }

            // Jika resto masih null berarti restoran tidak ditemukan, maka program akan
            // berhenti dan mengembalikan pesan
            if (resto == null) {
                System.out.println("Restoran tidak terdaftar pada sistem.");
                continue;
            }

            System.out.print("Tanggal Pemesanan (DD/MM/YYYY) : ");
             orderDate= input.nextLine();

            // Melakukan validasi tanggal menggunakan fungsi pada class OrderGenerator TP1
            // Cek karakter slash ('/') pada tanggal
            if (orderDate.charAt(2) != '/' || orderDate.charAt(5) != '/') {
                continue;
            }

            try {
                int tanggal = Integer.parseInt(orderDate.substring(0, 2));
                int bulan = Integer.parseInt(orderDate.substring(3, 5));
                int tahun = Integer.parseInt(orderDate.substring(6));

                if (tanggal > 31 || tanggal < 0 || bulan > 12 || bulan < 0 || tahun < 0) {
                    continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }

            // Meminta input jumlah order
            System.out.print("Jumlah Pesanan: ");

            // Mengecek input jumlah pesanan apakah valid
            try {
                totalOrder = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                // Jika error, program akan melakukan loop lagi
                System.out.println("Jumlah pesanan tidak valid!");
                continue;
            }

            //
            isMenuValid = true;

            // Meminta input makanan
            for (int i = 0; i < totalOrder; i++) {

                // Mengisi makanan dari input
                menuStrings.add(input.nextLine());
            }

            // Melakukan looping untuk memparsing menu dalam bentuk string ke objek menu
            for (String stringMenu: menuStrings) {

                Menu currentMenu = null;

                // Mengecek nama menu pada restoran apakah valid
                for (Menu menu: resto.getMenu()) {
                    if (menu.getNamaMakanan().equalsIgnoreCase(stringMenu.strip())) {
                        currentMenu = menu;
                    }
                }

                if (currentMenu == null) {
                    System.out.println("Mohon memesan menu yang tersedia di Restoran!");
                    isMenuValid = false;
                    break;
                }

                menuList.add(currentMenu);
            }
        }

        // Melakukan generate order Id dan mengambil biaya ongkir
        String orderId = OrderGenerator.generateOrderID(restaurantName, orderDate, userLoggedIn.getNomorTelepon());
        int biayaOngkir = switch (userLoggedIn.getLokasi()) {
            case "P" -> 10000;
            case "U" -> 20000;
            case "T" -> 35000;
            case "S" -> 40000;
            case "B" -> 60000;
            default -> 0;
        };

        // Tambahkan order baru ke orderList
        orderList.add(new Order(orderId, orderDate, biayaOngkir, resto, menuList));

        System.out.printf("Pesanan dengan ID %s diterima!\n", orderId);
    }

    /*
    * Fungsi untuk mencetak bill dari order
    * */
    public static void handleCetakBill(){
        System.out.println("--------------Cetak Bill--------------");

        // Siapkan variabel untuk menyimpan data yang ingin dipakai
        String orderId;
        Order order = null;
        double totalPrice = 0;
        String isFinished = "";
        String lokasiPengiriman;

        // Lakukan looping untuk mengulang kode
        while (true) {

            // Minta input order
            System.out.print("Masukkan Order ID: ");
            orderId = input.nextLine();

            // Cari order Id dari tiap order pada orderList
            for (Order orderTmp: orderList) {
                if (orderTmp.getOrderId().equals(orderId)) {
                    order = orderTmp;
                }
            }

            // Jika order Id tidak ditemukan, maka program akan mengirimkan pesan error
            // dan akan melanjutkan loop
            if (order == null) {
                System.out.println("Order ID tidak dapat ditemukan.");
                continue;
            }

            // Jika berhasil program akan berhenti
            break;
        }

        // Cetak bill dan ambil data
        if (order.isOrderFinished()) {
            isFinished = "Finished";
        } else {
            isFinished = "Not Finished";
        }

        lokasiPengiriman = switch (order.getBiayaOngkosKirim()) {
            case 10000 -> "P";
            case 20000 -> "U";
            case 35000 -> "T";
            case 40000 -> "S";
            case 60000 -> "B";
            default -> "";
        };

        // Tambahkan harga ongkir dengan harga dari makanan
        totalPrice += order.getBiayaOngkosKirim();

        // Siapkan variabel pesanan untuk mendaftarkan nama-nama pesanan yang akan dipesan
        String orders = "";

        // Menambahkan nama-nama orders
        for (Menu menu: order.getItems()) {
            orders += "\n- " + menu.getNamaMakanan() + " " + menu.getHarga();
            totalPrice += menu.getHarga();
        }

        System.out.printf("Bill:\nOrder ID: %s\nTanggal Pemesanan: %s" +
                "\nRestaurant: %s\nLokasi Pengiriman: %s\nStatus Pengiriman: %s" +
                "\nPesanan: %s\nBiaya Ongkos Kirim: Rp %d\nTotal Biaya: Rp %f",
                orderId, order.getTanggalPemesanan(), order.getRestaurant().getNama(),
                lokasiPengiriman, isFinished, orders, order.getBiayaOngkosKirim(), totalPrice);
    }

    /*
    * Fungsi untuk melihat menu
    * */
    public static void handleLihatMenu(){
        System.out.println("--------------Lihat Menu--------------");

        // Menyiapkan data yang ingin diambil
        String restaurantName = "";
        Restaurant resto = null;

        // Melakukan looping untuk mengecek nama restoran
        while (true) {

            // Meminta input dari user
            System.out.print("Nama Restoran: ");
            restaurantName = input.nextLine();

            // Cek apakah nama restoran terdaftar pada sistem
            for (Restaurant restaurant: restoList) {

                // Jika ada nama restoran yang cocok, akan diolah
                if (restaurant.getNama().equalsIgnoreCase(restaurantName)) {
                    resto = restaurant;
                    break;
                }
            }

            // Jika resto masih null berarti restoran tidak ditemukan, maka program akan
            // berhenti dan mengembalikan pesan
            if (resto == null) {
                System.out.println("Restoran tidak terdaftar pada sistem.");
                continue;
            }

            break;
        }

        // Mencetak menu
        String menu = "Menu:";

        // Melakukan looping untuk menambahkan menu
        for (int i = 0; i < resto.getMenu().size(); i++) {
            Menu currentMenu = resto.getMenu().get(i);

            // Mengambil nama makanan dan harga dari menu saat ini
            String namaMakanan = currentMenu.getNamaMakanan();
            double harga = currentMenu.getHarga();

            // Tambahkan nama makanan dan harga pada menu
            menu += String.format("\n%d. %s %d", (i + 1), namaMakanan, harga);
        }

        System.out.println(menu);
    }

    public static void handleUpdateStatusPesanan(){
        System.out.println("--------------Update Status Pesanan--------------");

        // Membuat variabel untuk nantinya diolah
        String orderId;
        Order currentOrder = null;
        boolean status;

        // Membuat looping untuk melakukan validasi
        while (true) {

            // Meminta input order Id
            System.out.print("Masukkan Order ID: ");
            orderId = input.nextLine();

            // Cari order Id dari tiap order pada orderList
            for (Order order: orderList) {
                if (order.getOrderId().equals(orderId)) {
                    currentOrder = order;
                }
            }

            // Jika order Id tidak ditemukan, maka program akan mengirimkan pesan error
            if (currentOrder == null) {
                System.out.println("Order ID tidak dapat ditemukan.");
                continue;
            }

            break;
        }

        // Meminta input status
        System.out.print("Status: ");
        String statusString = input.nextLine();

        if (statusString.equalsIgnoreCase("Selesai")) {
            status = true;
        } else {
            status = false;
        }

        // Jika status order sama dengan yang diberikan user, maka order saat ini diubah
        if (currentOrder.isOrderFinished() == status) {
            System.out.printf("Status pesanan dengan ID %s tidak berhasil diupdate!\n", orderId);
            return;
        }

        // Mengubah status order sesuai dengan yang diberikan user
        currentOrder.setOrderFinished(status);
        System.out.printf("Status pesanan dengan ID %s berhasil diupdate!\n", orderId);
    }

    public static void handleTambahRestoran(){
        System.out.println("--------------Tambah Restoran--------------");
        String restaurantName = "";
        Restaurant newRestaurant = null;
        int totalOrder;
        ArrayList<String> inputMenu = new ArrayList<>();
        ArrayList<Menu> menuList = new ArrayList<>();
        boolean isRestaurantNameValid = false;
        boolean isMenuValid = false;

        while (!isRestaurantNameValid || !isMenuValid) {
            isRestaurantNameValid = true;
            System.out.print("Nama: ");
            restaurantName = input.nextLine();

            // Menyiapkan nama restoran tanpa whitespace
            String restaurantNameNoWhiteSpace = "";

            // Memfilter nama restoran tanpa whitespace (spasi)
            for (int i = 0; i < restaurantName.length(); i++) {
                // Menambahkan karakter nama restoran tanpa spasi
                if (restaurantName.charAt(i) != ' ') {
                    restaurantNameNoWhiteSpace += restaurantName.charAt(i);
                }
            }

            // Cek apakah nama restoran memiliki karakter lebih dari empat
            if (restaurantNameNoWhiteSpace.length() < 4) {
                System.out.println("Nama Restoran tidak valid!\n");
                isRestaurantNameValid = false;
                continue;
            }

            for (Restaurant restaurant: restoList) {

                // Mengecek apakah nama restoran sudah terdaftar
                if (restaurant.getNama().equalsIgnoreCase(restaurantName)) {
                    System.out.printf("Restoran dengan nama %s sudah pernah terdaftar. Mohon masukkan nama yang berbeda!\n\n", restaurantName);
                    isRestaurantNameValid = false;
                }
            }

            // Jika nama restoran tidak valid, program akan mengulang
            if (!isRestaurantNameValid) {
                continue;
            }

            newRestaurant = new Restaurant(restaurantName);

            // Meminta input jumlah makanan
            System.out.print("Jumlah Makanan: ");

            // Mengecek input jumlah makanan apakah valid
            try {
                totalOrder = Integer.parseInt(input.nextLine());
            } catch (Exception e) {
                System.out.println("Jumlah makanan tidak valid!\n");
                continue;
            }

            // Mengasumsikan jika menu sudah valid dari awal
            isMenuValid = true;

            // Meminta input makanan
            for (int i = 0; i < totalOrder; i++) {

                // Mengisi makanan dari input
                inputMenu.add(input.nextLine());
            }

            // Mengecek nama menu,
            for (String stringMenu: inputMenu) {

                // Ambil data dari string menu
                String[] splittedString = stringMenu.split(" ");
                String namaMenu = "";

                for (int j = 0; j < splittedString.length - 1; j++) {
                    namaMenu += splittedString[j];
                }
                int hargaMenu;

                // Cek
                try {
                    hargaMenu = Integer.parseInt(splittedString[splittedString.length - 1]);
                } catch (Exception e) {
                    System.out.println("Harga menu harus bilangan bulat!");
                    isMenuValid = false;
                    break;
                }

                // Jika valid maka akan membuat objek menu dan menambahkannya ke menuList
                Menu newMenu = new Menu(namaMenu, hargaMenu);

                // Mengurutkan menu sesuai nama,
                boolean isAppended = false;

                for (int i = 0; i < menuList.size(); i++) {
                    if (isAppended) {
                        break;
                    }

                    // Bandingkan harga terlebih dahulu
                    if (menuList.get(i).getHarga() > newMenu.getHarga()) {
                        menuList.add(i, newMenu);
                    }

                    // Bandingkan nama
                    String smallerString = "";

                    if (newMenu.getNamaMakanan().length() < menuList.get(i).getNamaMakanan().length()) {
                        smallerString = newMenu.getNamaMakanan();
                    } else {
                        smallerString = menuList.get(i).getNamaMakanan();
                    }

                    for (int j = 0; j < smallerString.length(); j++) {
                        if (newMenu.getNamaMakanan().charAt(j) < menuList.get(i).getNamaMakanan().charAt(j)) {
                            menuList.add(i, newMenu);
                            isAppended = true;
                            break;
                        }
                    }
                }

                // Jika menu list masih kosong, akan menambahkan data pertama
                if (menuList.isEmpty()) {
                    menuList.add(newMenu);
                }
            }
        }

        // Menambahkan menuList ke restoran
        newRestaurant.setMenu(menuList);

        // Menambahkan restoran baru ke restoList
        restoList.add(newRestaurant);

        System.out.printf("Restoran %s berhasil terdaftar.\n", restaurantName);
    }

    public static void handleHapusRestoran(){
        System.out.println("--------------Hapus Restoran--------------");

        // Menyiapkan data untuk diolah nanti
        String restaurantName = "";
        Restaurant resto = null;

        // Melakukan looping untuk meminta nama restoran
        while (true) {
            System.out.print("Nama: ");
            restaurantName = input.nextLine();

            // Cek apakah nama restoran terdaftar pada sistem
            for (Restaurant restaurant: restoList) {

                // Jika ada nama restoran yang cocok, akan diolah
                if (restaurant.getNama().equalsIgnoreCase(restaurantName)) {
                    resto = restaurant;
                    break;
                }
            }

            // Jika resto tidak ditemukan maka loop akan meminta nama lagi
            if (resto == null) {
                System.out.println("Restoran tidak terdaftar pada sistem.");
                continue;
            }

            break;
        }

        // Jika ditemukan program akan menghapus restoran pada restoList
        restoList.remove(resto);

        System.out.println("Restoran berhasil dihapus.");
    }

    public static void initUser(){
        userList = new ArrayList<User>();
        userList.add(new User("Thomas N", "9928765403", "thomas.n@gmail.com", "P", "Customer"));
        userList.add(new User("Sekar Andita", "089877658190", "dita.sekar@gmail.com", "B", "Customer"));
        userList.add(new User("Sofita Yasusa", "084789607222", "sofita.susa@gmail.com", "T", "Customer"));
        userList.add(new User("Dekdepe G", "080811236789", "ddp2.gampang@gmail.com", "S", "Customer"));
        userList.add(new User("Aurora Anum", "087788129043", "a.anum@gmail.com", "U", "Customer"));

        userList.add(new User("Admin", "123456789", "admin@gmail.com", "-", "Admin"));
        userList.add(new User("Admin Baik", "9123912308", "admin.b@gmail.com", "-", "Admin"));
    }

    public static void printHeader(){
        System.out.println("\n>>=======================================<<");
        System.out.println("|| ___                 ___             _ ||");
        System.out.println("||| . \\ ___  ___  ___ | __>___  ___  _| |||");
        System.out.println("||| | |/ ._>| . \\/ ._>| _>/ . \\/ . \\/ . |||");
        System.out.println("|||___/\\___.|  _/\\___.|_| \\___/\\___/\\___|||");
        System.out.println("||          |_|                          ||");
        System.out.println(">>=======================================<<");
    }

    public static void startMenu(){
        System.out.println("Selamat datang di DepeFood!");
        System.out.println("--------------------------------------------");
        System.out.println("Pilih menu:");
        System.out.println("1. Login");
        System.out.println("2. Keluar");
        System.out.println("--------------------------------------------");
        System.out.print("Pilihan menu: ");
    }

    public static void menuAdmin(){
        System.out.println("\n--------------------------------------------");
        System.out.println("Pilih menu:");
        System.out.println("1. Tambah Restoran");
        System.out.println("2. Hapus Restoran");
        System.out.println("3. Keluar");
        System.out.println("--------------------------------------------");
        System.out.print("Pilihan menu: ");
    }

    public static void menuCustomer(){
        System.out.println("\n--------------------------------------------");
        System.out.println("Pilih menu:");
        System.out.println("1. Buat Pesanan");
        System.out.println("2. Cetak Bill");
        System.out.println("3. Lihat Menu");
        System.out.println("4. Update Status Pesanan");
        System.out.println("5. Keluar");
        System.out.println("--------------------------------------------");
        System.out.print("Pilihan menu: ");
    }
}
