package edu.grinnell.sandb;

import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.img.URLImageGetterAsync;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;
import edu.grinnell.sandb.xmlpull.FeedContent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;


public class ArticleDetailFragment extends Fragment {

	public static final String ARTICLE_ID_KEY = "article_id";
	private static final String ADF = "ArticleDetailFragment";
	private Article mArticle;

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		
		Bundle b = getArguments();
		
		if(b != null && FeedContent.articles != null)
			mArticle = FeedContent.articles.get(b.getInt(ARTICLE_ID_KEY, 0));
		else
			mArticle = new Article("Title","","","","","","","Body"); // maybe navigateUp instead
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        
        ((TextView) rootView.findViewById(R.id.article_title)).setText(mArticle.title);
        TextView body = (TextView) rootView.findViewById(R.id.article_body);
        body.setText(Html.fromHtml(mArticle.body, new URLImageGetterAsync(body, getActivity()), null));
        
        //Log.d(ADF, "getActivity(): " + getActivity().toString());
        
        Log.d(ADF, mArticle.title);
        //Log.d(ADF, mArticle.body);
        
        return rootView;
    }
	
}