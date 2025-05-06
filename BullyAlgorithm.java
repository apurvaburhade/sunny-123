import java.util.Scanner;

class BullyAlgorithm {
    private int numProcesses;          // Total number of processes
    private int coordinator;           // The current coordinator
    private boolean[] activeProcesses; // Tracks active processes

    
    // Constructor to initialize processes and set the coordinator
    public BullyAlgorithm(int numProcesses, int initialCoordinator) {
        this.numProcesses = numProcesses;
        this.activeProcesses = new boolean[numProcesses];

        // All processes are initially active
        for (int i = 0; i < numProcesses; i++) {
            activeProcesses[i] = true;
        }

        // Set the initial coordinator provided by the user
        if (initialCoordinator >= 0 && initialCoordinator < numProcesses) {
            coordinator = initialCoordinator;
            System.out.println("Initial Coordinator: Process " + coordinator);
        } else {
            System.out.println("Invalid process ID for coordinator. Defaulting to last process as coordinator.");
            coordinator = numProcesses - 1;  // Default to last process if invalid ID
        }
    }

    // Start the election from a given process
    public void startElection(int initiator) {
        System.out.println("\nProcess " + initiator + " is starting an election...");

        // Step 1: Send ELECTION messages to higher-numbered processes
        for (int i = initiator + 1; i < numProcesses; i++) {
            if (activeProcesses[i]) {
                System.out.println("Process " + initiator + " -> Process " + i + " (ELECTION)");
            }
        }

        // Step 2: Simulate OK responses
        simulateElectionResponses(initiator);

        // Step 3: Determine the new coordinator
        determineCoordinator(initiator);
    }

    // Simulate OK responses from higher processes
    private void simulateElectionResponses(int initiator) {
        System.out.println("\nWaiting for OK responses...");
        for (int i = initiator + 1; i < numProcesses; i++) {
            if (i == numProcesses - 1) {
                System.out.println("Process " + i + " has crashed and does not respond.");
            } else {
                System.out.println("Process " + i + " -> Process " + initiator + " (OK)");
            }
        }
    }

    // Determine the new coordinator after responses
    private void determineCoordinator(int initiator) {
        boolean newCoordinatorFound = false;

        // Check if any higher process takes over the election
        for (int i = initiator + 1; i < numProcesses; i++) {
            if (i == numProcesses - 1) {
                continue; // Skip the crashed process
            }
            if (i > initiator) {
                System.out.println("Process " + i + " takes over the election.");
                startElection(i);  // Higher process takes over
                newCoordinatorFound = true;
                return;
            }
        }

        // If no higher process takes over, the initiator wins the election
        if (!newCoordinatorFound) {
            coordinator = initiator;
            System.out.println("Process " + coordinator + " wins the election and becomes the new coordinator.");
            announceNewCoordinator();
        }
    }

    // Announce the new coordinator to all other active processes
    private void announceNewCoordinator() {
        for (int i = 0; i < numProcesses; i++) {
            if (i != coordinator && activeProcesses[i]) {
                System.out.println("Process " + coordinator + " -> Process " + i + " (COORDINATOR)");
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        System.out.print("Enter the initial coordinator process ID: ");
        int initialCoordinator = scanner.nextInt();

        BullyAlgorithm bully = new BullyAlgorithm(numProcesses, initialCoordinator);

        System.out.print("Enter the process to start the election: ");
        int initiator = scanner.nextInt();

        if (initiator >= 0 && initiator < numProcesses) {
            bully.startElection(initiator);
        } else {
            System.out.println("Invalid process ID.");
        }

        scanner.close();
    }
}

