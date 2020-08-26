package parking;

import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import com.google.gson.*;
import static java.util.logging.Level.*;

public class PMain implements Runnable{
	private final PMainUI v;
	private PEnter pEnter;
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

	public PMain(PMainUI v){
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
		if(v.flagN==1) {
    		outMsg.println(gson.toJson(new Parking(0, v.carN, v.phoneN, "", "", 0, null, null, "", 0, "exitparking")));
		}
		
		v.addButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = e.getSource();
                if (obj == v.back) {
                	outMsg.println(gson.toJson(new Parking(0, v.carN, v.phoneN, "", "", 0, null, null, "", 0, "logout")));
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
                for(int i=0; i<10; i++) {
                	if (obj == v.btn[i]) {
                		int result = JOptionPane.showConfirmDialog(null, "���� �Ͻðڽ��ϱ�?", v.btn[i].getText()+"�� �ڸ�", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                		if(result == 0) {
                			//System.exit(0);
                            outMsg.println(gson.toJson(new Parking(0, v.carN, v.phoneN, "", "", 0, null, null, "", i, "registration")));
                    		System.out.println(i+1);
                		}
                		else {
                			
                		}
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
		//new PMain(new PMainUI("1", "2"));
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
            				v.btn[i].setEnabled(false);
            			}
            			else {
            				v.btn[i].setBackground(new Color(200,255,200));
            				v.btn[i].setEnabled(true);
            			}
            		}
                }
                else if(pk.getType().equals("registration")) {
                	JOptionPane.showMessageDialog(null, pk.getMemo());
                	if(pk.getMemo().equals("��������")) {
                        refresh();	// �ڸ� ���� ���ΰ�ħ
                		outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "", 0, "logout")));
                		outMsg.close();
                        try {
                            inMsg.close();
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        status = false;
                		v.dispose();
                		pEnter = new PEnter();
                	}
                }
                else if(pk.getType().equals("Parking_Minute")) {
					int[] fee = new int[2];
                	int price = pk.getPrice();
                	int ptime;
                	ptime = price;
                	fee = pk.getFee();
                	if((price-30) <= 0) {
                		price = fee[0];
                	}
                	else {
                		price = ((price-30)/10)*fee[1] + fee[0];
                	}
                	outMsg.println(gson.toJson(new Parking(pk.getNum(), "", "", "", "", price, null, null, "", 0, "updateexprice")));
                	System.out.println(price);
                	JOptionPane.showMessageDialog(null, "[" + pk.getCnum() + "]\n" + ptime/60 + "�ð� " + ptime%60 + "�� ����ϼ̽��ϴ�." + "\n���� �� ����� " + price + "�� �Դϴ�.");
                    refresh();	// �ڸ� ���� ���ΰ�ħ
                	outMsg.println(gson.toJson(new Parking(0, v.carN, v.phoneN, "", "", 0, null, null, "", 0, "logout")));
                	outMsg.close();
                    try {
                        inMsg.close();
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    status = false;
            		v.dispose();
            		pEnter = new PEnter();
                }
                
                
            }
            catch(IOException e) {
                logger.log(WARNING, "[MParkingUI]�޽��� ��Ʈ�� ����!!");
            }
        }
        logger.info("[MParkingUI]" + thread.getName()+ " �޽��� ���� ������ �����!!");  
	}
    
}