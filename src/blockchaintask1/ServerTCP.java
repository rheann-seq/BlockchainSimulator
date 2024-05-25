package blockchaintask1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Scanner;

/*
 * @author Rheann Sequeira (rsequeir@andrew.cmu.edu)
 * */
public class ServerTCP {


    public static void main(String[] args) {
        Socket clientSocket = null;

        BlockChain blockChain = new BlockChain();
        blockChain.computeHashesPerSecond();
//        System.out.println(Timestamp.from(Instant.now()));
        Block genesis = new Block(0, Timestamp.from(Instant.now()), "Genesis", BigInteger.ZERO, 2);
        genesis.setPreviousHash("");
        genesis.proofOfWork();
        blockChain.addBlock(genesis);

        try {
            int serverPort = 7777; // the server port we are using

            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */

            System.out.println("---The server is running.---");
            // If we get here, then we are now connected to a client.
            clientSocket = listenSocket.accept();

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */
            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            while (true) {

                //initialize variables here to include all calls to them in scope
                String tx = null;
                int difficulty = 0;
                int index = 0;

                String data = in.nextLine();

                //print request on the console
                System.out.println("Request: "+data);

                //Get JSON OBject form input stream
                Object object = new JsonParser().parse(data);
                JsonObject jsonObject = (JsonObject) object;

                System.out.println("We have a visitor");

                //Parse operation and other information from Json Object
                int operation = Integer.parseInt(jsonObject.get("operation").toString());

                //switch case based on operation number
                switch (operation) {

                    //request block statistics
                    case 0: {

                        System.out.println("Operation Requested: 0" + "\n");

                        //Add all requested information to a JsonObject
                        int size = blockChain.getChainSize();
                        int difficultyint = blockChain.getLatestBlock().getDifficulty();
                        double totalDifficulty = blockChain.getTotalDifficulty();
                        int hashesPerSecond = blockChain.getHashesPerSec();
                        double totalExpectedHash = blockChain.getTotalExpectedHashes();
                        BigInteger nonce = blockChain.getLatestBlock().getNonce();
                        String hash = blockChain.getChainHash();
                        ResponseMessage jsonReply = new ResponseMessage(0, size, difficultyint, totalDifficulty, hashesPerSecond, totalExpectedHash, nonce, hash);

                        //Print response
                        System.out.println("Response: " + jsonReply.getJsonResponse().toString());

                        //Send response
                        out.println(jsonReply.getJsonResponse());
                        out.flush();

                        //Break and wait for future communcation
                        break;
                    }

                    //Add a block
                    case 1: {

                        System.out.println("Operation Requested: 1" + "\n");
                        System.out.println("Adding a block");
//                        Timestamp t1 = blockChain.getTime();
                        long time1 = System.currentTimeMillis();

                        //fetch difficulty and data from request
                        difficulty = Integer.parseInt(jsonObject.get("input1").toString().replace("\"", ""));
                        tx = jsonObject.get("input2").toString();

                        //Create new block and set appropriate properties / complete proof of work
                        Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(), tx, BigInteger.ZERO, difficulty);
                        newBlock.setPreviousHash(blockChain.getChainHash());
                        newBlock.proofOfWork();

                        //add block to blockchain
                        blockChain.addBlock(newBlock);


                        //Compute elapsed time
                        long time2 = System.currentTimeMillis();
//                        Timestamp t2 = blockChain.getTime();
//                        double elapsedTime = t2.getTime() - t1.getTime();
                        double elapsedTime = time2-time1;
                        System.out.println("Total execution time was " + elapsedTime + " milliseconds\n");

                        //
                        ResponseMessage jsonReply = new ResponseMessage(1, elapsedTime);

                        //Send reply
                        System.out.println("Response: " + jsonReply.getJsonResponse().toString());
                        out.println(jsonReply.getJsonResponse());
                        out.flush();
                    }
                    break;

                    //Verify blockchain
                    case 2: {

                        System.out.println("Operation Requested: 2" + "\n");
                        System.out.println("Verifying entire chain");
                        long time1 = System.currentTimeMillis();

                        //Verify chain
                        String verification = blockChain.isChainValid();

                        long time2 = System.currentTimeMillis();
                        double elapsedTime = time2-time1;
                        System.out.println("Total execution time was " + elapsedTime + " milliseconds\n");

                        ResponseMessage jsonReply = new ResponseMessage(2, elapsedTime, verification);

                        System.out.println("Response: " + jsonReply.getJsonResponse().toString());
                        out.println(jsonReply.getJsonResponse());
                        out.flush();
                    }
                    break;

                    //view the blockchain
                    case 3: {
                        //View chain
                        System.out.println("Operation Requested: 3" + "\n");
                        System.out.println("View the blockchain");
                        String wholeChain = blockChain.toString();

                        ResponseMessage jsonReply = new ResponseMessage(3, wholeChain);

                        System.out.println("Response: " + jsonReply.getJsonResponse().toString());

                        out.println(jsonReply.getJsonResponse());
                        out.flush();
                    }
                    break;

                    //corrupt data
                    case 4: {

                        //Corrupt chain
                        System.out.println("Operation Requested: 4" + "\n");
                        System.out.println("Corrupt the blockchain");

                        index = Integer.parseInt(jsonObject.get("input1").toString().replace("\"", ""));
                        tx = jsonObject.get("input2").toString();

                        blockChain.getBlock(index).setData(tx);

                        System.out.println("Block " + index + " now contains " + tx);

                        ResponseMessage jsonReply = new ResponseMessage(4, index, tx);

                        System.out.println("Response: " + jsonReply.getJsonResponse().toString());
                        out.println(jsonReply.getJsonResponse());
                        out.flush();
                    }
                    break;

                    //Fix the chain
                    case 5: {

                        //Fix chain
                        System.out.println("Operation Requested: 5" + "\n");
                        System.out.println("Repairing chain");
                        long time1 = System.currentTimeMillis();

                        blockChain.repairChain();

                        long time2 = System.currentTimeMillis();
                        double elapsedTime = time2-time1;

                        ResponseMessage jsonReply = new ResponseMessage(5, elapsedTime);

                        System.out.println("Response: " + jsonReply.getJsonResponse().toString());
                        out.println(jsonReply.getJsonResponse());
                        out.flush();
                    }
                    break;

                    //Exit
                    case 6:
                        System.out.println("Operation Requested: 6" + "\n");
                        System.out.println("Exiting.\n");

                        break;
                    default:
                        System.out.println("Incorrect submission.");
                        break;
                }

                //Make sure output is flushed
                out.flush();

            }


        } catch (IOException e) {
            // Handle exceptions
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
}

