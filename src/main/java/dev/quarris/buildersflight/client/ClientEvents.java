package dev.quarris.buildersflight.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.quarris.buildersflight.BuildersFlight;
import dev.quarris.buildersflight.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BuildersFlight.ID)
public class ClientEvents {

    private static long startTime = System.currentTimeMillis();
    private static boolean demoEnded = false;



    @SubscribeEvent
    public static void demo(TickEvent.ClientTickEvent event) {
        if (BuildersFlight.isDemo && System.currentTimeMillis() - startTime > 30 * 60 * 1000 && !demoEnded) {
            demoEnded = true;
            Minecraft.getInstance().displayGuiScreen(new Screen(new StringTextComponent("Demo Ended")) {
                boolean firstUpd = true;

                @Override
                public void tick() {
                    if (this.firstUpd) {
                        this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 10, 150, 20, new TranslationTextComponent("menu.quit"), (p_213047_1_) -> {
                            this.minecraft.shutdown();
                        }));
                        firstUpd = false;
                    }
                }

                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    this.renderBackground(matrixStack);
                    drawCenteredString(matrixStack, this.font, this.title, this.width / 2, this.height / 2 - 60, 16777215);
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }

                public boolean shouldCloseOnEsc() {
                    return false;
                }
            });
        }
    }

}
