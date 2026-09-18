package com.maor.roboticarm;

import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import com.maor.roboticarm.entity.MysteriousVillagerEntity;
import com.maor.roboticarm.menu.RoboticArmMenu;
import com.maor.roboticarm.network.ArmControlPayload;
import com.maor.roboticarm.worldgen.CircuitRuinFeature;
import com.maor.roboticarm.worldgen.DataVaultFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.Regions;
import com.maor.roboticarm.worldgen.DigitalHavenRegion;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(RoboticArm.MODID)
public final class RoboticArm {
    public static final String MODID = "roboticarm";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<net.minecraft.world.level.levelgen.feature.Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);
    public static final DeferredRegister<net.neoforged.neoforge.attachment.AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    private static final BlockBehaviour.Properties DECORATIVE = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE);
    public static final DeferredHolder<Block, Block> ABANDONED_CIRCUIT = BLOCKS.register("abandoned_circuit", () -> new Block(DECORATIVE));
    public static final DeferredHolder<Block, Block> CABLE_BLOCK = BLOCKS.register("cable_block", () -> new Block(DECORATIVE));
    public static final DeferredHolder<Block, Block> BROKEN_MACHINE = BLOCKS.register("broken_machine", () -> new Block(DECORATIVE));
    public static final DeferredHolder<Block, com.maor.roboticarm.block.RoboticArmBlock> ROBOTIC_ARM = BLOCKS.register("robotic_arm",
            () -> new com.maor.roboticarm.block.RoboticArmBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Item, Item> POWER_CORE = ITEMS.register("power_core", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> PROCESSING_CORE = ITEMS.register("processing_core", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ROBOTIC_ARM_BLUEPRINT = ITEMS.register("robotic_arm_blueprint",
            () -> new com.maor.roboticarm.item.BlueprintItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, BlockItem> ABANDONED_CIRCUIT_ITEM = blockItem("abandoned_circuit", ABANDONED_CIRCUIT);
    public static final DeferredHolder<Item, BlockItem> CABLE_BLOCK_ITEM = blockItem("cable_block", CABLE_BLOCK);
    public static final DeferredHolder<Item, BlockItem> BROKEN_MACHINE_ITEM = blockItem("broken_machine", BROKEN_MACHINE);
    public static final DeferredHolder<Item, BlockItem> ROBOTIC_ARM_ITEM = blockItem("robotic_arm", ROBOTIC_ARM);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RoboticArmBlockEntity>> ROBOTIC_ARM_BE =
            BLOCK_ENTITIES.register("robotic_arm", () -> BlockEntityType.Builder.of(RoboticArmBlockEntity::new, ROBOTIC_ARM.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<RoboticArmMenu>> ROBOTIC_ARM_MENU =
            MENUS.register("robotic_arm", () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(RoboticArmMenu::fromBuffer));
    public static final DeferredHolder<EntityType<?>, EntityType<MysteriousVillagerEntity>> MYSTERIOUS_VILLAGER =
            ENTITIES.register("mysterious_villager", () -> EntityType.Builder.of(MysteriousVillagerEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f).clientTrackingRange(8).build("mysterious_villager"));
    public static final DeferredHolder<Item, Item> MYSTERIOUS_VILLAGER_SPAWN_EGG = ITEMS.register("mysterious_villager_spawn_egg",
            () -> new net.minecraft.world.item.SpawnEggItem(MYSTERIOUS_VILLAGER.get(), 0x234d59, 0x72d2c0, new Item.Properties()));
    public static final DeferredHolder<net.neoforged.neoforge.attachment.AttachmentType<?>, net.neoforged.neoforge.attachment.AttachmentType<Integer>> MISSION_STAGE =
            ATTACHMENTS.register("mission_stage", () -> net.neoforged.neoforge.attachment.AttachmentType.builder(() -> 0)
                    .serialize(com.mojang.serialization.Codec.INT).copyOnDeath().build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("robotic_arm", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.roboticarm"))
            .icon(() -> new net.minecraft.world.item.ItemStack(ROBOTIC_ARM_ITEM.get()))
            .displayItems((parameters, output) -> {
                output.accept(POWER_CORE.get());
                output.accept(PROCESSING_CORE.get());
                output.accept(ROBOTIC_ARM_BLUEPRINT.get());
                output.accept(ROBOTIC_ARM_ITEM.get());
                output.accept(ABANDONED_CIRCUIT_ITEM.get());
                output.accept(CABLE_BLOCK_ITEM.get());
                output.accept(BROKEN_MACHINE_ITEM.get());
                output.accept(MYSTERIOUS_VILLAGER_SPAWN_EGG.get());
            }).build());
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, CircuitRuinFeature> CIRCUIT_RUIN_FEATURE =
            FEATURES.register("circuit_ruin", CircuitRuinFeature::new);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, DataVaultFeature> DATA_VAULT_FEATURE =
            FEATURES.register("data_vault", DataVaultFeature::new);

    private static DeferredHolder<Item, BlockItem> blockItem(String id, DeferredHolder<Block, ? extends Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }

    public RoboticArm(IEventBus modBus, ModContainer container) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENUS.register(modBus);
        ENTITIES.register(modBus);
        TABS.register(modBus);
        ATTACHMENTS.register(modBus);
        FEATURES.register(modBus);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(MysteriousVillagerEntity::registerAttributes);
        modBus.addListener(RoboticArm::registerSpawnPlacements);
        modBus.addListener(this::commonSetup);
        modBus.addListener(ArmControlPayload::register);
        NeoForge.EVENT_BUS.register(this);
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Regions.register(new DigitalHavenRegion()));
    }
    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(MYSTERIOUS_VILLAGER.get(), net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> level.getBlockState(pos.below()).isSolidRender(level, pos.below()),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ROBOTIC_ARM_BE.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ROBOTIC_ARM_BE.get(), (be, side) -> be.getStorageHandler());
    }
}
