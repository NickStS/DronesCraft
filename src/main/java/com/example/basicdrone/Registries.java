package com.example.basicdrone;

import com.example.basicdrone.block.CableBlock;
import com.example.basicdrone.block.ChargingStationBlock;
import com.example.basicdrone.block.DroneBlock;
import com.example.basicdrone.block.EnergyStorageBlock;
import com.example.basicdrone.block.GeneratorBlock;
import com.example.basicdrone.block.entity.CableBlockEntity;
import com.example.basicdrone.block.entity.ChargingStationBlockEntity;
import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.example.basicdrone.block.entity.EnergyStorageBlockEntity;
import com.example.basicdrone.block.entity.GeneratorBlockEntity;
import com.example.basicdrone.entity.DroneEntity;
import com.example.basicdrone.entity.client.GeckoDroneRenderer;
import com.example.basicdrone.item.RemoteControllerItem;
import com.example.basicdrone.menu.EnergyStorageMenu;
import com.example.basicdrone.menu.GeneratorMenu;
import com.example.basicdrone.menu.RemoteControllerMenu;
import com.example.basicdrone.item.DroneItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registries {
    public static final DeferredRegister<Block>            BLOCKS       = DeferredRegister.create(ForgeRegistries.BLOCKS,       BasicDroneMod.MODID);
    public static final DeferredRegister<Item>             ITEMS        = DeferredRegister.create(ForgeRegistries.ITEMS,        BasicDroneMod.MODID);
    public static final DeferredRegister<EntityType<?>>    ENTITIES     = DeferredRegister.create(ForgeRegistries.ENTITIES,     BasicDroneMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BasicDroneMod.MODID);
    public static final DeferredRegister<MenuType<?>>      CONTAINERS   = DeferredRegister.create(ForgeRegistries.CONTAINERS,   BasicDroneMod.MODID);
    public static final DeferredRegister<SoundEvent>       SOUNDS       = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BasicDroneMod.MODID);

    public static final RegistryObject<Block> CHARGING_STATION_BLOCK =
            BLOCKS.register("charging_station", ChargingStationBlock::new);
    public static final RegistryObject<Block> GENERATOR_BLOCK =
            BLOCKS.register("generator", GeneratorBlock::new);
    public static final RegistryObject<Block> ENERGY_STORAGE_BLOCK =
            BLOCKS.register("energy_storage", EnergyStorageBlock::new);
    public static final RegistryObject<Block> CABLE_BLOCK =
            BLOCKS.register("cable", CableBlock::new);
    public static final RegistryObject<Block> DRONE_BLOCK =
            BLOCKS.register("drone", DroneBlock::new);


    public static final RegistryObject<Item> CHARGING_STATION_BLOCK_ITEM =
            ITEMS.register("charging_station", () ->
                    new BlockItem(CHARGING_STATION_BLOCK.get(),
                            new Item.Properties().tab(BasicDroneMod.TAB))
            );
    public static final RegistryObject<Item> GENERATOR_BLOCK_ITEM =
            ITEMS.register("generator", () ->
                    new BlockItem(GENERATOR_BLOCK.get(),
                            new Item.Properties().tab(BasicDroneMod.TAB))
            );
    public static final RegistryObject<Item> ENERGY_STORAGE_BLOCK_ITEM =
            ITEMS.register("energy_storage", () ->
                    new BlockItem(ENERGY_STORAGE_BLOCK.get(),
                            new Item.Properties().tab(BasicDroneMod.TAB))
            );
    public static final RegistryObject<Item> CABLE_BLOCK_ITEM =
            ITEMS.register("cable", () ->
                    new BlockItem(CABLE_BLOCK.get(),
                            new Item.Properties().tab(BasicDroneMod.TAB))
            );
    public static final RegistryObject<Item> DRONE_ITEM =
            ITEMS.register("drone",
                    () -> new DroneItem(DRONE_BLOCK.get())
            );
    public static final RegistryObject<Item> REMOTE_CONTROLLER_ITEM =
            ITEMS.register("remote_controller",
                    () -> new RemoteControllerItem(new Item.Properties()
                            .stacksTo(1)
                            .tab(BasicDroneMod.TAB)
                    )
            );

    public static final RegistryObject<EntityType<DroneEntity>> DRONE_ENTITY =
            ENTITIES.register("drone", () ->
                    EntityType.Builder.of(DroneEntity::new, MobCategory.MISC)
                            .sized(1.0f, 0.5f)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(ResourceLocation.parse(BasicDroneMod.MODID + ":drone").toString())
            );

    public static final RegistryObject<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION_BLOCK_ENTITY =
            TILE_ENTITIES.register("charging_station", () ->
                    BlockEntityType.Builder.of(
                            ChargingStationBlockEntity::new,
                            CHARGING_STATION_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BLOCK_ENTITY =
            TILE_ENTITIES.register("generator", () ->
                    BlockEntityType.Builder.of(
                            GeneratorBlockEntity::new,
                            GENERATOR_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<EnergyStorageBlockEntity>> ENERGY_STORAGE_BLOCK_ENTITY =
            TILE_ENTITIES.register("energy_storage", () ->
                    BlockEntityType.Builder.of(
                            EnergyStorageBlockEntity::new,
                            ENERGY_STORAGE_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE_BLOCK_ENTITY =
            TILE_ENTITIES.register("cable", () ->
                    BlockEntityType.Builder.of(
                            CableBlockEntity::new,
                            CABLE_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<DroneBlockEntity>> DRONE_BLOCK_ENTITY =
            TILE_ENTITIES.register("drone", () ->
                    BlockEntityType.Builder.of(
                            DroneBlockEntity::new,
                            DRONE_BLOCK.get()
                    ).build(null)
            );


    public static final RegistryObject<MenuType<GeneratorMenu>> GENERATOR_MENU =
            CONTAINERS.register("generator", () ->
                    IForgeMenuType.create((windowId, inv, buf) -> {
                        var pos = buf.readBlockPos();
                        var te  = (GeneratorBlockEntity)inv.player.level.getBlockEntity(pos);
                        return new GeneratorMenu(windowId, inv, te);
                    })
            );
    public static final RegistryObject<MenuType<EnergyStorageMenu>> ENERGY_STORAGE_MENU =
            CONTAINERS.register("energy_storage", () ->
                    IForgeMenuType.create((windowId, inv, buf) -> {
                        var pos = buf.readBlockPos();
                        var te  = (EnergyStorageBlockEntity)inv.player.level.getBlockEntity(pos);
                        return new EnergyStorageMenu(windowId, inv, te);
                    })
            );
    public static final RegistryObject<MenuType<RemoteControllerMenu>> REMOTE_CONTROLLER_MENU =
            CONTAINERS.register("remote_controller", () ->
                    IForgeMenuType.create((windowId, inv, buf) -> {
                        int battery = buf.readInt();
                        boolean flying = buf.readBoolean();
                        return new RemoteControllerMenu(windowId, inv, battery, flying);
                    })
            );


    public static final RegistryObject<SoundEvent> CHARGER_WORKING =
            SOUNDS.register("charger_working", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":charger_working"))
            );
    public static final RegistryObject<SoundEvent> GENERATOR_WORKING =
            SOUNDS.register("generator_working", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":generator_working"))
            );
    public static final RegistryObject<SoundEvent> DRONE_CHARGED =
            SOUNDS.register("drone_charged", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":drone_charged"))
            );
    public static final RegistryObject<SoundEvent> DRONE_LIFT =
            SOUNDS.register("drone_lift", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":drone_lift"))
            );
    public static final RegistryObject<SoundEvent> DRONE_LOW_BATTERY =
            SOUNDS.register("drone_low_battery", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":drone_low_battery"))
            );
    public static final RegistryObject<SoundEvent> DRONE_SHOOT =
            SOUNDS.register("drone_shoot", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":drone_shoot"))
            );
    public static final RegistryObject<SoundEvent> DRONE_SPOT =
            SOUNDS.register("drone_spot", () ->
                    new SoundEvent(ResourceLocation.parse(BasicDroneMod.MODID + ":drone_spot"))
            );

    public static void registerAll(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        TILE_ENTITIES.register(bus);
        CONTAINERS.register(bus);
        SOUNDS.register(bus);
    }

    @Mod.EventBusSubscriber(
            modid = BasicDroneMod.MODID,
            bus   = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers evt) {
            evt.registerEntityRenderer(
                    DRONE_ENTITY.get(),
                    GeckoDroneRenderer::new
            );
        }
    }
}
