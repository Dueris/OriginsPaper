package me.dueris.genesismc.utils.exception;

import org.json.simple.parser.ParseException;

public class OriginParseException extends ParseException {
    public OriginParseException(int errorType) {
        super(errorType);
    }

    public OriginParseException(int errorType, Object unexpectedObject) {
        super(errorType, unexpectedObject);
    }

    public OriginParseException(int position, int errorType, Object unexpectedObject) {
        super(position, errorType, unexpectedObject);
    }
}
