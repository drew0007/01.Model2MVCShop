package com.model2.mvc.common;


public class Search {
	
	private int page;					// 현재 페이지
	private String searchCondition;		// 검색어
	private String searchKeyword;		// 검색 조건
	private int pageSize;				// 한 페이지당 보이는 게시물 수
	private String sort;				// 정렬 ( default : 기본 / asc : 오름차순 / dsc : 내림차순 )
	
	public Search(){
	}
	
	public int getpageSize() {
		return pageSize;
	}
	public void setpageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}

	public String getSearchCondition() {
		return searchCondition;
	}
	public void setSearchCondition(String searchCondition) {
		this.searchCondition = searchCondition;
	}
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}