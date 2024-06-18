import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Utility {
	private static boolean forceInput = false;
	
	public static void RankWrite(JPanel screen, int gameScore) {
		JPanel rankWriteOverlay = new JPanel();
		rankWriteOverlay.setVisible(false);
		rankWriteOverlay.setSize(550, 235);
		rankWriteOverlay.setBackground(new Color(58, 58, 74, 200));
		rankWriteOverlay.setLayout(null);
		screen.getParent().add(rankWriteOverlay, new Integer(2));
		
		int absX = (int)(screen.getParent().getSize().width/2) - (int)(rankWriteOverlay.getSize().width/2);
		int absY = (int)(screen.getParent().getSize().height/2) - (int)(rankWriteOverlay.getSize().height/2);
		
		rankWriteOverlay.setLocation(absX, absY);
		rankWriteOverlay.setVisible(true);
		
		JLabel rankWriteMsg = new JLabel("Enter your name: ");
		rankWriteMsg.setFont(new Font("Consolas", Font.PLAIN, 32));
		rankWriteMsg.setForeground(Color.WHITE);
		rankWriteMsg.setHorizontalAlignment(SwingConstants.LEFT);
		rankWriteMsg.setBounds(25, 25, 355, 75);
		rankWriteOverlay.add(rankWriteMsg);
		
		JTextField userName = new JTextField();
		userName.setFont(new Font("Consolas", Font.PLAIN, 18));
		userName.setBounds(335, 35, 190, 50);
		rankWriteOverlay.add(userName);
		userName.setColumns(1);
		userName.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		userName.requestFocus();

		JLabel writeYes = new JLabel("Yes");
		JLabel writeNo = new JLabel("No");
		userName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (userName.getText().length() == 0)
					userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
				else
					userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(89, 180, 123)));
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				userName.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			}
		});
		
		userName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeYes.getMouseListeners()[0].mouseClicked(null);
			}
		});
		
		userName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (forceInput == true) {
					forceInput = false;
					userName.setText("");
					userName.setForeground(Color.BLACK);
				}
				if (userName.getText().length() == 0)
					userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
				else
					if (forceInput == true)
						userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
					else
						userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(89, 180, 123)));
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (userName.getText().length() == 0)
					userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
				else
					if (forceInput == true)
						userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
					else
						userName.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(89, 180, 123)));
			}
		});
		
		writeYes.setFont(new Font("Consolas", Font.PLAIN, 28));
		writeYes.setHorizontalAlignment(SwingConstants.CENTER);
		writeYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
		writeYes.setOpaque(true);
		writeYes.setBackground(new Color(58, 58, 74));
		writeYes.setForeground(new Color(204, 204, 204));
		writeYes.setBounds(85, 135, 150, 60);
		writeYes.setFocusable(false);
		rankWriteOverlay.add(writeYes);
		
		writeYes.addMouseListener(new MouseAdapter() {
			private boolean mousePressState = false;
			@Override
			public void mouseClicked(MouseEvent e) {
				writeYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(89, 180, 123)));
				writeYes.setForeground(new Color(89, 180, 123));
				if (userName.getText().length() == 0) {
					forceInput = true;
					userName.setForeground(Color.LIGHT_GRAY);
					userName.setText("type your name");
					userName.requestFocus();
				} else {
					if (forceInput != true) {
						try {
							FileWriter fw1 = new FileWriter("rank.txt", true); //append
							BufferedWriter bw1 = new BufferedWriter(fw1);
							PrintWriter pw = new PrintWriter(bw1,true);
							
							pw.println(userName.getText().trim() + ":" + gameScore);
							System.out.println("랭킹 등록 완료");
							
							fw1.close(); bw1.close(); pw.close();

							rankWriteOverlay.setVisible(false);
							screen.getParent().remove(rankWriteOverlay);
							screen.getParent().repaint();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mousePressState = true;
				writeYes.setBackground(new Color(33, 46, 68).brighter());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressState = false;
				writeYes.setBackground(new Color(58, 58, 74));
				writeYes.setForeground(new Color(204, 204, 204));
				writeYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
			}
			public void mouseEntered(MouseEvent e) {
				writeYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(89, 180, 123)));
				writeYes.setForeground(new Color(89, 180, 123));
			}
			public void mouseExited(MouseEvent e) {
				if (mousePressState == false) { 
					writeYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
					writeYes.setForeground(new Color(204, 204, 204));
				}
			}
		});
		
		writeNo.setFont(new Font("Consolas", Font.PLAIN, 28));
		writeNo.setHorizontalAlignment(SwingConstants.CENTER);
		writeNo.setBounds(315, 135, 150, 60);
		writeNo.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
		writeNo.setOpaque(true);
		writeNo.setBackground(new Color(58, 58, 74));
		writeNo.setForeground(new Color(204, 204, 204));
		writeNo.setFocusable(false);
		rankWriteOverlay.add(writeNo);

		writeNo.addMouseListener(new MouseAdapter() {
			private boolean mousePressState = false;
			@Override
			public void mouseClicked(MouseEvent e) {
				writeNo.setForeground(new Color(233, 33, 40));
				writeNo.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
				rankWriteOverlay.setVisible(false);
				screen.getParent().remove(rankWriteOverlay);
				screen.getParent().repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mousePressState = true;
				writeNo.setBackground(new Color(33, 46, 68).brighter());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressState = false;
				writeNo.setBackground(new Color(58, 58, 74));
				writeNo.setForeground(new Color(204, 204, 204));
				writeNo.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
			}
			public void mouseEntered(MouseEvent e) {
				writeNo.setForeground(new Color(233, 33, 40));
				writeNo.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
			}
			public void mouseExited(MouseEvent e) {
				if (mousePressState == false) { 
					writeNo.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
					writeNo.setForeground(new Color(204, 204, 204));
				}
			}
		});

		screen.getParent().repaint();
	}
	
	public static void RankList(JPanel screen) {
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 850, 565);
		panel.setBackground(new Color(2, 28, 55));
		panel.setLayout(null);
		screen.add(panel);
		
		JLabel label1 = new JLabel();
		label1.setFont(new Font("Impact", Font.PLAIN, 24));
		label1.setForeground(Color.WHITE);
		JLabel label2 = new JLabel();
		label2.setFont(new Font("Impact", Font.PLAIN, 24));
		label2.setForeground(Color.WHITE);
		
		label1.setBounds(266, 0, 500, 500);
		label2.setBounds(456, 0, 500, 500);
		
		try {
			File path = new File("rank.txt");
			FileReader fr1 = new FileReader(path);
			BufferedReader br1 = new BufferedReader(fr1);
			
			String inText1 = "<html>";
			String inText2 = "<html>";
			String s = null;
			
			//출력을 위한 해시맵 <이름, 점수>
			HashMap<String, Integer> rankList = new HashMap<String,Integer>();
			while((s=br1.readLine()) != null) {
				rankList.put(s.split(":")[0], Integer.parseInt(s.split(":")[1]));
			}
			
			//ArrayList 내림차순 정렬
			List<String> keySetList = new ArrayList<>(rankList.keySet());
			Collections.sort(keySetList, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return rankList.get(o2).compareTo(rankList.get(o1));
				}
			});
	
			int limit=0;
			String ordinalNum[] = { "st", "nd", "rd", "th", "th" };
			for(String key : keySetList) {
				//System.out.println(key +":"+ rankList.get(key)+"점");
				inText1 += (limit+1) + ordinalNum[limit] + " - " + key + "<br /><br />";
				inText2 += rankList.get(key) + "점<br /><br />";
				limit += 1;
				if(limit == 5) {
					inText1 += "</html>";
					inText2 += "</html>";
					break;
				}
			}
			label1.setText(inText1);
			label2.setText(inText2);
			
			fr1.close(); br1.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		panel.add(label1);
		panel.add(label2);
	}	
	
	public static void printBlock(int block) {
		int col = 0;
		for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
			if ((bitmask & block) > 0)
				System.out.print("1 ");
			else
				System.out.print("0 ");
			
			if (++col == 4) {
				col = 0;
				System.out.println();
			}
		}
	}
	
	public static void printMap(boolean[][] map) {
		for (int i=0; i < map.length; i++) {
			for (int j=0; j < map[i].length; j++) {
				if (map[i][j])
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printGraphicMap(CustomButton[][] map) {
		for (int i=0; i < map.length; i++) {
			for (int j=0; j < map[i].length; j++) {
				if (map[i][j] != null)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static int getDec(String binary) {
		int result = 0;
		int bitCount = 0;
		while (binary.length() > bitCount)
			result += Integer.parseInt(binary.charAt(binary.length()-1-bitCount)+"") * Math.pow(2, bitCount++);
		return result;
	}
	
	public static int[] extractRGB(int color) {
    	String r = "",g = "",b = "";
    	int[] rgbArr = new int[3];
		int bitCount = 23;
    	for (int bitmask=0x800000; bitmask > 0; bitmask >>= 1) {
    		if (bitCount >= 16)
    			if ((bitmask & color) > 0)
    				r = r + "1";
    			else
    				r = r + "0";
    		else if (bitCount >= 8)
    			if ((bitmask & color) > 0)
    				g = g + "1";
    			else
    				g = g + "0";
    		else
    			if ((bitmask & color) > 0)
    				b = b + "1";
    			else
    				b = b + "0";
    		bitCount -= 1;
    	}
    	rgbArr[0] = getDec(r);
    	rgbArr[1] = getDec(g);
    	rgbArr[2] = getDec(b);
    	return rgbArr;
	}
    
//	// isMovable 대체 코드
//  public boolean isMovable(int futureX, int futureY, int futureBlock) {
//  	boolean movableFlag = true;
//  	int deltaX = 0, deltaY = 0;
//  	for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
//  		if ((bitmask & futureBlock) > 1) {
//  			int absX = futureX + deltaX;
//  			int absY = futureY + deltaY;
//  			if (absX < 0 || absX >= this.maxWidth || absY < 0 || absY >= this.maxHeight
//  					|| dataGround[absY][absX] == true) {
//  				movableFlag = false;
//  				break;
//  			}
//  		}
//  		if (++deltaX == 4) {
//  			deltaX = 0;
//  			deltaY += 1;
//  		}
//  	}
//  	if (!movableFlag) {
//  		System.out.println("is not movable");
//  	}
//  	return movableFlag;
//  }
	
}
