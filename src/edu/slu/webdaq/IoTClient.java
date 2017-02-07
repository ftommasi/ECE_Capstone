package edu.slu.webdaq;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

public class IoTClient {
	
	private static AWSIotMqttClient awsIotClient;
	private static final AWSIotQos TestTopicQos = AWSIotQos.QOS1;
	
	public IoTClient(String filename) throws AWSIotException {
        initClient(filename);
        awsIotClient.connect();
	}
	
	public Thread publish(String testTopic) throws InterruptedException {
        Thread nonBlockingPublishThread = new Thread(new NonBlockingPublisher(awsIotClient, testTopic));

        nonBlockingPublishThread.start();
        
        return nonBlockingPublishThread;
	}
	
	public void subscribe(String testTopic) throws AWSIotException {
		AWSIotTopic topic = new TestTopicListener(testTopic, TestTopicQos);
	    awsIotClient.subscribe(topic, true);
	}
	
    public static class NonBlockingPublisher implements Runnable {
        private final AWSIotMqttClient awsIotClient;
        private String testTopic;

        public NonBlockingPublisher(AWSIotMqttClient awsIotClient, String testTopic) {
            this.awsIotClient = awsIotClient;
            this.testTopic = testTopic;
        }

        @Override
        public void run() {
        	
        	String sessionID = "demo";

            while (true) {
            	long millis = System.currentTimeMillis();
                String payload = "{\"time\": " + millis + ",\"session\": " + "\"" + sessionID + "\"" + ",\"value\": " + Math.sin((double) millis / 1000) + "}";
                AWSIotMessage message = new NonBlockingPublishListener(testTopic, TestTopicQos, payload);
                try {
                    awsIotClient.publish(message);
                } catch (AWSIotException e) {
                    System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(System.currentTimeMillis() + ": NonBlockingPublisher was interrupted");
                    return;
                }
            }
        }
    }
	
    public void initClient(String filename) {
    	
    	File config = new File(filename);
    	Map<String, String> configMap = new HashMap<String, String>();
    	
    	Scanner sc = null;
		try {
			sc = new Scanner(config);
			
	    	while (sc.hasNextLine()) {
	    		String line = sc.nextLine();
	    		String[] fields = line.split("\\s+");

	    		if (fields.length != 2) {
	    			throw new IllegalArgumentException("invalid format for config file");
	    		}
	    		
	    		configMap.put(fields[0], fields[1]);
	    	}	    	
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("bad filename for config file");
		} finally {
			sc.close();
		}
    	
        String clientEndpoint = configMap.get("clientEndpoint");
        String clientId = configMap.get("clientId");
        String certificateFile = configMap.get("certificateFile");
        String privateKeyFile = configMap.get("privateKeyFile");
                
        if (clientEndpoint != null && clientId != null && certificateFile != null && privateKeyFile != null) {
        	KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        } else {
        	throw new IllegalArgumentException("Failed to construct client due to missing arguments");
        }
    }
}
