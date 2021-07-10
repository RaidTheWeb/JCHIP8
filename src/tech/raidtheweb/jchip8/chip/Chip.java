package tech.raidtheweb.jchip8.chip;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class Chip {

	private char[] memory; // 4KB 8bit memory
	
	private char[] V; // 8bit regs
	
	private char I; // 16bit pointer
	
	private char pc; // 16bit program pointer
	
	private char stack[]; // callstack
	
	private int stackPointer; // next free slot in stack (integer form)
	
	private int delay_timer; // delay timer
	
	private int sound_timer; // sound timer
	
	
	private byte[] keys; // key array
	
	private byte[] display; // display
	
	private boolean needRedraw; // boolean value for redraw requirements
	
	
	public void init() {
		// initialize all variables
		memory = new char[4096];
		V = new char[16];
		I = 0x0;
		pc = 0x200;
		
		stack = new char[16];
		stackPointer = 0;
		
		delay_timer = 0;
		sound_timer = 0;
		
		keys = new byte[16];
		
		display = new byte[64 * 32];
		
		needRedraw = false;
		// load fontset at 0x50
		loadFontset();
	}
	
	
	public void run() {
		// get the opcode in memory via program pointer
		char opcode = (char)((memory[pc] << 8) | memory[pc + 1]);
		switch(opcode & 0xF000) {
		
		case 0x0000:
			switch(opcode & 0x00FF) {
			case 0x00E0:
				for(int i = 0; i < display.length; i++) {
					display[i] = 0;
				}
				pc += 2;
				needRedraw = true;
				break;
				
			case 0x00EE:
				stackPointer--;
				pc = (char)(stack[stackPointer] + 2);
				break;
				
			default:
				System.err.println("Unsupported Opcode");
				System.exit(0);
				break;
			}
			break;
		
		case 0x1000: {
			int nnn = opcode & 0x0FFF;
			pc = (char)nnn;
			break;
		}
			
		case 0x2000:
			stack[stackPointer] = pc;
			stackPointer++;
			pc = (char)(opcode & 0x0FFF);
			break;
			
		case 0x3000: {
			int x = (opcode & 0x0F00) >> 8;
			int nn = (opcode & 0x00FF);
			if(V[x] == nn) {
				pc += 4;
			} else {
				pc += 2;
			}
			break;
		}
		
		case 0x4000: {
			int x = (opcode & 0x0F00) >> 8;
			int nn = opcode & 0x00FF;
			if(V[x] != nn) {
				pc += 4;
			} else {
				pc += 2;
			}
			break;
		}
		
		case 0x5000: {
			int x = (opcode & 0x0F00) >> 8;
			int y = (opcode & 0x00F0) >> 4;
			if(V[x] == V[y]) {
				pc += 4;
			} else {			
				pc += 2;
			}
			break;
		}
			
		case 0x6000: {
			int x = (opcode & 0x0F00) >> 8;
			V[x] = (char)(opcode & 0x00FF);
			pc += 2;
			break;
		}
			
		case 0x7000: {
			int x = (opcode & 0x0F00) >> 8;
			int nn = (opcode & 0x00FF);
			V[x] = (char)((V[x] + nn) & 0xFF);
			pc += 2;
			break;
		}
		
		case 0x8000:
			
			switch(opcode & 0x000F) {
			
			case 0x0000: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				V[x] = V[y];
				pc += 2;
				break;
			}
			
			case 0x0001: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				V[x] = (char)((V[x] | V[y]) & 0xFF);
				pc += 2;
				break;
				
			}
				
			case 0x0002: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				V[x] = (char)(V[x] & V[y]);
				pc += 2;
				break;
			}
			
			case 0x0003: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				V[x] = (char)((V[x] ^ V[y]) & 0xFF);
				pc += 2;
				break;
				
			}
				
			case 0x0004: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				if(V[y] > 0xFF - V[x]) {
					V[0xF] = 1;
				} else {
					V[0xF] = 0;
				}
				V[x] = (char)((V[x] + V[y]) & 0xFF);
				pc += 2;
				break;
			}
			
			case 0x0005: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				if(V[x] > V[y]) {
					V[0xF] = 1;
				} else {
					V[0xF] = 0;
				}
				V[x] = (char)((V[x] - V[y]) & 0xFF);
				pc += 2;
				break;
			}
			
			case 0x0006: {
				int x = (opcode & 0x0F00) >> 8;
				V[0xF] = (char)(V[x] & 0x1);
				V[x] = (char)(V[x] >> 1);
				pc += 2;
				break;
			}
			
			case 0x0007: {
				int x = (opcode & 0x0F00) >> 8;
				int y = (opcode & 0x00F0) >> 4;
				if(V[x] > V[y]) {
					V[0xF] = 0;
				} else {
					V[0xF] = 1;
				}
				V[x] = (char)((V[y] - V[x]) & 0xFF);
				pc += 2;
				break;
			}
			
			case 0x000E: {
				int x = (opcode & 0x0F00) >> 8;
				V[0xF] = (char)(V[x] & 0x80);
				V[x] = (char)(V[x] << 1);
				pc += 2;
				break;
			}
			
				default:
					System.err.println("Unsupported Opcode!");
					System.exit(0);
					break;
			}
				
			break;
		
		
		case 0x9000: {
			int x = (opcode & 0x0F00) >> 8;
			int y = (opcode & 0x00F0) >> 4;
			if(V[x] != V[y]) {
				pc += 4;
			} else {			
				pc += 2;
			}
			break;
		}
			
		case 0xA000:
			I = (char)(opcode & 0x0FFF);
			pc += 2;
			break;
			
		case 0xB000: {
			int nnn = opcode & 0x0FFF;
			int extra = V[0] & 0xFF;
			
			pc = (char)(nnn + extra);
			break;
		}
			
		case 0xC000: {
			int x = (opcode & 0x0F00) >> 8;
			int nn = (opcode & 0x00FF);
			int randomNumber = new Random().nextInt(255) & nn;
			V[x] = (char)randomNumber;
			pc += 2;
			break;
		}
			
		case 0xD000: {
			int x = V[(opcode & 0x0F00) >> 8];
			int y = V[(opcode & 0x00F0) >> 4];
			int height = opcode & 0x000F;
			
			V[0xF] = 0;
			
			for(int _y = 0; _y < height; _y++) {
				int line = memory[I + _y];
				for(int _x = 0; _x < 8; _x++) {
					int pixel = line & (0x80 >> _x);
					if(pixel != 0) {
						int totalX = x + _x;
						int totalY = y + _y;
						
						totalX = totalX % 64;
						totalY = totalY % 32;
						
						int index = (totalY * 64) + totalX;
						
						if(display[index] == 1)
							V[0xF] = 1;
						
						display[index] ^= 1;
					}
				}
			}
			pc += 2;
			needRedraw = true;
			break;
		}
		
		case 0xE000: {
			switch (opcode & 0x00FF) {
			case 0x009E: {
				int x = (opcode & 0x0F00) >> 8;
				int key = V[x];
				if(keys[key] == 1) {
					pc += 4;
				} else {
					pc += 2;
				}
				break;
			}
				
			case 0x00A1: {
				int x = (opcode & 0x0F00) >> 8;
				int key = V[x];
				if(keys[key] == 0) {
					pc += 4;
				} else {
					pc += 2;
				}
				break;
			}
				
				default:
					System.err.println("Unexisting opcode");
					System.exit(0);
					return;
			}
			break;
		}
		
		case 0xF000:
			
			switch(opcode & 0x00FF) {
			
			case 0x0007: {
				int x = (opcode & 0x0F00) >> 8;
				V[x] = (char)delay_timer;
				pc += 2;
				break;
			}
			
			case 0x000A: {
				int x = (opcode & 0x0F00) >> 8;
				for(int i = 0; i < keys.length; i++) {
					if(keys[i] == 1) {
						V[x] = (char)i;
						pc += 2;
						break;
					}
				}
				break;
			}
			
			case 0x0015: {
				int x = (opcode & 0x0F00) >> 8;
				delay_timer = V[x];
				pc += 2;
				break;
			}
			
			case 0x0018: {
				int x = (opcode & 0x0F00) >> 8;
				sound_timer = V[x];
				pc += 2;
				break;
			}
			
			case 0x001E: {
				int x = (opcode & 0x0F00) >> 8;
				I = (char)(I + V[x]);
				pc += 2;
				break;
			}
			
			case 0x0029: {
				int x = (opcode & 0x0F00) >> 8;
				int character = V[x];
				I = (char)(0x050 + (character * 5));
				pc += 2;
				break;
			}
			
			case 0x0033: {
				int x = (opcode & 0x0F00) >> 8;
				int value = V[x];
				int hundreds = (value - (value % 100)) / 100;
				value -= hundreds * 100;
				int tens = (value - (value % 10))/ 10;
				value -= tens * 10;
				memory[I] = (char)hundreds;
				memory[I + 1] = (char)tens;
				memory[I + 2] = (char)value;
				
				pc += 2;
				break;
			}
			
			case 0x0055: {
				int x = (opcode & 0x0F00) >> 8;
				for(int i = 0; i <= x; i++) {
					memory[I + i] = V[i];
				}
				pc += 2;
				break;
			}
			
			case 0x0065: {
				int x = (opcode & 0x0F00) >> 8;
				for(int i = 0; i <= x; i++) { 
					V[i] = memory[I + i];
				}
				
				I = (char)(I + x + 1);
				pc += 2;
				break;
			}
			
			default:
				System.err.println("Unsupported Opcode!");
				System.exit(0);
			}
			break;
		
			default:
				System.err.println("Unsupported Opcode!");
				System.exit(0);
		}
		if(sound_timer > 0)
			sound_timer--;
		if(delay_timer > 0)
			delay_timer--;
	}
	
	public byte[] getDisplay() {
		return display;
	}

	public boolean needsRedraw() {
		return needRedraw;
	}

	public void removeDrawFlag() {
		needRedraw = false;
	}

	public void loadProgram(String file) {
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(new File(file)));
			
			int offset = 0;
			while(input.available() > 0) {
				memory[0x200 + offset] = (char)(input.readByte() & 0xFF);
				offset++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if(input != null) {
				try { input.close(); } catch (IOException ex) {}
			}
		}
	}
	
	public void loadFontset() {
		for(int i = 0; i < ChipData.fontset.length; i++) {
			memory[0x50 + i] = (char)(ChipData.fontset[i] & 0xFF);
		}
	}
	
	public void setKeyBuffer(int[] keyBuffer) {
		for(int i = 0; i < keys.length; i++) {
			keys[i] = (byte)keyBuffer[i];
		}
	}

}