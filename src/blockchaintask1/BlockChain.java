package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * @author Rheann Sequeira (rsequeir@andrew.cmu.edu)
 * */

/*
 * Represents the entire blockchain
 * */
public class BlockChain {
  @SerializedName("ds_chain")
  List<Block> chain;

  String chainHash;
  private int hashesPerSec;

  public BlockChain() {
    this.chain = new ArrayList<>();
    this.chainHash = "";
    this.hashesPerSec = 0;
  }

  /*
   * describe how this system behaves as the difficulty level increases.
   * Run some experiments by adding new blocks with increasing difficulties.
   * Describe what you find. Be specific and quote some times.
   * You need not employ a system clock. You should be able to make clear statements describing the approximate run times associated with addBlock(), isChainValid(), and chainRepair()
   * */


  public String getChainHash() {
    return this.chainHash;
  }

  public java.sql.Timestamp getTime() {
    return new Timestamp(System.currentTimeMillis());
  }

  /*
   * @return - reference to latest block in the chain
   * */
  public Block getLatestBlock() {
    return chain.get(chain.size() - 1);
  }

  /*
   * @return - the size of the blockchain
   * */
  public int getChainSize() {
    return chain.size();
  }

  /*
   * computes exactly 2 million hashes and times how long that process takes
   * */
  public void computeHashesPerSecond() {
    String strToHash = "00000000";
    int numHashes = 2000000;

    long startTime = System.currentTimeMillis();
    // logic to compute 2million hashes
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      for (int i = 0; i < numHashes; i++) {
        byte[] hash = md.digest(strToHash.getBytes());
        String convertedToHex = HashHelper.bytesToHex(hash);
      }
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    long endTime = System.currentTimeMillis();

    // set the hashesPerSecond to the calculated value
    this.hashesPerSec = (int) (numHashes / ((endTime - startTime)/1000.0));
  }

  /*
   * getter for hashes per second
   * */
  public int getHashesPerSec() {
    return this.hashesPerSec;
  }

  public Block getBlock(int i) {
    return chain.get(i);
  }

  /*
   * @return - int total hash of the chain
   * */
  public int getTotalDifficulty() {
    int totalDifficulty = 0;
    for (int i = 0; i < chain.size(); i++) {
      totalDifficulty += chain.get(i).getDifficulty();
    }
    return totalDifficulty;
  }

  /*
   * Compute and return the expected number of hashes required for the entire chain
   * */
  public double getTotalExpectedHashes() {
    double totalExpectedHashes = 0;
    for (int i = 0; i < chain.size(); i++) {
      totalExpectedHashes = totalExpectedHashes + Math.pow(16, chain.get(i).getDifficulty());
    }
    return totalExpectedHashes;
  }

  /*
   * @return "TRUE" if the chain is valid, otherwise return a string with an appropriate error message
   * */
  public String isChainValid() {

    /*
      if chain only contains genesis block
     compute hash of block & check that hash has requisite number of leftmost zeroes
     Also check if chain hash is equal to computed hash
    */
    if (chain.size() == 1) {
      Block b1 = chain.get(0);
      String hash = b1.proofOfWork();
      String leadingZeroes = leadingZeroes(b1.getDifficulty());
      if (!hash.startsWith(leadingZeroes)) {
        System.out.println("Improper hash on node 0 Does not begin with " + leadingZeroes);
        return "FALSE";
      }
      if (!hash.equals(chainHash)) {
        System.out.println("Improper hash on node 0 hash doesn't match chainHash");
        return "FALSE";
      }
    } else {
      /* if chain contains more than one block
       traverse through each block
      compute hash and compare with the previousHash field in the next block*/
      for (int i = 0; i < chain.size() - 1; i++) {
        Block b1 = chain.get(i); // current block
        Block b2 = chain.get(i + 1);
        String hash = b1.proofOfWork();
        if (!hash.equals(b2.getPreviousHash())) {
          System.out.printf(
                  "Improper hash on node %d Does not match previous hash of next block", i);
          return "FALSE";
        }
      }
      // check if the hash of the last block is equal to chainHash
      Block lastBlock = chain.get(chain.size() - 1);
      String hash = lastBlock.proofOfWork();
      if (!chainHash.equals(hash)) {
        System.out.println("Improper hash, hash of last block does not match chainHash");
        return "FALSE";
      }
    }
    return "TRUE";
  }

  /*
   * Provided the difficulty, returns a String containing the required number of 0s
   * @param difficulty - the difficulty associated with each block
   * */
  private String leadingZeroes(int difficulty) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < difficulty; i++) {
      sb.append("0");
    }
    return sb.toString();
  }

  /*
   * Check hashes of every block and recompute illegal hashes
   * */
  public void repairChain() {
    // if hash is invalid, recompute proof of work
    String prevHash = "";
    Block currentBlock;
    for (int i = 0; i < chain.size(); i++) {
      currentBlock = this.getBlock(i);
      if (!currentBlock.getPreviousHash().equals(prevHash)) currentBlock.setPreviousHash(prevHash);
      prevHash = currentBlock.proofOfWork();
    }
    this.chainHash = prevHash;
  }

  /*
   * adds a new block to the chain
   * */
  public void addBlock(Block newBlock) {
    newBlock.setPreviousHash(this.chainHash);
    //    String hash = newBlock.calculateHash(chain.size(), );
    this.chainHash = newBlock.proofOfWork();
    chain.add(newBlock);
  }

  /*
   * @return the blockchain in a String format
   * */
  @Override
  public String toString() {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
    String jsonString = gson.toJson(this);
    return jsonString;
  }
}
