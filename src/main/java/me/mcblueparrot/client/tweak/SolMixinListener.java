package me.mcblueparrot.client.tweak;

import com.github.glassmc.loader.api.GlassLoader;
import com.github.glassmc.loader.api.Listener;
import com.github.glassmc.mixin.Mixin;

public class SolMixinListener implements Listener {

    @Override
    public void run() {
        Mixin mixin = GlassLoader.getInstance().getAPI(Mixin.class);
        mixin.addConfiguration("mixins.solclient.json");
    }

}
