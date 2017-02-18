package org.gotti.wurmunlimited.modsupport.questions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.questions.ModQuestionImpl;
import com.wurmonline.server.questions.Question;

public class ModQuestions {

	private static Method getBmlHeader;
	private static Method getBmlHeaderNoQuestion;
	private static Method getBmlHeaderWithScroll;
	private static Method getBmlHeaderWithScrollAndQuestion;
	private static Method getBmlHeaderScrollOnly;
	private static Method createAnswerButtonForNoBorder;
	private static Method createAnswerButton2;
	private static Method createAnswerButton2_String;
	private static Method createOkAnswerButton;
	private static Method createBackAnswerButton;
	private static Method createAnswerButton3;

	static {
		try {
			getBmlHeader = ReflectionUtil.getMethod(Question.class, "getBmlHeader");
			getBmlHeaderNoQuestion = ReflectionUtil.getMethod(Question.class,  "getBmlHeaderNoQuestion");

			getBmlHeaderWithScroll = ReflectionUtil.getMethod(Question.class,  "getBmlHeaderWithScroll");
			getBmlHeaderWithScrollAndQuestion = ReflectionUtil.getMethod(Question.class,  "getBmlHeaderWithScrollAndQuestion");
			getBmlHeaderScrollOnly = ReflectionUtil.getMethod(Question.class,  "getBmlHeaderScrollOnly");
			createAnswerButtonForNoBorder = ReflectionUtil.getMethod(Question.class,  "createAnswerButtonForNoBorder");
			createAnswerButton2 = ReflectionUtil.getMethod(Question.class,  "createAnswerButton2", new Class<?>[0]);
			createAnswerButton2_String = ReflectionUtil.getMethod(Question.class,  "createAnswerButton2", new Class<?>[] { String.class });
			createOkAnswerButton = ReflectionUtil.getMethod(Question.class,  "createOkAnswerButton");
			createBackAnswerButton = ReflectionUtil.getMethod(Question.class,  "createBackAnswerButton");
			createAnswerButton3 = ReflectionUtil.getMethod(Question.class,  "createAnswerButton3");
			
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static Question createQuestion(Creature responder, String title, String question, long target, ModQuestion modQuestion) {
		return new ModQuestionImpl(responder, title, question, modQuestion.getType(), target, modQuestion);
	}

	private static <T> T callPrivateMethod(Question question, Method method, Object... args) {
		try {
			return ReflectionUtil.callPrivateMethod(question, method, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getBmlHeader(Question question) {
		return callPrivateMethod(question, getBmlHeader);
	}

	public static String getBmlHeaderNoQuestion(Question question) {
		return callPrivateMethod(question, getBmlHeaderNoQuestion);
	}

	public static String getBmlHeaderWithScroll(Question question) {
		return callPrivateMethod(question, getBmlHeaderWithScroll);
	}

	public static String getBmlHeaderWithScrollAndQuestion(Question question) {
		return callPrivateMethod(question, getBmlHeaderWithScrollAndQuestion);
	}

	public static String getBmlHeaderScrollOnly(Question question) {
		return callPrivateMethod(question, getBmlHeaderScrollOnly);
	}

	public static String createAnswerButtonForNoBorder(Question question) {
		return callPrivateMethod(question, createAnswerButtonForNoBorder);
	}

	public static String createAnswerButton2(Question question) {
		return callPrivateMethod(question, createAnswerButton2);
	}

	public static String createAnswerButton2(Question question, String sendText) {
		return callPrivateMethod(question, createAnswerButton2_String, sendText);
	}

	public static String createOkAnswerButton(Question question) {
		return callPrivateMethod(question, createOkAnswerButton);
	}

	public static String createBackAnswerButton(Question question) {
		return callPrivateMethod(question, createBackAnswerButton);
	}

	public static String createAnswerButton3(Question question) {
		return callPrivateMethod(question, createAnswerButton3);
	}

}
