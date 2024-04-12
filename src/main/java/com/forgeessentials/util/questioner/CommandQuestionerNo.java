package com.forgeessentials.util.questioner;

import net.minecraft.commands.CommandSourceStack;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.permissions.DefaultPermissionLevel;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandQuestionerNo extends ForgeEssentialsCommandBuilder
{
    public CommandQuestionerNo(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "no";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "decline" };
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        Questioner.answer(getServerPlayer(ctx.getSource()), false);
        return Command.SINGLE_SUCCESS;
    }
}
