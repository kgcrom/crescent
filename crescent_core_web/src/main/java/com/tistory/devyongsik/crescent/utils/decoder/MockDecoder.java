package com.tistory.devyongsik.crescent.utils.decoder;


public class MockDecoder implements Decoder {

	@Override
	public String decodeTerm(Object value) {
		return (String)value;
	}

	@Override
	public String decodeStored(Object value) {
		return value.toString();
	}

}
