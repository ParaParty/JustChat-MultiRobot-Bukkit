package cn.endymx.multirobot.vexview;

import lk.vexview.api.VexViewAPI;
import lk.vexview.hud.VexImageShow;
import org.bukkit.entity.Player;

public class VexView {
    public void sendHUD(Player player, int id, String url, int x, int y, int w, int h, int ws, int hs, int time, double xs, double ys){
        VexViewAPI.sendHUD(player, new VexImageShow(id, url, x, y, w, h, ws, hs, time), xs, ys);
    }
}
