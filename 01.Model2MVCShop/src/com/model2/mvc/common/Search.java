package com.model2.mvc.common;


public class Search {
	
	private int page;					// ���� ������
	private String searchCondition;		// �˻���
	private String searchKeyword;		// �˻� ����
	private int pageSize;				// �� �������� ���̴� �Խù� ��
	private String sort;				// ���� ( default : �⺻ / asc : �������� / dsc : �������� )
	
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