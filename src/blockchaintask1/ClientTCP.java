package blockchaintask1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/*
 * @author Rheann Sequeira (rsequeir@andrew.cmu.edu)
 * */
public class ClientTCP {
    static Socket clientSocket = null;
    static int serverPort = 7777;

    static PrintWriter out = null;
    static BufferedReader in = null;

    public static void main(String[] args) throws IOException {
        // arguments supply hostname
        Socket clientSocket = null;
        Scanner sc = new Scanner(System.in);

        try {
//            connectToServer();

            clientSocket = new Socket("localhost", serverPort);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            String m;
            /*while ((m = typed.readLine()) != null) {
                out.println(m);
                out.flush();
                String data = in.readLine(); // read a line of data from the stream
                System.out.println("Received: " + data);
            }*/
            int selectedOption = -1;

            Object object;
//            Scanner sc = new Scanner(System.in);
            String request;
            String response;
            JsonObject jsonReply;

            while (selectedOption != 6) {
                displayMenu();
                // take the option selected from the user
                selectedOption = sc.nextInt();
//                performOperation(selectedOption);

                switch (selectedOption) {
                    case 0: // View basic blockchain status
                        //no input required
                        request = new RequestMessage(0, "", "").toString();
                        response = sendToServer(request);

                        object = new JsonParser().parse(response);
                        jsonReply = (JsonObject) object;

                        System.out.println("Current Size of chain: " + jsonReply.get("size").toString());
                        System.out.println("Difficulty of most recent block: " + jsonReply.get("difficulty").toString());
                        System.out.println("Total difficulty for all blocks: " + jsonReply.get("totalDifficulty").toString());
                        System.out.println("Experimented with 2,000,000 hashes.");
                        System.out.println(
                                "Approximate hashes per second on this machine: " + jsonReply.get("hashesPerSecond"));
                        System.out.println(
                                "Approximate total hashes required for the whole chain: "
                                        + jsonReply.get("totalExpectedHashes"));
                        System.out.println("Nonce for the most recent block: " + jsonReply.get("nonce").toString());
                        System.out.println("Chain hash: " + jsonReply.get("hash").toString() + "\n");

                        break;
                    case 1: // Add a transaction to the blockchain
                        //input difficulty and transaction
                        System.out.println("Enter a difficulty > 1");
                        int difficulty = sc.nextInt();
                        System.out.println("Enter transaction");
                        // Consumes the newline character
                        sc.nextLine();
                        String tx = sc.nextLine();
                        //create the RequestMessage json
                        request = new RequestMessage(1, String.valueOf(difficulty), tx).toString();
                        response = sendToServer(request);
                        object = new JsonParser().parse(response);
                        jsonReply = (JsonObject) object;

                        System.out.println("Total execution time to add this block was " + jsonReply.get("elapsedTime") + " milliseconds");
                        System.out.println();

                        break;
                    case 2: // Verify the blockchain
                        System.out.println("Verifying entire chain");
                        //create the RequestMessage json
                        request = new RequestMessage(2, "", "").toString();

                        response = sendToServer(request);
                        object = new JsonParser().parse(response);
                        jsonReply = (JsonObject) object;
                        System.out.println("Chain verification: "+jsonReply.get("verification").toString());
                        System.out.println("Total execution time required to verify the chain was " + jsonReply.get("elapsedTime").toString() + " milliseconds\n");

                        break;
                    case 3: // View the blockchain
                        //no input required
                        System.out.println("View the blockchain");
                        //create the RequestMessage json
                        request = new RequestMessage(3, "", "").toString();
                        response = sendToServer(request);
                        object = new JsonParser().parse(response);
                        jsonReply = (JsonObject) object;
                        System.out.println(jsonReply.get("wholeChain").toString().replace("\\", ""));
                        break;
                    case 4: // Corrupt the chain
                        //input block id and new data
                        System.out.println("Corrupt the Blockchain");
                        System.out.println("Enter block ID of block to corrupt");
                        int id = sc.nextInt();
                        // Consumes the newline character
                        sc.nextLine();
                        System.out.println("Enter new data for block " + id);
                        String newData = sc.nextLine();
                        //create the RequestMessage json
                        request = new RequestMessage(4, String.valueOf(id), newData).toString();
                        response = sendToServer(request);
                        object = new JsonParser().parse(response);
                        jsonReply = (JsonObject) object;
                        System.out.println("Block " + jsonReply.get("index").toString() + " now contains "
                                + jsonReply.get("tx").toString().replace("\\", ""));
                        break;
                    case 5: // Hide the corruption by repairing the chain
                        //create the RequestMessage json
                        request = new RequestMessage(5, "", "").toString();
                        response = sendToServer(request);
                        object = new JsonParser().parse(response);
                        jsonReply = (JsonObject) object;
                        System.out.println(
                                "Total execution time required to repair the chain was "
                                        + jsonReply.get("elapsedTime").toString()
                                        + " milliseconds");
                        break;
                    case 6: // exit
                        System.out.println("Client is shutting down");
                        System.exit(0);
                }

            }
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }

    private static void performOperation(int selectedOption) throws IOException {
        Object object;
        Scanner sc = new Scanner(System.in);
        String request;
        String response;
        JsonObject jsonReply;
        switch (selectedOption) {
            case 0: // View basic blockchain status
                //no input required
                request = new RequestMessage(1, "", "").toString();
//                response = sendToServer(request);


                out.println(request);
                response = in.readLine();
                out.flush();


                object = new JsonParser().parse(response);
                jsonReply = (JsonObject) object;

                System.out.println("Current Size of chain: " + jsonReply.get("size").toString());
                System.out.println("Difficulty of most recent block: " + jsonReply.get("difficulty").toString());
                System.out.println("Total difficulty for all blocks: " + jsonReply.get("totalDifficulty").toString());
                System.out.println("Experimented with 2,000,000 hashes.");
                System.out.println(
                        "Approximate hashes per second on this machine: " + jsonReply.get("hashesPerSec"));
                System.out.println(
                        "Approximate total hashes required for the whole chain: "
                                + jsonReply.get("totalExpectedHashes"));
                System.out.println("Nonce for the most recent block: " + jsonReply.get("nonce").toString());
                System.out.println("Chain hash: " + jsonReply.get("hash").toString() + "\n");

                break;
            case 1: // Add a transaction to the blockchain
                //input difficulty and transaction
                System.out.println("Enter a difficulty > 1");
                int difficulty = sc.nextInt();
                System.out.println("Enter transaction");
                // Consumes the newline character
                sc.nextLine();
                String tx = sc.nextLine();
                //create the RequestMessage json
                request = new RequestMessage(1, String.valueOf(difficulty), tx).toString();
                response = sendToServer(request);
                object = new JsonParser().parse(response);
                jsonReply = (JsonObject) object;

                System.out.println("Total execution time to add this block was " + jsonReply.get("elapsedTime") + " milliseconds");
                System.out.println();

                break;
            case 2: // Verify the blockchain
                System.out.println("Verifying entire chain");
                //create the RequestMessage json
                request = new RequestMessage(2, "", "").toString();

                response = sendToServer(request);
                object = new JsonParser().parse(response);
                jsonReply = (JsonObject) object;
                System.out.println(jsonReply.get("verification").toString());
                System.out.println("Total execution time required to verify the chain was " + jsonReply.get("elapsedTime").toString() + " milliseconds\n");

                break;
            case 3: // View the blockchain
                //no input required
                System.out.println("View the blockchain");
                //create the RequestMessage json
                request = new RequestMessage(3, "", "").toString();
                response = sendToServer(request);
                object = new JsonParser().parse(response);
                jsonReply = (JsonObject) object;
                System.out.println(jsonReply.get("wholeChain").toString().replace("\\", ""));
                break;
            case 4: // Corrupt the chain
                //input block id and new data
                System.out.println("Corrupt the Blockchain");
                System.out.println("Enter block ID of block to corrupt");
                int id = sc.nextInt();
                // Consumes the newline character
                sc.nextLine();
                System.out.println("Enter new data for block " + id);
                String newData = sc.nextLine();
                //create the RequestMessage json
                request = new RequestMessage(4, String.valueOf(id), newData).toString();
                response = sendToServer(request);
                object = new JsonParser().parse(response);
                jsonReply = (JsonObject) object;
                System.out.println("Block " + jsonReply.get("index").toString() + " now contains "
                        + jsonReply.get("tx").toString().replace("\\", ""));
                break;
            case 5: // Hide the corruption by repairing the chain
                //create the RequestMessage json
                request = new RequestMessage(4, "", "").toString();
                response = sendToServer(request);
                object = new JsonParser().parse(response);
                jsonReply = (JsonObject) object;
                System.out.println(
                        "Total execution time required to repair the chain was "
                                + jsonReply.get("elapsedTime").toString()
                                + " milliseconds");
                break;
            case 6: // exit
                System.out.println("Client is shutting down");
                System.exit(0);
        }

    }

    private static String sendToServer(String request) {
        try {

            out.println(request);
            out.flush();
            String response = in.readLine();

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void displayMenu() {

        String menu =
                "\nBlock Chain Menu\n"
                        + "0. View basic blockchain status.\n"
                        + "1. Add a transaction to the blockchain.\n"
                        + "2. Verify the blockchain.\n"
                        + "3. View the blockchain.\n"
                        + "4. Corrupt the chain.\n"
                        + "5. Hide the corruption by repairing the chain.\n"
                        + "6. Exit.";
        System.out.println(menu);
    }
}
