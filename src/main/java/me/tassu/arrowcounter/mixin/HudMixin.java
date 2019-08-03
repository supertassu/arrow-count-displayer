package me.tassu.arrowcounter.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.StreamSupport;

@Mixin(InGameHud.class)
public abstract class HudMixin extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getFontRenderer();

    @Inject(at = @At("RETURN"), method = "render")
    public void render(float float_1, CallbackInfo ci) {
        if (this.client.options.hudHidden) return;

        if (StreamSupport.stream(this.client.player.getItemsHand().spliterator(), false)
            .map(ItemStack::getItem)
            .noneMatch(it -> it == Items.BOW || it == Items.CROSSBOW)) {
            return;
        }

        TextRenderer renderer = this.getFontRenderer();
        renderer.drawWithShadow("test", 1f, 1f, 0xFFFFFF);
    }

}
