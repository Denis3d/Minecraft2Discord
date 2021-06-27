package ml.denisd3d.mc2discord.forge.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.denisd3d.mc2discord.core.M2DUtils;
import ml.denisd3d.mc2discord.core.Mc2Discord;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.command.impl.SayCommand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SayCommand.class)
public class MixinSayCommand {
    @SuppressWarnings("target")
    @Inject(method = {"lambda$register$1(Lcom/mojang/brigadier/context/CommandContext;)I", "func_198626_a(Lcom/mojang/brigadier/context/CommandContext;)I"}, at = @At("RETURN"), remap = false)
    private static void lambda(CommandContext<CommandSource> commandContext, CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
        if (Mc2Discord.INSTANCE.config.relay_say_me_command && M2DUtils.canHandleEvent()) {
            ITextComponent itextcomponent = MessageArgument.getMessage(commandContext, "message");
            TranslationTextComponent translationtextcomponent = new TranslationTextComponent("chat.type.announcement", commandContext.getSource().getDisplayName(), itextcomponent);
            Mc2Discord.INSTANCE.messageManager.sendInfoMessage(translationtextcomponent.getString());
        }
    }
}
