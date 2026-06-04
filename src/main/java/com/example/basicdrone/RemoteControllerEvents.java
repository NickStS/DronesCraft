package com.example.basicdrone;

import com.example.basicdrone.entity.DroneEntity;
import com.example.basicdrone.item.RemoteControllerItem;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = BasicDroneMod.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE)
public class RemoteControllerEvents {
    @SubscribeEvent
    public static void onEntityInteract(EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof RemoteControllerItem)) return;

        if (event.getTarget() instanceof DroneEntity drone) {
            stack.getOrCreateTag()
                    .putUUID(RemoteControllerItem.TAG_LINKED_DRONE_POS,
                            drone.getUUID());
            event.getPlayer().sendMessage(
                    new TextComponent("Контроллер привязан к дрону!"),
                    Util.NIL_UUID
            );
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
