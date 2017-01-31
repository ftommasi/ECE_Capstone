import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.client.sample.sampleUtil.CommandArguments;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

public class IoTClient {
	
	private static AWSIotMqttClient awsIotClient;
	private static final AWSIotQos TestTopicQos = AWSIotQos.QOS1;
	
	public IoTClient(String[] args) throws AWSIotException {
        CommandArguments arguments = CommandArguments.parse(args);
        initClient(arguments);
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
	
    public void initClient(CommandArguments arguments) {
        String clientEndpoint = arguments.getNotNull("clientEndpoint", SampleUtil.getConfig("clientEndpoint"));
        String clientId = arguments.getNotNull("clientId", SampleUtil.getConfig("clientId"));

        String certificateFile = arguments.get("certificateFile", SampleUtil.getConfig("certificateFile"));
        String privateKeyFile = arguments.get("privateKeyFile", SampleUtil.getConfig("privateKeyFile"));
        if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
            String algorithm = arguments.get("keyAlgorithm", SampleUtil.getConfig("keyAlgorithm"));
            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);

            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        }

        if (awsIotClient == null) {
            String awsAccessKeyId = arguments.get("awsAccessKeyId", SampleUtil.getConfig("awsAccessKeyId"));
            String awsSecretAccessKey = arguments.get("awsSecretAccessKey", SampleUtil.getConfig("awsSecretAccessKey"));
            String sessionToken = arguments.get("sessionToken", SampleUtil.getConfig("sessionToken"));

            if (awsAccessKeyId != null && awsSecretAccessKey != null) {
                awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
                        sessionToken);
            }
        }

        if (awsIotClient == null) {
            throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
        }
    }
}
