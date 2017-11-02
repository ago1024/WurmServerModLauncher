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

/**
 * Listeners management
 *
 * @param <T> Listener type
 * @param <V> Listener return type
 */
public class Listeners<T, V> {
	
	private static final Logger LOGGER = Logger.getLogger(Listeners.class.getName());
	
	private List<T> listeners = new CopyOnWriteArrayList<>();
	private Class<T> listenerClass;
	
	/**
	 * Create listeners manager
	 * @param listenerClass listener class type
	 */
	public Listeners(Class<T> listenerClass) {
		this.listenerClass = listenerClass;
	}
	
	/**
	 * Add a mod if it implements the listener
	 * @param mod mod to add
	 */
	public void add(WurmServerMod mod) {
		if (this.listenerClass.isInstance(mod) && !listeners.contains(mod)) {
			listeners.add(listenerClass.cast(mod));
		}
	}
	
	/**
	 * Add a listener
	 * @param listener listener to add
	 */
	public void add(T listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Process listeners
	 * @param handler consumer which is called for each listener
	 */
	public void fire(Consumer<T> handler) {
		listeners.forEach(listener -> {
			try {
				handler.accept(listener);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, listenerClass.getSimpleName() + " handler for mod " + listener.getClass().getSimpleName() + " failed", e);
			}
		});
	}
	
	/**
	 * Process listeners
	 * @param handler consumer which is called for each listener
	 * @param onFailure supplier which is called if a listener threw an exception
	 * @param combiner combine results of each listener
	 * @return combined results
	 */
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
