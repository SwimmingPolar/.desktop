import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

public class Tetris extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Point oldMousePoint;
	private int shadow;
	private int width, height;
	private JPanel mainPanel;
	private JLayeredPane gamePane;
	private JLayeredPane rankingPane;
	private JPanel gameScreen;
	private JPanel rankingScreen;
	private GameHelper player;
	private Timer keepAliveTimer;
	private HashMap<String, Boolean> gameState;
	private HashMap<String, Boolean> pauseState;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tetris frame = new Tetris();
					frame.setTitle("TETRIS");
					frame.setIconImage(new ImageIcon("icon64x64.png").getImage());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Tetris() {
		// 화면 버튼
		JButton toMainFromGame = new JButton("");
		JButton PlayPause = new JButton("");
		JButton Reset = new JButton("");
		// 환경 변수 설정
		// 창 그림자
		this.shadow = 2;
		// 창 크기
		this.width = 850;
		this.height = 600;

		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setSize(this.width + shadow * 2, this.height + shadow * 2);
		// 창 화면 중앙 정렬
		setLocationRelativeTo(null);
		// 기본 툴바 제거
		setUndecorated(true);
		// 창 투명하게
		setBackground(new Color(89, 89, 89, 60));

		JPanel opaquePanel = new JPanel();
		opaquePanel.setLayout(null);
		opaquePanel.setOpaque(false);
		setContentPane(opaquePanel);

		contentPane = new JPanel();
		contentPane.setOpaque(false);
		opaquePanel.add(contentPane);
		contentPane.setBounds(shadow, shadow, width, height);
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(2, 28, 55));

		// 창 드래그 기능
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// 마우스 왼쪽 클릭 시
				if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
					// 클릭 시점 상대 좌표
					oldMousePoint = e.getPoint();
				}
			}
		});
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// 마우스 왼쪽 클릭 시
				if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
					// 마우스 드래그 시점 절대 좌표
					Point currentMousePoint = e.getLocationOnScreen();
					setLocation(currentMousePoint.x - oldMousePoint.x, currentMousePoint.y - oldMousePoint.y);
				}
			}
		});

		// 탑바 구현
		JPanel topbar = new JPanel();
		topbar.setBackground(new Color(13, 16, 30));
		topbar.setBounds(0, 0, 850, 35);
		topbar.setLayout(null);
		contentPane.add(topbar);
		// 타이틀 아이콘
		JLabel titleIcon = new JLabel();
		titleIcon.setIcon(new ImageIcon("icon32x32.png"));
		titleIcon.setBounds(5, 1, 32, 32);
		topbar.add(titleIcon);
		// 타이틀 라벨
		JLabel title = new JLabel("TETRIS");
		title.setForeground(new Color(200, 200, 200));
		title.setFont(new Font("Consolas", Font.PLAIN, 28));
		title.setBounds(45, 0, 100, 40);
		topbar.add(title);
		// 닫기 버튼
		JButton close = new JButton("X");
		close.setFont(new Font("Consolas", Font.BOLD, 23));
		close.setForeground(new Color(149, 149, 149));
		close.setBackground(new Color(13, 16, 30));
		close.setBounds(803, 0, 47, 40);
		close.setBorderPainted(false);
		close.setFocusPainted(false);
		close.setFocusable(false);
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				close.setBackground(new Color(233, 75, 53));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				close.setBackground(new Color(13, 16, 30));
			}
		});
		gameState = new HashMap<String, Boolean>();
		gameState.put("state", false);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		topbar.add(close);
		// 최소화 버튼
		JButton minimize = new JButton("_");
		minimize.setFont(new Font("Consolas", Font.BOLD, 23));
		minimize.setForeground(new Color(149, 149, 149));
		minimize.setBackground(new Color(13, 16, 30));
		minimize.setBounds(756, -5, 47, 40);
		minimize.setBorderPainted(false);
		minimize.setFocusPainted(false);
		minimize.setFocusable(false);
		minimize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				minimize.setBackground(new Color(104, 104, 104));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				minimize.setBackground(new Color(13, 16, 30));
			}
		});
		minimize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState(JFrame.ICONIFIED);
			}
		});
		topbar.add(minimize);

		// 화면 구성 컨테이너 (카드 레이아웃)
		JPanel CardLayoutContainer = new JPanel();
		CardLayoutContainer.setBounds(0, 35, 850, 565);
		contentPane.add(CardLayoutContainer);
		CardLayoutContainer.setLayout(new CardLayout(0, 0));

		/*******************************************************************************/
		/*******************************************************************************/
		/*******************************************************************************/
		// 1. 메인 패인
		mainPanel = new JPanel();
		CardLayoutContainer.add(mainPanel, "mainPanel");
		mainPanel.setLayout(null);
		mainPanel.setBackground(new Color(2, 28, 55));

		JLabel logo = new JLabel();
		logo.setBounds(265, 50, 320, 222);
		logo.setIcon(new ImageIcon("img\\logo.gif"));
		mainPanel.add(logo);

		JLabel menuArrow = new JLabel();
		menuArrow.setSize(100, 100);
		menuArrow.setIcon(new ImageIcon("C:\\Users\\swimmingPolar\\Documents\\Eclipse\\Tetris\\img\\arrow.gif"));
		mainPanel.add(menuArrow);

		JButton start = new JButton("Start");
		start.setFont(new Font("Consolas", Font.PLAIN, 16));
		start.setForeground(Color.WHITE);
		start.setContentAreaFilled(false);
		start.setBounds(325, 330, 200, 45);
		start.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(6, 75, 125)));
		start.setFocusPainted(false);
		start.setFocusable(false);
		start.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				start.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE));
				getMenuArrow(menuArrow, start);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				start.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(6, 75, 125)));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				start.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				start.setContentAreaFilled(false);
			}
		});
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Show Game Pane
				CardLayout layoutCaller = (CardLayout) (CardLayoutContainer.getLayout());
				layoutCaller.show(CardLayoutContainer, "gamePane");

				// 게임 중이 아닐 경우, 새로운 게임
				if (player == null) {
					PlayPause.setIcon(new ImageIcon("img\\pause.png"));
					pauseState.put("state", false);
					gameState.put("state", true);
					if (gameScreen != null)
						gameScreen.removeAll();
					player = new GameHelper(gameScreen);
					player.start();
					keepAliveTimer = new Timer();
					keepAliveTimer.scheduleAtFixedRate(new TimerTask() {
						public void run() {
							if (player != null) {
								gameState.put("state", player.isPlayingGame());
								if (gameState.get("state") == false) {
									keepAliveTimer.cancel();
									keepAliveTimer = null;
									int score = player.getScore();
									pauseState.put("state", true);
									PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
									PlayPause.setEnabled(false);
									PlayPause.repaint();
									Reset.setEnabled(false);
									Reset.repaint();
									Utility.RankWrite(gameScreen, score);
									player = null;
									PlayPause.setEnabled(true);
									PlayPause.repaint();
								}
							} else {
								keepAliveTimer.cancel();
								keepAliveTimer = null;
							}
						}
					}, 500, 40);
				}

				// 게임 중일 경우, 게임 재개
				else {
					PlayPause.setIcon(new ImageIcon("img\\pause.png"));
					pauseState.put("state", false);
					player.gameResume();
				}
			}
		});
		mainPanel.add(start);
		getMenuArrow(menuArrow, start);

		JButton ranking = new JButton("Ranking");
		ranking.setFont(new Font("Consolas", Font.PLAIN, 16));
		ranking.setForeground(Color.WHITE);
		ranking.setContentAreaFilled(false);
		ranking.setBounds(325, 398, 200, 45);
		ranking.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(6, 75, 125)));
		ranking.setFocusPainted(false);
		ranking.setFocusable(false);
		ranking.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				ranking.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE));
				getMenuArrow(menuArrow, ranking);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				ranking.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(6, 75, 125)));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				ranking.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				ranking.setContentAreaFilled(false);
			}
		});
		ranking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Show Ranking Pane
				CardLayout layoutCaller = (CardLayout) (CardLayoutContainer.getLayout());
				layoutCaller.show(CardLayoutContainer, "rankingPane");
				Utility.RankList(rankingScreen);
			}
		});
		mainPanel.add(ranking);

		JButton exit = new JButton("Exit");
		exit.setFont(new Font("Consolas", Font.PLAIN, 16));
		exit.setForeground(Color.WHITE);
		exit.setContentAreaFilled(false);
		exit.setBounds(325, 467, 200, 45);
		exit.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(6, 75, 125)));
		exit.setFocusPainted(false);
		exit.setFocusable(false);
		exit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				exit.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE));
				getMenuArrow(menuArrow, exit);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exit.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(6, 75, 125)));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				exit.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				exit.setContentAreaFilled(false);
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		mainPanel.add(exit);

		/*******************************************************************************/
		/*******************************************************************************/
		/*******************************************************************************/
		// 2. 게임 패인
		gamePane = new JLayeredPane();
		gamePane.setOpaque(true);
		gamePane.setBackground(new Color(2, 28, 55));
		CardLayoutContainer.add(gamePane, "gamePane");
		gamePane.setLayout(null);

		// 게임 스크린 생성
		gameScreen = new JPanel();
		gameScreen.setBorder(new MatteBorder(3, 3, 3, 3, new Color(6, 75, 125)));
		gameScreen.setBackground(new Color(2, 28, 55));
		gameScreen.setLayout(null);
		gameScreen.setBounds(217, 50, 395, 465);
		gamePane.add(gameScreen, new Integer(0));

		// 리셋 화면 오버레이
		JPanel resetOverlay = new JPanel();
		resetOverlay.setVisible(false);
		resetOverlay.setBounds(150, 165, 550, 235);
		resetOverlay.setBackground(new Color(58, 58, 74, 200));
		resetOverlay.setLayout(null);
		gamePane.add(resetOverlay, new Integer(1));

		// 홈버튼
		toMainFromGame.setIcon(new ImageIcon("img\\home.png"));
		toMainFromGame.setContentAreaFilled(false);
		toMainFromGame.setFocusPainted(false);
		toMainFromGame.setBorderPainted(false);
		toMainFromGame.setFocusable(false);
		toMainFromGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout layoutCaller = (CardLayout) (CardLayoutContainer.getLayout());
				layoutCaller.show(CardLayoutContainer, "mainPanel");
				if (player != null) {
					player.gamePause();
				} else {
					PlayPause.setEnabled(true);
					Reset.setEnabled(true);
				}
			}
		});
		toMainFromGame.setBounds(30, 30, 74, 74);
		toMainFromGame.addMouseListener(new MouseAdapter() {
			private boolean mouseOverState = false;
			private boolean mousePressState = false;

			@Override
			public void mousePressed(MouseEvent arg0) {
				mousePressState = true;
				toMainFromGame.setIcon(new ImageIcon("img\\homeMouseOverClicked.png"));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressState = false;
				if (mouseOverState == true)
					toMainFromGame.setIcon(new ImageIcon("img\\homeMouseOver.png"));
				else
					toMainFromGame.setIcon(new ImageIcon("img\\home.png"));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOverState = true;
				toMainFromGame.setIcon(new ImageIcon("img\\homeMouseOver.png"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseOverState = false;
				if (mousePressState == true)
					toMainFromGame.setIcon(new ImageIcon("img\\homeMouseOverClicked.png"));
				else
					toMainFromGame.setIcon(new ImageIcon("img\\home.png"));
			}
		});
		gamePane.add(toMainFromGame);

		// 플레이/퍼즈 버튼
		PlayPause.setIcon(new ImageIcon("img\\pause.png"));
		PlayPause.setContentAreaFilled(false);
		PlayPause.setFocusPainted(false);
		PlayPause.setBorderPainted(false);
		PlayPause.setFocusable(false);
		// 게임 상태 감시
		pauseState = new HashMap<String, Boolean>();
		pauseState.put("state", false);
		PlayPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (player != null) {
					if (pauseState.get("state") == false) {
						pauseState.put("state", true);
						player.gamePause();
					} else {
						pauseState.put("state", false);
						player.gameResume();
					}
				} else {
					Reset.setEnabled(true);
					Reset.repaint();
					pauseState.put("state", false);
					gameState.put("state", true);
					PlayPause.setIcon(new ImageIcon("img\\pauseMouseOver.png"));
					PlayPause.repaint();
					if (gameScreen != null)
						gameScreen.removeAll();
					player = new GameHelper(gameScreen);
					player.start();
					
					if (keepAliveTimer != null) {
						keepAliveTimer.cancel();
						keepAliveTimer = null;
					}
					keepAliveTimer = new Timer();
					keepAliveTimer.scheduleAtFixedRate(new TimerTask() {
						public void run() {
							if (player != null) {
								gameState.put("state", player.isPlayingGame());
								if (gameState.get("state") == false) {
									keepAliveTimer.cancel();
									keepAliveTimer = null;
									int score = player.getScore();
									pauseState.put("state", true);
									PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
									PlayPause.setEnabled(false);
									PlayPause.repaint();
									Reset.setEnabled(false);
									Reset.repaint();
									Utility.RankWrite(gameScreen, score);
									player = null;
									PlayPause.setEnabled(true);
									PlayPause.repaint();
								}
							} else {
								keepAliveTimer.cancel();
								keepAliveTimer = null;
							}
						}
					}, 500, 40);
				}
			}
		});
		PlayPause.setBounds(30, 104, 74, 74);
		PlayPause.addMouseListener(new MouseAdapter() {
			private boolean mouseOverState = false;
			private boolean mousePressState = false;

			@Override
			public void mousePressed(MouseEvent arg0) {
				mousePressState = true;
				if (pauseState.get("state") == false)
					PlayPause.setIcon(new ImageIcon("img\\pauseMouseOverClicked.png"));
				else
					PlayPause.setIcon(new ImageIcon("img\\playMouseOverClicked.png"));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressState = false;
				if (pauseState.get("state") == false)
					if (mouseOverState == true)
						PlayPause.setIcon(new ImageIcon("img\\pauseMouseOver.png"));
					else
						PlayPause.setIcon(new ImageIcon("img\\pause.png"));
				else if (mouseOverState == true)
					PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
				else
					PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOverState = true;
				if (pauseState.get("state") == false)
					PlayPause.setIcon(new ImageIcon("img\\pauseMouseOver.png"));
				else
					PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseOverState = false;
				if (pauseState.get("state") == false)
					if (mousePressState == true)
						PlayPause.setIcon(new ImageIcon("img\\pauseMouseOverClicked.png"));
					else
						PlayPause.setIcon(new ImageIcon("img\\pause.png"));
				else if (mousePressState == true)
					PlayPause.setIcon(new ImageIcon("img\\playMouseOverClicked.png"));
				else
					PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
			}
		});
		gamePane.add(PlayPause);

		// 리셋 버튼
		Reset.setIcon(new ImageIcon("img\\reset.png"));
		Reset.setContentAreaFilled(false);
		Reset.setFocusPainted(false);
		Reset.setBorderPainted(false);
		Reset.setFocusable(false);
		Reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (player != null) {
					player.gamePause();

					toMainFromGame.setEnabled(false);
					PlayPause.setEnabled(false);
					Reset.setEnabled(false);

					JLabel resetMsg = new JLabel("Do you want to reset the game?");
					resetMsg.setForeground(new Color(204, 204, 204));
					resetMsg.setBounds(83, -25, 390, 184);
					resetMsg.setFont(new Font("Consolas", Font.PLAIN, 24));
					resetOverlay.add(resetMsg);

					JLabel resetYes = new JLabel("Yes");
					resetYes.setHorizontalAlignment(SwingConstants.CENTER);
					resetYes.setFont(new Font("Consolas", Font.PLAIN, 17));
					resetYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
					resetYes.setOpaque(true);
					resetYes.setBackground(new Color(58, 58, 74));
					resetYes.setForeground(new Color(204, 204, 204));
					resetYes.setBounds(125, 135, 125, 50);
					resetYes.setFocusable(false);
					resetOverlay.add(resetYes);

					resetYes.addMouseListener(new MouseAdapter() {
						private boolean mousePressState = false;

						// 리셋 동의 할 때
						@Override
						public void mouseClicked(MouseEvent e) {
							// player 쓰레드 멈추고 제거
							player.gamePause();
							player = null;

							// gameScreen 리셋
							gameScreen.removeAll();
							gameScreen.repaint();

							// 리셋 화면을 투명하게 한 뒤 제거
							resetOverlay.setVisible(false);
							// setVisible 시 없어지는 리스너 재등록을 위해
							// 오버레이 위에 컴포넌트 삭제
							resetOverlay.removeAll();

							toMainFromGame.setEnabled(true);
							toMainFromGame.repaint();

							PlayPause.setEnabled(true);
							PlayPause.setIcon(new ImageIcon("img\\playMouseOver.png"));
							PlayPause.repaint();
							pauseState.put("state", true);
						}

						@Override
						public void mousePressed(MouseEvent e) {
							mousePressState = true;
							resetYes.setBackground(new Color(33, 46, 68).brighter());
						}

						@Override
						public void mouseReleased(MouseEvent e) {
							mousePressState = false;
							resetYes.setBackground(new Color(58, 58, 74));
							resetYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
							resetYes.setForeground(new Color(204, 204, 204));
						}

						public void mouseEntered(MouseEvent e) {
							resetYes.setForeground(new Color(233, 33, 40));
							resetYes.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
						}

						public void mouseExited(MouseEvent e) {
							if (mousePressState == false) {
								resetYes.setBorder(
										BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
								resetYes.setForeground(new Color(204, 204, 204));
							}
						}
					});

					JLabel resetCancel = new JLabel("Cancel");
					resetCancel.setFont(new Font("Consolas", Font.PLAIN, 17));
					resetCancel.setBounds(306, 135, 125, 50);
					resetCancel.setHorizontalAlignment(SwingConstants.CENTER);
					resetCancel.setFont(new Font("Consolas", Font.PLAIN, 17));
					resetCancel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
					resetCancel.setOpaque(true);
					resetCancel.setBackground(new Color(58, 58, 74));
					resetCancel.setForeground(new Color(204, 204, 204));
					resetCancel.setFocusable(false);
					resetOverlay.add(resetCancel);

					resetCancel.addMouseListener(new MouseAdapter() {
						private boolean mousePressState = false;

						@Override
						public void mouseClicked(MouseEvent e) {
							PlayPause.setIcon(new ImageIcon("img\\pause.png"));
							pauseState.put("state", false);

							resetOverlay.setVisible(false);
							resetOverlay.removeAll();

							toMainFromGame.setEnabled(true);
							PlayPause.setEnabled(true);
							Reset.setEnabled(true);
							toMainFromGame.repaint();
							PlayPause.repaint();
							Reset.repaint();

							player.gameResume();
						}

						@Override
						public void mousePressed(MouseEvent e) {
							mousePressState = true;
							resetCancel.setBackground(new Color(33, 46, 68).brighter());
						}

						@Override
						public void mouseReleased(MouseEvent e) {
							mousePressState = false;
							resetCancel.setBackground(new Color(58, 58, 74));
							resetCancel
									.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
							resetCancel.setForeground(new Color(204, 204, 204));
						}

						public void mouseEntered(MouseEvent e) {
							resetCancel.setForeground(new Color(233, 33, 40));
							resetCancel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(233, 33, 40)));
						}

						public void mouseExited(MouseEvent e) {
							if (mousePressState == false) {
								resetCancel.setBorder(
										BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(204, 204, 204)));
								resetCancel.setForeground(new Color(204, 204, 204));
							}
						}
					});

					resetOverlay.setVisible(true);
					resetOverlay.repaint();
					toMainFromGame.repaint();
					PlayPause.repaint();
					Reset.repaint();
				}
			}
		});
		Reset.setBounds(30, 178, 74, 74);
		Reset.addMouseListener(new MouseAdapter() {
			private boolean mouseOverState = false;
			private boolean mousePressState = false;

			@Override
			public void mousePressed(MouseEvent arg0) {
				mousePressState = true;
				Reset.setIcon(new ImageIcon("img\\resetMouseOverClicked.png"));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressState = false;
				if (mouseOverState == true)
					Reset.setIcon(new ImageIcon("img\\resetMouseOver.png"));
				else
					Reset.setIcon(new ImageIcon("img\\reset.png"));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOverState = true;
				Reset.setIcon(new ImageIcon("img\\resetMouseOver.png"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseOverState = false;
				if (mousePressState == true)
					Reset.setIcon(new ImageIcon("img\\resetMouseOverClicked.png"));
				else
					Reset.setIcon(new ImageIcon("img\\reset.png"));
			}
		});
		gamePane.add(Reset);

		/*******************************************************************************/
		/*******************************************************************************/
		/*******************************************************************************/
		// 3. 랭킹 패인
		rankingPane = new JLayeredPane();
		rankingPane.setOpaque(true);
		rankingPane.setBackground(new Color(2, 28, 55));
		CardLayoutContainer.add(rankingPane, "rankingPane");
		rankingPane.setLayout(null);

		JButton toMainFromRanking = new JButton("");
		toMainFromRanking.setIcon(new ImageIcon("img\\home.png"));
		toMainFromRanking.setContentAreaFilled(false);
		toMainFromRanking.setFocusPainted(false);
		toMainFromRanking.setBorderPainted(false);
		toMainFromRanking.setFocusable(false);
		toMainFromRanking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout layoutCaller = (CardLayout) (CardLayoutContainer.getLayout());
				layoutCaller.show(CardLayoutContainer, "mainPanel");
			}
		});
		toMainFromRanking.setBounds(30, 30, 74, 74);
		toMainFromRanking.addMouseListener(new MouseAdapter() {
			private boolean mouseOverState = false;
			private boolean mousePressState = false;

			@Override
			public void mousePressed(MouseEvent arg0) {
				mousePressState = true;
				toMainFromRanking.setIcon(new ImageIcon("img\\homeMouseOverClicked.png"));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressState = false;
				if (mouseOverState == true)
					toMainFromRanking.setIcon(new ImageIcon("img\\homeMouseOver.png"));
				else
					toMainFromRanking.setIcon(new ImageIcon("img\\home.png"));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOverState = true;
				toMainFromRanking.setIcon(new ImageIcon("img\\homeMouseOver.png"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseOverState = false;
				if (mousePressState == true)
					toMainFromRanking.setIcon(new ImageIcon("img\\homeMouseOverClicked.png"));
				else
					toMainFromRanking.setIcon(new ImageIcon("img\\home.png"));
			}
		});
		rankingPane.add(toMainFromRanking);

		rankingScreen = new JPanel();
		rankingScreen.setSize(850, 565);
		rankingScreen.setLocation(0, 35);
		rankingPane.add(rankingScreen);
		rankingScreen.setLayout(null);
	}

	// 메뉴 화살표를 옮기는 함수
	public void getMenuArrow(JLabel arrow, JButton menu) {
		int menuAbsX = menu.getLocation().x;
		int menuAbsY = menu.getLocation().y;
		int menuHeight = menu.getSize().height;
		int arrowWidth = arrow.getSize().width;
		int arrowHeight = arrow.getSize().height;

		int arrowAbsX = menuAbsX - arrowWidth;
		int arrowAbsY = menuAbsY + (int) (menuHeight / 2) - (int) (arrowHeight / 2);
		arrow.setLocation(arrowAbsX, arrowAbsY);
		arrow.repaint();
	}
}
