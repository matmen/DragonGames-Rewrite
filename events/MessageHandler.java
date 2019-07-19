package events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class MessageHandler implements Listener {

    @EventHandler
    public void onEvent(@NotNull AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            for (Player t : Bukkit.getOnlinePlayers())
                if (t.getGameMode() == GameMode.SPECTATOR)
                    t.sendMessage(String.format(Messages.getString("MessageHandler.DeadFormat"), p.getName(), //$NON-NLS-1$
                            e.getMessage()));
        }

        e.setFormat(Messages.getString("MessageHandler.AliveFormat")); //$NON-NLS-1$
    }
}