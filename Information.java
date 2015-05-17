import java.awt.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.util.Random;


/**
 * 通知を扱うクラス
 * 主にダイアログの表示に使用
 */
public class Information implements ActionListener, MouseListener
{
	static JLayeredPane dialogPane = new JLayeredPane();
	static JButton button1;
	static JButton button2;
	static ArrayList<String> messageList = new ArrayList<String>();
	static Timer timer = new Timer(); // 一定時間ごとにメッセージをきれかえるためのtimerインスタンス

	// 通知やダイアログを貼付けるペインを引数にとる
	Information()
	{
		// 配列にメッセージをセットする
		setMessageList();

	}


	/*----------------- module -----------------------*/

	/**
	 * 文字列型の配列にメッセージを格納する
	 */
	public void setMessageList()
	{
		// ファイルデータの生成
		try {
			File mFile = new File("messageList.txt");

			// ファイルデータの読み込み
			FileReader reader = new FileReader(mFile);
			BufferedReader br = new BufferedReader(reader);

			/*---------------------
			  メッセージの格納
			--------------------*/
			String str;
			try {
				while ((str = br.readLine()) != null) {
					messageList.add(str);
				} 
			}catch (IOException e) {
				System.out.println(e);
					return;
			}
		} catch (FileNotFoundException e) {
			return;
		}
	}

	/**
	 * スケジュールを設定する
	 */
	static public void setSchedule()
	{
	    timer.scheduleAtFixedRate(new changeMessageTask(), 5000,1000);
	}

	static public void changeMessage()
	{
		// 乱数の生成
        Random rnd = new Random(); 
        int ran = rnd.nextInt(500);

        // インデックスの生成
        int index = ran % messageList.size();

        // メッセージの読み込み
        String message = messageList.get(index);

		GamePanel.messageArea.setText(message);	
	}

	/**
	 * 相手にコマを取られたことを通知する
	 */


	/**
	 * 相手のコマをとったことを通知する
	 */

	/**
	　* ターンを知らせる
	 */
	static public void informTurn(String turn)
	{
		// タイマーを止める
		//changeMessageTask.stopMessage();

		// どっちのターンか判定
		String message;
		if (turn.equals("YES")) {
			message = "自分のターンだよ。";
		} else {
			message = "相手のターンだよ。";
		}

		GamePanel.messageArea.setText(message);
		// タイマーの再開
		//changeMessageTask.startMessage();
	}




	/**
	 * ダイアログを表示するメソッド
	 * 引数:
	 * str OK_button : OK(YES)ボタンに表示する文字列
	 * str NO_button : NOボタンに表示する文字列
	 *  
	 */
	public void showDialog(String dialogImage, String okImage, String noImage, int buttonCount)
	{
		dialogPane.setBounds(218, 150, 639, 308);
		// 画像を読み込んで生成貼り付け
		JLabel dialogLabel = MainPanel.createImageLabel(dialogImage, "nameEnter", 0, 0, 639, 308);
		dialogPane.add(dialogLabel, (Integer)100);



		// 貼り付けるボタン数によって分岐
		if (buttonCount == 1) {
			/*-------------
			  ボタンの配置
			-------------*/
			button1 = MainPanel.createImageButton("img/blueButton.png", "gamePre_JButton", "", 280, 220, 79, 34);
			button1.addMouseListener(this);
			dialogPane.add(button1, (Integer)150);

			dialogPane.setVisible(true);

			/*-----------------------------------
			  半透明のラベルを設置してダイアログを見やすくする 
			-----------------------------------*/
			/*
			JLabel wrapperLabel = new JLabel();
			wrapperLabel.setBackground(new Color((float)1.0, (float)1.0, (float)1.0, (float)0.5));
			wrapperLabel.setBounds(20, 20, 1052, 863);
			wrapperLabel.setOpaque(true);
			GamePanel.piecePanel.add(wrapperLabel, (Integer)0);	
			*/
		} else {

			button1 = MainPanel.createImageButton("img/blueButton1.png", "retry_JButton", "", 180, 190, 79, 34);
			button2 = MainPanel.createImageButton("img/redButton.png", "end_JButton", "", 330, 190, 79, 34);

			button1.addMouseListener(this);
			button2.addMouseListener(this);

			dialogPane.add(button1, (Integer)150);
			dialogPane.add(button2, (Integer)150);

			dialogPane.setVisible(true);

		}
		/*
		JButton yesButton = MainPanel.createImageButton();
		JButton noButton = MainPanel.createImageButton();
		*/

		// ダイアログペインの貼り付け
		GamePanel.piecePanel.add(dialogPane, (Integer)1000);
	}

	public static void removeDialog()
	{
		dialogPane.setVisible(false);
		dialogPane.removeAll();

		// wrapperLabelの非表示
	}


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
	}


	public void mouseClicked(MouseEvent e)
	{
		// getComponetType[0]に個別名、[1]にコンポーネント名が格納される
		String componentType[] = MainPanel.getComponentType(e);

		if (componentType[0].equals("gamePre")) {
			// 対戦相手に名前を送る
			GamePanel.out.println(GamePanel.name);
			// ダイアログを非表示にする
			removeDialog();

			// ゲーム準備フラグをOFF
			GamePanel.gamePre = "OFF";
			// メッセージの表示
			GamePanel.information.informTurn(GamePanel.turn);
			//GamePanel.information.setSchedule();	
		} else if (componentType[0].equals("retry")) {
			removeDialog();
			Piece.resetPiece();
			GamePanel.out.println("info_retry");
		} else if (componentType[0].equals("end")) {
			GamePanel.out.println("info_end");
		}
	}

	public void actionPerformed(ActionEvent e)
	{

	}

	static class changeMessageTask extends TimerTask {
        
        public void run(){
        	changeMessage();
        }


        static public synchronized void stopMessage()
		{
			try {
				timer.wait();
			} catch(InterruptedException e) {
				System.out.println(e);
			}
		}

		static public void startMessage()
		{
			timer.notify();	
		}
    }
}