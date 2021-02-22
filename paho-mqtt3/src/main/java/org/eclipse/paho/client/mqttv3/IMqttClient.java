package org.eclipse.paho.client.mqttv3;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.paho.Utils;

@Weave(type=MatchType.Interface)
public class IMqttClient {

	@Trace
	public IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners) {
		messageListeners = Utils.wrapListeners(messageListeners);
		return Weaver.callOriginal();
	}
	
	@Trace
	public IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener) {
		messageListener = Utils.wrapListener(messageListener);
		return Weaver.callOriginal();
	}
	
	@Trace
	public IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener) {
		messageListener = Utils.wrapListener(messageListener);
		return Weaver.callOriginal();
	}
	
	@Trace
	public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
		messageListeners = Utils.wrapListeners(messageListeners);
		return Weaver.callOriginal();
	}
	
}
