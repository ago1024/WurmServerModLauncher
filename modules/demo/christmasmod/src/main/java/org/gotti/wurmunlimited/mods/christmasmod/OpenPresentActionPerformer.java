package org.gotti.wurmunlimited.mods.christmasmod;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.FINISH_ACTION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_SERVER_PROPAGATION;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.sounds.SoundPlayer;

/**
 * Action performer to unwrap the present and give the gift to the player
 * 
 * @author ago
 */
public class OpenPresentActionPerformer implements ActionPerformer {

	private static final Logger LOGGER = Logger.getLogger(OpenPresentActionPerformer.class.getName());

	/**
	 * Configure present content
	 */
	public static final class GiftData {
		private int templateId;
		private float quality;
		private byte auxdata;
		private BiConsumer<Creature, Item> doAfter;

		/**
		 * Create {@link GiftData} with quality 99 and auxdata 0
		 * 
		 * @param templateId
		 *            Template id of gift
		 */
		public GiftData(int templateId) {
			this(templateId, 99.0f, (byte) 0);
		}

		/**
		 * Create {@link GiftData} with auxdata 0
		 * 
		 * @param templateId
		 *            Template id of gift
		 * @param quality
		 *            Quality of gift
		 */
		public GiftData(int templateId, float quality) {
			this(templateId, quality, (byte) 0);
		}

		/**
		 * Create {@link GiftData}
		 * 
		 * @param templateId
		 *            Template id of gift
		 * @param quality
		 *            Quality of gift
		 * @param auxdata
		 *            Auxdata of gift
		 */
		public GiftData(int templateId, float quality, byte auxdata) {
			this.templateId = templateId;
			this.quality = quality;
			this.auxdata = auxdata;
			this.doAfter = OpenPresentActionPerformer::noop;
		}

		/**
		 * Add code to run after creating the gift
		 * 
		 * @param doAfter
		 *            doAfter method
		 * @return {@link GiftData}
		 */
		public GiftData doAfter(BiConsumer<Creature, Item> doAfter) {
			this.doAfter = doAfter;
			return this;
		}
	}

	private final Function<Byte, GiftData> giftDataSupplier;

	private static final void noop(Creature performer, Item gift) {
	}

	private static final void addTransmutationLiquid(Creature performer, Item gift) {
		try {
			Item liquid = ItemFactory.createItem(ItemList.potionTransmutation, 99.0f, null);
			gift.insertItem(liquid, true);
		} catch (NoSuchTemplateException nst) {
			LOGGER.log(Level.WARNING, performer.getName() + " Christmas present template gone? " + nst.getMessage(), (Throwable) nst);
		} catch (FailedException fe) {
			LOGGER.log(Level.WARNING, performer.getName() + " receives no Christmas present: " + fe.getMessage(), (Throwable) fe);
		}
	}

	private static final void makeHummingSound(Creature performer, Item gift) {
		performer.getCommunicator().sendSafeServerMessage("You hear a barely audible humming sound.");
	}

	/**
	 * Create {@link GiftData} for regular server presents.
	 * 
	 * @param auxData
	 *            AuxData of present
	 * @return {@link GiftData} for the gift inside the present
	 */
	public static GiftData getDefaultPresentData(byte auxData) {

		switch (auxData) {
		case 0: // 2007 and fallback

		default:
			return new GiftData(ItemList.farwalkerAmulet, 99.0f);
		case 1: // 2008
			return new GiftData(ItemList.wandSculpting, 60.0f, (byte) 1).doAfter(OpenPresentActionPerformer::makeHummingSound);
		case 2: // 2009
			return new GiftData(ItemList.flaskGlass, 60.0f, (byte) 0).doAfter(OpenPresentActionPerformer::addTransmutationLiquid);
		case 3: // 2010
			return new GiftData(ItemList.fireworks, 99.0f);
		case 4: // 2011
			return new GiftData(ItemList.gardenGnome, 1.0f, (byte) 99);
		case 5: // 2012
			return new GiftData(ItemList.santaHat, 99.0f);
		case 6: // 2013
			return new GiftData(ItemList.snowLantern, 99.0f, (byte) 99);
		case 7: // 2014
			return new GiftData(ItemList.yuleGoat, 99.0f);
		case 8: // 2015
			return new GiftData(ItemList.yuleReindeer, 99.0f);
		case 9: // 2016
			return new GiftData(ItemList.xmasLunchbox, 99.0f);
		}
	}

	public OpenPresentActionPerformer(Function<Byte, GiftData> giftDataSupplier) {
		this.giftDataSupplier = giftDataSupplier;
	}

	@Override
	public short getActionId() {
		return Actions.OPEN;
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
		if (target.getTemplateId() == ItemList.present) {
			return this.action(action, performer, target, num, counter);
		}
		return ActionPerformer.super.action(action, performer, source, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item target, short num, float counter) {
		if (target.getTemplateId() == ItemList.present) {
			if (!performer.isWithinDistanceTo(target, 4.0f)) {
				return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION);
			}

			Communicator comm = performer.getCommunicator();

			try {
				final Item parent = Items.getItem(target.getParentId());
				parent.dropItem(target.getWurmId(), false);

				final GiftData data = giftDataSupplier.apply(target.getAuxData());
				final Item gift = ItemFactory.createItem(data.templateId, data.quality, performer.getName());
				parent.insertItem(gift, true);
				comm.sendSafeServerMessage("There is something inside with your name on it!");

				gift.setAuxData(data.auxdata);
				data.doAfter.accept(performer, gift);

				Items.decay(target.getWurmId(), target.getDbStrings());
				SoundPlayer.playSong("sound.music.song.christmas", performer);
			} catch (NoSuchTemplateException nst) {
				LOGGER.log(Level.WARNING, performer.getName() + " Christmas present template gone? " + nst.getMessage(), (Throwable) nst);
			} catch (NoSuchItemException nsi) {
				LOGGER.log(Level.WARNING, performer.getName() + " Christmas present loss: " + nsi.getMessage(), (Throwable) nsi);
			} catch (FailedException fe) {
				LOGGER.log(Level.WARNING, performer.getName() + " receives no Christmas present: " + fe.getMessage(), (Throwable) fe);
			}
			
			// The item is destroyed. Do not propagate the action to the server or any other mods
			return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
		}
		return ActionPerformer.super.action(action, performer, target, num, counter);
	}
}
