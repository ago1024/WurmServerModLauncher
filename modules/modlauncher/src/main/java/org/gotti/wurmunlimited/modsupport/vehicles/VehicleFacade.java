package org.gotti.wurmunlimited.modsupport.vehicles;

import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.items.Item;

public interface VehicleFacade {

	void setUnmountable(boolean b);

	void createOnlyPassengerSeats(int i);

	void setSeatFightMod(int i, float f, float g);

	void setCreature(boolean b);

	void setEmbarkString(String string);

	void setName(String name);

	void setMaxDepth(float f);

	void setMaxHeightDiff(float f);

	void setCommandType(byte i);

	void addHitchSeats(Seat[] hitches);

	void createPassengerSeats(int i);

	void setSeatOffset(int i, float f, float g, float h);

	void setSkillNeeded(float f);

	void setMaxSpeed(float f);

	void setCanHaveEquipment(boolean b);

	Item getItem() throws NoSuchItemException;

	long getWurmid();

}


