package me.mcblueparrot.client.ui.screen;

import v1_8_9.net.minecraft.client.gui.GuiMultiplayer;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

public class IngameServerList extends GuiMultiplayer {

	public IngameServerList(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void connectToSelected() {
		disconnect();
		super.connectToSelected();
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		super.confirmClicked(result, id);
	}

	private void disconnect() {
		mc.theWorld.sendQuittingDisconnectingPacket();
		mc.loadWorld(null);
	}

}
