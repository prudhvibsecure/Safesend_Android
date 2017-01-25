package com.mail.sendsafe.models;

public class Folder implements Comparable<Folder> {

	String folderName, fid;

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	@Override
	public String toString() {
		return "Folder [folderName=" + folderName + "]";
	}

	@Override
	public int compareTo(Folder another) {
		return this.getFolderName().compareTo(another.getFolderName());
	}

}
