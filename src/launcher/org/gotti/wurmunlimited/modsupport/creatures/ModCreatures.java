package org.gotti.wurmunlimited.modsupport.creatures;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviours;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.Traits;

public class ModCreatures {

	private static List<ModCreature> creatures = new LinkedList<>();
	private static Map<Integer, ModCreature> creaturesById = new HashMap<>();
	private static boolean inited;
	
	private static Map<Integer, String> customTraits = new LinkedHashMap<>();
	
	static {
		customTraits.put(24, "Custom1");
		customTraits.put(25, "Custom2");
		customTraits.put(26, "Custom3");
		customTraits.put(27, "Custom4");
		customTraits.put(28, "Custom5");
		customTraits.put(29, "Custom6");
		customTraits.put(30, "Custom7");
		customTraits.put(31, "Custom8");
		customTraits.put(32, "Custom9");
		customTraits.put(33, "Custom10");
	}
	
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
						
						for (Entry<Integer, String> entry : customTraits.entrySet()) {
							neutralTraits[entry.getKey()] = true;;
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
						for (Entry<Integer, String> entry : customTraits.entrySet()) {
							buffer.append("if ($1 == " + entry.getKey() + ") { $_ = \"" + entry.getValue() + "\"; } else \n");
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
			// com.wurmonline.server.creatures.Creature.getModelName()
			String code = "{ String name = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getModelName($0); if (name != null) { return name; }; }";
			classPool.get("com.wurmonline.server.creatures.Creature").getMethod("getModelName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[0])).insertBefore(code);
		
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
		
		
		try {
			// com.wurmonline.server.creatures.Creature.getModelName()
			String code = "{ String name = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getModelName($0); if (name != null) { return name; }; }";
			classPool.get("com.wurmonline.server.creatures.Creature").getMethod("getModelName", Descriptor.ofMethod(classPool.get("java.lang.String"), new CtClass[0])).insertBefore(code);
			
			classPool.get("com.wurmonline.server.creatures.Creature").getMethod("die", "(Z)V").instrument(new ExprEditor() {
				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if (f.getClassName().equals("com.wurmonline.server.creatures.CreatureTemplate") && f.getFieldName().equals("isHorse")) {
						f.replace("{ String name = org.gotti.wurmunlimited.modsupport.creatures.ModCreatures.getTraitName(this); if (name != null) { corpse.setDescription(name); $_ = false; } else { $_ = $proceed($$); } }");
					}
				}
				
			});
			
			for (CtMethod method : classPool.get("com.wurmonline.server.creatures.Creature").getMethods()) {
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
	
	public static boolean hasTraits(int templateId) {
		return creaturesById.get(templateId) != null && creaturesById.get(templateId).hasTraits();
	}

	public static String getTraitName(Creature creature) {
		ModCreature c = creaturesById.get(creature.getTemplate().getTemplateId());
		if (c != null && c.hasTraits()) {
			for (Entry<Integer, String> entry : customTraits.entrySet()) {
				Integer trait = entry.getKey();
				if (creature.hasTrait(trait)) {
					String name = c.getTraitName(trait);
					return name != null ? name : entry.getValue();
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


}
