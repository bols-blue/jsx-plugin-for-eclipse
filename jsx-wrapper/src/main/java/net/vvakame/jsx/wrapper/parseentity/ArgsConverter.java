package net.vvakame.jsx.wrapper.parseentity;

import java.io.IOException;

import net.vvakame.util.jsonpullparser.JsonFormatException;
import net.vvakame.util.jsonpullparser.JsonPullParser;
import net.vvakame.util.jsonpullparser.JsonPullParser.State;
import net.vvakame.util.jsonpullparser.util.OnJsonObjectAddListener;
import net.vvakame.util.jsonpullparser.util.TokenConverter;

/**
 * {@link TokenConverter} for {@link Args}.
 * for [ "NumberLiteralExpression",
 * ["2.718281828459045",false,"./lib/built-in.jsx",696,18]]
 * 
 * @author vvakame
 */
class ArgsConverter extends TokenConverter<Args> {

	static ArgsConverter converter;

	static JsxTokenConverter tokenConverter;


	/**
	 * get instance
	 * @return singleton instance
	 * @author vvakame
	 */
	public static ArgsConverter getInstance() {
		if (converter != null) {
			return converter;
		}
		tokenConverter = JsxTokenConverter.getInstance();
		converter = new ArgsConverter();
		return converter;
	}

	@Override
	public Args parse(JsonPullParser parser, OnJsonObjectAddListener listener) throws IOException,
			JsonFormatException {

		if (parser == null) {
			throw new IllegalArgumentException();
		}

		State state = parser.getEventType();

		switch (state) {
			case VALUE_NULL:
				return null;
			case START_ARRAY:
				Args args = new Args();

				if (parser.lookAhead() == State.VALUE_STRING) {
					{
						if (parser.getEventType() != State.VALUE_STRING) {
							throw new JsonFormatException("value is not String");
						}
						args.setType(parser.getValueString());
					}
					Token token = tokenConverter.parse(parser, listener);
					args.setToken(token);
				} else if (parser.lookAhead() == State.START_HASH) {
					Token token = TokenGen.get(parser, listener);
					args.setToken(token);
					{
						State eventType = parser.getEventType();
						if (eventType == State.VALUE_STRING) {
							args.setType(parser.getValueString());
						} else if (eventType == State.VALUE_NULL) {
						} else {
							throw new JsonFormatException("value is not String");
						}
					}
				} else {
					Token token = tokenConverter.parse(parser, listener);
					args.setToken(token);
					{
						if (parser.getEventType() != State.VALUE_STRING) {
							throw new JsonFormatException("value is not String");
						}
						args.setType(parser.getValueString());
					}
				}
				{
					State eventType = parser.getEventType();
					if (eventType != State.END_ARRAY) {
						throw new JsonFormatException("value is not end array");
					}
				}

				return args;
			default:
				throw new JsonFormatException("unknown state " + state);
		}
	}
}
