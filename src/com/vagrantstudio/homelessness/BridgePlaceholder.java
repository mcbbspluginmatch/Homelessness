/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Guild;
import java.util.HashMap;
import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author BergStudio
 */
public class BridgePlaceholder extends PlaceholderExpansion {

    private Map<String, Request> requestMap = new HashMap();

    public BridgePlaceholder() {
        requestMap.put("guild", new Request() {
            @Override
            public String request(OfflinePlayer player, String key) {
                Guild guild = PixelGuild.forPlayer(player);
                if (guild == null) {
                    return "§c无公会";
                }
                switch (key) {
                    case "name":
                        return guild.getName();
                    case "onlines":
                        int online = 0;
                        online = guild.getPlayers().keySet().stream().filter((paramUniqueId) -> (Bukkit.getOfflinePlayer(paramUniqueId).isOnline())).map((_item) -> 1).reduce(online, Integer::sum);
                        return String.valueOf(online);
                    default:
                        return null;
                }
            }
        });
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "homelessness";
    }

    @Override
    public String getAuthor() {
        return "Retr0";
    }

    @Override
    public String getVersion() {
        return "0.0.3";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        String[] ids = identifier.split("_");
        return (ids.length == 2 && requestMap.containsKey(ids[0])) ? requestMap.get(ids[0]).request(player, ids[1]) : null;
    }

    private abstract static class Request {

        public abstract String request(OfflinePlayer player, String key);

    }

}
