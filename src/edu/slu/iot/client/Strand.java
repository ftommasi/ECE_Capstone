package edu.slu.iot.client;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;

class Strand {
    
	public static void main(String args[]) throws InterruptedException, AWSIotException, AWSIotTimeoutException {
    	
		IoTClient client = new IoTClient("conf.txt");
    	
        client.subscribe("test");
    }
}
