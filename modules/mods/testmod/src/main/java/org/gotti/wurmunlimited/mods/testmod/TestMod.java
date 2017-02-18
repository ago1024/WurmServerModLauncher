package org.gotti.wurmunlimited.mods.testmod;

import java.util.Properties;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.questions.Question;

public class TestMod implements WurmServerMod, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants, PlayerMessageListener {

	private static Logger logger = Logger.getLogger(TestMod.class.getName());

	@Override
	public void onItemTemplatesCreated() {
	}

	@Override
	public void onServerStarted() {
	}

	@Override
	public void init() {
	}

	public static boolean willMineSlope(Creature performer, Item source) {
		return true;
	}

	@Override
	public void preInit() {
		ModActions.init();
	}
	
	@Override
	public MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
		if ("/question".equals(message)) {
			ModQuestions.createQuestion(communicator.getPlayer(), "Test", "Test", -10, new ModQuestion() {
				
				@Override
				public void sendQuestion(Question question) {
					final StringBuilder buf = new StringBuilder(ModQuestions.getBmlHeader(question));
					buf.append("label{text=\"Test.\"}");
					buf.append(ModQuestions.createAnswerButton2(question, "Send");
					question.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, question.getTitle());
				}
				
				@Override
				public void answer(Question question, Properties answers) {
					logger.info(String.valueOf(answers));
				}
			}).sendQuestion();
			
			return MessagePolicy.DISCARD;
		}
		return MessagePolicy.PASS;
	}
	
	@Override
	public boolean onPlayerMessage(Communicator communicator, String message) {
		return false;
	}

}
