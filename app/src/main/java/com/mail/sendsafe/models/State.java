package com.mail.sendsafe.models;

public class State {
	private String stateid;
	private String statename;
	private String loctype;
	private String p_id;
	private String is_visible;

	public String getStateid() {
		return stateid;
	}

	public void setStateid(String stateid) {
		this.stateid = stateid;
	}
	public String getStatename() {
		return statename;
	}

	public void setStatename(String statename) {
		this.statename = statename;
	}

	public String getLoctype() {
		return loctype;
	}

	public void setLoctype(String loctype) {
		this.loctype = loctype;
	}

	public String getP_id() {
		return p_id;
	}

	public void setP_id(String p_id) {
		this.p_id = p_id;
	}

	public String getIs_visible() {
		return is_visible;
	}

	public void setIs_visible(String is_visible) {
		this.is_visible = is_visible;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return statename;
	}

}
