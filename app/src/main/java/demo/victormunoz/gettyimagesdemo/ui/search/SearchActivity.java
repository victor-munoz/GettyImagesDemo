package demo.victormunoz.gettyimagesdemo.ui.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.gettyimagesdemo.BuildConfig;
import demo.victormunoz.gettyimagesdemo.R;
import demo.victormunoz.gettyimagesdemo.injection.component.DaggerSearchComponent;
import demo.victormunoz.gettyimagesdemo.injection.module.AdapterModule;
import demo.victormunoz.gettyimagesdemo.injection.module.ContextModule;
import demo.victormunoz.gettyimagesdemo.injection.module.PresenterModule;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;
import demo.victormunoz.gettyimagesdemo.utils.espresso.EspressoIdlingResource;
import demo.victormunoz.gettyimagesdemo.utils.recyclerview.SameMargin;
import rx.Observer;

public class SearchActivity extends AppCompatActivity
        implements Contract.Views, Adapter.AdapterListener {
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.caption)
    TextView bottomSheetCaption;
    @BindView(R.id.tittle)
    TextView bottomSheetTittle;
    @BindView(R.id.image)
    ImageView bottomSheetImage;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.bottom_sheet)
    NestedScrollView bottomSheet;
    @Inject
    Adapter adapter;
    @Inject
    Contract.UserActionsListener userActionsListener;
    private BottomSheetBehavior bottomSheetBehavior;
    private Typeface myTypeface;

    static {
        //enable vector drawables on pre-lollipop
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void setRecyclerView() {
        int numColumns = getResources().getInteger(R.integer.columns);
        int itemMargin = getResources().getDimensionPixelSize(R.dimen.margin_between_content);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numColumns, OrientationHelper.VERTICAL));
        recyclerView.addItemDecoration(new SameMargin(itemMargin, numColumns));
        recyclerView.setPadding(itemMargin, 0, 0, 0);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                hideSoftKeyboard();
            }
        });
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    private void setSearchEditText() {
        myTypeface = Typeface.createFromAsset(getAssets(), getString(R.string.caviar_font));
        search.setTypeface(myTypeface);
        Drawable cloudIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_search, null);
        search.setCompoundDrawablesWithIntrinsicBounds(cloudIcon, null, null, null);
        search.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.margin_content));
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    String phrase = v.getText().toString();
                    if (phrase.length() == 0) {
                        removeAllImages();
                    } else {
                        progress.setVisibility(View.VISIBLE);
                        userActionsListener.loadImagesByPhrase(phrase);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setSnackBarLeftIcon(Snackbar snackBar) {
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Drawable cloudIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_cloud, null);
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudIcon, null, null, null);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.margin_content));
    }

    private void setProgressColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);
            progress.getIndeterminateDrawable().setColorFilter(colorAccent, PorterDuff.Mode.SRC_IN);
        }
    }

    private void setDependencyInjection() {
        DaggerSearchComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .adapterModule(new AdapterModule(this))
                .presenterModule(new PresenterModule(this))
                .build().inject(this);
    }

    private void animateSearchEnter() {
        search.setVisibility(View.GONE);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 200f, 0f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(search, alpha, translationY);
        anim.setDuration(1200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showSoftKeyboard();
                EspressoIdlingResource.decrement();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                search.setVisibility(View.VISIBLE);
                EspressoIdlingResource.increment();
            }
        });
        anim.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setDependencyInjection();
        setRecyclerView();
        setActionBar();
        setSearchEditText();
        setProgressColor();
        setBottomSheetBehavior();
        if (BuildConfig.DEBUG) {

            StrictMode.setThreadPolicy(new
                    StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        animateSearchEnter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userActionsListener = null;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            super.onBackPressed();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onImageClick(int position, Drawable drawable) {
        hideSoftKeyboard();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            String caption = adapter.getItem(position).getCaption();
            bottomSheetCaption.setText(caption);
            bottomSheetCaption.setTypeface(myTypeface);
            bottomSheetTittle.setTypeface(myTypeface);
            bottomSheetImage.setImageDrawable(drawable);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onEndOfTheList() {
        userActionsListener.loadMoreImages();
    }

    @Override
    public void addImages(List<GettyImage> images) {
        progress.setVisibility(View.GONE);
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.addImages(images);
    }

    @Override
    public void removeAllImages() {
        recyclerView.setVisibility(View.GONE);
        adapter.removeAllImages();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //hide shadow line when there is no images displayed
            appBarLayout.setElevation(0f);
        }
    }

    @Override
    public void onLoadImagesFail() {
        progress.setVisibility(View.GONE);
        String message = getString(R.string.error_downloading);
        Snackbar snackbar = Snackbar
                .make(recyclerView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userActionsListener.loadMoreImages();
                    }
                });
        setSnackBarLeftIcon(snackbar);
        snackbar.show();
    }

    @Override
    public void noImagesFound() {
        progress.setVisibility(View.GONE);
        if (adapter.getItemCount() == 0) {
            String message = getString(R.string.no_images_were_found);
            Snackbar snackbar = Snackbar
                    .make(recyclerView, message, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            setSnackBarLeftIcon(snackbar);
            snackbar.show();
        }

    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
