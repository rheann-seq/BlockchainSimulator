package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/*
 * @author Rheann Sequeira (rsequeir@andrew.cmu.edu)
 * */
public class Block {

  private int index; // postion of the block on the chain

  @SerializedName("time stamp")
  private Timestamp timestamp; // the time of the block's creation

  @SerializedName("Tx")
  private String data; // single transaction details

  @SerializedName("PrevHash")
  private String previousHash; // the SHA256 hash of a block's parent

  private BigInteger nonce; // number of times hash calculated until correct no. of 0s found
  private int difficulty;

  /**
   * Constructs a Block object with the given parameters.
   *
   * @param index      The position of the block on the chain
   * @param timestamp  The time of the block's creation
   * @param data       Single transaction details
   * @param nonce      Number of times hash calculated until correct no. of 0s found
   * @param difficulty The leading number of 0s required for the hash
   */
  public Block(int index, Timestamp timestamp, String data, BigInteger nonce, int difficulty) {
    this.index = index;
    this.timestamp = timestamp;
    this.data = data;
    this.nonce = nonce;
    this.difficulty = difficulty;
  }

  //Getters and Setters
  public int getIndex() {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public BigInteger getNonce() {
    return this.nonce;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getPreviousHash() {
    return this.previousHash;
  }

  public void setPreviousHash(String previousHash) {
    this.previousHash = previousHash;
  }

  public int getDifficulty() {
    return this.difficulty;
  }

  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

  /*
   * this methods calculates the hashe using the provided params
   * @param index -  index of the block
   * @param timestamp - time at which the block was created
   * @param data - single transaction details
   * @param previousHash - hash of the previous node
   * @param nonce - nonce for this block
   * @param difficulty - leading number of 0s required
   * @return - Hash of the params in String format
   * */
  public String calculateHash(
      int index,
      Timestamp timestamp,
      String data,
      String previousHash,
      BigInteger nonce,
      int difficulty) {
    String hash = "";
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      String strToHash =
          String.join(
              ",",
              Integer.toString(index),
              timestamp.toString(),
              data,
              previousHash,
              nonce.toString(),
              Integer.toString(difficulty));

      md.update(strToHash.getBytes());
      hash = HashHelper.bytesToHex(md.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    return hash;
  }

  /**
   * Performs a proof of work to find a hash that starts with a specified number of zeros.
   *
   * @return The resulting hash
   */
  public String proofOfWork() {

    String hash =
        calculateHash(
            this.index, this.timestamp, this.data, this.previousHash, this.nonce, this.difficulty);

    String zeroes = startingZeroes(difficulty);

    while (!hash.startsWith(zeroes)) {
      this.nonce = this.nonce.add(BigInteger.ONE);
      hash =
          calculateHash(
              this.index,
              this.timestamp,
              this.data,
              this.previousHash,
              this.nonce,
              this.difficulty);
    }
    return hash;
  }

  private String startingZeroes(int difficulty) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < difficulty; i++) {
      sb.append("0");
    }
    return sb.toString();
  }

  /**
   * Returns a String representation of the block in JSON format.
   *
   * @return The block as a JSON string
   */
  @Override
  public String toString() {
    Gson gson = new Gson();
    String blockAsJson = gson.toJson(this);
    return blockAsJson;
  }
}
