package com.model2.mvc.view.purchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddPurchaseViewAction extends Action{

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getParameter("prodNo") != null) {
			int prodNo = Integer.parseInt(request.getParameter("prodNo"));
			System.out.println("AddPurchaseViewAction prodNo : "+prodNo);
			
			int tranCnt = Integer.parseInt(request.getParameter("tranCnt"));
			
			ProductService service = new ProductServiceImpl();
			Product product = service.getProduct(prodNo);
			
			Cart cart = new Cart();
			cart.setCartProd(product);
			cart.setCartCnt(tranCnt);
			
			List<Cart> list = new ArrayList<Cart>();
			list.add(cart);

			request.setAttribute("list", list);
			
		}else {
			System.out.println(request.getParameter("cartList"));

			CartService service = new CartServiceImpl();
			Map<String, Object> map = service.getCartList2(request.getParameter("cartList"));

			request.setAttribute("list", map.get("list"));
		}
		
		return "forward:/purchase/addPurchaseView.jsp";
	}
}
