package parking;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;
import java.util.logging.*;

public class PServer {
	private ParkingDAO dao;

	// ���� ���� �� Ŭ���̾�Ʈ ���� ���� 
	private ServerSocket  ss = null;
	private Socket s = null;
	
	// ����� Ŭ���̾�Ʈ �����带 �����ϱ� ���� ArrayList
	ArrayList<ParkingThread> parkingThreads = new ArrayList<ParkingThread>();

    Logger logger;
		
	// ��Ƽê ���� ���α׷� ��
	public void start() {
		logger = Logger.getLogger(this.getClass().getName());
		try {
			// ���� ���� ����
			ss = new ServerSocket(8888);
			logger.info("MParkingServer start");
			
			// ���� ������ ���鼭 Ŭ���̾�Ʈ ������ ��ٸ�
			while(true) {
				s = ss.accept();				
				// ����� Ŭ���̾�Ʈ�� ���� ������ Ŭ���� ����
				ParkingThread parking = new ParkingThread();
				// Ŭ���̾�Ʈ ����Ʈ �߰�
				parkingThreads.add(parking);
				// ������ ����
				parking.start();
			}
		} catch (Exception e) {
			logger.info("[MParkingServer]start() Exception �߻�!!");
            e.printStackTrace();
		}
	}

	// ������ Ŭ���̾�Ʈ ������ ���� ������ Ŭ����
		class ParkingThread extends Thread {
			// ���� �޽��� �� �Ľ� �޽��� ó���� ���� ���� ����
			String msg;

	        // �޽��� ��ü ����
			Parking pk = new Parking();

	        // Json Parser �ʱ�ȭ
			Gson gson = new Gson();

			// ����� ��Ʈ��
			private BufferedReader inMsg = null;
			private PrintWriter outMsg = null;

			public void run() {
				dao = new ParkingDAO();
			
				boolean status = true;
				logger.info("ParkingThread start...");

				try {
					// ����� ��Ʈ�� ����
					inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
					outMsg = new PrintWriter(s.getOutputStream(),true);
					
					// ���������� true �̸� ������ ���鼭 ����ڷ� ���� ���ŵ� �޽��� ó��
					while(status) {
						// ���ŵ� �޽����� msg ������ ����
						msg = inMsg.readLine();
						
						// JSON �޽����� Parking ��ü�� ����
						pk = gson.fromJson(msg, Parking.class);
													
						// �Ľ̵� ���ڿ� �迭�� �ι�° ��� ���� ���� ó��
						// �α׾ƿ� �޽��� �� ���
						if(pk.getType().equals("logout")) {
							parkingThreads.remove(this);
							status = false;
						}
						// registration �޽��� �� ���
						else if(pk.getType().equals("registration")) {
							System.out.println(pk.getCnum());
							if(dao.newParking(pk.getCnum(), pk.getPnum())) {	// ���� �ڵ��� ��ȣ�� ����ȣ�� ���� ����
								int num = dao.returnNum(pk.getCnum());	// ���� ���� �ڵ��� ��ȣ�� num�� ���ؿ�
								System.out.println("="+num);
								dao.updateEnPosition(1, pk.getPos(), num);	// �ش� �ڸ��� num��ȣ�� �����1���� ����
								for(ParkingThread ct : parkingThreads) {	// �ش� �����忡 ���� ���� �޽����� ����
									if(ct.pk.getCnum().equals(pk.getCnum())) {
										ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "��������", 0, "registration")));
									}
								}
							}
							else {
								for(ParkingThread ct : parkingThreads) {
									if(ct.pk.getCnum().equals(pk.getCnum())) {
										ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, "��������", 0, "registration")));
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
							int num = dao.returnNum(pk.getCnum());	// �����ϱ� ���� �ڵ��� ��ȣ�� num�� ���ؿ�
							if(dao.exitParking(num)) { // �����ð��� ������Ʈ
								if(dao.updateExPosition(0, num)) {	// �ڸ��� �ݳ�
									int price = dao.returnPrice(num);	// �����ð�(��)�� ������
									for(ParkingThread ct : parkingThreads) {
										if(ct.pk.getCnum().equals(pk.getCnum())) {	// �ش� �����忡 ����
											fee = dao.returnFee();	// ��� ���� ������
											ct.outMsg.println(gson.toJson(new Parking(num, pk.getCnum(), "", "", "", price, null, fee, "�����ð�", 0, "Parking_Minute")));
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
								if(ct.pk.getPos() == pk.getPos()) {	// �ش� �����忡 ����
									ct.outMsg.println(gson.toJson(new Parking(datas.getNum(), datas.getCnum(), datas.getPnum(),
											datas.getEntime(), datas.getExtime(), datas.getPrice(), null, null, "������", datas.getPos(), "Information")));
								}
							}
						}
						else if(pk.getType().equals("pricesum")) {
							int sum = dao.priceSum();
							for(ParkingThread ct : parkingThreads) {
								if(ct.pk.getCnum() == pk.getCnum()) {	// �ش� �����忡 ����
									ct.outMsg.println(gson.toJson(new Parking(0, "", "", "", "", 0, null, null, Integer.toString(sum), 0, "PriceSum")));
								}
							}
						}
						// �׹��� ��� �� �Ϲ� �޽����� ���
						else {
							//
						}
					}
					// ������ ����� Ŭ���̾�Ʈ ���� ���� �̹Ƿ� ������ ���ͷ�Ʈ
					this.interrupt();
					logger.info(this.getName() + " �����!!");
				} catch (IOException e) {
					parkingThreads.remove(this);
					logger.info("[ParkingThread]run() IOException �߻�!!");
	                e.printStackTrace();
				}
			}
		}

	    public static void main(String[] args){
	        PServer server = new PServer();
	        server.start();
	    }
}
