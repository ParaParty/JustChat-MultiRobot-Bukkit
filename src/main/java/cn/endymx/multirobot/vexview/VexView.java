package cn.endymx.multirobot.vexview;

import cn.endymx.multirobot.LoadClass;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.components.expand.VexGifImage;
import lk.vexview.hud.VexImageShow;
import org.bukkit.entity.Player;


public class VexView{

    private static LoadClass plugin;

    public VexView(LoadClass plugin){
        VexView.plugin = plugin;
    }

    public static String getVersion(){
        return lk.vexview.VexView.getInstance().getVersion();
    }

    public static void sendHUD(Player player, int id, String url, int x, int y, int w, int h, int time, double xs, double ys, String mode){
        double[] xy = zoomImage(w, h);
        if (mode.equals("gif")) {
            VexViewAPI.sendHUD(player, new VexImageShow(id, new VexGifImage(url, x, y, (int) xy[1], (int) xy[0], (int) xy[1], (int) xy[0]), time), xs, ys);
        }else{
            VexViewAPI.sendHUD(player, new VexImageShow(id, url, x, y, (int) xy[1], (int) xy[0], (int) xy[1], (int) xy[0], time), xs, ys);
        }
    }

    private static double[] zoomImage(double x, double y){
        while (!(x < plugin.config.getInt("imageBigX")) && !(y < plugin.config.getInt("imageBigY"))){
            x = x * 0.9;
            y = y * 0.9;
        }
        return new double[]{x, y};
    }
}