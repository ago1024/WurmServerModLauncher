package org.gotti.wurmunlimited.modsupport.creatures;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviours;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.Traits;
import com.wurmonline.shared.util.StringUtilities;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class ModCreatures {
	
	enum CustomTrait {
		
		ebonyblack(23),
		piebaldpinto(24),
		bloodbay(25),
		Custom4(26),
		skewbaldpinto(30),
		goldbuckskin(31),
		blacksilver(32),
		appaloosa(33),
		chestnut(34),
		Custom12(35),
		Custom13(36),
		Custom14(37),
		Custom15(38),
		Custom16(39),
		Custom17(40),
		Custom18(41),
		Custom19(42),
		Custom20(43),
		Custom21(44),
		Custom22(45),
		Custom23(46),
		Custom24(47),
		Custom25(48),
		Custom26(49),
		Custom27(50),
		Custom28(51),
		Custom29(52),
		Custom30(53),
		Custom31(54),
		Custom32(55),
		Custom33(56),
		Custom34(57),
		Custom35(58),
		Custom36(59),
		Custom37(60),
		Custom38(61),
		Custom39(62),
		;
		
		private static long customTraits;
		private int number;
		
		static {
			customTraits = 0;
			for (CustomTrait trait : values()) {
				customTraits |= 1l << trait.getTraitNumber();
			}
		}

		private CustomTrait(int number) {
			this.number = number;
		}
		
		public int getTraitNumber() {
			return number;
		}
		
		public String getTraitName() {
			return name();
		}
		
		public static boolean isCustomTrait(int number) {
			if (number <= 0 || number > 63)
				return false;
			return (customTraits & (1l << number)) != 0;
		}
	}
	
	private static List<ModCreature> creatures = new LinkedList<>();
	private static Map<Integer, ModCreature> creaturesById = new HashMap<>();
	private static boolean inited;
	
	public static void init() {
		if (inited)
			return;
		
		final ClassPool classPool = HookManager.getInstance().getClassPool();
		try {
			CtClass ctEncounter = classPool.get("com.wurmonline.server.zones.Encounter");
			ctEncounter.setModifiers(Modifier.setPublic(ctEncounter.getModifiers()));
			CtConstructor ctConstructor = ctEncounter.getConstructor(Descriptor.ofConstructor(new CtClass[0]));
			ctConstructor.setModifiers(Modifier.setPublic(ctConstructor.getModifiers()));
		} catch (NotFoundException e) {
			throw new HookException(e);
		}

		ModVehicleBehaviours.init();

		// com.wurmonline.server.creatures.CreatureTemplateCreator.createCreatureTemplates()
		HookManager.getInstance().registerHook("com.wurmonline.server.creatures.CreatureTemplateCreator", "createCreatureTemplates", "()V", new InvocationHandlerFactory() {

			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						method.invoke(proxy, args);
						
						for (ModCreature creature : creatures) {
							CreatureTemplate creatureTemplate = creature.createCreateTemplateBuilder().build();
							creaturesById.put(creatureTemplate.getTemplateId(), creature);
							
							ModVehicleBehaviour vehicleBehaviour = creature.getVehicleBehaviour();
							if (vehicleBehaviour != null) {
								ModVehicleBehaviours.addCreatureVehicle(creatureTemplate.getTemplateId(), vehicleBehaviour);
							}
						}
						
						return null;
					}
				};
			}
		});
		
		// com.wurmonline.server.zones.SpawnTable.createEncounters()
		HookManager.getInstance().registerHook("com.wurmonline.server.zones.SpawnTable", "createEncounters", "()V", new InvocationHandlerFactory() {
			
			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						method.invoke(proxy, args);
						
						for (ModCreature creature : creatures) {
							creature.addEncounters();
						}
						
						return null;
					}
				};
			}
		});
		
		// com.wurmonline.server.creatures.Traits.initialiseTraits()
		HookManager.getInstance().registerHook("com.wurmonline.server.creatures.Traits", "initialiseTraits", "()V", new InvocationHandlerFactory() {
			
			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						method.invoke(proxy, args);
						
						boolean[] neutralTraits = ReflectionUtil.getPrivateField(Traits.class, ReflectionUtil.getField(Traits.class, "neutralTraits"));
						
						for (CustomTrait customTrait : CustomTrait.values()) {
							neutralTraits[customTrait.getTraitNumber()] = true;
						}
						
						return null;
					}
				};
			}
		});
		
		try {
			/**
			 * Replace call for Traits.getTraitString() with a call to GmSetTraitsCallbacks.getTraitString()
			 * where custom colors and custom traits are checked checked first before falling back to Traits.getTraitString()
			 */
			CtClass ctGmSetTraits = classPool.get("com.wurmonline.server.questions.GmSetTraits");
			HookManager.getInstance().addCallback(ctGmSetTraits, "modcreatures", new GmSetTraitsCallbacks());
			
			// com.wurmonline.server.questions.GmSetTraits.sendQuestion()
			ctGmSetTraits.getMethod("sendQuestion", "()V").instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getClassName().equals("com.wurmonline.server.creatures.Traits") && m.getMethodName().equals("getTraitString")) {
						m.replace("$_ = modcreatures.getTraitString(creature, $1);");
					}
				}
			});
		
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
		
		try {
			final CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
			HookManager.getInstance().addCallback(ctCreature, "modcreatures", new CreatureCallbacks());

			{
				// com.wurmonline.server.creatures.Creature.getModelName()
				String code = "{ String name = modcreatures.getModelName($0); if (name != null) { return name; }; }";
				ctCreature.getMethod("getModelName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[0])).insertBefore(code);
			}
			
			{
				// com.wurmonline.server.creatures.Creature.getColourName()
				String code = "{ String colour = modcreatures.getColourName($0); if (colour != null) { return colour; }; }";
				ctCreature.getMethod("getColourName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[0])).insertBefore(code);
			}
			
			{
				// com.wurmonline.server.creatures.Creature.getColourName(int trait)
				String code = "{ String colour = modcreatures.getColourName($0, $1); if (colour != null) { return colour; }; }";
				ctCreature.getMethod("getColourName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[] { classPool.get("int") })).insertBefore(code);
			}
			
			
			ctCreature.getMethod("die", "(ZLjava/lang/String;Z)V").instrument(new ExprEditor() {
				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if (f.getClassName().equals("com.wurmonline.server.creatures.CreatureTemplate") && f.getFieldName().equals("isHorse")) {
						f.replace("{ $_ = modcreatures.hasTraits(this.getTemplate().getTemplateId()) || $proceed($$); }");
					}
				}
				
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getClassName().equals("com.wurmonline.server.creatures.CreatureTemplate") && m.getMethodName().equals("getColourName")) {
						m.replace("{ String color = modcreatures.getColourName(this); if (color != null) { $_ = color; } else { $_ = $proceed($$); } }");
					}
				}
				
			});
			
			ctCreature.getMethod("mate", Descriptor.ofMethod(classPool.get("void"), new CtClass[] {ctCreature, ctCreature})).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getClassName().equals("com.wurmonline.server.creatures.Traits") && m.getMethodName().equals("calcNewTraits") && m.getSignature().equals("(DZJJ)J")) {
						m.replace("$_ = modcreatures.calcNewTraits($1, $2, this, father);");
					}
				}
			});
			
			
			for (CtMethod method : ctCreature.getMethods()) {
				if (method.getName().equals("doNew")) {
					method.instrument(new ExprEditor() {
						@Override
						public void edit(MethodCall m) throws CannotCompileException {
							if (m.getClassName().equals("com.wurmonline.server.creatures.Creature") && m.getMethodName().equals("isHorse")) {
								StringBuffer code = new StringBuffer();
								code.append("$_ = !modcreatures.assignTraits($0) && $proceed($$);");
								m.replace(code.toString());
							}
						}
					});
				}
			}
		
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
		
		

		inited = true;
	}
	
	public static void addCreature(ModCreature creature) {
		if (!inited) {
			throw new RuntimeException("ModCreatures was not initied");
		}
		creatures.add(creature);
	}
	
	public static ModCreature getModCreature(int templateId) {
		return creaturesById.get(templateId);
	}
	
	public static boolean hasTraits(int templateId) {
		return creaturesById.get(templateId) != null && creaturesById.get(templateId).hasTraits();
	}

	public static String getTraitName(Creature creature) {
		ModCreature c = creaturesById.get(creature.getTemplate().getTemplateId());
		if (c != null && c.hasTraits()) {
			for (CustomTrait customTrait : CustomTrait.values()) {
				int trait = customTrait.getTraitNumber();
				if (creature.hasTrait(trait)) {
					Optional<String> name = Optional.ofNullable(c.getTraitName(trait));
					return name.orElseGet(() -> customTrait.getTraitName());
				}
			}
		}
		return null;
	}
	
	public static String getColourName(Creature creature) {
		ModCreature c = creaturesById.get(creature.getTemplate().getTemplateId());
		if (c != null && c.hasTraits()) {
			for (CustomTrait customTrait : CustomTrait.values()) {
				int trait = customTrait.getTraitNumber();
				if (creature.hasTrait(trait)) {
					return c.getColourName(trait);
				}
			}
		}
		return null;
	}
	
	public static String getModelName(Creature creature) {
		String traitName = getTraitName(creature);
		if (traitName != null) {
			final StringBuilder s = new StringBuilder();
			s.append(creature.getTemplate().getModelName());
			s.append('.');
			s.append(traitName);
			if (creature.getStatus().getSex() == 0) {
				s.append(".male");
			}
			if (creature.getStatus().getSex() == 1) {
				s.append(".female");
			}
			if (creature.getStatus().disease > 0) {
				s.append(".diseased");
			}
			return s.toString();
		}
		return null;
	}
	
	public static void assignTraits(Creature creature) {
		ModCreature c = creaturesById.get(creature.getTemplate().getTemplateId());
		if (c != null && c.hasTraits()) {
			c.assignTraits(new TraitsSetter() {
				
				@Override
				public void setTraitBit(int i, boolean b) {
					creature.getStatus().setTraitBit(i, b);
				}
			});
		}
	}

	public static boolean isCustomTrait(int trait) {
		return CustomTrait.isCustomTrait(trait);
	}
	
	public static long calcNewTraits(final double breederSkill, final boolean inbred, final Creature mother, final Creature father) {
		
		long mothertraits = ModTraits.getTraits(mother);
		long fathertraits = ModTraits.getTraits(father);
		
		ModCreature modMother = ModCreatures.getModCreature(mother.getTemplate().getTemplateId());
		if (modMother == null || !modMother.hasTraits()) {
			return Traits.calcNewTraits(breederSkill, inbred, mothertraits, fathertraits);
		}
		
		return modMother.calcNewTraits(breederSkill, inbred, mothertraits, fathertraits);
	}
	
	private static class GmSetTraitsCallbacks {
		@CallbackApi
		public String getTraitString(Creature creature, int trait) {
			ModCreature modCreature = ModCreatures.getModCreature(creature.getTemplate().getTemplateId());
			if (modCreature != null && isCustomTrait(trait)) {
				String colorName = modCreature.getColourName(trait);
				if (colorName != null)
					return StringUtilities.raiseFirstLetterOnly(colorName);
				
				String traitName = modCreature.getTraitName(trait);
				if (traitName != null)
					return traitName;
			}
			
			return Traits.getTraitString(trait);
		}
	}
	
	private static class CreatureCallbacks {
		
		@CallbackApi
		public String getModelName(Creature creature) {
			return ModCreatures.getModelName(creature);
		}
		
		@CallbackApi
		public String getColourName(Creature creature) {
			return ModCreatures.getColourName(creature);
		}
		
		@CallbackApi
		public String getColourName(Creature creature, int trait) {
			ModCreature c = ModCreatures.getModCreature(creature.getTemplate().getTemplateId());
			if (c != null && c.hasTraits()) {
				// The creature has traits. Return the color name or an empty string
				String color = c.getColourName(trait);
				if (color != null) {
					return color;
				}
				return "";
			}
			return null;
		}
		
		
		@CallbackApi
		public String getTraitName() {
			return ModCreatures.getTraitName(null);
		}
		
		@CallbackApi
		public boolean hasTraits(int templateId) {
			return ModCreatures.hasTraits(templateId);
		}
		
		@CallbackApi
		public long calcNewTraits(final double breederSkill, final boolean inbred, final Creature mother, final Creature father) {
			return ModCreatures.calcNewTraits(breederSkill, inbred, mother, father);
		}
		
		/**
		 * Assign traits if the creature has custom traits.
		 * @return true if the creature has custom traits, false otherwise
		 */
		@CallbackApi
		public boolean assignTraits(Creature creature) {
			if (ModCreatures.hasTraits(creature.getTemplate().getTemplateId())) {
				ModCreatures.assignTraits(creature);
				return true;
			}
			return false;
		}
		
	}
}
