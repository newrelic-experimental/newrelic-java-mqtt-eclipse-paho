package com.nr.instrumentation.paho;

import java.util.List;

import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {
	private MqttProperties properties = null;
	
	public InboundWrapper(MqttProperties p) {
		properties = p;
	}

	@Override
	public String getHeader(String name) {
		List<UserProperty> userProps = properties.getUserProperties();
		
		for(UserProperty prop : userProps) {
			String key = prop.getKey();
			if(key.equals(name)) {
				return prop.getValue();
			}
		}
		
		return null;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

}
