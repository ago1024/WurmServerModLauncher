package org.gotti.wurmunlimited.mods.hitchingpost;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviours;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.AdvancedCreationEntry;
import com.wurmonline.server.items.CreationCategories;
import com.wurmonline.server.items.CreationEntryCreator;
import com.wurmonline.server.items.CreationRequirement;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.skills.SkillList;

public class HitchingPostMod implements WurmMod, Initable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants {

	private static Logger logger = Logger.getLogger(HitchingPostMod.class.getName());
	private int hitchingPostId;

	@Override
	public void onItemTemplatesCreated() {
		logger.log(Level.INFO, "Adding hitchingpost");

		try {
			ItemTemplateBuilder itemTemplateBuilder = new ItemTemplateBuilder("ago.hitchingpost");
			itemTemplateBuilder.name("hitching post", "hitching posts", "A post used to tie up animals.");
			itemTemplateBuilder.descriptions("excellent", "good", "ok", "poor");
			itemTemplateBuilder.itemTypes(new short[] { 
					ITEM_TYPE_WOOD,
					ITEM_TYPE_NOTAKE,
					ITEM_TYPE_REPAIRABLE,
					ITEM_TYPE_TURNABLE,
					ITEM_TYPE_DECORATION,
					ITEM_TYPE_DESTROYABLE,
					ITEM_TYPE_ONE_PER_TILE,
					ITEM_TYPE_VEHICLE,
					ITEM_TYPE_IMPROVEITEM,
					ITEM_TYPE_OWNER_DESTROYABLE,
					ITEM_TYPE_OWNER_TURNABLE,
					ITEM_TYPE_OWNER_MOVEABLE
			});
			itemTemplateBuilder.imageNumber((short) 60);
			itemTemplateBuilder.behaviourType((short) 41);
			itemTemplateBuilder.combatDamage(0);
			itemTemplateBuilder.decayTime(9072000L);
			itemTemplateBuilder.dimensions(90, 10, 64);
			itemTemplateBuilder.primarySkill((int) NOID);
			itemTemplateBuilder.bodySpaces(EMPTY_BYTE_PRIMITIVE_ARRAY);
			itemTemplateBuilder.modelName("model.structure.hitchingpost.");
			itemTemplateBuilder.difficulty(5.0f);
			itemTemplateBuilder.weightGrams(70000);
			itemTemplateBuilder.material((byte) 14);
			ItemTemplate hitchingPostTemplate = itemTemplateBuilder.build();
			this.hitchingPostId = hitchingPostTemplate.getTemplateId();
			logger.log(Level.INFO, "Using template id " + hitchingPostId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onServerStarted() {
		
		if (hitchingPostId > 0) {
			final AdvancedCreationEntry creationEntry = CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.log, ItemList.plank, hitchingPostId, false, false, 0.0f, true, true, CreationCategories.ANIMAL_EQUIPMENT);
			creationEntry.addRequirement(new CreationRequirement(1, ItemList.log, 2, true));
			creationEntry.addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 5, true));
			creationEntry.addRequirement(new CreationRequirement(3, ItemList.plank, 2, true));
			creationEntry.addRequirement(new CreationRequirement(4, ItemList.horseShoe, 3, true));
			
			HitchingPostBehaviour hitchingPostBehaviour = new HitchingPostBehaviour();
			ModVehicleBehaviours.addItemVehicle(hitchingPostId, hitchingPostBehaviour);
		}
	}

	@Override
	public void init() {
		ModVehicleBehaviours.init();
	}
}
