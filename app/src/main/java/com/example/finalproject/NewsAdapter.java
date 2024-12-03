package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> newsList = new ArrayList<>();

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.bind(news);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setNews(List<News> news) {
        this.newsList = news;
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView dateTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.newsTitle);
            descriptionTextView = itemView.findViewById(R.id.newsDescription);
            dateTextView = itemView.findViewById(R.id.newsDate);
        }

        public void bind(News news) {
            titleTextView.setText(news.getTitle());
            descriptionTextView.setText(news.getDescription());
            dateTextView.setText(news.getPublishedAt());
        }
    }
} 