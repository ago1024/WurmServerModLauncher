package org.gotti.wurmunlimited.modsupport;

import org.gotti.wurmunlimited.modloader.DefrostingClassLoader;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import javassist.CannotCompileException;
import javassist.NotFoundException;

/**
 * Create a NamedIdParser using the DefrostingClassLoader.
 * <p>
 * This means the class generated to read the names will be defrosted
 * after use and is available for further modifications
 */
public abstract class NonFreezingNamedIdParser extends NamedIdParser {
	
	/**
	 * Get the name of the class holding the names.
	 * @return class name
	 */
	protected abstract String getNamesClassName();
	
	@Override
	protected final Class<?> getNamesClass() {
		try (DefrostingClassLoader classLoader =  new DefrostingClassLoader(HookManager.getInstance().getClassPool())) {
			return classLoader.loadClass(getNamesClassName());
		} catch (ClassNotFoundException | NotFoundException | CannotCompileException e) {
			throw new IllegalStateException(e);
		}
	}
}
