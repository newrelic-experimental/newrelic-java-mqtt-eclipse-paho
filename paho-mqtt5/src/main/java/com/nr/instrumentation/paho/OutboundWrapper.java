package com.nr.instrumentation.paho;

import java.util.List;

import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {
	
	private MqttProperties properties = null;

	public OutboundWrapper(MqttProperties p) {
		properties = p;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String key, String value) {
		if(properties == null) {
			properties = new MqttProperties();
		}
		List<UserProperty> userProps = properties.getUserProperties();
		
		UserProperty prop = new UserProperty(key, value);
		userProps.add(prop);
		
		properties.setUserProperties(userProps);
	}
	
	public MqttProperties getCurrent() {
		return properties;
	}

}
