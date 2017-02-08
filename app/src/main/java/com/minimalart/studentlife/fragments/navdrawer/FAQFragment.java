package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FAQAdapter;
import com.minimalart.studentlife.models.FAQ;

import java.util.ArrayList;

public class FAQFragment extends Fragment {

    public FAQFragment() {
        // Required empty public constructor
    }

    public static FAQFragment newInstance() {
        FAQFragment fragment = new FAQFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        RecyclerView faqRecyclerView = (RecyclerView) view.findViewById(R.id.faq_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        faqRecyclerView.setLayoutManager(llm);
        faqRecyclerView.addItemDecoration(new DividerItemDecoration(faqRecyclerView.getContext(), llm.getOrientation()));
        ArrayList<FAQ> list = new ArrayList<>();

        String[] questions = getResources().getStringArray(R.array.faq_questions);
        String[] answers = getResources().getStringArray(R.array.faq_answers);

        for(int i=0; i<questions.length; i++){
            list.add(new FAQ(questions[i], answers[i]));
        }

        FAQAdapter adapter = new FAQAdapter(list);
        faqRecyclerView.setAdapter(adapter);



        return view;
    }

}
