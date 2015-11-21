package org.gotti.wurmunlimited.modsupport.vehicles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public abstract class ModVehicleBehaviour {
	
	public abstract void setSettingsForVehicle(final Creature creature, final Vehicle vehicle);

	public abstract void setSettingsForVehicle(final Item item, final Vehicle vehicle);
	

	protected VehicleFacade wrap(final Vehicle v) {
		return new VehicleFacadeImpl(v);
	}
	

	protected Seat createSeat(byte typeHitched) {
		try {
			Constructor<Seat> c = Seat.class.getDeclaredConstructor(byte.class);
			boolean wasAcessible = c.isAccessible();
			c.setAccessible(true);
			try {
				return c.newInstance(typeHitched);
			} finally {
				c.setAccessible(wasAcessible);
			}
		} catch (InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
		
	}

}
