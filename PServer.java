package parking;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;
import java.util.logging.*;

public class PServer {
	private ParkingDAO dao;

	// 서버 소켓 및 클라이언트 연결 소켓 
	private ServerSocket  ss = null;
	private Socket s = null;
	
	// 연결된 클라이언트 스레드를 관리하기 위한 ArrayList
	ArrayList<ParkingThread> parkingThreads = new ArrayList<ParkingThread>();

    Logger logger;
		
	// 멀티챗 메인 프로그램 부
	public void start() {
		logger = Logger.getLogger(this.getClass().getName());
		try {
			// 서버 소켓 생성
			ss = new ServerSocket(8888);
			logger.info("MParkingServer start");
			
			// 무한 루프를 돌면서 클라이언트 연결을 기다림
			while(true) {
				s = ss.accept();				
				// 연결된 클라이언트에 대해 쓰레드 클래스 생성
				ParkingThread parking = new ParkingThread();
				// 클라이언트 리스트 추가
				parkingThreads.add(parking);
				// 스레드 시작
				parking.start();
			}
		} catch (Exception e) {
			logger.info("[MParkingServer]start() Exception 발생!!");
            e.printStackTrace();
		}
	}

	// 각각의 클라이언트 관리를 위한 쓰레드 클래스
		class ParkingThread extends Thread {
			// 수신 메시지 및 파싱 메시지 처리를 위한 변수 선언
			String msg;

	        // 메시지 객체 생성
			Parking pk = new Parking();

	        // Json Parser 초기화
			Gson gson = new Gson();

			// 입출력 스트림
			private BufferedReader inMsg = null;
			private PrintWriter outMsg = null;

			public void run() {
				dao = new ParkingDAO();
			
				boolean status = true;
				logger.info("ParkingThread start...");

				try {
					// 입출력 스트림 생성
					inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
					outMsg = new PrintWriter(s.getOutputStream(),true);
					
					// 상태정보가 true 이면 루프를 돌면서 사용자로 부터 수신된 메시지 처리
					while(status) {
						// 수신된 메시지를 msg 변수에 저장
						msg = inMsg.readLine();
						
						// JSON 메시지를 Parking 객체로 매핑
						pk = gson.fromJson(msg, Parking.class);
													
						// 파싱된 문자열 배열의 두번째 요소 값에 따라 처리
						// 로그아웃 메시지 인 경우
						if(pk.getType().equals("logout")) {
							parkingThreads.remove(this);
							status = false;
						}
						// registration 메시지 인 경우
						else if(pk.getType().equals("registration")) {
							System.out.println(pk.getCnum());
							if(dao.newParking(pk.getCnum(), pk.getPnum())) {	// 새로 자동차 번호와 폰번호를 새로 넣음
								int num = dao.returnNum(pk.getCnum());	// 새로 넣은 자동차 번호의 num을 구해옴
								System.out.println("="+num);
								dao.updateEnPosition(1, pk.getPos(), num);	// 해당 자리에 num번호와 사용중1으로 변경
								for(ParkingThread ct : parkingThreads) {	// 해당 스레드에 주차 성공 메시지를 보냄
									if(ct.pk.getCnum().equals(pk.getCnum())) {
										ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "주차성공", 0, "registration")));
									}
								}
							}
							else {
								for(ParkingThread ct : parkingThreads) {
									if(ct.pk.getCnum().equals(pk.getCnum())) {
										ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "주차실패", 0, "registration")));
									}
								}
							}
						}
						else if(pk.getType().equals("refresh")) {
							int[] stat = new int[10];
							int[] fee = new int[2];
							fee = dao.returnFee();
							stat = dao.refreshStat();
							for(ParkingThread ct : parkingThreads) {
								ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, stat, fee, "", 0, "refresh")));
							}
						}
						else if(pk.getType().equals("exitparking")) {
							int[] fee = new int[2];
							int num = dao.returnNum(pk.getCnum());	// 출차하기 위한 자동차 번호의 num을 구해옴
							if(dao.exitParking(num)) { // 출차시간을 업데이트
								if(dao.updateExPosition(0, num)) {	// 자리를 반납
									int price = dao.returnPrice(num);	// 주차시간(분)을 가져옴
									for(ParkingThread ct : parkingThreads) {
										if(ct.pk.getCnum().equals(pk.getCnum())) {	// 해당 스레드에 보냄
											fee = dao.returnFee();	// 요금 정보 가져옴
											ct.outMsg.println(gson.toJson(new Parking(num, pk.getCnum(), "", "", "", price, null, fee, "주차시간", 0, "Parking_Minute")));
										}
									}
								}
							}
						}
						else if(pk.getType().equals("updateexprice")) {
							if(dao.updatePrice(pk.getNum(), pk.getPrice())) {
								parkingThreads.remove(this);
								status = false;
							}
						}
						else if(pk.getType().equals("changefee")) {
							int[] fee = new int[2];
							if(pk.getMemo().equals("30")) {
								fee[0] = pk.getFee()[0];
								fee[1] = dao.returnFee()[1];
							}
							else {
								fee[0] = dao.returnFee()[0];
								fee[1] = pk.getFee()[1];
							}
							dao.changeFee(fee);
						}
						else if(pk.getType().equals("information")) {
							Parking datas = new Parking();
							datas = dao.getData(pk.getPos());
							for(ParkingThread ct : parkingThreads) {
								if(ct.pk.getPos() == pk.getPos()) {	// 해당 스레드에 보냄
									ct.outMsg.println(gson.toJson(new Parking(datas.getNum(), datas.getCnum(), datas.getPnum(),
											datas.getEntime(), datas.getExtime(), datas.getPrice(), null, null, "데이터", datas.getPos(), "Information")));
								}
							}
						}
						else if(pk.getType().equals("pricesum")) {
							int sum = dao.priceSum();
							for(ParkingThread ct : parkingThreads) {
								if(ct.pk.getCnum() == pk.getCnum()) {	// 해당 스레드에 보냄
									ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, Integer.toString(sum), 0, "PriceSum")));
								}
							}
						}
						// 그밖의 경우 즉 일반 메시지인 경우
						else {
							//
						}
					}
					// 루프를 벗어나면 클라이언트 연결 종료 이므로 스레드 인터럽트
					this.interrupt();
					logger.info(this.getName() + " 종료됨!!");
				} catch (IOException e) {
					parkingThreads.remove(this);
					logger.info("[ParkingThread]run() IOException 발생!!");
	                e.printStackTrace();
				}
			}
		}

	    public static void main(String[] args){
	        PServer server = new PServer();
	        server.start();
	    }
}
