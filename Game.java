//java C:\Game\Game.java

import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Game {
	public static void main(String[] args) {
		new GameApp();
	}
}

class Gaming extends GameLogic {

	@Override
	public void update() {
		setPos(15, 15);
		setRadius(5);
		Win.add(15, 17, 1);
		PLAYER_X++;

	}

}

class GameApp extends JFrame{
	
	public final int windowHight = 576;
	public final int windowWidth = 768;
	public final int tileSize = 48;

	public GameApp() {

		JFrame window = new JFrame();
		Gaming gl = new Gaming();

		gl.GameLogic(windowWidth, windowHight, tileSize);

		window.setTitle("Game");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);

		window.add(gl);
		
		window.pack();
		window.setVisible(true);

		gl.startGameThread();

	}

}

class GameLogic extends JPanel implements Runnable{

	final int FPS = 2;
	int radius = 5;
	Thread gameThread;
	Window Win = new Window(50, 50);

	int PLAYER_X = 15;
	int PLAYER_Y = 15;

	public void setPos(int px, int py) {
		this.PLAYER_Y = py;
		this.PLAYER_X = px;
	}

	public void setRadius(int r) {
		this.radius = r;
	}

	public void GameLogic(int width, int height, int tileSize) {
		this.setPreferredSize(new Dimension((radius*2+1)*48, (radius*2+1)*48));
		this.setBackground(Color.cyan);
		this.setDoubleBuffered(true);
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {

		double drawInterval = 1000000000/FPS;
		double nextDrawTime = System.nanoTime() + drawInterval;

		while (gameThread != null) {
			update();
			repaint();
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				if (remainingTime < 0) { remainingTime=0; }
				Thread.sleep((long) remainingTime);
				nextDrawTime += drawInterval;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void update() {
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;

		Win.getLocationAtRadius(PLAYER_X, PLAYER_Y, radius);
		Win.require(Win.windowCut[0].length, Win.windowCut.length, 48, g2);
    
		g2.dispose();
	}

}

class Window {
	int[][] window;
	int[][] windowCut;

	int height;
	int width;

	public Window(int width, int height) {
		this.width = width;
		this.height = height;

		this.window = new int[this.height][this.width];

		for (int i = 0; i<this.height; i++) {
			for (int j = 0; j<this.height; j++) {
				this.window[i][j] = 0;
			}
		}
	}

	public void add(int x, int y, int id) {
		this.window[y][x] = id;
	}

	public void render() {
		for (int i = 0; i<windowCut.length; i++) {
			System.out.println();
			for (int j = 0; j<this.windowCut[0].length; j++) {
				System.out.print(this.windowCut[i][j]);
			}
		} System.out.println();
	}

	public void getLocationAtRadius(int x, int y, int r) {
		windowCut = new int[r*2+1][r*2+1];
		int i = -1;
		int j = -1;
		for (int _y = y-r; _y<=y+r; _y++) {
			i++;
			for (int _x = x-r; _x<=x+r; _x++) {
				j++;
				windowCut[i][j] = window[_y][_x];
			}
			j = -1;
		}
	}

	public void require(int raw, int col, int tileSize, Graphics2D g2) {
		String tmp = "";
		for(int t_y = 0; t_y<col; t_y++) {
			for (int t_x = 0; t_x<raw; t_x++) {
				int y = t_y*tileSize;
				int x = t_x*tileSize;
				tmp = "";
				try {
					switch (windowCut[t_y][t_x]) {
						case 0:
							tmp = new String("air");
							break;
						case 1:
							tmp = new String("grass");
							break;
						case 2:
							tmp = new String("dirt");
							break;
					}
					BufferedImage img = ImageIO.read(getClass().getResourceAsStream(new String(tmp + ".png")));
					g2.drawImage(img, x, y, 48, 48, null);
				} catch (IOException e) {
					//
				}
			}
		}
	}
}