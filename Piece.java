import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 * 駒の操作を扱うクラス
 *
 */

class Piece
{

	static int myRedPiece     = 4; 
	static int myBluePiece    = 4;
	static int clickCount;
	static int x1, y1, x2, y2;   // 駒の位置情報格納変数
	static String status1, status2; // 駒の状態格納変数
	Piece()
	{

	}

	/* --------------------- module ---------------------*/

	/**
	 * 駒の配置を初期化するメソッド
	 *
	 */
	public static void resetPiece()
	{
		/*-------------------------
		  駒の数の初期化 
		-------------------------*/
		myRedPiece  = 4;
		myBluePiece = 4;

		/*-------------------------
		  全てのパネルの状態を初期化する 
		-------------------------*/
		// 通常パネルの初期化
		for(int i=0; i < 6; i++) {
			for(int j=0; j < 6; j++) {
				GamePanel.piecePanelButton[i][j].setActionCommand("none");
			}
		}

		/*-------------------------
		  全ての駒を画面から削除する 
		-------------------------*/
		GamePanel.piecePanel.setVisible(false);
		GamePanel.piecePanel.removeAll();
		GamePanel.piecePanel.setVisible(true);

		/*-------------------------
		  駒を配置する	
		-------------------------*/
		// 自分の駒を配置
		for(int y = 4; y < 6; y++) {
			for (int x = 1; x < 5; x++) {
				if (y == 4) {
					setPiece(x, y, "red");
				} else {
					setPiece(x, y, "blue");
				}
			}
		}

		// 敵の駒を配置
		for (int y = 0; y < 2; y++) {
			for (int x = 1; x < 5; x++) {
				setPiece(x, y, "enemy");
			}
		}

	}

	/**
	 * 駒を配置するメソッド
	 * 
	 * 引数:
	 * int x, y
	 */
	public static void setPiece(int x, int y, String status)
	{	

		JLabel newPiece;
		Integer layer = 100 * y; // wrapperLabelをpiecePanelの一番下(0)に貼り付けるために10をプラス

		if (status.equals("enemy")) {
			newPiece = MainPanel.createImageLabel("img/piece_front.png", x + "_" + y, x * 112 + 217, y * 142 -30, 80, 157);	
		} else {
			newPiece = MainPanel.createImageLabel("img/piece_back_" + status + ".png", x + "_" + y, x * 112 + 217, y * 142 -30, 80, 157);	
		}
		
		// 画面への張り付け	
		GamePanel.piecePanel.add(newPiece, layer);	

		// パネルの状態の変更
		GamePanel.piecePanelButton[x][y].setActionCommand(status);
	}


	/**
	 * 駒を削除するメソッド
	 *
	 */
	public static void removePiece(int x, int y)
	{
		// 受け取ったy座標から横一列のコンポーネントを取得
		Component[] cArray = GamePanel.piecePanel.getComponentsInLayer(y * 100);

		// 0 ~ 5のコンポーネントを連番で取得してgetNameが位置するか判定
		for (int i = 0; i < 6; i++) {
			try {
				// JLabelのNameプロパティのx,y座標が一致すればJLabelを消去
				String[] cPosition = cArray[i].getName().split("_");
				if (x == Integer.parseInt(cPosition[0]) && y == Integer.parseInt(cPosition[1])) {
					cArray[i].setVisible(false);
					GamePanel.piecePanel.remove(cArray[i]);	
					// パネルの状態の変更
					GamePanel.piecePanelButton[x][y].setActionCommand("none");
				}
			} catch(IndexOutOfBoundsException error) {
				// コンポーネントがないのでループを抜ける
				break;
			}
		}

		// パネル状態の初期化
		GamePanel.piecePanelButton[x][y].setActionCommand("none");
	}


	/**
	 * 駒を移動させるメソッド
	 *
	 */
	public static void movePiece()
	{
		if (GamePanel.gamePre.equals("ON")) {
			/*-------------------------------
			  自分の駒を入れ替える(ゲーム準備時のみ)
			-------------------------------*/
			if (isMine()) {
				// gamePreがONでなおかつ、選択した両パネルに自駒が存在するとき
				removePiece(x1, y1);
				removePiece(x2, y2);
				setPiece(x1, y1, status2);
				setPiece(x2, y2, status1);
			}
		} else {
			// 移動先が問題ないか確認
			if (isCurrentMove()) {

				// 自分のターンであるか判定
				if (!GamePanel.canSetPiece()) return;

				/*----------------
				  自分の駒を移動させる
				-----------------*/
				if ((status1.equals("red") || status1.equals("blue")) && status2.equals("none")) {
					removePiece(x1, y1);
					setPiece(x2, y2, status1);
					isFinish();
				}

				/*----------------
				  相手の駒を取る
				-----------------*/
				if (isMineAndEnemy()) {
					removePiece(x1, y1);
					removePiece(x2, y2);
					setPiece(x2, y2, status1);
					isFinish();
				}

				// 対戦相手にコマの移動を通知
				if (isMineAndEnemy() || isMineAndNone()) {
					GamePanel.out.println("piece_" + getPieceInfo());
				}
			}
		}
	}

	/**
	 * 敵のコマの動きを反映させる
	 */
	public static void reflectEnemyMove(int x1, int y1, String status1, int x2, int y2, String status2)
	{
		x1 = 5 - x1;
		y1 = 5 - y1;
		x2 = 5 - x2;
		y2 = 5 - y2;

		/*-----------------
		  相手が単に移動するだけ
		-----------------*/
		if (status2.equals("none")) {
			removePiece(x1, y1);
			setPiece(x2, y2, "enemy");
		}

		/*-----------------
		  相手が自分のコマをとる
		-----------------*/
		if (status2.equals("enemy")) {
			/*-------------------
			   駒の所持数を１つ減らす
			-------------------*/
			if (getColor(x2, y2).equals("red")) {
				myRedPiece--;
				// 自分の表示を更新
				changePieceCount(1, 0, myRedPiece);	
				// 相手の表示を更新 
				GamePanel.out.println("info_" + "countChange_" + "0_0_" + myRedPiece);
			} else {
				myBluePiece--;
				// 表示を更新
				changePieceCount(1, 1, myBluePiece);
				// 相手の表示を更新	
				GamePanel.out.println("info_" + "countChange_" + "0_1_" + myBluePiece);
			}


			// 駒の削除と配置
			removePiece(x1, y1);
			removePiece(x2, y2);
			setPiece(x2, y2, "enemy");
			isFinish();
		}
	}

	/**
	 * 持ち駒の表示を変更するメソッド
	 */
	public static void changePieceCount(int i, int j, int count)
	{
		String str = String.valueOf(count);
		GamePanel.pieceCountLabel[i][j].setText(str);
	}

	/**
	 * 受け取った座標の自分の駒の色を取得するメソッド
	 */
	public static String getColor(int x, int y)
	{
		JButton button = GamePanel.piecePanelButton[x][y];
		String str = button.getActionCommand(); 
		return str;
	}

	/**
	 * 受け取ったJButtonから駒の移動に必要な情報を取得するメソッド
	 */
	 public static void getVariable(JButton button)
	 {
	 	if (clickCount == 1) {
			// 位置情報の取得
			String str = button.getName();	
			String[] strArray = str.split("_");
			x1 = Integer.parseInt(strArray[2]);
			y1 = Integer.parseInt(strArray[3]);
			// 状態の取得
			status1 = button.getActionCommand();
		} else if (clickCount == 2) {
			// 位置情報の取得
			String str = button.getName();	
			String[] strArray = str.split("_");
			x2 = Integer.parseInt(strArray[2]);
			y2 = Integer.parseInt(strArray[3]);

			// 状態の取得
			status2 = button.getActionCommand();
		}

		return;
	 }


	 /**
	  * 移動前と移動後のコマ情報を文字列でまとめて返すメソッド
	  * （主に相手に自分のコマの移動を反映させる時に使用）
	  */
	 public static String getPieceInfo()
	 {
	 	return x1 + "_" + y1 + "_" + status1 + "_" + x2 + "_" + y2 + "_" + status2;
	 }


	/**
	 * 格納されている駒情報を解放するメソッド
	 */
	 public static void releaseVariable()
	 {
	 	clickCount = 0;
	 	x1 = 0;
	 	x2 = 0;
	 	y1 = 0;
	 	y2 = 0;
	 	status1 = "";
	 	status2 = "";
	 }

	 /**
	  * 格納されている駒情報を出力するメソッド（デバッグ用）
	  */
	  public static void showVariable()
	  {
	  	System.out.println("clickCount =" + clickCount);
	  	System.out.println(x1);
	  	System.out.println(y1);
	  	System.out.println(status1);
	  	System.out.println(x2);
	  	System.out.println(y2);
	  	System.out.println(status2);
	  	System.out.println(GamePanel.gamePre);
	  }

	  /**
	   * 移動先が正しい位置かどうか判定するメソッド
	   */
	   public static boolean isCurrentMove()
	   {
	   	// 横移動
	   	if ((x1 + 1 == x2 || x1 - 1 == x2) && y1 == y2) return true;
	   	// 縦移動
	   	if ((y1 + 1 == y2 || y1 - 1 == y2) && x1 == x2) return true;

	   	return false;
	   }


	  /**
	   * 選択した二つの駒が自分のものであるか
	   */
	  public static boolean isMine()
	  {
	  	if (status1.equals("none") || status1.equals("enemy") || status2.equals("none") || status2.equals("enemy")) {
	  		return false;
	  	}
	  	return true;
	  }


	  /**
	   * 同じ駒を選択しているかどうか 
	   */
	  public static boolean isSame()
	  {
	  	if (x1 != x2 || y1 != y2) {
	  		return false;
	  	}

	  	return true;
	  }

	  /**
	   * 選択したコマが自分のモノと何もないところかどうか
	   */
	  public static boolean isMineAndNone()
	  {
	  	if ((status1.equals("red") || status1.equals("blue")) && status2.equals("none")) {
	  		return true;
	  	}

	  	return false;
	  }


	  /**
	   * 選択した２つの駒が相手のものと自分のものを１つずつ選択しているかどうか
	   */
	  public static boolean isMineAndEnemy()
	  {
	  	if ((status1.equals("red") || status1.equals("blue")) && status2.equals("enemy")) {
	  		return true;
	  	}
	  	return false;
	  }

	 /**
	 * ゲームが終了したか判定するメソッド
	 */
	 public static void isFinish()
	 {
	 	// 自分の青い駒がゴールに到達したとき
	 	if ((x2 == 0 && y2 == 0 || x2 == 5 && y2 == 0) && status1.equals("blue")) win(); 

	 	// 自分の赤い駒がすべて取られたとき
	 	if (myRedPiece == 0) win();

	 	// 自分の青い駒がすべて取られたとき
	 	if (myBluePiece == 0) lose();
	 }

	 /**
	* 勝利処理を行うメソッド
	*/
	public static void win()
	{
		System.out.println("win!!!!!!!");
		GamePanel.information.showDialog("img/dialogResultWin.png", "test", "test", 2);
		GamePanel.out.println("info_lose_" + GamePanel.myNum);

	}

	/**
	* 敗北処理を行うメソッド
	*/
	public static void lose()
	{
		System.out.println("lose!!!!!!!");
		GamePanel.information.showDialog("img/dialogResultLose.png", "test", "test", 2);
		GamePanel.out.println("info_win_" + GamePanel.myNum);

	}

}