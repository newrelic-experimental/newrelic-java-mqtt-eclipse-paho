package com.nr.instrumentation.paho;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageProduceParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class MqttPublishActionWrapper implements IMqttActionListener {
	
	private Segment segment = null;
	private IMqttActionListener delegate = null;
	private String topicName = null;
	
	public MqttPublishActionWrapper(IMqttActionListener d, String tn) {
		delegate = d;
		topicName = tn;
		segment = NewRelic.getAgent().getTransaction().startSegment("MQTT-Send");
	}

	@Override
	public void onSuccess(IMqttToken asyncActionToken) {
		if(segment != null) {
			MessageProduceParameters params = MessageProduceParameters.library("Paho").destinationType(DestinationType.NAMED_TOPIC).destinationName(topicName).outboundHeaders(null).build();
			segment.reportAsExternal(params);
			segment.end();
			segment = null;
		}
		if(delegate != null) {
			delegate.onSuccess(asyncActionToken);
		}
	}

	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		Map<String, Object> params = new HashMap<String, Object>();
		int msgId = asyncActionToken.getMessageId();
		params.put("MessageID", msgId);
		Object userCtx = asyncActionToken.getUserContext();
		if(userCtx != null) {
			params.put("UserContext", userCtx);
		}
		params.put("Topic", topicName);
		IMqttAsyncClient client = asyncActionToken.getClient();
		params.put("ClientID",client.getClientId());
		params.put("Client-ServerURL", client.getServerURI());
		NewRelic.noticeError(exception,params);
		if(segment != null) {
			segment.ignore();
			segment = null;
		}
		if(delegate != null) {
			delegate.onFailure(asyncActionToken, exception);
		}
	}

}
