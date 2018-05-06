package com.model2.mvc.service.purchase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;

public class PurchaseDAO {

	public PurchaseDAO() {
		// TODO Auto-generated constructor stub
	}
	
	public int insertPurchase(List<Purchase> list) throws Exception{

		Connection con = null;
		int seqTranNo = 0;
		
		try {
			con = DBUtil.getConnection();
			con.setAutoCommit(false);
			
			String sql = "SELECT seq_transaction_tran_no.NEXTVAL from dual";
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				seqTranNo = rs.getInt(1);
			}
			
			
			for(int i=0; i<list.size(); i++) {
				Purchase purchase = list.get(i);
				
				sql = "INSERT INTO transaction(tran_no, prod_no, buyer_id, payment_option, receiver_name, receiver_phone, "
						+ "demailaddr, dlvy_request, tran_status_code, order_data, dlvy_date, tran_cnt) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?, ?)";
				PreparedStatement stmt1 = con.prepareStatement(sql);
				stmt1.setInt(1, seqTranNo);
				stmt1.setInt(2, purchase.getPurchaseProd().getProdNo());
				stmt1.setString(3, purchase.getBuyer().getUserId());
				stmt1.setString(4, purchase.getPaymentOption());
				stmt1.setString(5, purchase.getReceiverName());
				stmt1.setString(6, purchase.getReceiverPhone());
				stmt1.setString(7, purchase.getDivyAddr());
				stmt1.setString(8, purchase.getDivyRequest());
				stmt1.setString(9, "1");	// 배송 상태 코드 ( 1 : 구매신청 / 2 : 배송중 / 3 : 배송완료 )
				stmt1.setString(10, purchase.getDivyDate());
				stmt1.setInt(11, purchase.getTranCnt());
				
				stmt1.executeUpdate();
				

				sql = "UPDATE product SET prod_cnt = prod_cnt - ? WHERE prod_no = ?";
				PreparedStatement stmt2 = con.prepareStatement(sql);
				stmt2.setInt(1, purchase.getTranCnt());
				stmt2.setInt(2, purchase.getPurchaseProd().getProdNo());
				
				stmt2.executeUpdate();
				
				
				sql = "DELETE FROM cart WHERE user_id = ? AND prod_no = ?";
				PreparedStatement stmt3 = con.prepareStatement(sql);
				stmt3.setString(1, purchase.getBuyer().getUserId());
				stmt3.setInt(2, purchase.getPurchaseProd().getProdNo());
				
				stmt3.executeUpdate();
			}
			
			con.commit();
			
		}catch(SQLException se) {
			con.rollback();
			se.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			con.close();	
		}
		
		return seqTranNo;
	}
	
	//구매이력조회
	public Map<String, Object> getPurchaseList(Search search, String buyerId) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT p.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date," + 
				" t.tran_no, t.buyer_id, t.payment_option, t.receiver_name, t.receiver_phone, t.demailaddr, t.dlvy_request," +
				" t.tran_status_code, t.order_data, t.dlvy_date, t.tran_cnt" + 
				"  FROM product p, transaction t" + 
				" WHERE p.prod_no = t.prod_no" + 
				"   AND t.buyer_id =  '"+buyerId+"'";

		sql += " ORDER BY t.tran_no DESC";

		System.out.println("PurchaseDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("PurchaseDAO :: totalCount :: "+totalCount);
		
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();

		System.out.println(search);		
		
		ArrayList<Purchase> list = new ArrayList<Purchase>();

		while(rs.next()) {
			Product product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManuDate(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setFileName(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));
			
			Purchase purchase = new Purchase();
			purchase.setTranNo(rs.getInt("tran_no"));
			purchase.setPurchaseProd(product);
			purchase.setPaymentOption(rs.getString("payment_option").trim());
			purchase.setReceiverName(rs.getString("receiver_name"));
			purchase.setReceiverPhone(rs.getString("receiver_phone"));
			purchase.setDivyAddr(rs.getString("demailaddr"));
			purchase.setDivyRequest(rs.getString("dlvy_request"));
			purchase.setTranCode(rs.getString("tran_status_code").trim());
			purchase.setOrderDate(rs.getDate("order_data"));
			purchase.setDivyDate(rs.getString("dlvy_date"));
			purchase.setTranCnt(rs.getInt("tran_cnt"));
			
			list.add(purchase);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();
		
		return map;
	}

	public void updateTranCode(Purchase purchase) throws Exception{
		
		Connection con = DBUtil.getConnection();
		String sql = "UPDATE transaction" + 
				"   SET tran_status_code = ?" + 
				" WHERE tran_no = ?"
				+ " AND prod_no = ?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, purchase.getTranCode());
		stmt.setInt(2, purchase.getTranNo());
		stmt.setInt(3, purchase.getPurchaseProd().getProdNo());
		stmt.executeUpdate();
		
		if(purchase.getTranCode().equals("5")) {
			sql = "UPDATE product SET prod_cnt = prod_cnt+? WHERE prod_no = ?";
			
			PreparedStatement stmt2 = con.prepareStatement(sql);
			stmt2.setInt(1, purchase.getTranCnt());
			stmt2.setInt(2, purchase.getPurchaseProd().getProdNo());
			stmt2.executeUpdate();
		}
		
		con.close();
	}
	
	//구매 상세 조회
	public Map<String, Object> findPurchase(int tranNo) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT p.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date," + 
				" t.tran_no, t.buyer_id, t.payment_option, t.receiver_name, t.receiver_phone, t.demailaddr, t.dlvy_request," +
				" t.tran_status_code, t.order_data, t.dlvy_date, t.tran_cnt" + 
				"  FROM product p, transaction t" + 
				" WHERE p.prod_no = t.prod_no" + 
				"   AND t.tran_no = "+tranNo;
		
		System.out.println("PurchaseDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("PurchaseDAO :: totalCount :: "+totalCount);
		
		PreparedStatement stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		
		Purchase purchase = null;
		Product product = null;
		User user = null;

		ArrayList<Purchase> list = new ArrayList<Purchase>();
		while(rs.next()) {
			product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManuDate(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setFileName(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));

			user = new User();
			user.setUserId(rs.getString("buyer_id"));
			
			purchase = new Purchase();
			purchase.setTranNo(rs.getInt("tran_no"));
			purchase.setPurchaseProd(product);
			purchase.setBuyer(user);
			purchase.setPaymentOption(rs.getString("payment_option").trim());
			purchase.setReceiverName(rs.getString("receiver_name"));
			purchase.setReceiverPhone(rs.getString("receiver_phone"));
			purchase.setDivyAddr(rs.getString("demailaddr"));
			purchase.setDivyRequest(rs.getString("dlvy_request"));
			purchase.setTranCode(rs.getString("tran_status_code").trim());
			purchase.setOrderDate(rs.getDate("order_data"));
			purchase.setDivyDate(rs.getString("dlvy_date"));
			purchase.setTranCnt(rs.getInt("tran_cnt"));
			
			System.out.println(rs.getString("payment_option").trim());
			
			list.add(purchase);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		stmt.close();
		con.close();
		
		return map;
	}
	
	public Map<String, Object> getSaleList(Search search, int prodNo) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT prod_no, buyer_id, tran_no, payment_option, receiver_name, receiver_phone, demailaddr"
				+ ", dlvy_request, dlvy_date, order_data, tran_status_code, tran_cnt "
				+ "FROM transaction ";
		
		if(prodNo != 0) {
			sql += "WHERE prod_no = '"+prodNo+"'";
		}

		sql += " ORDER BY tran_no DESC";

		System.out.println("PurchaseDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("PurchaseDAO :: totalCount :: "+totalCount);
		
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();

		System.out.println(search);		
		
		ArrayList<Purchase> list = new ArrayList<Purchase>();

		Product product = null;
		Purchase purchase = null;
		User user = null;
		while(rs.next()) {
			product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			
			user = new User();
			user.setUserId(rs.getString("buyer_id"));
			
			purchase = new Purchase();
			purchase.setTranNo(rs.getInt("tran_no"));
			purchase.setPurchaseProd(product);
			purchase.setBuyer(user);
			purchase.setPaymentOption(rs.getString("payment_option").trim());
			purchase.setReceiverName(rs.getString("receiver_name"));
			purchase.setReceiverPhone(rs.getString("receiver_phone"));
			purchase.setDivyAddr(rs.getString("demailaddr"));
			purchase.setDivyRequest(rs.getString("dlvy_request"));
			purchase.setTranCode(rs.getString("tran_status_code").trim());
			purchase.setDivyDate(rs.getString("dlvy_date"));
			purchase.setOrderDate(rs.getDate("order_data"));
			purchase.setTranCnt(rs.getInt("tran_cnt"));
			
			list.add(purchase);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();
		
		return map;
	}
	
	public void updatePurchase(Purchase purchase) throws Exception{

		Connection con = DBUtil.getConnection();
		String sql = "UPDATE transaction" + 
				"   SET payment_option = ?" + 
				"      ,receiver_name = ?" + 
				"      ,receiver_phone = ?" + 
				"      ,demailaddr = ?" + 
				"      ,dlvy_request = ?" + 
				"      ,dlvy_date = ?" + 
				" WHERE tran_no = ?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, purchase.getPaymentOption());
		stmt.setString(2, purchase.getReceiverName());
		stmt.setString(3, purchase.getReceiverPhone());
		stmt.setString(4, purchase.getDivyAddr());
		stmt.setString(5, purchase.getDivyRequest());
		stmt.setString(6, purchase.getDivyDate());
		stmt.setInt(7, purchase.getTranNo());
		stmt.executeUpdate();
		
		con.close();
	}
	
	private int getTotalCount(String sql) throws Exception{
		
		sql = "SELECT COUNT(*) FROM (" + sql + ") countTable";
		
		Connection con = DBUtil.getConnection();
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		int totalCount = 0;
		if(rs.next()) {
			totalCount = rs.getInt(1);
		}
		
		pStmt.close();
		con.close();
		rs.close();
		
		return totalCount;
	}
	
	private String makeCurrentPageSql(String sql, Search search) {
		sql = "SELECT * "+
				"FROM (	SELECT inner_table.*, ROWNUM AS row_seq " +
							" FROM ( " +sql+ " ) inner_table " +
							" WHERE ROWNUM <= "+search.getPage()*search.getpageSize()+")"+
				"WHERE row_seq BETWEEN " + ((search.getPage()-1)*search.getpageSize()+1) + " AND " + search.getPage()*search.getpageSize();
		
		System.out.println("UserDAO :: make SQL :: " + sql);
		
		return sql;
	}
	
}
