package ru.kowkodivka.mindustry.antibot;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Plugin;
import mindustry.net.NetConnection;
import mindustry.net.Packets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AntiBot extends Plugin {
    public AntiBot() {
        Vars.net.handleServer(Packets.Connect.class, (con, packet) -> {
            try {
                String organization = getOrganization(con.address);
                if (organization.contains("Microsoft Corporation")) {
                    handleMicrosoftConnection(con);
                }
            } catch (IOException e) {
                Log.err("Failed to check organization for IP: " + con.address, e);
            }

            handleDuplicateConnections(con);
        });
    }

    private String getOrganization(String ipAddress) throws IOException {
        URL url = new URL("https://ipinfo.io/" + ipAddress + "/org");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private void handleMicrosoftConnection(NetConnection connection) {
        try {
            if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                Runtime.getRuntime().exec(String.format("/bin/sh iptables -A INPUT %s -j DROP", connection.address));
            }
        } catch (IOException e) {
            Log.err("Failed to block Microsoft connection for IP: " + connection.address, e);
        }

        Vars.netServer.admins.blacklistDos(connection.address);
        connection.close();
    }

    private void handleDuplicateConnections(NetConnection connection) {
        var connections = Seq.with(Vars.net.getConnections()).retainAll(other -> other.address.equals(connection.address));
        if (connections.size >= 2) {
            Vars.netServer.admins.blacklistDos(connection.address);
            connections.each(NetConnection::close);
        }
    }
}
