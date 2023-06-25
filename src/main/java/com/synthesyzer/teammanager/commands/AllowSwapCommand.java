package com.synthesyzer.teammanager.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class AllowSwapCommand {

    public static boolean AllowSwaps = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("teammanager")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("allowswaps").executes(context -> {
                    AllowSwaps = !AllowSwaps;

                    String message = AllowSwaps ? "Team swaps are now allowed" : "Team swaps are no longer allowed";
                    context.getSource().sendFeedback(Text.of(message), true);
                    
                    return 1;
                })));
    }

}
