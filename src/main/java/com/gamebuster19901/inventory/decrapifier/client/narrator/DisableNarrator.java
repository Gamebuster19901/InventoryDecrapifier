package com.gamebuster19901.inventory.decrapifier.client.narrator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class DisableNarrator extends NarratorChatListener{

	public DisableNarrator() {
		try {
			Field instanceField = ReflectionHelper.findField(NarratorChatListener.class, "INSTANCE", "field_193643_a");
			Field modifiersOfInstanceField = ReflectionHelper.findField(Field.class, "modifiers");
			modifiersOfInstanceField.setInt(instanceField, instanceField.getModifiers() & ~Modifier.FINAL);
			instanceField.set(null, this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}
	
    public void announceMode(int p_193641_1_)
    {
       
    }

    public boolean isActive()
    {
        return false;
    }

    public void clear()
    {
        
    }
	
}
