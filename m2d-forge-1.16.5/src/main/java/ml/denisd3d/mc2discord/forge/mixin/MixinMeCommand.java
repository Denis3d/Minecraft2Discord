package ml.denisd3d.mc2discord.forge.mixin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.denisd3d.mc2discord.core.M2DUtils;
import ml.denisd3d.mc2discord.core.Mc2Discord;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.MeCommand;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeCommand.class)
public class MixinMeCommand {
    @SuppressWarnings("target")
    @Inject(method = {"lambda$register$2(Lcom/mojang/brigadier/context/CommandContext;)I", "func_198365_a(Lcom/mojang/brigadier/context/CommandContext;)I"}, at = @At("RETURN"), remap = false)
    private static void lambda(CommandContext<CommandSource> commandContext, CallbackInfoReturnable<Integer> ci) {
        if (Mc2Discord.INSTANCE.config.relay_say_me_command && M2DUtils.canHandleEvent()) {
            TranslationTextComponent translationtextcomponent = new TranslationTextComponent("chat.type.emote",
                    commandContext.getSource().getDisplayName(), StringArgumentType.getString(commandContext, "action"));
            Mc2Discord.INSTANCE.messageManager.sendInfoMessage(translationtextcomponent.getString());
        }
    }
}
