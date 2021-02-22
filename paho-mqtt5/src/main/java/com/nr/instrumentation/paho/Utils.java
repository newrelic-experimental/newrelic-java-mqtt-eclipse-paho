package com.nr.instrumentation.paho;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttActionListener;

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
	
	public static MqttActionListener wrapSubAction(MqttActionListener listener, String topic) {
		if(listener == null || !(listener instanceof MqttSubscribeWrapper)) {
			return new MqttSubscribeWrapper(listener, topic);
		}
		return listener;
	}
	
}
