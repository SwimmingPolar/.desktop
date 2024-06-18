import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

// 게임이 이루어지는 과정을 총괄
public class GameHelper extends Thread {
    // 7개의 테트리스 블록
    private HashMap<Character, Block> blocks;

    // 전체 7개 테트리스 블록의 n개의 묶음 (n * 7)
    // 다음 블록을 여기서 가져온다. (다음 블록이 완전 랜덤이 아님)
    private ArrayList<Block> blockBasket;
    private int blockRepeat;

    // 현재 블록과 다음 블록
    private Block currentBlock, nextBlock;

    // 데이터를 매핑하고 블록들의 움직임을 조절
    private Playground playground;

    // 테트리스 창 폭과 높이
    private int maxWidth, maxHeight;

    // 게임 속도 (n초)
    private int gameSpeed;
    private int initialGameSpeed;

    // 게임 점수, 레벨, 제거 라인
    private int gameLevel;
    private int gameScore;
    private int gameLine;

    // 정보를 표시 할 텍스트필드
    private JTextField level;
	private JTextField score;
	private JTextField line;

    // 게임 화면
    private JPanel screen;
    private JPanel gameScreen;
    private JPanel blockScreen;
    private JPanel infoScreen;
    
    // 게임 상태
    private boolean gameState;
    
    public GameHelper(JPanel screen) {
        this.screen = screen;
        gameScreen = new JPanel();
        blockScreen = new JPanel();
        infoScreen = new JPanel();
        gameLevel = 1;
        gameScore = 0;
        gameLine = 0;
        // 게임 속도 750ms
        gameSpeed = 750;
        initialGameSpeed = gameSpeed;
        maxWidth = 10; maxHeight = 20;
        playground = new Playground(gameScreen, maxWidth, maxHeight);
        blocks = new HashMap<Character, Block>();
        blocks.put('I', new Block('I', new int[]{ 0x0F00, 0x2222, 0x00F0, 0x4444 }, new Color(44, 151, 222)));
        blocks.put('J', new Block('J', new int[]{ 0x8E00, 0x6440, 0x0E20, 0x44C0 }, new Color(232, 127, 5)));
        blocks.put('L', new Block('L', new int[]{ 0x2E00, 0x4460, 0x0E80 ,0xC440 }, new Color(34, 87, 189)));
        blocks.put('O', new Block('O', new int[]{ 0xCC00, 0xCC00, 0xCC00 ,0xCC00 }, new Color(243, 197, 1)));
        blocks.put('S', new Block('S', new int[]{ 0x6C00, 0x4620, 0x06C0, 0x8C40 }, new Color(233, 75, 53)));
        blocks.put('T', new Block('T', new int[]{ 0x4E00, 0x4640, 0x0E40, 0x4C40 }, new Color(156, 86, 182)));
        blocks.put('Z', new Block('Z', new int[]{ 0xC600 ,0x2640, 0x0C60, 0x4C80 }, new Color(0, 190, 157)));
        blockRepeat = 4;
        blockBasket = new ArrayList<Block>();
        currentBlock = getNextBlock(); nextBlock = getNextBlock();
        
    	// 키보드 리스너 등록
        KeySniper();
    }

    // 다음 블록 생성
    private Block getNextBlock() {
        if (blockBasket.size() == 0)
            for (int i = 0; i < blockRepeat; i++)
                for (Map.Entry<Character, Block> blockEntry : blocks.entrySet()) {
                	Block block = blockEntry.getValue();
                    blockBasket.add(new Block(block.getShape(), block.getShapes(), block.getColor()));
                }
        int randomIndex = (int)(Math.floor(Math.random() *  blockBasket.size()));
        return blockBasket.remove(randomIndex);
    }

    // 입력 받은 키를 playground에 전달
    // playground.move(keyCode);
    public void passKeyCode(int keyCode) {
        switch (keyCode) {
            // Left
            case 37:
                playground.move("LEFT");
                break;
            // Right
            case 39:
                playground.move("RIGHT");
                break;
            // Down
            case 40:
                playground.move("DOWN");
                break;
            // Up (rotate)
            case 38:
                playground.rotate();
                break;
            // Spacebar (immediate pulldown)
            case 32:
                playground.move("PULLDOWN");
                break;
            default:
                break;
        }
    }

    public boolean isPlayingGame() {
    	if (this.gameState == true)
    		return true;
    	else
    		return false;
    }
    
    public void KeySniper() {
    	KeyListener[] keyListeners = screen.getKeyListeners();
    	if (keyListeners != null)
    		for (KeyListener keyListener : keyListeners)
    			screen.removeKeyListener(keyListener);
    		
    	 screen.setFocusable(true);
         screen.requestFocusInWindow();
         screen.addKeyListener(new KeyAdapter() {
         	boolean rotationLock = false;
         	boolean keyLock = false;
         	Timer timer = new Timer();
            public void keyPressed(KeyEvent e) {
                 if (e.getKeyCode() == 38) {
                     if (!rotationLock) {
                         rotationLock = true;
                         passKeyCode(e.getKeyCode());
                     }
                 }
                 // 회전 이외의 연속 입력 허용
                 else {
                     rotationLock = false;
                     if (!keyLock) {
                     	keyLock = true;
                     	passKeyCode(e.getKeyCode());
                     	timer.schedule(new TimerTask() {
                     		public void run() {
                     			keyLock = false;
                     		}
                     	}, 40);
                     }
                 }
             }
             public void keyReleased(KeyEvent e) {
                 // 회전 잠금 해체
                 rotationLock = false;
             }
         });
    }
    
    public void renderNextBlock() {
    	blockScreen.removeAll();
    	blockScreen.repaint();

    	CustomButton[] graphicBlock = new CustomButton[4]; 
    	
    	for (int i=0; i < 4; i++) {
        	Color miniBlockColor = nextBlock.getColor();
            graphicBlock[i] = new CustomButton(miniBlockColor);
            blockScreen.add(graphicBlock[i]);
    	}
    	
    	int miniBlockWidth = graphicBlock[0].getSize().width;
    	int miniBlockHeight = graphicBlock[0].getSize().height;
    	// 6 => border(3) * 2
    	int blockScreenX = blockScreen.getSize().width - 6;
    	int blockScreenY = blockScreen.getSize().height - 6;
    	int baseX = 0, baseY = 0;
    	
    	// 막대기 모양 블록
    	if (nextBlock.getCurrentShape() == 0x0F00) { 
    		baseX = (int)(blockScreenX/2) - miniBlockWidth*2;
    		baseY =  (int)(blockScreenY/2) - (int)(miniBlockHeight*1.5);
    	}
    	// 네모 모양 블록
    	else if (nextBlock.getCurrentShape() == 0xCC00) {
    		baseX = (int)(blockScreenX/2) - miniBlockWidth;
    		baseY = (int)(blockScreenY/2) - miniBlockHeight;
    	}
    	// 나머지 블록
    	else {
    		baseX = (int)(blockScreenX/2) - (int)(miniBlockWidth*1.5);
    		baseY = (int)(blockScreenY/2) - miniBlockHeight;
    	}

    	int deltaX = 0, deltaY = 0, blockCounter = 0;
    	for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
    		if ((bitmask & nextBlock.getCurrentShape()) > 0) {
    			int absX = baseX + (deltaX*20);
    			int absY = baseY + (deltaY*20);
    			graphicBlock[blockCounter].setLocation(absX, absY);
    			blockCounter += 1;
    		}
    		if (++deltaX == 4) {
    			deltaX = 0;
    			deltaY += 1;
    		}
    	}
    }
    
    public int getScore() {
    	return gameScore;
    }
    
    private void pauseThread() {
    	this.suspend();
    }

    private void resumeThread() {
    	this.resume();
    }
    
    public void gamePause() {
    	playground.enableCoordinateChangable(false);
    	pauseThread();
    }
    
    public void gameResume() {
    	// 게임 재개 시, 좌표 수정 가능 + 블록 내려가는 시간 초기화
    	playground.enableCoordinateChangable(true);
    	playground.modifyThreadWaitingTime(gameSpeed);

    	// 키보드 리스너 재등록
    	KeySniper();
    	
    	// 게임 재개
    	resumeThread();
    }

    @Override
    public void run() {
    	this.initScreen();
    	this.startGame();
    }
    
    // 화면에 게임 인터페이스를 초기화
    public void initScreen() {
    	this.gameState = true;
    	
		gameScreen.setBorder(new MatteBorder(3, 3, 3, 3, new Color(6, 75, 125)));
		gameScreen.setBackground(new Color(2, 28, 55));
        gameScreen.setBounds(33, 33, 206, 406);
        screen.add(gameScreen);
        gameScreen.setLayout(null);
        
		blockScreen.setBounds(268, 33, 106, 106);
		blockScreen.setBorder(new MatteBorder(3, 3, 3, 3, new Color(6, 75, 125)));
		blockScreen.setBackground(new Color(2, 28, 55));
		screen.add(blockScreen);
		blockScreen.setLayout(null);
		
		infoScreen.setBounds(268, 268, 106, 171);
		infoScreen.setBorder(new MatteBorder(3, 3, 3, 3, new Color(6, 75, 125)));
		infoScreen.setBackground(new Color(2, 28, 55));
		screen.add(infoScreen);
		infoScreen.setLayout(null);
		
		JTextField levelField = new JTextField();
		JTextField scoreField = new JTextField();
		JTextField lineField = new JTextField();
		level = new JTextField();
		score =  new JTextField();
		line =  new JTextField();
		
		levelField.setBorder(BorderFactory.createEmptyBorder());
		levelField.setFont(new Font("Consolas", Font.BOLD, 18));
		levelField.setBounds(3, 3, 100, 30);
		infoScreen.add(levelField);
		levelField.setText("LEVEL");
		levelField.setHorizontalAlignment(SwingConstants.CENTER);
		levelField.setColumns(10);
		levelField.setFocusable(false);
		levelField.setDragEnabled(false);
		levelField.setCursor(Cursor.getDefaultCursor());
		levelField.setBackground(new Color(33, 46, 68));
		levelField.setForeground(new Color(204, 204, 204));
		
		level.setBorder(BorderFactory.createEmptyBorder());
		level.setForeground(Color.WHITE);
		level.setBackground(new Color(2, 28, 55));
		level.setFont(new Font("Impact", Font.PLAIN, 18));
		level.setHorizontalAlignment(SwingConstants.CENTER);
		level.setText("1");
		level.setBounds(3, 33, 100, 25);
		infoScreen.add(level);
		level.setColumns(10);
		level.setFocusable(false);
		
		scoreField.setBorder(BorderFactory.createEmptyBorder());
		scoreField.setFont(new Font("Consolas", Font.BOLD, 18));
		scoreField.setText("SCORE");
		scoreField.setBounds(3, 58, 100, 30);
		infoScreen.add(scoreField);
		scoreField.setColumns(10);
		scoreField.setHorizontalAlignment(JTextField.CENTER);
		scoreField.setFocusable(false);
		scoreField.setDragEnabled(false);
		scoreField.setCursor(Cursor.getDefaultCursor());
		scoreField.setBackground(new Color(33, 46, 68));
		scoreField.setForeground(new Color(204, 204, 204));
		
		score.setBorder(BorderFactory.createEmptyBorder());
		score.setForeground(Color.WHITE);
		score.setBackground(new Color(2, 28, 55));
		score.setText("1634");
		score.setHorizontalAlignment(SwingConstants.CENTER);
		score.setFont(new Font("Impact", Font.PLAIN, 18));
		score.setColumns(10);
		score.setBounds(3, 88, 100, 25);
		infoScreen.add(score);
		score.setFocusable(false);
		
		lineField.setBorder(BorderFactory.createEmptyBorder());
		lineField.setFont(new Font("Consolas", Font.BOLD, 18));
		lineField.setText("LINES");
		lineField.setHorizontalAlignment(SwingConstants.CENTER);
		lineField.setColumns(10);
		lineField.setBounds(3, 113, 100, 30);
		infoScreen.add(lineField);
		lineField.setFocusable(false);
		lineField.setDragEnabled(false);
		lineField.setCursor(Cursor.getDefaultCursor());
		lineField.setBackground(new Color(33, 46, 68));
		lineField.setForeground(new Color(204, 204, 204));
		
		line.setBorder(BorderFactory.createEmptyBorder());
		line.setForeground(Color.WHITE);
		line.setBackground(new Color(2, 28, 55));
		line.setText("53");
		line.setHorizontalAlignment(SwingConstants.CENTER);
		line.setFont(new Font("Impact", Font.PLAIN, 18));
		line.setColumns(10);
		line.setBounds(3, 143, 100, 25);
		infoScreen.add(line);
		line.setFocusable(false);
        
        screen.repaint();
    }
    
    // screen 게임 전체를 표시할 JPanel
    public void startGame() {
        while (playground.isPlayingGame(currentBlock)) {
        	// 레벨 20 클리어 시 게임 종료
        	if (gameLevel > 20) break;
        	
        	level.setText(gameLevel + "");
        	level.setForeground(Color.WHITE);
        	level.setFont(new Font("Impact", Font.PLAIN, 18));
        	level.repaint();
        	
        	score.setText(gameScore + "");
        	score.repaint();
        	
        	line.setText(gameLine + "");
        	line.repaint();

        	// 60초마다 레벨업 혹은 현재 점수가 gameLevel*450 점보다 높을 때 레벨업
        	if (gameScore - (gameLine*2.5) > (double)gameLevel * 450) {
        		gameLevel += 1;
        		// 레벨 10 이하는 레벨업 마다 꾸준히 속도 -10ms 씩 조절
        		if (gameLevel < 10)
        			gameSpeed += -10 - gameLevel*3;
        		else
        			gameSpeed += - gameLevel*3;
        		
        		level.setFont(new Font("Impact", Font.PLAIN, 24));
        		level.setForeground(Color.RED);
        		level.setText(gameLevel + "");
        		level.repaint();

        		// 화면 클리어 할까 말까
        		//playground.levelClear();
        	}
        	
        	renderNextBlock();

	        // 테트리스 창에 블록 추가
	        // 제거한 줄 갯수를 리턴함 (스코어)
        	// ※playground.insertBlock이 리턴 할 때까지 프로그램 멈춤※
            int clearedLine = playground.insertBlock(currentBlock, gameSpeed);
            if (clearedLine > 0)
            	gameScore += (25 * clearedLine) + (10 * gameLevel) + (15 * (initialGameSpeed - gameSpeed)%10);
            gameLine += clearedLine;
            
            // 다음 블록 세팅
            currentBlock = nextBlock;
            nextBlock = getNextBlock();
        }
        this.gameState = false;
    }
    
}
