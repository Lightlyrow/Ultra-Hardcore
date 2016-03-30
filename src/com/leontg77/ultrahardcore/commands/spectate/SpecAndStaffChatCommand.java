package com.leontg77.ultrahardcore.commands.spectate;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SpecAndStaffChatCommand extends UHCCommand {
    private final SpecManager spec;
    private final CanAccessChatPredicate predicate = new CanAccessChatPredicate();

    public SpecAndStaffChatCommand(SpecManager spec) {
        super("specandstaffchat", "<message>");

        this.spec = spec;
    }

    private static final String PREFIX = "�dSpec And Staff Chat �8� �d";

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!predicate.apply(sender)) {
            throw new CommandException("You must be either spectating or a staff member.");
        }

        if (args.length == 0) {
            return false;
        }

        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!predicate.apply(online)) {
                continue;
            }

            online.sendMessage(PREFIX + sender.getName() + "�8: �7" + message);
        }

        Bukkit.getLogger().info(message);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    protected class CanAccessChatPredicate implements Predicate<CommandSender> {
        @Override
        public boolean apply(@Nullable CommandSender sender) {
            if (sender == null) return false;

            boolean isStaff = sender.hasPermission("uhc.staff");
            boolean isSpectating = false;

            if (sender instanceof Player) {
                Player player = (Player) sender;
                isSpectating = spec.isSpectating(player);
            }

            return isStaff || isSpectating;
        }
    }
}