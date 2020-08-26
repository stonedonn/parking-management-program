package parking;

public class MainStart {
	PEnter pEnter;
	public static void main(String[] args) {
		// 메인클래스 실행
		MainStart main = new MainStart();
		main.pEnter = new PEnter(); // Enter창 보이기
		//main.pEnter.setMain(main); // Enter창에게 메인 클래스보내기
	}
}
