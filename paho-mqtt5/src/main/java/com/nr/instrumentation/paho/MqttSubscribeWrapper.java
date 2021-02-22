package com.nr.instrumentation.paho;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttClientInterface;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class MqttSubscribeWrapper implements MqttActionListener {
	
	
	private MqttActionListener delegate = null;
	private String topics = null;
	private Segment segment = null;

	public MqttSubscribeWrapper(MqttActionListener d, String t) {
		delegate = d;
		topics = t;
		segment = NewRelic.getAgent().getTransaction().startSegment("Subscribe-"+t);
	}

	@Override
	public void onSuccess(IMqttToken asyncActionToken) {
		if(segment != null) {
			segment.end();
			segment = null;
		}
		if(delegate != null) {
			delegate.onSuccess(asyncActionToken);
		}
	}

	@Override
	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		if(segment != null) {
			segment.ignore();
			segment = null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		int msgId = asyncActionToken.getMessageId();
		params.put("MessageID", msgId);
		Object userCtx = asyncActionToken.getUserContext();
		if(userCtx != null) {
			params.put("UserContext", userCtx);
		}
		params.put("Topics", topics);
		MqttClientInterface client = asyncActionToken.getClient();
		params.put("ClientID",client.getClientId());
		params.put("Client-ServerURL", client.getServerURI());
		NewRelic.noticeError(exception,params);
		if(delegate != null) {
			delegate.onFailure(asyncActionToken, exception);
		}
	}

}
