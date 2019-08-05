package me.tassu.arrowcounter.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Mixin(InGameHud.class)
public abstract class HudMixin extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getFontRenderer();

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Inject(at = @At("RETURN"), method = "render")
    public void render(float float_1, CallbackInfo ci) {
        if (this.client.options.hudHidden) return;
        if (this.client.player.isSpectator()) return;

        AtomicBoolean crossbowLoaded = new AtomicBoolean(false);

        if (StreamSupport.stream(this.client.player.getItemsHand().spliterator(), false)
            .peek(it -> {
                if (it.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(it)) {
                    crossbowLoaded.set(true);
                }
            })
            .map(ItemStack::getItem)
            .noneMatch(it -> it == Items.BOW || it == Items.CROSSBOW)) {
            return;
        }

        int count = IntStream.range(0, this.client.player.inventory.getInvSize()).boxed()
                .map(this.client.player.inventory::getInvStack)
                .filter(it -> it.getItem() == Items.ARROW)
                .mapToInt(ItemStack::getCount)
                .sum();

        String string = String.valueOf(count);

        TextRenderer renderer = this.getFontRenderer();

        renderer.drawWithShadow(string,
                (this.scaledWidth - renderer.getStringWidth(string)) / 2,
                this.scaledHeight - 47,
                crossbowLoaded.get() ? 0x54FC00 : 0xFCE700);
    }

}
