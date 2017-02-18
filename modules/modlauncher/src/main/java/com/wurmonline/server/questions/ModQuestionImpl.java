package com.wurmonline.server.questions;

import java.util.Properties;

import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;

import com.wurmonline.server.creatures.Creature;

public class ModQuestionImpl extends Question {
	
	private ModQuestion modQuestion;

	public ModQuestionImpl(final Creature responder, final String title, final String question, final int type, final long target, ModQuestion modQuestion) {
		super(responder, title, question, type, target);
		this.modQuestion = modQuestion;
	}

	@Override
	public void answer(Properties answers) {
		this.setAnswer(answers);
		modQuestion.answer(this, answers);
	}

	@Override
	public void sendQuestion() {
		modQuestion.sendQuestion(this);
	}
}
