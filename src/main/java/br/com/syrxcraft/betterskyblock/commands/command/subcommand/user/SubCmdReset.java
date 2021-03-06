package br.com.syrxcraft.betterskyblock.commands.command.subcommand.user;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.commands.CommandManager;
import br.com.syrxcraft.betterskyblock.commands.manager.cSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.HasSubCommand;
import br.com.syrxcraft.betterskyblock.commands.manager.ISubCommand;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.Cooldown;
import br.com.syrxcraft.betterskyblock.utils.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@HasSubCommand
public class SubCmdReset implements ISubCommand {

    @cSubCommand(name = "reset", targetCommand = "island")
    public boolean execute(CommandSender commandSender, String command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        if(!player.hasPermission(PermissionNodes.COMMAND_RESET)){
            return CommandManager.noPermission(player);
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null) {
            commandSender.sendMessage("§4§l ▶ §cVocê ainda não possui uma ilha nesse servidor! Para criar uma, use \"/" + command + " spawn\"");
            return false;
        }


        if(Cooldown.isInCooldown(player, "COMMANDS_RESET")){
            player.sendMessage("§3Você precisa esperar " + TimeUtils.formatSec((int) Cooldown.getCooldownTimeSec(player, "COMMANDS_RESET"))  + " para executar este comando.");
            return false;
        }


        if (!island.ready) {
            commandSender.sendMessage("§4§l ▶ §cExiste alguma operação pendente nessa ilha!");
            return false;
        }

        String conf = BetterSkyBlock.getInstance().getCommandManager().getConfirmations().remove(player.getName());

        if (conf == null || !conf.equals("reset")) {
            commandSender.sendMessage("§4§l ▶ §c§lCUIDADO: §cSua ilha inteira será APAGADA!\n§cSe você tem certeza disso, use \"/"+command+" reset\" novamente!");
            BetterSkyBlock.getInstance().getCommandManager().getConfirmations().put(player.getName(), "reset");
            return false;
        }

        Cooldown.setCooldown(player, 1800L, "COMMANDS_RESET");

        island.reset();
        return true;
    }
}
