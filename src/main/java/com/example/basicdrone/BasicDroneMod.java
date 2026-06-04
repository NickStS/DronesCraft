package com.example.basicdrone;

import com.example.basicdrone.client.KeyBindings;
import com.example.basicdrone.client.gui.RemoteControllerScreen;
import com.example.basicdrone.entity.client.GeckoDroneRenderer;
import com.example.basicdrone.network.NetworkHandler;
import com.example.basicdrone.screen.EnergyStorageScreen;
import com.example.basicdrone.screen.GeneratorScreen;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(BasicDroneMod.MODID)
public class BasicDroneMod {
    public static final String MODID = "basicdrone";

    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registries.GENERATOR_BLOCK_ITEM.get());
        }
    };

    public BasicDroneMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        Registries.registerAll(bus);

        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onRegisterEntityAttributes);

        MinecraftForge.EVENT_BUS.register(KeyBindings.class);
        MinecraftForge.EVENT_BUS.register(RemoteControllerEvents.class);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(
                Registries.GENERATOR_MENU.get(),
                GeneratorScreen::new
        );
        MenuScreens.register(
                Registries.ENERGY_STORAGE_MENU.get(),
                EnergyStorageScreen::new
        );
        MenuScreens.register(
                Registries.REMOTE_CONTROLLER_MENU.get(),
                RemoteControllerScreen::new
        );

        KeyBindings.register();
    }

    private void onRegisterEntityAttributes(EntityAttributeCreationEvent evt) {
        AttributeSupplier.Builder builder = DroneEntity.createAttributes();
        evt.put(Registries.DRONE_ENTITY.get(), builder.build());
    }

    @Mod.EventBusSubscriber(
            modid = MODID,
            bus   = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientEntityRenderers {
        @SubscribeEvent
        public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
            evt.registerEntityRenderer(
                    Registries.DRONE_ENTITY.get(),
                    GeckoDroneRenderer::new
            );
        }
    }
}
