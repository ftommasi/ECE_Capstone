package edu.slu.iot.realdaq;


import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import edu.slu.iot.Publisher;

public class DaqPublisher extends Publisher {

  private String sessionID;
  private String deviceID = "defaultDeviceID";
  private static final Gson gson = new Gson();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final AdcReader reader = new AdcReader();
  public AtomicInteger c = new AtomicInteger(0);
  public DaqPublisher(String topic, AWSIotQos qos, String sessionID) {
    super(topic, qos);
    this.sessionID = sessionID;
  }

  @Override
  public void run() {
  scheduler.scheduleAtFixedRate( ()-> {
    
    //Sample s = new Sample(deviceID, sessionID, System.nanoTime(), 1.0f);
     reader.sampleAt(50000,50000.0f);
    /* 
    String jsonSample = gson.toJson(s);
     AWSIotMessage message = new NonBlockingPublishListener(topic, qos, jsonSample);
     publish(message);
     */
     c.incrementAndGet();
//      System.out.println("INNER: " + c);
   },0,1,TimeUnit.SECONDS);
    try{ 
      Thread.sleep(1000);
    }
    catch(InterruptedException e){
      //NOT HADNLING SHIT
    }

    System.err.println("FINAL: " + c.intValue());
  }

  private class NonBlockingPublishListener extends AWSIotMessage {
	  
	Sample sample;

    public NonBlockingPublishListener(String topic, AWSIotQos qos, String payload) {
      super(topic, qos, payload);
      sample = gson.fromJson(getStringPayload(), Sample.class);
    }

    @Override
    public void onSuccess() {
      System.out.println(System.currentTimeMillis() + ": >>> " + sample);
    }

    @Override
    public void onFailure() {

      System.out.println(this.errorCode + " " + this.errorMessage);
      System.out.println(System.currentTimeMillis() + ": publish failed for " + sample);
    }

    @Override
    public void onTimeout() {
      System.out.println(System.currentTimeMillis() + ": publish timeout for " + sample);
    }

  }
}


