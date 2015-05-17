import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 * 対戦時の画面を管理するクラス
 *
 * MEMO:
 * 設置されているコンポーネントを取得する際には上から順に連番で取得している。よって
 * 既に設置されているコンポーネントの上にadd文を加えるとエラーがでるので注意
 */

class GamePanel extends JLayeredPane implements MouseListener, ActionListener
{
	static PrintWriter out; // 出力用のライター
	static JLayeredPane mainPanel; 
	static JLayeredPane piecePanel; // 駒をおくためのペイン
	static JButton piecePanelButton[][];
	static JButton goalPanelButton[][];
	static JLabel  pieceCountLabel[][];
	static String gamePre = "ON";
	static JTextArea messageArea; //

	static String name; // ユーザ名
	static String _name; // 対戦相手の名前 
	static int myNum = 0;
	static String turn = "NO"; // コマを置けるかどうか

	// 通知を扱うクラスのインスタンスを生成
	static Information information = new Information();	
	
	GamePanel(JLayeredPane _mainPanel, String _name, String ip)
	{
		mainPanel = _mainPanel; // JLayerdPaneの引き継ぎ
		name = _name;           // ユーザ名の引き継ぎ
		String _ip = ip;
		

		/*---------------
		  サーバーに接続する
		---------------*/
		Socket socket = null;
		try {
			// とりあえずlocalhostでソケット作成
			socket = new Socket(_ip, 10000);
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		}

		// 受信用のスレッドを作成する
		MesgRecvThread mrt = new MesgRecvThread(socket);
		mrt.start();
	}


	// メッセージ受信の為のスレッド
	public class MesgRecvThread extends Thread
	{
		Socket socket;

		public MesgRecvThread(Socket s)
		{
			socket = s;
			//myName = n;
		}

		// 通信状況を監視し、受信データによって動作する
		public void run()
		{
			try {
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(name);

				// ソケットへの入力を監視する
				while (true) {
					String str = br.readLine();
					if (str != null) {
						// 受け取った文字列を"_"で区切る
						String messageToken[] = str.split("_");
						//System.out.println(messageToken[0]);
						/*-----------------------
						 * Socketへの入力の条件分岐
						-----------------------*/
						// ユーザには見えない内部システムに対する入力
						if (messageToken[0].equals("info")) {
							// ゲーム待機命令
							if (messageToken[1].equals("gameStop")) {
								System.out.println("GameStop!");
								myNum = 1;
								ImageIcon imageIcon = new ImageIcon("img/dialogSearchEnemy.png");
								MainPanel.dialogLabel.setIcon(imageIcon);

								// 先に待機しているユーザが先行
								turn = "YES";

							// ゲーム開始命令
							} else if (messageToken[1].equals("gameStart")) {
								System.out.println("GameStart!");
								createGameTop();

							// 駒数の更新情報
							} else if (messageToken[1].equals("countChange")) {
								if (!canSetPiece()) {
									int i = Integer.parseInt(messageToken[2]);
									int j = Integer.parseInt(messageToken[3]);
									int count = Integer.parseInt(messageToken[4]);
									Piece.changePieceCount(i, j, count);
									
									//information.showDialog("img/dialogGetBlueGhost.png", "test", "test", 1);

								}
							} else if (messageToken[1].equals("win")) {
								if (!messageToken[2].equals(myNum)) {
									information.showDialog("img/dialogResultWin.png", "test", "test", 2);	
								}	
							} else if (messageToken[1].equals("lose")) {
								if (!messageToken[2].equals(myNum)) {
									information.showDialog("img/dialogResultLose.png", "test", "test", 2);	
								}
							} else if (messageToken[1].equals("name")) {
								if (!messageToken[2].equals(name)) {
									System.out.println(name);
									System.out.println(messageToken[2]);
									_name = messageToken[2];
								}
							} else if (messageToken[1].equals("retry")) {
								// 再戦処理
								GamePanel.information.removeDialog();
								Piece.resetPiece();

								// 初期配置の変更を促すダイアログを表示
								information.showDialog("img/dialogGamePre.png", "test", "test", 1);								

								// ゲーム状態を準備中に変更
								gamePre = "ON";

								// 駒の所持数を初期化
								for (int i = 0; i < 2; i++) {
									for (int j = 0; j < 2; j++) {
										pieceCountLabel[i][j].setText("4");
									}	
								}

								// チャットを初期化
								Component labelArray[] = mainPanel.getComponentsInLayer((Integer)360);
								JTextArea getTextArea = (JTextArea)labelArray[0];
								getTextArea.setText("");

								// メッセージエリアの初期化
								messageArea.setText("コマの初期配置を変更しよう！！");


							} else if(messageToken[1].equals("end")) {
								System.exit(0);
							}
						// チャットメッセージの入力
						} else if(messageToken[0].equals("chat")) {
							// 360番に配置されているコンポーネントを配列に格納
							Component labelArray[] = mainPanel.getComponentsInLayer((Integer)360);
							// テクストエリアのコンポーネントを取得
							JTextArea getTextArea = (JTextArea)labelArray[0];
							// 文字が入力されていればテクストエリアに文字列を貼付ける
							getTextArea.append(messageToken[1] + "\n");

						// コマの移動メッセージ
						} else if (messageToken[0].equals("piece")) {
							// 相手のターンの結果を自分の盤面に反映させる
							if (!canSetPiece()) {

								int x1 = Integer.parseInt(messageToken[1]);
								int x2 = Integer.parseInt(messageToken[4]);

								int y1 = Integer.parseInt(messageToken[2]);
								int y2 = Integer.parseInt(messageToken[5]);

								String status1 = messageToken[3];
								String status2 = messageToken[6];

								Piece.reflectEnemyMove(x1, y1, status1, x2, y2, status2);
							}

							// ターンの変更
							changeSetPiece();

							// ターン変更のしらせ
							information.informTurn(turn);
						} 

					}
				}
			} catch (IOException e) {

			}
		}
	}
	

	/*------------------------ module ------------------------------*/
	public void createGameTop()
	{
		
		/*----------------------
		  盤面レイアウト配置
		----------------------*/
		// フレームの作成
		JLabel topFrame   = MainPanel.createImageLabel("img/gameTopFrame.png", "topFrame_JLabel", 0, 0, 1440, 900);
		mainPanel.add(topFrame, (Integer)300);

		/*---------------------
		  パネルの生成と設置
		---------------------*/
		piecePanelButton = new JButton[6][6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				piecePanelButton[i][j] = MainPanel.createImageButton("img/gameTopPanel.png", "piecePanelButton_JButton_" + i + "_"+ j, "NONE", 201+i*112 , 22+j*143, 113, 144);
				piecePanelButton[i][j].addMouseListener(this);
				mainPanel.add(piecePanelButton[i][j], (Integer)350);
			}
		}

		// 駒を置くためのペインを作成
		piecePanel = new JLayeredPane();
		piecePanel.setBounds(0, 0, 1440, 900);
		mainPanel.add(piecePanel, (Integer)400);

		// 駒の位置を初期化
		Piece.resetPiece();

		/*----------------------
		  チャット関係レイアウト配置
		----------------------*/
		// 受信用テキストエリアの生成と設置
		JTextArea getTextArea = new JTextArea(20, 21);
		getTextArea.setLineWrap(true); // 折り返しを有効にする
		getTextArea.setBounds(1115, 137, 280, 338);
		getTextArea.setEditable(false); // 編集不可にする
		getTextArea.setForeground(Color.WHITE); // 文字色を白色にする
		getTextArea.setOpaque(false);   // 背景色を透明にする
		mainPanel.add(getTextArea, (Integer)360);

		// 送信用テキストエリアの生成と設置
		JTextArea sendTextArea = new JTextArea(1, 21);
		sendTextArea.setLineWrap(true); // 折り返しを有効にする
		sendTextArea.setBounds(1115, 588, 280, 32);
		sendTextArea.setForeground(Color.WHITE); // 文字色を白色にする
		sendTextArea.setOpaque(false);   // 背景色を透明にする
		mainPanel.add(sendTextArea, (Integer)360);

		// 送信用ボタンの生成と設置
		JButton sendButton = MainPanel.createImageButton("img/sendButton.png", "sendButton_JButton", "sendButton_JButton", 1308, 633, 96, 42);
		sendButton.addMouseListener(this);
		sendButton.setBorderPainted(false); // ボタンの枠線を非表示にする
		mainPanel.add(sendButton, (Integer)360);

		/*-------------------------
		  メッセージエリアの生成
		-------------------------*/
		messageArea = new JTextArea(15, 10);
		messageArea.setForeground(Color.BLACK);
		messageArea.setLineWrap(true);
		messageArea.setBounds(1130, 705, 190, 115);
		messageArea.setEditable(false);
		messageArea.setText("コマの初期配置を変更しよう！！");
		mainPanel.add(messageArea, (Integer)360);	


		// 駒の所持数を表示するラベルの生成と設置
		pieceCountLabel = new JLabel[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (i == 0) {
					createPieceCountLabel(i,  j, 36 + j * 30);
				} else {
					createPieceCountLabel(i,  j, 490 + j * 30);	
				}
			}
		}

		/*--------------------
		   名前ラベルの生成と設置
		--------------------*/
		if ( _name == null || _name.length() == 0 ) _name = "NO-NAME";

		JLabel myNameLabel = new JLabel(name);
		JLabel enemyNameLabel = new JLabel(_name);

		myNameLabel.setBounds(1169, 520, 200, 30);
		enemyNameLabel.setBounds(1169, 67, 200, 30);

		Font f = new Font("Serif", Font.PLAIN, 16);
		myNameLabel.setFont(f);
		enemyNameLabel.setFont(f);

		myNameLabel.setForeground(Color.WHITE);
		enemyNameLabel.setForeground(Color.WHITE);

		mainPanel.add(myNameLabel, (Integer)360);
		mainPanel.add(enemyNameLabel, (Integer)360);

		// 初期配置の変更を促すダイアログを表示
		information.showDialog("img/dialogGamePre.png", "test", "test", 1);
	}

	/**
	 * 駒の所持数を表示させるラベルを生成、設置するメソッド
	 */
	public static void createPieceCountLabel(int i, int j, int y)
	{
		pieceCountLabel[i][j] = new JLabel("4");
		pieceCountLabel[i][j].setForeground(Color.WHITE);
		Font f = new Font("Serif", Font.PLAIN, 22);
		pieceCountLabel[i][j].setFont(f);
		pieceCountLabel[i][j].setBounds(1370, y, 50, 50);
		mainPanel.add(pieceCountLabel[i][j], (Integer)370);
	}


	/**
	 * 選択したパネルの色を変えるメソッド
	 */
	public static void selectPanel()
	{
		int x = Piece.x1;
		int y = Piece.y1;


		if (Piece.clickCount == 1) {
			ImageIcon iconSelect = new ImageIcon("./img/gameTopPanelSelect.png");
			piecePanelButton[x][y].setIcon(iconSelect);
		} else {
			ImageIcon iconNoSelect = new ImageIcon("./img/gameTopPanel.png");
			piecePanelButton[x][y].setIcon(iconNoSelect);
	 	}
	}


	/**
	 * 自分がコマをおけるか判定するメソッド
	 */
	public static boolean canSetPiece()
	{
		if (turn.equals("YES")) return true;

		return false;
	}

	/**
	 * コマを置ける権利を変更するメソッド
	 */
	public static void changeSetPiece()
	{
		if (turn.equals("YES")) {
			turn = "NO";
		} else {
			turn = "YES";
		}
	}



	/*------------------------ MouseEvent ------------------------------*/
	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void mousePressed(MouseEvent e)
	{

	}


	public void mouseReleased(MouseEvent e)
	{

		// getComponetType[0]に個別名、[1]にコンポーネント名が格納される
		String componentType[] = MainPanel.getComponentType(e);

		// 360番に配置されているコンポーネントを配列に格納
		Component labelArray[] = mainPanel.getComponentsInLayer((Integer)360);
		// チャット送信処理
		if (componentType[1].equals("JButton")) {
			if (componentType[0].equals("sendButton")) {
				// TextAreaから文字列を取得しサーバーに送信する
				JTextArea sendTextArea = (JTextArea)labelArray[1];
				String str = "chat_" + "［" + name + "］" + sendTextArea.getText();
				out.println(str);

				// 送信した後は文字列をテキストエリアから削除
				sendTextArea.setText("");

			// パネル操作処理
			} else if (componentType[0].equals("piecePanelButton")) {

				// 自分のターンかどうか判定
				if (!canSetPiece() && gamePre.equals("OFF")) return;

				// クリックしたパネル情報の取得
				JButton panelComponent = (JButton)e.getComponent();
				Piece.clickCount++;
				Piece.getVariable(panelComponent);

				// 選択したパネルの色を変更
				selectPanel();


				//Piece.showVariable(); // debug

				/*-----------------------------
				   コマの移動処理
				-----------------------------*/
				if (Piece.clickCount == 2) {

					// コマの移動					
					Piece.movePiece();


					// 変数の開放
					Piece.releaseVariable();
				}
			}
		}

	}


	public void mouseClicked(MouseEvent e)
	{
		// ここに処理を書いてしまうとドラッグしてしまったとき、反応しないので書かない。
	}

	public void actionPerformed(ActionEvent e)
	{
	}
}