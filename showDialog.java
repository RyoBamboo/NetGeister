import java.awt.*;
import java.lang.*;
import javax.swing.*;


// ダイアログを画面上から召還するクラス
public class showDialog extends Thread
{
	JLayeredPane dialogLabel;

	showDialog(JLayeredPane label)
	{
		dialogLabel = label;
	}

	public void run()
	{
		try {
			for(int i = -600; i <= 40; i = i + 30) {
				dialogLabel.setBounds(210, i, 1006, 592);
				sleep(50);
			}
		} catch (InterruptedException e) {

		}
	}
}