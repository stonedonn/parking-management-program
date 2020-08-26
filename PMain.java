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
	
	// 소켓 연결을 위한 변수 선언
    private String ip = "127.0.0.1";
    private Socket socket;
    private BufferedReader inMsg = null;
    private PrintWriter outMsg = null;

    // 메시지 파싱을 위한 객체 생성
    Gson gson = new Gson();
    //Message m;
    Parking pk;
    
    Logger logger;
    
    boolean status;
    Thread thread;

	public PMain(PMainUI v){
		 // 로거 객체 초기화
        logger = Logger.getLogger(this.getClass().getName());

        // 모델과 뷰 클래스 참조
        //this.chatData = chatData;
        this.v = v;
	}
	
	public void refresh() {
		outMsg.println(gson.toJson(new Parking(0,"", "", "", "", 0, stat, null, "", 0, "refresh")));
	}
	
	public void appMain() {
		connectServer();
		refresh();	// 자리 정보 새로고침
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
                    refresh();	// 자리 정보 새로고침
                    status = false;
            		v.dispose();
            		pEnter = new PEnter();
                }
                for(int i=0; i<10; i++) {
                	if (obj == v.btn[i]) {
                		int result = JOptionPane.showConfirmDialog(null, "주차 하시겠습니까?", v.btn[i].getText()+"번 자리", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
            // 소켓 생성
            socket = new Socket(ip, 8888);
            logger.log(INFO, "[Client]Server 연결 성공!!");

            // 입출력 스트림 생성
            inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outMsg = new PrintWriter(socket.getOutputStream(),true);

            // 메시지 수신을 위한 스레드 생성
            thread = new Thread(this);
            thread.start();
        }
        catch(Exception e) {
            logger.log(WARNING, "[MParkingUI]connectServer() Exception 발생!!");
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
                // 수신을 위한 부분
                msg = inMsg.readLine();
                pk = gson.fromJson(msg, Parking.class);
                if(pk.getType().equals("refresh")) {
                	v.feeLabel_30.setText("기본 30분 : "+ pk.getFee()[0] + "원");
                	v.feeLabel_10.setText("매 10분당 : "+ pk.getFee()[1] + "원 추가");
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
                	if(pk.getMemo().equals("주차성공")) {
                        refresh();	// 자리 정보 새로고침
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
                	JOptionPane.showMessageDialog(null, "[" + pk.getCnum() + "]\n" + ptime/60 + "시간 " + ptime%60 + "분 사용하셨습니다." + "\n내야 할 요금은 " + price + "원 입니다.");
                    refresh();	// 자리 정보 새로고침
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
                logger.log(WARNING, "[MParkingUI]메시지 스트림 종료!!");
            }
        }
        logger.info("[MParkingUI]" + thread.getName()+ " 메시지 수신 스레드 종료됨!!");  
	}
    
}