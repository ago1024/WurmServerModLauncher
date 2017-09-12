package org.gotti.wurmunlimited.modsupport.questions;

import java.util.Properties;

import com.wurmonline.server.questions.Question;

public interface ModQuestion {
	
	public void answer(Question question, final Properties answers);
	
	public void sendQuestion(Question question);
	
	public default int getType() {
		return 0;
	}
	
	public default void sendBml(Question question, int width, int height, boolean resizeable, boolean closeable, CharSequence content, int red, int green, int blue, String title) {
		question.getResponder().getCommunicator().sendBml(width, height, resizeable, closeable, content.toString(), red, green, blue, title);
	}
	
	public default void sendBml(Question question, int width, int height, boolean resizeable, boolean closeable, CharSequence content) {
		sendBml(question, width, height, resizeable, closeable, content, 200, 200, 200, question.getTitle());
	}
}
