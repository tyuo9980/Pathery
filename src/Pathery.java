import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import PCPC.PCPC;

/*
// ~~~PATHERY CLIENT~~~
// By Peter Li
// and Ryan Wang
// ~~~~~~~~~~~~~~~~~~~~
// ICS4UP-01
// Mr. Goutziomitros
// ~~~~~~~~~~~~~~~~~~~~
// Last updated 6/10/13
*/


public class Pathery
{
	//Console frame vars
	public PCPC c;                                          //Console object
	public Graphics2D g;                                    //Graphics object
	public int windowHeight = 700, windowWidth = 1100;      //Frame size

	//Font and text vars
	public Font font;                                       //Font object
	public Color bgColor = new Color(18, 18, 18);           //Font color #1
	public Color txtColor = new Color(230, 230, 140);       //Font color #2

	//FPS and loop control vars
	public int frameDelay;                                  //Delay between loops
	public long time;                                       //Temportary time storage
	public final int fps = 60;                              //FramesPerSecond constant
	public final int spd = 60;                              //Path animation FPS
	public final int blockSize = 35;                        //Block size constant

	//Game objects
	public MapData map;                                     //MapData object for maps
	public int numMoves;                                    //Number of moves
	public ArrayList<Integer> path;                         //Stores list of directions for solved path
	public int startX, startY;                              //Path start coodinates
	public int xMapPosition, yMapPosition;                  //Map placement coordinates
	public int mouseX, mouseY;                              //Mouse coodinates
	public int[] pathX, pathY;                              //Path coordinates
	public boolean pathActive;                              //Path animation state
	public int pathCurrent = 0;                             //Path counter
	public int Score = 0;                                   //Score variable
	public int walls;                                       //Number of walls
	public boolean solvable;                                //Solve state

	//Leaderboards vars
	public String name = "Newb";                            //Temporary name
	public String enterName = "derp";                       //Logged in name
	public String saveFile = "score.txt";                   //Save file name and location
	public String names[] = new String[14];                 //Stores list of names
	public int scores[] = new int[14];                      //Stores list of scores

	//Image vars
	public BufferedImage aCheckpoint, bCheckpoint, cCheckpoint, dCheckpoint, eCheckpoint;   //Checkpoint images
	public BufferedImage wall, open, rock, start, finish, empty;                            //Blocktype images
	public BufferedImage up, right, down, left;                                             //Path direction images
	public BufferedImage leaderboards, goButton, header;                                    //Background images

	//Main method; starts game
	public static void main(String[] L33T) throws Exception
	{
		Pathery pathery = new Pathery();
		pathery.init();
		pathery.imageLoad();
		pathery.openingPage();
		pathery.run();
	}

	//Resets map state and variables
	public void reset(MapData m)
	{
		xMapPosition = (c.f.getWidth() - m.width * blockSize) / 2 - 200;
		yMapPosition = (c.f.getHeight() - m.height * blockSize) / 2;
		pathActive = false;
		solvable = true;
		walls = m.numWalls;
	}

	//Clears leaderboard values
	public void scoreReset() throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
		writer.write("");
		writer.flush();
		writer = new BufferedWriter(new FileWriter(saveFile, true));

		// deletes all characters in text file
		for (int i = 1; i <= 10; i++)
		{
			writer.write("-\n");
			writer.write(Integer.toString(0) + "\n");
			writer.flush();
		}
		writer.close();
	}

	//Resets all variables to default values
	public void fullReset() throws Exception
	{
		getMaps();
		reset(map);
		scoreReset();
		getScore();
	}

	//Initializes game variables and sets frame parameters
	public void init() throws Exception
	{
		frameSet();
		fullReset();
	}

	//Loads all images
	public void imageLoad() throws Exception
	{
		start = ImageIO.read(new File("images/start.png"));
		finish = ImageIO.read(new File("images/finish.png"));

		aCheckpoint = ImageIO.read(new File("images/a.png"));
		bCheckpoint = ImageIO.read(new File("images/b.png"));
		cCheckpoint = ImageIO.read(new File("images/c.png"));
		dCheckpoint = ImageIO.read(new File("images/d.png"));
		eCheckpoint = ImageIO.read(new File("images/e.png"));
		rock = ImageIO.read(new File("images/rock.png"));
		wall = ImageIO.read(new File("images/wall.png"));
		empty = ImageIO.read(new File("images/open.png"));

		up = ImageIO.read(new File("images/paths/up.png"));
		right = ImageIO.read(new File("images/paths/right.png"));
		down = ImageIO.read(new File("images/paths/down.png"));
		left = ImageIO.read(new File("images/paths/left.png"));

		goButton = ImageIO.read(new File("images/goButton.png"));
		header = ImageIO.read(new File("images/header.png"));
		leaderboards = ImageIO.read(new File("images/leaderboards.png"));
	}

	//Sets frame size, title, font, and color
	public void frameSet() throws Exception
	{
		c = new PCPC(windowWidth, windowHeight, false);
		c.f.setTitle("Pathery");
		c.f.setResizable(false);
		g = (Graphics2D) c.getGraphics();
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		font = new Font("Trebuchet MS", Font.BOLD, 19);
		g.setFont(font);
	}

	//Controls loop delay timer
	public void fpsManager(int framesPerSecond) throws Exception
	{
		// calculates the difference between (apprporate time for each frame)
		// and (the actual time taken for each frame)
		frameDelay = (int) ((1000 / framesPerSecond) - (System.currentTimeMillis() - time));

		// if program runs faster then set fps, appropiate delay is added
		if (frameDelay > 0)
		{
			c.delay(frameDelay);
		} else
		{
			c.delay(1);
		}
	}

	//Game loop
	public void run() throws Exception
	{
		while (true)
		{
			time = System.currentTimeMillis(); // used for the fpsManager

			drawBG();               //draws the background image
			drawMap(map);           //draws the game map
			drawPath(spd);          //draws path of the map
			drawLeaderboards(map);  //draws leaderboards
			drawExtras(map);        //draws screen text and other misc elements
			mouseDetection(map);    //gets mouse input
			c.ViewUpdate();         //updates screen

			fpsManager(fps);        //loop control method
		}
	}

	//Main menu and instructions page
	public void openingPage() throws Exception
	{
		while (true)
		{
			char key;

			time = System.currentTimeMillis(); // used for the fpsManager

			drawBG();

			// instructions
			g.setColor(Color.white);
			g.drawString("Welcome to Pathery!", 450, 50);

			g.setColor(txtColor);
			g.drawString("You will be presented a map consisting of rocks and checkpoints.", 230, 90);
			g.drawString("The game will find the shortest path from the start tile to the finish tile.", 230, 120);
			g.drawString("The path will also traverse through the checkpoints,", 230, 150);
			g.drawString("which are denoted by these letters: A, B, C, D, E.", 230, 180);
			g.drawString("It will arrive at the checkpoints in order, arriving first at A, then B, etc.", 230, 210);
			g.drawString("Your goal is to find the LOONNGGGESSTT path.", 230, 240);
			g.drawString("You are given a set number of walls to place on the map.", 230, 270);
			g.drawString("Use the walls to lengthen the path that the game will travel.", 230, 300);
			g.drawString("Have fun!", 230, 330);
			g.drawString("Visit the main page at: http://www.pathery.com/", 320, 650);

			g.setColor(Color.white);
			g.drawString("Enter name: " + enterName, 430, 400);

			key = c.getKeyChar();

			enterName = c.getName();

			if (key == 10) // enter key
			{
				name = enterName;
				if (name.length() > 0)
					break;
			}

			c.ViewUpdate();

			fpsManager(fps);
		}
	}

	//Generates a random date to parse maps
	public void getMaps() throws Exception
	{
		// Date date = new Date();
		String d = Integer.toString(c.randInt(28) + 1);
		String m = Integer.toString(c.randInt(12) + 1);
		String s = "2012-" + m + "-" + d;
		parse(s);
	}

	//Gets list of map codes from pathery.com
	public void parse(String s) throws Exception
	{
		// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); dateFormat.format(d)
		URL url = new URL("http://www.pathery.com/a/mapsbydate/" + s + ".js");

		Scanner sc = new Scanner(url.openStream());
		sc.useDelimiter("\\[|\\]|,");
		sc.nextInt(); // skips first map
		int n = sc.nextInt();
		sc.close();

		// parses map based on map code
		URL url2 = new URL("http://www.pathery.com/a/map/" + n + ".js");
		Scanner ss = new Scanner(url2.openStream());
		String mapCode = ss.next();
		ss.close();
		map = new MapData(mapCode);
	}

	//Solves the maze by finding the shortest path
	public void solve(MapData m)
	{
		int c = m.numCheckpoints;
		int sx[] = new int[m.numStarts];
		int sy[] = new int[m.numStarts];
		int sx2[] = new int[1];
		int sy2[] = new int[1];
		int sc = 0;
		numMoves = 0;
		solvable = true;

		ArrayList<Character> cp = new ArrayList<Character>();
		path = new ArrayList<Integer>();

		// adds list of checkpoints
		for (int i = 'a'; i < 'a' + c; i++)
		{
			cp.add((char) i);
		}

		cp.add('f');

		// gets positions of starting tiles
		for (int y = 0; y < m.height; y++)
		{
			for (int x = 0; x < m.width; x++)
			{
				if (m.map[x][y] == 's')
				{
					sx[sc] = x;
					sy[sc] = y;
					sc++;
				}
			}
		}

		// finds shortest path from start to checkpoints
		Vertex v = BFS(sx, sy, cp.remove(0), m);

		if (v != null)
		{
			//adds start position for BFS
			startX = v.sx;
			startY = v.sy;

			//adds solved number of moves and path to the list
			path.addAll(v.path);
			numMoves += v.moves;

			int size = cp.size();
			for (int i = 0; i < size; i++)
			{
				//BFS for additional checkpoints
				sx2[0] = v.x;
				sy2[0] = v.y;
				v = BFS(sx2, sy2, cp.remove(0), m);

				if (v == null)
				{
					solvable = false;
					break;
				}

				//adds solved number of moves and path to the global vars
				path.addAll(v.path);
				numMoves += v.moves;
			}
		} else
		{
			solvable = false;
		}
	}

	//BFS for shortest path solving
	public Vertex BFS(int sx[], int sy[], char target, MapData m)
	{
		ArrayList<Vertex> list = new ArrayList<Vertex>();
		int x;
		int y;
		boolean visited[][] = new boolean[m.width][m.height];
		Vertex v;
		Vertex v2 = new Vertex();

		// initializes queue
		for (int i = 0; i < sx.length; i++)
		{
			list.add(new Vertex(sx[i], sy[i]));
		}

		while (!list.isEmpty())
		{
			//retrieves element from list
			v = list.remove(0);
			x = v.x;
			y = v.y;

			//checks if target has been reached
			if (m.map[x][y] == target)
			{
				return v;
			} else if (m.map[x][y] != 'r' && m.map[x][y] != 'w' && !visited[x][y])
			{
			 // adds possible moves into the queue
				if (y - 1 >= 0) // up
				{
					v2 = new Vertex(v);
					v2.y--;
					v2.moves++;
					v2.path.add(0);
					list.add(v2);
				}
				if (x + 1 < m.width) // right
				{
					v2 = new Vertex(v);
					v2.x++;
					v2.moves++;
					v2.path.add(1);
					list.add(v2);
				}
				if (y + 1 < m.height) // down
				{
					v2 = new Vertex(v);
					v2.y++;
					v2.moves++;
					v2.path.add(2);
					list.add(v2);
				}
				if (x - 1 >= 0) // left
				{
					v2 = new Vertex(v);
					v2.x--;
					v2.moves++;
					v2.path.add(3);
					list.add(v2);
				}
			}
			visited[x][y] = true;
		}

		return null;
	}

	//draws all map elements
	public void drawMap(MapData m)
	{
		// draws border of map
		g.setColor(new Color(51, 51, 68));
		g.fillRect(xMapPosition - 6, yMapPosition - 30, m.width * blockSize + 12, m.height * blockSize + 60);
		g.setColor(Color.white);
		g.fillRect(xMapPosition, yMapPosition, m.width * blockSize, m.height * blockSize);

		//draws all map tiles
		for (int h = 0; h < m.height; h++)
		{
			for (int w = 0; w < m.width; w++)
			{

				int x = w * blockSize + xMapPosition;
				int y = h * blockSize + yMapPosition;

				if (m.map[w][h] == 'o') // open
				{
					g.setColor(Color.white);
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(empty, x, y, c.f);
				} else if (m.map[w][h] == 's') // start
				{

					g.drawImage(start, x, y, c.f);
				} else if (m.map[w][h] == 'r') // solid rocks
				{
					g.setColor(new Color(165, 82, 79));
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(wall, x, y, c.f);
				} else if (m.map[w][h] == 'a') // a checkpoint
				{
					g.setColor(new Color(221, 108, 226));
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(aCheckpoint, x, y, c.f);
				} else if (m.map[w][h] == 'b') // b checkpoint
				{
					g.setColor(new Color(191, 255, 0));
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(bCheckpoint, x, y, c.f);
				} else if (m.map[w][h] == 'c') // c checkpoint
				{
					g.setColor(new Color(192, 84, 102));
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(cCheckpoint, x, y, c.f);
				} else if (m.map[w][h] == 'd') // d checkpoint
				{
					g.setColor(Color.GREEN);
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(dCheckpoint, x, y, c.f);
				} else if (m.map[w][h] == 'e') // e checkpoint
				{
					g.setColor(Color.GREEN);
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(eCheckpoint, x, y, c.f);
				} else if (m.map[w][h] == 'w') // placeable wall
				{
					g.setColor(Color.lightGray);
					g.fillRect(x, y, blockSize, blockSize);
					g.drawImage(wall, x, y, c.f);
				} else if (m.map[w][h] == 'f') // finish
				{
					g.drawImage(finish, x, y, c.f);
				}
			}
		}
	}

	// Draws background
	public void drawBG()
	{
		g.setColor(new Color(18, 18, 18));
		g.fillRect(0, 0, windowWidth, windowHeight);
	}

	//Enables functionality fo the go button and parses list of moves for the solved path
	public void goButton(MapData m) throws Exception
	{
		solve(m);

		if (solvable)
		{
			pathX = new int[numMoves + 1];
			pathY = new int[numMoves + 1];

			pathX[0] = startX;
			pathY[0] = startY;

			// draws appropriate path coordinates
			for (int c = 0; c < numMoves; c++)
			{
				if (path.get(c) == 0) // up
				{
					pathY[c + 1] = pathY[c] - 1;
					pathX[c + 1] = pathX[c];
				} else if (path.get(c) == 1) // right
				{
					pathX[c + 1] = pathX[c] + 1;
					pathY[c + 1] = pathY[c];
				} else if (path.get(c) == 2) // down
				{
					pathY[c + 1] = pathY[c] + 1;
					pathX[c + 1] = pathX[c];
				} else if (path.get(c) == 3) // left
				{
					pathX[c + 1] = pathX[c] - 1;
					pathY[c + 1] = pathY[c];
				}
			}

			pathActive = true;
		}
	}

	//Draws actual solved maze path
	public void drawPath(int speed) throws Exception
	{
		if (pathActive)
		{
			// draws image of direction arrows for the path
			for (int d = 0; d < pathCurrent && d < numMoves; d++)
			{
				if (pathCurrent - d < 20)
				{
					if (path.get(d) == 0) // top
					{
						g.drawImage(up, pathX[d] * blockSize + xMapPosition, pathY[d] * blockSize + yMapPosition, c.f);
					} else if (path.get(d) == 1) // right
					{
						g.drawImage(right, pathX[d] * blockSize + xMapPosition, pathY[d] * blockSize + yMapPosition, c.f);
					} else if (path.get(d) == 2) // down
					{
						g.drawImage(down, pathX[d] * blockSize + xMapPosition, pathY[d] * blockSize + yMapPosition, c.f);
					} else if (path.get(d) == 3) // left
					{
						g.drawImage(left, pathX[d] * blockSize + xMapPosition, pathY[d] * blockSize + yMapPosition, c.f);
					}
				}
			}

			pathCurrent++;

			// updates all variables and score after path is solved
			if (pathCurrent <= numMoves)
			{
				Score = pathCurrent;
			} else
			{
				Score = numMoves;
			}

			if (pathCurrent == numMoves)
			{
				saveScore(name, numMoves);
				getScore();
			}

			if (pathCurrent >= numMoves + 21)
			{
				pathActive = false;
			}

			fpsManager(speed);
		} else
		{
			pathCurrent = 0;
		}
	}

	// Draws additional misc elements
	public void drawExtras(MapData m) throws Exception
	{
		// go button
		g.setColor(Color.white);
		g.drawImage(goButton, xMapPosition + 2, yMapPosition + m.height * blockSize + 5, c.f);

		// score, moves, reset
		if (solvable)
		{
			g.drawString(Score + " Moves", xMapPosition + m.width * blockSize - 90, yMapPosition + m.height * blockSize + 20);
		} else
		{
			g.drawString("Path blocked", xMapPosition + m.width * blockSize - 120, yMapPosition + m.height * blockSize + 20);
		}
		g.drawString(walls + " Walls", xMapPosition + m.width * blockSize - 160, yMapPosition - 8);

		// toggles mouse over color of Reset
		if (mouseX > m.width * blockSize - 80 && mouseX < m.width * blockSize && mouseY > -28 && mouseY < 0) // reset button
		{
			g.setColor(Color.YELLOW);
		} else
		{
			g.setColor(Color.WHITE);
		}
		g.drawString("( Reset )", xMapPosition + m.width * blockSize - 80, yMapPosition - 8);

		// draws current "logged in" user
		g.drawImage(header, 0, 0, c.f); // header
		g.setColor(Color.LIGHT_GRAY);
		g.drawString("Logged in as: ", 15, 30);
		g.setColor(new Color(230, 230, 184));
		g.drawString(name, 140, 30);

		// toggles mouse over color of name change
		if (c.getMouseX() > 940 && c.getMouseY() < 51) // reset button
		{
			g.setColor(Color.YELLOW);
			if (c.getClick())
			{
				openingPage();
			}
		} else
		{
			g.setColor(Color.LIGHT_GRAY);
		}

		g.drawString("( Change user )", 940, 30);

		// toggles mouse over color of new map button
		if (c.getMouseX() > 800 && c.getMouseX() < 910 && c.getMouseY() < 51) // reset button
		{
			g.setColor(Color.YELLOW);
			if (c.getClick())
			{
				fullReset();
			}
		} else
		{
			g.setColor(Color.LIGHT_GRAY);
		}

		g.drawString("( New map )", 800, 30);

	}

	// Draws leaderboard scores
	public void drawLeaderboards(MapData m)
	{
		int w = (windowWidth - xMapPosition - m.width * blockSize) / 2 + xMapPosition + m.width * blockSize - 142;
		int h = (windowHeight - 407) / 2;
		g.drawImage(leaderboards, w, h, c.f);
		g.setColor(Color.white);

		// draws scores and name of top 10 ranks
		for (int i = 1; i <= 10; i++)
		{
			g.drawString(Integer.toString(i), w + 25, h + i * 35 + 10);
			g.drawString(names[i], w + 90, h + i * 35 + 10);
			g.drawString(Integer.toString(scores[i]), w + 240, h + i * 35 + 10);
		}

	}

	//Retrieves score from file
	public void getScore() throws Exception
	{
		Scanner sc = new Scanner(new File(saveFile));

		// reads file
		for (int i = 1; i <= 10; i++)
		{
			names[i] = sc.next();
			scores[i] = sc.nextInt();
		}
	}

	//Saves score to file
	public void saveScore(String n, int s) throws Exception
	{
		boolean exists = false;

		// retrieves data
		for (int i = 1; i <= 10; i++)
		{
			if (n.equals(names[i]))
			{
				exists = true;
				if (s > scores[i])
				{
					scores[i] = s;
				}
			}
		}

		if (!exists)
		{
			scores[11] = s;
			names[11] = n;
		}

		// sorts data using bubblesort
		boolean swap;

		for (int x = 1; x <= 10; x++)
		{
			swap = false;
			for (int y = 1; y <= 11 - x; y++)
			{
				if (scores[y] < scores[y + 1])
				{
					scores[0] = scores[y];
					scores[y] = scores[y + 1];
					scores[y + 1] = scores[0];

					names[0] = names[y];
					names[y] = names[y + 1];
					names[y + 1] = names[0];
					swap = true;
				}
			}
			if (!swap)
			{
				break;
			}
		}

		// saves sorted scores and names to file
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
		writer.write("");
		writer.flush();
		writer = new BufferedWriter(new FileWriter(saveFile, true));

		// writes to file
		for (int i = 1; i <= 10; i++)
		{
			writer.write(names[i] + "\n");
			writer.write(Integer.toString(scores[i]) + "\n");
			writer.flush();
		}
		writer.close();

	}

	//Manages mouse coordinates and states
	public void mouseDetection(MapData m) throws Exception
	{
		//gets mouse coordinates
		mouseX = c.getMouseX() - xMapPosition;
		mouseY = c.getMouseY() - yMapPosition;

		if (c.getClick() == true)
		{
			// Adds/removes walls to map
			// System.out.println(c.getMouseX()+" "+ c.getMouseY());
			if (mouseX > 0 && mouseX < m.width * blockSize && mouseY > 0 && mouseY < m.height * blockSize)
			{
				if (m.map[(int) (mouseX / blockSize)][(int) (mouseY / blockSize)] == 'o' && walls > 0)
				{
					m.map[(int) (mouseX / blockSize)][(int) (mouseY / blockSize)] = 'w';
					walls -= 1;
				} else if (m.map[(int) (mouseX / blockSize)][(int) (mouseY / blockSize)] == 'w')
				{
					m.map[(int) (mouseX / blockSize)][(int) (mouseY / blockSize)] = 'o';
					walls += 1;
				}
			}
			// "Go" button click detection
			else if (mouseX > 2 && mouseX < 36 && mouseY > m.height * blockSize + 5 && mouseY < m.height * blockSize + 25)
			{
				goButton(map);
			}
			// "Reset" button click detection
			else if (mouseX > m.width * blockSize - 80 && mouseX < m.width * blockSize && mouseY > -28 && mouseY < 0)
			{
				for (int h = 0; h < m.height; h++)
				{
					for (int w = 0; w < m.width; w++)
					{
						// clears the map
						if (m.map[w][h] == 'w')
						{
							m.map[w][h] = 'o';
							walls = m.numWalls;
						}
					}
				}
			}
		}
	}
}
