package ca.koda3.spigot.chronometre;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {
	private static Logger logger;
	private static HashMap<UUID, Long> instances = new HashMap<>();

	public void onEnable() {
		this.logger = this.getLogger();
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getLogger().info("クロノメータープラグインロードしました");
		this.getCommand("ta").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equals("ta")) {
			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーにしか使用できません");
					return false;
				}
				UUID uuid = ((Player) sender).getUniqueId();
				sender.sendMessage(triggerTimer(uuid));
				return true;
			}
			if (args.length == 1) { // /ta <プレイヤー>
				if (!(sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender)) {
					sender.sendMessage(ChatColor.RED + "このコマンドはコンソールやコマンドブロックにしか使用できません");
					return false;
				}
				Player player = getServer().getPlayer(args[0]);
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "そのプレイヤーが見つけられませんでした。");
					return false;
				}
				UUID uuid = player.getUniqueId();
				String message = triggerTimer(uuid);
				sender.sendMessage(message);
				player.sendMessage(message);
				return true;
			}
		}
		return false;
	}

	private String triggerTimer(UUID uuid) {
		if (instances.containsKey(uuid)) {
			long difference = System.currentTimeMillis() - instances.get(uuid);
			instances.remove(uuid);
			return ChatColor.GREEN + "タイマー停止 "
					+ ChatColor.LIGHT_PURPLE + String.format("%d分 %.2f秒",
					difference / (1000 * 60),
					difference / 1000f
			);
		} else {
			instances.put(uuid, System.currentTimeMillis());
			return ChatColor.GREEN + "タイマー開始";
		}
	}
}
