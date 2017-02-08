package com.minimalart.studentlife.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.models.FAQ;

import java.util.ArrayList;

/**
 * Created by ytgab on 08.02.2017.
 */

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private ArrayList<FAQ> faqs;

    public FAQAdapter (ArrayList<FAQ> faqs){
        this.faqs = faqs;
    }

    @Override
    public FAQViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View faqCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_frag_faq, parent, false);

        return new FAQViewHolder(faqCard);
    }

    @Override
    public void onBindViewHolder(FAQViewHolder holder, int position) {
        FAQ faq = faqs.get(position);
        holder.updateUI(faq);
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    class FAQViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView body;

        public FAQViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.faq_title);
            body = (TextView) itemView.findViewById(R.id.faq_body);
        }

        public void updateUI(FAQ faq){
            title.setText(faq.getQuestion());
            body.setText(faq.getAnswer());
        }

    }
}
