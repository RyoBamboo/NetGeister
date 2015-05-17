import java.io.*;
import javax.sound.sampled.*;


public class MainApp
{
	public MainApp()
	{
		// ウィンドウの作成
		MainPanel mainPanel = new MainPanel();
		mainPanel.setVisible(true);

		// オープニングBGMの再生
		//playSound("bgm/op1.wav");
		//playSound("bgm/op2.wav");

	}


	public static void main(String[] argc)
	{
		MainApp mainApp = new MainApp();
	}


	/**
	 * BGMを再生するメソッド
	 */
	public static void playSound(String name)
	{
		AudioFormat format = null;
        DataLine.Info info = null;
        Clip line = null;
        File audioFile = null;

        try{
            
            audioFile = new File(name);
            format = AudioSystem.getAudioFileFormat(audioFile).getFormat();
            info = new DataLine.Info(Clip.class, format);
            line = (Clip)AudioSystem.getLine(info);
            line.open(AudioSystem.getAudioInputStream(audioFile));
            line.start();
        }   catch(Exception e){
            e.printStackTrace();
			System.exit(0);
        }

	}	
	
}