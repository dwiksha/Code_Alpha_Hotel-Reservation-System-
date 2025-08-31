
import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category; // Standard, Deluxe, Suite
    boolean available;

    Room(int number, String category) {
        this.roomNumber = number;
        this.category = category;
        this.available = true;
    }
}

// --- Reservation class ---
class Reservation {
    int bookingId;
    String guestName;
    Room room;
    double amountPaid;

    Reservation(int id, String name, Room room, double amount) {
        this.bookingId = id;
        this.guestName = name;
        this.room = room;
        this.amountPaid = amount;
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId +
               ", Guest: " + guestName +
               ", Room: " + room.roomNumber +
               " (" + room.category + ")" +
               ", Paid: $" + amountPaid;
    }
}

// --- Hotel class ---
class Hotel {
    List<Room> rooms = new ArrayList<>();
    List<Reservation> reservations = new ArrayList<>();
    int nextBookingId = 1;

    Hotel() {
        // sample rooms
        rooms.add(new Room(101, "Standard"));
        rooms.add(new Room(102, "Standard"));
        rooms.add(new Room(201, "Deluxe"));
        rooms.add(new Room(202, "Deluxe"));
        rooms.add(new Room(301, "Suite"));
    }

    void searchRooms(String category) {
        System.out.println("Available " + category + " rooms:");
        for (Room r : rooms) {
            if (r.available && r.category.equalsIgnoreCase(category)) {
                System.out.println("Room " + r.roomNumber);
            }
        }
    }

    Reservation bookRoom(String guestName, String category) {
        for (Room r : rooms) {
            if (r.available && r.category.equalsIgnoreCase(category)) {
                r.available = false;
                double price = switch (category) {
                    case "Deluxe" -> 150;
                    case "Suite" -> 250;
                    default -> 100;
                };
                Reservation res = new Reservation(nextBookingId++, guestName, r, price);
                reservations.add(res);
                System.out.println("Payment successful: $" + price);
                return res;
            }
        }
        System.out.println("No " + category + " rooms available!");
        return null;
    }

    void cancelReservation(int bookingId) {
        for (Reservation res : reservations) {
            if (res.bookingId == bookingId) {
                res.room.available = true;
                reservations.remove(res);
                System.out.println("Reservation " + bookingId + " cancelled.");
                return;
            }
        }
        System.out.println("Booking ID not found!");
    }

    void viewReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
        } else {
            for (Reservation res : reservations) {
                System.out.println(res);
            }
        }
    }

    // --- File I/O persistence ---
    void saveToFile(String fileName) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            for (Reservation res : reservations) {
                out.println(res.bookingId + "," + res.guestName + "," + res.room.roomNumber +
                        "," + res.room.category + "," + res.amountPaid);
            }
        }
        System.out.println("Reservations saved to " + fileName);
    }

    void loadFromFile(String fileName) throws IOException {
        reservations.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                int roomNum = Integer.parseInt(data[2]);
                String category= data[3];
                double amount = Double.parseDouble(data[4]);
                Room r = findRoomByNumber(roomNum);
                if (r != null) r.available = false;
                reservations.add(new Reservation(id, name, r, amount));
                nextBookingId = Math.max(nextBookingId, id + 1);
            }
        }
        System.out.println("Reservations loaded from " + fileName);
    }

    private Room findRoomByNumber(int num) {
        for (Room r : rooms) if (r.roomNumber == num) return r;
        return null;
    }
}

// --- Main System ---
public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();

        while (true) {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. Search Rooms  2. Book Room  3. Cancel Reservation");
            System.out.println("4. View Reservations  5. Save  6. Load  0. Exit");
            System.out.print("Choose: ");
            int ch = sc.nextInt();
            sc.nextLine(); // consume newline

            try {
                switch (ch) {
                    case 1 -> {
                        System.out.print("Enter category (Standard/Deluxe/Suite): ");
                        hotel.searchRooms(sc.nextLine());
                    }
                    case 2 -> {
                        System.out.print("Enter your name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter category: ");
                        hotel.bookRoom(name, sc.nextLine());
                    }
                    case 3 -> {
                        System.out.print("Enter Booking ID to cancel: ");
                        hotel.cancelReservation(sc.nextInt());
                    }
                    case 4 -> hotel.viewReservations();
                    case 5 -> hotel.saveToFile("reservations.txt");
                    case 6 -> hotel.loadFromFile("reservations.txt");
                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
