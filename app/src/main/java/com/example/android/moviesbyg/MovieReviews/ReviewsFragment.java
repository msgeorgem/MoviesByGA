package com.example.android.moviesbyg.MovieReviews;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviesbyg.DividerItemDecoration;
import com.example.android.moviesbyg.MoviesActivity;
import com.example.android.moviesbyg.R;

import java.util.ArrayList;

import static com.example.android.moviesbyg.MoviesActivity.MDB_CURRENT_MOVIE_ID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewsFragment.OnFragmentInteractionListenerR} interface
 * to handle interaction events.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<SingleMovieReview>> {

    public static final String MDB_MOVIE_PATH1 = "https://api.themoviedb.org/3/movie/";
    public static final String LOG_TAG = ReviewsFragment.class.getSimpleName();
    private static final String api_key = "1157007d8e3f7d5e0af6d7e4165e2730";
    public static final String MDB_REVIEWS_PATH2 = "/reviews?api_key=" + api_key;
    private static final int REVIEWS_LOADER_ID = 333;
    public static String QUERY_BASE_URL_R;
    public static RecyclerView reviewsRecyclerView;
    Parcelable state;
    private MovieReviewsAdapter mAdapterR;
    private ArrayList<SingleMovieReview> reviewsList = new ArrayList<>();
    private TextView mEmptyStateTextView2;
    private View view;
    private OnFragmentInteractionListenerR mListener;

    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = MoviesActivity.cm.getActiveNetworkInfo();
        QUERY_BASE_URL_R = MDB_MOVIE_PATH1 + MDB_CURRENT_MOVIE_ID + MDB_REVIEWS_PATH2;

        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(REVIEWS_LOADER_ID, null, ReviewsFragment.this);


        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator1 = view.findViewById(R.id.loading_indicator2);
            loadingIndicator1.setVisibility(View.GONE);
            // Set empty state text to display "No internet connection"
            mEmptyStateTextView2.setText(R.string.no_internet);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reviews, container, false);
        reviewsRecyclerView = view.findViewById(R.id.list_reviews);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapterR = new MovieReviewsAdapter(reviewsList);
        reviewsRecyclerView.setAdapter(mAdapterR);
        reviewsRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEmptyStateTextView2 = view.findViewById(R.id.empty_view2);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerR) {
            mListener = (OnFragmentInteractionListenerR) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null; // => avoid leaking,
    }

    @Override
    public Loader<ArrayList<SingleMovieReview>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader ReviewsFragment");
        Uri baseUriR = Uri.parse(QUERY_BASE_URL_R);
        return new MovieReviewsLoader(getActivity(), baseUriR.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<SingleMovieReview>> loader, ArrayList<SingleMovieReview> reviews) {
        // Hide loading indicator because the data has been loaded
        Log.i(LOG_TAG, "onLoadFinished ReviewsFragment");

        View loadingIndicator = view.findViewById(R.id.loading_indicator2);
        loadingIndicator.setVisibility(View.GONE);

        reviewsRecyclerView.setVisibility(View.VISIBLE);
        mAdapterR = new MovieReviewsAdapter(reviewsList);


        // If there is a valid list of {@link Movies}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (reviews != null && !reviews.isEmpty()) {
            mAdapterR = new MovieReviewsAdapter(reviews);
            reviewsRecyclerView.setAdapter(mAdapterR);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<SingleMovieReview>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset");
        mAdapterR = new MovieReviewsAdapter(reviewsList);
    }

    @Override
    public void onPause() {
        super.onPause();

        // save RecyclerView state
        state = reviewsRecyclerView.getLayoutManager().onSaveInstanceState();

    }

    @Override
    public void onResume() {
        super.onResume();

        // restore RecyclerView state
        if (state != null) {
            reviewsRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListenerR {
        void onFragmentInteractionR(Uri uri);
    }

}
