package ru.kowkodivka.mindustry.antibot;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Plugin;
import mindustry.net.Administration;
import mindustry.net.NetConnection;
import mindustry.net.Packets;

import java.io.IOException;

public class AntiBot extends Plugin {
    public AntiBot() {
        Vars.net.handleServer(Packets.Connect.class, (con, packet) -> {
            var connections = Seq.with(Vars.net.getConnections()).retainAll(other -> other.address.equals(con.address));
            if (connections.size >= 2) {
                try {
                    if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                        Runtime.getRuntime().exec(String.format("/bin/sh iptables -A INPUT %s -j DROP", con.address));
                    }
                } catch (IOException e) {
                    Log.err("Something went wrong:\n@", e);
                }

                // Уже есть на Darkdustry
                if (!Administration.Config.serverName.name.toLowerCase().contains("darkdustry")) {
                    Vars.netServer.admins.blacklistDos(con.address);
                    connections.each(NetConnection::close);
                }
            }
        });
    }
}