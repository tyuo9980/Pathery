import java.util.ArrayList;

public class Vertex
{
	int x, y;
	int sx, sy;
	int moves;
	ArrayList<Integer> path;

	public Vertex()
	{
	}

	public Vertex(Vertex v2)
	{
		this.x = v2.x;
		this.y = v2.y;
		this.sx = v2.sx;
		this.sy = v2.sy;
		this.moves = v2.moves;
		this.path = new ArrayList<Integer>(v2.path);
	}

	public Vertex(int sX, int sY)
	{
		x = sX;
		y = sY;
		sx = sX;
		sy = sY;
		moves = 0;
		path = new ArrayList<Integer>();
	}
}
