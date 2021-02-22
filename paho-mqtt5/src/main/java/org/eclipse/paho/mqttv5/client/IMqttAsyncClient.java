package org.eclipse.paho.mqttv5.client;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageProduceParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.paho.MqttPublishActionWrapper;
import com.nr.instrumentation.paho.OutboundWrapper;
import com.nr.instrumentation.paho.Utils;

@Weave(type=MatchType.Interface)
public abstract class IMqttAsyncClient {

	@Trace
	public IMqttToken publish(String topic, MqttMessage message, Object userContext, MqttActionListener callback) {
		OutboundWrapper wrapper = new OutboundWrapper(message.getProperties());
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		message.setProperties(wrapper.getCurrent());
		if(callback == null || !(callback instanceof MqttPublishActionWrapper)) {
			NewRelic.incrementCounter("Paho/CreatePublish");
			MqttPublishActionWrapper pubWrapper = new MqttPublishActionWrapper(callback, topic);
			callback = pubWrapper;
		} else {
			NewRelic.incrementCounter("Paho/UseParams");
			MessageProduceParameters params = MessageProduceParameters.library("Paho-MQTT5").destinationType(DestinationType.NAMED_TOPIC).destinationName(topic).outboundHeaders(null).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		IMqttToken token = Weaver.callOriginal();
		return token;
	}

	@Trace
	public IMqttToken subscribe(MqttSubscription[] subscriptions, Object userContext, MqttActionListener callback, IMqttMessageListener messageListener, MqttProperties subscriptionProperties) {
		messageListener = Utils.wrapListener(messageListener);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<subscriptions.length;i++) {
			MqttSubscription subscription = subscriptions[i];
			sb.append(subscription.getTopic());
			if(i < subscriptions.length-1) {
				sb.append(',');
			}
		}
		callback = Utils.wrapSubAction(callback, sb.toString());
		return Weaver.callOriginal();
	}

	@Trace
	public IMqttToken subscribe(MqttSubscription[] subscriptions, Object userContext, MqttActionListener callback, IMqttMessageListener[] messageListeners, MqttProperties subscriptionProperties) {
		messageListeners = Utils.wrapListeners(messageListeners);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<subscriptions.length;i++) {
			MqttSubscription subscription = subscriptions[i];
			sb.append(subscription.getTopic());
			if(i < subscriptions.length-1) {
				sb.append(',');
			}
		}
		callback = Utils.wrapSubAction(callback, sb.toString());
		return Weaver.callOriginal();		
	}

	@Trace
	public IMqttToken subscribe(MqttSubscription[] subscriptions, Object userContext, MqttActionListener callback, MqttProperties subscriptionProperties) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<subscriptions.length;i++) {
			MqttSubscription subscription = subscriptions[i];
			sb.append(subscription.getTopic());
			if(i < subscriptions.length-1) {
				sb.append(',');
			}
		}
		callback = Utils.wrapSubAction(callback, sb.toString());
		return Weaver.callOriginal();
	}
}
