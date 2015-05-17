/**
 * MainPanel.java
 * トップ画面を扱うクラス
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.lang.*;

public class MainPanel extends JFrame implements MouseListener, ActionListener
{
	static JLayeredPane mainLayerPane = new JLayeredPane();   // 最下層のレイヤー この上にコンポーネントを追加していく
	static GamePanel gamePanel; // 対戦画面を扱うパネル
	static String name = "NO-NAME"; // ユーザ名
	static String ip = "localhost"; // ipアドレス
	static JLabel dialogLabel;

	// JLayerPaneの階層を定義
	public Integer baseLayer = new Integer(100);       // 一番下のレイヤー（主に背景)
	public Integer baseComponent = new Integer(150);   // 背景の上におくコンポーネントレイヤー
	public Integer middleLayer = new Integer(200);     // 中間層のレイヤー (主にダイアログ)
	public Integer middleComponent = new Integer(250); // 中間層のコンポーネントレイヤー

	// 音楽を扱うクラスのインスタンス取得
	static Player player = new Player();	


	public MainPanel()
	{
		/*-------------------------------
		  初期設定
		-------------------------------*/
		// 初期画面の生成
		setTitle("Geister");         // タイトルの設定
		setBounds(0, 0, 1440, 930); // サイズ設定
		setResizable(false);        // サイズの変更不可
		//setUndecorated(true);       // 画面の枠を非表示
		mainLayerPane.setLayout(null);
		setContentPane(mainLayerPane);

		// トップ画面の作成
		JLabel topLabel   = createImageLabel("img/top_16.jpg", "top_JLabel", 0, 0, 1440, 900);
		JLabel startLabel = createImageLabel("img/start.png", "start_JLabel", 470, 422, 174, 60);
		JLabel startHoverLabel = createImageLabel("img/start_hover.png", "startHover_JLabel", 470, 422, 174, 60);
		JLabel exitLabel  = createImageLabel("img/exit.png", "exit_JLabel", 800, 420, 138, 59);
		JLabel exitHoverLabel = createImageLabel("img/exit_hover.png", "exitHover_JLabel", 800, 420, 138, 59);

		// 作成したコンポーネントをペインに貼り付けていく
		mainLayerPane.add(topLabel, baseLayer);
		mainLayerPane.add(startLabel, baseComponent);
		mainLayerPane.add(startHoverLabel, baseComponent);
		mainLayerPane.add(exitLabel, baseComponent);
		mainLayerPane.add(exitHoverLabel, baseComponent);

		// イベントの登録
		startLabel.addMouseListener(this);
		startHoverLabel.addMouseListener(this);
		exitLabel.addMouseListener(this);
		exitHoverLabel.addMouseListener(this);

		// ホバーした時の画像は非表示にしておく
		startHoverLabel.setVisible(false);
		exitHoverLabel.setVisible(false);
	}

	/*------------------------ module ---------------------------------*/

	/**
	 * マウスイベントが起こったコンポーネントの種類を判定するメソッド
	 *
	 * 引数
	 * MouseEvent event : 発火したイベント
	 * 戻り値
	 * String[] componentType : イベントが起こったコンポーネントの種類を表す文字列 [0]に個別名 [1]にコンポーネント名
	 */
	public static String[] getComponentType(MouseEvent event)
	{
		String componentType[];

		String componentSourse = event.getComponent().getName();
		componentType = componentSourse.split("_");

		return componentType;
	}


	/**
	 * 画像付きラベルを生成するメソッド
	 *
	 * 引数
	 * String imageName : 画像ファイル名
	 * 戻り値
	 * JLabel imageLabel : 生成されたラベル
	 */
	public static JLabel createImageLabel(String imageName, String actionString, int x, int y, int width, int height)
	{
		// ラベルの生成
		ImageIcon imageIcon = new ImageIcon(imageName);
		JLabel imageLabel = new JLabel(imageIcon);
		imageLabel.setName(actionString);

		// 配置の設定
		imageLabel.setBounds(x, y, width, height);

		return imageLabel;
	}

	/**
	 * 画像付きのボタンを生成するメソッド
	 * 
	 * 引数
	 * str imageName : 画像ファイル名, str actionString, int actionCommand
	 * int x, int y, int width, int height
	 */
	public static JButton createImageButton(String imageName, String actionString, String actionCommand, int x, int y, int width, int height)
	{
		// ボタンの作成
		ImageIcon imageIcon = new ImageIcon(imageName);
		JButton imageButton = new JButton(imageIcon);
		imageButton.setName(actionString);
		imageButton.setActionCommand(actionCommand);

		// 配置の設定
		imageButton.setBounds(x, y, width, height);

		return imageButton;
	}

	/*---------------------- Dialog Module -----------------------------------*/

	/**
	 * ダイアログウィンドウを表示する
	 */
	public void showDialogWindow()
	{
		JLayeredPane dialogPane = new JLayeredPane();
		dialogPane.setBounds(210, 20, 1006, 592);
		dialogLabel = createImageLabel("img/dialogEnterName.png", "nameEnter", 0, 0, 1006, 592);
		dialogPane.add(dialogLabel, new Integer(105));
		mainLayerPane.add(dialogPane, middleLayer);

		// 入力フィールドの設置
		showEnterNameItem(dialogPane);
		showDialog thread1 = new showDialog(dialogPane);
		thread1.start();
		repaint();
	}


	/**
	 * ダイアログウィンドウを非表示にする
	 */
	public void hideDialogWindow(Component dialogPane)
	{
		hideDialog thread2 = new hideDialog(dialogPane);
		thread2.start();
	}


	/**
	 * 名前入力画面を設置する
	 */
	public void showEnterNameItem(JLayeredPane dialogPane)
	{
		JTextField nameEnterField = new JTextField("NO-NAME");
		nameEnterField.setBounds(355, 270, 120, 30);
		JTextField ipEnterField = new JTextField("localhost");
		ipEnterField.setBounds(550, 270, 120, 30);

		JLabel goLabel = createImageLabel("img/go.png", "go_JLabel", 300, 330, 77, 51);
		JLabel goHoverLabel = createImageLabel("img/go_hover.png", "goHover_JLabel", 300, 330, 77, 51);
		JLabel backLabel = createImageLabel("img/back.png", "back_JLabel", 550, 330, 137, 51);
		JLabel backHoverLabel = createImageLabel("img/back_hover.png", "backHover_JLabel", 550, 330, 137, 51);


		goLabel.addMouseListener(this);
		goHoverLabel.addMouseListener(this);
		backLabel.addMouseListener(this);
		backHoverLabel.addMouseListener(this);

		dialogPane.add(goLabel, middleComponent);
		dialogPane.add(goHoverLabel, middleComponent);
		dialogPane.add(backLabel, middleComponent);
		dialogPane.add(backHoverLabel, middleComponent);
		dialogPane.add(nameEnterField, middleComponent);
		dialogPane.add(ipEnterField, middleComponent);

		goHoverLabel.setVisible(false);
		backHoverLabel.setVisible(false);

		repaint();
	}

	/*------------------------ Mouse event ---------------------------------*/

	public void mouseEntered(MouseEvent e)
	{
		String componentType[] = getComponentType(e);
		// ラベルイベント時の処理
		if (componentType[1].equals("JLabel")) {
			Component labelArray[] = mainLayerPane.getComponentsInLayer(baseComponent);

			if (componentType[0].equals("start")) {
				// startLabelを非表示、startHoverLabelを表示する
				labelArray[0].setVisible(false);
				labelArray[1].setVisible(true);
				repaint();

			} else if (componentType[0].equals("exit")) {
				// exitLabelを非表示、exitHoverLabelを表示する
				labelArray[2].setVisible(false);
				labelArray[3].setVisible(true);
				repaint();
			} else if (componentType[0].equals("go")) {
				Container parentPane = e.getComponent().getParent(); // ダイアログのペインを取得
				Component componentArray[] = parentPane.getComponents();

				componentArray[0].setVisible(false);
				componentArray[1].setVisible(true);

				repaint();

			} else if (componentType[0].equals("back")) {
				Container parentPane = e.getComponent().getParent(); // ダイアログのペインを取得
				Component componentArray[] = parentPane.getComponents();

				componentArray[2].setVisible(false);
				componentArray[3].setVisible(true);

				repaint();
			} else {

			}

		// ボタンイベント時の処理
		} else if (componentType[1].equals("JButton")) {

		// その他のイベントの処理
		} else {

		}

	}


	public void mouseExited(MouseEvent e)
	{
		String componentType[] = getComponentType(e);

		// ラベルイベント時の処理
		if (componentType[1].equals("JLabel")) {
			Component labelArray[] = mainLayerPane.getComponentsInLayer(baseComponent);
			if (componentType[0].equals("startHover")) {
				// startHoverLabelを非表示、startLabelを表示する
				labelArray[0].setVisible(true);
				labelArray[1].setVisible(false);
				repaint();

			} else if (componentType[0].equals("exitHover")) {
				// exitHoberLabelを非表示、exitLabelを表示する
				labelArray[2].setVisible(true);
				labelArray[3].setVisible(false);
				repaint();

			} else if (componentType[0].equals("goHover")) {
				Container parentPane = e.getComponent().getParent(); // ダイアログのペインを取得
				Component componentArray[] = parentPane.getComponents();
				componentArray[0].setVisible(true);
				componentArray[1].setVisible(false);

				repaint();

			} else if (componentType[0].equals("backHover")) {
				Container parentPane = e.getComponent().getParent(); // ダイアログのペインを取得
				Component componentArray[] = parentPane.getComponents();

				componentArray[2].setVisible(true);
				componentArray[3].setVisible(false);

				repaint();
			} else {

			}

		// ボタンイベント時の処理
		} else if (componentType[1].equals("JButton")) {

		// その他のイベントの処理
		} else {

		}
	}


	public void mousePressed(MouseEvent e)
	{

	}


	public void mouseReleased(MouseEvent e)
	{

	}


	public void mouseClicked(MouseEvent e)
	{
		String componentType[] = getComponentType(e);

		// ラベルイベント時の処理
		if (componentType[1].equals("JLabel")) {
			Component labelArray[] = mainLayerPane.getComponentsInLayer(baseComponent);

			if (componentType[0].equals("startHover")) {
				// ダイアログを表示してstart, exitボタンを非表示にする
				showDialogWindow();
				labelArray[1].setVisible(false);
				labelArray[2].setVisible(false);

				// バグ修正の為に一時的にマウスイベントを無効化
				labelArray[1].removeMouseListener(this);

			} else if (componentType[0].equals("exitHover")) {
				System.exit(0);


			} else if (componentType[0].equals("goHover")) {

				Container parentPane = e.getComponent().getParent(); // ダイアログのペインを取得
				Component componentArray[] = parentPane.getComponents();
				JTextField textField = (JTextField)componentArray[4];
				JTextField ipField = (JTextField)componentArray[5];
				name = textField.getText();
				ip   = ipField.getText();


				// ダイアログウィンドの画面を変更
				Component dialogComponent[] = e.getComponent().getParent().getComponents();
				for (int i = 0; i < 6; i++) {
					dialogComponent[i].setVisible(false);
					dialogComponent[i].removeMouseListener(this);
				}

				// マッチング開始
				gamePanel = new GamePanel(mainLayerPane, name, ip);

			} else if (componentType[0].equals("backHover")) {
				Component dialogPane = e.getComponent().getParent(); // ダイアログのペインを取得
				hideDialogWindow(dialogPane);
				labelArray[0].setVisible(true);
				labelArray[2].setVisible(true);

				// 無効化していたマウスイベントを有効化
				labelArray[1].addMouseListener(this);
			} else {

			}

		// ボタンイベント時の処理
		} else if (componentType[1].equals("JButton")) {

		// その他のイベントの処理
		} else {

		}
	}


	public void actionPerformed(ActionEvent e)
	{

	}


}
