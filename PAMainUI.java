package parking;

import java.awt.event.ActionListener;

import javax.swing.*;

public class PAMainUI extends JFrame{
	private PEnter pEnter;
	protected JButton[] btn = new JButton[10];
	protected JButton ok_30, ok_10, info, back;
	protected JLabel userLabel, feeLabel_30, feeLabel_10, pricesum;
	
	protected JTextField textField_30, textField_10;

    protected int flagN;
    protected String carN;
    protected String phoneN;
    
	public PAMainUI(){
		// setting
		setTitle("Parking Management Program");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 450);
		setResizable(false);
		setLocation(600, 350);
		
		// panel
		JPanel panel = new JPanel();
		placeMainPanel(panel);
		// add
		add(panel);
		// visible
		setVisible(true);
	}
	
	public void placeMainPanel(JPanel panel){
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

		userLabel = new JLabel("운영자님 환영합니다.");
		userLabel.setBounds(50, 250, 150, 50);
		panel.add(userLabel);

		textField_30 = new JTextField("");
		textField_30.setBounds(50, 300, 100, 25);
		panel.add(textField_30);
		textField_10 = new JTextField("");
		textField_10.setBounds(50, 330, 100, 25);
		panel.add(textField_10);
		ok_30 = new JButton("30분 변경");
		ok_30.setBounds(160, 300, 100, 25);
		panel.add(ok_30);
		ok_10 = new JButton("10분 변경");
		ok_10.setBounds(160, 330, 100, 25);
		panel.add(ok_10);
		
		info = new JButton("로그 정보");
		info.setBounds(160, 380, 100, 25);
		panel.add(info);
		
		feeLabel_30 = new JLabel("30");
		feeLabel_30.setBounds(350, 250, 200, 50);
		panel.add(feeLabel_30);
		feeLabel_10 = new JLabel("10");
		feeLabel_10.setBounds(350, 270, 200, 50);
		panel.add(feeLabel_10);

		pricesum = new JLabel("sum");
		pricesum.setBounds(350, 300, 200, 50);
		panel.add(pricesum);
		
		back = new JButton("뒤로");
		back.setBounds(480, 350, 80, 50);
		panel.add(back);
	}
	public void addButtonActionListener(ActionListener listener) {
        // 이벤트 리스너 등록
		for(int i=0; i<10; i++) {
			btn[i].addActionListener(listener);
		}
		ok_30.addActionListener(listener);
		ok_10.addActionListener(listener);
		info.addActionListener(listener);
		back.addActionListener(listener);
    }
}