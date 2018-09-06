package org.gotti.wurmunlimited.modsupport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;

public class ItemTemplateBuilder {

	private int templateId;
	private String name;
	private String plural;
	private int size = 3;
	private String itemDescriptionSuperb = "superb"; 
	private String itemDescriptionNormal = "good";
	private String itemDescriptionBad = "ok";
	private String itemDescriptionRotten = "poor";
	private String itemDescriptionLong;
	private short[] itemTypes;
	private short imageNumber;
	private short behaviourType;
	private int combatDamage = 0;
	private long decayTime = 9072000L;
	private int centimetersX;
	private int centimetersY;
	private int centimetersZ;
	private int primarySkill = -10;
	private byte[] bodySpaces = MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY;
	private String modelName;
	private float difficulty;
	private int weightGrams;
	private byte material;
	private int value = 0;
	private boolean isTraded = false;
	private int dyeAmountOverrideGrams = 0;
	private boolean hasContainerSizes = false;
	private int containerSizeX;
	private int containerSizeY;
	private int containerSizeZ;
	private int maxItemCount = -1;
	private int maxItemWeight = -1;
	private int dyePrimaryAmountRequired = 0;
	private int dyeSecondaryAmountRequired = 0;
	private String secondaryItemName;
	private int foodGroup;
	private boolean hasNutritionValues;
	private int calories;
	private int carbs;
	private int fats;
	private int proteins;
	private int alcoholStrength;
	private int grows;
	private int crushsTo;
	private int harvestTo;
	private int pickSeeds;

	public ItemTemplateBuilder(String identifier) {
		this.templateId = IdFactory.getIdFor(identifier, IdType.ITEMTEMPLATE);
	}

	public ItemTemplateBuilder name(String name, String plural, String description) {
		this.name = name;
		this.plural = plural;
		this.itemDescriptionLong = description;
		return this;
	}
	
	public ItemTemplateBuilder size(int size) {
		this.size = size;
		return this;
	}

	public ItemTemplateBuilder descriptions(String itemDescriptionSuperb, String itemDescriptionNormal, String itemDescriptionBad, String itemDescriptionRotten) {
		this.itemDescriptionSuperb = itemDescriptionSuperb;
		this.itemDescriptionNormal = itemDescriptionNormal;
		this.itemDescriptionBad = itemDescriptionBad;
		this.itemDescriptionRotten = itemDescriptionRotten;
		return this;
	}

	public ItemTemplateBuilder itemTypes(short[] itemTypes) {
		this.itemTypes = itemTypes;
		return this;
	}

	public ItemTemplateBuilder weightGrams(int weightGrams) {
		this.weightGrams = weightGrams;
		return this;
	}

	public ItemTemplateBuilder material(byte material2) {
		this.material = material2;
		return this;
	}

	public ItemTemplateBuilder value(int value2) {
		this.value = value2;
		return this;
	}

	public ItemTemplateBuilder isTraded(boolean isTraded2) {
		this.isTraded = isTraded2;
		return this;
	}

	@Deprecated
	public ItemTemplateBuilder armourType(int armourType2) {
		return this;
	}
	
	public ItemTemplateBuilder difficulty(float difficulty2) {
		this.difficulty = difficulty2;
		return this;
	}

	public ItemTemplateBuilder modelName(String modelName2) {
		this.modelName = modelName2;
		return this;
	}

	public ItemTemplateBuilder bodySpaces(byte[] bodySpaces2) {
		this.bodySpaces = bodySpaces2;
		return this;
	}

	public ItemTemplateBuilder primarySkill(int primarySkill2) {
		this.primarySkill = primarySkill2;
		return this;
	}

	public ItemTemplateBuilder dimensions(int centimetersX2, int centimetersY2, int centimetersZ2) {
		this.centimetersX = centimetersX2;
		this.centimetersY = centimetersY2;
		this.centimetersZ = centimetersZ2;
		return this;
	}

	public ItemTemplateBuilder decayTime(long decayTime2) {
		this.decayTime = decayTime2;
		return this;
	}

	public ItemTemplateBuilder combatDamage(int combatDamage2) {
		this.combatDamage = combatDamage2;
		return this;
	}

	public ItemTemplateBuilder behaviourType(short behaviourType2) {
		this.behaviourType = behaviourType2;
		return this;
	}

	public ItemTemplateBuilder imageNumber(short imageNumber) {
		this.imageNumber = imageNumber;
		return this;
	}

	public ItemTemplateBuilder dyeAmountOverrideGrams(short dyeAmountOverrideGrams) {
		this.dyeAmountOverrideGrams = dyeAmountOverrideGrams;
		return this;
	}

	public ItemTemplateBuilder containerSize(int sizeX, int sizeY, int sizeZ) {
		this.hasContainerSizes = true;
		this.containerSizeX = sizeX;
		this.containerSizeY = sizeY;
		this.containerSizeZ = sizeZ;
		return this;
	}

	public ItemTemplateBuilder maxItemCount(int maxItemCount) {
		this.maxItemCount = maxItemCount;
		return this;
	}

	public ItemTemplateBuilder maxItemWeight(int maxItemWeight) {
		this.maxItemWeight = maxItemWeight;
		return this;
	}

	public ItemTemplateBuilder dyePrimaryAmountGrams(int dyePrimaryAmountRequired) {
		this.dyePrimaryAmountRequired = dyePrimaryAmountRequired;
		return this;
	}

	public ItemTemplateBuilder dyeSecondaryAmountGrams(int dyeSecondaryAmountRequired) {
		this.dyeSecondaryAmountRequired = dyeSecondaryAmountRequired;
		return this;
	}

	public ItemTemplateBuilder secondaryItemName(String secondaryItemName) {
		this.secondaryItemName = secondaryItemName;
		return this;
	}
	
	public ItemTemplateBuilder foodGroup(final int foodGroupTemplateId) {
		this.foodGroup = foodGroupTemplateId;
		return this;
	}
	
	public ItemTemplateBuilder nutritionValues(final int calories, final int carbs, final int fats, final int proteins) {
		this.hasNutritionValues = true;
		this.calories = calories;
		this.carbs = carbs;
		this.fats = fats;
		this.proteins = proteins;
		return this;
	}
	
	public ItemTemplateBuilder alcoholStrength(final int newAlcoholStrength) {
		this.alcoholStrength = newAlcoholStrength;
		return this;
	}

	public ItemTemplateBuilder grows(final int growsTemplateId) {
		this.grows = growsTemplateId;
		return this;
	}

	public ItemTemplateBuilder setCrushsTo(final int toTemplateId) {
		this.crushsTo = toTemplateId;
		return this;
	}

	public ItemTemplateBuilder harvestsTo(final int toTemplateId) {
		this.harvestTo = toTemplateId;
		return this;
	}
	
	public ItemTemplateBuilder pickSeeds(final int seedTemplateId) {
		this.pickSeeds = seedTemplateId;
		return this;
	}
	
	public ItemTemplate build(final String name, int size, final String plural, final String itemDescriptionSuperb, final String itemDescriptionNormal, final String itemDescriptionBad, final String itemDescriptionRotten, final String itemDescriptionLong, final short[] itemTypes, final short imageNumber,
			final short behaviourType, final int combatDamage, final long decayTime, final int centimetersX, final int centimetersY, final int centimetersZ, final int primarySkill, final byte[] bodySpaces, final String modelName, final float difficulty, final int weightGrams, final byte material, int value, boolean isTraded, int armourType)
			throws IOException {

		this.name(name, plural, itemDescriptionLong);
		this.size(size);
		this.descriptions(itemDescriptionSuperb, itemDescriptionNormal, itemDescriptionBad, itemDescriptionRotten);
		this.itemTypes(itemTypes);
		this.imageNumber(imageNumber);
		this.behaviourType(behaviourType);
		this.combatDamage(combatDamage);
		this.decayTime(decayTime);
		this.dimensions(centimetersX, centimetersY, centimetersZ);
		this.primarySkill(primarySkill);
		this.bodySpaces(bodySpaces);
		this.modelName(modelName);
		this.difficulty(difficulty);
		this.weightGrams(weightGrams);
		this.material(material);
		this.value(value);
		this.isTraded(isTraded);
		this.armourType(armourType);

		return build();

	}

	public ItemTemplate build() throws IOException {
		try {
			ItemTemplate template = ItemTemplateFactory.getInstance().createItemTemplate(templateId, size, name, plural, itemDescriptionSuperb, itemDescriptionNormal, itemDescriptionBad, itemDescriptionRotten, itemDescriptionLong, itemTypes, imageNumber, behaviourType, combatDamage, decayTime, centimetersX,
					centimetersY, centimetersZ, primarySkill, bodySpaces, modelName, difficulty, weightGrams, material, value, isTraded, dyeAmountOverrideGrams);
	
			if (hasContainerSizes) {
				template.setContainerSize(containerSizeX, containerSizeY, containerSizeZ);
			}
			if (maxItemCount >= 0) {
				template.setMaxItemCount(maxItemCount);
			}
			if (maxItemWeight >= 0) {
				template.setMaxItemWeight(maxItemWeight);
			}
	
			template.setDyeAmountGrams(dyePrimaryAmountRequired);
			if (secondaryItemName != null) {
				template.setSecondryItem(secondaryItemName, dyeSecondaryAmountRequired);
			}
			
			if (hasNutritionValues) {
				template.setNutritionValues(calories, carbs, fats, proteins);
			}
			
			if (alcoholStrength > 0) {
				ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "setAlcoholStrength"), alcoholStrength);
			}
			
			if (foodGroup > 0) {
				ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "setFoodGroup"), foodGroup);
			}
			
			if (crushsTo > 0) {
				ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "setCrushsTo"), crushsTo);
			}
			
			if (pickSeeds > 0) {
				ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "setPickSeeds"), pickSeeds);
			}
			
			if (grows > 0) {
				ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "setGrows"), grows);
			}
			
			if (harvestTo > 0) {
				ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "setHarvestsTo"), harvestTo);
			}
			
			return template;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

}
