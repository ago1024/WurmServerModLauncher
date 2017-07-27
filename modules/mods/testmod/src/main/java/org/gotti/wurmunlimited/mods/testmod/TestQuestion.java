package org.gotti.wurmunlimited.mods.testmod;

import java.util.Properties;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.Question;

final class TestQuestion implements ModQuestion {

	static Logger logger = Logger.getLogger(TestQuestion.class.getName());

	@Override
	public void sendQuestion(Question question) {
		final StringBuilder buf = new StringBuilder(ModQuestions.getBmlHeader(question));
		buf.append("label{text=\"Test.\"}");
		buf.append(ModQuestions.createAnswerButton2(question, "Send"));
		question.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, question.getTitle());
	}

	@Override
	public void answer(Question question, Properties answers) {
		logger.info(String.valueOf(answers));
	}

	public static void create(Player player) {
		ModQuestions.createQuestion(player, "Test", "Test", -10, new TestQuestion()).sendQuestion();
	}
}