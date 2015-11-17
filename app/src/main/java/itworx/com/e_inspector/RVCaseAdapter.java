package itworx.com.e_inspector;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class RVCaseAdapter extends RecyclerView.Adapter<RVCaseAdapter.CellViewHolder> {
    List<Case> list;
    Context mContext;
    public static final String TITLE = "title";
    public static final String PublishContent = "PublishContent";
    public static final String DOESLIKE = "DOESLIKE";
    public static final String LIKES = "LIKES";
    public static final String IMAGE = "image";
    public static final String Location = "location";
    public static final String DATE = "date";

    public RVCaseAdapter(Context con, ArrayList<Case> persons) {
        this.list = persons;
        this.mContext = con;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(CellViewHolder itemViewHolder, final int i) {
        try {
            itemViewHolder.currentItem = list.get(i);
            Case news = itemViewHolder.currentItem;
            itemViewHolder.itemIndex = i;
            itemViewHolder.title.setText(news.title);
            itemViewHolder.content.setText(news.description);
            itemViewHolder.likeCount.setText("5 Likes");
            try {
                Picasso.with(mContext)
                        .load(itemViewHolder.currentItem.incidentImageURI)
                        .placeholder(R.drawable.picasedefault)
                        .into(itemViewHolder.itemImage);
            } catch (Exception ex) {
            }
            if (true)
                itemViewHolder.imageLike.setImageResource(R.drawable.like_icon);
            else
                itemViewHolder.imageLike.setImageResource(R.drawable.unlike);

        } catch (Exception ex) {
        }
    }

    @Override
    public CellViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_cardview, viewGroup, false);
        CellViewHolder pvh = new CellViewHolder(v);
        return pvh;
    }


    public class CellViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView content;
        TextView likeCount;
        ImageView itemImage;
        ImageButton imageLike;
        Case currentItem;
        int itemIndex;

        CellViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.tv_news_title);
            content = (TextView) itemView.findViewById(R.id.tv_news_sybtitle);
            itemImage = (ImageView) itemView.findViewById(R.id.imageView);
            imageLike = (ImageButton) itemView.findViewById(R.id.iv_like);
            likeCount = (TextView) itemView.findViewById(R.id.tv_likescount);
            itemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CaseDetailsActivity.class);
                    intent.putExtra(TITLE, currentItem.title);
                    intent.putExtra(PublishContent, currentItem.description);
                    intent.putExtra(DOESLIKE, true);
                    intent.putExtra(IMAGE, currentItem.incidentImageURI);
                    intent.putExtra(LIKES, 3);
                    intent.putExtra("lat", currentItem.latitude);
                    intent.putExtra("lon", currentItem.longitude);
                    intent.putExtra("audio", currentItem.incidentAudioURI);
                    mContext.startActivity(intent);
                }
            });


        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}