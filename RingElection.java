import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class RingElection {
    private int numProcesses;
    private int coordinator;
    private boolean[] activeProcesses;

    public RingElection(int numProcesses, int initialCoordinator) {
        this.numProcesses = numProcesses;
        this.activeProcesses = new boolean[numProcesses];

        for (int i = 0; i < numProcesses; i++) {
            activeProcesses[i] = true;
        }

        if (initialCoordinator >= 0 && initialCoordinator < numProcesses) {
            coordinator = initialCoordinator;
            System.out.println("Initial Coordinator: Process " + coordinator);
            activeProcesses[coordinator] = false;
            System.out.println("Process " + coordinator + " has crashed (Initial Coordinator).");
        } else {
            System.out.println("Invalid coordinator ID, defaulting to Process " + (numProcesses - 1));
            coordinator = numProcesses - 1;
        }
    }

    public void startElection(int initiator) {
        System.out.println("\n✅ Process " + initiator + " is initiating an election...");

        List<Integer> electionPath = new ArrayList<>();
        electionPath.add(initiator);
        System.out.println("Election path: " + electionPath);

        int maxId = initiator;
        int current = (initiator + 1) % numProcesses;

        while (current != initiator) {
            if (activeProcesses[current]) {
                System.out.println("Process " + maxId + " -> Process " + current + " (ELECTION)");
                electionPath.add(current);
                System.out.println("Election path: " + electionPath);

                if (current > maxId) {
                    maxId = current;
                }
            } else {
                System.out.println("Process " + current + " is skipped (CRASHED).");
            }

            current = (current + 1) % numProcesses;
        }

        coordinator = maxId;
        System.out.println("\n🏆 Process " + coordinator + " wins the election and becomes the new coordinator.");
        announceNewCoordinator();
    }

    private void announceNewCoordinator() {
        int current = (coordinator + 1) % numProcesses;

        while (current != coordinator) {
            if (activeProcesses[current]) {
                System.out.println("Process " + coordinator + " -> Process " + current + " (ELECTED)");
            }
            current = (current + 1) % numProcesses;
        }
    }

    public boolean isActive(int processId) {
        return processId >= 0 && processId < numProcesses && activeProcesses[processId];
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        System.out.print("Enter the initial coordinator process (0 to " + (numProcesses - 1) + "): ");
        int initialCoordinator = scanner.nextInt();

        RingElection ringElection = new RingElection(numProcesses, initialCoordinator);

        int initiator;
        while (true) {
            System.out.print("\nEnter the process to start the election: ");
            initiator = scanner.nextInt();

            if (!ringElection.isActive(initiator)) {
                System.out.println("⚠️ Process " + initiator + " is crashed and cannot initiate the election.");
            } else {
                break;
            }
        }

        ringElection.startElection(initiator);
        scanner.close();
    }
}
