import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class Playground {
    // 게임이 이루어질 창
    private JPanel panel;

    // 현재 블록
    private Block currentBlock;
    // 현재 게임 속도
    private int gameSpeed;

    // 블록이 매핑 될 2차원 데이터 배열
    private boolean[][] dataGround;

    // 블록이 매핑 될 2차원 그래픽 배열
    private CustomButton[][] graphicGround;
    
    // 미니블록이 저장 될 배열
    private CustomButton[] graphicBlock;
    private CustomButton[] shadowBlock;

    // 테트리스 창 폭과 높이
    private int maxWidth, maxHeight;

    // 블록이 위치할 기준 좌표 (n초 마다 y가 증가할 예정)
    private int currentX, currentY;
    private boolean coordinateChangable;
    
    private int threadWaitingTime;
    private boolean threadWaiting;
    private boolean threadWaitingTimeLock;

    public Playground() {
        System.out.println("Playground는 인자가 필요합니다.");
        System.exit(-1);
    }

    public Playground(JPanel panel, int maxWidth, int maxHeight) {
        this.panel = panel;

        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        dataGround = new boolean[maxHeight][maxWidth];
        graphicGround = new CustomButton[maxHeight][maxWidth];
        graphicBlock = new CustomButton[4];
        shadowBlock = new CustomButton[4];

        coordinateChangable = false;
		threadWaitingTime = 0;
		threadWaiting = false;
		threadWaitingTimeLock = false;
    }

    // 게임에 블록이 추가되고 블록 사이클 시작
    public int insertBlock(Block currentBlock, int gameSpeed) {
        // 현재 지정 받은 블록 저장
        this.currentBlock = currentBlock;
        this.gameSpeed = gameSpeed;

        // 블록 모양을 나타낼 JButton 배열 (블록 하나는 4개의 버튼으로 이루어짐)
        for (int i=0; i < 4; i++) {
        	Color miniBlockColor = currentBlock.getColor();
            graphicBlock[i] = new CustomButton(miniBlockColor);
            panel.add(graphicBlock[i]);
            shadowBlock[i] = new CustomButton(miniBlockColor);
            panel.add(shadowBlock[i]);
        }
        
        // 블록 초기 위치
        currentX = 3; currentY = 0;
        // 좌표 수정 잠금을 해체 (좌표 수정 가능)
        coordinateChangable = true;
        while (isMovable(currentX, currentY, currentBlock.getCurrentShape())) {
            renderBlock();
            
            threadWaiting = true;
            threadWaitingTime = gameSpeed;
            // thread sleep interval 1ms ~ 128ms 중 최적화 인터벌: 83ms -> 57ms
            int interval = 83;
            boolean timeLock = true;
            while (timeLock) {
            	if (threadWaitingTime-interval < 0) {
            		if (threadWaiting == false) break;
            		interval = threadWaitingTime;
            		timeLock = false;
            	}
	            try {
	                Thread.sleep(interval);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            threadWaitingTimeLock = true;
	            threadWaitingTime -= interval;
	            threadWaitingTimeLock = false;
            }
            currentY += 1;
        }
        // 좌표 수정 잠금 (좌표 수정 불가능)
        coordinateChangable = false;
        
        currentY += -1;

        // 1 블록 완성 후 상태 저장
        saveBooleanData();
        saveGraphicData();

        // 완성된 줄 삭제
        int clearedLine = clearLine();

        // 삭제 된 라인이 있을 경우 전체 렌더링
        if (clearedLine > 0)
            renderAll();
        
        // 삭제 된 줄 갯수 리턴
        return clearedLine;
    }
    
    // 게임 진행 여부 확인
    public boolean isPlayingGame(Block currentBlock) {
    	return isMovable(3, 0, currentBlock.getCurrentShape());
    }
    
	public boolean isMovable(int X, int Y, int block) {
		int bX = X+1;
		int bY = Y;
		boolean movable = true;

		//맵 생성 boundary
		HashMap<Integer, int[]> boundaryMap = new HashMap<Integer, int[]>();
		for (int y=0; y < dataGround.length; y++) {
			int[] yLine = new int[12];
			yLine[0] = 1;
			for (int x=0; x < dataGround[y].length; x++) {
				if (dataGround[y][x] == true)
					yLine[x+1] = 1;
				else
					yLine[x+1] = 0;
			}
			yLine[11] = 1;
			boundaryMap.put(y, yLine);	
		}
		boundaryMap.put(20, new int[] {1,1,1,1,1,1,1,1,1,1,1,1});	
		
		//block 16진수 -> 2진수 쪼갬
		ArrayList<Integer> blockToBit = new ArrayList<Integer>();
		for(int bit=0x8000;bit>0;bit=bit>>1)
			if( (bit & block) == bit)
				blockToBit.add(1);
			else
				blockToBit.add(0);
		
		//블록 비교
		for(int dY=0;dY<4;dY++)
			for(int dX=0;dX<4;dX++)
				if(blockToBit.get(dY*4 + dX) == 1)
					if(blockToBit.get(dY*4+dX) == 1)
						if( (bX+dX < 0) || (bX+dX >= 12) || (bY+dY < 0) || (bY+dY >= 21) )
							movable = false;
						else
							if( (bX+dX >= 0) && (bX+dX < 13) && (bY+dY >= 0) && (bY+dY < 22) )
								if( (boundaryMap.get(bY+dY)[bX+dX] & blockToBit.get(dY*4+dX)) == 1)
									movable = false;
		return movable;
	}

    // 기준 좌표 currentX, currentY 조작 (좌, 우, 하 움직임)
    public void move(String command) {
        if (coordinateChangable) {
            if (command.equals("LEFT")) {
                if (isMovable(currentX-1, currentY, currentBlock.getCurrentShape()))
                    currentX += -1;
            }
            else if (command.equals("RIGHT")) {
                if (isMovable(currentX+1, currentY, currentBlock.getCurrentShape()))
                    currentX += 1;
            }
            else if (command.equals("DOWN")) {
                if (isMovable(currentX, currentY+1, currentBlock.getCurrentShape()))
                    currentY += 1;
            }
            else if (command.equals("PULLDOWN")) {
        		coordinateChangable = false;
            	while (isMovable(currentX, currentY+1, currentBlock.getCurrentShape())) {
            		currentY += 1;
            	}
            	modifyThreadWaitingTime(0);
            }
            renderBlock();
        }
    }
    
    public void Kick() {
    	boolean rotateFlag = false;
		// floor kick 위로 1칸
		if( (isMovable(currentX, currentY-1,currentBlock.getNextShape())) ) {
			currentY = currentY - 1;
			rotateFlag = true;
		}
		// floor kick 위로 2칸
		else if ( (isMovable(currentX, currentY-2,currentBlock.getNextShape())) ) {
				currentY = currentY - 2;
				rotateFlag = true;
		}
		// wall kick
		if ( !(isMovable(currentX, currentY, currentBlock.getNextShape()))) {
			// 좌로 1칸
			if((isMovable(currentX-1, currentY-1,currentBlock.getNextShape()))) {
				//좌표 왼쪽을 옮기고 break;
				currentX = currentX - 1;
				currentY = currentY - 1;
				rotateFlag = true;
			}
			// 좌로 2칸
			else if((isMovable(currentX-2, currentY-1,currentBlock.getNextShape()))) {
					currentX = currentX - 2;
					currentY = currentY - 1;
					rotateFlag = true;
			}
			// 우로 1칸
			else if((isMovable(currentX+1, currentY-1,currentBlock.getNextShape()))) {
				//좌표 오른쪽으로 옮기고 break;
				currentX = currentX + 1;
				currentY = currentY - 1;
				rotateFlag = true;
			}
			// 우로 2칸
			else if((isMovable(currentX+2, currentY-1,currentBlock.getNextShape()))) {
				currentX = currentX + 2;
				currentY = currentY - 1;
				rotateFlag = true;
			}
		}
    	if (rotateFlag == true) {
    		currentBlock.rotate();
    		modifyThreadWaitingTime(gameSpeed);
    	}
    }

    public void rotate() {
    	if (coordinateChangable) {
	        if (isMovable(currentX, currentY, currentBlock.getNextShape()))
	            currentBlock.rotate();
	        if (!(isMovable(currentX, currentY, currentBlock.getNextShape())))
	        	Kick();
	        renderBlock();
    	}
    }

    // 데이터 배열과 그래픽 배열에서 삭제 가능한 줄 삭제
    public int clearLine() {
		//tempGround를 넣을 큐
		Queue<boolean[]> dataQueue = new LinkedList<boolean[]>();
        Queue<CustomButton[]> graphicQueue = new LinkedList<CustomButton[]>();
		
        // 모든 Y축을 아래에서부터 각자의 Queue로 offer
		for(int y=dataGround.length-1; y >= 0; y--) {
            dataQueue.offer(dataGround[y]);
            graphicQueue.offer(graphicGround[y]);
        }

        // dataGround의 모든 값이 true이면 해당 라인을 버리고 false가 하나라도 있으면 다시 Queue로 offer
        int clearedLine = 0;
        int fixedYsize = dataQueue.size();
		for (int y=0; y < fixedYsize; y++) {
		    boolean trueFlag = true;
		    boolean[] dataCurrentYLine = dataQueue.poll();
		    CustomButton[] graphicCurrentYLine = graphicQueue.poll();
			for(int x=0; x < dataCurrentYLine.length; x++) {
				if (dataCurrentYLine[x] == false) {
					trueFlag = false;
					break;
				}
			}
			if (trueFlag == false) {
                dataQueue.offer(dataCurrentYLine);
                graphicQueue.offer(graphicCurrentYLine);
            }
			else {
                clearedLine++;
            }
		}

		if (clearedLine > 0) {
            for (int y = dataGround.length - 1; y >= 0; y--) {
                boolean[] dataCurrentYLine = new boolean[10];
                CustomButton[] graphicCurrentYLine = new CustomButton[10];
                if (!dataQueue.isEmpty()) {
                    dataCurrentYLine = dataQueue.poll();
                    graphicCurrentYLine = graphicQueue.poll();
                }
                for (int x = 0; x < dataGround[y].length; x++) {
                    dataGround[y][x] = dataCurrentYLine[x];
                    graphicGround[y][x] = graphicCurrentYLine[x];
                }
            }
        }
		return clearedLine;
	}

    // 데이터 배열 수정
    private void saveBooleanData() {
        int deltaX = 0, deltaY = 0;
        for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
            if ((bitmask & currentBlock.getCurrentShape()) > 0) {
                int absX = currentX + deltaX;
                int absY = currentY + deltaY;
                dataGround[absY][absX] = true;
            }
            if (++deltaX == 4) {
                deltaX = 0;
                deltaY += 1;
            }
        }
    }

    // 그래픽 배열 수정
    private void saveGraphicData() {
        int deltaX = 0, deltaY = 0;
        // 1개 블록에 4개의 작은 블럭이 있음
        int miniBlockCounter = 0;
        for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
            if ((bitmask & currentBlock.getCurrentShape()) > 0) {
                int absX = currentX + deltaX;
                int absY = currentY + deltaY;
                graphicGround[absY][absX] = graphicBlock[miniBlockCounter++];
            }
            if (++deltaX == 4) {
                deltaX = 0;
                deltaY += 1;
            }
        }
    }

    // 블록만 렌더링
    private void renderBlock() {
        int blockWidth = graphicBlock[0].getSize().width;
        int blockHeight = graphicBlock[0].getSize().height;
        
        int deltaX = 0, deltaY = 0;
        int miniBlockCounter = 0;
        for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
            if ((bitmask & currentBlock.getCurrentShape()) > 0) {
                int absX = currentX + deltaX;
                int absY = currentY + deltaY;
                if ((0 <= absX && absX < maxWidth) && (0 <= absY && absY < maxHeight)) {
                	graphicBlock[miniBlockCounter++].setLocation(absX * blockWidth, absY * blockHeight);
                }
        	}
            if (++deltaX == 4) {
            	deltaX = 0;
            	deltaY += 1;
            }
        }

    	int color = currentBlock.getColor().getRGB();
    	int[] rgb = Utility.extractRGB(color);
    	int r = rgb[0], g = rgb[1], b = rgb[2];
        
        deltaX = 0; deltaY = 0;
        miniBlockCounter = 0;
        int tempCurrentY = currentY;
        while (isMovable(currentX, tempCurrentY+1, currentBlock.getCurrentShape()))
        		tempCurrentY += 1;
        
        for (int bitmask=0x8000; bitmask > 0; bitmask >>= 1) {
            if ((bitmask & currentBlock.getCurrentShape()) > 0) {
                int absX = currentX + deltaX;
                int absY = tempCurrentY + deltaY;
                if ((0 <= absX && absX < maxWidth) && (0 <= absY && absY < maxHeight)) {
                	shadowBlock[miniBlockCounter].setBackground(new Color(r, g, b, 60));
                	shadowBlock[miniBlockCounter++].setLocation(absX * blockWidth, absY * blockHeight);
                }
        	}
            if (++deltaX == 4) {
            	deltaX = 0;
            	deltaY += 1;
            }
        }
        
    }

    // 그래픽 렌더링
    private void renderAll() {
    	panel.removeAll();
    	
        for (int y=0; y < graphicGround.length; y++) {
            for (int x=0; x < graphicGround[y].length; x++) {
            	// block cell
                if (graphicGround[y][x] != null) {
                    CustomButton miniBlock = graphicGround[y][x];
                    panel.add(miniBlock);
                    int miniBlockWidth = miniBlock.getSize().width;
                    int miniBlockHeight = miniBlock.getSize().height;
                    miniBlock.setLocation(x*miniBlockWidth, y*miniBlockHeight);
                }
            }
        }
        
    	panel.repaint();
    }
    
    // Y 좌표 수정을 가능하게 해줌
    public void enableCoordinateChangable(boolean flag) {
    	if (flag) {
    		coordinateChangable = true;
    	} else {
    		coordinateChangable = false;
    	}
    }
    
    // threadWaitingTime의 값을 간접적으로 바꿔줌
    public void modifyThreadWaitingTime(int time) {
    	if (time == 0) threadWaiting = false;
    	new Timer().schedule(new TimerTask() {
    		public void run() {
    			if (threadWaitingTimeLock == false)
    				threadWaitingTime = time;
    			else
	    			while (threadWaitingTimeLock == true)
	    				threadWaitingTime = time;
    		}
    	}, 0);
    }
    
    public void levelClear() {
    	dataGround = null;
    	dataGround = new boolean[20][10];
    	graphicGround = null;
    	graphicGround = new CustomButton[20][10];
    	renderAll();
    }
}
