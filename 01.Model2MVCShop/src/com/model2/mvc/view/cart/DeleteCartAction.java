package com.model2.mvc.view.cart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;

public class DeleteCartAction extends Action{

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		int cartNo = Integer.parseInt(request.getParameter("cartNo"));
		
		CartService service = new CartServiceImpl();
		service.deleteCart(cartNo);
		
		return "forward:/listCart.do";
	}
}
