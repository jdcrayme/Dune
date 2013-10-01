package game.dune.ii;

import java.nio.ByteBuffer;

public class MapGenerator {
	
	private static final int SAND = 0;
	private static final int DUNES = 1;
	private static final int ROCK = 2;
	private static final int MOUNTAINS = 3;
	private static final int SPICE = 4;
	
	static int seed;
	
	static int random()	{
	        byte[] s = ByteBuffer.allocate(4).putInt(seed).array();

	        byte t[] = new byte[3];
	        t[0] = (byte) (~(s[0] >> 2 ^ s[0] ^ s[1] >> 7) << 7 | s[0] >> 1);
	        t[1] = (byte) (s[1] << 1 | s[2] >> 7);
	        t[2] = (byte) (s[2] << 1 | s[0] >> 1 & 1);
	        
	        s[0] = t[0];
	        s[1] = t[1];
	        s[2] = t[2];

	        return s[0] ^ s[1];
	}

	/*
	 * Replaces edges of regions with special numbers
	 * (so converter knows which icons to use)
	 * The technique is similar to --> balanceMap
	 */
	
	static void scanRegions(byte map[][])
	{
	        byte prevln[] = new byte[64];
	        byte currln[] = new byte[64];
	        
	        for (int i = 0; i < 64; i++)
	        	currln[i] = map[0][i];
	        
	        for (int y = 0; y < 64; y++)
	        {
	                for (int i = 0; i < 64; i++)
	                {
	                        prevln[i] = currln[i];
	                        currln[i] = map[y][i];
	                }
	                for (int x = 0; x < 64; x++)
	                {
	                        int middle = map[y][x];
	                        int left   = x != 0 ? currln[x - 1] : middle;
	                        int up     = y != 0 ? prevln[x] : middle;
	                        int right  = x != 63 ? currln[x + 1] : middle;
	                        int down   = y != 63 ? map[y + 1][x] : middle;
	                        int id = 0;
	                        if	(left == middle)
	                        	id = id | 1 << 3;
	                        if (down == middle)
	                        	id = id | 1 << 2;
	                        if (right == middle)
	                        	id = id | 1 << 1;
	                        if (up == middle)
	                        	id = id | 1;
	                        if (middle == ROCK)
	                        {
	                        	if(left == MOUNTAINS)
	                                id |= 1 << 3;
	                        	if(down == MOUNTAINS)
	                        		id |= 1 << 2;
	                        	if(right == MOUNTAINS)
	                        		id |= 1 << 1;
	                        	if(up == MOUNTAINS)
	                        		id |= 1;
	                        }
	                        switch (middle)
	                        {
	            case SAND:
	                                id = 0;
	                                break;
	            case ROCK:
	                                id += 1;
	                                break;
	            case DUNES:
	                                id += 0x11;
	                                break;
	            case MOUNTAINS:
	                                id += 0x21;
	                                break;
	            case SPICE:
	                                id += 0x31;
	                                break;
	                        }
	                        map[y][x] = (byte) id;
	                }
	        }
	}
	
	/*
	 * creates terrain regions by replacing numbers within specified range
	 */
	static void createRegions(byte map[][])
	{
	   int rock = Math.min(Math.max(random() & 0xf, 8), 0xc);
	   int mountains = rock + 4;
	   int dunes = (random() & 3) - 1;
	
	   for (int y = 0; y < 64; y++)
	   {
	      for (int x = 0; x < 64; x++)
	      {
	         int num = map[y][x];
	         int reg;
	         if (num > mountains)
	                         reg = MOUNTAINS;
	         else if (num >= rock)
	                         reg = ROCK;
	         else if (num <= dunes)
	                         reg = DUNES;
	         else
	                         reg = SAND;
	         map[y][x] = (byte) reg;
	      }
	   }
	}
		
	/*
	 * Replaces each byte with arithmetic mean of itself
	 * and all eight neighbors.
	 * As the new numbers are stored in the same array
	 * the original line is stored in 'currln' and then copied
	 * to 'prevln'.
	 */
		
	static void balanceMap(byte map[][])
	{
	   byte prevln[] = new byte[64];
	   byte currln[] = new byte[64];
	   
	   for (int i = 0; i < 64; i++)
	      currln[i] = 0;
	   for (int y = 0; y < 64; y++)
	   {
	      for (int i = 0; i < 64; i++)
	      {
	         prevln[i] = currln[i];
	         currln[i] = map[y][i];
	      }
	      for (int x = 0; x < 64; x++)
	      {
	         int lu = prevln[x-1],   u = prevln[x],   ru = prevln[x+1];
	         int l  = currln[x-1],   c = currln[x],   r  = currln[x+1];
	         int rd = map[y+1][x+1], d = map[y+1][x], ld = map[y+1][x-1];
	         if (x==0)
	                         lu = l = ld = c; /* left edge */
	         else if (x == 63)
	                         ru = r = rd = c; /* right edge */
	         if (y==0)
	                         lu = u = ru = c; /* top edge*/
	         else if (y == 63)
	                         ld = d = rd = c; /* bottom edge */
	         map[y][x] = (byte) ((lu + u + ru + r + rd + d + ld + l + c) / 9);
	      }
	   }
	}
	
	static final int offsets2[] =
	{
	        0,0,4,0, 4,0,4,4, 0,0,0,4, 0,4,4,4, 0,0,0,2, 0,2,0,4,
	        0,0,2,0, 2,0,4,0, 4,0,4,2, 4,2,4,4, 0,4,2,4, 2,4,4,4,
	        0,0,4,4, 2,0,2,2, 0,0,2,2, 4,0,2,2, 0,2,2,2, 2,2,4,2,
	        2,2,0,4, 2,2,4,4, 2,2,2,4,
	        0,0,4,0, 4,0,4,4, 0,0,0,4, 0,4,4,4, 0,0,0,2, 0,2,0,4,
	        0,0,2,0, 2,0,4,0, 4,0,4,2, 4,2,4,4, 0,4,2,4, 2,4,4,4,
	        4,0,0,4, 2,0,2,2, 0,0,2,2, 4,0,2,2, 0,2,2,2, 2,2,4,2,
	        2,2,0,4, 2,2,4,4, 2,2,2,4
	};
	
	/*
	 * "spreads" the matrix by replacing empty bytes with arithmetic
	 * mean of two neighbors. Offsets needed to locate neighbors
	 * are stored in 'offsets2' array.
	 * in this part the generator has a tend to use 65 row of the map.
	 * also the right border bytes are improperly calculated.
	 */
	
	static void spreadMatrix(byte map[][])
	{
	   int diag = 0;
	   for (int y = 0; y < 64; y += 4)
	   {
	      for (int x = 0; x < 64; x += 4)
	      {
	    	  int offs = diag!=0 ? 0 : 84;
	                 
	         diag = 1 - diag;
	         for (int i = 0; i < 21; i++)
	         {
	            int bx = x + offsets2[offs+0];
	                        int by = y + offsets2[offs+1];
	            int ex = x + offsets2[offs+2];
	                        int ey = y + offsets2[offs+3];
	            int medx = bx + ex >> 1;
	                        int medy = by + ey >> 1;
	            if (medx + 64 * medy < 64 * 64)
	               map[medy][medx] = (byte) (map[by][bx & 0x3f] + map[ey][ex & 0x3f] + 1 >> 1);
	            offs += 4;
	         }
	      }
	   }
	}
	
	/*
	 * copies matrix on map
	 * the height of 65 should be used to be best compatible
	 * (the generator has a tend to use that extra row in spreadMatrix :)
	 */
	static void copyMatrix(byte [] matrix, byte map[][])
	{
	        for (int y = 0; y < 65; y++)
	        {
	                for (int x = 0; x < 64; x++)
	                        map[y][x] = 0;
	        }
	        int i = 0;
	        for (int y = 0; y < 64; y += 4)
	        {
	                for (int x = 0; x < 64; x += 4)
	                        map[y][x] = matrix[i++];
	        }
	}
	
	static void createMatrix(byte [] matrix)
	{
	        for (int i = 0; i < 16 * 17; i++)
	        {
	                int v = random();
	                matrix[i] = (byte) Math.min(v & 0xf, 10);
	        }
	        matrix[272] = 0;
	}
	
	final static int offsets[] =
	{
	        0,
	        -1,
	        1,
	        -16,
	        16,
	        -17,
	        17,
	        -15,
	        15,
	        -2,
	        2,
	        -32,
	        32,
	        -4,
	        4,
	        -64,
	        64,
	        -30,
	        30,
	        -34,
	        34
	};

	static void addNoise1(byte [] matrix)
	{
		for (int count = random() & 0xf; count >= 0 ; count--)
		{
			int ncell = random() & 0xff;
			for (int i = 0; i < 21; i++)
			{
				int cell = Math.min(Math.max(ncell + offsets[i], 0), 16 * 17);
				matrix[cell] = (byte) (matrix[cell] + random() & 0xf);
			}
		}
	}
	
	static void addNoise2(byte [] matrix)
	{
		for (int count = random() & 3; count >= 0; count--)
		{
			int ncell = random() & 0xff;
			for (int i = 0; i < 21; i++)
				matrix[Math.min(Math.max(ncell + offsets[i], 0), 16 * 17)] = (byte) (random() & 3);
		}
	}
	
	public static byte[][] generateMap(int seed)
	{
		MapGenerator.seed = seed;
		
        byte matrix [] = new byte[16 * 17 + 1];
        createMatrix(matrix);
        addNoise1(matrix);
        addNoise2(matrix);
		
        byte map [][] = new byte[65][64];
        copyMatrix(matrix, map);
        spreadMatrix(map);

        balanceMap(map);
        createRegions(map);
        scanRegions(map);

        return map;
	}
}
