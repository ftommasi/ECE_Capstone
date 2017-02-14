package edu.slu.iot.realdaq;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.slu.iot.Publisher;

public class DaqPublisher extends Publisher {

  private String sessionID;

  public DaqPublisher(String topic, AWSIotQos qos, String sessionID) {
    super(topic, qos);
    this.sessionID = sessionID;
  }

  @Override
  public void run() {


    File file = new File("/home/debian/ECE_Capstone_Networking/src/main/c/ECE_Capstone_ADC/SAMPLE-SESSION");
    try{
      Scanner sc = new Scanner(file);
      while(true){
        if(sc.hasNextLine()){
          long millis = System.currentTimeMillis();
          String value = sc.nextLine();
          String payload = "{\"time\": " + millis + ",\"session\": " + "\"" + sessionID + "\"" + ",\"value\": " + value + "}";

          AWSIotMessage message = new NonBlockingPublishListener(topic, qos, payload);

          publish(message);
        }
      }
      //sc.close();
    }

    catch(FileNotFoundException e){
      System.out.println("could not find file");
      e.printStackTrace();
    }



  }

  private class NonBlockingPublishListener extends AWSIotMessage {

    public NonBlockingPublishListener(String topic, AWSIotQos qos, String payload) {
      super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
      System.out.println(System.currentTimeMillis() + ": >>> " + getStringPayload());
    }

    @Override
    public void onFailure() {
      System.out.println(System.currentTimeMillis() + ": publish failed for " + getStringPayload());
    }

    @Override
    public void onTimeout() {
      System.out.println(System.currentTimeMillis() + ": publish timeout for " + getStringPayload());
    }

  }
}


