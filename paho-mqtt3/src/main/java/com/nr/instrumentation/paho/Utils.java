package com.nr.instrumentation.paho;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

public class Utils {

	public static IMqttMessageListener[] wrapListeners(IMqttMessageListener[] messageListeners) {
		int length = messageListeners.length;
		IMqttMessageListener[] wrappers = new IMqttMessageListener[length];
		
		for(int i=0;i<length;i++) {
			wrappers[i] = wrapListener(messageListeners[i]);
		}
		
		return wrappers;
	}
	
	public static IMqttMessageListener wrapListener(IMqttMessageListener listener) {
		if(!(listener instanceof MessageListenerWrapper)) {
			return new MessageListenerWrapper(listener);
		} else {
			return listener;
		}
		
	}
}
