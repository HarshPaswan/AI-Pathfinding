
import java.util.*;
import java.util.Timer;
import java.awt.event.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class PathFinder {

	JFrame frame;
	Scanner scan = new Scanner(System.in);
	private int numCells = 20;
	private int permStartX = -1;
	private int permStartY = -1;
	private int permEndX = -1;
	private int permEndY = -1;
	private int startX = -1;
	private int startY = -1;
	private int counter = 0;
	int endX = -1;
	int endY = -1;
	private int steps;
	private int livesCount;
	private int width = 850;
	private final int HEIGHT = 650;
	private final int MAPSIZE = 600;
	private int cellSize = MAPSIZE/numCells;
	private boolean solve = false;
	private int numObstacles;
	private File[] maps;
	private int mapIndex = 0;
	Node[][] map;
	Random r = new Random();
	private String[] keys;
	private String[] dummys;
	private int keyIndex = 0;
	private int keyMoves = 0;
	private String key = "";
	private String encryptLine = "";
	private String decryptLine = "";
	private String dummy = "x";
	private int usableX;
	private int usableY;
	private int space;
	private boolean change = false;
	JLabel stepsLabel = new JLabel("Steps: " + steps);
	JLabel keyL = new JLabel("Key: " + key);
	JLabel encrL = new JLabel("Encrypted: " + encryptLine);
	JLabel decrL = new JLabel("Decrypted: " + decryptLine);
	JLabel mapL = new JLabel("Map: " + (mapIndex+1));
	JLabel lives = new JLabel("Lives: "+livesCount);
	Map canvas;
	private long count = 0;
	JLabel timerL = new JLabel("Timer count: " + count);
	JLabel ownKey = new JLabel("Own Key");
	JLabel keyChanges = new JLabel("Key changed");
	JTextField addOwn = new JTextField("");
	JButton searchButton = new JButton("Start Search");
	JButton resetButton = new JButton("Reset");
	JButton genMapButton = new JButton("Generate Map");
	JButton clearMapButton = new JButton("Clear Map");
	JButton enterKey = new JButton("Enter");
	JPanel toolPanel = new JPanel();
	Border lower = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	FileWriter fw = new FileWriter("PathFinderData.txt");
	BufferedWriter x = new BufferedWriter(fw);

	public PathFinder(String fileName1, String fileName2, String fileName3) throws IOException {
		maps = new File[3];
		maps[0] = new File(fileName1);
		maps[1] = new File(fileName2);
		maps[2] = new File(fileName3);
		space = 0;
		keys = new String[] {"w", "k", "z"}; //w is first key
		dummys = new String[] {"x", "a", "y"};
		clear();
		livesCount = 3;
		System.out.println("How many obstacles? (1-10)");
		numObstacles = scan.nextInt();
		initializeGUI();
		steps = 0;
	}
	public void generateMap() throws IOException {

		BufferedReader fr = new BufferedReader(new FileReader(maps[mapIndex]));
		for (int i = 0; i < numCells; i++) {
			String sl = fr.readLine();
			for (int j = 0; j < numCells; j++) {
				map[i][j] = new Node(Integer.parseInt("" + sl.charAt(j)), i, j);
				if (map[i][j].getType() == 0) {
					permStartX = startX = i;
					permStartY = startY = j;
				}
				else if (map[i][j].getType() == 1) {
					permEndX = endX = i;
					permEndY = endY = j;
				}
			}
		}
		for (int i = 0; i < numObstacles; i+=0) {
			int x = r.nextInt(numCells-2);
			int y = r.nextInt(numCells-2);
			if (map[x][y].getType() == 3 && x > 1 && y > 1 && x < numCells-1 && y < numCells-1) {
				map[x][y].setType(2);
				map[x-1][y].setType(2);
				map[x+1][y].setType(2);
				i++;
			}
		}
		int[] types = new int[]{5, 7, 9};
		for (int i = 0; i <= 2; i+=0) {
			int x = r.nextInt(numCells);
			int y = r.nextInt(numCells);
			if (map[x][y].getType() == 3 && x > 1 && y > 1 && x < numCells-1 && y < numCells-1) {
				map[x][y].setType(types[i]);
				i++;
			}
			if (map[x][y].getType() == 5) {
				map[x][y].setKey(keys[keyIndex]);
			}
			if (map[x][y].getType() == 9) {
				map[x][y].setEncrypt(encrypt(keys[keyIndex]));
			}
			if (map[x][y].getType() == 7) {
				map[x][y].setDummy("x");
			}
		}
		fr.close();
	}
	public void clear() {
		resetXY();
		map = new Node[numCells][numCells];
		for (int r = 0; r < numCells; r++) {
			for (int c = 0; c < numCells; c++) {
				map[r][c] = new Node(3, r, c);
			}
		}
		resetSome();
	}
	public void repeat() throws IOException{
		if (mapIndex < 2) {
			change = false;
			solve = false;
			mapIndex++;
			keyIndex++;
			try {
				Thread.sleep(4000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			clear();
			generateMap();
			mapL.setText("Map: " + (mapIndex+1));
			update();
			key = "";
			encryptLine = "";
			decryptLine = "";
			count = 0;
			livesCount = 3;
			keyL.setText("Key: " + key);
			encrL.setText("Encrypted: " + encryptLine);
			decrL.setText("Decrypted: " + decryptLine);
			timerL.setText("Timer count: " + count);
			lives.setText("Lives: " + livesCount);
			if((startX > -1 && startY > -1) && (endX > -1 && endY > -1))
				solve = true;
			startSearch();
		}
		else if (mapIndex == 2){
			JFrame f = new JFrame();
			f.setVisible(true);
			f.setResizable(false);
			f.setSize(300, 300);
			f.setTitle("AI Path Finder Success");
			JLabel Success = new JLabel("Success");
			Success.setBounds(100,100,600,100);
			Success.setFont(new Font("Serif", Font.PLAIN, 50));
			f.add(Success);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//extras
			f.getContentPane().setLayout(null);
			f.setLocationRelativeTo(null);
		}
	}
	//Private methods
	private void initializeGUI() {
		frame = new JFrame();
		frame.setVisible(true);
		frame.setResizable(false); //prevent resizing which can sometimes cause confusion
		frame.setSize(width, HEIGHT);
		frame.setTitle("AI Path Finder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//extras
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);

		toolPanel.setBorder(BorderFactory.createTitledBorder(lower, "Map Controls"));
		int s = 25; 
		int b = 45;

		toolPanel.setLayout(null);
		toolPanel.setBounds(10, 10, 210, 600);

		searchButton.setBounds(40, s, 120, 25);
		toolPanel.add(searchButton);
		s+=b;

		resetButton.setBounds(40, s, 120, 25);
		toolPanel.add(resetButton);
		s+=b;

		genMapButton.setBounds(40, s, 120, 25);
		toolPanel.add(genMapButton);
		s+=b;

		clearMapButton.setBounds(40, s, 120, 25);
		toolPanel.add(clearMapButton);
		s+=40;

		stepsLabel.setBounds(15,s,100,25);
		stepsLabel.setBorder(BorderFactory.createTitledBorder(lower));
		toolPanel.add(stepsLabel);
		s+=b;

		keyL.setBounds(15,s,100,25);
		keyL.setBorder(BorderFactory.createTitledBorder(lower));
		toolPanel.add(keyL);
		s+=b;

		encrL.setBounds(15, s, 150, 25);
		encrL.setBorder(BorderFactory.createTitledBorder(lower));
		toolPanel.add(encrL);
		s+=b;

		decrL.setBounds(15, s, 150, 25);
		decrL.setBorder(BorderFactory.createTitledBorder(lower));
		toolPanel.add(decrL);
		s+=b;

		timerL.setBounds(15, s, 150, 25);
		timerL.setBorder(BorderFactory.createTitledBorder(lower));
		toolPanel.add(timerL);
		s+=b;

		ownKey.setBounds(15, s, 50, 25);
		addOwn.setBounds(75, s, 50, 25);
		enterKey.setBounds(130, s, 65, 25);
		toolPanel.add(ownKey);
		toolPanel.add(addOwn);
		toolPanel.add(enterKey);
		s+=60;

		mapL.setBounds(15, s, 200, 25);
		mapL.setFont(new Font("Serif", Font.PLAIN, 20));
		toolPanel.add(mapL);
		s+=b;

		lives.setBounds(20,s,100,25);
		lives.setBorder(BorderFactory.createTitledBorder(lower));
		toolPanel.add(lives);
		s+=b;
		
		
		frame.getContentPane().add(toolPanel);

		canvas = new Map();
		canvas.setBounds(230, 10, MAPSIZE+1, MAPSIZE+1);
		frame.getContentPane().add(canvas);

		searchButton.addActionListener(new ActionListener() {		//ACTION LISTENERS
			@Override
			public void actionPerformed(ActionEvent e) {
				resetSome();

				if((startX > -1 && startY > -1) && (endX > -1 && endY > -1)) {
					solve = true;
				}
			}

		});
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMap();
				update();
			}
		});
		genMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					generateMap();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				update();
			}
		});
		clearMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
				update();
			}
		});
		enterKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s.equals("Enter")) {
					keys[mapIndex] = addOwn.getText();
					keyChanges.setBounds(15, 445, 100, 25);
					toolPanel.add(keyChanges);
					addOwn.setText("");
				}
			}
		});
		startSearch();
	}
	//Encryption and Decryption
	public String encrypt(String k) {
		String encodeLine = "";
		String coords = permEndX + " "+ permEndY;
		for (int i = 0; i < coords.length(); i++) {
			int a1 = (int) coords.charAt(i);
			int a2 = (int) k.charAt(0) - 65;
			int a3 = a1 + a2;
			if (a3 >= 128) { 
				a3 -= 96;

			}
			char c = (char) a3;
			encodeLine += c;
		}
		return encodeLine;
	}
	public void decrypt(String line) {
		String decrypt = "";
		for(int i = 0; i < line.length(); i++) {
			int a1 = (int) line.charAt(i);
			int a2 = key.charAt(0)-65;
			int a3 = a1-a2;
			if(a3 < 32)
				a3+=96;
			decrypt += (char)a3;
		}
		usableX = Integer.parseInt(""+(decrypt.charAt(0))+decrypt.charAt(1));
		usableY = Integer.parseInt(""+(decrypt.charAt(3))+decrypt.charAt(4));
	}
	//Reset Methods
	public void resetMap() {
		for (int r = 0; r < numCells; r++) {
			for (int c = 0; c < numCells; c++) {
				Node curr = map[r][c];
				if (curr.getType() == 4)
					map[r][c] = new Node(3, r, c);
			}
		}
		if (startX > -1 && startY > -1) {
			map[startX][startY] = new Node(0, startX, startY);
			map[startX][startY].setMoves(0);
		}
		if (endX > -1 && endY > -1) 
			map[endX][endY] = new Node(1, endX, endY);
		resetSome();
	}
	public void  resetXY() {
		endX = permEndX;
		endY = permEndY;
		startX = permStartX;
		startY = permStartY;
	}
	public void resetSome() {
		solve = false;
		steps = 0;
	}
	//start
	public void startSearch() {
		if (solve) {
			AStar();
		}
		pause();
	}
	public void pause() { //pauses the state of the bot
		int p = 0;
		while(!solve) {
			p++;
			if (p > 500)
				p = 0;
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		startSearch();
	}
	//update method
	public void update() {
		cellSize = MAPSIZE/numCells;
		canvas.repaint();
		stepsLabel.setText("Steps: "+ steps);
		lives.setText("Lives: "+ livesCount);
	}
	public void delay() {
		try {
			Thread.sleep(30);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	//Algorithm
	public void AStar() {
		long startTime = System.nanoTime();
		long stopTime = System.nanoTime();
		count = stopTime - startTime;
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.add(map[startX][startY]);
		while(solve) {
			if(nodes.size() <= 0) {
				solve = false;
				break;
			}
			int moves = nodes.get(0).getMoves()+1;
			ArrayList<Node> checked = neighbors(nodes.get(0),moves);
			if(checked.size() > 0) {
				nodes.remove(0);
				nodes.addAll(checked);
				update();
				delay();
			} else {
				nodes.remove(0);
			}
			sort(nodes);
		}
		try {
			repeat();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public ArrayList<Node> sort(ArrayList<Node> sort) {	
		int c = 0;
		while(c < sort.size()) {
			int s = c;
			for(int i = c+1; i < sort.size(); i++) {
				if(sort.get(i).getEuclidDist()+sort.get(i).getMoves() < sort.get(s).getEuclidDist()+sort.get(s).getMoves())
					s = i;
			}
			if(c != s) {  //Swaps the values at c and s in the sort arraylist
				Node temp = sort.get(c);
				sort.set(c, sort.get(s));
				sort.set(s, temp);
			}	
			c++;
		}
		return sort;
	}
	public ArrayList<Node> neighbors(Node curr, int moves) {
		ArrayList<Node> neighbors = new ArrayList<Node>();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int tempX = curr.getX() + i;
				int tempY = curr.getY() + j;
				if ((tempX > -1 && tempX < numCells) && (tempY > -1 && tempY < numCells)) {
					Node neighbor = map[tempX][tempY];
					if (neighbor.getType() == 2) {
						break;
					}
					if ((neighbor.getMoves() == -1 || neighbor.getMoves() > moves) && neighbor.getType() != 2)    //(trap invisbile to bot but visible to player)
					{
						checkNode(neighbor, curr.getX(), curr.getY(), moves);
						neighbors.add(neighbor);
					}    

				}
			}
		}

		return neighbors;
	}
	public void checkNode(Node curr, int x, int y, int moves) {
		if (curr.getType()==3)
			curr.setType(4);
		curr.setLastNode(x, y);
		curr.setMoves(moves);
		steps++;
		count++;
		if (curr.getType() == 5) {
			key = curr.getKey();
			keyL.setText("Key: " + key);
			curr.setType(10);
			backtrack(curr.getX(), curr.getY(), moves);
			keyMoves++;
			startX = x+1;
			startY = y+1;
			AStar();
		}
		if (curr.getType() == 9) {
			encryptLine = curr.getEncrypt();
			encrL.setText("Encrypted: " + encryptLine);
			curr.setType(10);
			backtrack(curr.getX(), curr.getY(), moves);
			keyMoves++;
			startX = x+1;
			startY = y+1;
			AStar();
		}
		if (curr.getType() == 7) {
			curr.setType(10);
			backtrack(curr.getX(), curr.getY(), moves);
			startX = x+1;
			startY = y+1;
			AStar();
		}
		if (key.length() > 0 && encryptLine.length()>0) {
			decrypt(encryptLine);
			decrL.setText("Decrypted: " + "(" + usableX + ", " + usableY + ")");
		}
		if (curr.getType() == 6) {
			JFrame f = new JFrame();
			f.setVisible(true);
			f.setResizable(true);
			f.setSize(150+counter, 150+counter);
			f.setTitle("Trap!");
			JLabel o = new JLabel("You lost 1 life Ouch!");
			o.setBounds(100+counter,100+counter,600+counter,100+counter);
			//Success.setBounds(100,100,300,100);
			counter += 50;
			o.setFont(new Font("Serif", Font.PLAIN, 20));
			f.add(o);
			delay();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO: handle exception
			}
			f.setVisible(false);
			livesCount--;
			//lives.setText("lives: " + livesCount);
			curr.setType(11);
			//update();
		}
		if (curr.getX() == endX && curr.getY() == endY) { 
			change = true;
		}
		if (curr.getX() == usableX && curr.getY() == usableY && usableX != 0 && usableY !=0) {
			backtrack(curr.getLastX(), curr.getLastY(), moves);
			timerL.setText("Timer count: " + count/100 + " seconds");
			change = false;
			try {
				write();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if ((change && keyMoves == 2)) {
			backtrack(curr.getLastX(), curr.getLastY(), moves);
			timerL.setText("Timer count: " + count/100 + " seconds");
			try {
				write();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	public void backtrack(int lastX, int lastY, int moves) {
		while (moves>=0) {
			Node curr = map[lastX][lastY];
			curr.setType(8);
			lastX = curr.getLastX();
			lastY = curr.getLastY();
			moves--;
		}
	}
	public void write() throws IOException {
		x.write("Map " + (mapIndex+1)); x.newLine();
		x.write("Steps to solve map: " + steps); x.newLine();
		x.write("Time taken in Seconds: " + (count/100)); x.newLine();
		if (mapIndex == 2) {
			x.close();
		}
	}
	class Map extends JPanel {	

		public Map() {

		}

		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			for(int x = 0; x < numCells; x++) {	
				for(int y = 0; y < numCells; y++) {
					switch(map[x][y].getType()) {
					case 0:
						g.setColor(Color.GREEN);
						break;
					case 1:
						g.setColor(Color.RED);
						break;
					case 2:
						g.setColor(Color.BLACK);
						break;
					case 3:
						g.setColor(Color.WHITE);
						break;
					case 4:
						g.setColor(Color.CYAN);
						break;
					case 5:
						g.setColor(Color.ORANGE);
						break;
					case 6:
						g.setColor(Color.WHITE);
						break;
					case 7:
						g.setColor(Color.ORANGE);
						break;
					case 8:
						g.setColor(Color.YELLOW);
						break;
					case 9: 
						g.setColor(Color.ORANGE);
						break;
					case 10:
						g.setColor(Color.ORANGE);
						break;
						//case 11:
						//g.setColor(new Color(138,43,226);
					}
					g.fillRect(x*cellSize,y*cellSize,cellSize,cellSize);
					g.setColor(Color.BLACK);
					g.drawRect(x*cellSize,y*cellSize,cellSize,cellSize);
					if (map[x][y].getType()==10) {
						g.setColor(Color.BLACK);
						g.drawLine(x*cellSize, y*cellSize, (x*cellSize)+cellSize, (y*cellSize)+cellSize);
						g.drawLine((x*cellSize)+cellSize, (y*cellSize), (x*cellSize), (y*cellSize)+cellSize);

					}
					if (map[x][y].getType()==0) {
						g.drawLine(x*cellSize, y*cellSize, (x*cellSize)+cellSize, (y*cellSize)+cellSize);
						g.drawLine((x*cellSize)+cellSize, (y*cellSize), (x*cellSize), (y*cellSize)+cellSize);
					}
					if (map[x][y].getType()==11) {
						g.setColor(new Color(138,42,226));
						g.fillRect(x*cellSize,y*cellSize,cellSize,cellSize);
						g.setColor(Color.BLACK);
						g.drawRect(x*cellSize,y*cellSize,cellSize,cellSize);

						g.drawLine(x*cellSize, y*cellSize, (x*cellSize)+cellSize, (y*cellSize)+cellSize);
						g.drawLine((x*cellSize)+cellSize, (y*cellSize), (x*cellSize), (y*cellSize)+cellSize);

					}

				}
			}
		}
	}

	class Node
	{
		// 0 = start 1 = finish 2 = plain obstacle 3 = empty cell 4 = has been checked 5 = key clue 6 = invisible trap (invisible to bot) 7 = dummy clue 8 = final 9 = encrypted clue 10 = clues have been checked
		// attributes, 11= trap has been triggered
		private int cellType = 0;
		private int moves; // moves
		private int x;
		private int y; 
		private int lastX;//final x pos
		private int lastY; //final y pos
		private double dToEnd = 0; // distance to end 
		private String key;
		private String encryptedLine;
		private String dummy;
		public Node() {
			cellType = 0;
			this.x = 0;
			this.y = 0;
			moves = 0;
			key = "";
			encryptedLine = "";
			dummy = "";
		}
		public Node(int type, int x, int y) //CONSTRUCTOR
		{	
			cellType = type;
			this.x = x;
			this.y = y;
			moves = -1;
			key = "";
			encryptedLine = "";
			dummy = "";
		}

		//GET METHODS
		public double getEuclidDist() {		//CALCULATES THE EUCLIDIAN DISTANCE TO THE FINISH NODE
			int xdif = Math.abs(endX);
			int ydif = Math.abs(endY);
			dToEnd = Math.sqrt((xdif*xdif)+(ydif*ydif));
			return dToEnd;
		}

		public int getX() {return x;}		
		public int getY() {return y;}
		public int getLastX() {return lastX;}
		public int getLastY() {return lastY;}
		public int getType() {return cellType;}
		public int getMoves() {return moves;}
		public String getKey() {return key;}
		public String getEncrypt() {return encryptedLine;}
		public String getDummy() {return dummy;}



		//SET METHODS

		public void setType(int type) {cellType = type;}	
		public void setLastNode(int x, int y) {lastX = x; lastY = y;}
		public void setMoves(int moves) {this.moves = moves;}
		public void setKey(String k) {key = k;}
		public void setEncrypt(String e) {encryptedLine = e;}
		public void setDummy(String d) {dummy = d;}
	}

}
