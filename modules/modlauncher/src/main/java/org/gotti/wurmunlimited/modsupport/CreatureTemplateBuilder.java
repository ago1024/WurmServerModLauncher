package org.gotti.wurmunlimited.modsupport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wurmonline.server.combat.ArmourTemplate;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;

import com.wurmonline.server.creatures.AttackAction;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.skills.SkillsFactory;
import com.wurmonline.shared.constants.ItemMaterials;

public class CreatureTemplateBuilder {
	
	private static class RefHelper {

		private final Field reputation;
		private final Field hasHands;
		private final Field isHorse;
		private final Method createCreatureTemplate;
		private final Method setAlignment;
		private final Method setDenMaterial;
		private final Method setDenName;
		private final Method setMaxGroupAttackSize;
		private final Method setBaseCombatRating;
		private final Method setArmourType;
		private final Method setMaxAge;
		private final Method setKickDamString;

		public RefHelper() {
			try {
				reputation = ReflectionUtil.getField(CreatureTemplate.class, "reputation");
				hasHands = ReflectionUtil.getField(CreatureTemplate.class, "hasHands");
				isHorse = ReflectionUtil.getField(CreatureTemplate.class, "isHorse");
				createCreatureTemplate = ReflectionUtil.getMethod(CreatureTemplateFactory.class, "createCreatureTemplate");
				setAlignment = ReflectionUtil.getMethod(CreatureTemplate.class, "setAlignment");
				setDenMaterial = ReflectionUtil.getMethod(CreatureTemplate.class, "setDenMaterial");
				setDenName = ReflectionUtil.getMethod(CreatureTemplate.class, "setDenName");
				setMaxGroupAttackSize = ReflectionUtil.getMethod(CreatureTemplate.class, "setMaxGroupAttackSize");
				setBaseCombatRating = ReflectionUtil.getMethod(CreatureTemplate.class, "setBaseCombatRating");
				setArmourType = ReflectionUtil.getMethod(CreatureTemplate.class, "setArmourType");
				setMaxAge = ReflectionUtil.getMethod(CreatureTemplate.class, "setMaxAge");
				setKickDamString = ReflectionUtil.getMethod(CreatureTemplate.class, "setKickDamString");
			} catch (NoSuchFieldException | NoSuchMethodException e) {
				throw new HookException(e);
			}
		}
	}
	
	private static final RefHelper REFHELPER = new RefHelper();
	
	private int templateId;

	private Map<Integer, Float> skills = new HashMap<>();

	private int[] types;

	private String name, plural;

	private String description;

	private int maxAge;

	private float baseCombatRating;

	private int maxGroupAttackSize;

	private String denName;

	private byte denMaterial;

	private float maxPercentOfCreatures;

	private boolean usesNewAttacks;

	private ArmourTemplate.ArmourType armourType = ArmourTemplate.ARMOUR_TYPE_CLOTH;

	private byte bodyType;

	private List<AttackAction> primaryAttackActions = new LinkedList<>();

	private List<AttackAction> secondaryAttackActions = new LinkedList<>();

	private String modelName;

	private float maxX;

	private float minX;

	private float minY;

	private float maxY;

	private String handDamString;

	private String kickDamString;

	private String headbuttDamString;

	private short vision;

	private byte sex;

	private short centimetersHigh;

	private short centimetersLong;

	private short centimetersWide;

	private String deathSndMale;

	private String deathSndFemale;

	private String hitSndMale;

	private String hitSndFemale;

	private float naturalArmour;

	private float handDam;

	private float kickDam;

	private float biteDam;

	private float headDam;

	private float breathDam;

	private float speed;

	private int moveRate;

	private int[] itemsButchered;

	private int maxHuntDist;

	private int aggressive;

	private boolean hasBounds;

	private byte combatDamageType;

	private float alignment;

	private boolean isHorse;

	private byte meatMaterial;

	private int colorRed = 255;

	private int colorGreen = 255;

	private int colorBlue = 255;

	private int sizeModX = 64;

	private int sizeModY = 64;

	private int sizeModZ = 64;

	private byte fireRadius;

	private boolean onFire;

	private boolean glowing;

	private int[] combatMoves;

	private boolean isEggLayer;

	private int eggTemplate;

	private int childTemplate;

	private byte daysOfPregnancy;

	private boolean hasHands;

	private boolean keepSex;

	private int maxPopulationOfCreatures;

	private int paintMode;

	private float bonusCombatRating;

	private float fireResistance;

	private float coldResistance;

	private float diseaseResistance;

	private float physicalResistance;

	private float pierceResistance;

	private float slashResistance;

	private float crushResistance;

	private float biteResistance;

	private float poisonResistance;

	private float waterResistance;

	private float acidResistance;

	private float internalResistance;

	private float fireVulnerability;

	private float coldVulnerability;

	private float diseaseVulnerability;

	private float physicalVulnerability;

	private float pierceVulnerability;

	private float slashVulnerability;

	private float crushVulnerability;

	private float biteVulnerability;

	private float poisonVulnerability;

	private float waterVulnerability;

	private float acidVulnerability;

	private float internalVulnerability;

	private int leaderTemplateId = -1;

	private float offZ;

	private int reputation = 100;

	public CreatureTemplateBuilder(int id) {
		this.templateId = id;
		defaultSkills();
	}

	public CreatureTemplateBuilder(String identifier) {
		this(IdFactory.getIdFor(identifier, IdType.CREATURETEMPLATE));
	}

	public CreatureTemplateBuilder(final String identifier, final String name, final String description, final String modelName, final int[] types, final byte bodyType, final short vision, final byte sex, final short centimetersHigh, final short centimetersLong, final short centimetersWide,
			final String deathSndMale, final String deathSndFemale, final String hitSndMale, final String hitSndFemale, final float naturalArmour, final float handDam, final float kickDam, final float biteDam, final float headDam, final float breathDam, final float speed, final int moveRate,
			final int[] itemsButchered, final int maxHuntDist, final int aggress) {
		this(identifier, name, description, modelName, types, bodyType, vision, sex, centimetersHigh, centimetersLong, centimetersWide, deathSndMale, deathSndFemale, hitSndMale, hitSndFemale, naturalArmour, handDam, kickDam, biteDam, headDam, breathDam, speed, moveRate, itemsButchered, maxHuntDist, aggress, ItemMaterials.MATERIAL_MEAT);
	}

	public CreatureTemplateBuilder(final String identifier, final String name, final String description, final String modelName, final int[] types, final byte bodyType, final short vision, final byte sex, final short centimetersHigh, final short centimetersLong, final short centimetersWide,
			final String deathSndMale, final String deathSndFemale, final String hitSndMale, final String hitSndFemale, final float naturalArmour, final float handDam, final float kickDam, final float biteDam, final float headDam, final float breathDam, final float speed, final int moveRate,
			final int[] itemsButchered, final int maxHuntDist, final int aggress, final byte meatMaterial) {
		this(identifier);
		name(name);
		description(description);
		modelName(modelName);
		types(types);
		bodyType(bodyType);
		vision(vision);
		sex(sex);
		dimension(centimetersHigh, centimetersLong, centimetersWide);
		deathSounds(deathSndMale, deathSndFemale);
		hitSounds(hitSndMale, hitSndFemale);
		naturalArmour(naturalArmour);
		damages(handDam, kickDam, biteDam, headDam, breathDam);
		speed(speed);
		moveRate(moveRate);
		itemsButchered(itemsButchered);
		maxHuntDist(maxHuntDist);
		aggressive(aggress);
		meatMaterial(meatMaterial);
	}

	public CreatureTemplateBuilder damages(float handDam2, float kickDam2, float biteDam2, float headDam2, float breathDam2) {
		this.handDam = handDam2;
		this.kickDam = kickDam2;
		this.biteDam = biteDam2;
		this.headDam = headDam2;
		this.breathDam = breathDam2;
		return this;
	}

	public CreatureTemplateBuilder speed(float speed) {
		this.speed = speed;
		return this;
	}

	public CreatureTemplateBuilder moveRate(int moveRate) {
		this.moveRate = moveRate;
		return this;
	}

	public CreatureTemplateBuilder itemsButchered(int[] itemsButchered) {
		this.itemsButchered = itemsButchered;
		return this;
	}

	public CreatureTemplateBuilder maxHuntDist(int maxHuntDist) {
		this.maxHuntDist = maxHuntDist;
		return this;
	}

	public CreatureTemplateBuilder aggressive(int aggress) {
		this.aggressive = aggress;
		return this;
	}

	public CreatureTemplateBuilder meatMaterial(byte meatMaterial) {
		this.meatMaterial = meatMaterial;
		return this;
	}

	public CreatureTemplateBuilder naturalArmour(float naturalArmour2) {
		this.naturalArmour = naturalArmour2;
		return this;
	}

	public CreatureTemplateBuilder hitSounds(String hitSndMale2, String hitSndFemale2) {
		this.hitSndMale = hitSndMale2;
		this.hitSndFemale = hitSndFemale2;
		return this;
	}

	public CreatureTemplateBuilder deathSounds(String deathSndMale, String deathSndFemale) {
		this.deathSndMale = deathSndMale;
		this.deathSndFemale = deathSndFemale;
		return this;
	}

	public CreatureTemplateBuilder dimension(short centimetersHigh2, short centimetersLong2, short centimetersWide2) {
		this.centimetersHigh = centimetersHigh2;
		this.centimetersLong = centimetersLong2;
		this.centimetersWide = centimetersWide2;
		return this;
	}

	public CreatureTemplateBuilder sex(byte sex) {
		this.sex = sex;
		return this;
	}

	public CreatureTemplateBuilder vision(short vision) {
		this.vision = vision;
		return this;
	}

	public CreatureTemplateBuilder bodyType(byte bodyType) {
		this.bodyType = bodyType;
		return this;
	}

	public CreatureTemplateBuilder modelName(String modelName) {
		this.modelName = modelName;
		return this;
	}

	public CreatureTemplateBuilder defaultSkills() {
		skills.put(102, 20.0f);
		skills.put(104, 20.0f);
		skills.put(103, 20.0f);
		skills.put(100, 20.0f);
		skills.put(101, 20.0f);
		skills.put(105, 20.0f);
		skills.put(106, 20.0f);
		return this;
	}

	public CreatureTemplateBuilder skill(int skillNumber, float startValue) {
		this.skills.put(skillNumber, startValue);
		return this;
	}

	public CreatureTemplateBuilder types(int[] types) {
		this.types = types;
		return this;
	}

	public CreatureTemplateBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CreatureTemplateBuilder plural(String plural) {
		this.plural = plural;
		return this;
	}

	public CreatureTemplateBuilder description(String description) {
		this.description = description;
		return this;
	}

	public CreatureTemplateBuilder addPrimaryAttack(AttackAction attackAction) {
		primaryAttackActions.add(attackAction);
		return this;
	}

	public CreatureTemplateBuilder addSecondaryAttack(AttackAction attackAction) {
		secondaryAttackActions.add(attackAction);
		return this;
	}

	/**
	 * Set headbutt damage string
	 * @param headbuttDamString headbutt damage string
	 */
	public CreatureTemplateBuilder headbuttDamString(String headbuttDamString) {
		this.headbuttDamString = headbuttDamString;
		return this;
	}

	/**
	 * Creature is an egg layer
	 * @param eggTemplate Egg template. -1 disabled egg laying
	 */
	public CreatureTemplateBuilder eggLayer(int eggTemplate) {
		this.isEggLayer = eggTemplate != -1;
		this.eggTemplate = eggTemplate;
		return this;
	}
	
	/**
	 * Set days of pregnancy
	 * @param daysOfPregnancy days of pregnancy
	 * @return this
	 */
	public CreatureTemplateBuilder daysOfPregnancy(byte daysOfPregnancy) {
		this.daysOfPregnancy = daysOfPregnancy;
		return this;
	}
	
	/**
	 * Set child template id
	 * 
	 * @param childTemplate child template id
	 * @return this
	 */
	public CreatureTemplateBuilder childTemplate(int childTemplate) {
		this.childTemplate = childTemplate;
		return this;
	}

	public CreatureTemplate build() {
		try {
			final Skills skills = SkillsFactory.createSkills(name);
			for (Entry<Integer, Float> skillEntry : this.skills.entrySet()) {
				skills.learnTemp(skillEntry.getKey(), skillEntry.getValue());
			}

			final CreatureTemplate temp = createCreatureTemplate(templateId, name, plural==null ? name+"s" : plural, description, modelName, types, bodyType, skills, vision, sex, centimetersHigh, centimetersLong, centimetersWide, deathSndMale, deathSndFemale, hitSndMale, hitSndFemale, naturalArmour, handDam, kickDam, biteDam,
					headDam, breathDam, speed, moveRate, itemsButchered, maxHuntDist, aggressive, meatMaterial);

			if (hasBounds)
				temp.setBoundsValues(minX, minY, maxX, maxY);

			if (this.handDamString != null)
				temp.setHandDamString(handDamString);

			if (this.kickDamString != null)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setKickDamString, kickDamString);

			if (this.headbuttDamString != null) {
				temp.setHeadbuttDamString(headbuttDamString);
			}

			if (maxAge > 0)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setMaxAge, maxAge);

			if (armourType != null)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setArmourType, armourType);

			if (baseCombatRating > 0)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setBaseCombatRating, baseCombatRating);

			if (combatDamageType > 0)
				temp.combatDamageType = combatDamageType;

			if (maxGroupAttackSize > 0)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setMaxGroupAttackSize, maxGroupAttackSize);

			if (denName != null)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setDenName, denName);

			if (denMaterial > 0)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setDenMaterial, denMaterial);

			if (maxPercentOfCreatures > 0)
				temp.setMaxPercentOfCreatures(maxPercentOfCreatures);

			if (alignment != 0)
				ReflectionUtil.callPrivateMethod(temp, REFHELPER.setAlignment, alignment);

			if (isHorse)
				ReflectionUtil.setPrivateField(temp, REFHELPER.isHorse, isHorse);

			if (usesNewAttacks)
				temp.setUsesNewAttacks(usesNewAttacks);

			for (AttackAction attackAction : primaryAttackActions) {
				temp.addPrimaryAttack(attackAction);
			}
			for (AttackAction attackAction : secondaryAttackActions) {
				temp.addSecondaryAttack(attackAction);
			}

			temp.setColorRed(colorRed);
			temp.setColorGreen(colorGreen);
			temp.setColorBlue(colorBlue);

			temp.setSizeModX(sizeModX);
			temp.setSizeModY(sizeModY);
			temp.setSizeModZ(sizeModZ);
			
			temp.offZ = offZ;

			temp.setGlowing(glowing);
			if (onFire) {
				temp.setOnFire(onFire);
				temp.setFireRadius(fireRadius);
			}
			if (combatMoves != null) {
				temp.setCombatMoves(combatMoves);
			}

			if (isEggLayer) {
				temp.setEggLayer(this.isEggLayer);
				temp.setEggTemplateId(this.eggTemplate);
			}
			
			if (childTemplate != 0) {
				temp.setChildTemplateId(childTemplate);
			}
			
			if (daysOfPregnancy != 0) {
				temp.setDaysOfPregnancy(daysOfPregnancy);
			}

			if (hasHands) {
				ReflectionUtil.setPrivateField(temp, REFHELPER.hasHands, hasHands);
			}

			temp.setKeepSex(keepSex);

			if (maxPopulationOfCreatures > 0) {
				temp.setMaxPopulationOfCreatures(maxPopulationOfCreatures);
			}

			temp.setPaintMode(paintMode);

			temp.setBonusCombatRating(bonusCombatRating);

			temp.fireResistance = fireResistance;
			temp.coldResistance = coldResistance;
			temp.diseaseResistance = diseaseResistance;
			temp.physicalResistance = physicalResistance;
			temp.pierceResistance = pierceResistance;
			temp.slashResistance = slashResistance;
			temp.crushResistance = crushResistance;
			temp.biteResistance = biteResistance;
			temp.poisonResistance = poisonResistance;
			temp.waterResistance = waterResistance;
			temp.acidResistance = acidResistance;
			temp.internalResistance = internalResistance;

			temp.fireVulnerability = fireVulnerability;
			temp.coldVulnerability = coldVulnerability;
			temp.diseaseVulnerability = diseaseVulnerability;
			temp.physicalVulnerability = physicalVulnerability;
			temp.pierceVulnerability = pierceVulnerability;
			temp.slashVulnerability = slashVulnerability;
			temp.crushVulnerability = crushVulnerability;
			temp.biteVulnerability = biteVulnerability;
			temp.poisonVulnerability = poisonVulnerability;
			temp.waterVulnerability = waterVulnerability;
			temp.acidVulnerability = acidVulnerability;
			temp.internalVulnerability = internalVulnerability;

			temp.setLeaderTemplateId(leaderTemplateId);
			
			if (this.reputation != 100) {
				ReflectionUtil.setPrivateField(temp, REFHELPER.reputation, reputation);
			}
			
			

			return temp;
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
			throw new HookException(e);
		}
	}

	private static CreatureTemplate createCreatureTemplate(final int id, final String name, final String plural, final String longDesc, final String modelName, final int[] types, final byte bodyType, final Skills skills, final short vision, final byte sex, final short centimetersHigh, final short centimetersLong,
			final short centimetersWide, final String deathSndMale, final String deathSndFemale, final String hitSndMale, final String hitSndFemale, final float naturalArmour, final float handDam, final float kickDam, final float biteDam, final float headDam, final float breathDam,
			final float speed, final int moveRate, final int[] itemsButchered, final int maxHuntDist, final int aggress, final byte meatMaterial) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return ReflectionUtil.callPrivateMethod(CreatureTemplateFactory.getInstance(), REFHELPER.createCreatureTemplate, id, name, plural, longDesc, modelName, types, bodyType, skills, vision, sex, centimetersHigh, centimetersLong, centimetersWide,
				deathSndMale, deathSndFemale, hitSndMale, hitSndFemale, naturalArmour, handDam, kickDam, biteDam, headDam, breathDam, speed, moveRate, itemsButchered, maxHuntDist, aggress, meatMaterial);
	}

	public CreatureTemplateBuilder boundsValues(final float minX, final float minY, final float maxX, final float maxY) {
		this.hasBounds = true;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;

	}

	public CreatureTemplateBuilder handDamString(String handDamString) {
		this.handDamString = handDamString;
		return this;
	}

	public CreatureTemplateBuilder kickDamString(String kickDamString) {
		this.kickDamString = kickDamString;
		return this;
	}

	public CreatureTemplateBuilder maxAge(int i) {
		maxAge = i;
		return this;
	}

	@Deprecated
	public CreatureTemplateBuilder armourType(int i) {
		switch (i) {
		case 1:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_LEATHER;
			break;
		case 2:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_STUDDED;
			break;
		case 3:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_CHAIN;
			break;
		case 4:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_PLATE;
			break;
		case 5:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_RING;
			break;
		case 6:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_CLOTH;
			break;
		case 7:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_SCALE;
			break;
		case 8:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_SPLINT;
			break;
		case 9:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON;
			break;
		case 10:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON;
			break;
		default:
			this.armourType = ArmourTemplate.ARMOUR_TYPE_CLOTH;
		}
		return this;
	}

	public CreatureTemplateBuilder armourType(ArmourTemplate.ArmourType t) {
		this.armourType = t;
		return this;
	}

	public CreatureTemplateBuilder baseCombatRating(float f) {
		this.baseCombatRating = f;
		return this;
	}

	public CreatureTemplateBuilder combatDamageType(byte i) {
		combatDamageType = i;
		return this;
	}

	public CreatureTemplateBuilder maxGroupAttackSize(int i) {
		maxGroupAttackSize = i;
		return this;
	}

	public CreatureTemplateBuilder denName(String string) {
		this.denName = string;
		return this;
	}

	public CreatureTemplateBuilder denMaterial(byte b) {
		denMaterial = b;
		return this;
	}

	public CreatureTemplateBuilder maxPercentOfCreatures(float f) {
		maxPercentOfCreatures = f;
		return this;
	}

	public CreatureTemplateBuilder usesNewAttacks(boolean b) {
		usesNewAttacks = b;
		return this;
	}

	public CreatureTemplateBuilder alignment(float f) {
		alignment = f;
		return this;
	}

	public CreatureTemplateBuilder isHorse(boolean b) {
		isHorse = b;
		return this;
	}

	/**
	 * Set size modifier. The default value is 64 for all values.
	 *
	 * @param sizeModX size modifier
	 * @param sizeModY size modifier
	 * @param sizeModZ size modifier
	 * @return builder
	 */
	public CreatureTemplateBuilder sizeModifier(int sizeModX, int sizeModY, int sizeModZ) {
		this.sizeModX = sizeModX;
		this.sizeModY = sizeModY;
		this.sizeModZ = sizeModZ;
		return this;
	}
	
	/**
	 * Set z-offset.
	 * The default is 0.0
	 * 
	 * @param offZ z-offset
	 * @return this
	 */
	public CreatureTemplateBuilder offZ(float offZ) {
		this.offZ = offZ;
		return this;
	}

	/**
	 * Set a custom color.
	 *
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public CreatureTemplateBuilder color(int red, int green, int blue) {
		this.colorRed = red;
		this.colorGreen = green;
		this.colorBlue = blue;
		return this;
	}

	/**
	 * Set glowing status.
	 * @param glowing
	 * @return
	 */
	public CreatureTemplateBuilder glowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}

	/**
	 * Set fire status
	 * @param onFire onFire flag
	 * @param fireRadius fire radius to use if onFire is set
	 * @return
	 */
	public CreatureTemplateBuilder onFire(boolean onFire, byte fireRadius) {
		this.onFire = onFire;
		this.fireRadius = fireRadius;
		return this;
	}

	public CreatureTemplateBuilder setCombatMoves(int[] aCombatMoves) {
		this.combatMoves = aCombatMoves;
		return this;
	}

	public int getTemplateId() {
		return templateId;
	}

	public CreatureTemplateBuilder hasHands(boolean hasHands) {
		this.hasHands = hasHands;
		return this;
	}

	public CreatureTemplateBuilder keepSex(boolean keepSex) {
		this.keepSex = keepSex;
		return this;
	}

	public CreatureTemplateBuilder maxPopulationOfCreatures(int maxPopulationOfCreatures) {
		this.maxPopulationOfCreatures = maxPopulationOfCreatures;
		return this;
	}

	public CreatureTemplateBuilder paintMode(int paintMode) {
		this.paintMode = paintMode;
		return this;
	}

	public CreatureTemplateBuilder bonusCombatRating(int bonusCombatRating) {
		this.bonusCombatRating = baseCombatRating;
		return this;
	}

	public CreatureTemplateBuilder fireResistance(float fireResistance) {
		this.fireResistance = fireResistance;
		return this;
	}

	public CreatureTemplateBuilder coldResistance(float coldResistance) {
		this.coldResistance = coldResistance;
		return this;
	}

	public CreatureTemplateBuilder diseaseResistance(float diseaseResistance) {
		this.diseaseResistance = diseaseResistance;
		return this;
	}

	public CreatureTemplateBuilder physicalResistance(float physicalResistance) {
		this.physicalResistance = physicalResistance;
		return this;
	}

	public CreatureTemplateBuilder pierceResistance(float pierceResistance) {
		this.pierceResistance = pierceResistance;
		return this;
	}

	public CreatureTemplateBuilder slashResistance(float slashResistance) {
		this.slashResistance = slashResistance;
		return this;
	}

	public CreatureTemplateBuilder crushResistance(float crushResistance) {
		this.crushResistance = crushResistance;
		return this;
	}

	public CreatureTemplateBuilder biteResistance(float biteResistance) {
		this.biteResistance = biteResistance;
		return this;
	}

	public CreatureTemplateBuilder poisonResistance(float poisonResistance) {
		this.poisonResistance = poisonResistance;
		return this;
	}

	public CreatureTemplateBuilder waterResistance(float waterResistance) {
		this.waterResistance = waterResistance;
		return this;
	}

	public CreatureTemplateBuilder acidResistance(float acidResistance) {
		this.acidResistance = acidResistance;
		return this;
	}

	public CreatureTemplateBuilder internalResistance(float internalResistance) {
		this.internalResistance = internalResistance;
		return this;
	}

	public CreatureTemplateBuilder fireVulnerability(float fireVulnerability) {
		this.fireVulnerability = fireVulnerability;
		return this;
	}

	public CreatureTemplateBuilder coldVulnerability(float coldVulnerability) {
		this.coldVulnerability = coldVulnerability;
		return this;
	}

	public CreatureTemplateBuilder diseaseVulnerability(float diseaseVulnerability) {
		this.diseaseVulnerability = diseaseVulnerability;
		return this;
	}

	public CreatureTemplateBuilder physicalVulnerability(float physicalVulnerability) {
		this.physicalVulnerability = physicalVulnerability;
		return this;
	}

	public CreatureTemplateBuilder pierceVulnerability(float pierceVulnerability) {
		this.pierceVulnerability = pierceVulnerability;
		return this;
	}

	public CreatureTemplateBuilder slashVulnerability(float slashVulnerability) {
		this.slashVulnerability = slashVulnerability;
		return this;
	}

	public CreatureTemplateBuilder crushVulnerability(float crushVulnerability) {
		this.crushVulnerability = crushVulnerability;
		return this;
	}

	public CreatureTemplateBuilder biteVulnerability(float biteVulnerability) {
		this.biteVulnerability = biteVulnerability;
		return this;
	}

	public CreatureTemplateBuilder poisonVulnerability(float poisonVulnerability) {
		this.poisonVulnerability = poisonVulnerability;
		return this;
	}

	public CreatureTemplateBuilder waterVulnerability(float waterVulnerability) {
		this.waterVulnerability = waterVulnerability;
		return this;
	}

	public CreatureTemplateBuilder acidVulnerability(float acidVulnerability) {
		this.acidVulnerability = acidVulnerability;
		return this;
	}

	public CreatureTemplateBuilder internalVulnerability(float internalVulnerability) {
		this.internalVulnerability = internalVulnerability;
		return this;
	}

	public CreatureTemplateBuilder leaderTemplateId(int leaderTemplateId) {
		this.leaderTemplateId = leaderTemplateId;
		return this;
	}
	
	public CreatureTemplateBuilder reputation(int reputation) {
		this.reputation = reputation;
		return this;
	}
}
