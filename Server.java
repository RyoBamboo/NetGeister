import java.io.*;
import java.net.*;

/**
 * サーバークラス
 *
 * MEMO:
 * デバッグ用にorderGameStartメソッドの中を変更中。
 * デバッグが必要なくなったら変更を戻す必要有り
 * 他に接続可能人数を２から１００に変更中。デバッグの必要がなくなったら2に戻すこと。
 * maxConnection = 2 => 100;
 */


class ClientProcThread extends Thread{
	int number;
	Socket inSocket;
	InputStreamReader myIsr;
	BufferedReader myIn;
	PrintWriter myOut;
	String clientName;
	int gameFlag;

	public ClientProcThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out)
	{
		// 受け取ったパラメータを変数に格納
		number   = n;
		inSocket = i;
		myIsr    = isr;
		myIn     = in;
		myOut    = out;

		
	}


	public void run()
	{
		try {
			/*-----------------------
			// ソケットへの入力を監視する
			-----------------------*/

			if (number == 0) {
				Server.name = myIn.readLine();
			} else {
				Server._name = myIn.readLine();
			}

			// 対戦相手が2人揃うまでクライアントに待機するように指示
			orderGameStart(number);
			while (true) {

				// クライアントからの入力を読み込む
				String str = myIn.readLine();
				if (str != null) {
					Server.sendAll(str);
				}
			}
		} catch (IOException e) {

		}
	}

	/*---------------------- Thread Modules ------------------------*/

	/**
	 * ゲームの開始、待機をクライアントに指示するメソッド
	 */
	public void orderGameStart(int number)
	{
		if (number%2 == 0) {
			Server.sendAll("info_gameStop");
			//Server.sendAll("info_gameStart");
		} else {
			Server.sendAll("info_name_" + Server.name);
			Server.sendAll("info_name_" + Server._name);
			Server.sendAll("info_gameStart");
		}
	}
}

class Server
{
	public static int gameFlag = -1; // ゲーム開始判別フラグ
	static int maxConnection = 100; // 最大接続可能人数
	static Socket[] inSocket;    // 受け付け用ソケット
	static InputStreamReader[] isr;
	static BufferedReader[] in;
	static PrintWriter[] out;
	static ClientProcThread[] myClientProcThread;
	static int member;
	static String name;
	static String _name;

	public Server()
	{

	}

	// クライアント全員にメッセージを送信する
	public static void sendAll(String str)
	{
		for (int i = 0; i <= member; i++) {
			out[i].println(str);
			out[i].flush();
		}
	}

	public static void main(String[] args)
	{
		// 必要な配列を確保する
		inSocket = new Socket[maxConnection];
		isr = new InputStreamReader[maxConnection];
		in  = new BufferedReader[maxConnection];
		out = new PrintWriter[maxConnection];
		myClientProcThread = new ClientProcThread[maxConnection];

		int n  = 0; // 必要な配列をクライアントごとに振り分ける為の変数
		member = 0;

		/*---------------
		  ソケットの作成
		---------------*/
		try {
			// サーバーソケットの生成
			System.out.println("The server has launched!");
			ServerSocket server = new ServerSocket(10000); // 10000ポートを使用する

			while (true)
			{
				// クライアントの受付
				inSocket[n] = server.accept();
				System.out.println("ClientAccepted!!!");
				member = n;

				// 必要な入出力ストリームを作成する
				isr[n] = new InputStreamReader(inSocket[n].getInputStream());
				in[n]  = new BufferedReader(isr[n]);
				out[n] = new PrintWriter(inSocket[n].getOutputStream(), true);

				// 必要なパラメーターを渡してスレッドを生成し、スレッドを開始する
				myClientProcThread[n] = new ClientProcThread(member, inSocket[n], isr[n], in[n], out[n]);
				myClientProcThread[n].start();

				n++;
			}
		} catch (Exception e) {
			System.out.println("ソケットの作成中にエラーが発生しました : " + e);
		}
	}
}

