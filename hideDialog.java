import java.awt.*;
import java.lang.*;
import javax.swing.*;


// ダイアログを画面上から撤退させるクラス
public class hideDialog extends Thread
{
	Component dialogLabel;

	hideDialog(Component label)
	{
		dialogLabel = label;
	}

	public void run()
	{
		try {
			for(int i = 40; i >= -600; i = i - 30) {
				dialogLabel.setBounds(210, i, 1006, 592);
				sleep(50);
			}
		} catch (InterruptedException e) {

		}
	}
}