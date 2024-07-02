package com.sist.dao;

import java.util.*;
import java.sql.*;

import com.sist.database.*;

public class FoodDAO {
	private Connection conn;
	private PreparedStatement ps;
	private static FoodDAO dao;
	private DataBaseConnection dbConn = new DataBaseConnection();

	// 라이브러리 형식 (.jar) => 보안
	
	// 싱글턴
	public static FoodDAO newInstance() {
		if (dao == null)
			dao = new FoodDAO();
		return dao;
	}
	
	// 기능
	// => 결과 값 (브라우저) => 사용자 요청
	// => 사용자가 페이지를 선택하면 오라클에 저장된 데이터 중에 페이지에 해당되는 데이터를 보낸다.
	// => List, FoodVO, int, String, void
	// 화면 목록 출력 => List
	// 상세보기
	public List<FoodVO> foodListData(int page) {
		List<FoodVO> list = new ArrayList<FoodVO>();
		try {
			dbConn.getConnection();
			String sql = "SELECT fno, poster, name, num "
						+ "FROM (SELECT fno, poster, name, rownum as num "
						+ "FROM (SELECT /*+ INDEX_ASC(food_house fh_fno_pk)*/ fno, poster, name "
						+ "FROM food_house)) "
						+ "WHERE num BETWEEN ? AND ?";
			ps = conn.prepareStatement(sql);
			int rowSize = 12;
			int start = (rowSize * page) - (rowSize - 1);
			int end = rowSize * page;
			ps.setInt(1, start);
			ps.setInt(2, end);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				FoodVO vo = new FoodVO();
				vo.setFno(rs.getInt(1));
				vo.setPoster(rs.getString(2));
				vo.setName(rs.getString(3));
				list.add(vo);
			}
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbConn.disConnection(conn, ps);
		}
		return list;
	}
	
	public int foodTotalPage() {
		int total = 0;
		try {
			dbConn.getConnection();
			String sql = "SELECT CEIL(COUNT(*)/12.0) FROM food_house";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.next();
			total = rs.getInt(1);
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbConn.disConnection(conn, ps);
		}
		return total;
	}
}
