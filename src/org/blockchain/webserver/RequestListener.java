package org.blockchain.webserver;

/**
 * @author Yulian Yordanov
 * Created: Jun 10, 2018
 */
public interface RequestListener {
	public void onRequest(HttpRequest httpRequest);
}
