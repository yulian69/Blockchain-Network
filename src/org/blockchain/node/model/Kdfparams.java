package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 19, 2018
 */
public class Kdfparams {
	private int dklen;
	private int n;
	private int p;
	private int r;
	private String salt;
	
	public int getDklen() {
		return dklen;
	}
	public void setDklen(int dklen) {
		this.dklen = dklen;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public int getP() {
		return p;
	}
	public void setP(int p) {
		this.p = p;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
}
