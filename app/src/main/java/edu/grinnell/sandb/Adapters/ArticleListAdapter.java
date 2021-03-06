package edu.grinnell.sandb.Adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.Image;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.DatabaseUtil;
import edu.grinnell.sandb.Util.UniversalLoaderUtility;

/* List Adapter to populate the article list */
public class ArticleListAdapter extends ArrayAdapter<Article> {
	private MainActivity mActivity;
	private List<Article> mData;
	protected UniversalLoaderUtility mLoader;

	public ArticleListAdapter(MainActivity a, int layoutId, List<Article> data) {
		super(a, layoutId, data);
		mActivity = a;
		mData = data;
		mLoader = new UniversalLoaderUtility();
	}

	private class ViewHolder {
		TextView title;
		TextView description;
		ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater li = mActivity.getLayoutInflater();
			convertView = li.inflate(R.layout.articles_row, parent, false);
			holder = new ViewHolder();
			//find the article title view
			holder.title = (TextView) convertView
					.findViewById(R.id.titleText);
			//find the article description view
			holder.description = (TextView) convertView
					.findViewById(R.id.descriptionText);
			//find the article image view
			holder.image = (ImageView) convertView
					.findViewById(R.id.articleThumb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}


		holder.image.setVisibility(View.VISIBLE);
		final Article a = mData.get(position);
				
		if (a != null) {
			holder.image.setVisibility(View.VISIBLE);
            Image articleImage = DatabaseUtil.getArticleImage(a);

            if (articleImage != null) {
                mLoader.loadImage(articleImage.getURL(), holder.image, mActivity);
            }
            else {
                holder.image.setImageResource(R.drawable.sb);
            }

			holder.title.setText(Html.fromHtml(a.getTitle()));
			holder.title.setPadding(3, 3, 3, 3);
			holder.description.setText(Html.fromHtml(a.getDescription()));
		}
		
		return convertView;
	}
}
