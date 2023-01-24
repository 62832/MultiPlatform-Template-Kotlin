package xyz.apex.minecraft.example.common;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface ExampleMod
{
    String ID = "examplemod";

    DeferredRegister<Block> BLOCKS = DeferredRegister.create(ID, Registries.BLOCK);
    DeferredRegister<Item> ITEMS = DeferredRegister.create(ID, Registries.ITEM);

    RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties()));

    RegistrySupplier<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    RegistrySupplier<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.register(EXAMPLE_BLOCK.getRegistryId().getPath(), () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    default void setup()
    {
        BLOCKS.register();
        ITEMS.register();
    }
}
