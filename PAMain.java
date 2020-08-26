package parking;

import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import com.google.gson.*;
import static java.util.logging.Level.*;

public class PAMain implements Runnable{
	private final PAMainUI v;
	private PEnter pEnter;
	private PInfo pInfo;
	private int[] stat = new int[10];
	
	// ���� ������ ���� ���� ����
    private String ip = "127.0.0.1";
    private Socket socket;
    private BufferedReader inMsg = null;
    private PrintWriter outMsg = null;

    // �޽��� �Ľ��� ���� ��ü ����
    Gson gson = new Gson();
    //Message m;
    Parking pk;
    
    Logger logger;
    
    boolean status;
    Thread thread;

	public PAMain(PAMainUI v){
		 // �ΰ� ��ü �ʱ�ȭ
        logger = Logger.getLogger(this.getClass().getName());

        // �𵨰� �� Ŭ���� ����
        //this.chatData = chatData;
        this.v = v;
	}
	
	public void refresh() {
		outMsg.println(gson.toJson(new Parking(0,"", "", "", "", 0, stat, null, "", 0, "refresh")));
	}
	
	public void appMain() {
		connectServer();
		refresh();	// �ڸ� ���� ���ΰ�ħ

		outMsg.println(gson.toJson(new Parking(0, "admin", "", "", "", 0, null, null, "", 0, "pricesum")));
		
		v.addButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = e.getSource();
                if (obj == v.back) {
                	outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "", 0, "logout")));
                	outMsg.close();
                    try {
                        inMsg.close();
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    refresh();	// �ڸ� ���� ���ΰ�ħ
                    status = false;
            		v.dispose();
            		pEnter = new PEnter();
                }
                else if(obj == v.ok_30) {
					int[] fee = new int[2];
                	fee[0] = Integer.parseInt(v.textField_30.getText());
                	outMsg.println(gson.toJson(new Parking(0,"", "", "", "", 0, null, fee, "30", 0, "changefee")));
                	refresh();	// �ڸ� ���� ���ΰ�ħ
                }
                else if(obj == v.ok_10) {
					int[] fee = new int[2];
                	fee[1] = Integer.parseInt(v.textField_10.getText());
                	outMsg.println(gson.toJson(new Parking(0,"", "", "", "", 0, null, fee, "10", 0, "changefee")));
                	refresh();	// �ڸ� ���� ���ΰ�ħ
                }
                else if(obj == v.info) {
            		pInfo = new PInfo();
                	//select hour(entime) A from parking where num = 32;
                	//select distinct count(*) a, hour(entime) b from parking group by b;
                }
                
                for(int i=0; i<10; i++) {
                	if (obj == v.btn[i]) {
                		outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "", i, "information")));
                	}
                }
                
            }
		});
	}

    public void connectServer() {
        try {
            // ���� ����
            socket = new Socket(ip, 8888);
            logger.log(INFO, "[Client]Server ���� ����!!");

            // ����� ��Ʈ�� ����
            inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outMsg = new PrintWriter(socket.getOutputStream(),true);

            // �޽��� ������ ���� ������ ����
            thread = new Thread(this);
            thread.start();
        }
        catch(Exception e) {
            logger.log(WARNING, "[MParkingUI]connectServer() Exception �߻�!!");
            e.printStackTrace();
        }
    }
    
	public static void main(String[] args) {
		
	}
	
	@Override
	public void run() {
		status=true;
		String msg;
		
        while(status) {
            try{
                // ������ ���� �κ�
                msg = inMsg.readLine();
                pk = gson.fromJson(msg, Parking.class);
                if(pk.getType().equals("refresh")) {
                	v.feeLabel_30.setText("�⺻ 30�� : "+ pk.getFee()[0] + "��");
                	v.feeLabel_10.setText("�� 10�д� : "+ pk.getFee()[1] + "�� �߰�");
                	stat = pk.getStat();
                    for(int i=0; i<10; i++) {
            			if(stat[i]==1) {
            				v.btn[i].setBackground(new Color(255,200,200));
            			}
            			else {
            				v.btn[i].setBackground(new Color(200,255,200));
            			}
            		}
                }
                else if(pk.getType().equals("PriceSum")) {
                	v.pricesum.setText("�� �����: " + pk.getMemo());
                }
                else if(pk.getType().equals("Information")) {
                	JOptionPane.showMessageDialog(null,  "������ȣ: " + pk.getCnum() + "\n��ȭ��ȣ: " + pk.getPnum() + "\n�����ð�: " + pk.getEntime(), (pk.getPos()+1) +"���ڸ�", 1);
                }
                /////
            }
            catch(IOException e) {
                logger.log(WARNING, "[MParkingUI]�޽��� ��Ʈ�� ����!!");
            }
        }
        logger.info("[MParkingUI]" + thread.getName()+ " �޽��� ���� ������ �����!!");  
	}
    
}