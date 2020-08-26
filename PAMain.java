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

	public PAMain(PAMainUI v){
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
                    refresh();	// 자리 정보 새로고침
                    status = false;
            		v.dispose();
            		pEnter = new PEnter();
                }
                else if(obj == v.ok_30) {
					int[] fee = new int[2];
                	fee[0] = Integer.parseInt(v.textField_30.getText());
                	outMsg.println(gson.toJson(new Parking(0,"", "", "", "", 0, null, fee, "30", 0, "changefee")));
                	refresh();	// 자리 정보 새로고침
                }
                else if(obj == v.ok_10) {
					int[] fee = new int[2];
                	fee[1] = Integer.parseInt(v.textField_10.getText());
                	outMsg.println(gson.toJson(new Parking(0,"", "", "", "", 0, null, fee, "10", 0, "changefee")));
                	refresh();	// 자리 정보 새로고침
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
            			}
            			else {
            				v.btn[i].setBackground(new Color(200,255,200));
            			}
            		}
                }
                else if(pk.getType().equals("PriceSum")) {
                	v.pricesum.setText("총 매출액: " + pk.getMemo());
                }
                else if(pk.getType().equals("Information")) {
                	JOptionPane.showMessageDialog(null,  "차량번호: " + pk.getCnum() + "\n전화번호: " + pk.getPnum() + "\n주차시간: " + pk.getEntime(), (pk.getPos()+1) +"번자리", 1);
                }
                /////
            }
            catch(IOException e) {
                logger.log(WARNING, "[MParkingUI]메시지 스트림 종료!!");
            }
        }
        logger.info("[MParkingUI]" + thread.getName()+ " 메시지 수신 스레드 종료됨!!");  
	}
    
}