package org.gotti.wurmunlimited.modsupport.questions;

import java.util.Properties;

import com.wurmonline.server.questions.Question;

public interface ModQuestion {
	
	public void answer(Question question, final Properties answers);
	
	public void sendQuestion(Question question);
	
	public default int getType() {
		return 0;
	}
	
}
