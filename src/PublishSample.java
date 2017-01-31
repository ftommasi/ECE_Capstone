import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;

class PublishSample {
	
	public static void main(String args[]) throws InterruptedException, AWSIotException, AWSIotTimeoutException {
    	
		IoTClient client = new IoTClient(args);
    	
        client.publish("test");

    }
}
