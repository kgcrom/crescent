package org.crescent.admin.entity;

public class MorphResult {
	private String word;
	private String type;
	private int startOffset;
	private int endOffset;

	public String getWord() {
		return word;
	}

	public String getType() {
		return type;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}
}
