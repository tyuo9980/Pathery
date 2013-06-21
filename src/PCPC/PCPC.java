// by Peter Li
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.15 - 3/28/12
// - Added getPixelColor function
// - Fixed uninitialized canvas bug
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.14 - 12/14/11
// - Massive efficiency improvements
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.13 - 11/25/11
// - Audio features
// - Added random color methods
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.12 - 11/20/11
// - Implemented MouseListener
// - Implemented MouseMotionListener
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.11 - 11/17/11
// - File IO features
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.1 - 11/04/11
// - Added graphics panel
// - Added Graphics drawing capabilities
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// v1.0 - 10/02/11
// - Initial Release
// - Basic IO features
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

package PCPC;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class PCPC implements KeyListener, MouseListener, MouseMotionListener
{
	// GUI variables
	public String version = "PC Pwns Console v1.15";
	public JFrame f;
	public Panel p;
	public TextArea textArea;
	public static TextField textField;

	// general variables
	public static boolean inputted = false;

	public static String iString = "";
	public static int iInt = 0;
	public static long iLong = 0;
	public static double iDouble = 0;
	public static float iFloat = 0;
	public static short iShort = 0;
	public static String text = "";

	public Random rd = new Random();
	public int randomInt;
	public int red;
	public int green;
	public int blue;
	public Color clr;

	// audio variables
	public static AudioInputStream stream;
	public static AudioFormat format;
	public static Clip clip;

	// mouse variables
	public static int mouseX;
	public static int mouseY;
	public static boolean mClicked;
	public static boolean mReleased;

	// keyboard variables
	public static int key;
	public static int keyR;
	public static char keychar;
	public static boolean released = true, pressed = false;
	public static String enterName = "Name";

	// graphics output variables
	public Graphics BufferedGraphics;
	public Graphics ImageGraphics;
	public Graphics g;
	public BufferedImage image;
	public Canvas canvas;

	public static int width = 768;
	public static int height = 698;

	// I/O variables
	public static BufferedReader reader;
	public static BufferedWriter writer;

	// printstream for setOut();
	PrintStream printStream = new PrintStream(new FilteredStream(new ByteArrayOutputStream()));

	public PCPC() throws Exception
	{
		// frame
		f = new JFrame(version);
		f.setSize(1024, 768);

		// textarea
		textArea = new TextArea(100, 30);
		textArea.setEditable(false);

		// textfield
		textField = new TextField();
		textField.addKeyListener(this);

		// canvas
		canvas = new Canvas();
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setBackground(Color.WHITE);
		canvas.setSize(width, height);

		// double buffering
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedGraphics = image.getGraphics();

		// panel
		p = new Panel();
		p.setLayout(new BorderLayout(10, 10));
		p.add(textArea, BorderLayout.LINE_START);
		p.add(canvas, BorderLayout.CENTER);
		p.add(textField, BorderLayout.PAGE_START);

		f.getContentPane().add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		System.setOut(printStream);
		Thread.sleep(100);
	}

	public PCPC(int w, int h) throws Exception
	{
		// frame
		f = new JFrame(version);
		f.setSize(w, h);
		width = w - 256;
		height = h - 70;

		// textarea
		textArea = new TextArea(100, 30);
		textArea.setEditable(false);

		// textfield
		textField = new TextField();
		textField.addKeyListener(this);

		// canvas
		canvas = new Canvas();
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setBackground(Color.WHITE);
		canvas.setSize(width, height);

		// double buffering
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedGraphics = image.getGraphics();

		// panel
		p = new Panel();
		p.setLayout(new BorderLayout(10, 10));
		p.add(textArea, BorderLayout.LINE_START);
		p.add(canvas, BorderLayout.CENTER);
		p.add(textField, BorderLayout.PAGE_START);

		f.getContentPane().add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		System.setOut(printStream);
		Thread.sleep(100);
	}

	public PCPC(int w, int h, boolean borderToggle) throws Exception
	{
		// frame
		f = new JFrame(version);
		f.setSize(w, h);
		width = w;
		height = h;

		// canvas
		canvas = new Canvas();
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setBackground(Color.WHITE);
		canvas.setSize(width, height);

		// double buffering
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedGraphics = image.getGraphics();

		// panel
		p = new Panel();
		p.setLayout(new BorderLayout(10, 10));
		// p.add(textArea, BorderLayout.LINE_START);
		p.add(canvas, BorderLayout.CENTER);
		p.addKeyListener(this);
		// p.add(textField, BorderLayout.PAGE_START);

		f.setIconImage(new ImageIcon("images/icon.png").getImage());
		f.getContentPane().add(p);
		f.addKeyListener(this);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Thread.sleep(100);
	}

	// creates new stream output to console screen
	class FilteredStream extends FilterOutputStream
	{
		public FilteredStream(OutputStream aStream)
		{
			super(aStream);
		}

		public void write(byte b[], int off, int len) throws IOException
		{
			String aString = new String(b, off, len);
			textArea.append(aString);
		}
	}

	// ////////////////////////////////
	// FILE IO
	// ////////////////////////////////

	public static String readFile(String file, int line) throws Exception
	{
		String text = "";
		reader = new BufferedReader(new FileReader(file));

		for (int i = 1; i <= line; i++)
		{
			text = reader.readLine();
		}

		reader.close();

		return text;
	}

	public static void writeFile(String text, String file) throws Exception
	{
		writer = new BufferedWriter(new FileWriter(file));

		writer.write(text);
		writer.flush();
		writer.close();
	}

	public static void appendFile(String text, String file) throws Exception
	{
		writer = new BufferedWriter(new FileWriter(file, true));

		writer.write(text);
		writer.newLine();
		writer.flush();
		writer.close();
	}

	// ////////////////////////////////
	// GRAPHICS
	// ////////////////////////////////

	public Graphics getGraphics()
	{
		return BufferedGraphics;
	}

	public void ViewUpdate()
	{
		ImageGraphics = canvas.getGraphics();
		ImageGraphics.drawImage(image, 0, 0, width, height, null);
	}

	public int getRGB(int x, int y)
	{
		int c = image.getRGB(x, y);
		return c;
	}

	public Color getPixelColor(int x, int y)
	{
		int c = image.getRGB(x, y);
		Color clr = new Color(c);

		return clr;
	}

	public void cls()
	{
		g = getGraphics();
		clr = g.getColor();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(clr);
	}

	public Color randColor()
	{
		red = rd.nextInt(256);
		green = rd.nextInt(256);
		blue = rd.nextInt(256);

		clr = new Color(red, green, blue);

		return clr;
	}

	public Color randBlue()
	{
		red = rd.nextInt(256);
		green = red;
		blue = 255;

		clr = new Color(red, green, blue);

		return clr;
	}

	public Color randRed()
	{
		red = 255;
		green = rd.nextInt(256);
		blue = green;

		clr = new Color(red, green, blue);

		return clr;
	}

	public Color randGreen()
	{
		red = rd.nextInt(256);
		green = 255;
		blue = red;

		clr = new Color(red, green, blue);

		return clr;
	}

	// ////////////////////////////////
	// AUDIO
	// ////////////////////////////////

	public static void playAudio(String file) throws Exception
	{
		stream = AudioSystem.getAudioInputStream(new File(file));
		format = stream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		clip = (Clip) AudioSystem.getLine(info);
		clip.open(stream);
		clip.start();
	}

	public static void loopAudio(String file) throws Exception
	{
		stream = AudioSystem.getAudioInputStream(new File(file));
		format = stream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		clip = (Clip) AudioSystem.getLine(info);
		clip.open(stream);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public static void stopAudio()
	{
		clip.close();
	}

	// ////////////////////////////////
	// TEXT
	// ////////////////////////////////

	public void clt()
	{
		textArea.setText("");
	}

	public static String getText()
	{
		if (inputted)
		{
			inputted = false;
			return text;
		}
		return "";
	}

	// ////////////////////////////////
	// GENERAL METHODS
	// ////////////////////////////////

	public int randInt(int i)
	{
		randomInt = rd.nextInt(i);

		return randomInt;
	}

	public void delay(int d) throws Exception
	{
		Thread.sleep(d);
	}

	public static String getString()
	{
		do
		{
		} while (inputted == false);

		inputted = false;
		iString = textField.getText();
		return iString;

	}

	public static char getChar()
	{
		do
		{
		} while (inputted == false);

		inputted = false;
		iString = textField.getText();
		textField.setText("");
		return iString.charAt(0);
	}

	public static Number getNumber()
	{
		String numberString = getString();
		try
		{
			numberString = numberString.trim().toUpperCase();
			return NumberFormat.getInstance().parse(numberString);
		} catch (Exception e)
		{
			// returns 0
			return new Integer(0);
		}
	}

	public static int getInt()
	{
		// returns int
		return getNumber().intValue();
	}

	public static long getLong()
	{
		// returns long
		return getNumber().longValue();
	}

	public static float getFloat()
	{
		// returns float
		return getNumber().floatValue();
	}

	public static double getDouble()
	{
		// returns double
		return getNumber().doubleValue();
	}

	public static double getShort()
	{
		// returns short
		return getNumber().shortValue();
	}

	public int getCanvasWidth()
	{
		// returns width
		return canvas.getWidth();
	}

	public int getCanvasHeight()
	{
		// returns height
		return canvas.getHeight();
	}

	public int getMouseX()
	{
		// returns x coord
		return mouseX;
	}

	public int getMouseY()
	{
		// returns y coord
		return mouseY;
	}

	public boolean getClick()
	{
		// returns mouseclick status
		if (mClicked)
		{
			mClicked = false;
			return true;
		}

		return false;
	}

	public boolean getRelease()
	{
		// returns mousereleased status
		return mReleased;
	}

	public int getKey()
	{
		// returns key pressed
		return key;
	}

	public char getKeyChar()
	{
		// returns key pressed
		return keychar;
	}

	public int getKeyR()
	{
		// returns key released
		return keyR;
	}

	public boolean getKeyRelease()
	{
		// returns key released
		if (released)
		{
			return true;
		}
		return false;
	}

	public boolean getKeyPress()
	{
		// returns key pressed
		if (pressed)
		{
			return true;
		}
		return false;
	}

	public String getName()
	{
		return enterName;
	}

	// ////////////////////////////////
	// LISTENER ARGUMENTS
	// ////////////////////////////////

	// mouselistener arguments
	public void mouseClicked(MouseEvent e)
	{
		//mClicked = true;
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		// mClicked = true;
	}

	public void mouseReleased(MouseEvent e)
	{
		mClicked = true;
	}

	// mousemotionlistener arguments
	public void mouseDragged(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	// keylistener arguments
	public void keyTyped(KeyEvent e)
	{
		keychar = e.getKeyChar();

		if (keychar != 0 && keychar != 10 && keychar != ' ')
		{
			if (keychar == 8)
			{
				if (enterName.length() > 0)
				{
					enterName = enterName.substring(0, enterName.length() - 1);
				}
			} else
			{
				if (enterName.length() < 12)
				{
					enterName += Character.toString((char) keychar);
				}
			}
		}

	}

	public void keyPressed(KeyEvent e)
	{
		key = e.getKeyCode();
		pressed = true;
		// released = false;
		// System.out.println (key);
		// key = e.getKeyCode();
		// key = e.getKeyCode();
		// text = textField.getText ();

		// sets inputted to true
		// switch (key)
		// {
		// case KeyEvent.VK_ENTER:
		// inputted = true;
		// text = textField.getText();
		// textField.setText("");
		// }
	}

	public void keyReleased(KeyEvent e)
	{
		released = true;
		pressed = false;
		keyR = e.getKeyCode();
		// text = textField.getText ();

		if (keyR == keychar)
		{
			keychar = 0;
		}
	}
}
