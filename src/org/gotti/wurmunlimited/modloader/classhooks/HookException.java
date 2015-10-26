package org.gotti.wurmunlimited.modloader.classhooks;

public class HookException extends RuntimeException {

	public HookException(Throwable e) {
		super(e);
	}

	public HookException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -8955817806357327378L;

}
