package xyz.ultrasplayroom.mc.spigottps;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ultrasplayroom.mc.spigottps.util.TickStatistics;

public class SpigotTPS implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("spigottps");

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            TickStatistics tickStatistics = new TickStatistics();
            dispatcher.register(CommandManager.literal("tps").executes(context -> {

                if (!Permissions.check(context.getSource(), "spigottps.tps", true)) {
                    context.getSource().sendFeedback(new LiteralText("Insufficient permission."), false);
                    return 0;
                }

                MutableText send1 = new LiteralText("TPS from last 1m, 5m, 15m: ").setStyle(Style.EMPTY.withColor(Formatting.GOLD));

                MutableText send2 = new LiteralText(String.join("" + ", ", formatTps(tickStatistics.tps1Min()), formatTps(tickStatistics.tps5Min()), formatTps(tickStatistics.tps15Min())));


                if (context.getSource().getName().equals("Server")) {
                    //text = new LiteralText(text.asString().replace("§a",""));
                    send1 = new LiteralText(send1.asString().replace("§a", "").replace("§e", "").replace("§c", ""));
                    send2 = new LiteralText(send2.asString().replace("§a", "").replace("§e", "").replace("§c", ""));
                    // text.fillStyle(Style.EMPTY);
                }

                MutableText text = send1.append(send2);

                //text.fillStyle(Style.EMPTY.withColor(Formatting.WHITE));

                context.getSource().sendFeedback(text,
                        false);
                //context.getSource().sendFeedback(new LiteralText("&eTPS from last 1m, 5m, 15m: 20.0, 20.0, 20.0"), false);
                return 1;
            }));
        });

    }

    // From PlanetTeamSpeak's MoreCommands
    // https://github.com/PlanetTeamSpeakk/MoreCommands

    public static String formatFromFloat(float v, float max, float yellow, float green, boolean includeMax) {
        float percent = v/max;
        return "" + (percent >= green ? Formatting.GREEN : percent >= yellow ? Formatting.YELLOW : Formatting.RED) + (includeMax ? "" + "/" + Formatting.GREEN + max : "");
    }

    private String formatTps(double tps) {
        return formatFromFloat((float) tps, 20, 0.8f, 0.9f, false) + (tps > 20 ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

}
