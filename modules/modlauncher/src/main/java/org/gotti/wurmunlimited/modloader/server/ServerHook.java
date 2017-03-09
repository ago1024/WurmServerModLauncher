package org.gotti.wurmunlimited.modloader.server;

import java.util.Arrays;
import java.util.List;

import org.gotti.wurmunlimited.modcomm.ModComm;
import org.gotti.wurmunlimited.modloader.interfaces.ChannelMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerPollListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.Message;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.villages.Village;

public class ServerHook {
	
	Listeners<ServerStartedListener, Void> serverStarted = new Listeners<>(ServerStartedListener.class);
	Listeners<ItemTemplatesCreatedListener, Void> itemTemplatesCreated = new Listeners<>(ItemTemplatesCreatedListener.class);
	Listeners<PlayerMessageListener, MessagePolicy> playerMessage = new Listeners<>(PlayerMessageListener.class);
	Listeners<PlayerLoginListener, Void> playerLogin = new Listeners<>(PlayerLoginListener.class);
	Listeners<ServerPollListener, Void> serverPoll = new Listeners<>(ServerPollListener.class);
	Listeners<ChannelMessageListener, MessagePolicy> channelMessage = new Listeners<>(ChannelMessageListener.class);
	
	List<Listeners<?, ?>> handlers = Arrays.asList(serverStarted, itemTemplatesCreated, playerMessage, playerLogin, serverPoll, channelMessage);
	
	
	protected ServerHook() {
	}

	public void addMods(List<WurmServerMod> wurmMods) {
		wurmMods.forEach(mod -> {
			handlers.forEach(handler -> handler.add(mod));
		});
	}
	
	public void fireOnServerStarted() {
		ModComm.serverStarted();
		serverStarted.fire(listener -> listener.onServerStarted());
	}
	
	public void fireOnItemTemplatesCreated() {
		itemTemplatesCreated.fire(listener -> listener.onItemTemplatesCreated());
	}

	public boolean fireOnMessage(Communicator communicator, String message, String title) {
		return playerMessage.fire(listener -> listener.onPlayerMessage(communicator, message, title), () -> MessagePolicy.PASS, MessagePolicy.ANY_DISCARDED).orElse(MessagePolicy.PASS) == MessagePolicy.DISCARD;
	}
	
	public void fireOnPlayerLogin(Player player) {
		ModComm.playerConnected(player);
		playerLogin.fire(listener -> listener.onPlayerLogin(player));
	}
	
	public void fireOnPlayerLogout(Player player) {
		playerLogin.fire(listener -> listener.onPlayerLogout(player));
	}
	
	public void fireOnServerPoll() {
		serverPoll.fire(listener -> listener.onServerPoll());
	}
	
	public boolean fireOnKingdomMessage(Message message) {
		return channelMessage.fire(listener -> listener.onKingdomMessage(message), () -> MessagePolicy.PASS, MessagePolicy.ANY_DISCARDED).orElse(MessagePolicy.PASS) == MessagePolicy.DISCARD;
	}
	
	public boolean fireOnVillageMessage(Village village, Message message) {
		return channelMessage.fire(listener -> listener.onVillageMessage(village, message), () -> MessagePolicy.PASS, MessagePolicy.ANY_DISCARDED).orElse(MessagePolicy.PASS) == MessagePolicy.DISCARD;
	}
	
	public boolean fireOnAllianceMessage(PvPAlliance alliance, Message message) {
		return channelMessage.fire(listener -> listener.onAllianceMessage(alliance, message), () -> MessagePolicy.PASS, MessagePolicy.ANY_DISCARDED).orElse(MessagePolicy.PASS) == MessagePolicy.DISCARD;
	}
	
	public static ServerHook createServerHook() {
		return ProxyServerHook.getInstance();
	}
}
