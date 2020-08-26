package parking;

import java.awt.event.*;
import javax.swing.*;

public class PEnter extends JFrame{
	private PMain pmain;
	private PAMain pamain;

	private JButton btnOK, btnAdmin;
	private JButton btnInit;
	private JTextField userText;
	private JTextField phoneText;
	private JRadioButton[] rbtn = new JRadioButton[2];
	
	public PEnter(){
		// setting
		setTitle("Parking Management Program");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 450);
		setResizable(false);
		setLocation(600, 350);
		
		// panel
		JPanel panel = new JPanel();
		placeEnterPanel(panel);

		// add
		add(panel);
		// visible
		setVisible(true);
		
	}

	public void placeEnterPanel(JPanel panel){
		panel.setLayout(null);
				
		JLabel userLabel = new JLabel("Car      Number :");
		userLabel.setBounds(100, 210, 100, 125);
		panel.add(userLabel);
		
		JLabel passLabel = new JLabel("Phone Number :");
		passLabel.setBounds(100, 250, 100, 125);
		panel.add(passLabel);
		
		rbtn[0]=new JRadioButton("주차", true);
		rbtn[1]=new JRadioButton("출차");
		rbtn[0].setBounds(420, 270, 60, 25);
		rbtn[1].setBounds(420, 300, 60, 25);
		ButtonGroup group=new ButtonGroup();
		group.add(rbtn[0]);
		group.add(rbtn[1]);

		panel.add(rbtn[0]);
		panel.add(rbtn[1]);
		
		userText = new JTextField(20);
		userText.setBounds(200, 260, 200, 25);
		panel.add(userText);
		
		phoneText = new JTextField(20);
		phoneText.setBounds(200, 300, 200, 25);
		panel.add(phoneText);

		btnOK = new JButton("OK");
		btnOK.setBounds(190, 340, 100, 25);
		panel.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rbtn[0].isSelected()) {
					isOKCheck();
				}
				else {
					isExitCheck();
				}
			}
		});
		
		btnInit = new JButton("Reset");
		btnInit.setBounds(300, 340, 100, 25);
		panel.add(btnInit);
		btnInit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userText.setText("");
				phoneText.setText("");
			}
		});

		btnAdmin = new JButton("Admin");
		btnAdmin.setBounds(490, 380, 80, 20);
		panel.add(btnAdmin);
		btnAdmin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isAdminCheck();
			}
		});
		
	}
	public void isOKCheck(){
		this.dispose(); // 창닫기
		this.pmain = new PMain(new PMainUI(0, userText.getText(), phoneText.getText())); // 프레임 오픈
		pmain.appMain();
	}
	public void isExitCheck(){
		int result = JOptionPane.showConfirmDialog(null, userText.getText()+" 출차 하시겠습니까?", null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(result == 0) {
			this.dispose(); // 창닫기
			this.pmain = new PMain(new PMainUI(1, userText.getText(), phoneText.getText())); // 프레임 오픈
			pmain.appMain();
		}
	}
	public void isAdminCheck(){
		String name = JOptionPane.showInputDialog("Admin Password");
		if(name != null) {
      		if(name.equals("admin")) {
      			JOptionPane.showMessageDialog(null, "Success");
      			this.dispose(); // 창닫기
      			this.pamain = new PAMain(new PAMainUI());
      			pamain.appMain();
      		}
      		else {
      			JOptionPane.showMessageDialog(null, "Faild");
      		}
		}
	}
	public static void main(String[] args) {

	}
}
