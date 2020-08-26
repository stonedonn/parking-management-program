package parking;

import java.awt.event.ActionListener;

import javax.swing.*;

public class PMainUI extends JFrame{
	private PEnter pEnter;
	protected JButton[] btn = new JButton[10];
	protected JButton back;
	protected JLabel userLabel, feeLabel_30, feeLabel_10;

    protected int flagN;
    protected String carN;
    protected String phoneN;
    
	public PMainUI(int flag, String name, String phone){
		// setting
		setTitle("Parking Management Program");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 450);
		setResizable(false);
		setLocation(600, 350);
		
		// panel
		JPanel panel = new JPanel();
		placeMainPanel(panel, name);
		flagN = flag;
		carN = name;
		phoneN = phone;
		// add
		add(panel);
		// visible
		setVisible(true);
	}
	
	public void placeMainPanel(JPanel panel, String name){
		panel.setLayout(null);
		
		for(int i = 0; i < 5; i++) {
			btn[i] = new JButton("" + (i+1) + "");
			btn[i].setBounds(((i+1)*100)-60, 20, 100, 100);
			panel.add(btn[i]);
		}
		for(int i = 5; i < 10; i++) {
			btn[i] = new JButton("" + (i+1) + "");
			btn[i].setBounds(((i-4)*100)-60, 130, 100, 100);
			panel.add(btn[i]);
		}

		userLabel = new JLabel(name +"님 환영합니다..");
		userLabel.setBounds(100, 250, 150, 50);
		panel.add(userLabel);
		feeLabel_30 = new JLabel("30");
		feeLabel_30.setBounds(350, 250, 200, 50);
		panel.add(feeLabel_30);
		feeLabel_10 = new JLabel("10");
		feeLabel_10.setBounds(350, 270, 200, 50);
		panel.add(feeLabel_10);
		back = new JButton("뒤로");
		back.setBounds(480, 350, 80, 50);
		panel.add(back);
	}
	public void addButtonActionListener(ActionListener listener) {
        // 이벤트 리스너 등록
		for(int i=0; i<10; i++) {
			btn[i].addActionListener(listener);
		}
		back.addActionListener(listener);
    }
}