package org.gotti.wurmunlimited.modloader.interfaces;

import com.wurmonline.server.Message;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.villages.Village;

public interface ChannelMessageListener {

	default MessagePolicy onKingdomMessage(Message message) {
		return MessagePolicy.PASS;
	};

	default MessagePolicy onVillageMessage(Village village, Message message) {
		return MessagePolicy.PASS;
	};

	default MessagePolicy onAllianceMessage(PvPAlliance alliance, Message message) {
		return MessagePolicy.PASS;
	};

}
