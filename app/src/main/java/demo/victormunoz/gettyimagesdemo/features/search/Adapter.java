package demo.victormunoz.gettyimagesdemo.features.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.gettyimagesdemo.R;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private final static String ALPHA = "alpha";
    private final static String TRANSLATION_Y = "translationY";
    private final static int ANIMATION_DURATION = 1000;
    private final Typeface caviarTypefaceBold;
    private final Context context;
    private final List<GettyImage> imagesList = new ArrayList<>();
    private final Picasso picasso;
    private final AdapterListener adapterListener;

    void addImages(List<GettyImage> users) {
        int startPosition = imagesList.size();
        int endPosition = startPosition + users.size() - 1;
        imagesList.addAll(users);
        notifyItemRangeInserted(startPosition, endPosition);
    }

    void removeAllImages() {
        imagesList.clear();
        notifyDataSetChanged();
    }

    private boolean isLastItem(MyViewHolder holder) {
        return holder.getAdapterPosition() == imagesList.size() - 1;
    }

    private void startItemEnterAnimation(final View view) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(ALPHA, 0f, 1f);
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 100f, 0f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, alpha, translationY);
        anim.setStartDelay(1);//we need to set this delay to avoid an undesirable blink effect
        anim.setDuration(ANIMATION_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    GettyImage getItem(int position) {
        return imagesList.get(position);
    }

    public Adapter(Picasso picasso, Context context, AdapterListener listener) {
        this.picasso = picasso;
        this.adapterListener = listener;
        this.context = context;
        caviarTypefaceBold = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.caviar_bold_font));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item, parent, false);
        return new MyViewHolder(itemView, adapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.itemView.setVisibility(View.GONE);
        GettyImage gettyImage = imagesList.get(position);
        String imageURL = context.getString(R.string.thumb_url, gettyImage.getId());
        holder.id.setTypeface(caviarTypefaceBold);
        holder.tittle.setTypeface(caviarTypefaceBold);
        holder.id.setText(gettyImage.getId());
        holder.tittle.setText(gettyImage.getTitle());
        picasso.load(imageURL).into(holder.picture, new Callback() {
            @Override
            public void onSuccess() {
                startItemEnterAnimation(holder.itemView);
            }

            @Override
            public void onError() {
            }
        });

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isLastItem(holder)) {
            adapterListener.onEndOfTheList();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AdapterListener usersListener;
        @BindView(R.id.image_id)
        TextView id;
        @BindView(R.id.title)
        TextView tittle;
        @BindView(R.id.picture)
        ImageView picture;

        MyViewHolder(View view, AdapterListener listener) {
            super(view);
            usersListener = listener;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Drawable drawable = picture.getDrawable();
            usersListener.onImageClick(position, drawable);
        }
    }

    public interface AdapterListener {

        void onImageClick(int position, Drawable drawable);

        void onEndOfTheList();
    }

}

