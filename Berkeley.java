Server.java

import java.io.*;
import java.net.*;
import java.util.*;

public class BerkeleyServer {
    private static final int PORT = 5000; // Port to listen on
    private static final List<Socket> clients = new ArrayList<>();
    private static final List<Integer> clientTimes = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Time Daemon Server Started. Waiting for clients...");

            while (clients.size() < 3) { // Accept 3 clients for example
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("Client " + clients.size() + " connected.");
                
                // Get time from client
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                int timeInMinutes = Integer.parseInt(reader.readLine());
                clientTimes.add(timeInMinutes);
            }

            // Time Daemon Initial Time (03:00 in minutes)
            int daemonTime = (3 * 60) + 0; // 3:00 AM converted to minutes
            System.out.println("Time Daemon Initial Clock: 03:00");

            // Compute the average time
            int sum = daemonTime;
            for (int time : clientTimes) {
                sum += time;
            }
            int avgTime = sum / (clients.size() + 1); // Including daemon

            int avgHour = (avgTime / 60) % 24;
            int avgMinute = avgTime % 60;

            System.out.println("Calculated Average Time: " + String.format("%02d:%02d", avgHour, avgMinute));

            // Send new time to clients
            for (Socket client : clients) {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(avgTime);
            }

            // Close all client sockets
            for (Socket client : clients) {
                client.close();
            }
            System.out.println("Time Synchronization Completed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


Client.java

import java.io.*;
import java.net.*;
import java.util.Random;

public class BerkeleyClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            // Simulated Client Time (Randomized between 2:45 - 3:30)
            Random random = new Random();
            int clientHour = 2 + random.nextInt(2); // Either 2 or 3
            int clientMinute = 45 + random.nextInt(30); // Between 45 and 75

            if (clientMinute >= 60) {
                clientHour += 1;
                clientMinute -= 60;
            }

            int clientTimeInMinutes = (clientHour * 60) + clientMinute;
            System.out.println("Client Initial Clock: " + String.format("%02d:%02d", clientHour, clientMinute));

            // Send current time to server
            writer.println(clientTimeInMinutes);

            // Receive adjusted time from server
            int newTimeInMinutes = Integer.parseInt(reader.readLine());
            int newHour = (newTimeInMinutes / 60) % 24;
            int newMinute = newTimeInMinutes % 60;

            System.out.println("Adjusted Clock: " + String.format("%02d:%02d", newHour, newMinute));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
