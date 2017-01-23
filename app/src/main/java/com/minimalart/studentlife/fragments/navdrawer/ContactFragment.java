package com.minimalart.studentlife.fragments.navdrawer;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.ContactAdapter;
import com.minimalart.studentlife.services.DataService;

public class ContactFragment extends Fragment {

    public ContactFragment() {

    }

    /**
     * @return a reference to this fragment
     */
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_contact, container, false);

        RecyclerView contactRecyclerView = (RecyclerView)view.findViewById(R.id.frag_contact_recyclerview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contactRecyclerView.setLayoutManager(llm);
        ContactAdapter contactAdapter = new ContactAdapter(DataService.getInstance().getContactFragItems(), getContext());
        contactRecyclerView.setAdapter(contactAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        contactRecyclerView.addItemDecoration(new SpaceVerticalItemDecoration(spacingInPixels));
        return view;
    }

}

class SpaceVerticalItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceVerticalItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildLayoutPosition(view);

        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        if (position == 0){
            outRect.top = space;
        } else{
            outRect.top = 0;
        }
    }
}
