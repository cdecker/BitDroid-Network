/**
 * Copyright 2011 Christian Decker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is part the BitDroidNetwork Project.
 */
package net.bitdroid.network;

import net.bitdroid.network.messages.Message;

/**
 * @author cdecker
 *
 */
public class Event {
	/**
	 * Indicates that apropriate actions have been taken to respond to the event.
	 */
	private boolean handled = false;
	/**
	 * Should this be false the reactor will stop notifying event listeners.
	 */
	private boolean propagate = true;
	
	public static enum EventType {
		CONNECTION_ACCEPTED_TYPE,
		DISCONNECTED_TYPE,
		VERSION_TYPE,
		VERACK_TYPE,
		ADDR_TYPE,
		BLOCK_TYPE,
		GET_ADDR_TYPE,
		GET_DATA_TYPE,
		INVENTORY_TYPE,
		PING_TYPE,
		TRANSACTION_TYPE,
		UNKNOWN_TYPE,
		PART_TYPE // Used to indicate that the message is not a standalone message.
	};	

	public Event(){}
	public Event(Object o, Message m){
		setOrigin(o);
		setSubject(m);
	}
	
	private EventType type;
	private Message subject;
	private Object origin;
	
	/**
	 * @return the type
	 */
	public EventType getType() {
		return type;
	}
	
	/**
	 * @param versionType the type to set
	 */
	public void setType(EventType versionType) {
		this.type = versionType;
	}
	
	/**
	 * @return the subject
	 */
	public Message getSubject() {
		return subject;
	}
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Message subject) {
		this.subject = subject;
		this.type = subject.getType();
	}
	
	/**
	 * An object identifying the origin of the event. This has to be meaningful
	 * only to the reactor, which will use it to identify the targets to send
	 * answers to.
	 * 
	 * @return the origin
	 */
	public Object getOrigin() {
		return origin;
	}
	
	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Object origin) {
		this.origin = origin;
	}
	
	/**
	 * @return the handled
	 */
	public boolean isHandled() {
		return handled;
	}
	
	/**
	 * @param handled the handled to set
	 */
	public void setHandled(boolean handled) {
		this.handled = handled;
	}
	
	/**
	 * @return the propagate
	 */
	public boolean isPropagate() {
		return propagate;
	}
	
	/**
	 * @param propagate the propagate to set
	 */
	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}
}
