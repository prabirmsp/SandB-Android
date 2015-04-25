package edu.grinnell.sandb.xmlpull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;
import edu.grinnell.sandb.comments.Comment;
import edu.grinnell.sandb.comments.CommentTable;

public class CommentParseTask {

	private static final String ns = null;

	public static final String XMP = "CommentParseTask";

	public static List<Comment> parseCommentsFromStream(
			InputStream xmlstream, Context c, CommentTable t)
			throws XmlPullParserException, IOException {

		Reader in = new InputStreamReader(xmlstream);

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in);
			parser.nextTag();
			return readFeed(parser, c, t);
		} finally {
			in.close();
		}
	}

	private static List<Comment> readFeed(XmlPullParser parser, Context c,
			CommentTable t) throws XmlPullParserException, IOException {
		List<Comment> comments = new ArrayList<Comment>();

		parser.require(XmlPullParser.START_TAG, ns, "rss");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("channel")) {
				continue;
			} else if (name.equals("item")) {
				Comment comment = readComment(parser, t);
				comments.add(comment);
			} else {
				skip(parser);
			}
		}
		return comments;
	}

	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them
	// off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private static Comment readComment(XmlPullParser parser, CommentTable table)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "item");
		String url = null;
		String date = null;
		String body = null;
		String author = null;
		String article_url = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("dc:creator")) {
				author = readAuthor(parser);
			} else if (name.equals("pubDate")) {
				date = readDate(parser);
			} else if (name.equals("content:encoded")) {
				body = readBody(parser);
			} else if (name.equals("link")) {
				url = readLink(parser);
			} else {
				skip(parser);
			}
		}
		
		article_url = url.substring(0, url.indexOf("comment-page-"));		
		
		return new Comment(url, date, body, author, article_url);
		//return table.createComment(url, date, body, author, article_url);
	}

	/*
	 * Listener you should implement for the callback method in the UI thread
	 * and pass to the constructor of GetMenuTask
	 */
	public interface ParseDataListener {
		public void onDataParsed(List<Comment> comments);
	}

	// Processes author tags in the feed.
	private static String readAuthor(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "dc:creator");
		String author = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "dc:creator");
		return author;
	}

	// Processes link tags in the feed.
	private static String readLink(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	// Processes date tags in the feed.
	private static String readDate(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "pubDate");
		String date = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "pubDate");
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int dateCut = date.lastIndexOf(Integer.toString(year)) + 4;
		date = date.substring(0, dateCut);

		return date;
	}

	// Processes description tags in the feed.
	private static String readBody(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "content:encoded");
		String body = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "content:encoded");
		return body;
	}

	// For the tags title and summary, extracts their text values.
	private static String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// Skips tags the parser isn't interested in. Uses depth to handle nested
	// tags. i.e.,
	// if the next tag after a START_TAG isn't a matching END_TAG, it keeps
	// going until it
	// finds the matching END_TAG (as indicated by the value of "depth" being
	// 0).
	private static void skip(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
