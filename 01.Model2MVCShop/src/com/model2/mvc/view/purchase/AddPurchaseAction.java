package com.model2.mvc.view.purchase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class AddPurchaseAction  extends Action{
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		String[] prodNoList = request.getParameter("prodNoList").split(",");
		String[] tranCntList = request.getParameter("tranCntList").split(",");
		
		int tranNo = 0;

		List<Purchase> list = new ArrayList<Purchase>();
		for(int i=0; i<prodNoList.length; i++) {
			Product product = new Product();
			product.setProdNo(Integer.parseInt(prodNoList[i]));

			User user = (User)session.getAttribute("user");
			
			Purchase purchase = new Purchase();
			purchase.setBuyer((User)session.getAttribute("user"));
			purchase.setDivyAddr(request.getParameter("receiverAddr"));
			purchase.setDivyDate(request.getParameter("receiverDate").replaceAll("-", ""));
			purchase.setDivyRequest(request.getParameter("receiverRequest"));
			purchase.setPaymentOption(request.getParameter("paymentOption"));
			purchase.setPurchaseProd(product);
			purchase.setReceiverName(request.getParameter("receiverName"));
			purchase.setReceiverPhone(request.getParameter("receiverPhone"));
			purchase.setTranCnt(Integer.parseInt(tranCntList[i]));
			purchase.setTranCode("1");
			
			list.add(purchase);
		}
		
		PurchaseService service2 = new PurchaseServiceImpl();
		tranNo = service2.addPurchase(list);
		System.out.println("상품 구매 끝");
		
		Map<String, Object> map = service2.getPurchase(tranNo);
		
		request.setAttribute("list", map.get("list"));

		return "forward:/purchase/addPurchase.jsp";
	}
}