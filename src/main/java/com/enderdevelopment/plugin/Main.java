package com.enderdevelopment.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            installDatapack(world);
        }
    }

    private void installDatapack(World world) {
        Path target = world.getWorldFolder().toPath().resolve("datapacks/historical_arsenal");
        try {
            Files.createDirectories(target);
            try (var resources = getClass().getClassLoader().getResources("datapack/")) {
                if (!resources.hasMoreElements()) {
                    throw new IOException("Bundled datapack resources are missing");
                }
            }
            copyResource("datapack/pack.mcmeta", target.resolve("pack.mcmeta"));
            copyResource("datapack/data/historical_arsenal/function/armory.mcfunction",
                    target.resolve("data/historical_arsenal/function/armory.mcfunction"));
            copyResource("datapack/data/historical_arsenal/function/load.mcfunction",
                    target.resolve("data/historical_arsenal/function/load.mcfunction"));
            copyResource("datapack/data/minecraft/tags/function/load.json",
                    target.resolve("data/minecraft/tags/function/load.json"));
            getLogger().info("Installed Historical Arsenal datapack in world " + world.getName());
        } catch (IOException exception) {
            getLogger().severe("Unable to install datapack in " + world.getName() + ": " + exception.getMessage());
        }
    }

    private void copyResource(String resource, Path destination) throws IOException {
        Files.createDirectories(destination.getParent());
        try (InputStream input = getResource(resource)) {
            if (input == null) {
                throw new IOException("Missing bundled resource " + resource);
            }
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
