package tech.raidtheweb.jchip8.emu;

import tech.raidtheweb.jchip8.chip.Chip;

public class Main extends Thread {
	
	private Chip chip8;
	private ChipFrame frame;
	
	public Main(String[] args) {
		System.out.println("JCHIP8 Chip8 System Emulation Software written in Java by RaidTheWeb");
		System.out.println();
		System.out.println("Attempting to start emulation...");
		
		System.out.println("Initializing Chip...");
		chip8 = new Chip();
		chip8.init();
		try {
			chip8.loadProgram(args[0]);
		} catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("No ROM given.");
			System.exit(1);
		}
		frame = new ChipFrame(chip8);
		
		
	}
	
	public void run() {
		//60HZ, epic speed
		while(true) {
			chip8.setKeyBuffer(frame.getKeyBuffer());
			chip8.run();
			if(chip8.needsRedraw()) {
				frame.repaint();
				chip8.removeDrawFlag();
			}
			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {
				// no
			}
		}
	}
	
	public static void main(String[] args) {
		Main main = new Main(args);
		main.start();
		
	}
}
