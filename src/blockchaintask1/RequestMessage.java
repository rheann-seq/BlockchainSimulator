package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/*
 * @author Rheann Sequeira (rsequeir@andrew.cmu.edu)
 * */
public class RequestMessage {

    int operation;
    String input1;
    String input2;

    public RequestMessage(int operation, String input1, String input2){
        this.operation = operation;
        this.input1 = input1;
        this.input2 = input2;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return jsonString;
    }
}
