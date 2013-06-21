public class MapData
{
	int width, height;
	char map[][];
	int numPlaceables;
	int numRocks;
	int numWalls;
	int numCheckpoints;
	int numTeleports;
	int numStarts;
	int numFinishes;

	public MapData(String code)
	{
		String s[] = code.split("\"");
		String s2[] = s[s.length - 2].split("\\.");
		String s3[] = s[s.length - 2].split(".:");
		String mapcode[] = s3[1].split("\\.");
		String dim[] = s2[0].split("x");

		width = Integer.parseInt(dim[0]);
		height = Integer.parseInt(dim[1]);
		numCheckpoints = Integer.parseInt(s2[1].substring(1));
		numRocks = Integer.parseInt(s2[2].substring(1));
		numWalls = Integer.parseInt(s2[3].substring(1));
		numTeleports = Integer.parseInt(s2[4].substring(1));

		map = new char[width][height];

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				map[j][i] = 'o';
			}
		}

		int x = 0;
		int y = 0;
		int dist = -1;

		for (int i = 0; i < mapcode.length; i++)
		{
			dist += Integer.parseInt(mapcode[i].substring(0, mapcode[i].length() - 1)) + 1;
			x = dist % width;
			y = dist / width;
			map[x][y] = mapcode[i].charAt(mapcode[i].length() - 1);

			if (map[x][y] == 's')
			{
				numStarts++;
			} else if (map[x][y] == 'f')
			{
				numFinishes++;
			}
		}
	}

	public void print()
	{
		for (int yy = 0; yy < height; yy++)
		{
			for (int xx = 0; xx < width; xx++)
			{
				System.out.print(map[xx][yy]);
			}
			System.out.println();
		}
	}
}
