package com.esquel.epass.item;

/**
 * 
 * @author hung
 * 
 */
public class ItemBookMark {
	String text;
	int img;
	private long id;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public ItemBookMark(String text, int img) {
		this.text = text;
		this.img = img;
	}

	public ItemBookMark() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
