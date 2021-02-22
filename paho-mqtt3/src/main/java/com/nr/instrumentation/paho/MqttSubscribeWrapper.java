package com.nr.instrumentation.paho;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class MqttSubscribeWrapper implements IMqttActionListener {
	
	
	private IMqttActionListener delegate = null;
	private String topics = null;
	private Segment segment = null;

	public MqttSubscribeWrapper(IMqttActionListener d, String t) {
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
		IMqttAsyncClient client = asyncActionToken.getClient();
		params.put("ClientID",client.getClientId());
		params.put("Client-ServerURL", client.getServerURI());
		NewRelic.noticeError(exception,params);
		if(delegate != null) {
			delegate.onFailure(asyncActionToken, exception);
		}
	}

}
