package com.audiopathanalyzer.connection;

import androidx.annotation.NonNull;

import java.io.OutputStream;

public class OutputStreamEventHandler extends OutputStream {

	private StringBuilder buffer = new StringBuilder();

	private LineReceiver lineReceiver;

	public OutputStreamEventHandler(@NonNull LineReceiver lineReceiver) {
		this.lineReceiver = lineReceiver;
	}

	@Override public void write(int i) {
		buffer.append((char) i);
		if (i == '\n')
			event();
	}

	@Override public void write(@NonNull byte[] b, int off, int len) {
		int start = off;
		int finallen = off + len;
		for (int i = off; i < finallen; i++) {
			if (b[i] == '\n') {
				buffer.append(new String(b, start, i - start + 1));
				event();
				start = i + 1;
			}
		}

		if (start < finallen) {
			buffer.append(new String(b, start, finallen - start));
		}
	}

	private void event() {
		lineReceiver.lineReceived(buffer.toString().replaceAll("([\n\r])", ""));
		buffer.setLength(0);
	}

	public interface LineReceiver {
		void lineReceived(String line);
	}

}