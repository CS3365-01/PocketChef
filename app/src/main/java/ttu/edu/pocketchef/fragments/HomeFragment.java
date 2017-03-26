package ttu.edu.pocketchef.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.zip.Inflater;

import ttu.edu.pocketchef.R;
import ttu.edu.pocketchef.content.DB;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private LayoutInflater inflater;
    private View view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshHome() {
        LinearLayout llmain = (LinearLayout)view.findViewById(R.id.home_card_area);
        llmain.removeAllViewsInLayout();

        String selectQuery = "SELECT * FROM Recipe";
        Cursor c = DB.getDB().rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("Name"));
            final int id = c.getInt(c.getColumnIndex("ID"));

            ViewGroup card = (ViewGroup)inflater.inflate(R.layout.card_home, null);
            ViewGroup ll = (ViewGroup)card.getChildAt(0);
            TextView title = (TextView)ll.getChildAt(0);
            final FloatingActionButton fab = (FloatingActionButton)ll.getChildAt(1);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(fab, id);
                }
            });

            //TextView title = (TextView)card.findViewById(R.id.home_card_title);
            title.setText(name);

            llmain.addView(card);
        }
        c.close();
    }

    public void showPopup(FloatingActionButton menuItemView, final int id){
        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.home_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_menu_delete:
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.ic_delete)
                                .setTitle("Deleting Recipe")
                                .setMessage("Are you sure you want to delete this recipe?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DB.getDB().execSQL("DELETE FROM Recipe WHERE ID = " + id);
                                        refreshHome();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();

                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        this.inflater = inflater;
        view = v;

        refreshHome();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        void onHomeFragmentInteraction();
    }
}
