package org.gotti.wurmunlimited.modloader.interfaces;

import com.wurmonline.server.Message;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.villages.Village;

public interface ChannelMessageListener {
	
	default void onKingdomMessage(Message message) {};
	
	default void onVillageMessage(Village village, Message message) {};
	
	default void onAllianceMessage(PvPAlliance alliance, Message message) {};

}
