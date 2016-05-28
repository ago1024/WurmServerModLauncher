package org.gotti.wurmunlimited.modloader.server;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

public class Listeners<T, V> {
	
	private static final Logger LOGGER = Logger.getLogger(Listeners.class.getName());
	
	private List<T> listeners = new CopyOnWriteArrayList<>();
	private Class<T> listenerClass;
	
	public Listeners(Class<T> listenerClass) {
		this.listenerClass = listenerClass;
	}
	
	public void add(WurmServerMod mod) {
		if (this.listenerClass.isInstance(mod) && !listeners.contains(mod)) {
			listeners.add(listenerClass.cast(mod));
		}
	}
	
	public void fire(Consumer<T> handler) {
		listeners.forEach(listener -> {
			try {
				handler.accept(listener);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, listenerClass.getSimpleName() + " handler for mod " + listener.getClass().getSimpleName() + " failed", e);
			}
		});
	}
	
	public Optional<V> fire(Function<T, V> handler, Supplier<V> onFailure,  BinaryOperator<V> combiner) {
		return listeners
			.stream()
			.map(listener -> {
					try {
						return handler.apply(listener);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, listenerClass.getSimpleName() + " handler for mod " + listener.getClass().getSimpleName() + " failed", e);
					}
					return onFailure.get();
			})
			.reduce(combiner);
	}

}
