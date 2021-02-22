package org.eclipse.paho.client.mqttv3;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.paho.MqttPublishActionWrapper;
import com.nr.instrumentation.paho.MqttSubscribeWrapper;
import com.nr.instrumentation.paho.Utils;

@Weave(type=MatchType.Interface)
public abstract class IMqttAsyncClient {
	
	public abstract String getClientId();
	public abstract String getServerURI();

	@Trace
	public IMqttToken subscribe(String[] topicFilters, int[] qos, Object userContext, IMqttActionListener callback, IMqttMessageListener[] messageListeners) {
		messageListeners = Utils.wrapListeners(messageListeners);
		StringBuffer sb = new StringBuffer();
		int length = topicFilters.length;
		for(int i=0;i<length;i++) {
			sb.append(topicFilters[i]);
			if(i < length-1) {
				sb.append(',');
			}
		}
		callback = new MqttSubscribeWrapper(callback, sb.toString());
		return Weaver.callOriginal();
	}
	
	@Trace
	public IMqttToken subscribe(String[] topicFilters, int[] qos, Object userContext, IMqttActionListener callback) {
		StringBuffer sb = new StringBuffer();
		int length = topicFilters.length;
		for(int i=0;i<length;i++) {
			sb.append(topicFilters[i]);
			if(i < length-1) {
				sb.append(',');
			}
		}
		callback = new MqttSubscribeWrapper(callback, sb.toString());
		return Weaver.callOriginal();
	}
	
	@Trace
	public IMqttDeliveryToken publish(String topic, MqttMessage message, Object userContext, IMqttActionListener callback) {
		MqttPublishActionWrapper wrapper = new MqttPublishActionWrapper(callback, topic);
		callback = wrapper;
		return Weaver.callOriginal();
	}
}
