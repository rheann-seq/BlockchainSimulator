package blockchaintask1;

/*
 * @author Rheann Sequeira (rsequeir@andrew.cmu.edu)
 * */

import com.google.gson.JsonObject;

import java.math.BigInteger;

public class ResponseMessage {

    private JsonObject jsonResponse;

    //Basic constructor for simple operation replies
    public ResponseMessage(int operation) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
    }

    //Constructor for case 1 & 5 - Add a transaction to the blockchain and Hide the corruption by repairing the chain
    public ResponseMessage(int operation, double timeTaken) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("elapsedTime", timeTaken);
    }

    //Constructor for case 4 - Corrupt the chain
    public ResponseMessage(int operation, int index, String data) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("index", index);
        jsonResponse.addProperty("tx", data);
    }

    //Constructor for case 3 (View chain)
    public ResponseMessage(int operation, String wholechain) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("wholeChain", wholechain);
    }

    //Constructor for case 2
    public ResponseMessage(int operation, double elapsedTime, String verification) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("elapsedTime", elapsedTime);
        jsonResponse.addProperty("verification", verification);
    }

    //Constructor for case 0 - View basic blockchain status
    public ResponseMessage(int operation, int size, int difficulty, double totalDifficulty, int hashesPerSecond, double totalExpectedHashes, BigInteger nonce, String hash) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        //Add all requested information to a JsonObject
        jsonResponse.addProperty("size", size);
        jsonResponse.addProperty("difficulty", difficulty);
        jsonResponse.addProperty("totalDifficulty", totalDifficulty);
        jsonResponse.addProperty("hashesPerSecond", hashesPerSecond);
        jsonResponse.addProperty("totalExpectedHashes", totalExpectedHashes);
        jsonResponse.addProperty("nonce", nonce);
        jsonResponse.addProperty("hash", hash);
    }

    public JsonObject getJsonResponse() {
        return jsonResponse;
    }


}