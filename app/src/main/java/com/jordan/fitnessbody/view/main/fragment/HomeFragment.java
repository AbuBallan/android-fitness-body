package com.jordan.fitnessbody.view.main.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.view.article.ArticleActivity;
import com.jordan.fitnessbody.view.main.MainActivity;

public class HomeFragment extends Fragment {

    private Toolbar mToolbar;
    private CardView mFitnessArticlesCardView;
    private CardView mHealthyFoodArticlesCardView;
    private CardView mGymLocatorArticlesCardView;
    private CardView mQuestionAnswerArticlesCardView;

    public HomeFragment() {
    }

    public static HomeFragment create() {
        return new HomeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bindViews(view);
        setupToolbar();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private void bindViews(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mFitnessArticlesCardView = view.findViewById(R.id.fitness_articles_card_view);
        mHealthyFoodArticlesCardView = view.findViewById(R.id.healthy_food_articles_card_view);
        mGymLocatorArticlesCardView = view.findViewById(R.id.gym_locator_articles_card_view);
        mQuestionAnswerArticlesCardView = view.findViewById(R.id.question_answer_articles_card_view);
    }

    private void setupToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Fitness Body");
    }

    private void setupViews() {
        mFitnessArticlesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArticleActivity.class);
                intent.putExtra(ArticleActivity.EXTRA_CATEGORY_ID, "fitness");
                getContext().startActivity(intent);
            }
        });

        mHealthyFoodArticlesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArticleActivity.class);
                intent.putExtra(ArticleActivity.EXTRA_CATEGORY_ID, "healthy_food");
                getContext().startActivity(intent);
            }
        });

        mGymLocatorArticlesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectNavigationItem(R.id.item_gym_locator);
            }
        });

        mQuestionAnswerArticlesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArticleActivity.class);
                intent.putExtra(ArticleActivity.EXTRA_CATEGORY_ID, "q_a");
                getContext().startActivity(intent);
            }
        });
    }

}