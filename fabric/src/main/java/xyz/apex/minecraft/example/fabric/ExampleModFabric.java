package xyz.apex.minecraft.example.fabric;

import net.fabricmc.api.ModInitializer;

import xyz.apex.minecraft.example.common.ExampleMod;

public final class ExampleModFabric implements ExampleMod, ModInitializer
{
    @Override
    public void onInitialize()
    {
        setup();
    }
}
