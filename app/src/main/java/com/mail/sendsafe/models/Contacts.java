package com.mail.sendsafe.models;

public class Contacts {
	
	String fname,laname,emailid,abid;

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	@Override
	public String toString() {
		return "Contacts [fname=" + fname + ", emailid=" + emailid + "]";
	}

	public String getLaname() {
		return laname;
	}

	public void setLaname(String laname) {
		this.laname = laname;
	}

	public String getEmailid() {
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getAbid() {
		return abid;
	}

	public void setAbid(String abid) {
		this.abid = abid;
	}

}
