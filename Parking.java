package parking;

public class Parking {
	private int num;
	private String cnum;
	private String pnum;
	private String entime;
	private String extime;
	private int price;
	private int pos;
	private int fee[];
	private String type;

	private String memo;
	private int stat[];
	
	public Parking() {}
		
	public Parking(int num, String cnum, String pnum, String entime, String extime, int price, int[] stat, int[] fee, String memo, int pos, String type) {
		this.num = num;
		this.cnum = cnum;
		this.pnum = pnum;
		this.entime = entime;
		this.extime = extime;
		this.price = price;
		this.stat = stat;
		this.fee = fee;
		this.memo = memo;
		this.pos = pos;
		this.type = type;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getCnum() {
		return cnum;
	}
	public void setCnum(String cnum) {
		this.cnum = cnum;
	}
	public String getPnum() {
		return pnum;
	}
	public void setPnum(String pnum) {
		this.pnum = pnum;
	}
	public String getEntime() {
		return entime;
	}
	public void setEntime(String entime) {
		this.entime = entime;
	}
	public String getExtime() {
		return extime;
	}
	public void setExtime(String extime) {
		this.extime = extime;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int[] getStat() {
		return stat;
	}
	public void setFee(int[] fee) {
		this.fee = fee;
	}
	public int[] getFee() {
		return fee;
	}
	public void setStat(int[] stat) {
		this.stat = stat;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}
