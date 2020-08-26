package parking;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class ParkingDAO {

	String jdbcDriver = "com.mysql.jdbc.Driver";
	String jdbcUrl = "jdbc:mysql://localhost/abc?useSSL=false";
	Connection conn;
	
	PreparedStatement pstmt;
	ResultSet rs;

	// 콤보박스 아이템 관리를 위한 벡터
	String sql;
	
	 public void connectDB() {
    	try {
    		Class.forName(jdbcDriver); // JDBC 드라이버 로드
    		
    		conn = DriverManager.getConnection(jdbcUrl, "root", "0000"); // DB연결
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    public void closeDB() {
    	try {
    		pstmt.close();
    		rs.close();
    		conn.close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    public boolean newParking(String cnum, String pnum) {
		connectDB();
		sql = "insert into parking (cnum, pnum) values(?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cnum);
			pstmt.setString(2, pnum);
    		pstmt.executeUpdate();	// SQL문 전송
    		return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
    public boolean exitParking(int num) {
    	connectDB();
		sql = "update parking set extime = now() where num = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
    		pstmt.executeUpdate();	// SQL문 전송
    		return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    public int[] refreshStat() {
    	int[] stat = new int[10];
    	int i=0;
    	connectDB();
		sql = "select * from pos";
		try {
			pstmt = conn.prepareStatement(sql);
    		rs = pstmt.executeQuery();
			while(rs.next()) {
				stat[i++]=rs.getInt("status");
			}
    		return stat;
    	} catch (Exception e) {
			e.printStackTrace();
			return stat;
		}
    }
    public void changeFee(int fee[]) {
    	connectDB();
		sql = "update fee set fee_30m = ?, fee_10m = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fee[0]);
			pstmt.setInt(2, fee[1]);
    		pstmt.executeUpdate();	// SQL문 전송
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public int[] returnFee() {
    	int[] fee = new int[2];
    	connectDB();
		sql = "select * from fee";
		try {
			pstmt = conn.prepareStatement(sql);
    		rs = pstmt.executeQuery();
			rs.next();
			fee[0] = rs.getInt("fee_30m");
			fee[1] = rs.getInt("fee_10m");
    		return fee;
    	} catch (Exception e) {
			e.printStackTrace();
			return fee;
		}
    }
    public int returnNum(String cnum) {
    	int num = 0;
    	connectDB();
		sql = "select * from parking where cnum = ? and price is NULL";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cnum);
    		rs = pstmt.executeQuery();
			rs.next();
			num = rs.getInt("num");
			
    		return num;
    	} catch (Exception e) {
			e.printStackTrace();
			return num;
		}
    }
    public int returnPrice(int num) {
    	int ans = 0;
    	connectDB();
		sql = "select TIMESTAMPDIFF(minute, entime, extime) A from parking where num = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
    		rs = pstmt.executeQuery();
			rs.next();
			ans = rs.getInt("A");
    		return ans;
    	} catch (Exception e) {
			e.printStackTrace();
			return ans;
		}
    }
    public boolean updatePrice(int num, int price) {
    	connectDB();
		sql = "update parking set price = ? where num = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, price);
			pstmt.setInt(2, num);
    		pstmt.executeUpdate();	// SQL문 전송
    		return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    public boolean updateEnPosition(int sta, int pos, int num) {
    	connectDB();
		sql = "update pos set status = ?, num = ? where posnum = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sta);
			pstmt.setInt(2, num);
			pstmt.setInt(3, pos+1);
    		pstmt.executeUpdate();	// SQL문 전송
    		return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
    }
    public boolean updateExPosition(int sta, int num) {
    	connectDB();
		sql = "update pos set status = ?, num = ? where num = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sta);
			pstmt.setInt(2, 0);
			pstmt.setInt(3, num);
    		pstmt.executeUpdate();	// SQL문 전송
    		return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
    }
    public Parking getData(int pos){
    	connectDB();
		sql = "select * from parking where num in(select num from pos where posnum = ?)";
		
		Parking datas = new Parking();
		try {
    		pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, pos+1);
    		rs = pstmt.executeQuery();
			rs.next();
			datas.setNum(rs.getInt("num"));
			datas.setCnum(rs.getString("cnum"));
			datas.setPnum(rs.getString("pnum"));
			datas.setEntime(rs.getString("entime"));
			datas.setExtime(rs.getString("extime"));
			datas.setPrice(rs.getInt("price"));
			datas.setPos(pos);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return datas;
    }
    public int priceSum(){
    	int ans = 0;
    	connectDB();
		sql = "select sum(price) sum from parking";
		
		Parking datas = new Parking();
		try {
    		pstmt = conn.prepareStatement(sql);
    		rs = pstmt.executeQuery();
			rs.next();
			ans = rs.getInt("sum");
			return ans;
		} catch (SQLException e) {
			e.printStackTrace();
			return ans;
		}
    }
    
}
