package bam.pong;

import java.util.*;

public class Event {
	
	private List<EventListener> listeners =
		new LinkedList<EventListener>();
	
	public Event() {}
	
	public void register(EventListener el) {
		listeners.add(el);
	}
	
	public boolean unregister(EventListener el) {
		return listeners.remove(el);
	}
	
	public void trigger() {
		trigger(null);
	}
	
	public void trigger(Object o) {
		for (EventListener el : listeners) {
			el.eventTriggered(o);
		}
	}
	
}
