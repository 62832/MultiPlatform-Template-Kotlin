package xyz.apex.minecraft.example.forge;

import dev.architectury.platform.forge.EventBuses;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import xyz.apex.minecraft.example.common.ExampleMod;

@Mod(ExampleMod.ID)
public final class ExampleModForge implements ExampleMod
{
    public ExampleModForge()
    {
        EventBuses.registerModEventBus(ID, FMLJavaModLoadingContext.get().getModEventBus());
        setup();
    }
}
