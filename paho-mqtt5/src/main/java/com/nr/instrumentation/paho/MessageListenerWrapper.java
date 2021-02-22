package com.nr.instrumentation.paho;


import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageConsumeParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;

public class MessageListenerWrapper implements IMqttMessageListener {
	
	private IMqttMessageListener delegate = null;
	private static boolean isTransformed = false;
	

	public MessageListenerWrapper(IMqttMessageListener d) {
		this.delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}



	@Override
	@Trace(dispatcher=true)
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "Paho", "Paho Message Received",topic);
		
		MessageConsumeParameters params = MessageConsumeParameters.library("Paho").destinationType(DestinationType.NAMED_TOPIC).destinationName(topic).inboundHeaders(new InboundWrapper(message.getProperties())).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		
		if(delegate != null) {
			delegate.messageArrived(topic, message);
		}
	}

}
