package ru.spbau.mit.tukh.serverArchitectures.client;

/**
 * Class to run clients during the testing.
 */
public class ClientTestingSession {
    private static final String HELP_STRING = "Usage: <number of clients> <number of requests> <number of elements> " +
            "<time delta> <ip> <port> <testing metrics> <metrics step> <upper bound of testing metrics>";

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 6) {
            System.out.println("Incorrect number of arguments");
            System.out.println(HELP_STRING);
        }

        int numberOfClients = Integer.parseInt(args[0]);
        int numberOfRequests = Integer.parseInt(args[1]);
        int numberOfElements = Integer.parseInt(args[2]);
        int timeDelta = Integer.parseInt(args[3]);
        String ip = args[4];
        int port = Integer.parseInt(args[5]);
        String testingMetrics = args[6];
        int metricsStep = Integer.parseInt(args[7]);
        int upperBound = Integer.parseInt(args[8]);


        while (true) {
            Thread[] clientThreads = new Thread[numberOfClients];

            for (int i = 0; i < numberOfClients; i++) {
                int finalNumberOfElements = numberOfElements;
                int finalTimeDelta = timeDelta;

                clientThreads[i] = new Thread(() -> {
                    Client client = new Client(ip, numberOfRequests, finalNumberOfElements, finalTimeDelta, port);
                    client.execute();
                });
                clientThreads[i].start();
            }

            for (Thread clientThread: clientThreads) {
                clientThread.join();
            }

            if (testingMetrics.equals("elements")) {
                numberOfElements += metricsStep;
                if (numberOfElements > upperBound) {
                    break;
                }
            } else if (testingMetrics.equals("clients")) {
                numberOfClients += metricsStep;
                if (numberOfClients > upperBound) {
                    break;
                }
            } else if (testingMetrics.equals("delta")) {
                timeDelta += metricsStep;
                if (timeDelta > upperBound) {
                    break;
                }
            }

        }
    }
}
