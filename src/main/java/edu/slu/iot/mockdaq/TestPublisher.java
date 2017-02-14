package edu.slu.iot.mockdaq;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

import edu.slu.iot.Publisher;

public class TestPublisher extends Publisher {
	
	private String sessionID;
	
	public TestPublisher(String topic, AWSIotQos qos, String sessionID) {
		super(topic, qos);
		this.sessionID = sessionID;
	}

	@Override
    public void run() {
    	
        while (true) {
        	
        	long millis = System.currentTimeMillis();
            String payload = "{\"time\": " + millis + ",\"session\": " + "\"" + sessionID + "\"" + ",\"value\": " + Math.sin((double) millis / 1000) + "}";
            
            AWSIotMessage message = new NonBlockingPublishListener(topic, qos, payload);
            
            publish(message);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(System.currentTimeMillis() + ": NonBlockingPublisher was interrupted");
                return;
            }
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
