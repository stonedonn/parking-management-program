package parking;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PInfo extends JFrame{
	
	public PInfo() {
		setTitle("�ð��뺰 ���� �׷���");
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(500,200);
		setPreferredSize(new Dimension(500,350));
		Container contentPane = getContentPane();
		
		DrawingPanel drawingPanel = new DrawingPanel();
		contentPane.add(drawingPanel, BorderLayout.CENTER);
		//�׷����� �׸� �г�
		
		JPanel controlPanel = new JPanel();
		JTextField text1 = new JTextField(3);
		JTextField text2 = new JTextField(3);
		JTextField text3 = new JTextField(3);
		JButton button = new JButton("�׷��� �׸���");
		controlPanel.add(new JLabel("01"));
		controlPanel.add(text1);
		controlPanel.add(new JLabel("02"));
		controlPanel.add(text2);
		controlPanel.add(new JLabel("03"));
		controlPanel.add(text3);
		controlPanel.add(button);
		contentPane.add(controlPanel, BorderLayout.SOUTH);
		button.addActionListener(new DrawActionListener(text1,text2,text3,drawingPanel));
		//"�׷��� �׸���" ��ư�� �������� �۵� �� �����͵��
		pack();
		setVisible(true);
	}
	
	public static void main(String args[])
	{
		
	}
}

//�׷��Ǹ� �׸��� �г� Ŭ����
class DrawingPanel extends JPanel
{
	int korean, english, math;
	public void paint(Graphics g){
	g.clearRect(0,0,getWidth(),getHeight());
	g.drawLine(50,250,350,250);
	for(int cnt = 1 ;cnt<11;cnt++)
	{
		g.drawString(cnt *10 +"",25,255-20*cnt);
		g.drawLine(50, 250-20*cnt, 350,250-20*cnt);
	}
	g.drawLine(50,20,50,250);
	g.drawString("����",100,270);
	g.drawString("����",200,270);
	g.drawString("����",300,270);
	g.setColor(Color.RED);
	if (korean>0)
		g.fillRect(110,250-korean*2,10,korean*2);
	if(english>0)
		g.fillRect(210,250-english*2,10,english*2);
	if(math>0)
		g.fillRect(310,250-math*2,10,math*2);
	}
	void setScores(int korean, int english, int math)
	{
		this.korean=korean;
		this.english=english;
		this.math=math;
	}
}

//��ư �������� �����ϴ� ������
class DrawActionListener implements ActionListener
{
	JTextField text1,text2,text3;
	DrawingPanel drawingPanel;
	DrawActionListener(JTextField text1, JTextField text2, JTextField text3, DrawingPanel drawingPanel)
	{
		this.text1=text1;
		this.text2=text2;
		this.text3=text3;
		this.drawingPanel = drawingPanel;
	}
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			int korean = Integer.parseInt(text1.getText());
			int english = Integer.parseInt(text2.getText());
			int math = Integer.parseInt(text3.getText());
			drawingPanel.setScores(korean, english, math);
			drawingPanel.repaint();
		}
		catch (NumberFormatException nfe){
			JOptionPane.showMessageDialog(drawingPanel,"�߸��� ���� �Է��Դϴ�","�����޽���",JOptionPane.ERROR_MESSAGE);
		}
	}
}