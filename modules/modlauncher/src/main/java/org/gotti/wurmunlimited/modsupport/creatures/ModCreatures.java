package org.gotti.wurmunlimited.modsupport.creatures;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviours;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.Traits;

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
		Custom7(30),
		Custom8(31),
		Custom9(32),
		Custom10(33),
		Custom11(34),
		Custom12(35),
		Custom13(36),
		Custom14(37),
		Custom15(38),
		Custom16(39),
		Custom17(40),
		;
		
		private int number;

		private CustomTrait(int number) {
			this.number = number;
		}
		
		public int getTraitNumber() {
			return number;
		}
		
		public String getTraitName() {
			return name();
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
							neutralTraits[customTrait.getTraitNumber()] = true;;
						}
						
						return null;
					}
				};
			}
		});
		
		try {
			// com.wurmonline.server.questions.GmSetTraits.sendQuestion()
			classPool.get("com.wurmonline.server.questions.GmSetTraits").getMethod("sendQuestion", "()V").instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getClassName().equals("com.wurmonline.server.creatures.Traits") && m.getMethodName().equals("getTraitString")) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("{\n");
						buffer.append("String name = null;\n");
						buffer.append("org.gotti.wurmunlimited.modsupport.creatures.ModCreature c = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getModCreature(creature.getTemplate().getTemplateId());\n");
						for (CustomTrait customTrait : CustomTrait.values()) {
							buffer.append("if (c != null && $1 == " + customTrait.getTraitNumber() + ") { name = c.getTraitName(" + customTrait.getTraitNumber() + "); $_ = name != null ? name : \"\"; } else \n");
						}
						buffer.append("$_ = $proceed($$);\n");
						buffer.append("}");
						m.replace(buffer.toString());
					}
				}
			});
		
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
		
		try {
			final CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");

			{
				// com.wurmonline.server.creatures.Creature.getModelName()
				String code = "{ String name = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getModelName($0); if (name != null) { return name; }; }";
				ctCreature.getMethod("getModelName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[0])).insertBefore(code);
			}
			
			{
				// com.wurmonline.server.creatures.Creature.getColourName()
				String code = "{ String colour = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getColourName($0); if (colour != null) { return colour; }; }";
				ctCreature.getMethod("getColourName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[0])).insertBefore(code);
			}
			
			ctCreature.getMethod("die", "(ZLjava/lang/String;)V").instrument(new ExprEditor() {
				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if (f.getClassName().equals("com.wurmonline.server.creatures.CreatureTemplate") && f.getFieldName().equals("isHorse")) {
						f.replace("{ String name = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getTraitName(this); if (name != null) { corpse.setDescription(name); $_ = false; } else { $_ = $proceed($$); } }");
					}
				}
				
			});
			
			ctCreature.getMethod("mate", Descriptor.ofMethod(classPool.get("void"), new CtClass[] {ctCreature, ctCreature})).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getClassName().equals("com.wurmonline.server.creatures.Traits") && m.getMethodName().equals("calcNewTraits") && m.getSignature().equals("(DZJJ)J")) {
						m.replace("$_ = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures#calcNewTraits($1, $2, this, father);");
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
								
								code.append("{");
								code.append("if (org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.hasTraits($0.getTemplate().getTemplateId())) { ");
								code.append("org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.assignTraits($0);");
								code.append("$_ = false;");
								code.append("} else {");
								code.append("$_ = $proceed($$);");
								code.append("}");
								code.append("}");
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
		for (CustomTrait customTrait : CustomTrait.values()) {
			if (trait == customTrait.getTraitNumber()) {
				return true;
			}
		}
		return false;
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
}
