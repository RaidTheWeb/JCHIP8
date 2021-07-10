package tech.raidtheweb.jchip8.emu;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import tech.raidtheweb.jchip8.chip.Chip;

public class ChipPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Chip chip;
	
	public ChipPanel(Chip chip) {
		this.chip = chip;
	}
	
	public void paint(Graphics g) {
		byte[] display = chip.getDisplay();
		for(int i = 0; i < display.length; i++) {
			if(display[i] == 0)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.WHITE);
			
			int x = (i % 64);
			int y = (int)Math.floor(i / 64);
			
			g.fillRect(x * 10, y * 10, 10, 10);
		}
	}

}
